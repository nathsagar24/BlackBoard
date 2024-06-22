package com.dell.blackboard.model;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.dell.blackboard.presenter.presenter_interfaces.ProfilePresenterInterface;

import static com.dell.blackboard.Common.CURRENT_USER;
import static com.dell.blackboard.Common.mREF_connections;

public class ProfileModel {

    ProfilePresenterInterface presenter;
    String hodEmail;
    public ProfileModel(ProfilePresenterInterface presenter) {
        this.presenter = presenter;
    }

    public void addHOD(String email){
        if(CURRENT_USER.AdminLevel.equals("admin")) {
            mREF_connections.child(CURRENT_USER.UID).child("master_admin").setValue(email);
        }
        presenter.hodAddSuccess();
    }

    public void downloadProfileData(){
        mREF_connections.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hodEmail = dataSnapshot.child(CURRENT_USER.UID).child("master_admin").getValue(String.class);
                //These lines are causing error when old profile added please check them
//                if(hodEmail.equals("")){hodEmail = "ADD HOD";}
//                presenter.loadHodEmail(hodEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
