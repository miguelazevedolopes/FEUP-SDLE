public class Subscriber {

    public final String SUB_SOCKET="5556";

    public Subscriber(ZContext ctx,String id){
        super(ctx,id);
    }
    public void get(String topic){
        String args= new String[1];
        args[0]=topic;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Socket subSocket = ctx.createSocket(SocketType.REQ);

        subSocket.connect("tcp://*:" + SUB_SOCKET);

        ZMsg msg = new Message(this.id,"GET",timestamp,args);
        String message=msg.createMessage();

        subSocket.send(message);
    }
}
