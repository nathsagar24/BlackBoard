package com.dell.blackboard.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.dell.blackboard.objects.UserObject;
import com.dell.blackboard.presenter.presenter_interfaces.SignUpPresenterInterface;

import static com.dell.blackboard.Common.FIREBASE_USER;
import static com.dell.blackboard.Common.mAUTH;
import static com.dell.blackboard.Common.mREF_ACCESS_CONTROL;
import static com.dell.blackboard.Common.mREF_ACCOUNT_LIST;
import static com.dell.blackboard.Common.mREF_users;

public class SignUpModel {

    SignUpPresenterInterface presenter;

    public SignUpModel(SignUpPresenterInterface presenter) {
        this.presenter = presenter;
    }
    public void initiate_signup(String getEmailSign, String getPasswordSign, final UserObject userObject){
        String s = getEmailSign.replace(".","_dot_");
        mREF_ACCESS_CONTROL.child(s).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.v("TAG",""+dataSnapshot);
                if (dataSnapshot.getValue() != null){
                    signup(getEmailSign,getPasswordSign,userObject);
                }
                else{
                    signup(getEmailSign,getPasswordSign,userObject);
                    presenter.signupFailed();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                presenter.signupFailed();
            }
        });
    }

    private void signup(String getEmailSign, String getPasswordSign, final UserObject userObject){
        Log.v("TAG",""+mAUTH);
        mAUTH.createUserWithEmailAndPassword(getEmailSign, getPasswordSign)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.v("TAG","onComplete");
                        ///only execute if successful sign up
                        if (task.isSuccessful()) {
                            Log.v("TAG","Verification Email Sent");
                            mAUTH.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        FIREBASE_USER = mAUTH.getCurrentUser();
                                        userObject.UID = mAUTH.getUid();
                                        mREF_users.child(userObject.UID).setValue(userObject);
                                        mREF_users.child(userObject.UID).child("lower_name").setValue(userObject.Name.toLowerCase());
                                        if (userObject.AdminLevel.equals("user")) {
                                            updateStudentList(userObject.Roll, userObject.UID, userObject);
                                        }else {
                                            mAUTH.signOut();
                                            presenter.signupSuccess();
                                        }
                                    }
                                }
                            });
                        } else {
                            presenter.signupFailed();
                        }
                    }
                });
    }

    private void updateStudentList(String sch_id, String uid,UserObject userObject){
        String year = sch_id.substring(0,2);
        String course = sch_id.substring(2,4);
        mREF_ACCOUNT_LIST.child(year).child(course).child(uid).child("roll").setValue(userObject.Roll);
        mREF_ACCOUNT_LIST.child(year).child(course).child(uid).child("name").setValue(userObject.Name);

        mAUTH.signOut();
        presenter.signupSuccess();
    }
}
