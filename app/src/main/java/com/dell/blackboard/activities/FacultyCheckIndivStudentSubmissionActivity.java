package com.dell.blackboard.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.dell.blackboard.R;
import com.dell.blackboard.adapters.AdapterAssignmentViewerInfoFrag;
import com.dell.blackboard.model.AssignmentModel;
import com.dell.blackboard.objects.AssignmentSubmissionObject;
import com.dell.blackboard.view.AssignmentViewerSubmissionFragView;

import java.util.ArrayList;

import static com.dell.blackboard.Common.ASSIGNMENT_OBJECT_TEMP;
import static com.dell.blackboard.Common.CURRENT_CLASS_ID_LIST;
import static com.dell.blackboard.Common.CURRENT_INDEX;
import static com.dell.blackboard.Common.SELECTED_ASSIGNMENT_ID;
import static com.dell.blackboard.Common.SELECTED_CHAT_UID;

public class FacultyCheckIndivStudentSubmissionActivity extends AppCompatActivity implements AssignmentViewerSubmissionFragView {
    private GridView gridView;
    AssignmentModel assignmentModel;
    private AdapterAssignmentViewerInfoFrag adapterAssignmentViewerInfoFrag;
    Button bt_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_check_indiv_student_submission);
        gridView = findViewById(R.id.gridViewAssignmentViewerFacultyCheckSubmission);
        assignmentModel = new AssignmentModel(this);
        String roll = getIntent().getStringExtra("roll");
        assignmentModel.downloadAssignmentSubmissionCheckFaculty(SELECTED_ASSIGNMENT_ID, CURRENT_CLASS_ID_LIST.get(CURRENT_INDEX),roll);
        bt_chat = findViewById(R.id.bt_chat_assignmentSubmission);
        bt_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("suffix",ASSIGNMENT_OBJECT_TEMP.getTitle());
                startActivity(intent);
            }
        });
    }

    @Override
    public void downloadSuccessStudent(ArrayList<AssignmentSubmissionObject> objectArrayList) {
        SELECTED_CHAT_UID = objectArrayList.get(0).student_uid;
        adapterAssignmentViewerInfoFrag = new AdapterAssignmentViewerInfoFrag(objectArrayList,this);
        gridView.setAdapter(adapterAssignmentViewerInfoFrag);
    }

    @Override
    public void downloadFailed() {
        Toast.makeText(this,"Try Again",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void downloadSuccessSubmissionStudentList(ArrayList<String> roll_list, ArrayList<String> name_list) {

    }
}
