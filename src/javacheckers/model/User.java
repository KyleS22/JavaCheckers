package javacheckers.model;

/**
 * Represents a user
 */
public class User {

    // The user's chosen name
    private String userName;

    // 1 Red or 0 Black, the colour of the users pieces
    private int colour;

    /**
     * Create a new user
     * @param username The new user's name
     * @param colour The new user's colour, 1 for red, 0 for black
     */
    public User(String username, int colour){
        this.userName = username;
        this.colour = colour;
    }

    /**
     * Get the users name
     * @return The users name
     */
    public String getUserName() {
        return userName;
    }


    /**
     * Get the user's piece colour
     * @return An int representing the user's piece colour.  0 For black, 1 for red
     */
    public int getColour() {
        return colour;
    }

}
