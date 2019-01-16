package ilya.exchange.main;

import android.content.Context;

import ilya.exchange.model.AuthManager;

public class MainPresenterImpl implements MainPresenter {
    private SignOut authManager;

    MainPresenterImpl(Context context) {
        authManager = new AuthManager(context);
    }

    @Override
    public void signOut() {
        authManager.signOut();
    }
}
