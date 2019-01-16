package ilya.exchange.model;

import android.content.Context;
import android.content.SharedPreferences;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

import ilya.exchange.R;
import ilya.exchange.login.LoginModel;
import ilya.exchange.login.events.EmailErrorEvent;
import ilya.exchange.login.events.PassErrorEvent;
import ilya.exchange.login.events.SignSuccessEvent;
import ilya.exchange.main.SignOut;
import ilya.exchange.main.events.SignOutEvent;
import ilya.exchange.model.db.DBHelper;
import ilya.exchange.model.db.User;
import ilya.exchange.utils.MD5Creator;

import static android.content.Context.MODE_PRIVATE;

public class AuthManager implements LoginModel, SignOut {
    private static final String PREFS_NAME = "auth_prefs";
    private static final String EMAIL_KEY = "email";
    private static final String PASS_KEY = "pass";

    private DBHelper db;
    private Context context;
    private SharedPreferences prefs;

    public AuthManager(Context context) {
        this.context = context;

        db = new DBHelper(context);
    }

    private void rememberUser(User user) {
        prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(EMAIL_KEY, user.getEmail());
        editor.putString(PASS_KEY, MD5Creator.create(user.getPass()));

        editor.apply();
        EventBus.getDefault().post(new SignSuccessEvent());
    }

    @Override
    public void signUp(String email, String pass) {
        long id = db.addUser(email, MD5Creator.create(pass));

        if (id != -1) rememberUser(new User(id, email, pass));
        else EventBus.getDefault().post(new EmailErrorEvent(R.string.user_exists));
    }

    @Override
    public void signIn(String email, String pass) {
        User user = db.getUser(email);

        if (user == null)
            EventBus.getDefault().post(new EmailErrorEvent(R.string.user_not_exists));
        else if (!MD5Creator.create(pass).equals(user.getPass()))
            EventBus.getDefault().post(new PassErrorEvent(R.string.wrong_pass));
        else {
            rememberUser(user);

            EventBus.getDefault().post(new SignSuccessEvent());
        }
    }

    @Override
    public boolean signCheck() {
        prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return !Objects.requireNonNull(prefs.getString("email", "")).equals("");
    }

    @Override
    public void signOut() {
        prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.remove(EMAIL_KEY);
        editor.remove(PASS_KEY);

        editor.apply();

        EventBus.getDefault().post(new SignOutEvent());
    }
}
