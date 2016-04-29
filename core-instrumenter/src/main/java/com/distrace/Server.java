package com.distrace;


import nanomsg.Nanomsg;
import nanomsg.async.AsyncSocket;
import nanomsg.reqrep.RepSocket;


public class Server {
 public void start(){
   RepSocket sock = new RepSocket();
     //final AsyncSocket asyncSock = new AsyncSocket(sock);
     sock.bind("ipc://test");

     byte[] receivedData = sock.recvBytes();
     sock.send(receivedData);

     System.out.println(receivedData);

     sock.close();
 }
}
