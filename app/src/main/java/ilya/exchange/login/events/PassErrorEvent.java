package ilya.exchange.login.events;

public class PassErrorEvent {
    private int strResId;

    public PassErrorEvent(int strResId) {
        this.strResId = strResId;
    }

    public int getStrResId() {
        return strResId;
    }
}
