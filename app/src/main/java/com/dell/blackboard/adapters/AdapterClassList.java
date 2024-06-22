package com.dell.blackboard.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dell.blackboard.Common;
import com.dell.blackboard.R;
import com.dell.blackboard.activities.CheckAttendanceActivity;
import com.dell.blackboard.model.FeedbackHODModel;
import com.dell.blackboard.objects.ClassObject;
import com.dell.blackboard.view.MainActivityView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.dell.blackboard.Common.CHECK_NEW_COMMENT_POST;
import static com.dell.blackboard.Common.CURRENT_ADMIN_FEEDBACK_STATUS;
import static com.dell.blackboard.Common.CURRENT_CLASS_ID_LIST;
import static com.dell.blackboard.Common.CURRENT_USER;


public class AdapterClassList extends RecyclerView.Adapter<AdapterClassList.MyViewHolder> {

    ArrayList<ClassObject> classObjectArrayList;
    ArrayList<String> percentageList;
    MainActivityView mainActivityView;

    ArrayList<Integer> gradient_color_code_reference;

    Context context;
    int adminLevel;
    public AdapterClassList(ArrayList<ClassObject> classObjectArrayList,
            ArrayList<String> percentageList, MainActivityView view,Context context) {
        this.classObjectArrayList = classObjectArrayList;
        this.percentageList = percentageList;
        this.mainActivityView = view;
        this.context = context;
        Log.v("TAG",CURRENT_USER.AdminLevel);
        if(CURRENT_USER.AdminLevel.equals("admin")){
            adminLevel = 1;
        }
        if(CURRENT_USER.AdminLevel.equals("user")){
            adminLevel = 0;
        }
        if(CURRENT_USER.AdminLevel.equals("master_admin")){
            adminLevel = 2;
        }
        gradient_color_code_reference = new ArrayList<>();
        int k=0;
        for (int i = 0; i<classObjectArrayList.size(); i++){
            gradient_color_code_reference.add(k);
            k++;
            if (k==4){k=0;}
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = null;
        if(adminLevel == 1) {
            view = inflater.inflate(R.layout.admin_classlist_row, viewGroup, false);
        }
        if(adminLevel == 0) {
            view = inflater.inflate(R.layout.user_class_list_row, viewGroup,false);
            Log.v("TAG","Classroom for user");
        }
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        if(adminLevel == 1){
            myViewHolder.tvclassName.setText(classObjectArrayList.get(i).className.toUpperCase());
            myViewHolder.session.
                    setText(classObjectArrayList.get(i).sessionStart + " - " + classObjectArrayList.get(i).sessionEnd);
            myViewHolder.roll.
                    setText(classObjectArrayList.get(i).startRoll + " - " + classObjectArrayList.get(i).endRoll);
            try {
            if (CHECK_NEW_COMMENT_POST.get(i) == 2){
                myViewHolder.img_notif.setImageResource(R.drawable.circle_yellow);
                myViewHolder.img_notif.setVisibility(View.VISIBLE);
            }
            }catch (Exception e){e.printStackTrace();}
            /*if (gradient_color_code_reference.get(i)==0){myViewHolder.linearLayout.setBackgroundResource(R.drawable.gradient_1);}
            if (gradient_color_code_reference.get(i)==1){myViewHolder.linearLayout.setBackgroundResource(R.drawable.gradient_2);}
            if (gradient_color_code_reference.get(i)==2){myViewHolder.linearLayout.setBackgroundResource(R.drawable.gradient_4);}
            if (gradient_color_code_reference.get(i)==3){myViewHolder.linearLayout.setBackgroundResource(R.drawable.gradient_3);}*/
        }
        if(adminLevel == 0) {
            myViewHolder.tvclassName.setText(classObjectArrayList.get(i).className);
            myViewHolder.faculty.setText("Prof : " + classObjectArrayList.get(i).facultyName);
            DecimalFormat df=new DecimalFormat("#.##");
            myViewHolder.attendance.setText(df.format(Double.parseDouble(percentageList.get(i))));
            myViewHolder.session.setText(classObjectArrayList.get(i).
                    sessionStart + " - " + classObjectArrayList.get(i).sessionEnd);
            try {
                if (CHECK_NEW_COMMENT_POST.get(i) == 1) {
                    myViewHolder.img_notif.setVisibility(View.VISIBLE);
                    myViewHolder.img_notif.setImageResource(R.drawable.circle);
                }
                if (CHECK_NEW_COMMENT_POST.get(i) == 2) {
                    myViewHolder.img_notif.setVisibility(View.VISIBLE);
                    myViewHolder.img_notif.setImageResource(R.drawable.circle_yellow);
                }
            }catch (Exception e){ e.printStackTrace(); }
            /*if (gradient_color_code_reference.get(i)==0){myViewHolder.linearLayout.setBackgroundResource(R.drawable.gradient_1);}
            if (gradient_color_code_reference.get(i)==1){myViewHolder.linearLayout.setBackgroundResource(R.drawable.gradient_2);}
            if (gradient_color_code_reference.get(i)==2){myViewHolder.linearLayout.setBackgroundResource(R.drawable.gradient_3);}
            if (gradient_color_code_reference.get(i)==3){myViewHolder.linearLayout.setBackgroundResource(R.drawable.gradient_4);}*/
        }

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.CURRENT_INDEX = i;
                Intent intent = new Intent(view.getContext(), CheckAttendanceActivity.class);
                intent.putExtra("ClassName",myViewHolder.tvclassName.getText());
                view.getContext().startActivity(intent);
            }
        });
        myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.end_session_dialog);
                Button endSession = dialog.findViewById(R.id.bt_faculty_end_session);
                Button addCollabButton = dialog.findViewById(R.id.bt_faculty_add_collab);
                Button transferClass = dialog.findViewById(R.id.bt_faculty_transfer);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Button feedback = dialog.findViewById(R.id.bt_faculty_initiate_feedback);
                if(CURRENT_USER.AdminLevel.equals("user")){
                    addCollabButton.setVisibility(View.GONE);
                    transferClass.setVisibility(View.GONE);
                    feedback.setVisibility(View.GONE);
                }
                if(CURRENT_USER.AdminLevel.equals("admin")){
                    if(!CURRENT_ADMIN_FEEDBACK_STATUS.get(CURRENT_CLASS_ID_LIST.get(i)).equals("null")){
                        feedback.setText("Submit Feedback : "+
                                CURRENT_ADMIN_FEEDBACK_STATUS.get(CURRENT_CLASS_ID_LIST.get(i)));
                    }
                }

                dialog.show();
                final Dialog subDialog = new Dialog(view.getContext());
                addCollabButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        subDialog.setContentView(R.layout.add_hod_admin);
                        subDialog.show();
                        final EditText editText = subDialog.findViewById(R.id.et_addFaculty_email);
                        Button newButton = subDialog.findViewById(R.id.bt_add_new_class_student_popup);
                        newButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mainActivityView.addCollab(i,editText.getText().toString());
                                subDialog.dismiss();
                            }
                        });
                    }
                });
                transferClass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        subDialog.setContentView(R.layout.add_hod_admin);
                        subDialog.show();
                        final EditText editText = subDialog.findViewById(R.id.et_addFaculty_email);
                        Button newButton = subDialog.findViewById(R.id.bt_add_new_class_student_popup);
                        newButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mainActivityView.transferClass(i,editText.getText().toString());
                                subDialog.dismiss();
                            }
                        });
                    }
                });
                endSession.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        new AlertDialog.Builder(view.getContext())
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Are You Sure?")
                                .setMessage("Once Session End, it CANNOT be reversed")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                     mainActivityView.endSession(i);
                                    }
                                }).setNegativeButton("No",null).show();
                        dialog.dismiss();
                    }
                });
                feedback.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        FeedbackHODModel hodModel = new FeedbackHODModel();
                        if(CURRENT_ADMIN_FEEDBACK_STATUS.get(CURRENT_CLASS_ID_LIST.get(i)).equals("null")){
                            final Dialog emailDialog = new Dialog(view.getContext());
                            emailDialog.setContentView(R.layout.hod_email_dialog);
                            emailDialog.show();
                            EditText et_email_dialog = emailDialog.findViewById(R.id.et_hod_email_dialog);
                            Button bt_email_dialog = emailDialog.findViewById(R.id.bt_hod_email_dialog);
                            bt_email_dialog.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if (!et_email_dialog.getText().toString().equals("")) {
                                        hodModel.performInitiateFeedback(
                                                et_email_dialog.getText().toString(),
                                                classObjectArrayList.get(i).className,
                                                classObjectArrayList
                                                        .get(i).sessionStart + " : " + classObjectArrayList.get(i).sessionEnd,
                                                CURRENT_USER.Name, CURRENT_CLASS_ID_LIST.get(i));
                                        emailDialog.dismiss();
                                        Toast.makeText(context,"Feed Back Initiated",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else{
                            hodModel.performFeedbackSubmitAdmin(CURRENT_CLASS_ID_LIST.get(i));
                        }
                    }
                });
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return classObjectArrayList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvclassName, session, roll,attendance,faculty;
        ImageView img_notif;
        //LinearLayout linearLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            if(adminLevel == 1){
                tvclassName = itemView.findViewById(R.id.tv_name_faculty_main_recycler);
                session = itemView.findViewById(R.id.tv_session_faculty_main_recycler);
                roll = itemView.findViewById(R.id.tv_student_roll_faculty_main_recycler);
                img_notif = itemView.findViewById(R.id.img_notif_admin_classlist_row);
                //linearLayout = itemView.findViewById(R.id.linearLayout_admin_class_row);
            }
            if(adminLevel == 0){
                //linearLayout = itemView.findViewById(R.id.linearLayout_admin_class_row);
                tvclassName = itemView.findViewById(R.id.tv_user_row_className);
                session = itemView.findViewById(R.id.tv_session_student_main_recycler_row);
                attendance = itemView.findViewById(R.id.tv_attendance_student_main_recycler_row);
                faculty = itemView.findViewById(R.id.tv_facultyname_student_main_recycler_row);
                img_notif = itemView.findViewById(R.id.img_newPost_userClassList_row);
            }
        }
    }



}
