package cz.cuni.mff.d3s.distrace.utils;



import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Byte code classloader exposed to native agent
 */
public class ByteCodeClassLoader extends ClassLoader {

    private HashMap<String, byte[]> cache = new HashMap<>();


    public void registerBytes(String name, byte[] bytes){
        if(!cache.containsKey(name)) {
            cache.put(name, bytes);
        }
    }

    public Class<?> getClass(String name) throws ClassNotFoundException {
        return findClass(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        System.out.println("FINDING RESOURCE " + name);

        byte[] bytes = cache.get(name.replaceAll("/", ".").substring(0,name.lastIndexOf(".")));
        return new ByteArrayInputStream(bytes);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        byte[] bytes = cache.get(name);
        System.out.println("FINDING CLASS " + name + " b "+ bytes);
        return defineClass(name, bytes, 0, bytes.length);
    }
}
