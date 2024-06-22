package com.dell.blackboard.view;

import com.dell.blackboard.objects.ChatObject;
import com.dell.blackboard.objects.UserObject;

import java.util.ArrayList;

public interface ChatView {

    void chatLoadSuccess(ArrayList<ChatObject> chatObjects);
    void chatUpdate();
    void sendSuccess();

    void chatUserDataLoad(UserObject userObject);

}
