package com.effinghamministorage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.effinghamministorage.network.ApiClient;
import com.effinghamministorage.network.LockboxPinIssuanceRequest;
import com.effinghamministorage.network.LockboxPinIssuanceResponse;
import com.effinghamministorage.storage.TokenManager;
import com.google.android.material.button.MaterialButton;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import co.igloo.access.sdk.IglooPlugin;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.BuildersKt;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnitAccessActivity extends AppCompatActivity {

    private static final String TAG = "UnitAccessActivity";
    private static final String KEYBOX_DEVICE_NAME = "IGK345975e55";
    private static final String KEYBOX_GUEST_KEY =
            "3ej4odPUJK888Xc9+1cMLsN+kKkGxRrDMDe9QfI9VWE9be3HtxZrQvIYLddu5s97"
            + "hC5UmlJBc0kYyd+7BaZmMP+zhZ3Clvv+FTH/havoB8amj6PIL+XP57w2Uy5WkGvP"
            + "3H3Ib1F2y1m7UDvdjnrI5qmGYDwd0veNFGXv0ChPFJJlnLRN38NqQJpYEIkUiYN"
            + "Vd0oSO0fpZq6ix3wT1VY4j82CkvDubHQ7C6HQ8ZOLFe57ds5wyfjT8M/1p5QD4x"
            + "HXo/BqA9PaLV/ho+gfJE0IB3Kd95s7BwyNsAvReTlwAej4odPUJK888Xc9+50+l"
            + "csvJBronVhh8TeAOdQ+lkSE";

    private MaterialButton openKeyboxButton;
    private MaterialButton sendPinButton;
    private ProgressBar progressBar;
    private TextView statusText;

    private final ActivityResultLauncher<String[]> bluetoothPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                boolean allGranted = !result.containsValue(false);
                if (allGranted) {
                    openKeybox();
                } else {
                    Toast.makeText(this, R.string.bluetooth_permission_required, Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_unit_access);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.unitAccessRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        TextView unitNumberText = findViewById(R.id.unitNumberText);
        TextView unitSizeText = findViewById(R.id.unitSizeText);
        openKeyboxButton = findViewById(R.id.openKeyboxButton);
        sendPinButton = findViewById(R.id.sendPinButton);
        progressBar = findViewById(R.id.accessProgress);
        statusText = findViewById(R.id.statusText);

        String unitSize = getIntent().getStringExtra(UnitSelectionActivity.EXTRA_UNIT_SIZE);
        String unitNumber = getIntent().getStringExtra(UnitSelectionActivity.EXTRA_UNIT_NUMBER);

        unitNumberText.setText(unitNumber != null ? unitNumber : "---");
        unitSizeText.setText(unitSize != null ? unitSize.replace("x", " x ") : "---");

        openKeyboxButton.setOnClickListener(v -> requestBluetoothPermissionsAndOpen());
        sendPinButton.setOnClickListener(v -> sendAccessPin());
    }

    private void requestBluetoothPermissionsAndOpen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            boolean hasScan = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
            boolean hasConnect = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
            if (hasScan && hasConnect) {
                openKeybox();
            } else {
                bluetoothPermissionLauncher.launch(new String[]{
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                });
            }
        } else {
            boolean hasLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            if (hasLocation) {
                openKeybox();
            } else {
                bluetoothPermissionLauncher.launch(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                });
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void openKeybox() {
        openKeyboxButton.setEnabled(false);
        openKeyboxButton.setText(R.string.keybox_opening);
        statusText.setVisibility(View.GONE);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                IglooPlugin plugin = EmsApplication.getIglooPlugin();
                BuildersKt.runBlocking(
                        EmptyCoroutineContext.INSTANCE,
                        (scope, continuation) -> plugin.unlock(
                                KEYBOX_DEVICE_NAME,
                                KEYBOX_GUEST_KEY,
                                null,
                                null,
                                continuation
                        )
                );
                runOnUiThread(() -> {
                    openKeyboxButton.setEnabled(true);
                    openKeyboxButton.setText(R.string.open_keybox);
                    showStatus(getString(R.string.keybox_opened), false);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    openKeyboxButton.setEnabled(true);
                    openKeyboxButton.setText(R.string.open_keybox);
                    showStatus(getString(R.string.keybox_failed) + ": " + e.getMessage(), true);
                });
            }
        });
        executor.shutdown();
    }

    private void sendAccessPin() {
        String phone = "+19126581701";

        sendPinButton.setEnabled(false);
        sendPinButton.setText(R.string.pin_sending);
        progressBar.setVisibility(View.VISIBLE);
        statusText.setVisibility(View.GONE);

        LockboxPinIssuanceRequest request = new LockboxPinIssuanceRequest(phone);
        Log.d(TAG, "Sending PIN issuance request for phone: " + phone);
        Log.d(TAG, "API Key present: " + (BuildConfig.LOCKBOX_API_KEY != null && !BuildConfig.LOCKBOX_API_KEY.isEmpty()));

        ApiClient.getInstance()
                .issueLockboxPin(BuildConfig.LOCKBOX_API_KEY, request)
                .enqueue(new Callback<LockboxPinIssuanceResponse>() {
                    @Override
                    public void onResponse(Call<LockboxPinIssuanceResponse> call,
                                           Response<LockboxPinIssuanceResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        sendPinButton.setEnabled(true);
                        sendPinButton.setText(R.string.send_access_pin);

                        Log.d(TAG, "PIN issuance response code: " + response.code());
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "PIN issuance success, issuanceId: " + response.body().getIssuanceId());
                            showStatus(getString(R.string.pin_sent_sms), false);
                        } else {
                            String errorBody = "";
                            try {
                                if (response.errorBody() != null) {
                                    errorBody = response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Failed to read error body", e);
                            }
                            Log.e(TAG, "PIN issuance failed - HTTP " + response.code() + ": " + errorBody);
                            showStatus(getString(R.string.pin_send_failed), true);
                        }
                    }

                    @Override
                    public void onFailure(Call<LockboxPinIssuanceResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        sendPinButton.setEnabled(true);
                        sendPinButton.setText(R.string.send_access_pin);
                        Log.e(TAG, "PIN issuance network failure", t);
                        showStatus(getString(R.string.error_network), true);
                    }
                });
    }

    private void showStatus(String message, boolean isError) {
        statusText.setText(message);
        statusText.setTextColor(getColor(isError ? android.R.color.holo_red_light : android.R.color.white));
        statusText.setVisibility(View.VISIBLE);
    }
}
