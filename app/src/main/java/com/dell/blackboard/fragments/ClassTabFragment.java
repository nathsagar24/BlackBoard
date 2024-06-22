package com.dell.blackboard.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.dell.blackboard.activities.AdminFeedbackActivity;
import com.dell.blackboard.adapters.AdapterClassList;
import com.dell.blackboard.objects.ClassObject;
import com.dell.blackboard.Common;
import com.dell.blackboard.R;
import com.dell.blackboard.activities.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.dell.blackboard.Common.ADAPTER_CLASS_LIST;
import static com.dell.blackboard.Common.CURRENT_USER;

public class ClassTabFragment extends Fragment  {

    RecyclerView recyclerView;

    FloatingActionButton floatingActionButton1,floatingActionButton2,floatingActionButton3;
    Dialog addClassDialog;
    public ClassTabFragment() {

    }

    @SuppressLint("WrongConstant")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myclass,container,false);
        floatingActionButton1 = view.findViewById(R.id.fab_addclass_sub);
        floatingActionButton2 = view.findViewById(R.id.fab_connection_sub);
        floatingActionButton3 = view.findViewById(R.id.fab_admin_feedback);
        recyclerView = view.findViewById(R.id.recyclerView_ClassList);
        Log.v("TAG","Creating Class Tab Fragment");

        if(Common.CURRENT_USER.AdminLevel.equals("admin")){
            ADAPTER_CLASS_LIST = new AdapterClassList(Common.CURRENT_ADMIN_CLASS_LIST,null,(MainActivity)getActivity(),view.getContext());
        }
        if(Common.CURRENT_USER.AdminLevel.equals("user")){
            floatingActionButton3.setVisibility(View.GONE);
            floatingActionButton1.setVisibility(View.GONE);
            Log.v("TAG","Initialising Adapter Class List");
            ADAPTER_CLASS_LIST = new AdapterClassList(Common.CURRENT_ADMIN_CLASS_LIST,Common.ATTD_PERCENTAGE_LIST,(MainActivity)getActivity(),view.getContext());
        }
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(ADAPTER_CLASS_LIST);
        if(!CURRENT_USER.AdminLevel.equals("user")){
            floatingActionButton2.setVisibility(View.GONE);
        }

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CURRENT_USER.AdminLevel.equals("admin")){
                    addClassDialog = new Dialog(getContext());
                    addClassDialog.setContentView(R.layout.add_class_admin);
                    final EditText className, sessionStart, sessionEnd, startRoll, endRoll;
                    Button addClass = addClassDialog.findViewById(R.id.bt_add_class);
                    className = addClassDialog.findViewById(R.id.et_class_name);
                    sessionStart = addClassDialog.findViewById(R.id.et_session_start);
                    sessionEnd = addClassDialog.findViewById(R.id.et_session_end);
                    startRoll = addClassDialog.findViewById(R.id.et_roll_start);
                    endRoll = addClassDialog.findViewById(R.id.et_roll_end);
                    addClassDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    addClassDialog.show();
                    sessionDatePick(sessionStart);
                    sessionDatePick(sessionEnd);
                    addClass.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ClassObject classObject = new ClassObject(CURRENT_USER.Name,CURRENT_USER.Email,
                                    CURRENT_USER.UID,className.getText().toString(), sessionStart.getText().toString(),
                                    sessionEnd.getText().toString(), startRoll.getText().toString(),
                                    endRoll.getText().toString());
                            if (classObject.sessionEnd.equals("") || classObject.className.equals("") ||
                                    classObject.sessionStart.equals("") ||
                                    classObject.startRoll.equals("") || classObject.endRoll.equals("")){

                            }
                            else{
                                MainActivity.presenter.performAdminAddClass(classObject);
                                addClassDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CURRENT_USER.AdminLevel.equals("user")){
                    MainActivity.presenter.performConnectionDownload();
                }
            }
        });
        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AdminFeedbackActivity.class);
                startActivity(intent);
            }
        });
        return view;
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
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

}
