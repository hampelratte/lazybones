package lazybones;

import de.hampelratte.svdrp.Connection;
import de.hampelratte.svdrp.Response;
import de.hampelratte.svdrp.commands.STAT;

public class ConnectionTester extends Thread {
    
    private boolean running = false;
    private static ConnectionTester ct = null;
    
    private boolean connected = false;
    
    private ConnectionTester() {
        start();
    }
    
    public static ConnectionTester getInstance() {
        if(ct == null) {
            ct = new ConnectionTester();
        }
        return ct;
    }

    public void startThread() {
        running = true;
    }
    
    public void stopThread() {
        running = false;
    }
    
    public void run() {
        Response res = null;
        while(true) {
            if(running) {
                try {
                    Connection connection = new Connection(VDRConnection.host, VDRConnection.port,
                            VDRConnection.timeout);
                    Connection.DEBUG = false;
                    res = connection.send(new STAT());
                    connection.close();
                    
                    if(res != null) {
                        connected = true;
                        System.out.println("CONNECTED");
                    } else {
                        connected = false;
                        System.out.println("DISCONNECTED");
                    }
                } catch (Exception e) {
                    connected = false;
                    System.out.println("DISCONNECTED");
                }
            }
            try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    public boolean isConnected() {
        return connected;
    }
}