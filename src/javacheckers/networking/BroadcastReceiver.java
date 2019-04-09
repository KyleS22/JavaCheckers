package javacheckers.networking;

import javacheckers.controller.JoinGameMenuController;
import javafx.util.Pair;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;

public class BroadcastReceiver {

    private volatile boolean broadcastShutdown; // Whether or not to shutdown the broadcaster

    private int TIMOUT_LENGTH = 2000;   // Number of milliseconds before considering a host to be dead

    /**
     * A list of maps to store the details of active hosts.  The keys are {"username": string, "IP": string,
     * "lastContact": long}
     */
    private List<Map<String, Object>> activeHosts;

    /**
     * The controller for this receiver
     */
    private JoinGameMenuController controller;

    /**
     * Creates the broadcast receiver and starts receiving messages
     * @param controller The controller for this receiver
     */
    public BroadcastReceiver(JoinGameMenuController controller){
        this.controller = controller;
        this.activeHosts = new ArrayList<>();
        BroadcastReceiveThread broadcastReceiveThread = new BroadcastReceiveThread();
        broadcastReceiveThread.setDaemon(true);
        broadcastReceiveThread.start();

        Thread checkHostsAliveThread = new Thread(){
            public void run(){
                while(!broadcastShutdown){
                    checkHostsActive();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        checkHostsAliveThread.setDaemon(true);
        checkHostsAliveThread.start();
    }

    /**
     * A thread for receiving broadcast messages
     */
    private class BroadcastReceiveThread extends Thread{
        public void run(){
            DatagramSocket socket = null;
            try{

                socket = new DatagramSocket(8889);
                socket.setBroadcast(true);


                while(!broadcastShutdown){
                    byte[] buffer = new byte[512];

                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    socket.receive(packet);

                    String received = new String(packet.getData(), 0, packet.getLength());

                    String IP = packet.getAddress().getHostAddress();

                    updateHost(received, IP, System.currentTimeMillis());

                }
                socket.close();
            }catch (Exception e){
                if(broadcastShutdown){
                    System.out.println("Broadcast Socket has shut down.");
                }else{
                    System.out.println("Broadcast Socket has shut down by error: " + e);
                }
                socket.close();
            }
        }
    }

    /**
     * Update the entry of a host in the active hosts list
     * @param username The username of the host
     * @param IP The IP address of the host
     * @param currentTimeMillis The current time in milliseconds
     */
    private void updateHost(String username, String IP, long currentTimeMillis){

        boolean newMap = true;
        for(Map m: this.activeHosts){
            if(m.get("IP").equals(IP)){
                m.put("lastContact", currentTimeMillis);
                newMap = false;
            }
        }

        if(newMap){
            System.out.println("Adding new host: " + IP);
            HashMap<String, Object> m = new HashMap<>();
            m.put("username", username);
            m.put("IP", IP);
            m.put("lastContact", currentTimeMillis);

            this.activeHosts.add(m);
            this.controller.updateHostList(username);
        }
        System.out.println("Updated Host");
    }

    /**
     * Check to see which hosts are still active
     */
    private void checkHostsActive(){
        System.out.println("Checking active hosts");
        Iterator<Map<String, Object>> iter = this.activeHosts.iterator();
        while(iter.hasNext()){
            Map m = iter.next();
            System.out.println(System.currentTimeMillis() - (long)m.get("lastContact"));
            if(System.currentTimeMillis() - (long)m.get("lastContact") > this.TIMOUT_LENGTH){
                iter.remove();
                System.out.println("Removing dead host");
                this.controller.removeHost((String) m.get("username"));
            }
        }
    }

    /**
     * Shutdown the broadcaster
     */
    public void shutdown(){
        this.broadcastShutdown = true;
    }

    /***
     * Getter for the list of active hosts
     * @return A list of the active host maps
     */
    public List<Map<String, Object>> getActiveHosts(){
        return activeHosts;
    }
}
