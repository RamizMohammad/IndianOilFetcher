package com.indian.ramiz.indianoilfetrcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.indian.ramiz.indianoilfetrcher.apiendpoints.CallMaker;
import com.indian.ramiz.indianoilfetrcher.apiendpoints.PostResponse;
import com.indian.ramiz.indianoilfetrcher.apiendpoints.RetroClient;
import com.indian.ramiz.indianoilfetrcher.apiendpoints.SendBody;
import com.indian.ramiz.indianoilfetrcher.dbfiles.ViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Response;

public class HomeScreen extends AppCompatActivity {

    private static final String TAG = "HomeScreen";
    private static final String ACTION_OTP_RECEIVED = "com.android.SmsReceiver.OTP_RECEIVED";
    private BroadcastReceiver smsReceiver;
    private ViewModel viewModel;
    private FrameLayout changeButton;
    private String BaseUrl,otp,date,time,route;
    private TextView otpPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        viewModel.getUrlLiveData().observe(this, entry-> {
            BaseUrl = entry.getUrl();
            route = entry.getRoute();
        });

        changeButton = findViewById(R.id.changeUrl);
        otpPanel = findViewById(R.id.otp);

        changeButton.setOnClickListener(v -> {
            changeTheUrl();
            Log.d("Button Debug", "Button Clicked");
        });

        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null && ACTION_OTP_RECEIVED.equals(intent.getAction())) {
                    otp = intent.getStringExtra("otp");

                    // ✅ Get current date & time
                    Date now = new Date();  // <--- THIS WAS MISSING

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

                    date = dateFormat.format(now);
                    time = timeFormat.format(now);

                    // Set OTP on UI
                    otpPanel.setText(otp != null ? otp : "N/A");

                    // Send OTP to server
                    otpSender();
                }
            }
        };

        // Register for the custom action
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(smsReceiver, new IntentFilter(ACTION_OTP_RECEIVED), Context.RECEIVER_EXPORTED);
        }
    }

    private void otpSender() {
        Log.d("Test Message", "test");
        if (BaseUrl == null || BaseUrl.isEmpty()) {
            Toast.makeText(this, "Base URL is not set", Toast.LENGTH_SHORT).show();
            return;
        }

        if (otp == null) {
            Toast.makeText(this, "OTP is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare request body
        SendBody body = new SendBody(otp, date, time); // Create this model

        if (route == null || route.isEmpty()) {
            Toast.makeText(this, "Route is not set", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrofit setup and call
        CallMaker callMaker = RetroClient.getInstance(BaseUrl).create(CallMaker.class);
        retrofit2.Call<PostResponse> call = callMaker.sendOtp(route, body); // Dynamic route usage

        call.enqueue(new retrofit2.Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(HomeScreen.this, "OTP Sent Successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeScreen.this, "Failed to send OTP: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Toast.makeText(HomeScreen.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void changeTheUrl(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = LayoutInflater.from(this).inflate(R.layout.urldialog, null);
        builder.setView(view);

        EditText urlData = view.findViewById(R.id.url);
        EditText routeData = view.findViewById(R.id.route);
        FrameLayout button = view.findViewById(R.id.changeUrlD);
        TextView urlShow = view.findViewById(R.id.urlCurrent);

        AlertDialog dialog = builder.create();

        viewModel.getUrlLiveData().observe(this, entry -> {
            if(entry != null && entry.getUrl() != null){
                urlShow.setText(entry.getUrl() + "/" + entry.getRoute());
            }
            else{
                urlShow.setText("No Url Found");
            }
        });

        button.setOnClickListener(v -> {
            String data = urlData.getText().toString();
            String route = routeData.getText().toString();
            if(!data.isEmpty()){
                viewModel.savUrlDirect(data, route);
                dialog.dismiss();
            }
            else {
                Toast.makeText(this, "Empty Feild", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Do NOT unregister the receiver in onPause, so it keeps listening
        Log.d(TAG, "Receiver still active in background");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister receiver to avoid memory leaks
        try {
            unregisterReceiver(smsReceiver);
            Log.d(TAG, "Receiver unregistered");
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Receiver was not registered, skipping unregistration.");
        }
    }
}