package com.effinghamministorage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.Random;

public class UnitSelectionActivity extends AppCompatActivity {

    public static final String EXTRA_UNIT_SIZE = "unit_size";
    public static final String EXTRA_UNIT_NUMBER = "unit_number";

    private String selectedSize = null;
    private MaterialButton confirmButton;
    private MaterialCardView card5x5, card10x10, card10x15, card10x20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_unit_selection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.unitSelectionRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        card5x5 = findViewById(R.id.card5x5);
        card10x10 = findViewById(R.id.card10x10);
        card10x15 = findViewById(R.id.card10x15);
        card10x20 = findViewById(R.id.card10x20);
        confirmButton = findViewById(R.id.confirmButton);

        card5x5.setOnClickListener(v -> selectSize(card5x5, "5x5"));
        card10x10.setOnClickListener(v -> selectSize(card10x10, "10x10"));
        card10x15.setOnClickListener(v -> selectSize(card10x15, "10x15"));
        card10x20.setOnClickListener(v -> selectSize(card10x20, "10x20"));

        confirmButton.setOnClickListener(v -> {
            String unitNumber = "IS-" + (100 + new Random().nextInt(900));
            Intent intent = new Intent(this, UnitAccessActivity.class);
            intent.putExtra(EXTRA_UNIT_SIZE, selectedSize);
            intent.putExtra(EXTRA_UNIT_NUMBER, unitNumber);
            startActivity(intent);
        });
    }

    private void selectSize(MaterialCardView selected, String size) {
        int accentColor = getColor(R.color.ems_accent);

        card5x5.setStrokeWidth(0);
        card10x10.setStrokeWidth(0);
        card10x15.setStrokeWidth(0);
        card10x20.setStrokeWidth(0);

        selected.setStrokeWidth(4);
        selected.setStrokeColor(accentColor);
        selectedSize = size;

        confirmButton.setEnabled(true);
        confirmButton.setAlpha(1.0f);
    }
}
