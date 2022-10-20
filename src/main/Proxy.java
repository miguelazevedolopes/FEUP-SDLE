import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMQ.Poller;
import org.zeromq.*;

public class Proxy extends Thread{
    public final String SOCKET_ACCESS="localhost:5560";
    public final String STATE_FILE_PATH="state";

    private boolean keepRunning = true;

    Map <String,Topic> topics;

    private Queue <Message> messagesToSend;

    final ExecutorService threadPool;

    ZContext ctx;
    Socket socket;
    
    public Proxy(ZContext ctx){
        this.ctx = ctx;

        if(!restoreStateFromFile()){
            messagesToSend=new LinkedList<>();
            topics=new HashMap<>();
            System.out.println("Deu erro a dar load");
        }

        socket = ctx.createSocket(SocketType.ROUTER);

        if(!socket.bind("tcp://" + SOCKET_ACCESS)){
            System.out.println("Error on proxy bind");
        }
        else System.out.println("Proxy bind success");

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
            if(poller.poll(1000)>=0){
                if(poller.pollin(0)){

                    zmsg=ZMsg.recvMsg(this.socket);
            

                    threadPool.execute(new ProxyThread(this,new Message(zmsg)));
                    saveStateToFile();
                }
            }

            while (!messagesToSend.isEmpty()) {
                ZMsg messageToSend=messagesToSend.poll().createIdentifiedMessage();
                messageToSend.send(this.socket);
            }
        }
        saveStateToFile();
    }

    @Override
    public void run(){
        pollSockets();
        socket.disconnect("tcp://" + SOCKET_ACCESS);
        socket.close();
    }

    public synchronized Topic newTopic(String topicName){
        Topic topic = new Topic();
        topics.put(topicName, topic);
        return topic;
    }

    private synchronized void saveStateToFile(){
        File myFile = new File(STATE_FILE_PATH);
        if(myFile.exists()){
            myFile.delete();
        }
        try {
            myFile.createNewFile();
            FileOutputStream fOutputStream = new FileOutputStream(myFile.getAbsolutePath());
            ObjectOutputStream objOutStream = new ObjectOutputStream(fOutputStream);
            objOutStream.writeObject(this.topics);
                          
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean restoreStateFromFile(){
        File myFile = new File(STATE_FILE_PATH);
        Map<String,Topic> savedState;
        if(!myFile.exists()){
            return false;
        }
        try {
            FileInputStream fInputStream = new FileInputStream(myFile.getAbsolutePath());
            ObjectInputStream objectInputStream = new ObjectInputStream(fInputStream);
            savedState = (HashMap<String,Topic>) objectInputStream.readObject();
            this.topics=savedState;
            return true;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }



}
