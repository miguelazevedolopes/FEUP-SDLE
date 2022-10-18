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
        //GET e nao esta subscrito, GET e nao tem updates ou get e tem content
        String args[] = new String[1];
        args[0]=topic;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Socket subSocket = ctx.createSocket(SocketType.REQ);
        subSocket.setReceiveTimeOut(3000);//3 segundos
        subSocket.connect("tcp://*:" + SUB_SOCKET);

        Message msg = new Message(this.id,"GET",timestamp,args);
        String message=msg.createMessage();

        subSocket.send(message);
        //tratar das mensagens com o servidor (ACK E OK)

        byte [] reply = subSocket.recv(0);
        String reply_string = new String(reply,ZMQ.CHARSET);
        Message reply_msg = new Message(reply_string);
        if (reply_msg.getType().equals("CONTENT")){
            String args_reply [] = new String[1];
            Timestamp timestampReply = new Timestamp(System.currentTimeMillis());
            //ID MESSAGE CONTEUDO 
            int idMsg =reply_msg.getArgs(0);
            args_reply[0]=id;
            //content se for null não ha update ? ou vem uma mensagem diferente quando não ha update (type==EMPTY)
            String content = reply_msg.getArgs(1);
            //o que fazemos com o content damos print ou fazemos return e tratamos na main??
            int tries=0;
            boolean okMsg=false;
            Message ack_msg = new Message(this.id,"ACK",timestampReply,idMsg);
            subSocket.send(ack_msg.createMessage());
            while(tries<3 && !okMsg ){
                byte [] replyOk = subSocket.recv(0);
                if(replyOk!=null){
                    okMsg=true;
                    //print?
                }
                    
                tries++;
            }
            
        }
           
        System.out.println(reply_string);

    }

}
