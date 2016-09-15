package cz.cuni.mff.d3s.distrace.utils;

import net.bytebuddy.description.type.TypeDescription;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * Byte code classloader exposed to native agent
 */
public class ByteCodeClassLoaderFromNative extends ClassLoader {

    //private static HashMap<String, byte[]> cache = new HashMap<>();
    //private static ByteCodeClassLoader loader = new ByteCodeClassLoader();

    private byte[] code;
    public ByteCodeClassLoaderFromNative(byte[] code){
        this.code = code;
    }
    public static byte[] typeDescrFor(byte[] code, String className) {
        try {

            System.out.println("Putting to cache "+ className);
            //cache.put(className.replaceAll("/","."), code);
            //java.lang.reflect.Field f = loader.getClass().getSuperclass().getDeclaredField("parent");
            //f.setAccessible(true);
            //f.set(loader, null);
            Class<?> clazz = new ByteCodeClassLoaderFromNative(code).findClass(className.replaceAll("/", "."));
            try {
                Class.forName(clazz.getName(), true, clazz.getClassLoader());
            } catch (ClassNotFoundException e) {
                throw new AssertionError(e);  // Can't happen
            }
            //TypeDescription holder = TypeDescriptionStub.from(typeDescr);
            // Only one class was loaded using this classloader. The class gets "unloaded"
            // from JVM if the classloader which loaded it gets garbage collected and we ensure this
            // by this call

            // http://geekrai.blogspot.cz/2013/05/class-loading-and-unloading-in-jvm.html
            // If the application has no references to a given type, then the type can be unloaded or garbage collected (like heap memory).
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            //return null;
            //oos.writeObject(holder);
            //return bos.toByteArray(); // return it as byte array
            return null;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        } //catch (IllegalAccessException | NoSuchFieldException e) {

        //  e.printStackTrace();
        //  return null;
        // }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = code;//cache.get(name);
        System.out.println("Finding class "+name);
        if(bytes==null){
            System.out.println("Class not available " + name);
        }
        return defineClass(name, bytes, 0, bytes.length);
    }
}
