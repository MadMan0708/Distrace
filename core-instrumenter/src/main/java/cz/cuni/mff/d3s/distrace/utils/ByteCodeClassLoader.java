package cz.cuni.mff.d3s.distrace.utils;

import net.bytebuddy.description.type.TypeDescription;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.HashMap;

/**
 * Byte code classloader exposed to native agent
 */
public class ByteCodeClassLoader extends ClassLoader{
    private static final Logger log = LogManager.getLogger(ByteCodeClassLoader.class);

    private HashMap<String, byte[]> cache = new HashMap<>();

    public void registerByteCode(String className, byte[] bytes){
        cache.put(className, bytes);
    }
    @Override
    public InputStream getResourceAsStream(String name) {
        log.info("LOADING FOR NAME STREAM: " + name);
        return new ByteArrayInputStream(cache.get(name));
        //return super.getResourceAsStream(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        log.info("FINDINF CLASS "+ name );
        byte[] bytes = cache.get(name);
        return defineClass(name, bytes, 0, bytes.length);
    }
}
