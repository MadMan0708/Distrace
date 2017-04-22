package cz.cuni.mff.d3s.distrace.instrumentation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Classloader used in the Instrumentor.
 * <p>
 * This classloader caches bytecode for all classes received from the native
 * agent. Byte buddy is using this class loader to create TypeDescriptions for each class
 * and also to get bytecode for the currently instrumented class.
 */
public class InstrumentorClassLoader extends ClassLoader {
    private static final Logger log = LogManager.getLogger(InstrumentorClassLoader.class);

    private HashMap<String, byte[]> cache = new HashMap<>();

    /**
     * Register the received byte code under the specified class name
     *
     * @param className class name
     * @param bytes     byte array with the byte code
     */
    public void registerByteCode(String className, byte[] bytes) {
        if (!cache.containsKey(className)) {
            cache.put(className, bytes);
        }
    }

    /**
     * Check whether the cache contains byte code for the given class
     *
     * @param className class name to check
     */
    public boolean contains(String className) {
        return cache.containsKey(className);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        InputStream resourceAsStream = super.getResourceAsStream(name);
        if (resourceAsStream != null) {
            log.info("Loading byte code for instrumentation of class using parent classloader: " + name);
            return resourceAsStream;
        } else {
            String nameDots = name.replaceAll("/", ".");
            String nameDotsNoClassExtension = nameDots.substring(0, name.lastIndexOf("."));
            log.info("Loading byte code for instrumentation of class from local byte cache: " + nameDotsNoClassExtension);
            byte[] bytes = cache.get(nameDotsNoClassExtension);
            return new ByteArrayInputStream(bytes);
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            log.info("Finding class using parent classloader: " + name);
            return super.findClass(name);
        } catch (ClassNotFoundException e) {
            log.info("Finding class from local byte cache: " + name);
            byte[] bytes = cache.get(name);
            if (bytes == null) {
                throw new RuntimeException("Bytes are not in the instrumentor cache for the class: " + name);
            }
            return defineClass(name, bytes, 0, bytes.length);
        }
    }
}
