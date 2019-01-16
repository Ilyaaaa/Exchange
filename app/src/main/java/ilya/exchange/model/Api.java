package ilya.exchange.model;

import java.util.List;

import ilya.exchange.main.objects.Rate;
import retrofit2.Call;
import retrofit2.http.GET;

public interface Api {
    @GET("p24api/pubinfo?exchange&json&coursid=11")
    Call<List<Rate>> loadRates();
}
