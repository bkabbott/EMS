package com.effinghamministorage;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.effinghamministorage.storage.TokenManager;
import com.google.android.material.button.MaterialButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

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

        if (TokenManager.isLoggedIn(this)) {
            Intent dashboardIntent = new Intent(this, DashboardActivity.class);
            startActivity(dashboardIntent);
            finish();
            return;
        }

        animateSplash();

        MaterialButton loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void animateSplash() {
        View topAccent = findViewById(R.id.topAccent);
        ImageView logo = findViewById(R.id.logoImage);
        TextView businessName = findViewById(R.id.businessName);
        TextView tagline = findViewById(R.id.tagline);
        View divider = findViewById(R.id.divider);
        LinearLayout servicesRow = findViewById(R.id.servicesRow);
        LinearLayout contactSection = findViewById(R.id.contactSection);
        MaterialButton loginButton = findViewById(R.id.loginButton);

        // Set initial states - invisible and slightly offset
        topAccent.setAlpha(0f);
        topAccent.setScaleX(0f);

        logo.setAlpha(0f);
        logo.setScaleX(0.8f);
        logo.setScaleY(0.8f);

        businessName.setAlpha(0f);
        businessName.setTranslationY(20f);

        tagline.setAlpha(0f);
        tagline.setTranslationY(15f);

        divider.setAlpha(0f);
        divider.setScaleX(0f);

        servicesRow.setAlpha(0f);
        servicesRow.setTranslationY(15f);

        contactSection.setAlpha(0f);
        contactSection.setTranslationY(30f);

        loginButton.setAlpha(0f);
        loginButton.setScaleX(0.8f);
        loginButton.setScaleY(0.8f);

        // Animate top accent line
        AnimatorSet topAccentAnim = new AnimatorSet();
        topAccentAnim.playTogether(
                ObjectAnimator.ofFloat(topAccent, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(topAccent, "scaleX", 0f, 1f)
        );
        topAccentAnim.setDuration(500);
        topAccentAnim.setStartDelay(300);
        topAccentAnim.setInterpolator(new DecelerateInterpolator());

        // Animate logo
        AnimatorSet logoAnim = new AnimatorSet();
        logoAnim.playTogether(
                ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(logo, "scaleX", 0.8f, 1f),
                ObjectAnimator.ofFloat(logo, "scaleY", 0.8f, 1f)
        );
        logoAnim.setDuration(600);
        logoAnim.setStartDelay(500);
        logoAnim.setInterpolator(new DecelerateInterpolator());

        // Animate business name
        AnimatorSet nameAnim = new AnimatorSet();
        nameAnim.playTogether(
                ObjectAnimator.ofFloat(businessName, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(businessName, "translationY", 20f, 0f)
        );
        nameAnim.setDuration(500);
        nameAnim.setStartDelay(900);
        nameAnim.setInterpolator(new DecelerateInterpolator());

        // Animate tagline
        AnimatorSet taglineAnim = new AnimatorSet();
        taglineAnim.playTogether(
                ObjectAnimator.ofFloat(tagline, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(tagline, "translationY", 15f, 0f)
        );
        taglineAnim.setDuration(400);
        taglineAnim.setStartDelay(1100);
        taglineAnim.setInterpolator(new DecelerateInterpolator());

        // Animate divider
        AnimatorSet dividerAnim = new AnimatorSet();
        dividerAnim.playTogether(
                ObjectAnimator.ofFloat(divider, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(divider, "scaleX", 0f, 1f)
        );
        dividerAnim.setDuration(400);
        dividerAnim.setStartDelay(1300);
        dividerAnim.setInterpolator(new DecelerateInterpolator());

        // Animate services row
        AnimatorSet servicesAnim = new AnimatorSet();
        servicesAnim.playTogether(
                ObjectAnimator.ofFloat(servicesRow, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(servicesRow, "translationY", 15f, 0f)
        );
        servicesAnim.setDuration(500);
        servicesAnim.setStartDelay(1500);
        servicesAnim.setInterpolator(new DecelerateInterpolator());

        // Animate contact section
        AnimatorSet contactAnim = new AnimatorSet();
        contactAnim.playTogether(
                ObjectAnimator.ofFloat(contactSection, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(contactSection, "translationY", 30f, 0f)
        );
        contactAnim.setDuration(600);
        contactAnim.setStartDelay(1700);
        contactAnim.setInterpolator(new DecelerateInterpolator());

        // Animate login button - loads last
        AnimatorSet loginAnim = new AnimatorSet();
        loginAnim.playTogether(
                ObjectAnimator.ofFloat(loginButton, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(loginButton, "scaleX", 0.8f, 1f),
                ObjectAnimator.ofFloat(loginButton, "scaleY", 0.8f, 1f)
        );
        loginAnim.setDuration(500);
        loginAnim.setStartDelay(2100);
        loginAnim.setInterpolator(new DecelerateInterpolator());

        // Play all animations
        AnimatorSet fullAnimation = new AnimatorSet();
        fullAnimation.playTogether(
                topAccentAnim, logoAnim, nameAnim, taglineAnim,
                dividerAnim, servicesAnim, contactAnim, loginAnim
        );
        fullAnimation.start();
    }
}
