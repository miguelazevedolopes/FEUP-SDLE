import org.junit.Test;
import org.zeromq.ZContext;
import org.zeromq.ZMsg;

public class SocketOwnerTest {
    @Test
    public void test() throws Exception {
        ZContext context = new ZContext();
        String id = "test";
        SocketOwner socketOwner = new SocketOwner(context,id,"tcp:*//localhost:5559");
        socketOwner.sendReceive(new ZMsg());
    }
}