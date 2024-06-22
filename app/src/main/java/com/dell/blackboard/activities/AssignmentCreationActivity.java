package com.dell.blackboard.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dell.blackboard.R;
import com.dell.blackboard.adapters.AdapterCreateAssignment;
import com.dell.blackboard.model.AssignmentModel;
import com.dell.blackboard.view.AssignmentCreationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.dell.blackboard.Common.ASSIGNMENT_OBJECT_TEMP;

public class AssignmentCreationActivity extends AppCompatActivity implements AssignmentCreationView {

    EditText et_title,et_description,et_due_date;
    Button bt_create;
    ImageButton ib_attachment;
    RecyclerView rv_attachmentList;
    AdapterCreateAssignment adapter;
    private static final int PICK_FILE_REQUEST = 1;
    ArrayList<Uri> attachmentUriList;
    ProgressDialog progressDialog;
    String status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_creation);
        final ActionBar abar = getSupportActionBar();
        if(abar!=null) {
            abar.setDisplayHomeAsUpEnabled(true);
            abar.setTitle("Create Assignment");
        }
        status ="";
        status = getIntent().getStringExtra("status");
        et_title = findViewById(R.id.et_title_assignmentCreation);
        et_description = findViewById(R.id.et_description_assignmentCreation);
        et_due_date = findViewById(R.id.et_dueDate_assignmentCreation);
        bt_create = findViewById(R.id.bt_assignmentCreationDone);
        rv_attachmentList = findViewById(R.id.rv_createAssignment);
        ib_attachment = findViewById(R.id.ib_addAttachment_assignmentCreation);
        attachmentUriList = new ArrayList<>();
        adapter = new AdapterCreateAssignment(attachmentUriList,this);
        LinearLayoutManager llm = new LinearLayoutManager(AssignmentCreationActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_attachmentList.setLayoutManager(llm);
        rv_attachmentList.setAdapter(adapter);
        ib_attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser(PICK_FILE_REQUEST);
            }
        });
        sessionDatePick(et_due_date);
        bt_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AssignmentModel assignmentModel = new AssignmentModel(AssignmentCreationActivity.this,getApplicationContext());
                if (!TextUtils.isEmpty(et_title.getText().toString())){
                    progressDialog = new ProgressDialog(AssignmentCreationActivity.this);
                    progressDialog.show();
                    if (status.equals("edit")) {
                        assignmentModel.performAssignmentCreation(et_title.getText().toString(), et_description.getText().toString(),
                                et_due_date.getText().toString(), attachmentUriList, ASSIGNMENT_OBJECT_TEMP.getAssignmentid());
                    }else{
                        assignmentModel.performAssignmentCreation(et_title.getText().toString(), et_description.getText().toString(),
                                et_due_date.getText().toString(), attachmentUriList, null);
                    }
                }
                else{et_title.setError("Please Give a Title"); }
            }
        });
        if (status.equals("edit")){
            bt_create.setText("Edit Done");
            et_title.setText(ASSIGNMENT_OBJECT_TEMP.getTitle());
            et_due_date.setText(ASSIGNMENT_OBJECT_TEMP.getDueDate());
            et_description.setText(ASSIGNMENT_OBJECT_TEMP.getDescription());
        }else{
            TextView textView = findViewById(R.id.tv_warning_assignmentCreation);
            textView.setVisibility(View.GONE);
        }


    }

    private void openFileChooser(int imgRQST){
        Intent intent = getFileChooserIntent();
        startActivityForResult(intent,imgRQST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == -1 && data != null && data.getData() != null){
            attachmentUriList.add(data.getData());
            adapter.notifyDataSetChanged();
        }

    }

    private Intent getFileChooserIntent() {
        String[] mimeTypes = {"image/*", "application/pdf"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";

            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }

            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }

        return intent;
    }

    private void sessionDatePick(final EditText editText){
        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                editText.setText(sdf.format(myCalendar.getTime()));
            }

        };

        editText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(AssignmentCreationActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    @Override
    public void assignmentCreationSuccess() {
        progressDialog.dismiss();
        finish();
    }

    @Override
    public void assignmentCreationFailed() {
        Toast.makeText(this,"Try Again",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return  false;
    }
}
