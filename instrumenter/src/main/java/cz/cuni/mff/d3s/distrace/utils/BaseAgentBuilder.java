package cz.cuni.mff.d3s.distrace.utils;

import cz.cuni.mff.d3s.distrace.Utils;
import nanomsg.pair.PairSocket;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.JavaModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Base agent builder exposing relevant method on ByteBuddy's agent builder
 */
public class BaseAgentBuilder {
    private static final Logger log = LogManager.getLogger(BaseAgentBuilder.class);

    private ArrayList<String> sendInterceptors = new ArrayList<>();
    private Map<String, byte[]> interceptorsByteCodes = Utils.getInterceptorByteCodes();
    private InstrumentorClassLoader instrumentorClassLoader;
    private SocketWrapper sock;

    public BaseAgentBuilder(SocketWrapper sock, InstrumentorClassLoader cl) {
        this.instrumentorClassLoader = cl;
        this.sock = sock;
        agentBuilder = initBuilder();
    }
    public AgentBuilder getAgentBuilder(){
        return agentBuilder;
    }

    private void process(LoadedTypeInitializer initializer){
        try {
            if(initializer instanceof LoadedTypeInitializer.ForStaticField){
                Field value = initializer.getClass().getDeclaredField("value");
                value.setAccessible(true);
                Object type = value.get(initializer);

                sock.send("initializers");
                // send name of interceptor
                log.info("Interceptor name " +   type.getClass().getName());
                String name = Utils.toNameWithSlashes(type.getClass().getName());
                sock.send(name);
                if(!sendInterceptors.contains(name)){
                    if(interceptorsByteCodes.get(name)==null){
                        throw new RuntimeException("Byte code for interceptor class "+ name +" not found! Are you sure your interceptor is implementing the Interceptor interface ?");
                    }
                    sock.send(interceptorsByteCodes.get(name).length + "");
                    sock.send(interceptorsByteCodes.get(name));
                    sendInterceptors.add(name);
                }

                // then send loaded type initializer
                log.info("Initializer name " + initializer.getClass().getName());
                sock.send(initializer.getClass().getName());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(initializer);
                byte[] arr = baos.toByteArray();
                sock.send(arr.length + "");
                sock.send(arr);
                //serialize and send initializer over the network
            }else if(initializer instanceof LoadedTypeInitializer.Compound){
                Field value = initializer.getClass().getDeclaredField("loadedTypeInitializer");
                value.setAccessible(true);
                LoadedTypeInitializer[] initializers = (LoadedTypeInitializer[]) value.get(initializer);
                for(LoadedTypeInitializer i: initializers){
                    process(i);
                }
            }

        } catch (NoSuchFieldException | IllegalAccessException | IOException e) {
            e.printStackTrace();
        }
    }
    private AgentBuilder initBuilder(){
        return new AgentBuilder.Default()
                .with(new AgentBuilder.Listener() {
                    @Override
                    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule
                            module, DynamicType dynamicType) {
                        log.info("Following type will be instrumented: " + typeDescription);
                        for(Map.Entry<TypeDescription, byte[]> entry : dynamicType.getAuxiliaryTypes().entrySet()){
                            sock.send("auxiliary_types");
                            log.info("Sending auxiliary class " + entry.getKey());
                            sock.send(Utils.toNameWithSlashes(entry.getKey().getName()));
                            sock.send(entry.getValue().length + "");
                            sock.send(entry.getValue());
                        }
                        sock.send("no_more_aux_classes");

                        // send loaded type initializers
                        for(Map.Entry<TypeDescription, LoadedTypeInitializer> entry : dynamicType.getLoadedTypeInitializers().entrySet()){
                           process(entry.getValue());
                        }
                        sock.send("no_more_initializers");
                        sock.send("ack_req_int_yes");
                    }

                    @Override
                    public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
                        log.info("Following type won't be instrumented: " + typeDescription);
                        sock.send("no_more_aux_classes");
                        sock.send("no_more_initializers");
                        sock.send("ack_req_int_no");
                    }

                    @Override
                    public void onError(String typeName, ClassLoader classLoader, JavaModule module, Throwable throwable) {
                        log.error("Error whilst instrumenting: " + typeName, throwable);
                    }

                    @Override
                    public void onComplete(String typeName, ClassLoader classLoader, JavaModule module) {
                        log.info("Finished processing of: " + typeName);
                    }
                })
                .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(AgentBuilder.PoolStrategy.ClassLoading.EXTENDED)
                .with(new AgentBuilder.PoolStrategy() {
                    @Override
                    public TypePool typePool(ClassFileLocator classFileLocator, final ClassLoader classLoader) {
                        return new TypePool() {
                            private HashMap<String, TypeDescription> cache = new HashMap<>();
                            @Override
                            public Resolution describe(String name) {
                                try {
                                    if(!cache.containsKey(name)) {
                                        Class<?> clazz = instrumentorClassLoader.loadClass(name);
                                        TypeDescription typeDescription = new TypeDescription.ForLoadedType(clazz);
                                        cache.put(name, typeDescription);
                                    }
                                    log.info("Created TypeDescription for class " + name);
                                    return new Resolution.Simple(cache.get(name));
                                } catch (ClassNotFoundException e) {
                                        assert false; //can't happen
                                    return null;
                                }
                            }

                            @Override
                            public void clear() {
                                // no need to implement
                            }
                        };
                    }
                })
                .with(new AgentBuilder.LocationStrategy.Simple(ClassFileLocator.ForClassLoader.of(instrumentorClassLoader)));

    }

    private AgentBuilder agentBuilder;
    public AgentBuilder.Identified.Narrowable type(ElementMatcher<? super TypeDescription> typeMatcher) {
        return agentBuilder.type(typeMatcher);
    }

    public AgentBuilder.Identified.Narrowable type(ElementMatcher<? super TypeDescription> typeMatcher, ElementMatcher<? super ClassLoader> classLoaderMatcher) {
        return agentBuilder.type(typeMatcher, classLoaderMatcher);
    }

    public AgentBuilder.Identified.Narrowable type(ElementMatcher<? super TypeDescription> typeMatcher, ElementMatcher<? super ClassLoader> classLoaderMatcher, ElementMatcher<? super JavaModule> moduleMatcher) {
        return agentBuilder.type(typeMatcher, classLoaderMatcher, moduleMatcher);
    }

    public AgentBuilder.Identified.Narrowable type(AgentBuilder.RawMatcher matcher) {
        return agentBuilder.type(matcher);
    }

    public AgentBuilder.Ignored ignore(ElementMatcher<? super TypeDescription> typeMatcher) {
        return agentBuilder.ignore(typeMatcher);
    }

    public AgentBuilder.Ignored ignore(ElementMatcher<? super TypeDescription> typeMatcher, ElementMatcher<? super ClassLoader> classLoaderMatcher) {
        return agentBuilder.ignore(typeMatcher, classLoaderMatcher);
    }

    public AgentBuilder.Ignored ignore(ElementMatcher<? super TypeDescription> typeMatcher, ElementMatcher<? super ClassLoader> classLoaderMatcher, ElementMatcher<? super JavaModule> moduleMatcher) {
        return agentBuilder.ignore(typeMatcher, classLoaderMatcher, moduleMatcher);
    }

    public AgentBuilder.Ignored ignore(AgentBuilder.RawMatcher rawMatcher) {
        return agentBuilder.ignore(rawMatcher);
    }
}
