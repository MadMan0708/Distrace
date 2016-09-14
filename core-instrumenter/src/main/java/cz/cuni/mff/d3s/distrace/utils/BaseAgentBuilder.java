package cz.cuni.mff.d3s.distrace.utils;

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

    private HashMap<String, TypeDescription> cache;
    private ByteCodeClassLoader cl;
    public BaseAgentBuilder(HashMap<String, TypeDescription> cache, ByteCodeClassLoader cl){
        this.cache = cache;
        this.cl = cl;
    }
    private AgentBuilder agentBuilder = new AgentBuilder.Default()
            .with(new AgentBuilder.Listener() {

                @Override
                public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule
                        module, DynamicType dynamicType) {
                    System.out.println("Before: Deciding whether to instrument class:  " + typeDescription);
                }

                @Override
                public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
                    System.out.println("Ignored: " + typeDescription);
                }

                @Override
                public void onError(String typeName, ClassLoader classLoader, JavaModule module, Throwable throwable) {
                    throwable.printStackTrace();
                    System.out.println("Error: " + typeName + " " );
                }

                @Override
                public void onComplete(String typeName, ClassLoader classLoader, JavaModule module) {
                    System.out.println("Complete: " + typeName);
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
                            System.out.println("Describing :::::: " + name);
                            return new Resolution.Simple(cache.get(name));
                        }

                        @Override
                        public void clear() {
                            // no need to implement
                        }
                    };
                }
            })
            .with(new AgentBuilder.LocationStrategy.Simple(ClassFileLocator.ForClassLoader.of(cl)));


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
