import org.zeromq.ZContext;

public class Main {

    static ZContext zContext = new ZContext();

    public static void incorrectArgs() {
        System.out.println("Incorrect argument usage");
        System.exit(1);;
    }

    public static void server() {
        final Server server = new Server(zContext);
        Thread shutdownListener = new Thread(){
            public void run(){
                server.stopServer();
                while(server.isAlive());
                zContext.destroy();
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
        zContext.destroy();
    }

    public static  void get(String topic, String id) {
        Subscriber subscriber = new Subscriber(zContext, id);
        subscriber.get(topic);
        subscriber.closeSocket();
        zContext.destroy();
    }

    public static void subscribe(String topic, String id) {
        Subscriber subscriber = new Subscriber(zContext, id);
        subscriber.subscribe(topic);
        subscriber.closeSocket();
        zContext.destroy();
    }

    public static void unsubscribe(String topic, String id) {
            Subscriber subscriber = new Subscriber(zContext, id);
        subscriber.unsubscribe(topic);
        subscriber.closeSocket();
        zContext.destroy();
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("No arguments not provided");
            System.exit(1);
        }

        switch (args[0]) {
            case "Server":
                server();
                break;
            case "Put":
                if(args.length < 4) {
                    incorrectArgs();
                    break;
                }
                put(args[1], args[2], args[3]);
                break;
            case "Get":
                if(args.length < 3) {
                    incorrectArgs();
                    break;
                }
                get(args[1], args[2]);
                break;
            case "Subscribe":
                if(args.length < 3) {
                    incorrectArgs();
                    break;
                }
                subscribe(args[1], args[2]);
                break;
            case "Unsubscribe":
                if(args.length < 3) {
                    incorrectArgs();
                    break;
                }
                unsubscribe(args[1], args[2]);
                break;
            default:
                incorrectArgs();
                break;
        }

    }
}
