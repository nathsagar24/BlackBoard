package com.dell.blackboard.presenter.presenter_interfaces;

import com.dell.blackboard.objects.NotificationStoreObj;

import java.util.ArrayList;

public interface NotificationHistoryPresenterInterface {

    void notificationDownloadSuccess(ArrayList<NotificationStoreObj> notifobj);
    void notificationDownloadFailed();

}
