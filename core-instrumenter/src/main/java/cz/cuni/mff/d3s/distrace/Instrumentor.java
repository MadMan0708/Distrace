package cz.cuni.mff.d3s.distrace;

class Instrumentor {

    public static void main(String[] args){
        System.out.println("Running forked JVM");
        Server s = new Server();
        s.start();
    }
}