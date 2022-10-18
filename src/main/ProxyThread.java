import java.util.Map;

import org.zeromq.ZMsg;

public class ProxyThread implements Runnable{
    
    Message message;
    Proxy parent;

    ProxyThread(Proxy parent, Message message){
        this.message=message;
        // dar reconstruct à mensagem com a classe Message
    }

    public void run(){
        Topic topic;
        switch(message.getCmd()){
            case PUT:
                // Envia ACK para o publisher
                topic = parent.topics.get(message.getTopic());
                if(topic==null){
                    parent.newTopic(message.getTopic());
                }
                topic.publish(message);
                break;
            case GET:
                topic = parent.topics.get(message.getTopic());
                if(topic==null){
                    ZMsg errorMsg=new ZMsg();
                    //errorMsg=new Message(ERROR, "No topic with that name");
                    parent.addMessageToSendQueue(errorMsg);
                }
                else{
                    ZMsg getResponse= topic.getMessage(message.getSenderID()).createMessage();
                    parent.addMessageToSendQueue(getResponse);
                }
                //topic.updateSubscriberNextMessage(message.getSenderID());
                break;
            case SUB:
                topic = parent.topics.get(message.getTopic());
                if(topic==null){
                    topic = parent.newTopic(message.getTopic());
                }
                topic.subscribe(message.getSenderID());
                ZMsg ackMessage=new ZMsg();
                //errorMsg=new Message(ERROR, "No topic with that name");
                parent.addMessageToSendQueue(ackMessage);      
                break;
            case UNSUB:
                topic = parent.topics.get(message.getTopic());
                if(topic==null){
                    ZMsg errorMsg=new ZMsg();
                    //errorMsg=new Message(ERROR, "No topic with that name");
                    parent.addMessageToSendQueue(errorMsg);
                }
                else{
                    topic.unsubscribe(message.getSenderID());
                    ackMessage=new ZMsg();
                    //errorMsg=new Message(ERROR, "No topic with that name");
                    parent.addMessageToSendQueue(ackMessage);      
                }
                break;
            case GET_REP:
                break;
            default:
                // Se isto acontecer é porque algo correu muito mal, ignora e envia msg de erro
                break;
        }
        
    }
}
