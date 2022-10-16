import java.util.Map;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMQ.Poller;
import org.zeromq.*;

public class Proxy {
    public final String PUB_SOCKET="5555";
    public final String SUB_SOCKET="5556";
    private boolean keepRunning = true;

    Map <String,Topic> topics;

    public Proxy(){
        Socket publisherSocket = ctx.createSocket(SocketType.ROUTER);
        Socket subscriberSocket = ctx.createSocket(SocketType.ROUTER);
        publisherSocket.bind("tcp://*:" + PUB_SOCKET); 
        subscriberSocket.bind("tcp://*:" + SUB_SOCKET); 
    
    }

    public synchronized void stopProxy(){
        keepRunning=false;
    }

    private synchronized boolean keepRunning(){
        return keepRunning;
    }

    private void pollSockets(){
        Poller poller = ctx.createPoller(2);
        poller.register(this.pubSocket, Poller.POLLIN);
        poller.register(this.subSocket, Poller.POLLIN);

        while(keepRunning()){
            
        }
    }

}
