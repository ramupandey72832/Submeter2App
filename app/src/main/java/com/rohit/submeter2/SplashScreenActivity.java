package com.rohit.submeter2;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.view.View;

import com.application.android_ui_templete1.templates.intro.TutorialAdapter;
import com.application.android_ui_templete1.templates.intro.TutorialData;
import com.rohit.submeter2.utils.ConfigManager;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class SplashScreenActivity extends AppCompatActivity {

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

        boolean isTutorialSeen = ConfigManager.isTutorialSeen(this);
        Intent targetIntent;
        if (isTutorialSeen) {
            // Force Setup: If MPIN doesn't exist, launch MainActivity where the Setup fragment loads
            targetIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(targetIntent);
            finish();
        }

        // we are creating array list for storing our image urls.
        ArrayList<TutorialData> tutorialDataArrayList = new ArrayList<>();

        // initializing the slider view.
        SliderView sliderView = findViewById(R.id.slider);

        // adding the urls inside array list
        tutorialDataArrayList.add(new TutorialData(url1));
        tutorialDataArrayList.add(new TutorialData(url2));
        tutorialDataArrayList.add(new TutorialData(url3));
        tutorialDataArrayList.add(new TutorialData(url4));

        // passing this array list inside our adapter class.
        TutorialAdapter adapter = new TutorialAdapter(this, tutorialDataArrayList);
        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        sliderView.setSliderAdapter(adapter);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);

        // to start autocycle below method is used.
        sliderView.startAutoCycle();

        findViewById(R.id.btnFinish).setOnClickListener(view -> {
            ConfigManager.setTutorialSeen(this,true);
            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}