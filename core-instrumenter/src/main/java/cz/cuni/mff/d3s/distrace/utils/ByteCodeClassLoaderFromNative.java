package cz.cuni.mff.d3s.distrace.utils;

/**
 * Created by kuba on 06/09/16.
 */
public class ByteCodeClassLoaderFromNative extends ClassLoader{
    public static void typeDescrFor(byte[] code, String className){
        try {
            ByteCodeClassLoaderFromNative cl = new ByteCodeClassLoaderFromNative(code);
            System.out.println("CREATING FAKE CLASS: " + className);

            Class<?> clazz = cl.findClass(className.replaceAll("/","."));

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private ByteCodeClassLoaderFromNative(byte[] bytes) {
        this.bytes = bytes;

    }

    private byte[] bytes;


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> cl =  defineClass(name, bytes, 0, bytes.length);
        System.out.println("LOADING FOR NAME: " + name +" cl: "+cl);

        return cl;
        //return super.findClass(name);
    }
}
