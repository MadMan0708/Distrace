package cz.cuni.mff.d3s.distrace;

import cz.cuni.mff.d3s.distrace.utils.ByteClassLoader;
import nanomsg.exceptions.IOException;
import nanomsg.pair.PairSocket;
import nanomsg.reqrep.RepSocket;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.IllegalClassFormatException;
import java.nio.charset.StandardCharsets;


public class Server {
    public static final byte REQ_TYPE_INSTRUMENT=0;
    public static final byte REQ_TYPE_STOP=1;
    ByteClassLoader cl = new ByteClassLoader();
    PairSocket sock;
    String sock_addr;

    public Server(String sock_addr){
        this.sock_addr = sock_addr;
    }
    public void handleInstrument(){
        try {
            byte[] name = sock.recvBytes();
            String nameString = new String(name, StandardCharsets.UTF_8);
            if( TransformersManager.transformers.containsKey(nameString.replace("/","."))){
                System.out.println("Handling instrumenting " + nameString.replace("/",".") + " OK");
                sock.send("ack_req_int_yes");

                byte[] byteCode = sock.recvBytes(); // receive the bytecode to instrument
                byte[] transformedByteCode = instrument(nameString, byteCode);
                sock.send(transformedByteCode.length+""); // send length of instrumented code
                sock.send(transformedByteCode); // send instrumented bytecode
            }else{
                sock.send("ack_req_int_no");
            }

        }catch (IllegalClassFormatException e){
            System.out.println("INVALID");
            sock.send("ERROR_INVALID_FORMAT");
        }
    }

    public void handleRequest(byte requestType){
        switch (requestType){
            case REQ_TYPE_INSTRUMENT:
                handleInstrument();
                break;
        }
    }

    public void start(){
         sock = new PairSocket();
         sock.bind(sock_addr);
     //noinspection InfiniteLoopStatement
     while(true){

         try {
             byte[] requestType = sock.recvBytes();
             assert requestType.length==1;
             byte req = requestType[0];
             sock.send("ack_req_msg"); // confirm receiving of the message
             if(req == REQ_TYPE_STOP){
                 // exit the loop which results with stopping the Instrumentor JAR
                 break;
             }
             handleRequest(req);
         }catch (IOException e){
             System.out.println("exception" + e);
             // nothing to receive, wait
             try {
                 Thread.sleep(1000);
             }catch (InterruptedException ignore){
                 // wake up and continue
             }
         }

     }

     sock.close();
 }


    public byte[] instrument(String className, final byte[] bytecode) throws IllegalClassFormatException{
        String nameAsInJava = className.replace("/",".");
        return new AgentBuilder.Default()
                .with(new AgentBuilder.Listener() {
                    @Override
                    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, DynamicType dynamicType) {
                        System.out.println("Transformed:" + typeDescription + " " + dynamicType);
                    }

                    @Override
                    public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader) {
                        System.out.println("Ignored" + typeDescription);
                    }

                    @Override
                    public void onError(String typeName, ClassLoader classLoader, Throwable throwable) {
                        System.out.println("Error:" + typeName + " " + throwable);
                    }

                    @Override
                    public void onComplete(String typeName, ClassLoader classLoader) {
                        System.out.println("Complete:" + typeName + " " + classLoader);
                    }
                })
                .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .type(ElementMatchers.named(nameAsInJava))
                .transform(TransformersManager.transformers.get(nameAsInJava)).makeRaw().transform(cl, className, null, null, bytecode);
    }

}
