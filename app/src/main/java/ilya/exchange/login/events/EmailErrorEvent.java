package ilya.exchange.login.events;

public class EmailErrorEvent {
    private int strResId;

    public EmailErrorEvent(int strResId) {
        this.strResId = strResId;
    }

    public int getStrResId() {
        return strResId;
    }
}
