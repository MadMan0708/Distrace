package cz.cuni.mff.d3s.distrace.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Classloader used in the Instrumenter. This classloader caches all bytecode received from the native
 * agent. Byte buddy is using this class loader to create TypeDescriptions for each class and also to get
 * bytecode for class really being instrumented.
 */
public class InstrumentorClassLoader extends ClassLoader{
    private static final Logger log = LogManager.getLogger(InstrumentorClassLoader.class);

    private HashMap<String, byte[]> cache = new HashMap<>();

    public void registerByteCode(String className, byte[] bytes){
        cache.put(className, bytes);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        // We get as name fully classified name class file name, we need to turn it into
        // java class fully qualified name
        String nameDots = name.replaceAll("/", ".");
        String nameDotsNoClassExtension = nameDots.substring(0, name.lastIndexOf("."));
        log.info("Loading byte code for instrumentation of class: " + nameDotsNoClassExtension);
        byte[] bytes = cache.get(nameDotsNoClassExtension);
        return new ByteArrayInputStream(bytes);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        log.info("Finding class: "+ name);
        byte[] bytes = cache.get(name);
        assert bytes != null;
        return defineClass(name, bytes, 0, bytes.length);
    }
}
