package cz.cuni.mff.d3s.distrace.utils;

import java.util.HashMap;

/**
 * Byte code classloader exposed to native agent. This loader loads the class from bytecode
 * and then calls Class.forName(..) on it which ensures that all dependant references are loaded too.
 */
public class ClassCreator extends ClassLoader {

    private static HashMap<String, byte[]> cache = new HashMap<>();

    public static boolean contains(String className){
        return cache.containsKey(className);
    }
    /**
     * This method finds the class. It is on purpose that this method does not use parent classloder,
     * otherwise we would cause infinite recursive call during onClassFileLoadHook
     */
    public static boolean loadClassWithAllReferences(byte[] classBytes, String className) {
        if (!cache.containsKey(className)) {
            ClassCreator loader = new ClassCreator();
            cache.put(className, classBytes);

            try {
                Class<?> clazz = loader.findClass(className);
                Class.forName(clazz.getName(), true, loader);

            } catch (ClassNotFoundException e) {
                assert false; // this can't happen since we have the class always in the bytecode
            }
            return true; // should continue with rest of onClassFileLoadHook
        }else {
              return false; // should not continue with rest of onClassFileLoadHook
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return defineClass(name, cache.get(name), 0, cache.get(name).length);
    }
}
