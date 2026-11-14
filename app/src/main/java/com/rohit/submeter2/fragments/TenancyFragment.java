package com.rohit.submeter2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.rrallutilitylib.util.DateUtils;
import com.rohit.submeter2.databinding.FragmentTenancyBinding;
import com.rohit.submeter2.utils.UiDateUtils;
import com.submeter.commonlib.dto.TenancyDTO;
import com.submeter.presentation.viewmodel.TenancyViewModel;

import java.util.Calendar;

public class TenancyFragment extends Fragment {

    private TenancyViewModel viewModel;
    private FragmentTenancyBinding binding;

    private TenancyDTO currentSelectedTenancy = null;

    private final Calendar addStartDate = Calendar.getInstance();
    private final Calendar updateStartDate = Calendar.getInstance();

    // Use a Calendar object that can be null, or maintain a tracking flag for the update end date
    private Calendar updateEndDate = null;

    public TenancyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTenancyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(TenancyViewModel.class);

        initializeDatePickers();
        setupClickListeners();

        // --- Click Listeners (Using View Binding) ---
        binding.btnSaveTenancy.setOnClickListener(v -> {
            viewModel.saveTenancy(
                    binding.etAddRoomNumber.getText().toString(),
                    binding.etAddTenantAadhaar.getText().toString(),
                    UiDateUtils.formatDateForDb(addStartDate),
                    ""
            );
        });

        binding.btnSearchById.setOnClickListener(v ->
                viewModel.searchTenancyById(binding.etSearchById.getText().toString())
        );

        binding.btnUpdateTenancy.setOnClickListener(v -> {
            // Check if end date calendar is set, otherwise send empty string ""
            String endDateForDb = (updateEndDate != null)
                    ? UiDateUtils.formatDateForDb(updateEndDate)
                    : "";

            viewModel.updateTenancy(
                    currentSelectedTenancy,
                    binding.etEditRoomNumber.getText().toString(),
                    binding.etEditTenantAadhaar.getText().toString(),
                    UiDateUtils.formatDateForDb(updateStartDate),
                    endDateForDb
            );
        });

        // --- OBSERVE VIEWMODEL LIVEDATA ---
        viewModel.getTenancyLiveData().observe(getViewLifecycleOwner(), tenancy -> {
            currentSelectedTenancy = tenancy;
            if (tenancy != null) {
                // Parse Start Date safely
                if (tenancy.getStartDate() != null) {
                    Calendar parsedStartCalendar = UiDateUtils.parseDateToCalendar(
                            DateUtils.toDisplayFormat(tenancy.getStartDate())
                    );
                    if (parsedStartCalendar != null) {
                        updateStartDate.setTime(parsedStartCalendar.getTime());
                    }
                }

                // Parse End Date safely (Optional)
                if (tenancy.getEndDate() != null && !tenancy.getEndDate().isEmpty()) {
                    Calendar parsedEndCalendar = UiDateUtils.parseDateToCalendar(
                            DateUtils.toDisplayFormat(tenancy.getEndDate())
                    );
                    if (parsedEndCalendar != null) {
                        updateEndDate = parsedEndCalendar;
                        binding.tvUpdateEndDate.setText(UiDateUtils.formatDateForDisplay(updateEndDate));
                    } else {
                        updateEndDate = null;
                        binding.tvUpdateEndDate.setText("");
                    }
                } else {
                    updateEndDate = null;
                    binding.tvUpdateEndDate.setText("");
                }

                binding.etEditId.setText(String.valueOf(tenancy.getId()));
                binding.etEditRoomNumber.setText(String.valueOf(tenancy.getRoomNumber()));
                binding.etEditTenantAadhaar.setText(String.valueOf(tenancy.getAadhaarNumber()));
                binding.tvUpdateStartDate.setText(UiDateUtils.formatDateForDisplay(updateStartDate));

                binding.etEditId.setEnabled(false); // ID remains uneditable
                binding.etEditRoomNumber.setEnabled(true);
                binding.etEditTenantAadhaar.setEnabled(true);
                binding.btnUpdateStartDate.setEnabled(true);
                binding.btnUpdateEndDate.setEnabled(true);
                binding.btnUpdateTenancy.setEnabled(true);
            } else {
                disableEditForm();
            }
        });

        viewModel.getToastMessageLiveData().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                if (message.contains("allocated successfully")) {
                    binding.etAddRoomNumber.setText("");
                    binding.etAddTenantAadhaar.setText("");
                    binding.tvAddStartDate.setText("");
                } else if (message.contains("updated successfully")) {
                    binding.etSearchById.setText("");
                }
            }
        });
    }

    private void initializeDatePickers() {
        binding.tvAddStartDate.setText(UiDateUtils.formatDateForDisplay(addStartDate));
    }

    private void setupClickListeners() {
        // Add Section Date Picker
        binding.btnAddStartDate.setOnClickListener(v -> UiDateUtils.showDatePicker(getContext(), addStartDate, (view1, year, month, dayOfMonth) -> {
            addStartDate.set(year, month, dayOfMonth);
            binding.tvAddStartDate.setText(UiDateUtils.formatDateForDisplay(addStartDate));
        }));

        // Update Section Start Date Picker
        binding.btnUpdateStartDate.setOnClickListener(v -> UiDateUtils.showDatePicker(getContext(), updateStartDate, (view1, year, month, dayOfMonth) -> {
            updateStartDate.set(year, month, dayOfMonth);
            binding.tvUpdateStartDate.setText(UiDateUtils.formatDateForDisplay(updateStartDate));
        }));

        // Update Section End Date Picker (Allows setting or leaving blank)
        binding.btnUpdateEndDate.setOnClickListener(v -> {
            // If tenancyUpdateEndCalendar is currently null, fallback to current date for the picker dialog
            Calendar calendarToUse = (updateEndDate != null) ? updateEndDate : Calendar.getInstance();

            UiDateUtils.showDatePicker(getContext(), calendarToUse, (view1, year, month, dayOfMonth) -> {
                if (updateEndDate == null) {
                    updateEndDate = Calendar.getInstance();
                }
                updateEndDate.set(year, month, dayOfMonth);
                binding.tvUpdateEndDate.setText(UiDateUtils.formatDateForDisplay(updateEndDate));
            });
        });
    }

    private void disableEditForm() {
        binding.etEditId.setText("");
        binding.etEditRoomNumber.setText("");
        binding.etEditTenantAadhaar.setText("");
        binding.tvUpdateStartDate.setText("");
        binding.tvUpdateEndDate.setText("");
        updateEndDate = null; // Clear out the optional end date reference

        binding.etEditId.setEnabled(false);
        binding.etEditRoomNumber.setEnabled(false);
        binding.etEditTenantAadhaar.setEnabled(false);
        binding.btnUpdateStartDate.setEnabled(false);
        binding.btnUpdateEndDate.setEnabled(false);
        binding.btnUpdateTenancy.setEnabled(false);
        currentSelectedTenancy = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }
}