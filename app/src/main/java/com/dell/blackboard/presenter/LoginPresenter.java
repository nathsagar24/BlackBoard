package com.dell.blackboard.presenter;

import com.dell.blackboard.model.LoginModel;
import com.dell.blackboard.presenter.presenter_interfaces.LoginPresenterInterface;
import com.dell.blackboard.view.LoginActivityView;

public class LoginPresenter implements LoginPresenterInterface {

    LoginActivityView view;
    LoginModel loginModel;
    public LoginPresenter(LoginActivityView view) {
        this.view = view;
        loginModel = new LoginModel(this);
    }

    public void performLogin(String getEmailLogin,String getPasswordLogin){
        loginModel.login(getEmailLogin,getPasswordLogin);
    }


    @Override
    public void loginSuccess() {
        view.successLogin();
    }

    @Override
    public void loginFailed() {
        view.showErrorFailed();
    }

}
