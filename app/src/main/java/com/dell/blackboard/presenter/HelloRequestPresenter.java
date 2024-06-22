package com.dell.blackboard.presenter;

import com.dell.blackboard.model.HelloRequestModel;
import com.dell.blackboard.presenter.presenter_interfaces.HelloRequestPresenterInterface;
import com.dell.blackboard.view.HelloRequestView;

public class HelloRequestPresenter implements HelloRequestPresenterInterface {
    HelloRequestView view;
    HelloRequestModel model;
    public HelloRequestPresenter(HelloRequestView view) {
        this.view = view;
        model = new HelloRequestModel(this);
    }

    public void performHelloRequestRespond(String UID, boolean hello){
        model.performHelloRequestRespond(UID,hello);
    }

    @Override
    public void requestAccepted(String UID) {
        view.requestAccepted(UID);
    }

    @Override
    public void requestRejected(String UID) {
        view.requestRejected(UID);
    }

    @Override
    public void requestActionFailed() {
        view.requestActionFailed();
    }
}
