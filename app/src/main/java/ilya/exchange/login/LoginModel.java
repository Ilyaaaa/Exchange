package ilya.exchange.login;

public interface LoginModel {
    boolean signCheck();
    void signIn(String email, String pass);
    void signUp(String email, String pass);
}
