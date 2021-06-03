package com.example.whatsapp_android_clone.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.whatsapp_android_clone.model.SignUpEmailActivityModel;

import java.util.Arrays;

public class SignUpEmailActivityViewModel extends ViewModel {

    private MutableLiveData<SignUpEmailActivityModel> signUpModel = new MutableLiveData<>(new SignUpEmailActivityModel( "", "", "", ""));

    public MutableLiveData<SignUpEmailActivityModel> getSignUpModel() {
        return signUpModel;
    }

    public void setSignUpModel(SignUpEmailActivityModel signUpModel) {
        this.signUpModel.setValue(signUpModel);
    }
}
