import org.zeromq.ZContext;
import org.zeromq.ZMsg;


public class Subscriber extends SocketOwner{
    public static final String SOCKET_ACCESS="localhost:5560";

    public Subscriber(ZContext ctx,String id){
        super(ctx,id,SOCKET_ACCESS);
    }

    public void subscribe(String topic){

        Message msg = new Message(MessageType.SUB,this.id,topic);
        ZMsg message=msg.createMessage();

        ZMsg reply;
        try {
            reply = sendReceive(message);
            if(reply==null){
                System.out.println("Failed trying to communicate with server. Gave up.");
                return;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
    

        Message reply_msg = new Message(reply);

        System.out.println(reply_msg.getMessageType().toString());
    }

    public void unsubscribe(String topic){


        Message msg = new Message(MessageType.UNSUB,this.id,topic);
        ZMsg message=msg.createMessage();

        ZMsg reply;
        try {
            reply = sendReceive(message);
            if(reply==null){
                System.out.println("Failed trying to communicate with server. Gave up.");
                return;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }

        Message reply_msg = new Message(reply);

        System.out.println(reply_msg.getMessageType().toString());
    }


    public void get(String topic){

        Message msg = new Message(MessageType.GET,this.id,topic);
        ZMsg message=msg.createMessage();

        ZMsg reply;
        try {
            reply = sendReceive(message);
            if(reply==null){
                System.out.println("Failed trying to communicate with server. Gave up.");
                return;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }

        Message reply_msg = new Message(reply);
        if (reply_msg.getMessageType()==MessageType.GET_REP){
            String msgTopic =reply_msg.getTopic();

            String content = reply_msg.getContent();
            if(content == null)
                System.out.println("No new messages!");
            else
                System.out.println(content);


            Message ackMsg = new Message(MessageType.ACK,this.id,msgTopic);
                
            ZMsg replyOk;
            try {
                replyOk = sendReceive(ackMsg.createMessage());
                if(replyOk==null){
                    System.out.println("Failed trying to communicate with server. Gave up.");
                    return;
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            }

            Message msg_Ok= new Message(replyOk);
            String cmd =msg_Ok.getMessageType().toString();
            System.out.println(cmd);
                    
            
        }else if(reply_msg.getMessageType()==MessageType.ERROR){
            System.out.println(reply_msg.getContent());
        }

    }

}
