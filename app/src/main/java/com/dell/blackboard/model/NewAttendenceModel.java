package com.dell.blackboard.model;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.dell.blackboard.presenter.presenter_interfaces.NewAttendencePresenterInterface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.dell.blackboard.Common.CURRENT_CLASS_ID_LIST;
import static com.dell.blackboard.Common.CURRENT_INDEX;
import static com.dell.blackboard.Common.ROLL_LIST;
import static com.dell.blackboard.Common.TEMP01_LIST;
import static com.dell.blackboard.Common.mREF_classList;

public class NewAttendenceModel {

    NewAttendencePresenterInterface presenter;
    int  totalLectureCount;
    double temp01, temp02;
    DatabaseReference refAttendance, refAttdPercentage;
    ValueEventListener valueEventListener;
    ArrayList<String> templist;String todayString;
    String attendanceKey;
    public NewAttendenceModel(NewAttendencePresenterInterface presenter) {
        this.presenter = presenter;
    }

    public void performAttendenceUpload(Date date){
        refAttendance = mREF_classList.child(CURRENT_CLASS_ID_LIST.get(CURRENT_INDEX))
                .child("attendance");
        refAttdPercentage = mREF_classList.child(CURRENT_CLASS_ID_LIST.get(CURRENT_INDEX))
                .child("attendance_percentage");
        // SET key for today attendance
        setAttendanceKey(date);
    }
    private void setAttendanceKey(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        todayString = formatter.format(date);
        attendanceKey = todayString+"-1";
        refAttendance.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if (dataSnapshot.getKey().contains(todayString)){
                        String p = dataSnapshot.getKey().replace(todayString+"-","");
                        int x = Integer.parseInt(p);
                        x++;
                        attendanceKey = todayString+"-"+x;
                    }
                }
                performIndividualAttendanceUpload();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void performIndividualAttendanceUpload(){
        for (int i = 0; i<ROLL_LIST.size(); i++){
            refAttendance.child(attendanceKey).child(ROLL_LIST.get(i)).setValue(TEMP01_LIST.get(i));
        }
        // Update Percentage List
        performAttendancePercentUpload();
    }

    private void tempFunction(){
        refAttdPercentage.removeEventListener(valueEventListener);
        for (int i = 0; i<templist.size(); i++){
            temp01 = Double.parseDouble(templist.get(i))/100;
            if(TEMP01_LIST.get(i).equals("PRESENT")){
                temp02 = 1.0;
            }
            else{
                temp02 = 0.0;
            }
            temp01 = (temp01*(totalLectureCount-1) + temp02)/totalLectureCount;
            double roundOff = temp01;
            refAttdPercentage.child(ROLL_LIST.get(i)).setValue(roundOff * 100);
        }
        presenter.success();

    }
    void performAttendancePercentUpload(){
        refAttendance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                totalLectureCount = (int)dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        templist = new ArrayList<>();
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(int i = 0; i<dataSnapshot.getChildrenCount();i++){
                    templist.add(dataSnapshot.child(ROLL_LIST.get(i)).getValue().toString());
                }
                tempFunction();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                presenter.failed();
            }
        };
        refAttdPercentage.addValueEventListener(valueEventListener);
    }


    public void performAttendancePercent(){
        //Load Percentage List
        ArrayList<Double> percentList = new ArrayList<>();
        mREF_classList.child(CURRENT_CLASS_ID_LIST.get(CURRENT_INDEX)).child("attendance_percentage")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            percentList.add(snapshot.getValue(Double.class));
                        }
                        presenter.percentListDownloaded(percentList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void updateEditedAttendance(ArrayList<String> roll,
                                       ArrayList<String> indivList, ArrayList<Double> percent, String date){

        refAttendance = mREF_classList.child(CURRENT_CLASS_ID_LIST.get(CURRENT_INDEX))
                .child("attendance").child(date);
        refAttdPercentage = mREF_classList.child(CURRENT_CLASS_ID_LIST.get(CURRENT_INDEX))
                .child("attendance_percentage");
        for (int i =0; i<roll.size(); i++){
            refAttendance.child(roll.get(i)).setValue(indivList.get(i));
            refAttdPercentage.child(roll.get(i)).setValue(percent.get(i));
        }
        presenter.editSuccess();

    }

}
