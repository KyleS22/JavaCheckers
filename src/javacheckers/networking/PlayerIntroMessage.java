package javacheckers.networking;

import java.io.Serializable;

public class PlayerIntroMessage implements Serializable {
    public String username;
    public int ID;

    public PlayerIntroMessage(String username, int ID){
        this.username = username;
        this.ID = ID;
    }
}
