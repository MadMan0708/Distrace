package javassist;

import com.google.auto.service.AutoService;
import cz.cuni.mff.d3s.distrace.instrumentation.Interceptor;
import net.bytebuddy.implementation.bind.annotation.This;


/**
 * Interceptor used to instrument javassist classpath to ensure that instrumented classes have
 * higher priority. This is required since H2O is using javassist to generate some classes at runtime.
 * These classes need to bee instrumented and monitored as well and we need to ensure that H2O picks the
 * instrumented variants
 */
@AutoService(Interceptor.class)
public class ClassPoolInterceptor implements Interceptor {

    private boolean alreadyInserted = false;
    private String pathToInstrumentedClasses;

    public ClassPoolInterceptor(String classOutputDir) {
        if (classOutputDir.endsWith("/")) {
            // trim the ending slash
            pathToInstrumentedClasses = classOutputDir.substring(0, classOutputDir.length() - 1);
        } else {
            pathToInstrumentedClasses = classOutputDir;
        }
    }

    public void get(@This Object o) {
        if (!alreadyInserted) {
            ClassPool classPool = (ClassPool) o;
            try {
                classPool.insertClassPath(pathToInstrumentedClasses);
                alreadyInserted = true;
            } catch (NotFoundException ignore) {
            }
            System.out.println("GET was called, class path: " + classPool.source + " ");
        }
    }
}
