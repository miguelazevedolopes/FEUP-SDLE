public class ProxyThread implements Runnable{
    
    Message message;
    Proxy parent;

    ProxyThread(Proxy parent, Message message){
        this.message=message;
        this.parent=parent;
    }

    public void run(){
        Topic topic;
        switch(message.getMessageType()){
            case PUT:
                topic = parent.topics.get(message.getTopic());
                if(topic==null){
                    parent.newTopic(message.getTopic());
                }
                topic.publish(message);
                Message ackMessage=new Message(MessageType.ACK,message.getClientID());
                parent.addMessageToSendQueue(ackMessage);
                break;
            case GET:
                topic = parent.topics.get(message.getTopic());
                if(topic==null){
                    Message errorMsg=new Message(MessageType.ERROR,message.getClientID(),message.getTopic(), "No topic with that name");
                    parent.addMessageToSendQueue(errorMsg);
                }
                else{
                    String messageContent= topic.getMessage(message.getClientID()).getContent();
                    Message getResponse = new Message(MessageType.GET_REP,message.getClientID(),message.getTopic(),messageContent);
                    parent.addMessageToSendQueue(getResponse);
                }
                break;
            case SUB:
                topic = parent.topics.get(message.getTopic());
                if(topic==null){
                    topic = parent.newTopic(message.getTopic());
                }
                topic.subscribe(message.getClientID());
                ackMessage=new Message(MessageType.ACK,message.getClientID());
                parent.addMessageToSendQueue(ackMessage);     
                break;
            case UNSUB:
                topic = parent.topics.get(message.getTopic());
                if(topic==null){
                    Message errorMsg=new Message(MessageType.ERROR,message.getClientID(),message.getTopic(), "No topic with that name");
                    parent.addMessageToSendQueue(errorMsg);
                }
                else{
                    topic.unsubscribe(message.getClientID());
                    ackMessage=new Message(MessageType.ACK,message.getClientID());
                    parent.addMessageToSendQueue(ackMessage);   
                }
                break;
            case ACK:
                topic = parent.topics.get(message.getTopic());
                if(topic==null){
                    Message errorMsg=new Message(MessageType.ERROR,message.getClientID(),message.getTopic(), "No topic with that name");
                    parent.addMessageToSendQueue(errorMsg);
                }
                else{
                    topic.updateSubscriberNextMessage(message.getClientID());
                    topic.removeMessagesWithoutRecipient();
                    ackMessage=new Message(MessageType.ACK,message.getClientID());
                    parent.addMessageToSendQueue(ackMessage);   
                }
                break;
            default:
                // Se isto acontecer é porque algo correu muito mal, ignora e envia msg de erro
                break;
        }
        
    }
}
