package com.dell.blackboard.model;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.dell.blackboard.presenter.presenter_interfaces.LoginPresenterInterface;

import androidx.annotation.NonNull;

import static com.dell.blackboard.Common.FIREBASE_USER;
import static com.dell.blackboard.Common.mAUTH;
import static com.dell.blackboard.Common.mREF_users;

public class LoginModel {
    LoginPresenterInterface presenter;

    public LoginModel(LoginPresenterInterface presenter) {
        this.presenter = presenter;
    }

    public void login(String email, String password){
        mAUTH.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FIREBASE_USER = mAUTH.getCurrentUser();
                            if (FIREBASE_USER.isEmailVerified()){
                                firebaseUpdateToken();
                                presenter.loginSuccess();
                            }else{
                                mAUTH.signOut();
                                presenter.loginFailed();
                            }
                        } else {
                            presenter.loginFailed();
                        }
                    }
                });
    }
    public void firebaseUpdateToken(){
        String token = FirebaseInstanceId.getInstance().getToken();
        if(token != null){
            mREF_users.child(mAUTH.getUid()).child("token").setValue(token);
        }
    }
}
