package cz.cuni.mff.d3s.distrace.utils;

import nanomsg.pair.PairSocket;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.JavaModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

/**
 * Base agent builder exposing relevant method on ByteBuddy's agent builder
 */
public class BaseAgentBuilder {
    private static final Logger log = LogManager.getLogger(BaseAgentBuilder.class);


    private InstrumentorClassLoader instrumentorClassLoader;
    private PairSocket sock;

    public BaseAgentBuilder(PairSocket sock, InstrumentorClassLoader cl) {
        this.instrumentorClassLoader = cl;
        this.sock = sock;
        agentBuilder = initBuilder();
    }

    private AgentBuilder initBuilder(){
        return new AgentBuilder.Default()
                .with(new AgentBuilder.Listener() {

                    @Override
                    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule
                            module, DynamicType dynamicType) {
                        log.info("Following type will be instrumented: " + typeDescription);
                        sock.send("ack_req_int_yes");
                    }

                    @Override
                    public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
                        log.info("Following type won't be instrumented: " + typeDescription);
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
