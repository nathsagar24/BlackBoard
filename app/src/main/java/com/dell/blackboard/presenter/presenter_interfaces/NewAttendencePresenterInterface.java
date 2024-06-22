package com.dell.blackboard.presenter.presenter_interfaces;

import java.util.ArrayList;

public interface NewAttendencePresenterInterface {

    void success();
    void failed();

    void percentListDownloaded(ArrayList<Double> percentList);
    void editSuccess();
    void editFailed();

}
