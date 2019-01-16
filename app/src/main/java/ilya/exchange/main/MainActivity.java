package ilya.exchange.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ilya.exchange.R;
import ilya.exchange.login.LoginActivity;
import ilya.exchange.main.events.RatesRequestFailureEvent;
import ilya.exchange.main.events.RatesRequestSuccessEvent;
import ilya.exchange.main.events.SignOutEvent;
import ilya.exchange.main.objects.Rate;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.main_root_lay) ConstraintLayout root;
    @BindView(R.id.main_content_lay) ConstraintLayout contentLayout;
    @BindView(R.id.main_progress) ProgressBar progress;

    private MainPresenter presenter;

    private List<Rate> rates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        presenter = new MainPresenterImpl(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.requestRates();

        EventBus.getDefault().register(this);
    }

    @OnClick(R.id.sign_out_btn)
    public void signOut() {
        presenter.signOut();
    }

    private void toggleProgress(boolean state) {
        if (state) {
            progress.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.GONE);
        } else {
            progress.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignOut(SignOutEvent event) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRateRequestSuccess(RatesRequestSuccessEvent event) {
        rates.clear();
        rates.addAll(event.getRates());
        toggleProgress(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRateRequestFailure(RatesRequestFailureEvent event) {
        Snackbar.make(root, event.getErr(), Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }
}
