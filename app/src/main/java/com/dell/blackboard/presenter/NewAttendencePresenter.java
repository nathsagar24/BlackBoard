package com.dell.blackboard.presenter;

import android.util.Log;

import com.dell.blackboard.model.NewAttendenceModel;
import com.dell.blackboard.presenter.presenter_interfaces.NewAttendencePresenterInterface;
import com.dell.blackboard.view.NewAttendenceView;

import java.util.ArrayList;
import java.util.Date;

import static com.dell.blackboard.Common.EDIT_ATTENDANCE_DATE;
import static com.dell.blackboard.Common.INDIV_ATTENDANCE_ORIGNAL;
import static com.dell.blackboard.Common.ROLL_LIST;
import static com.dell.blackboard.Common.TEMP01_LIST;
import static com.dell.blackboard.Common.TOTAL_CLASSES;

public class NewAttendencePresenter implements NewAttendencePresenterInterface {

    NewAttendenceView view;
    NewAttendenceModel model;

    public NewAttendencePresenter(NewAttendenceView view) {
        this.view = view;
        this.model =new  NewAttendenceModel(this);
    }

    public void performAttendenceUpload(Date date){
        model.performAttendenceUpload(date);
    }

    public void performEditUpload(){
        model.performAttendancePercent();
    }

    @Override
    public void success() {
        view.uploadSuccess();
    }

    @Override
    public void failed() {
        view.uploadFailed();
    }

    @Override
    public void percentListDownloaded(ArrayList<Double> percentList) {
        for (int i = 0; i<TEMP01_LIST.size(); i++){
            Log.i("TAG",TEMP01_LIST.get(i)+"  "+INDIV_ATTENDANCE_ORIGNAL.get(i));
            if (TEMP01_LIST.get(i).equals("PRESENT")&&INDIV_ATTENDANCE_ORIGNAL.get(i).equals("ABSENT")){
                // A->P
                double percent = percentList.get(i)/100.0;
                double presentDays = ((double)TOTAL_CLASSES)*percent;
                presentDays += 1;
                percent = presentDays/(double)TOTAL_CLASSES;
                percentList.set(i,percent*100);
            }
            if (TEMP01_LIST.get(i).equals("ABSENT")&&INDIV_ATTENDANCE_ORIGNAL.get(i).equals("PRESENT")){
                // P->A
                double percent = percentList.get(i)/100.0;
                double presentDays = ((double)TOTAL_CLASSES)*percent;
                presentDays -= 1;
                percent = presentDays/(double)TOTAL_CLASSES;
                percentList.set(i,percent);
            }
        }
        model.updateEditedAttendance(ROLL_LIST, TEMP01_LIST, percentList, EDIT_ATTENDANCE_DATE);
    }

    @Override
    public void editSuccess() {
        view.editSuccess();
    }

    @Override
    public void editFailed() {
        view.editFailed();
    }
}
