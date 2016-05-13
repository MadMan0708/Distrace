package cz.cuni.mff.d3s.distrace.utils;

public class ByteClassLoader extends ClassLoader {

    public Class findClass(String name, byte[] bytecode) {
        return defineClass(name,bytecode,0,bytecode.length);
    }

}