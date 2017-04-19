package javassist;

import com.google.auto.service.AutoService;
import cz.cuni.mff.d3s.distrace.instrumentation.Interceptor;
import cz.cuni.mff.d3s.distrace.tracing.TraceContext;
import net.bytebuddy.implementation.bind.annotation.This;


/**
 * Interceptor used to instrument javassist classpath so instrumented classes have
 * higher priority
 */
@AutoService(Interceptor.class)
public class ClassPoolInterceptor implements Interceptor {

    private boolean alreadyInserted = false;
    private String pathToInstrumentedClasses;

    public ClassPoolInterceptor(){
        final String classOutputDir = TraceContext.getClassOutputDir();
        if(TraceContext.getClassOutputDir().endsWith("/")){
            // trim the ending slash
            pathToInstrumentedClasses = classOutputDir.substring(0, classOutputDir.length() - 1);
        }else{
            pathToInstrumentedClasses = classOutputDir;
        }
    }

    public void get(@This Object o){
        if(!alreadyInserted) {
            ClassPool classPool = (ClassPool) o;
            try {
                classPool.insertClassPath(pathToInstrumentedClasses);
                alreadyInserted = true;
            } catch (NotFoundException ignore) {}
            System.out.println("GET was called, class path: " + classPool.source);
        }
    }
}
