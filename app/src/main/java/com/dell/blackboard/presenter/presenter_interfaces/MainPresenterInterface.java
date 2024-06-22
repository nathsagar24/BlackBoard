package com.dell.blackboard.presenter.presenter_interfaces;

import com.dell.blackboard.objects.ClassObject;

import com.dell.blackboard.objects.UserObject;

import java.util.ArrayList;

public interface MainPresenterInterface {

    void downloadDataSuccess();
    void downloadDataFailed();

    void addAdminClassSuccess();
    void addAdminClassFailed();

    void adminClassListDownloadSuccess(ArrayList<ClassObject> classObjectArrayList);
    void userClassListDownloadSuccess(ArrayList<ClassObject> classObjectArrayList,
                                      ArrayList<String> userPercentageList);

    void transferCollabActionFailed();

    void connectionListDownloadSuccess(ArrayList<UserObject> userList);
    void connectionListDownloadFailed();

    void loadHelloSuccess();
    void loadHelloFailed();

}
