package com.dell.blackboard.presenter;

import com.dell.blackboard.model.ProfileModel;
import com.dell.blackboard.presenter.presenter_interfaces.ProfilePresenterInterface;
import com.dell.blackboard.view.ProfileActivityView;

public class ProfilePresenter implements ProfilePresenterInterface {

    ProfileActivityView view;
    ProfileModel model;
    public ProfilePresenter(ProfileActivityView view) {
        this.view = view;
        model = new ProfileModel(this);
    }

    public void addHOD(String email){
        model.addHOD(email);
    }

    public void downloadProfileData(){
        model.downloadProfileData();
    }

    @Override
    public void hodAddSuccess() {
        view.hodAddSuccess();
    }

    @Override
    public void hodAddFailed() {
        view.hodAddFailed();
    }

    @Override
    public void loadHodEmail(String text) {
        view.hodNameLoad(text);
    }
}
