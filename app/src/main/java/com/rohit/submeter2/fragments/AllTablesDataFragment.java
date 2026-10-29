package com.rohit.submeter2.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.tabs.TabLayout;
import com.rohit.submeter2.adapters.TableDataAdapter;
import com.rohit.submeter2.databinding.FragmentAllTablesDataBinding;
import com.submeter.presentation.viewmodel.AllTablesDataViewModel;
import com.submeter.presentation.viewmodel.BaseViewModel.StateListener;

public class AllTablesDataFragment extends Fragment {
    private static final String TAG = "DbInspectorFragment";

    private FragmentAllTablesDataBinding binding;
    private AllTablesDataViewModel viewModel;
    private TableDataAdapter tableAdapter;
    private StateListener stateListener;

    private final String[] databaseTables = {
            "ROOMS", "TENANTS", "TENANCIES", "SUBMETERS", "SUBMETERS_ALLOCATION", "BILLS", "METER_READINGS"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAllTablesDataBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Setup RecyclerView
        binding.rvDatabaseTable.setLayoutManager(new LinearLayoutManager(getContext()));
        tableAdapter = new TableDataAdapter();
        binding.rvDatabaseTable.setAdapter(tableAdapter);

        // 2. Initialize ViewModel and state tracking listener
        viewModel = new AllTablesDataViewModel();
        stateListener = () -> {
            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(this::renderTableUIState);
            }
        };
        viewModel.addStateListener(stateListener);

        // 3. Populate TabLayout items programmatically
        for (String table : databaseTables) {
            binding.tableTabLayout.addTab(binding.tableTabLayout.newTab().setText(table));
        }

        // 4. Handle user tab updates
        binding.tableTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewModel.loadTableData(databaseTables[tab.getPosition()]);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // 5. Initial fetch trigger for the landing state
        viewModel.loadTableData(databaseTables[0]);
    }

    private void renderTableUIState() {
        // Handle database exceptions gracefully
        if (viewModel.getErrorMessage() != null) {
            Log.e(TAG, "Database Inspection Failure: " + viewModel.getErrorMessage());
            Toast.makeText(getContext(), viewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            return;
        }


        // Push clean tabular payload arrays down to the adapter layer when rendering finishes
        if (!viewModel.isLoading() && viewModel.isOperationSuccess()) {
            tableAdapter.updateData(viewModel.getCurrentHeaders(), viewModel.getCurrentRows());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // CRITICAL FIX: Safe reference removal stops active views leaking memory footprints
        if (viewModel != null && stateListener != null) {
            viewModel.removeStateListener(stateListener);
        }
        binding = null;
    }
}
