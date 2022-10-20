import org.zeromq.ZMsg;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;



public class SocketOwner {
    private static final int REPLYTIMEOUT = 5000;

    protected ZMQ.Socket socketZMQ;
    protected String id;
    protected String socketEndpoint;
    protected ZContext zContext;

    public SocketOwner(ZContext zContext, String id, String socketEndpoint) {
        this.id = id;
        this.zContext = zContext;
        this.socketEndpoint = socketEndpoint;
        this.setup();
        this.connect();
    }

    protected void setup() {
        this.socketZMQ = zContext.createSocket(SocketType.REQ);
        this.socketZMQ.setIdentity(id.getBytes(ZMQ.CHARSET));
        this.socketZMQ.setReceiveTimeOut(REPLYTIMEOUT);
    }

    public ZMsg sendReceive(ZMsg message) throws Exception{
        int tries = 0;
        ZMsg reply;
        reply = null;

        while (reply == null && tries<4) {
            if (!message.send(this.socketZMQ)){
                this.receiveMessage();
                return null;
            };
            reply = this.receiveMessage();
            tries++;
        }
        return reply;
    }

    public void disconnect_reconnect() {

        this.socketZMQ.close();
        this.setup();
        this.connect();
    }

    public boolean connect() {
        return this.socketZMQ.connect("tcp://" +this.socketEndpoint);
    }

    public ZMsg receiveMessage() throws Exception {
        ZMsg replyZMsg = ZMsg.recvMsg(this.socketZMQ);
        if (replyZMsg == null) {
            System.out.println("Got time out so I'll reconnect");
            this.disconnect_reconnect();
        }
        return replyZMsg;
    }

    public void closeSocket(){
        socketZMQ.close();
    }
}