package com.dell.blackboard.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.os.Bundle;

import com.dell.blackboard.Common;
import com.dell.blackboard.R;
import com.dell.blackboard.fragments.AdminCheckAttendanceFragment;
import com.dell.blackboard.fragments.UserCheckAttendanceFragment;

public class CheckAttendanceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_attendance);

        Bundle str=getIntent().getExtras();

        Fragment fragment;

        if(Common.CURRENT_USER.AdminLevel.equals("admin")){
            fragment = new AdminCheckAttendanceFragment();
            fragment.setArguments(str);
            getFragmentManager().beginTransaction().replace(R.id.frame_layout_CheckAttendance,fragment).commit();
        }
        if(Common.CURRENT_USER.AdminLevel.equals("user")){
            fragment = new UserCheckAttendanceFragment();
            fragment.setArguments(str);
            getFragmentManager().beginTransaction().replace(R.id.frame_layout_CheckAttendance,fragment).commit();
        }
    }
}
