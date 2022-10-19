import org.zeromq.ZMsg;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;



public class SocketOwner {
    private static final int REPLYTIMEOUT = 9500;

    protected ZMQ.Socket socketZMQ;
    protected String id;
    protected String socketEndpoint;
    protected ZContext zContext;
    protected int timeoutCounter;

    public SocketOwner(ZContext zContext, String id, String socketEndpoint) {
        this.id = id;
        this.zContext = zContext;
        this.socketEndpoint = socketEndpoint;
        this.setup();
    }

    protected void setup() {
        this.socketZMQ = zContext.createSocket(SocketType.REQ);
        this.socketZMQ.setIdentity(id.getBytes(ZMQ.CHARSET));
        this.socketZMQ.setReceiveTimeOut(SocketOwner.REPLYTIMEOUT);
    }

    public ZMsg sendReceive(ZMsg message) throws Exception {
        timeoutCounter = -1;
        ZMsg reply;
        reply = null;


        while (reply == null) {
            ++timeoutCounter;
            if (!message.send(this.socketZMQ)) return null;
            reply = this.receiveMessage();
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

    public ZMsg receiveMessage() {
        ZMsg replyZMsg = ZMsg.recvMsg(this.socketZMQ);
        if (replyZMsg == null) {
            System.out.println("Got time out so I'll reconnect");
            this.disconnect_reconnect();
        }
        return replyZMsg;
    }
}