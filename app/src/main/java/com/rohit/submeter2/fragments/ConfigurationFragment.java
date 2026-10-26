package com.rohit.submeter2.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rohit.submeter2.adapters.SimpleListAdapter;
import com.rohit.submeter2.databinding.FragmentConfigurationBinding;
import com.rohit.submeter2.utils.ConfigManager;
import com.submeter.service.ServiceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationFragment extends Fragment {

    private FragmentConfigurationBinding binding;

    public ConfigurationFragment() {
        // Required empty constructor
    }

    public static ConfigurationFragment newInstance() {
        return new ConfigurationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentConfigurationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadSavedPreferences();
        showExistingDbs();

        binding.btnSaveConfig.setOnClickListener(v -> saveConfig());
    }

    private void loadSavedPreferences() {
        Context context = getContext();
        if (context == null) return;

        binding.etDbUrl.setText(ConfigManager.getDbUrl(context));
        binding.etDbDriver.setText(ConfigManager.getDbDriver(context));
        binding.etSecretKey.setText(ConfigManager.getSecretKey(context));
    }

    private void saveConfig() {
        String url = binding.etDbUrl.getText().toString().trim();
        String driver = binding.etDbDriver.getText().toString().trim();
        String secretKey = binding.etSecretKey.getText().toString().trim();

        if (url.isEmpty() || driver.isEmpty() || secretKey.isEmpty()) {
            Toast.makeText(getContext(), "All parameters are mandatory requirements.", Toast.LENGTH_SHORT).show();
            return;
        }

        Context context = requireContext();
        ConfigManager.saveConfig(context, url, driver, secretKey);

        // 2. Initialize database engine with error handling
        try {
            ServiceManager.getDatabaseSetup().initializeDb(url, null, null, driver);
            Toast.makeText(context, "Configurations saved. Connection engine re-initialized.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "Error initializing database: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void showExistingDbs() {
        Context context = getContext();
        if (context == null) return;

        File directory = context.getFilesDir();
        File[] dbFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".db"));

        List<String> dbNamesList = new ArrayList<>();
        if (dbFiles != null) {
            for (File file : dbFiles) {
                dbNamesList.add(file.getName());
            }
        }

        if (!dbNamesList.isEmpty()) {
            binding.rvListOfExistingDBsName.setLayoutManager(new LinearLayoutManager(context));
            binding.rvListOfExistingDBsName.setAdapter(new SimpleListAdapter(dbNamesList));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}