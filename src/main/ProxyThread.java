public enum ThreadType {
    PUB_HANDLER,
    SUB_HANDLER
}

public class ProxyThread implements Runnable{
    
    private ThreadType type;

    ProxyThread(ThreadType type){
        this.type=type;
    }

    public void run(){
        
    }
}
