package cz.cuni.mff.d3s.distrace.utils;

import nanomsg.pair.PairSocket;

import java.nio.charset.StandardCharsets;


public class SocketWrapper {
    private PairSocket socket;
    public SocketWrapper(String socketAddress){
        socket = new PairSocket();
        socket.bind(socketAddress);
    }

    public SocketWrapper send(long num){
        socket.send(num+"");
        return this;
    }

    public SocketWrapper send(byte[] arr){
        socket.send(arr);
        return this;
    }

    public SocketWrapper send(String str){
        socket.send(str);
        return this;
    }

    public String receiveString(){
        return new String(socket.recvBytes(), StandardCharsets.UTF_8);
    }

    public byte[] receiveBytes(){
        return socket.recvBytes();
    }

    public void close(){
        socket.close();
    }
}
