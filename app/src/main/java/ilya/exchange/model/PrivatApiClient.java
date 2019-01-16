package ilya.exchange.model;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import ilya.exchange.main.ApiClient;
import ilya.exchange.main.events.RatesRequestFailureEvent;
import ilya.exchange.main.events.RatesRequestSuccessEvent;
import ilya.exchange.main.objects.Rate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PrivatApiClient implements ApiClient {
    private Retrofit retrofit;
    private Api api;

    public PrivatApiClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.privatbank.ua")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(Api.class);
    }

    @Override
    public void requestRates() {
        Call<List<Rate>> rates = api.loadRates();
        rates.enqueue(new Callback<List<Rate>>() {
            @Override
            public void onResponse(Call<List<Rate>> call, Response<List<Rate>> response) {
                EventBus.getDefault().post(new RatesRequestSuccessEvent(response.body()));
            }

            @Override
            public void onFailure(Call<List<Rate>> call, Throwable t) {
                EventBus.getDefault().post(new RatesRequestFailureEvent(t.toString()));
            }
        });
    }
}
