package com.distrace;

import com.distrace.transformers.CustomImpl;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

class Instrumentor {

    public static void main(String[] args){
        System.out.println("Runnin forked JVM");
        try {
            Thread.sleep(1000);
        }catch (Exception ignore){
        }
            //registerTransformers();
    }


    public static void registerTransformers(Instrumentation inst){
        new AgentBuilder.Default()
                .with(AgentBuilder.RedefinitionStrategy.REDEFINITION)
                .type(ElementMatchers.named("java.lang.Thread"))
                .transform(new AgentBuilder.Transformer() {
                    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader) {
                        return builder.method(ElementMatchers.nameEndsWith("run"))
                                .intercept(MethodDelegation.to(new CustomImpl()).andThen(SuperMethodCall.INSTANCE));
                    }
                }).installOnByteBuddyAgent();

    }

}