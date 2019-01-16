package ilya.exchange.login;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.google.android.material.textfield.TextInputLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ilya.exchange.main.MainActivity;
import ilya.exchange.R;
import ilya.exchange.login.events.EmailErrorEvent;
import ilya.exchange.login.events.PassErrorEvent;
import ilya.exchange.login.events.SignSuccessEvent;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.login_root_view) ScrollView rootView;
    @BindView(R.id.login_progress_bar) ProgressBar progressBar;
    @BindView(R.id.login_form) LinearLayout form;
    @BindView(R.id.login_email_layout)
    TextInputLayout emailLayout;
    @BindView(R.id.login_pass_layout) TextInputLayout passLayout;
    @BindView(R.id.login_email_field) EditText emailField;
    @BindView(R.id.login_pass_field) EditText passField;

    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        presenter = new LoginPresenterImpl(getApplicationContext());
        if (presenter.signCheck()) startMainActivity();

        ButterKnife.bind(this); }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    private void toggleProgress(boolean state) {
        if (state) {
            progressBar.setVisibility(View.VISIBLE);
            form.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            form.setVisibility(View.VISIBLE);
        }
    }

    private void resetErrors() {
        emailLayout.setError(null);
        passLayout.setError(null);
    }

    @OnClick(R.id.login_sign_in_btn)
    public void signIn(){
        toggleProgress(true);
        resetErrors();
        presenter.signIn(emailField.getText().toString(), passField.getText().toString());
    }

    @OnClick(R.id.login_sign_up_btn)
    public void signUp(){
        toggleProgress(true);
        resetErrors();
        presenter.signUp(emailField.getText().toString(), passField.getText().toString());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEmailError(EmailErrorEvent event) {
        toggleProgress(false);
        emailLayout.setError(getString(event.getStrResId()));
        emailField.requestFocus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPassError(PassErrorEvent event) {
        toggleProgress(false);
        passLayout.setError(getString(event.getStrResId()));
        passField.requestFocus();
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignSuccess(SignSuccessEvent event) {
        toggleProgress(false);
        startMainActivity();
    }
}
