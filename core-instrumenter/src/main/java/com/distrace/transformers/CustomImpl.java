package com.distrace.transformers;

import net.bytebuddy.implementation.bind.annotation.Origin;

import java.lang.reflect.Method;

/**
 * Created by kuba on 01/04/16.
 */
public class CustomImpl {
    public void interceptaaaaa(){
        System.out.println("was called");
    }
}

/*
m.addLocalVariable("elapsedTime", CtClass.longType);
        m.insertBefore("elapsedTime = System.currentTimeMillis();");
        m.insertAfter("{elapsedTime = System.currentTimeMillis() - elapsedTime;"
        + "System.out.println(\"Method Executed in ms: \" + elapsedTime);}");*/
