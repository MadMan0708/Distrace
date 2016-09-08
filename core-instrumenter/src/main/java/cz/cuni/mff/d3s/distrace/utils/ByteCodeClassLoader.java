package cz.cuni.mff.d3s.distrace.utils;

import net.bytebuddy.description.type.TypeDescription;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Byte code classloader exposed to native agent
 */
public class ByteCodeClassLoader extends ClassLoader{

    public static byte[] typeDescrFor(byte[] code, String className){
        try {
            ByteCodeClassLoader cl = new ByteCodeClassLoader(code);
            Class<?> clazz = cl.findClass(className.replaceAll("/","."));
            TypeDescription typeDescr = new TypeDescription.ForLoadedType(clazz);
            System.out.println("TypeDesc as string " + typeDescr.getName());
            //cl = null;
            // Only one class was loaded using this classloader. The class gets "unloaded"
            // from JVM if the classloader which loaded it gets garbage collected and we ensure this
            // by this call

            // http://geekrai.blogspot.cz/2013/05/class-loading-and-unloading-in-jvm.html
            // If the application has no references to a given type, then the type can be unloaded or garbage collected (like heap memory).
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(typeDescr);
            //return bos.toByteArray(); // return it as byte array
            return null;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ByteCodeClassLoader(byte[] bytes) {
        this.bytes = bytes;
    }

    private byte[] bytes;



    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        System.out.println("LOADING FOR NAME: " + name);
        return defineClass(name, bytes, 0, bytes.length);
        //return super.findClass(name);
    }
}
