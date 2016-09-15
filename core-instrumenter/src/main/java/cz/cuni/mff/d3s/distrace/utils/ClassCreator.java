package cz.cuni.mff.d3s.distrace.utils;

/**
 * Byte code classloader exposed to native agent. This loader loads the class from bytecode
 * and then calls Class.forName(..) on it which ensures that all dependant references are loaded too.
 */
public class ClassCreator extends ClassLoader {

    private byte[] classBytes;
    private String className;
    private ClassCreator(byte[] classBytes, String className){
        this.classBytes = classBytes;
        this.className = className;
    }
    /**
     * This method finds the class. It is on purpose that this method does not use parent classloder,
     * otherwise we would cause infinite recursive call during onClassFileLoadHook
     */
    public static void loadClassWithAllReferences(byte[] classBytes, String className){
        ClassCreator loader = new ClassCreator(classBytes, className);
        try {
            Class<?> clazz = loader.findClass(className);
            Class.forName(clazz.getName(), true, loader);
        } catch (ClassNotFoundException e) {
            assert false; // this can't happen since we have the class always in the bytecode
        }

    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return defineClass(name, classBytes, 0, classBytes.length);
    }
}
