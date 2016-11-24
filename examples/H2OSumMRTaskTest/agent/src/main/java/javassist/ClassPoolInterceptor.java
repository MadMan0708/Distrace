package javassist;

import cz.cuni.mff.d3s.distrace.Interceptor;
import net.bytebuddy.implementation.bind.annotation.This;


public class ClassPoolInterceptor implements Interceptor{

    private boolean alreadyInserted = false;
    private String pathToInstrumentedClasses;

    public ClassPoolInterceptor(String pathToClasses){
        if(pathToClasses.endsWith("/")){
            // trim the ending slash
            pathToInstrumentedClasses = pathToClasses.substring(0, pathToClasses.length()-1);
        }else{
            pathToInstrumentedClasses = pathToClasses;
        }
    }

    public void get(@This Object o){
        if(!alreadyInserted) {
            ClassPool classPool = (ClassPool) o;
            try {
                classPool.insertClassPath(pathToInstrumentedClasses);
                alreadyInserted = true;
            } catch (NotFoundException ignore) {
            }
            System.out.println("GET was called, class path: " + classPool.source);
        }
    }
}
