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

    private Map<String, TypeDescription> typeDescriptions;
    private ByteCodeClassLoader byteCodeClassLoader;
    private PairSocket sock;

    public BaseAgentBuilder(Map<String, TypeDescription> typeDescriptions, PairSocket sock) {
        this.typeDescriptions = typeDescriptions;
        this.byteCodeClassLoader = new ByteCodeClassLoader(sock);
        this.sock = sock;
    }


    private AgentBuilder agentBuilder = new AgentBuilder.Default()
            .with(new AgentBuilder.Listener() {

                @Override
                public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule
                        module, DynamicType dynamicType) {
                    log.info("Handling instrumentation of class:  " + typeDescription);
                    sock.send("ack_req_int_yes");
                }

                @Override
                public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
                    log.info("Ignored: " + typeDescription);
                    sock.send("ack_req_int_no");
                }

                @Override
                public void onError(String typeName, ClassLoader classLoader, JavaModule module, Throwable throwable) {
                    log.error("Error: " + typeName + " " + throwable + ", classloader: " + classLoader.toString());
                }

                @Override
                public void onComplete(String typeName, ClassLoader classLoader, JavaModule module) {
                    log.info("Complete: " + typeName + " " + classLoader);
                }
            })
            .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
            .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
            .with(new AgentBuilder.TypeLocator() {
                @Override
                public TypePool typePool(ClassFileLocator classFileLocator, ClassLoader classLoader) {
                    return new TypePool() {
                        @Override
                        public Resolution describe(String name) {
                            log.info("Describing :::::: " + name);
                            return new Resolution.Simple(typeDescriptions.get(name));
                        }

                        @Override
                        public void clear() {
                            // no need to implement
                        }
                    };
                }
            })
            .with(new AgentBuilder.LocationStrategy.Simple(ClassFileLocator.ForClassLoader.of(byteCodeClassLoader)));


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