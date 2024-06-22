package com.dell.blackboard.presenter.presenter_interfaces;

public interface HelloRequestPresenterInterface {


    void requestAccepted(String UID);
    void requestRejected(String UID);
    void requestActionFailed();

}
