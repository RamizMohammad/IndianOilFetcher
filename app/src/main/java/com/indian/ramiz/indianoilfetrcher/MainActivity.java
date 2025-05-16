package com.indian.ramiz.indianoilfetrcher;

import android.Manifest;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.indian.ramiz.indianoilfetrcher.apiendpoints.CallMaker;
import com.indian.ramiz.indianoilfetrcher.apiendpoints.PingResponse;
import com.indian.ramiz.indianoilfetrcher.apiendpoints.RetroClient;
import com.indian.ramiz.indianoilfetrcher.databinding.ActivityMainBinding;
import com.indian.ramiz.indianoilfetrcher.dbfiles.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private FrameLayout mainLayout;
    private ImageView bannerImage;
    private FrameLayout continueButton, permissionButton, testButton;
    private TextView bannerText, footer, ping;
    private ViewModel viewModel;
    private String baseUrl;
    private CallMaker callMaker;
    private static final int Code = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Data binding setup
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        binding.setDatabind(viewModel);
        binding.setLifecycleOwner(this);

        // View references
        mainLayout = findViewById(R.id.main);
        bannerImage = findViewById(R.id.bannerImage);
        bannerText = findViewById(R.id.bannerText);
        continueButton = findViewById(R.id.continueButton);
        permissionButton = findViewById(R.id.permissionButton);
        footer = findViewById(R.id.footer);
        testButton = findViewById(R.id.testButton);
        ping = findViewById(R.id.ping);

        // Observe URL from database
        viewModel.getUrlLiveData().observe(this, entry -> {
            if (entry != null) {
                viewModel.uriInput.setValue(entry.getUrl());
                baseUrl = entry.getUrl();
            }
        });

        // Test Button Click
        testButton.setOnClickListener(v -> {
            if (baseUrl == null || baseUrl.isEmpty()) {
                Toast.makeText(MainActivity.this, "Base URL is not set", Toast.LENGTH_SHORT).show();
                return;
            }

            callMaker = RetroClient.getInstance(baseUrl).create(CallMaker.class);
            Call<PingResponse> call = callMaker.ping();

            call.enqueue(new Callback<PingResponse>() {
                @Override
                public void onResponse(Call<PingResponse> call, Response<PingResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String message = response.body().getMessage();
                        ping.setText((message != null ? message : "Unknown") + "ms");
                    } else {
                        Toast.makeText(MainActivity.this, "Ping failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PingResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Ping error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Permission button
        permissionButton.setOnClickListener(v ->
                PermissionHelper.RequestPermission(this, Code)
        );

        // Handle keyboard UI changes
        handleKeyboardVisibility();
    }

    @Override
    protected void onStart() {
        super.onStart();
        PermissionHelper.CheckPermission(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.handlePermission(this, requestCode, permissions, grantResults);
    }

    // Handle keyboard visibility to toggle UI elements
    private void handleKeyboardVisibility() {
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            boolean isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime());
            int visibility = isKeyboardVisible ? android.view.View.GONE : android.view.View.VISIBLE;

            bannerImage.setVisibility(visibility);
            bannerText.setVisibility(visibility);
            continueButton.setVisibility(visibility);
            permissionButton.setVisibility(visibility);
            footer.setVisibility(visibility);

            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;
        });
    }
}
