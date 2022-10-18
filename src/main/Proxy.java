import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMQ.Poller;
import org.zeromq.*;

public class Proxy extends Thread{
    public final String PUB_SOCKET="5555";
    public final String SUB_SOCKET="5556";
    private boolean keepRunning = true;

    Map <String,Topic> topics;

    final ExecutorService threadPool;

    ZContext ctx;
    Socket publisherSocket,subscriberSocket;
    
    public Proxy(){
        ctx = new ZContext(); //?
        publisherSocket = ctx.createSocket(SocketType.ROUTER);
        subscriberSocket = ctx.createSocket(SocketType.ROUTER);
        publisherSocket.bind("tcp://*:" + PUB_SOCKET); 
        subscriberSocket.bind("tcp://*:" + SUB_SOCKET);
        threadPool= Executors.newCachedThreadPool();
    }

    public synchronized void stopProxy(){
        keepRunning=false;
    }

    private synchronized boolean keepRunning(){
        return keepRunning;
    }

    private void pollSockets(){
        Poller poller = ctx.createPoller(2);
        poller.register(this.publisherSocket, Poller.POLLIN);
        poller.register(this.subscriberSocket, Poller.POLLIN);

        while(keepRunning()){
            byte[] message;
            if(poller.poll()>=0){
                if(poller.pollin(0)){
                    message=publisherSocket.recv(0);
                    threadPool.execute(new ProxyThread(ThreadType.PUB_HANDLER,topics,message));
                }
                if(poller.pollin(1)){
                    message=publisherSocket.recv(0);
                    threadPool.execute(new ProxyThread(ThreadType.SUB_HANDLER,topics,message));
                }
            }
        }
    }

    public void start(){
        pollSockets();
    }

}
