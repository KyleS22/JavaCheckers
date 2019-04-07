package javacheckers.networking;

import javafx.util.Pair;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;

public class BroadcastReceiver {

    private volatile boolean broadcastShutdown;
    private int TIMOUT_LENGTH = 2000;   // Number of milliseconds before considering a host to be dead

    private List<Map<String, Object>> activeHosts;

    public BroadcastReceiver(){
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
        }
        System.out.println("Updated Host");
    }

    private void checkHostsActive(){
        System.out.println("Checking active hosts");
        Iterator<Map<String, Object>> iter = this.activeHosts.iterator();
        while(iter.hasNext()){
            Map m = iter.next();
            System.out.println(System.currentTimeMillis() - (long)m.get("lastContact"));
            if(System.currentTimeMillis() - (long)m.get("lastContact") > this.TIMOUT_LENGTH){
                iter.remove();
                System.out.println("Removing dead host");
            }
        }
    }

    public void shutdown(){
        this.broadcastShutdown = true;
    }

    public List<Map<String, Object>> getActiveHosts(){
        return activeHosts;
    }
}
