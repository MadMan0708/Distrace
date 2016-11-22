package cz.cuni.mff.d3s.distrace.examples;


import javassist.ClassPath;
import javassist.ClassPool;
import javassist.NotFoundException;
import net.bytebuddy.asm.Advice;

public class ClassPoolInterceptor{

    @Advice.OnMethodEnter
    public static void enter(@Advice.This Object o) {
        ClassPool pool = (ClassPool) o;
        try {
            ClassPath cp = pool.insertClassPath("/Users/kuba/dir");
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }
}
