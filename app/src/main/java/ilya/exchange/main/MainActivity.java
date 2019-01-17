package ilya.exchange.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

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
    @BindView(R.id.num_field) EditText numField;
    @BindView(R.id.result_view) EditText resultView;
    @BindView(R.id.spinner1) Spinner spinner;
    @BindView(R.id.spinner2) Spinner spinner2;

    private MainPresenter presenter;
    private List<Rate> rates;
    private String baseCcy;
    private String ccy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        presenter = new MainPresenterImpl(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.requestRates();

        EventBus.getDefault().register(this);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_item: {
                presenter.signOut();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignOut(SignOutEvent event) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRateRequestSuccess(RatesRequestSuccessEvent event) {
        rates = event.getRates();

        List<String> baseCcys = new ArrayList<>();
        for (Rate rate: rates) {
            if (!baseCcys.contains(rate.getCcy())) baseCcys.add(rate.getCcy());
            if (!baseCcys.contains(rate.getBaseCcy())) baseCcys.add(rate.getBaseCcy());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, baseCcys);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                baseCcy = item;

                List<String> ccys = new ArrayList<>();
                for (Rate rate: rates) {
                    if (!(rate.getCcy().equals(item) || rate.getBaseCcy().equals(item))) continue;

                    if (!ccys.contains(rate.getCcy()) && !rate.getCcy().equals(item))
                        ccys.add(rate.getCcy());
                    if (!ccys.contains(rate.getBaseCcy()) && !rate.getBaseCcy().equals(item))
                        ccys.add(rate.getBaseCcy());
                }

                adapter2.clear();
                adapter2.addAll(ccys);
                adapter2.notifyDataSetChanged();

                ccy = (String) spinner2.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setSelection(0);

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ccy = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner2.setSelection(0);

        toggleProgress(false);
    }

    @OnClick(R.id.calc_btn)
    public void calculate() {
        if (numField.length() == 0) return;

        float num = Float.parseFloat(numField.getText().toString());
        for (Rate rate : rates) {
            if (rate.getBaseCcy().equals(baseCcy) && rate.getCcy().equals(ccy))
                resultView.setText(String.format("%.2f", (num / rate.getSale())));

            if (rate.getBaseCcy().equals(ccy) && rate.getCcy().equals(baseCcy))
                resultView.setText(String.format("%.2f", num * rate.getSale()));
        }
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
