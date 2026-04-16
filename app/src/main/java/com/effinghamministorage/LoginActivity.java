package com.effinghamministorage;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.effinghamministorage.network.ApiClient;
import com.effinghamministorage.network.AuthResponse;
import com.effinghamministorage.network.AuthenticateRequest;
import com.effinghamministorage.network.GetPinRequest;
import com.effinghamministorage.storage.TokenManager;
import com.effinghamministorage.util.PhoneNumberUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout phoneInputLayout;
    private TextInputEditText phoneEditText;
    private TextInputLayout pinInputLayout;
    private TextInputEditText pinEditText;
    private MaterialButton actionButton;
    private ProgressBar progressBar;
    private TextView errorText;

    private boolean pinSent = false;
    private boolean isFormatting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        phoneInputLayout = findViewById(R.id.phoneInputLayout);
        phoneEditText = findViewById(R.id.phoneEditText);
        pinInputLayout = findViewById(R.id.pinInputLayout);
        pinEditText = findViewById(R.id.pinEditText);
        actionButton = findViewById(R.id.actionButton);
        progressBar = findViewById(R.id.progressBar);
        errorText = findViewById(R.id.errorText);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        setupPhoneFormatting();

        actionButton.setOnClickListener(v -> {
            clearError();
            if (!pinSent) {
                requestPin();
            } else {
                authenticate();
            }
        });
    }

    private void setupPhoneFormatting() {
        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;
                isFormatting = true;

                String digits = PhoneNumberUtils.stripPhone(s.toString());
                String formatted = PhoneNumberUtils.formatUsPhone(digits);

                s.replace(0, s.length(), formatted);
                isFormatting = false;
            }
        });
    }

    private void requestPin() {
        String digits = PhoneNumberUtils.stripPhone(phoneEditText.getText().toString());
        if (!PhoneNumberUtils.isValidPhone(digits)) {
            showError(getString(R.string.error_invalid_phone));
            return;
        }

        setLoading(true);
        GetPinRequest request = new GetPinRequest(digits);
        ApiClient.getInstance().getPin(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    showPinInput();
                    Toast.makeText(LoginActivity.this, R.string.pin_sent_success, Toast.LENGTH_SHORT).show();
                } else {
                    showError(parseErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                setLoading(false);
                showError(getString(R.string.error_network));
            }
        });
    }

    private void authenticate() {
        String pin = pinEditText.getText() != null ? pinEditText.getText().toString().trim() : "";
        if (pin.isEmpty()) {
            showError(getString(R.string.error_empty_pin));
            return;
        }

        String digits = PhoneNumberUtils.stripPhone(phoneEditText.getText().toString());
        setLoading(true);
        AuthenticateRequest request = new AuthenticateRequest(digits, pin);
        ApiClient.getInstance().authenticate(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess() && response.body().getToken() != null) {
                    TokenManager.saveToken(LoginActivity.this, response.body().getToken());
                    Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    showError(parseErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                setLoading(false);
                showError(getString(R.string.error_network));
            }
        });
    }

    private void showPinInput() {
        pinSent = true;
        pinInputLayout.setVisibility(View.VISIBLE);
        actionButton.setText(R.string.login);
        pinEditText.requestFocus();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        actionButton.setEnabled(!loading);
        actionButton.setAlpha(loading ? 0.6f : 1.0f);
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
    }

    private void clearError() {
        errorText.setVisibility(View.GONE);
        errorText.setText("");
    }

    private String parseErrorMessage(Response<AuthResponse> response) {
        if (response.body() != null && response.body().getMessage() != null
                && !response.body().getMessage().isEmpty()) {
            return response.body().getMessage();
        }
        try {
            if (response.errorBody() != null) {
                String errorJson = response.errorBody().string();
                // Try parsing as our AuthResponse format first
                AuthResponse errorResponse = new Gson().fromJson(errorJson, AuthResponse.class);
                if (errorResponse != null && errorResponse.getMessage() != null
                        && !errorResponse.getMessage().isEmpty()) {
                    return errorResponse.getMessage();
                }
                // Fall back to Spring Boot's default error format {"error": "...", "status": 500}
                com.google.gson.JsonObject jsonObject = new Gson().fromJson(errorJson, com.google.gson.JsonObject.class);
                if (jsonObject != null && jsonObject.has("error")) {
                    return jsonObject.get("error").getAsString();
                }
            }
        } catch (Exception ignored) {}
        return getString(R.string.error_generic) + " (HTTP " + response.code() + ")";
    }
}
