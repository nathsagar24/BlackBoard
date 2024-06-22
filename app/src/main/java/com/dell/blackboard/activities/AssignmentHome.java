package com.dell.blackboard.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.dell.blackboard.R;
import com.dell.blackboard.adapters.AdapterAssignmentHome;
import com.dell.blackboard.model.AssignmentModel;
import com.dell.blackboard.objects.AssignmentObject;
import com.dell.blackboard.view.AssignmentHomeView;

import java.util.ArrayList;

import static com.dell.blackboard.Common.CURRENT_CLASS_ID_LIST;
import static com.dell.blackboard.Common.CURRENT_INDEX;
import static com.dell.blackboard.Common.CURRENT_USER;

public class AssignmentHome extends AppCompatActivity implements AssignmentHomeView {

    FloatingActionButton fab_new_assignmnents;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_home_admin);
        final ActionBar abar = getSupportActionBar();
        if(abar!=null) {
            abar.setDisplayHomeAsUpEnabled(true);
            abar.setTitle("Assignments");
        }
        recyclerView=findViewById(R.id.rv_assignmentHome_admin);

        fab_new_assignmnents = findViewById(R.id.fab_new_assignments_admin);
        if (CURRENT_USER.AdminLevel.equals("user")){
            fab_new_assignmnents.setVisibility(View.GONE);
        }
        fab_new_assignmnents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AssignmentCreationActivity.class);
                intent.putExtra("status","create");
                startActivity(intent);
            }
        });
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return  false;
    }
    @Override
    protected void onStart() {
        super.onStart();
        AssignmentModel model = new AssignmentModel(this);
        model.downloadAssignmentList(CURRENT_CLASS_ID_LIST.get(CURRENT_INDEX));
    }

    @Override
    public void assignmentDownloadSuccess(ArrayList<AssignmentObject> assignmentObjects) {
        AdapterAssignmentHome adapterAssignmentHome = new AdapterAssignmentHome(assignmentObjects,this);
        recyclerView.setAdapter(adapterAssignmentHome);
    }

    @Override
    public void assignmentDownloadFailed() {
        Toast.makeText(this,"Try Again",Toast.LENGTH_SHORT).show();
    }
}
