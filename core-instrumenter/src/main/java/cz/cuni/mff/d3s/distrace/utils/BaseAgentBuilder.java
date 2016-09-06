package cz.cuni.mff.d3s.distrace.utils;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.JavaModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base agent builder exposing relevant method on ByteBuddy's agent builder
 */
public class BaseAgentBuilder {
    private static final Logger log = LogManager.getLogger(BaseAgentBuilder.class);

    private AgentBuilder agentBuilder = new AgentBuilder.Default()
            .with(new AgentBuilder.Listener() {

        @Override
        public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule
        module, DynamicType dynamicType) {
            log.info("Transformed: " + typeDescription + " " + dynamicType);
        }

        @Override
        public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
            log.info("Ignored: " + typeDescription);
        }

        @Override
        public void onError(String typeName, ClassLoader classLoader, JavaModule module, Throwable throwable) {
            log.error("Error: " + typeName + " " + throwable + ", classloader: " + classLoader.toString());
        }

        @Override
        public void onComplete(String typeName, ClassLoader classLoader, JavaModule module) {
            log.info("Complete: " + typeName + " " + classLoader);
        }
    });


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
