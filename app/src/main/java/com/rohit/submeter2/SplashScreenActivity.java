package com.rohit.submeter2;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.application.android_ui_templete1.templates.intro.TutorialAdapter;
import com.application.android_ui_templete1.templates.intro.TutorialData;
import com.rohit.submeter2.utils.ConfigManager;
import com.smarteist.autoimageslider.SliderView;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.service.DatabaseMigrationService;
import com.submeter.service.ServiceManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";

    String url1 = "https://raw.githubusercontent.com/DevFrogora/SubmeterBilling3LayerProject/refs/heads/main/Hosting_Images/guide_01.png";
    String url2 = "https://raw.githubusercontent.com/DevFrogora/SubmeterBilling3LayerProject/refs/heads/main/Hosting_Images/guide_02.png";
    String url3 = "https://raw.githubusercontent.com/DevFrogora/SubmeterBilling3LayerProject/refs/heads/main/Hosting_Images/guide_03.png";
    String url4 = "https://raw.githubusercontent.com/DevFrogora/SubmeterBilling3LayerProject/refs/heads/main/Hosting_Images/guide_04.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Check metadata or system states asynchronously in the background
        checkInitializationStatusAsync();

        boolean isTutorialSeen = ConfigManager.isTutorialSeen(this);
        if (isTutorialSeen) {
            Intent targetIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(targetIntent);
            finish();
            return;
        }

        // Setup tutorial image slider view
        ArrayList<TutorialData> tutorialDataArrayList = new ArrayList<>();
        SliderView sliderView = findViewById(R.id.slider);

        tutorialDataArrayList.add(new TutorialData(url1));
        tutorialDataArrayList.add(new TutorialData(url2));
        tutorialDataArrayList.add(new TutorialData(url3));
        tutorialDataArrayList.add(new TutorialData(url4));

        TutorialAdapter adapter = new TutorialAdapter(this, tutorialDataArrayList);
        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        sliderView.setSliderAdapter(adapter);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();

        findViewById(R.id.btnFinish).setOnClickListener(view -> {
            ConfigManager.setTutorialSeen(this, true);
            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Checks initialization metadata or execution flags asynchronously.
     */
    private void checkInitializationStatusAsync() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                DatabaseMigrationService migrationService = ServiceManager.getDatabaseMigrationService();

                if (migrationService == null) {
                    Log.w(TAG, "DatabaseMigrationService is not initialized yet in ServiceManager.");
                    return;
                }

                OperationResult<Void> result = migrationService.executeOneTimeInitializations();

                if (result != null && result.isSuccess()) {
                    Log.i(TAG, "One-time initialization checked/executed successfully: " + result.getMessage());
                } else {
                    Log.e(TAG, "One-time initialization failed: " + (result != null ? result.getMessage() : "Unknown error"));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error executing one-time initializations: " + e.getMessage(), e);
            }
        });
    }
}