package com.dell.blackboard.model;


import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.opencsv.CSVWriter;
import com.dell.blackboard.presenter.presenter_interfaces.CheckAttendancePresenterInterface;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.dell.blackboard.Common.ATTD_PERCENTAGE_LIST;
import static com.dell.blackboard.Common.CURRENT_ADMIN_CLASS_LIST;
import static com.dell.blackboard.Common.CURRENT_INDEX;
import static com.dell.blackboard.Common.ROLL_LIST;
import static com.dell.blackboard.Common.mREF_classList;

public class ExportCSVModel {

    String classID;
    DatabaseReference refAttendanceDetailed;
    ArrayList<String> dateListID;
    ArrayList<String> tempList;
    List<String []> data;
    String csvName;
    Date sDate1 = null,sDate2 = null;

    CheckAttendancePresenterInterface presenter;
    public ExportCSVModel(String classID, CheckAttendancePresenterInterface presenter) {
        this.classID = classID;
        this.presenter = presenter;
        refAttendanceDetailed = mREF_classList.child(classID).child("attendance");
        dateListID = new ArrayList<>();
        tempList = new ArrayList<>();
        data = new ArrayList<>();
        csvName = ROLL_LIST.get(0)+" - "+ROLL_LIST.get(ROLL_LIST.size()-1)+" : "+CURRENT_ADMIN_CLASS_LIST.get(CURRENT_INDEX).className;
    }

    public void exportAttendanceData(String date1, String date2){
        //Download dateList
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sDate1 = sdf.parse(date1);
            sDate2 = sdf.parse(date2);
        }catch (Exception e){e.printStackTrace();}
        refAttendanceDetailed.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    try {
                        Date sDatex = new SimpleDateFormat("yyyy-MM-dd").parse(postSnapshot.getKey().substring(0,10));
                        if (!sDate1.after(sDatex) && !sDate2.before(sDatex)){
                            dateListID.add(postSnapshot.getKey());
                        }
                    }catch (Exception e){e.printStackTrace();}
                }
                dateListID.add("Percentage Till Now");
                data.add(dateListID.toArray(new String[0]));
                downloadLIST();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                failed();
            }
        });
    }

    private void downloadLIST(){
        refAttendanceDetailed.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 0; i<ROLL_LIST.size(); i++){
                    for (int j = 0; j<dateListID.size()-1; j++){
                        if (dataSnapshot
                                .child(dateListID.get(j))
                                .child(ROLL_LIST.get(i))
                                .getValue().toString().equals("PRESENT")){
                        tempList.add("P");}
                        else{tempList.add("A");}
                    }
                    tempList.add(ATTD_PERCENTAGE_LIST.get(i));
                    data.add(tempList.toArray(new String[0]));
                    tempList.clear();
                }
                exportCSV();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                failed();
            }
        });
    }

    private void exportCSV(){
        String csv = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+csvName+".csv"); // Here csv file name is MyCsvFile.csv

                CSVWriter writer = null;
                try {
                    writer = new CSVWriter(new FileWriter(csv));

                    writer.writeAll(data);

                    writer.close();
                    success();
                } catch (IOException e) {
                    e.printStackTrace();
                    failed();
                }

    }

    public void success(){
        Log.i("TAG","DONE CSV");
        presenter.exportCsvSuccess();
    }
    public void failed(){
        presenter.exportCsvFailed();
    }

}
