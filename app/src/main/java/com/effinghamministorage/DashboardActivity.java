package com.effinghamministorage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
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
import com.effinghamministorage.network.RentalCategoryCount;
import com.effinghamministorage.network.RentalSummaryResponse;
import com.effinghamministorage.storage.TokenManager;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import co.igloo.access.sdk.IglooPlugin;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.BuildersKt;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private static final String KEYBOX_DEVICE_NAME = "IGK345975e55";
    private static final String KEYBOX_GUEST_KEY =
            "3ej4odPUJK888Xc9+1cMLsN+kKkGxRrDMDe9QfI9VWE9be3HtxZrQvIYLddu5s97"
            + "hC5UmlJBc0kYyd+7BaZmMP+zhZ3Clvv+FTH/havoB8amj6PIL+XP57w2Uy5WkGvP"
            + "3H3Ib1F2y1m7UDvdjnrI5qmGYDwd0veNFGXv0ChPFJJlnLRN38NqQJpYEIkUiYN"
            + "Vd0oSO0fpZq6ix3wT1VY4j82CkvDubHQ7C6HQ8ZOLFe57ds5wyfjT8M/1p5QD4x"
            + "HXo/BqA9PaLV/ho+gfJE0IB3Kd95s7BwyNsAvReTlwAej4odPUJK888Xc9+50+l"
            + "csvJBronVhh8TeAOdQ+lkSE";

    private TextView countIndoorStorage;
    private TextView countCamperBoat;
    private TextView countRv;
    private ProgressBar progressBar;
    private TextView errorText;
    private MaterialButton openKeyboxButton;

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
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashboardRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        countIndoorStorage = findViewById(R.id.countIndoorStorage);
        countCamperBoat = findViewById(R.id.countCamperBoat);
        countRv = findViewById(R.id.countRv);
        progressBar = findViewById(R.id.dashboardProgress);
        errorText = findViewById(R.id.dashboardError);
        openKeyboxButton = findViewById(R.id.openKeyboxButton);

        openKeyboxButton.setOnClickListener(v -> requestBluetoothPermissionsAndOpen());

        MaterialButton rentUnitButton = findViewById(R.id.rentUnitButton);
        rentUnitButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, UnitSelectionActivity.class);
            startActivity(intent);
        });

        MaterialButton logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> logout());

        loadRentalSummary();
    }

    private void requestBluetoothPermissionsAndOpen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+: need BLUETOOTH_SCAN and BLUETOOTH_CONNECT
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
            // Android 11 and below: need ACCESS_FINE_LOCATION
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
                    Toast.makeText(this, R.string.keybox_opened, Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    openKeyboxButton.setEnabled(true);
                    openKeyboxButton.setText(R.string.open_keybox);
                    Toast.makeText(this,
                            getString(R.string.keybox_failed) + ": " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
        });
        executor.shutdown();
    }

    private void loadRentalSummary() {
        String token = TokenManager.getToken(this);
        if (token == null) {
            redirectToLogin(getString(R.string.error_session_expired));
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.GONE);

        ApiClient.getInstance().getRentalSummary("Bearer " + token).enqueue(new Callback<RentalSummaryResponse>() {
            @Override
            public void onResponse(Call<RentalSummaryResponse> call, Response<RentalSummaryResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    displayRentalCounts(response.body().getCategories());
                } else if (response.code() == 401) {
                    TokenManager.clearToken(DashboardActivity.this);
                    redirectToLogin(getString(R.string.error_session_expired));
                } else {
                    showError(getString(R.string.error_loading_rentals));
                }
            }

            @Override
            public void onFailure(Call<RentalSummaryResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError(getString(R.string.error_network));
            }
        });
    }

    private void displayRentalCounts(List<RentalCategoryCount> categories) {
        countIndoorStorage.setText("0");
        countCamperBoat.setText("0");
        countRv.setText("0");

        if (categories == null) return;

        for (RentalCategoryCount category : categories) {
            if (category.getCategory() == null) continue;
            switch (category.getCategory()) {
                case "INDOOR_STORAGE":
                    countIndoorStorage.setText(String.valueOf(category.getCount()));
                    break;
                case "CAMPER_BOAT":
                    countCamperBoat.setText(String.valueOf(category.getCount()));
                    break;
                case "RV":
                    countRv.setText(String.valueOf(category.getCount()));
                    break;
            }
        }
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
    }

    private void redirectToLogin(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void logout() {
        TokenManager.clearToken(this);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
