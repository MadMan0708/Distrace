package com.distrace.transformers;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

/**
 * Created by kuba on 09/03/16.
 */
public class CustomTransformer implements ClassFileTransformer {

    public byte[] transform(ClassLoader loader, String className, Class classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        byte[] byteCode = classfileBuffer;
        System.out.println(className);
        if (className.equals("com.distrace.examples.InfiniteLoop")) {
            System.out.println("Inside !!!!!!!!!!!!!!!!");
            try {
                ClassPool cp = ClassPool.getDefault();
                CtClass cc = cp.get("com.distrace.examples.InfiniteLoop");
                CtMethod m = cc.getDeclaredMethod("randomSleep");
                m.addLocalVariable("elapsedTime", CtClass.longType);
                m.insertBefore("elapsedTime = System.currentTimeMillis();");
                m.insertAfter("{elapsedTime = System.currentTimeMillis() - elapsedTime;"
                        + "System.out.println(\"Method Executed in ms: \" + elapsedTime);}");
                byteCode = cc.toBytecode();
                cc.detach();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return byteCode;
    }
}
