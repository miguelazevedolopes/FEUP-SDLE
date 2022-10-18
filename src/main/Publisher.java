import org.zeromq.ZContext;
import org.zeromq.ZMsg;

public class Publisher extends SocketOwner{

    public Publisher(ZContext ctx,String id, String endpoint){
        super(ctx,id,endpoint);
    }
    public void put (String topic, String message){
        setup();
        connect();

        Message msg = new Message(MessageType.PUT,this.id,topic,message);
        ZMsg messageString=msg.createMessage();

        messageString.send(socketZMQ);

        ZMsg reply = ZMsg.recvMsg(socketZMQ);
        Message reply_msg = new Message(reply);

        System.out.println(reply_msg.getCmd().toString());


    }


}