package com.dell.blackboard.view;

public interface HelloRequestView {

    void requestAccepted(String UID);
    void requestRejected(String UID);
    void requestActionFailed();

}
