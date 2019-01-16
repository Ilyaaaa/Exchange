package ilya.exchange.login;


import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import ilya.exchange.R;
import ilya.exchange.login.events.EmailErrorEvent;
import ilya.exchange.login.events.PassErrorEvent;
import ilya.exchange.model.AuthManager;

public class LoginPresenterImpl implements LoginPresenter {
    private LoginModel model;

    LoginPresenterImpl(Context context){
         model = new AuthManager(context);
    }

    @Override
    public boolean signCheck() {
        return model.signCheck();
    }

    @Override
    public void signIn(String email, String pass) {
        if (validate(email, pass)) model.signIn(email, pass);
    }

    @Override
    public void signUp(String email, String pass) {
        if (validate(email, pass)) model.signUp(email, pass);
    }

    private boolean validate(String email, String pass){
        if (email.isEmpty() || !email.contains("@")) {
            EventBus.getDefault().post(new EmailErrorEvent(R.string.email_error));
            return false;
        }

        if (pass.length() < 6){
            EventBus.getDefault().post(new PassErrorEvent(R.string.pass_error));
            return false;
        }

        return true;
    }
}
