import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Topic {
    Map <String,String> subscribers;
    LinkedHashMap <String,Message> messages;  

    private boolean hasMessages(String subscriberID){
        return subscribers.get(subscriberID)!=null;
    }

    private String getNextMessageID(String messageID){

        Iterator<Map.Entry<String, Message>> iterator = messages.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Message> entry = iterator.next();
            if (entry.getKey().equals(messageID)) {
                if (iterator.hasNext()) {
                    entry = iterator.next();
                    return entry.getKey();
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    public Message getMessage(String subscriberID) {
        if(!hasMessages(subscriberID)) return null;
        String msgID = subscribers.get(subscriberID);
        return messages.get(msgID);
    }

    public void updateSubscriberNextMessage(String subscriberID){
        String nextMessageID=getNextMessageID(subscriberID);
        subscribers.put(subscriberID,nextMessageID);
    }

    public void subscribe(String subscriberID){
        subscribers.put(subscriberID, null);
    }

    public void publish(Message message){
        if(!messages.containsKey(message.getID()))
            messages.put(message.getID(), message);
        for (Map.Entry<String,String> entry : subscribers.entrySet()){
            if(entry.getValue()==null)
                subscribers.put(entry.getKey(),message.getID());
        }
    }
    
}