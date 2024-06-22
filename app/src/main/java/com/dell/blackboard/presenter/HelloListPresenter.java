package com.dell.blackboard.presenter;

import com.dell.blackboard.model.HelloListModel;
import com.dell.blackboard.objects.UserObject;
import com.dell.blackboard.presenter.presenter_interfaces.HelloListPresenterInterface;
import com.dell.blackboard.view.HelloListView;

import java.util.ArrayList;


public class HelloListPresenter implements HelloListPresenterInterface {
    HelloListView view;
    HelloListModel model;
    public HelloListPresenter(HelloListView view) {
        this.view = view;
        model = new HelloListModel(this);
    }

    public void performHelloListDownload(){
        model.performHelloListDownload();
    }

    @Override
    public void helloListLoadSuccess(ArrayList<UserObject> userObjects) {
        view.helloListLoadSuccess(userObjects);
    }

    @Override
    public void helloListLoadFailed() {
        view.helloListLoadFailed();
    }
}
