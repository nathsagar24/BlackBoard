package com.dell.blackboard.view;

import com.dell.blackboard.objects.AssignmentObject;

import java.util.ArrayList;

public interface AssignmentHomeView {

    void assignmentDownloadSuccess(ArrayList<AssignmentObject> assignmentObjects);
    void assignmentDownloadFailed();

}
