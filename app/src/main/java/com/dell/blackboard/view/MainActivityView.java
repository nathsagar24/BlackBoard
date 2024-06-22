package com.dell.blackboard.view;

import com.dell.blackboard.objects.ChatListMasterObject;
import com.dell.blackboard.objects.ClassObject;
import com.dell.blackboard.objects.UserObject;

import java.util.ArrayList;

public interface MainActivityView {


    void downloadDataSuccess();
    void downloadDataFailed();

    void addAdminClassSuccess();
    void addAdminClassFailed();

    void endSession(int index);

    void loadAdminClassList(ArrayList<ClassObject> classObjectArrayList);
    void loadUserClassList(ArrayList<ClassObject> classObjects, ArrayList<String> userAttendanceList);

    void addCollab(int index, String email);
    void transferClass(int index, String email);
    void transferClassFailed();

    void connectionListDownloadSuccess(ArrayList<UserObject> userList);
    void connectionListDownloadFailed();

    void loadHelloSuccess();
    void loadHelloFailed();

    void newChatNotif(ChatListMasterObject chatListMasterObject);

    void notifNewComment();
}
