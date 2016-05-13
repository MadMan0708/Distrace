package cz.cuni.mff.d3s.distrace;


import cz.cuni.mff.d3s.distrace.transformers.SimpleTransformer;
import cz.cuni.mff.d3s.distrace.utils.ByteClassLoader;
import nanomsg.reqrep.RepSocket;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.IllegalClassFormatException;
import java.nio.charset.StandardCharsets;


public class Server {
    ByteClassLoader cl = new ByteClassLoader();
 public void start(){
   RepSocket sock = new RepSocket();
     sock.bind("ipc://test");
    sock.setRecvTimeout(10000); // for now, need better solution
    sock.setSendTimeout(10000); // for now, need better solution
     TransformersManager.transformers.put("com.distrace.examples.Test",new SimpleTransformer());
     try {
         byte[] name = sock.recvBytes();
         sock.send("OK");
         byte[] byteCode = sock.recvBytes();
         sock.send("OK");
         String nameStr = new String(name, StandardCharsets.UTF_8);
         System.out.println("Length: "+ byteCode.length);
         byte[] transformedByteCode = instrument(nameStr, byteCode);
         sock.recvBytes();
         // send transformedByteCode
         sock.send(transformedByteCode.length+"");
         System.out.println("Length: "+ transformedByteCode.length);
         sock.recvBytes();
         sock.send(transformedByteCode);
     }catch (IllegalClassFormatException e){
         sock.send("ERROR_INVALID_FORMAT");
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
