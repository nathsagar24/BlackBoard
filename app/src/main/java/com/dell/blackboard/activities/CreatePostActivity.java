package com.dell.blackboard.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Fragment;

import android.os.Bundle;
import android.util.Log;

import com.dell.blackboard.R;
import com.dell.blackboard.fragments.CreatePostFragment;

import java.util.Objects;

public class CreatePostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_attendance);
        Fragment fragment;
        Bundle str=getIntent().getExtras();
        assert str != null;
        Log.d("Extra",str.getString("Createpost"));
        if(str.getString("Createpost").equals("createpost")){
            Log.d("LOG","HereInPostAct");
            fragment=new CreatePostFragment();
            getFragmentManager().beginTransaction().replace(R.id.frame_layout_create_post,fragment).commit();
        }
        else if(Objects.equals(getIntent().getExtras(), "createpoll")){

        }
    }
}
