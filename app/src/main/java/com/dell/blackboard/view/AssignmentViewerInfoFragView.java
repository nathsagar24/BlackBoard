package com.dell.blackboard.view;

import com.dell.blackboard.objects.AssignmentObject;

import java.util.ArrayList;
import java.util.HashMap;

public interface AssignmentViewerInfoFragView {

    void downloadSuccess(AssignmentObject assignmentObject, HashMap<String,String> meta_data,ArrayList<String> name_meta_data);
    void downloadFailed();

}
