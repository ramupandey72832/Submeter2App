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
import com.rohit.submeter2.databinding.FragmentSubmeterAllocationBinding;
import com.rohit.submeter2.utils.UiDateUtils;
import com.submeter.commonlib.dto.SubmeterAllocationDTO;
import com.submeter.presentation.viewmodel.SubmeterAllocationViewModel;

import java.util.Calendar;

public class SubmeterAllocationFragment extends Fragment {

    private SubmeterAllocationViewModel viewModel;

    // View Binding variable
    private FragmentSubmeterAllocationBinding binding;

    private SubmeterAllocationDTO currentSelectedAllocation = null;

    // Calendar instances for dates
    private final Calendar addStartDate = Calendar.getInstance();
    private final Calendar updateStartDate = Calendar.getInstance();
    private Calendar updateEndDate = null; // Can be null if left blank

    public SubmeterAllocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout using View Binding
        binding = FragmentSubmeterAllocationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(SubmeterAllocationViewModel.class);

        // 1. Set initial date text on UI
        initializeDatePickers();

        // 2. Setup click listeners for date pickers
        setupClickListeners();

        // --- Click Listeners ---
        binding.btnSaveAllocation.setOnClickListener(v ->
                viewModel.saveAllocation(
                        binding.etAddMeterSerialNumber.getText().toString(),
                        binding.etAddRoomNumber.getText().toString(),
                        UiDateUtils.formatDateForDb(addStartDate),
                        "" // Submeter allocations start active, so end date is blank
                )
        );

        binding.btnSearchById.setOnClickListener(v ->
                viewModel.searchAllocationById(binding.etSearchById.getText().toString())
        );

        binding.btnUpdateAllocation.setOnClickListener(v -> {
            String endDateForDb = (updateEndDate != null)
                    ? UiDateUtils.formatDateForDb(updateEndDate)
                    : "";

            viewModel.updateAllocation(
                    currentSelectedAllocation,
                    binding.etEditMeterSerialNumber.getText().toString(),
                    binding.etEditRoomNumber.getText().toString(),
                    UiDateUtils.formatDateForDb(updateStartDate),
                    endDateForDb
            );
        });

        // --- OBSERVE VIEWMODEL LIVEDATA ---
        viewModel.getAllocationLiveData().observe(getViewLifecycleOwner(), allocation -> {
            currentSelectedAllocation = allocation;
            if (allocation != null) {

                if (allocation.getStartingDate() != null) {
                    Calendar parsedStartCalendar = UiDateUtils.parseDateToCalendar(
                            DateUtils.toDisplayFormat(allocation.getStartingDate())
                    );
                    if (parsedStartCalendar != null) {
                        updateStartDate.setTime(parsedStartCalendar.getTime());
                    }
                }

                // Parse End Date safely (Optional)
                if (allocation.getEndingDate() != null && !allocation.getEndingDate().isEmpty()) {
                    Calendar parsedEndCalendar = UiDateUtils.parseDateToCalendar(
                            DateUtils.toDisplayFormat(allocation.getEndingDate())
                    );
                    if (parsedEndCalendar != null) {
                        updateEndDate = parsedEndCalendar;
                        binding.tvUpdateEndingDate.setText(UiDateUtils.formatDateForDisplay(updateEndDate));
                    } else {
                        updateEndDate = null;
                        binding.tvUpdateEndingDate.setText("");
                    }
                } else {
                    updateEndDate = null;
                    binding.tvUpdateEndingDate.setText("");
                }

                binding.etEditId.setText(String.valueOf(allocation.getId()));
                binding.etEditMeterSerialNumber.setText(String.valueOf(allocation.getMeterSerialNumber()));
                binding.etEditRoomNumber.setText(String.valueOf(allocation.getRoomNumber()));
                binding.tvUpdateStartingDate.setText(UiDateUtils.formatDateForDisplay(updateStartDate));

                binding.etEditId.setEnabled(false); // ID remains uneditable
                binding.etEditMeterSerialNumber.setEnabled(true);
                binding.etEditRoomNumber.setEnabled(true);
                binding.btnUpdateStartingDate.setEnabled(true);
                binding.btnUpdateEndingDate.setEnabled(true);
                binding.btnUpdateAllocation.setEnabled(true);
            } else {
                disableEditForm();
            }
        });

        viewModel.getToastMessageLiveData().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                // Clear input fields on successful actions
                if (message.contains("allocated successfully")) {
                    binding.etAddMeterSerialNumber.setText("");
                    binding.etAddRoomNumber.setText("");
                    // Reset add start date back to today's display text
                    addStartDate.setTime(Calendar.getInstance().getTime());
                    binding.tvAddStartingDate.setText(UiDateUtils.formatDateForDisplay(addStartDate));
                } else if (message.contains("updated successfully")) {
                    binding.etSearchById.setText("");
                }
            }
        });
    }

    private void initializeDatePickers() {
        // Set default text for the add starting date display view
        binding.tvAddStartingDate.setText(UiDateUtils.formatDateForDisplay(addStartDate));
    }

    private void setupClickListeners() {
        // Wire up Add Starting Date picker dialog
        binding.btnAddStartingDate.setOnClickListener(v ->
                UiDateUtils.showDatePicker(getContext(), addStartDate, (view1, year, month, dayOfMonth) -> {
                    addStartDate.set(year, month, dayOfMonth);
                    binding.tvAddStartingDate.setText(UiDateUtils.formatDateForDisplay(addStartDate));
                })
        );

        // Wire up Update Starting Date picker dialog
        binding.btnUpdateStartingDate.setOnClickListener(v ->
                UiDateUtils.showDatePicker(getContext(), updateStartDate, (view1, year, month, dayOfMonth) -> {
                    updateStartDate.set(year, month, dayOfMonth);
                    binding.tvUpdateStartingDate.setText(UiDateUtils.formatDateForDisplay(updateStartDate));
                })
        );

        // Wire up Update Ending Date picker dialog (allows setting or leaving blank)
        binding.btnUpdateEndingDate.setOnClickListener(v -> {
            Calendar calendarToUse = (updateEndDate != null) ? updateEndDate : Calendar.getInstance();

            UiDateUtils.showDatePicker(getContext(), calendarToUse, (view1, year, month, dayOfMonth) -> {
                if (updateEndDate == null) {
                    updateEndDate = Calendar.getInstance();
                }
                updateEndDate.set(year, month, dayOfMonth);
                binding.tvUpdateEndingDate.setText(UiDateUtils.formatDateForDisplay(updateEndDate));
            });
        });
    }

    private void disableEditForm() {
        binding.etEditId.setText("");
        binding.etEditMeterSerialNumber.setText("");
        binding.etEditRoomNumber.setText("");
        binding.tvUpdateStartingDate.setText("");
        binding.tvUpdateEndingDate.setText("");
        updateEndDate = null; // Clear out the optional end date reference

        binding.etEditId.setEnabled(false);
        binding.etEditMeterSerialNumber.setEnabled(false);
        binding.etEditRoomNumber.setEnabled(false);
        binding.btnUpdateStartingDate.setEnabled(false);
        binding.btnUpdateEndingDate.setEnabled(false);
        binding.btnUpdateAllocation.setEnabled(false);
        currentSelectedAllocation = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Prevent memory leaks in Fragments by setting binding to null
        binding = null;
    }
}