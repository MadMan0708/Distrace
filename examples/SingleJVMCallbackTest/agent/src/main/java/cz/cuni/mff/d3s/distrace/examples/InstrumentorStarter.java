package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.Instrumentor;
import cz.cuni.mff.d3s.distrace.utils.BaseAgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 *
 */
public class InstrumentorStarter extends Instrumentor {

    @Override
    public AgentBuilder createAgentBuilder(BaseAgentBuilder builder) {
        return builder
                .type(named("cz.cuni.mff.d3s.distrace.examples.CallbackCreator"))
                .transform(new CallBackCreatorTransformer())
                .type(nameStartsWith("cz.cuni.mff.d3s.distrace.examples.Callback").and(not(isInterface())))
                .transform(new CallbackTransformer());

    }

}
