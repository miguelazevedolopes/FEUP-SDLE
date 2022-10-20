import org.zeromq.ZContext;

public class Main {

    public static int incorrectArgs() {
        System.out.println("Incorrect argument usage");
        return 1;
    }

    public static void proxy() {
        ZContext zContext = new ZContext();
        Proxy proxy = new Proxy(zContext);
        proxy.start();
        
    }

    public static void put(String topic, String message) {
        ZContext zContext = new ZContext();
        Publisher publisher;
        publisher = new Publisher(zContext, "PUBLISHER_ID");
        publisher.put(topic, message);
        System.out.printf("I Published an update to topic >%s< with >%s< ", topic, message);
        System.exit(0);
    }

    public static  void get(String topic) {
        ZContext zContext = new ZContext();
        Subscriber subscriber = new Subscriber(zContext, "SUBSCRIBER_ID");
        subscriber.subscribe(topic);
        subscriber.get(topic);
        System.out.printf("Successfully performed a Get from topic >%s<", topic);
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
                if(args.length < 3) {
                    incorrectArgs();
                    return;
                }
                put(args[1], args[2]);
                return;
            case "Get":
                if(args.length < 2) {
                    incorrectArgs();
                    return;
                }
                get(args[1]);
                return;
            default:
                incorrectArgs();
                break;
        }
    }

}
