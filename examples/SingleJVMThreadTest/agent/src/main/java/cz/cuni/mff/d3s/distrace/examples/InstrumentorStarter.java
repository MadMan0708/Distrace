package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.Instrumentor;
import cz.cuni.mff.d3s.distrace.examples.transformers.DependantTaskTransformer;
import cz.cuni.mff.d3s.distrace.examples.transformers.StarterTaskTransformer;
import cz.cuni.mff.d3s.distrace.utils.BaseAgentBuilder;
import cz.cuni.mff.d3s.distrace.utils.CustomAgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * Starter of instrumentor
 */
public class InstrumentorStarter extends Instrumentor {

    @Override
    public AgentBuilder createAgentBuilder(BaseAgentBuilder builder) {
        return builder
                .type(named("cz.cuni.mff.d3s.distrace.examples.StarterTask"))
                .transform(new StarterTaskTransformer())
                .type(named("cz.cuni.mff.d3s.distrace.examples.DependantTask"))
                .transform(new DependantTaskTransformer());

    }

}
