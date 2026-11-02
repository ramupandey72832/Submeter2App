package com.submeter.presentation.viewmodel;

import com.example.rrallutilitylib.util.CryptoHelper;
import com.submeter.service.RoomService;

public class QrScanRoomViewModel extends BaseViewModel {
    private String decryptedRoomNumber;

    public QrScanRoomViewModel(RoomService roomService) {}

    public void processScannedPayload(String scannedPayload) {
        if (scannedPayload == null || scannedPayload.trim().isEmpty()) {
            setErrorMessage("Scan Error: Empty barcode or QR code detected.");
            return;
        }

        try {
            String decrypted = CryptoHelper.decryptFromBase64(scannedPayload, CryptoHelper.MY_SECRET_KEY);
            if (decrypted == null || !decrypted.contains("ROOM_NUMBER_")) {
                setErrorMessage("Invalid Format: Scanned code is not a valid room QR identifier.");
                return;
            }

            decryptedRoomNumber = decrypted.replace("ROOM_NUMBER_", "").trim();
            setErrorMessage(null); // Clear previous errors on success
            notifyStateChanged();
        } catch (Exception e) {
            setErrorMessage("Security Error: Unable to decrypt QR code data. Invalid Key or Data.");
        }
    }

    public String getDecryptedRoomNumber() {
        return decryptedRoomNumber;
    }

    public boolean isRoomNumberExist(String roomNumber) {
        return true;
    }
}