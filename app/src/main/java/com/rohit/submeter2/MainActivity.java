package com.rohit.submeter2;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import com.application.android_ui_templete1.templates.nav_activity.bottom_nav_activity.BottomNavActivityConstant;
import com.application.baselibrary.ui.utils.NavigationUtils;
import com.application.baselibrary.ui.utils.ToastMessage;
import com.application.baselibrary.utils.GenericPermissionHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rohit.submeter2.utils.ConfigManager;
import com.rohit.submeter2.fragments.mpin.SetupMpinFragment;
import com.submeter.service.ServiceManager;

import java.util.Arrays;
import java.util.List;
public class MainActivity extends AppCompatActivity {

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
        ServiceManager.initialize();
        setupDatabase();
        setupUI();
        handleInitialNavigation();
    }

    private void initCoreComponents() {
        toastMessage = new ToastMessage(this);
        containerId = BottomNavActivityConstant.MAIN_CONTAINER;

        // Permissions Handling
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
            if(dbUrl.isEmpty()){
                ServiceManager.getDatabaseSetup().initializeWithDefault("Android");
            }else{
                if(dbUrl == null) {
                    System.out.println("NULLLLLLLLLLLLLL");
                }else {
                    ServiceManager.getDatabaseSetup().initializeDb(dbUrl, null, null, driver);
                }
            }
        } catch (Exception e) {
            toastMessage.showError("MainActivity$Error While initializing database", e);
        }
    }

    private void setupUI() {
        setContentView(com.application.android_ui_templete1.R.layout.bottom_navigation_activity_main);

        bottomNavigationView = findViewById(com.application.android_ui_templete1.R.id.bottom_nav_activity_main_bottomNavigationView);
        scanMeterFab = findViewById(com.application.android_ui_templete1.R.id.bottom_nav_activity_main_btn_fab);

        // Edge-to-Edge Window Insets setup
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(com.application.android_ui_templete1.R.id.bottom_nav_activity_main_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // FAB Action
        scanMeterFab.setOnClickListener(view -> {
            // NavigationUtils.replaceFragment(this, new QrScanFragment(), containerId);
        });
    }

    private void handleInitialNavigation() {
        if (!ConfigManager.isMPinConfigured(this)) {
            // Fix: Use the same instance where the listener is registered
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
                // NavigationUtils.replaceFragment(this, new ReportsFragment(), containerId);
                return true;
            } else if (itemId == R.id.billing) {
                // NavigationUtils.replaceFragment(this, new BillsFragment(), containerId);
                return true;
            }
            return false;
        });
    }
}
//TODO MAKE THIS CLASS MORE SIMPLE AND SPLIT IF NEEDED