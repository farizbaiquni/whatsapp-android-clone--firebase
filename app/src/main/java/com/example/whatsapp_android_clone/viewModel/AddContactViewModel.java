package com.example.whatsapp_android_clone.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.whatsapp_android_clone.model.AddContactModel;

public class AddContactViewModel extends ViewModel {

    private MutableLiveData<AddContactModel> addContactModel = new MutableLiveData<>();

    public LiveData<AddContactModel> getAddContactModel(){
        return addContactModel;
    }

    public void setAddContactModel(AddContactModel model){
        this.addContactModel.setValue(model);
    }


    private MutableLiveData<String> keyword = new MutableLiveData<>(null);
    public LiveData<String> getKeyword(){
        return keyword;
    }
    public void setKeyword(String keyword){
        this.keyword.setValue(keyword);
    }

}
