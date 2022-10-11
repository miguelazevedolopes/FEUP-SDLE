import java.util.Map;

public class Proxy {
    Map <String,Topic> topics;

    public Message getSubscriberMessage(String subscriberID, String topic){
        Topic t = topics.get(topic);
        return t.getMessage(subscriberID);
    }
}
