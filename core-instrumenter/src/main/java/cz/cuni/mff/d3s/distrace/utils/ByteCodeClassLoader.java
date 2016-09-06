package cz.cuni.mff.d3s.distrace.utils;

import cz.cuni.mff.d3s.distrace.InstrumentorServer;
import nanomsg.pair.PairSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by kuba on 06/09/16.
 */
public class ByteCodeClassLoader extends ClassLoader{
    private static final Logger log = LogManager.getLogger(ByteCodeClassLoader.class);
    private PairSocket sock;

    public ByteCodeClassLoader(PairSocket sock) {
        this.sock = sock;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        log.info("LOADING FOR NAME STREAM: " + name);
        return new ByteArrayInputStream(sock.recvBytes());
        //return super.getResourceAsStream(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] byteCode = sock.recvBytes(); // receive the bytecode to instrument
        log.info("LOADING FOR NAME: " + name);
        return defineClass(name, byteCode, 0, byteCode.length);
        //return super.findClass(name);
    }
}
