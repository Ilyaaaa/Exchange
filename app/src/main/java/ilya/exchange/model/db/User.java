package ilya.exchange.model.db;

public class User {
    private long id;
    private String email;
    private String pass;

    public User(long id, String email, String pass) {
        this.id = id;
        this.email = email;
        this.pass = pass;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPass() {
        return pass;
    }
}
