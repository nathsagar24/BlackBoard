package com.dell.blackboard.presenter.presenter_interfaces;

import com.dell.blackboard.objects.UserObject;

import java.util.ArrayList;

public interface HelloListPresenterInterface {
    void helloListLoadSuccess(ArrayList<UserObject> userObjects);
    void helloListLoadFailed();
}
