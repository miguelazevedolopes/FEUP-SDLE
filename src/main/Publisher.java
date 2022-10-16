import org.zeromq.ZMQ.Socket;
import org.zeromq.*;
import org.zeromq.ZContext;
import java.sql.Timestamp;
import org.zeromq.ZMsg;
public class Publisher {
    public final String PUB_SOCKET="5555";

    public Publisher(ZContext ctx,String id){
        super(ctx,id);
    }
    public void put (String topic, String message){
        String args[] = new String[2];
        args[0]=topic;
        args[1]=message;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Socket pubSocket = ctx.createSocket(SocketType.REQ);

        pubSocket.connect("tcp://*:" + PUB_SOCKET);

        Message msg = new Message(this.id,"PUT",timestamp,args);
        String message=msg.createMessage();

        pubSocket.send(message);

    }


}
