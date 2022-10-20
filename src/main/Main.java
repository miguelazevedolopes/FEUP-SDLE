import org.zeromq.ZContext;

public class Main {

    static ZContext zContext = new ZContext();

    public static int incorrectArgs() {
        System.out.println("Incorrect argument usage");
        return 1;
    }

    public static void proxy() {
        
        Proxy proxy = new Proxy(zContext);
        proxy.start();
    }

    public static void put(String topic, String message, String id) {
        ZContext zContext = new ZContext();
        Publisher publisher;
        publisher = new Publisher(zContext, id);
        publisher.put(topic, message);
        //System.out.printf("%s: I Published an update to topic >%s< with >%s< ",id, topic, message);
        System.exit(0);
    }

    public static  void get(String topic, String id) {
        ZContext zContext = new ZContext();
        Subscriber subscriber = new Subscriber(zContext, id);
        subscriber.get(topic);
        //System.out.printf("%s: Successfully performed a Get from topic >%s<", id, topic);
        publisher.closeSocket();
        System.exit(0);
    }

    public static void subscribe(String topic, String id) {
        ZContext zContext = new ZContext();
        Subscriber subscriber = new Subscriber(zContext, id);
        subscriber.subscribe(topic);
        //System.out.printf("%s: Successfully subscribed to topic >%s<", id, topic);
        subscriber.subscribe(topic);
        System.exit(0);
    }

    public static void unsubscribe(String topic, String id) {
        ZContext zContext = new ZContext();
        Subscriber subscriber = new Subscriber(zContext, id);
        subscriber.unsubscribe(topic);
        //System.out.printf("%s: Successfully unsubscribed from topic >%s<", id, topic);
        System.exit(0);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("No arguments not provided");
            return;
        }

        switch (args[0]) {
            case "Proxy":
                proxy();
                return;
            case "Put":
                if(args.length < 4) {
                    incorrectArgs();
                    return;
                }
                put(args[1], args[2], args[3]);
                return;
            case "Get":
                if(args.length < 3) {
                    incorrectArgs();
                    return;
                }
                put(args[1], args[2]);
                System.exit(0);
                return;
            case "Subscribe":
                if(args.length < 3) {
                    incorrectArgs();
                    return;
                }
                subscribe(args[1], args[2]);
                return;
            case "Unsubscribe":
                if(args.length < 3) {
                    incorrectArgs();
                    return;
                }
                unsubscribe(args[1], args[2]);
                return;
            default:
                incorrectArgs();
                break;
        }
    }
}
