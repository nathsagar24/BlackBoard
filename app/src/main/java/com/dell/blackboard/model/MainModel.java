package com.dell.blackboard.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.dell.blackboard.objects.AdminClassObject;
import com.dell.blackboard.objects.ClassObject;
import com.dell.blackboard.Common;
import com.dell.blackboard.objects.HelloListObject;
import com.dell.blackboard.objects.NotificationStoreObj;
import com.dell.blackboard.objects.StudentClassObject;
import com.dell.blackboard.objects.UserObject;
import com.dell.blackboard.presenter.presenter_interfaces.MainPresenterInterface;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static android.content.ContentValues.TAG;
import static com.dell.blackboard.Common.CURRENT_ADMIN_CLASS_LIST;
import static com.dell.blackboard.Common.CURRENT_ADMIN_FEEDBACK_STATUS;
import static com.dell.blackboard.Common.CURRENT_CLASS_ID_LIST;
import static com.dell.blackboard.Common.CURRENT_USER;
import static com.dell.blackboard.Common.CURRENT_USER_CLASS_LIST_ID;
import static com.dell.blackboard.Common.FIREBASE_USER;
import static com.dell.blackboard.Common.HELLO_REQUEST_USERS;

import static com.dell.blackboard.Common.SHARED_PREFERENCES;
import static com.dell.blackboard.Common.mREF_admin_classList;
import static com.dell.blackboard.Common.mREF_classList;
import static com.dell.blackboard.Common.mREF_oldRecords;
import static com.dell.blackboard.Common.mREF_student_classList;
import static com.dell.blackboard.Common.mREF_users;

public class MainModel {

    MainPresenterInterface presenter;
    ArrayList<ClassObject> classObjectArrayList;
    ArrayList<String> adminList;
    ArrayList<UserObject> userObjectsList;
    ClassObject classObject;
    String temp;String collabUID = "",collabName = "";
    int roll,flag =0;
    ArrayList<String> adminIndex;
    DatabaseReference fromPath;
    DatabaseReference toPath;
    ChildEventListener childListener;
    ValueEventListener tempListener, tempListener2;
    ValueEventListener valueEventListener;
    ArrayList<StudentClassObject> studentClassObjectArrayList;
    StudentClassObject studentClassObject;
    ArrayList<String> userAttendancePercentList;
    ValueEventListener valueEventListenerCollab,valueEventListenerCollab2;
    public MainModel(MainPresenterInterface presenter) {
        this.presenter = presenter;
    }

    public void performDataDownload(){

        //CURRENT USER
        mREF_users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Common.CURRENT_USER = dataSnapshot.child(FIREBASE_USER.getUid()).getValue(UserObject.class);
                if(CURRENT_USER != null){
                    if (CURRENT_USER.AdminLevel.equals("admin")) {
                        SHARED_PREFERENCES.edit().putInt("AdminLevel", 0).apply();
                    }else{
                        SHARED_PREFERENCES.edit().putInt("AdminLevel", 1).apply();
                    }
                    presenter.downloadDataSuccess();
                }
                else { presenter.downloadDataFailed(); }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    presenter.downloadDataFailed();
            }
        });


    }

    public void performAdminClassListDownload(){
        classObjectArrayList = new ArrayList<>();
        classObjectArrayList.clear();
       // presenter.adminClassListDownloadSuccess(classObjectArrayList);
        tempListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (int i=0; i<CURRENT_CLASS_ID_LIST.size();i++){
                    classObject = dataSnapshot.child(CURRENT_CLASS_ID_LIST.get(i)).getValue(ClassObject.class);
                    classObjectArrayList.add(classObject);
                }
                countFeedbackState();
                presenter.adminClassListDownloadSuccess(classObjectArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        tempListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CURRENT_CLASS_ID_LIST.clear();
                for (DataSnapshot s : dataSnapshot.getChildren()){
                    CURRENT_CLASS_ID_LIST.add(s.getKey());
                }

                mREF_classList.addListenerForSingleValueEvent(tempListener2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        mREF_admin_classList.addListenerForSingleValueEvent(tempListener);
    }

    private void countFeedbackState(){
        CURRENT_ADMIN_FEEDBACK_STATUS = new HashMap<>();
        mREF_classList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (String id : CURRENT_CLASS_ID_LIST){
                    if (!dataSnapshot.child(id).hasChild("feedback")){
                        CURRENT_ADMIN_FEEDBACK_STATUS.put(id,"null");
                    }
                    else {
                        long x;
                        if (dataSnapshot.child(id).child("feedback").hasChild("entry")) {

                            x = dataSnapshot.child(id).child("feedback").child("entry").getChildrenCount();
                        }
                        else{x = 0;}
                        int p = (int)x;
                        CURRENT_ADMIN_FEEDBACK_STATUS.put(id,Integer.toString(p));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void performAdminAddClass(final ClassObject classObject){
        CURRENT_CLASS_ID_LIST.clear();
        CURRENT_ADMIN_CLASS_LIST.clear();
        final String newID = mREF_classList.push().getKey();
        mREF_classList.child(newID).setValue(classObject, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if(databaseError == null){
                    // now upload Fresh Blank Percentage List
                    double x=0;
                    for (int i = Integer.parseInt(classObject.startRoll); i<= Integer.parseInt(classObject.endRoll); i++){
                        mREF_classList.child(newID).child("attendance_percentage")
                                .child(Integer.toString(i)).setValue(x);

                    }
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a MMM d, ''yy");
                    String simpleTime = simpleDateFormat.format(new Date());
                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    mREF_classList.child(newID).child("notification").push()
                            .setValue(new NotificationStoreObj(classObject.className,
                                    "Class Created: \n"+currentDateTimeString,simpleTime,newID));
                    AdminClassObject obj = new AdminClassObject(CURRENT_USER.UID, newID);
                    mREF_admin_classList.child(newID).setValue(obj);
                    presenter.addAdminClassSuccess();
                }
                else{
                    presenter.addAdminClassFailed();
                }
            }
        });
        mREF_classList.child(newID).child("admin_index").child(FIREBASE_USER.getUid()).setValue(FIREBASE_USER.getUid());
    }

    public void performEndSession(int index){
        if(CURRENT_USER.AdminLevel.equals("admin")) {
            fromPath = mREF_classList.child(CURRENT_CLASS_ID_LIST.get(index));
            toPath = mREF_oldRecords;
            fromPath.child("student_index").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    deleteIndex(CURRENT_CLASS_ID_LIST.get(index));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        if(CURRENT_USER.AdminLevel.equals("user")){
            DatabaseReference fromPath = mREF_student_classList.child(CURRENT_USER_CLASS_LIST_ID.get(index));
            fromPath.removeValue();
            removeNotificationEndSession(CURRENT_CLASS_ID_LIST.get(index));
        }
    }

    private void moveRecord(final DatabaseReference fromPath, final DatabaseReference toPath) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.push().setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()) {
                            fromPath.removeValue();
                            Log.d(TAG, "Success!");
                        } else {
                            Log.d(TAG, "Copy failed!");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        fromPath.addListenerForSingleValueEvent(valueEventListener);
    }

    public void performUserClassListDownload(){

        ChildEventListener childEventListener;
        studentClassObjectArrayList = new ArrayList<>();
        userAttendancePercentList = new ArrayList<>();
        classObjectArrayList = new ArrayList<>();
        //Done by sagar
        //roll = Integer.parseInt(CURRENT_USER.Roll)%1000;
        roll=Integer.parseInt(CURRENT_USER.Roll);
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                studentClassObject = dataSnapshot.getValue(StudentClassObject.class);
                    studentClassObjectArrayList.add(studentClassObject);
                    Log.d("StudentClassKey",studentClassObject.classKey);
                    CURRENT_USER_CLASS_LIST_ID.add(dataSnapshot.getKey());
                    CURRENT_CLASS_ID_LIST.add(studentClassObject.classKey);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<String> uselessValue = new ArrayList<>();

                    for(int i = 0; i<studentClassObjectArrayList.size(); i++){
                       // Log.v("TAG","Student Object Array List :"+studentClassObjectArrayList.get(i).classKey+" "+i);
                        try{temp = dataSnapshot.child(studentClassObjectArrayList.get(i).classKey)
                                .child("attendance_percentage")
                                .child(Integer.toString(roll))
                                .getValue(Double.class).toString();
                            classObjectArrayList.add(dataSnapshot.child(studentClassObjectArrayList.get(i).classKey)
                                .getValue(ClassObject.class));
                            userAttendancePercentList.add(temp);
                        }
                        catch (Exception e){
                                Log.v("TAG","Exception occured : "+dataSnapshot.child(studentClassObjectArrayList.get(i).classKey)
                                        .child("attendance_percentage").child(Integer.toString(roll)));
                            uselessValue.add(studentClassObjectArrayList.get(i).classKey);
                        }
                    }
                    mREF_classList.removeEventListener(valueEventListener);
                    mREF_student_classList.removeEventListener(childEventListener);
                    Common.ATTD_PERCENTAGE_LIST = userAttendancePercentList;
                    cleanUselessStudentKey(uselessValue);
                    presenter.userClassListDownloadSuccess(classObjectArrayList, userAttendancePercentList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    presenter.downloadDataFailed();
                }
            };
        mREF_student_classList.addChildEventListener(childEventListener);
        mREF_classList.addListenerForSingleValueEvent(valueEventListener);
    }

    public void deleteIndex(final String x){
        adminIndex= new ArrayList<>();
        mREF_classList.child(x).child("admin_index").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                adminIndex.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(int i=0; i<adminIndex.size();i++){
                    mREF_users.child(adminIndex.get(i)).child("class_list").child(x).removeValue();
                }
               moveRecord(fromPath,toPath);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mREF_classList.addListenerForSingleValueEvent(eventListener);
    }

    public void addCollab(int index, final String email,boolean isTransfer){
        final String classID = CURRENT_CLASS_ID_LIST.get(index);
        mREF_users.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                UserObject userObject = dataSnapshot.getValue(UserObject.class);
                if(userObject.Email.equals(email)){
                    collabUID = userObject.UID;
                    collabName = userObject.Name;
                    if (isTransfer){mREF_classList.child(CURRENT_CLASS_ID_LIST.get(index))
                            .child("facultyEmail").setValue(email);
                        mREF_classList.child(CURRENT_CLASS_ID_LIST.get(index))
                                .child("facultyUID").setValue(collabUID);
                        mREF_classList.child(CURRENT_CLASS_ID_LIST.get(index))
                                .child("facultyName").setValue(collabName);}
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        valueEventListenerCollab2 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!collabUID.equals("")){
                    AdminClassObject obj = new AdminClassObject(collabUID, classID);
                    searchAndAddCollabFinal(obj);
                    return;
                }
                else {
                    presenter.transferCollabActionFailed();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mREF_users.addValueEventListener(valueEventListenerCollab2);
    }

    public void transferClass(final int index, final String email){
        addCollab(index,email,true);
        mREF_users.child(CURRENT_USER.UID).child("class_list")
                    .child(CURRENT_CLASS_ID_LIST.get(index)).removeValue();
        mREF_classList.child(CURRENT_CLASS_ID_LIST.get(index)).child("admin_index")
                    .child(CURRENT_USER.UID).removeValue();
    }

    public void searchAndAddCollabFinal(final AdminClassObject obj){
        flag = 0;
        mREF_users.removeEventListener(valueEventListenerCollab2);
        valueEventListenerCollab = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AdminClassObject objTemp;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    objTemp = snapshot.getValue(AdminClassObject.class);
                    if(objTemp.adminUID.equals(obj.adminUID)&&objTemp.classKey.equals(obj.classKey)){
                        flag = 1;
                    }
                }
                if(flag == 0){
                    temp1(obj);
                }
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mREF_admin_classList.addValueEventListener(valueEventListenerCollab);
    }

    public void performConnectionDownload(){
        adminList = new ArrayList<>();
        userObjectsList = new ArrayList<>();
        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(int count = 0; count<adminList.size();count++) {
                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        UserObject obj = s.getValue(UserObject.class);
                        if(adminList.get(count).equals(obj.UID)){
                            flag = 0;
                            for (int i = 0; i<userObjectsList.size();i++){
                                if(userObjectsList.get(0) != null){
                                   if(userObjectsList.get(i).UID.equals(obj.UID)){
                                       flag = 1;
                                   }
                                }
                            }
                            if (flag == 0){
                                userObjectsList.add(obj);
                            }
                        }
                    }
                }

                presenter.connectionListDownloadSuccess(userObjectsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                presenter.connectionListDownloadFailed();
            }
        };

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                   if(!adminList.contains(s.getKey())){
                       adminList.add(s.getKey());
                   }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        for (int k =0; k<CURRENT_CLASS_ID_LIST.size();k++) {
            mREF_classList.child(CURRENT_CLASS_ID_LIST.get(k)).child("admin_index").addListenerForSingleValueEvent(valueEventListener);
        }
        mREF_users.addValueEventListener(valueEventListener1);
    }

    private void temp1(AdminClassObject obj){
        mREF_admin_classList.removeEventListener(valueEventListenerCollab);
        mREF_users.child(collabUID).child("class_list").child(obj.classKey).setValue(obj);
        mREF_classList.child(obj.classKey).child("admin_index").child(collabUID).setValue(collabUID);
    }

    private void cleanUselessStudentKey(ArrayList<String> arrayList){
        for (int i = 0; i<arrayList.size();i++){
            mREF_users.child(CURRENT_USER.UID).child("class_list").child(arrayList.get(i)).removeValue();
            mREF_classList.child(arrayList.get(i)).child("student_index").child(CURRENT_USER.UID).removeValue();
            removeNotificationEndSession(arrayList.get(i));
        }
    }

    public void loadHelloRequest(){
        HashMap<String, HelloListObject> tempHashMap = new HashMap<>();

        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for ( String key : tempHashMap.keySet() ) {
                    UserObject userObject = dataSnapshot.child(key).getValue(UserObject.class);
                    HelloListObject object = new HelloListObject(userObject,tempHashMap.get(key).hello);
                    tempHashMap.put(key,object);
                }
                HELLO_REQUEST_USERS = tempHashMap;
                presenter.loadHelloSuccess();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                presenter.loadHelloFailed();
            }
        };
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s: dataSnapshot.getChildren()){
                    if (s.getValue().toString().equals("1")){
                    HelloListObject obj = new HelloListObject(null,Integer.parseInt(s.getValue().toString()));
                    tempHashMap.put(s.getKey(), obj);
                    }
                }
                mREF_users.addListenerForSingleValueEvent(valueEventListener1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mREF_users.child(CURRENT_USER.UID).child("hello").addValueEventListener(valueEventListener);
    }

    private void removeNotificationEndSession(String classID) {
        //this function is responsible for removing notification object of removed class;
        mREF_users.child(CURRENT_USER.UID).child("notification")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            if (snapshot.child("classID").getValue().toString().equals(classID)){
                                mREF_users.child(CURRENT_USER.UID)
                                        .child("notification").child(snapshot.getKey()).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
