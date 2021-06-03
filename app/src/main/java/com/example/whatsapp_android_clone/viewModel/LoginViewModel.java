package com.example.whatsapp_android_clone.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.whatsapp_android_clone.model.LoginModel;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginModel> loginModel = new MutableLiveData<>(new LoginModel("", ""));
    public LiveData<LoginModel> getLoginModel(){
        return loginModel;
    }

    public void setLoginModel(LoginModel loginModel){
        this.loginModel.setValue(loginModel);
    }

}
