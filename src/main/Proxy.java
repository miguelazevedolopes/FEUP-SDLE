import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMQ.Poller;
import org.zeromq.*;

public class Proxy extends Thread implements Serializable{
    public final String SOCKET_ACCESS="5555";
    public final String STATE_FILE_PATH="state";

    private boolean keepRunning = true;

    Map <String,Topic> topics;

    private Queue <Message> messagesToSend=new LinkedList<>();

    final ExecutorService threadPool;

    ZContext ctx;
    Socket socket;
    
    public Proxy(){
        ctx = new ZContext();
        
        socket = ctx.createSocket(SocketType.ROUTER);

        socket.bind("tcp://*:" + SOCKET_ACCESS); 

        threadPool= Executors.newCachedThreadPool();

    }

    public synchronized void addMessageToSendQueue(Message msg){
        messagesToSend.add(msg);
    }

    public Map<String, Topic> getTopics() {
        return topics;
    }

    public synchronized void stopProxy(){
        keepRunning=false;
    }

    private synchronized boolean keepRunning(){
        return keepRunning;
    }

    private void pollSockets(){
        Poller poller = ctx.createPoller(1);
        poller.register(this.socket, Poller.POLLIN);

        while(keepRunning()){
            ZMsg zmsg;
            if(poller.poll()>=0){
                if(poller.pollin(0)){
                    zmsg=ZMsg.recvMsg(this.socket);
                    threadPool.execute(new ProxyThread(this,new Message(zmsg)));
                    saveStateToFile();
                }
            }
            while (!messagesToSend.isEmpty()) {
                ZMsg messageToSend=messagesToSend.poll().createMessage();
                messageToSend.send(this.socket);
            }
        }
    }

    public void start(){
        pollSockets();
    }

    public synchronized Topic newTopic(String topicName){
        Topic topic = new Topic();
        topics.put(topicName, topic);
        return topic;
    }

    private void saveStateToFile(){
        File myFile = new File(STATE_FILE_PATH);
        if(myFile.exists()){
            myFile.delete();
        }
        myFile = new File(STATE_FILE_PATH);
        try {
            FileOutputStream fOutputStream = new FileOutputStream(myFile.getAbsolutePath());
            ObjectOutputStream objOutStream = new ObjectOutputStream(fOutputStream);
            for (Message message : messagesToSend) {
                objOutStream.writeObject(message);
            }                
        } catch (IOException e) {
            e.printStackTrace();
        }
   }

}
