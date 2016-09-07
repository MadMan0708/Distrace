package cz.cuni.mff.d3s.distrace.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by kuba on 06/09/16.
 */
public class ByteCodeClassLoader extends ClassLoader{
    private static final Logger log = LogManager.getLogger(ByteCodeClassLoader.class);

    public static Class loadClass(byte[] code, String className){
        try {
            return new ByteCodeClassLoader(code).findClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ByteCodeClassLoader(byte[] bytes) {
        this.bytes = bytes;
    }

    private byte[] bytes;



    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        log.info("LOADING FOR NAME: " + name);
        return defineClass(name, bytes, 0, bytes.length);
        //return super.findClass(name);
    }
}
