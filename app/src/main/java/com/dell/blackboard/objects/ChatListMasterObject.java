package com.dell.blackboard.objects;

public class ChatListMasterObject {

    public String lastText;
    public UserObject userObjects;
    public String chatCounts;

    public ChatListMasterObject(String lastText,UserObject userObjects, String chatCounts) {
        this.lastText = lastText;
        this.userObjects = userObjects;
        this.chatCounts = chatCounts;
    }

    public ChatListMasterObject() {
    }
}
