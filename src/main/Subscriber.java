import org.zeromq.ZMQ.Socket;
import org.zeromq.*;
import org.zeromq.ZContext;
import java.sql.Timestamp;
import org.zeromq.ZMsg;

public class Subscriber {

    public final String SUB_SOCKET="5556";

    public Subscriber(ZContext ctx,String id){
        super(ctx,id);
    }

    public void subscribe(String topic){
        String args[] = new String[1];
        args[0]=topic;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Socket subSocket = ctx.createSocket(SocketType.REQ);

        subSocket.connect("tcp://*:" + SUB_SOCKET);

        Message msg = new Message(this.id,"SUBSCRIBE",timestamp,args);
        String message=msg.createMessage();

        subSocket.send(message);
    }

    public void unsubscribe(String topic){
        String args[] = new String[1];
        args[0]=topic;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Socket subSocket = ctx.createSocket(SocketType.REQ);

        subSocket.connect("tcp://*:" + SUB_SOCKET);

        Message msg = new Message(this.id,"UNSUBSCRIBE",timestamp,args);
        String message=msg.createMessage();

        subSocket.send(message);
    }


    public void get(String topic){
        String args[] = new String[1];
        args[0]=topic;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Socket subSocket = ctx.createSocket(SocketType.REQ);

        subSocket.connect("tcp://*:" + SUB_SOCKET);

        Message msg = new Message(this.id,"GET",timestamp,args);
        String message=msg.createMessage();

        subSocket.send(message);
        //tratar das mensagens com o servidor (ACK E OK)
    }

}
