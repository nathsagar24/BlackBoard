package com.dell.blackboard.view;

import java.util.ArrayList;

public interface ShowAttendanceView {
    void totalClassDownloadSuccess(long totalClass, ArrayList<String> dateList);
    void indivAttendanceDownload(ArrayList<String> data);
}
