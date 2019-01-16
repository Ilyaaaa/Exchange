package ilya.exchange.main;

import android.content.Context;

import ilya.exchange.model.AuthManager;
import ilya.exchange.model.PrivatApiClient;

public class MainPresenterImpl implements MainPresenter {
    private SignOut authManager;
    private ApiClient client;

    MainPresenterImpl(Context context) {
        authManager = new AuthManager(context);
        client = new PrivatApiClient();
    }

    @Override
    public void signOut() {
        authManager.signOut();
    }

    @Override
    public void requestRates() {
        client.requestRates();
    }
}
