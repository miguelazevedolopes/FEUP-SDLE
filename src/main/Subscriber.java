import javax.xml.transform.Source;

import org.zeromq.ZContext;
import org.zeromq.ZMsg;


public class Subscriber extends SocketOwner{

    public Subscriber(ZContext ctx,String id, String endpoint){
         super(ctx,id,endpoint);
    }

    public void subscribe(String topic){
        setup();
        connect();
        Message msg = new Message(MessageType.SUB,this.id,topic);
        ZMsg message=msg.createMessage();

        message.send(socketZMQ);

        ZMsg reply = ZMsg.recvMsg(socketZMQ);
        Message reply_msg = new Message(reply);

        System.out.println(reply_msg.getCmd().toString());
    }

    public void unsubscribe(String topic){
        setup();
        connect();

        Message msg = new Message(MessageType.UNSUB,this.id,topic);
        ZMsg message=msg.createMessage();

        message.send(socketZMQ);

        ZMsg reply = ZMsg.recvMsg(socketZMQ);
        Message reply_msg = new Message(reply);

        System.out.println(reply_msg.getCmd().toString());
    }


    public void get(String topic){
        setup();
        connect();

        Message msg = new Message(MessageType.GET,this.id,topic);
        ZMsg message=msg.createMessage();

        message.send(socketZMQ);

        ZMsg reply = ZMsg.recvMsg(socketZMQ);
        Message reply_msg = new Message(reply);
        if (reply_msg.getCmd()==MessageType.GET_REP){
            String idMsg =reply_msg.getID();

            String content = reply_msg.getContent();
            if(content.equals(null))
                System.out.println("No new messages!");
            else
                System.out.println(content);

            int tries=0;
            boolean okMsg=false;
            Message ack_msg = new Message(MessageType.ACK,this.id,idMsg);
            ack_msg.createMessage().send(socketZMQ);
            while(tries<3 && !okMsg ){
                
                ZMsg replyOk = ZMsg.recvMsg(socketZMQ);
                Message msg_Ok= new Message(replyOk);

                if(replyOk!=null){
                    okMsg=true;
                    String cmd =msg_Ok.getCmd().toString();
                    System.out.println(cmd);
                }
                    
                tries++;
            }
            
        }else if(reply_msg.getCmd()==MessageType.ERROR){
            System.out.println("Error!");
        }

    }

}
