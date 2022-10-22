public class ServerThread implements Runnable{
    
    Message message;
    Server parent;

    ServerThread(Server parent, Message message){
        this.message=message;
        this.parent=parent;
    }

    public void run(){
        Topic topic;
        switch(message.getMessageType()){
            case PUT:
                System.out.println("SERVER: Receive a "+message.getMessageType().toString() +" from "+message.getClientID());
                topic = parent.topics.get(message.getTopic());
                if(topic==null){
                    topic=parent.newTopic(message.getTopic());
                }
                topic.publish(message);
                Message ackMessage=new Message(MessageType.ACK,message.getClientID());
                parent.addMessageToSendQueue(ackMessage);
                break;
            case GET:
                System.out.println("SERVER: Receive a "+message.getMessageType().toString() +" from "+message.getClientID());

                topic = parent.topics.get(message.getTopic());
                if(topic==null){
                    Message errorMsg=new Message(MessageType.ERROR,message.getClientID(),message.getTopic(), "No topic with that name");
                    parent.addMessageToSendQueue(errorMsg);
                }
                else{
                    if(!topic.isSubscribed(message.getClientID())){
                        Message errorMsg=new Message(MessageType.ERROR,message.getClientID(),message.getTopic(), "You are not subscribed to that topic");
                        parent.addMessageToSendQueue(errorMsg);
                        return;
                    }
                    Message retrievedMessage =topic.getMessage(message.getClientID());
                    if(retrievedMessage==null){
                        Message errorMsg=new Message(MessageType.GET_REP,message.getClientID(),message.getTopic(), null);
                        parent.addMessageToSendQueue(errorMsg);
                        return;
                    }
                    String messageContent= retrievedMessage.getContent();

                    Message getResponse = new Message(MessageType.GET_REP,message.getClientID(),message.getTopic(),messageContent);
                    parent.addMessageToSendQueue(getResponse);
                }
                break;
            case SUB:
                System.out.println("SERVER: Receive a "+message.getMessageType().toString() +" from "+message.getClientID());
                topic = parent.topics.get(message.getTopic());
                if(topic==null){
                    topic = parent.newTopic(message.getTopic());
                }
                topic.subscribe(message.getClientID());
                ackMessage=new Message(MessageType.ACK,message.getClientID());
                parent.addMessageToSendQueue(ackMessage);     
                break;
            case UNSUB:
                System.out.println("SERVER: Receive a "+message.getMessageType().toString() +" from "+message.getClientID());
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
                System.out.println("SERVER: Receive a "+message.getMessageType().toString() +" from "+message.getClientID());
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
                System.out.println("weird");
                // Se isto acontecer é porque algo correu muito mal, ignora e envia msg de erro
                break;
        }
        
    }
}
