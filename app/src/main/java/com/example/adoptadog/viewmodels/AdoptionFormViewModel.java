package com.example.adoptadog.viewmodels;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.adoptadog.models.AdoptionForm;

public class AdoptionFormViewModel extends ViewModel {

    private final MutableLiveData<Boolean> saveResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFormValid = new MutableLiveData<>();
    private final MutableLiveData<String> phoneError = new MutableLiveData<>();

    public LiveData<Boolean> getSaveResult() {
        return saveResult;
    }

    public LiveData<Boolean> getIsFormValid() {
        return isFormValid;
    }

    public LiveData<String> getPhoneError() {
        return phoneError;
    }

    public void saveAdoptionForm(AdoptionForm form) {
        if (validateForm(form)) {
            saveResult.setValue(true);
        } else {
            saveResult.setValue(false);
        }
    }

    public void validatePhoneNumber(String phoneNumber, String countryCode) {
        String error = null;
        switch (countryCode) {
            case "+1 (US)":
                if (phoneNumber.length() != 10) {
                    error = "Phone number must be 10 digits for US";
                } else if (!phoneNumber.matches("\\d+")) {
                    error = "Phone number must contain only digits";
                }
                break;

            case "+44 (GB)":
                if (phoneNumber.length() < 10 || phoneNumber.length() > 11) {
                    error = "Phone number must be 10 or 11 digits for UK";
                } else if (!phoneNumber.matches("\\d+")) {
                    error = "Phone number must contain only digits";
                }
                break;

            case "+34 (ES)":
                if (phoneNumber.length() != 9) {
                    error = "Phone number must be 9 digits for Spain";
                } else if (!phoneNumber.matches("\\d+")) {
                    error = "Phone number must contain only digits";
                }
                break;

            case "+49 (DE)":
                if (phoneNumber.length() != 11) {
                    error = "Phone number must be 11 digits for Germany";
                } else if (!phoneNumber.matches("\\d+")) {
                    error = "Phone number must contain only digits";
                }
                break;

            default:
                error = null; //
                break;
        }

        phoneError.setValue(error);
    }

    private boolean validateForm(AdoptionForm form) {
        if (TextUtils.isEmpty(form.getFullName())) {
            isFormValid.setValue(false);
            return false;
        }
        if (TextUtils.isEmpty(form.getEmail())) {
            isFormValid.setValue(false);
            return false;
        }
        if (TextUtils.isEmpty(form.getPhone())) {
            isFormValid.setValue(false);
            return false;
        }
        if (TextUtils.isEmpty(form.getAddress())) {
            isFormValid.setValue(false);
            return false;
        }
        isFormValid.setValue(true);
        return true;
    }
}
