package com.dell.blackboard.view;

import com.dell.blackboard.objects.AssignmentSubmissionObject;

import java.util.ArrayList;

public interface AssignmentViewerSubmissionFragView {
    void downloadSuccessStudent(ArrayList<AssignmentSubmissionObject> objectArrayList);
    void downloadFailed();
    void downloadSuccessSubmissionStudentList(ArrayList<String> roll_list, ArrayList<String> name_list);
}
