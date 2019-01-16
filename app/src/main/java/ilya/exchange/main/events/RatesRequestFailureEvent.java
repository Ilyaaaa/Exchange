package ilya.exchange.main.events;

public class RatesRequestFailureEvent {
    private String err;

    public RatesRequestFailureEvent(String err) {
        this.err = err;
    }

    public String getErr() {
        return err;
    }
}
