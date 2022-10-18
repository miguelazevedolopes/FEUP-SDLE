import java.util.Map;

public class ProxyThread implements Runnable{
    
    private ThreadType type;
    Map <String,Topic> topics;
    Message message;

    ProxyThread(ThreadType type, Map <String,Topic> topics, byte[] message){
        this.type=type;
        this.topics=topics;

        // dar reconstruct à mensagem com a classe Message
    }

    public void run(){
        switch(type){
            case PUB_HANDLER:
                // Envia ACK para o publisher
                Topic topic = topics.get(message.getTopic());
                topic.publish(message);
                break;
            case SUB_HANDLER:
                Topic topic = topics.get(message.getTopic());
                topic.getMessage(message.getSubscriberID());
                // Envia Message e aguarda por ACK. Se receber envia OK. Se não receber ACK reenvia K vezes até receber 
                if(true){ /*Tudo correr direitinho */
                    topic.updateSubscriberNextMessage(message.getSubscriberID);
                }

            default:
                // Se isto acontecer é porque algo correu muito mal, ignora e envia msg de erro
                break;
        }
        
    }
}
