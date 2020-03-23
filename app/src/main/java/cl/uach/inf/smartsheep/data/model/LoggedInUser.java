package cl.uach.inf.smartsheep.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String displayName;

    public LoggedInUser( String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(){ this.displayName = displayName;}


}
