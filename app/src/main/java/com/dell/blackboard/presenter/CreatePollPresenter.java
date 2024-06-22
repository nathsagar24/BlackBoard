package com.dell.blackboard.presenter;

import com.dell.blackboard.model.CreatePollModel;
import com.dell.blackboard.presenter.presenter_interfaces.CreatePollPresenterInterface;
import com.dell.blackboard.view.CreatePollView;

import java.util.ArrayList;

public class CreatePollPresenter implements CreatePollPresenterInterface {
    CreatePollView view;
    CreatePollModel model;
    public CreatePollPresenter(CreatePollView view) {
        this.view = view;
        model = new CreatePollModel(this);
    }

    public void performCreatePoll(String title, ArrayList<String> optionList){
        model.performCreatePoll(title,optionList);
    }


    @Override
    public void createPollSuccess() {
        view.createPollSuccess();
    }

    @Override
    public void createPollFailed() {
        view.createPollFailed();
    }
}
