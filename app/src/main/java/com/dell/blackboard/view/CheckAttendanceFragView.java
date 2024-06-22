package com.dell.blackboard.view;

import com.dell.blackboard.objects.PollOptionValueLikeObject;
import com.dell.blackboard.objects.PostObject;

import java.util.ArrayList;
import java.util.HashMap;

public interface CheckAttendanceFragView {

    void success(ArrayList<Double> attendancePercentageList);
    void failed();

    void exportCsvSuccess();
    void exportCsvFailed();

    void notifySuccess();
    void notifyFailed();

    void postingSuccess();
    void postingFailed();

    void checkAttendanceReturn(boolean b);
    void fileUploadDone(String link);

    void loadPostSuccess(ArrayList<PostObject> postObjects ,
                         HashMap<String, PollOptionValueLikeObject> post_poll_option ,
                         ArrayList<String> post_like_list ,
                         HashMap<String, ArrayList<String>> post_url_list,
                         ArrayList<String> comment_count,ArrayList<String> likedID,HashMap<String,String> postPollSelect);

}
