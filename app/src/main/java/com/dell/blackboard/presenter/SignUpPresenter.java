package com.dell.blackboard.presenter;

import android.util.Log;

import com.dell.blackboard.objects.UserObject;
import com.dell.blackboard.model.SignUpModel;
import com.dell.blackboard.presenter.presenter_interfaces.SignUpPresenterInterface;
import com.dell.blackboard.view.SignUpView;

public class SignUpPresenter implements SignUpPresenterInterface {

    SignUpView view;
    SignUpModel signUpModel;
    public SignUpPresenter(SignUpView view) {
        this.view = view;
        signUpModel = new SignUpModel(this);
    }

    public void performSignUp(String getEmailSign, String getPasswordSign, String getPhoneSign, String getNameSign, String getAdminLevelSign, String getRollSign){
        UserObject userObject = new UserObject(getEmailSign, getPhoneSign, getNameSign, getAdminLevelSign, getRollSign);
        Log.v("TAG","perform Signup Called");
        signUpModel.initiate_signup(getEmailSign,getPasswordSign,userObject);
    }


    @Override
    public void signupSuccess() {
        view.successSignup();
    }

    @Override
    public void signupFailed() {
        view.showErrorFailed();
    }
}
