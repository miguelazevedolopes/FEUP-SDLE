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
import java.util.concurrent.TimeUnit;

import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMQ.Poller;
import org.zeromq.*;

public class Server extends Thread{
    public final String SOCKET_ACCESS="*:5560";
    public final String STATE_FILE_PATH="state";

    private boolean keepRunning = true;

    Map <String,Topic> topics;

    private Queue <Message> messagesToSend;

    final ExecutorService threadPool;

    ZContext ctx;
    Socket socket;
    
    public Server(ZContext ctx){
        this.ctx = ctx;

        if(!restoreStateFromFile()){
            topics=new HashMap<>();
        }
        messagesToSend=new LinkedList<>();

        socket = ctx.createSocket(SocketType.ROUTER);
        socket.setSendTimeOut(0);
        if(!socket.bind("tcp://" + SOCKET_ACCESS)){
            System.out.println("SERVER: Bind error");
        }
        else System.out.println("SERVER: Bind success");

        threadPool= Executors.newCachedThreadPool();        
    }

    public synchronized void addMessageToSendQueue(Message msg){
        messagesToSend.add(msg);
    }

    public Map<String, Topic> getTopics() {
        return topics;
    }

    public synchronized void stopServer(){
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
            poller.poll(50);

            if(poller.pollin(0)){
                zmsg=ZMsg.recvMsg(this.socket,ZMQ.DONTWAIT);
                threadPool.execute(new ServerThread(this,new Message(zmsg)));
            }
            while (!messagesToSend.isEmpty()) {
                Message messageToSend =messagesToSend.poll();
                System.out.println("SERVER: " + "Sending a " + messageToSend.getMessageType().toString() + " to " + messageToSend.getClientID());
                ZMsg zMessageToSend=messageToSend.createIdentifiedMessage();
                zMessageToSend.send(this.socket);
                saveStateToFile();
            }
            
        }
        threadPool.shutdown();
        try {
            if(!threadPool.awaitTermination(10, TimeUnit.SECONDS)) threadPool.shutdownNow();
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }
        while (!messagesToSend.isEmpty()) {
            ZMsg messageToSend=messagesToSend.poll().createIdentifiedMessage();
            messageToSend.send(this.socket);
        }
        saveStateToFile();
    }

    @Override
    public void run(){
        pollSockets();
        socket.unbind("tcp://" + SOCKET_ACCESS);
        socket.close();
        System.out.println("SERVER: Closed");
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
            objOutStream.close();
            fOutputStream.close();                          
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private boolean restoreStateFromFile(){
        File myFile = new File(STATE_FILE_PATH);

        Map<String,Topic> savedState;

        if(!(myFile.isFile()&& myFile.canRead())){
            return false;
        }
        try {
            FileInputStream fInputStream = new FileInputStream(myFile.getAbsolutePath());
            ObjectInputStream objectInputStream = new ObjectInputStream(fInputStream);

            savedState = (HashMap<String,Topic>) objectInputStream.readObject();
            
            fInputStream.close();
            objectInputStream.close();
            
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
