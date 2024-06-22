package com.dell.blackboard.view;

import com.dell.blackboard.objects.UserObject;

import java.util.ArrayList;

public interface HelloListView {

    void helloListLoadSuccess(ArrayList<UserObject> userObjects);
    void helloListLoadFailed();

}
