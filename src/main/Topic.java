import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Topic implements Serializable{
    
    /*
     * Maps SubscriberIDs to MessageIDs
     */
    Map <String,String> subscribers;

    /*
     * Maps MessageIDs to the actual message object
     */
    Map <String,Message> messages;  

    public Topic(){
        this.subscribers= new HashMap<>();
        this.messages = new LinkedHashMap<>();
    }

    private boolean hasMessages(String subscriberID){
        return subscribers.get(subscriberID)!=null;
    }

    public boolean isSubscribed(String subscriberID){
        return subscribers.keySet().contains(subscriberID);
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

    public synchronized void removeMessagesWithoutRecipient(){
        Iterator<Map.Entry<String, Message>> iterator = messages.entrySet().iterator();
        List <String> messagesToRemove = new ArrayList<>();
        boolean flag=true;

        while(iterator.hasNext()){
            Map.Entry<String, Message> entry = iterator.next();
            String messageID = entry.getKey();
            for (String mID : subscribers.values()) {
                if(messageID.equals(mID)){
                    flag=false;
                    break;
                }
            }
            if(flag){
                messagesToRemove.add(messageID);
            }
            else{
                break;
            }
        }

        for (String mID : messagesToRemove) {
            messages.remove(mID);
        }
    }

    public Message getMessage(String subscriberID) {
        if(!hasMessages(subscriberID)) return null;
        String msgID = subscribers.get(subscriberID);
        return messages.get(msgID);
    }

    public synchronized void updateSubscriberNextMessage(String subscriberID){
        String nextMessageID=getNextMessageID(subscriberID);
        subscribers.put(subscriberID,nextMessageID);
    }

    public synchronized void subscribe(String subscriberID){
        subscribers.put(subscriberID, null);
    }

    public synchronized void unsubscribe(String subscriberID){
        subscribers.remove(subscriberID);
    }

    public synchronized void publish(Message message){
        if(!messages.containsKey(message.getID()))
            messages.put(message.getID(), message);
        for (Map.Entry<String,String> entry : subscribers.entrySet()){
            if(entry.getValue()==null)
                subscribers.put(entry.getKey(),message.getID());
        }
    }

}
