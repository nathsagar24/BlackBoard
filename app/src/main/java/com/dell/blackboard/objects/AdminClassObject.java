package com.dell.blackboard.objects;

public class AdminClassObject {

    public String adminUID, classKey;

    public AdminClassObject(String adminUID, String classID) {
        this.adminUID = adminUID;
        this.classKey = classID;
    }

    public AdminClassObject() {
    }
}
