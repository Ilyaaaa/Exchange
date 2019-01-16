package ilya.exchange.main.events;

import java.util.List;

import ilya.exchange.main.objects.Rate;

public class RatesRequestSuccessEvent {
    private List<Rate> rates;

    public RatesRequestSuccessEvent(List<Rate> rates) {
        this.rates = rates;
    }

    public List<Rate> getRates() {
        return rates;
    }
}
