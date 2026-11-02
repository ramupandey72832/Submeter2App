package com.rohit.submeter2.fragments;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.application.baselibrary.libs.qr.QrCameraScanner;
import com.application.baselibrary.ui.utils.NavigationUtils;
import com.application.baselibrary.ui.utils.ToastMessage;
import com.rohit.submeter2.MainActivity;
import com.rohit.submeter2.databinding.FragmentQrScanRoomBinding;
import com.submeter.presentation.viewmodel.QrScanRoomViewModel;
import com.submeter.service.ServiceManager;

public class QrScanRoomFragment extends Fragment {

    private ToastMessage toastMessage;
    private FragmentQrScanRoomBinding binding;
    private QrCameraScanner cameraHelper;
    private QrScanRoomViewModel viewModel;
    private Uri temporaryImageUri;
    private boolean isHandlingError = false;

    // Stored reference so it can be cleanly removed in onDestroyView()
    private final QrScanRoomViewModel.StateListener stateListener = this::renderUiState;

    public QrScanRoomFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQrScanRoomBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toastMessage = new ToastMessage(requireContext());
        viewModel = new QrScanRoomViewModel(ServiceManager.getRoomService());

        viewModel.addStateListener(stateListener);

        // Initialize Camera Scanner
        cameraHelper = new QrCameraScanner((data, uri) -> {
            temporaryImageUri = uri;
            viewModel.processScannedPayload(data);
        });

        setupCameraPreview(view);

        // Click Listeners
        binding.btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        binding.btnFlashlight.setOnClickListener(v -> onFlashClicked());
        binding.btnRefresh.setOnClickListener(this::refreshCamera);

        binding.btnNext.setOnClickListener(v -> {
            String roomNumber = binding.etRoomNumber.getText().toString().trim();
            if (roomNumber.isEmpty()) {
                toastMessage.showError("Please enter a room number");
            } else if (viewModel.isRoomNumberExist(roomNumber) && temporaryImageUri != null) {
                sendToResultFragment(roomNumber, temporaryImageUri);
            }
        });
    }

    private void renderUiState() {
        if (getActivity() == null || binding == null) return;

        // Ensure UI updates happen on Main Thread
        getActivity().runOnUiThread(() -> {
            // Update room number field if decrypted successfully
            String roomNum = viewModel.getDecryptedRoomNumber();
            if (roomNum != null && !roomNum.equals(binding.etRoomNumber.getText().toString())) {
                binding.etRoomNumber.setText(roomNum);
            }

            // Handle error messages with popup animation
            String errorMsg = viewModel.getErrorMessage();
            if (errorMsg != null && !isHandlingError) {
                isHandlingError = true;
                binding.tvErrorMessage.setText(errorMsg);
                binding.layoutErrorPopup.setVisibility(View.VISIBLE);
                binding.layoutErrorPopup.setAlpha(0f);
                binding.layoutErrorPopup.animate().alpha(1f).setDuration(300).start();

                binding.layoutErrorPopup.postDelayed(() -> {
                    if (binding != null) {
                        binding.layoutErrorPopup.animate().alpha(0f).setDuration(300).withEndAction(() -> {
                            if (binding != null) {
                                binding.layoutErrorPopup.setVisibility(View.GONE);
                                isHandlingError = false;
                                refreshCamera(binding.btnRefresh);
                            }
                        }).start();
                    }
                }, 3000);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            View view = getView();
            if (view != null && mainActivity.permissionHelper != null && mainActivity.permissionHelper.hasAllPermissions()) {
                setupCameraPreview(view);
            }
        }
    }

    private void setupCameraPreview(@NonNull View view) {
        view.postDelayed(() -> {
            if (isAdded() && getContext() != null && binding != null) {
                cameraHelper.startScanning(requireContext(), binding.previewView, this);
            }
        }, 300);
    }

    private void onFlashClicked() {
        if (cameraHelper == null || binding == null) return;
        boolean isNowOn = cameraHelper.toggleTorch();
        binding.btnFlashlight.setColorFilter(isNowOn ? Color.YELLOW : Color.DKGRAY);
    }

    public void refreshCamera(View v) {
        if (cameraHelper == null || getContext() == null || binding == null) return;
        cameraHelper.restartScanning(requireContext(), binding.previewView, this);
        v.animate().rotationBy(360).setDuration(500).start();
    }

    public void sendToResultFragment(String result, Uri uri) {
        if (result == null || uri == null || getActivity() == null) return;
        Bundle bundle = new Bundle();
        bundle.putString("ARG_QR_DATA", result);
        MeterRecordingFragment resultFragment = new MeterRecordingFragment();
        NavigationUtils.navigateTo(getActivity(), resultFragment, com.application.android_ui_templete1.R.id.bottom_nav_activity_main_frame_layout, bundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (viewModel != null) {
            viewModel.removeStateListener(stateListener);
        }
        binding = null;
    }
}