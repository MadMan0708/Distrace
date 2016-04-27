package com.distrace;

import org.zeromq.ZMQ;

/**
 * Created by kuba on 27/04/16.
 */
public class Server {
 public void start(){
     ZMQ.Context context = ZMQ.context(1);
     // Socket to talk to clients
     ZMQ.Socket socket = context.socket(ZMQ.REP);
     socket.bind ("ipc://*");
     try {
         while (!Thread.currentThread ().isInterrupted ()) {
             byte[] reply = socket.recv(0);
             System.out.println("Received Hello");
             String request = "World" ;
             socket.send(request.getBytes (), 0);
             Thread.sleep(1000); // Do some 'work'
         }
     } catch(Exception e) {
         //StringWriter sw = new StringWriter();
         //PrintWriter pw = new PrintWriter(sw);
         //e.printStackTrace(pw);
         //System.out.println(sw.toString());
     }
     socket.close();
     context.term();

 }
}
