package lazybones;

import de.hampelratte.svdrp.Response;
import de.hampelratte.svdrp.commands.STAT;

public class ConnectionTester extends Thread {
    
    private boolean running = true;
    private static ConnectionTester ct = null;
    
    private boolean connected = false;
    
    private ConnectionTester() {}
    
    public static ConnectionTester getInstance() {
        if(ct == null) {
            ct = new ConnectionTester();
        }
        return ct;
    }
    
    public void stopThread() {
        running = false;
    }
    
    public void run() {
        while(running) {
            try {
                Response res = VDRConnection.send(new STAT());
                if(res != null) {
                    connected = true;
                } else {
                    connected = false;
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                connected = false;
            }
        }
    }

    public boolean isConnected() {
        return connected;
    }
}
