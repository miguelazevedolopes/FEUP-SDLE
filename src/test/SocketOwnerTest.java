import org.junit.Test;
import org.zeromq.ZContext;

public class SocketOwnerTest {
    @Test
    public void test() {
        ZContext context = new ZContext();
        String id = "test";
        SocketOwner socketOwner = new SocketOwner(context,id,"tcp://localhost:5559");
        socketOwner.receiveMessage();
    }
}