package cz.cuni.mff.d3s.distrace;

import cz.cuni.mff.d3s.distrace.utils.BaseAgentBuilder;
import cz.cuni.mff.d3s.distrace.utils.ByteCodeClassLoader;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;

import java.lang.instrument.IllegalClassFormatException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

public abstract class Instrumentor {

    private static HashMap<String, TypeDescription> cache = new HashMap<>();
    private ByteCodeClassLoader cl = new ByteCodeClassLoader();
    private BaseAgentBuilder agent = new BaseAgentBuilder(cache, cl);

    public byte[] instrument(String className, byte[] bytes) {

        String classNameDots = className.replaceAll("/", ".");
        System.out.println("Instrumenting "+ classNameDots);
        cl.registerBytes(classNameDots, bytes);
        Class<?> clazz = null;
        try {
            clazz = cl.getClass(classNameDots);

            System.out.println("aaaaaa "+ clazz.getClassLoader());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        cache.put(classNameDots, new TypeDescription.ForLoadedType(clazz));


        // register this typeDescription
        // we do not have to provide bytecode as parameter to transform method since it is fetched when needed by our class file locator
        // implemented using byte code class loader
        // it returns null in case the class shouldn't have been transformed
        try {
            byte[] transformed = createAgentBuilder(agent).makeRaw().
                    transform(cl, className, null, null, null);
            System.out.println("transformed !!!!" + transformed);
            return transformed;
        } catch (IllegalClassFormatException e) {
            e.printStackTrace();
            System.out.println("NULL");
            return null;
        }


    }

    public abstract AgentBuilder createAgentBuilder(BaseAgentBuilder builder);


}