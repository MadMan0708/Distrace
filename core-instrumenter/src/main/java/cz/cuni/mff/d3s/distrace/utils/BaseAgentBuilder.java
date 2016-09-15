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

import java.util.Map;

/**
 * Base agent builder exposing relevant method on ByteBuddy's agent builder
 */
public class BaseAgentBuilder {
    private static final Logger log = LogManager.getLogger(BaseAgentBuilder.class);


    private ByteCodeClassLoader byteCodeClassLoader;
    private PairSocket sock;

    public BaseAgentBuilder(PairSocket sock, ByteCodeClassLoader cl) {
        this.byteCodeClassLoader = cl;
        this.sock = sock;
        agentBuilder = initBuilder();
    }

    private AgentBuilder initBuilder(){
        return new AgentBuilder.Default()
                .with(new AgentBuilder.Listener() {

                    @Override
                    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule
                            module, DynamicType dynamicType) {
                        log.info("Before: Deciding whether to instrument class:  " + typeDescription);
                        sock.send("ack_req_int_yes");
                    }

                    @Override
                    public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
                        log.info("Ignored: " + typeDescription);
                        sock.send("ack_req_int_no");
                    }

                    @Override
                    public void onError(String typeName, ClassLoader classLoader, JavaModule module, Throwable throwable) {
                        throwable.printStackTrace();
                        log.error("Error: " + typeName + " " );
                    }

                    @Override
                    public void onComplete(String typeName, ClassLoader classLoader, JavaModule module) {
                        log.info("Complete: " + typeName);
                    }
                })
                .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(new AgentBuilder.PoolStrategy() {
                    @Override
                    public TypePool typePool(ClassFileLocator classFileLocator, final ClassLoader classLoader) {
                        return new TypePool() {
                            @Override
                            public Resolution describe(String name) {
                                log.info("Describing :::::: " + name);

                                try {
                                    Class<?> clazz = byteCodeClassLoader.loadClass(name);
                                    log.info("DESCRIBED CLAZZ " + clazz.getName());
                                    return new Resolution.Simple( new TypeDescription.ForLoadedType(clazz));
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
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
                .with(new AgentBuilder.LocationStrategy.Simple(ClassFileLocator.ForClassLoader.of(byteCodeClassLoader)));

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
