package com.dell.blackboard.view;

import java.util.ArrayList;

public interface NotificationHistoryView {

    void loadRecyclerSuccess(ArrayList<String> title, ArrayList<String> body);
    void loadRecyclerFailed();

}
