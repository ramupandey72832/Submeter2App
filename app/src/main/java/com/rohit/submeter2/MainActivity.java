package com.rohit.submeter2;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.application.android_ui_templete1.templates.nav_activity.bottom_nav_activity.BottomNavActivityConstant;
import com.application.baselibrary.ui.utils.NavigationUtils;
import com.application.baselibrary.ui.utils.ToastMessage;
import com.application.baselibrary.utils.GenericPermissionHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rohit.submeter2.fragments.BillFragment;
import com.rohit.submeter2.fragments.mpin.SetupMpinFragment;
import com.rohit.submeter2.utils.ConfigManager;
import com.submeter.service.ServiceManager;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private ToastMessage toastMessage;
    public GenericPermissionHelper permissionHelper;

    private final List<String> requiredPermissions = Arrays.asList(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_MEDIA_IMAGES
    );

    private int containerId;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton scanMeterFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        initCoreComponents();
        setupDatabase(); // Handles database & service configuration via ConfigManager
        setupUI();
        handleInitialNavigation();
    }

    private void initCoreComponents() {
        toastMessage = new ToastMessage(this);
        containerId = BottomNavActivityConstant.MAIN_CONTAINER;

        permissionHelper = new GenericPermissionHelper(this, requiredPermissions, new GenericPermissionHelper.OnPermissionsListener() {
            @Override
            public void onAllPermissionsGranted() {
                toastMessage.showSuccess("All Permissions are Granted");
            }
            @Override
            public void onPermissionsDenied(List<String> deniedPermissions) {
                String deniedPermsString = String.join(", ", deniedPermissions);
                toastMessage.showWarning("Permission Not Granted: " + deniedPermsString);
            }
        });
        permissionHelper.checkForPermissions();
    }

    private void setupDatabase() {
        try {
            String dbUrl = ConfigManager.getDbUrl(this);
            String driver = ConfigManager.getDbDriver(this);

            if (dbUrl == null || dbUrl.isEmpty()) {
                // Build absolute path for Android internal storage
                java.io.File dbFile = new java.io.File(getFilesDir(), "submeter_bill.db");
                String absoluteDbUrl = "jdbc:sqldroid:" + dbFile.getAbsolutePath();

                ServiceManager.getDatabaseSetup().initializeDb(absoluteDbUrl, null, null, "org.sqldroid.SQLDroidDriver");
            } else {
                ServiceManager.getDatabaseSetup().initializeDb(dbUrl, null, null, driver);
            }

            // Now initialize DAOs and Services after the absolute path connection is active
//            DaoManager.initialize();
            ServiceManager.initialize();

        } catch (Exception e) {
            Log.e(TAG, "Error initializing database: " + e.getMessage(), e);
            toastMessage.showError("MainActivity$Error While initializing database", e);
        }
    }

    private void setupUI() {
        setContentView(com.application.android_ui_templete1.R.layout.bottom_navigation_activity_main);

        bottomNavigationView = findViewById(com.application.android_ui_templete1.R.id.bottom_nav_activity_main_bottomNavigationView);
        scanMeterFab = findViewById(com.application.android_ui_templete1.R.id.bottom_nav_activity_main_btn_fab);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(com.application.android_ui_templete1.R.id.bottom_nav_activity_main_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        scanMeterFab.setOnClickListener(view -> {
            // TODO: Add scan action here
        });
    }

    private void handleInitialNavigation() {
        if (!ConfigManager.isMPinConfigured(this)) {
            SetupMpinFragment setupMpinFragment = new SetupMpinFragment();
            setupMpinFragment.addStateListener(activity ->
                    NavigationUtils.replaceFragment(activity, new DashboardFragment(), containerId)
            );
            NavigationUtils.replaceFragment(this, setupMpinFragment, containerId);
        } else {
            NavigationUtils.replaceFragment(this, new DashboardFragment(), containerId);
            setupBottomNavigation();
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.dashboard) {
                NavigationUtils.replaceFragment(this, new DashboardFragment(), containerId);
                return true;
            } else if (itemId == R.id.room) {
                // NavigationUtils.replaceFragment(this, new RoomsFragment(), containerId);
                return true;
            } else if (itemId == R.id.report) {
                NavigationUtils.replaceFragment(this, new ExportImportBillFragment(), containerId);
                return true;
            } else if (itemId == R.id.billing) {
                NavigationUtils.replaceFragment(this, new BillFragment(), containerId);
                return true;
            }
            return false;
        });
    }
}