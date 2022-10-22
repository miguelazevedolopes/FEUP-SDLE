import org.zeromq.ZContext;

public class Main {

    static ZContext zContext = new ZContext();

    public static int incorrectArgs() {
        System.out.println("Incorrect argument usage");
        return 1;
    }

    public static void server() {
        final Server server = new Server(zContext);
        Thread shutdownListener = new Thread(){
            public void run(){
                server.stopServer();
                while(server.isAlive());
            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownListener);
        server.start();
    }

    public static void put(String topic, String message, String id) {
        Publisher publisher;
        publisher = new Publisher(zContext, id);
        publisher.put(topic, message);
        publisher.closeSocket();
        System.exit(0);
    }

    public static  void get(String topic, String id) {
        Subscriber subscriber = new Subscriber(zContext, id);
        subscriber.get(topic);
        subscriber.closeSocket();
        System.exit(0);

    }

    public static void subscribe(String topic, String id) {
        Subscriber subscriber = new Subscriber(zContext, id);
        subscriber.subscribe(topic);
        subscriber.closeSocket();
        System.exit(0);

    }

    public static void unsubscribe(String topic, String id) {
            Subscriber subscriber = new Subscriber(zContext, id);
        subscriber.unsubscribe(topic);
        subscriber.closeSocket();
        System.exit(0);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("No arguments not provided");
            return;
        }

        switch (args[0]) {
            case "Server":
                server();
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
                get(args[1], args[2]);
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
