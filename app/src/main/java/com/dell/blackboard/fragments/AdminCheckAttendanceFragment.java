package com.dell.blackboard.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.dell.blackboard.activities.AssignmentHome;
import com.dell.blackboard.activities.ResourceBucketActivity;
import com.dell.blackboard.activities.ShowAttendanceActivity;
import com.dell.blackboard.activities.StudentListActivity;
import com.dell.blackboard.adapters.AdapterPostLoad;
import com.dell.blackboard.objects.PollOptionValueLikeObject;
import com.dell.blackboard.objects.PostObject;
import com.dell.blackboard.R;
import com.dell.blackboard.activities.CreatePollActivity;
import com.dell.blackboard.activities.NewAttendenceAcitivity;
import com.dell.blackboard.presenter.CheckAttendancePresenter;
import com.dell.blackboard.view.CheckAttendanceFragView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.dell.blackboard.Common.CURRENT_CLASS_ID_LIST;
import static com.dell.blackboard.Common.CURRENT_INDEX;
import static com.dell.blackboard.Common.CURRENT_USER;
import static com.dell.blackboard.Common.NEW_ATTENDANCE;
import static com.dell.blackboard.Common.SHOW_ATTENDANCE_PERCENTAGE;
import static com.dell.blackboard.Common.mREF_RESOURCE_BUCKET;
import static com.dell.blackboard.Common.mREF_classList;


public class AdminCheckAttendanceFragment extends Fragment implements CheckAttendanceFragView {
    ProgressDialog progressDialog,progressDialogPosting,uploadDialog;
    CheckAttendancePresenter presenter;
    Dialog dialog, postDialog;
    boolean notMultipleAttendance = false;
    String type = "";
    Uri imgURI01,imgURI02,imgURI03, fileURI;
    Button uploadFile;
    ImageButton pickImage,pickFile;
    ImageView img1,img2,img3;
    String fileUploadLink = "";
    TextView noPostMsg,toolbarText;
    int temp;
    private static final int PICK_IMAGE_REQUEST01 = 1,PICK_IMAGE_REQUEST02 = 2, PICK_IMAGE_REQUEST03 = 3,
                        PICK_FILE_REQUEST = 4;

    RecyclerView recyclerViewPost;
    FloatingActionButton fab_exportcsv,fab_create_poll,fab_new_attendance,fab_assignments,
            fab_show_attendace_percent,fab_createPost,fab_show_bucket,fab_addStudent;
    FloatingActionsMenu floatingActionsMenu;
    public AdminCheckAttendanceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v=inflater.inflate(R.layout.admin_check_attendance, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        Toolbar toolbar = v.findViewById(R.id.toolbar_admin);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        String className = getArguments().getString("ClassName");
        toolbarText=v.findViewById(R.id.toolbar_text_admin);
        toolbarText.setText(className);
        noPostMsg=v.findViewById(R.id.no_post_msg);
        fab_create_poll = v.findViewById(R.id.fab_create_poll);
        fab_exportcsv = v.findViewById(R.id.fab_export_csv);
        fab_new_attendance = v.findViewById(R.id.fab_new_attendance);
        fab_assignments = v.findViewById(R.id.fab_assignments_faculty);
/*
        fab_notify = v.findViewById(R.id.fab_notify);
*/
        fab_show_attendace_percent = v.findViewById(R.id.fab_show_attendnace_list);
        fab_createPost = v.findViewById(R.id.fab_post);
        fab_show_bucket = v.findViewById(R.id.fab_res_bucket);
        fab_addStudent = v.findViewById(R.id.fab_student_list);
        floatingActionsMenu=v.findViewById(R.id.fab_check_attendance);
        presenter = new CheckAttendancePresenter(this);
        presenter.performDeleteRead(CURRENT_CLASS_ID_LIST.get(CURRENT_INDEX));
        presenter.performAdminAttendanceDataDownload();
        presenter.performLoadPost(CURRENT_CLASS_ID_LIST.get(CURRENT_INDEX));
        progressDialog = ProgressDialog.show(getContext(), "",
                "Loading. Please wait...", true);
       //presenter.performMultipleAttendanceCheck();
        recyclerViewPost = v.findViewById(R.id.recyclerView_check_attendance_post);

        fab_new_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NEW_ATTENDANCE = true;
                Intent intent = new Intent(v.getContext(), NewAttendenceAcitivity.class);
                startActivity(intent);
            }
        });

        fab_exportcsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog exportDialog = new Dialog(getContext());
                exportDialog.setContentView(R.layout.export_csv_dialog);
                EditText et_date1,et_date2;
                Button bt_export;
                et_date1 = exportDialog.findViewById(R.id.et_date1_exportcsv);
                et_date2 = exportDialog.findViewById(R.id.et_date2_exportcsv);
                bt_export = exportDialog.findViewById(R.id.bt_exportcsv);
                sessionDatePick(et_date1);
                sessionDatePick(et_date2);
                exportDialog.show();
                bt_export.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        exportDialog.dismiss();
                        if (!TextUtils.isEmpty(et_date1.getText().toString()) && !TextUtils.isEmpty(et_date2.getText().toString())){
                            presenter.performExportCSV(et_date1.getText().toString(),et_date2.getText().toString());
                        }
                    }
                });
            }
        });

       /* fab_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.admin_notify_dialog);
                final EditText broadcastTitle,broadcast,date01,date02;
                LinearLayout datelayout;
                RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup_admin_notify);
                broadcast = dialog.findViewById(R.id.et_broadcast_message);
                broadcastTitle = dialog.findViewById(R.id.et_broadcastTitle);
                datelayout=dialog.findViewById(R.id.dateLayout);
                date01 = dialog.findViewById(R.id.et_date01);
                date02 = dialog.findViewById(R.id.et_date02);
                sessionDatePick(date01);
                sessionDatePick(date02);

                Button bt_done,bt_cancel;
                bt_done= dialog.findViewById(R.id.bt_notify_broadcast);
                bt_cancel=dialog.findViewById(R.id.bt_cancelBroadcast);
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        int id = radioGroup.getCheckedRadioButtonId();
                        switch (id){
                            case R.id.rb_broadcast:
                                type = "Broadcast";
                                broadcastTitle.setVisibility(View.VISIBLE);
                                broadcastTitle.setEnabled(true);
                                broadcast.setVisibility(View.VISIBLE);
                                date01.setVisibility(View.GONE);
                                date02.setVisibility(View.GONE);
                                break;
                            case R.id.rb_classCancel:
                                type = "Class_Cancel";
                                broadcastTitle.setText(type);
                                broadcastTitle.setEnabled(false);
                                broadcastTitle.setTextColor(Color.BLACK);
                                datelayout.setVisibility(View.VISIBLE);
                                date01.setVisibility(View.VISIBLE);
                                date02.setVisibility(View.VISIBLE);
                                broadcast.setVisibility(View.GONE);
                                break;
                            case R.id.rb_classShift:
                                type = "Class_Shifted";
                                broadcastTitle.setText(type);
                                broadcastTitle.setEnabled(false);
                                broadcastTitle.setTextColor(Color.BLACK);
                                datelayout.setVisibility(View.VISIBLE);
                                date01.setVisibility(View.VISIBLE);
                                date02.setVisibility(View.VISIBLE);
                                broadcast.setVisibility(View.GONE);
                                break;
                            default:
                                type = "Broadcast";
                                broadcastTitle.setVisibility(View.VISIBLE);
                                broadcast.setVisibility(View.VISIBLE);
                                date01.setVisibility(View.GONE);
                                date02.setVisibility(View.GONE);
                                broadcastTitle.setEnabled(true);
                                break;
                        }

                    }
                });
                bt_done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        presenter.performBroadcast(type,broadcastTitle.getText().toString(),
                                broadcast.getText().toString(),
                                date01.getText().toString(),date02.getText().toString());
                    }
                });
                //Added cancel BroadCast Button

                bt_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                //
                dialog.show();
            }
        });*/

        fab_create_poll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CreatePollActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        fab_show_attendace_percent.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ShowAttendanceActivity.class));

            }
        });

        fab_createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPost();
            }
        });

        fab_show_bucket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ResourceBucketActivity.class);
                mREF_RESOURCE_BUCKET = mREF_classList.child(CURRENT_CLASS_ID_LIST.get(CURRENT_INDEX))
                        .child("resource_bucket");
                startActivity(intent);
            }
        });

        fab_addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), StudentListActivity.class));
            }
        });

        fab_assignments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AssignmentHome.class));
            }
        });

        return v;
    }


    @SuppressLint("WrongConstant")
    @Override
    public void success(ArrayList<Double> attendancePercentageList) {
        SHOW_ATTENDANCE_PERCENTAGE = attendancePercentageList;
        progressDialog.dismiss();
    }

    @Override
    public void failed() {
        new AlertDialog.Builder(getContext())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Server Unreachable")
                .setMessage("Try Again after Some time")
                .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                }).show();
    }

    @Override
    public void exportCsvSuccess() {
        Toast.makeText(getContext(),"Export CSV DONE",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void exportCsvFailed() {
        Toast.makeText(getContext(),"Export CSV FAILED",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void notifySuccess() {
        Toast.makeText(getActivity(),"Success", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    @Override
    public void notifyFailed() {
        Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    @Override
    public void postingSuccess() {
        postDialog.dismiss();
        progressDialogPosting.dismiss();
        imgURI03 =null;
        imgURI01 = null;
        imgURI02 = null;
        Toast.makeText(getActivity(),"Posted !", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void postingFailed() {
        Toast.makeText(getActivity(),"Posting Failed",Toast.LENGTH_SHORT).show();
        postDialog.dismiss();
        progressDialogPosting.dismiss();
    }

    @Override
    public void checkAttendanceReturn(boolean b) {
        notMultipleAttendance = b;
    }

    @Override
    public void fileUploadDone(String link) {
        fileUploadLink = link;
        uploadFile.setVisibility(View.VISIBLE);
        pickFile.setEnabled(false);
        uploadDialog.dismiss();
    }

    @SuppressLint("WrongConstant")
    @Override
    public void loadPostSuccess(ArrayList<PostObject> postObjects,
                                HashMap<String, PollOptionValueLikeObject> post_poll_option,
                                ArrayList<String> post_like_list, HashMap<String,
            ArrayList<String>> post_url_list, ArrayList<String> comment_count,
                                ArrayList<String> likedPostID,
            HashMap<String,String> postPollSelect) {
        Collections.reverse(postObjects);
        Collections.reverse(post_like_list);
        Collections.reverse(comment_count);
        Collections.reverse(likedPostID);
        ArrayList<PostObject> postlist = postObjects;
        AdapterPostLoad adapterPostLoad =
                new AdapterPostLoad(postlist,
                        post_poll_option,
                        post_like_list,
                        post_url_list,comment_count,
                        getContext(), likedPostID,postPollSelect);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewPost.getContext(),
//                llm.getOrientation());
//        recyclerViewPost.addItemDecoration(dividerItemDecoration);
        recyclerViewPost.setLayoutManager(llm);
        recyclerViewPost.setAdapter(adapterPostLoad);
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
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void createPost(){
        temp = 0;
        postDialog = new Dialog(getContext());
        postDialog.setContentView(R.layout.create_post_dialog);
        Button postDone = postDialog.findViewById(R.id.bt_create_post);
        uploadFile = postDialog.findViewById(R.id.bt_create_post_upload_file);

        pickFile = postDialog.findViewById(R.id.img_uploadFile);
        pickImage = postDialog.findViewById(R.id.img_uploadImage);

        img1 = postDialog.findViewById(R.id.img_create_post_01);
        img2 = postDialog.findViewById(R.id.img_create_post_02);
        img3 = postDialog.findViewById(R.id.img_create_post_03);
        EditText et_post_content = postDialog.findViewById(R.id.et_create_post_body);
        postDialog.show();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a MMM d, ''yy");
        String simpleTime = simpleDateFormat.format(new Date());

        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgURI01 == null){
                    openFileChooser(PICK_IMAGE_REQUEST01);
                }else{
                    if (imgURI02 == null){
                        openFileChooser(PICK_IMAGE_REQUEST02); temp++;
                    }else {
                        if (imgURI03 == null){
                            openFileChooser(PICK_IMAGE_REQUEST03); temp++;
                        }else{
                            imgURI03 =null;
                            imgURI01 = null;
                            imgURI02 = null;
                            img1.setVisibility(View.INVISIBLE);
                            img2.setVisibility(View.INVISIBLE);
                            img3.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(),"Max 3 Image",Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });
        pickFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser(PICK_FILE_REQUEST);
            }
        });
        postDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!et_post_content.getText().toString().equals("") && et_post_content.getText().toString() != null){
                    ArrayList<Uri> imgURILIST = new ArrayList<>();
                    ArrayList<String> extensionList = new ArrayList<>();
                    imgURILIST.clear();
                    extensionList.clear();
                    PostObject postObject;
                    if (imgURI01 != null){imgURILIST.add(imgURI01); extensionList.add(getExtension(imgURI01));}
                    if (imgURI02 != null){imgURILIST.add(imgURI02); extensionList.add(getExtension(imgURI02));}
                    if (imgURI03 != null){imgURILIST.add(imgURI03); extensionList.add(getExtension(imgURI03));}
                    if(fileUploadLink.equals("")){
                        postObject = new PostObject(
                                CURRENT_USER.Name,simpleTime,
                                et_post_content.getText().toString(),"simple_admin_post",
                                CURRENT_USER.UID,CURRENT_USER.profilePicLink,null);
                    }
                    else {
                        postObject = new PostObject(
                                CURRENT_USER.Name,simpleTime,
                                et_post_content.getText().toString()+". Link: "+fileUploadLink,"simple_admin_post",
                                CURRENT_USER.UID,CURRENT_USER.profilePicLink,null);
                    }
                    progressDialogPosting = ProgressDialog.show(getContext(), "",
                            "Posting. Please wait...", true);

                    if (imgURILIST.size() > 0) {
                        presenter.performPosting(postObject, imgURILIST, extensionList);
                    } else {
                        presenter.performPosting(postObject, null, null);
                    }

                }
                else{
                    Toast.makeText(getActivity(),"Cannot Have Empty Post !",Toast.LENGTH_SHORT).show();
                    postDialog.dismiss();
                }
            }
        });

    }

    private void openFileChooser(int imgRQST){
        Intent intent = new Intent();
        if (imgRQST == PICK_FILE_REQUEST){
            intent.setType("application/pdf");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,imgRQST);
        }
        else {
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,imgRQST);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST01 && resultCode == -1 && data != null && data.getData() != null){
            imgURI01 = data.getData();
            img1.setImageURI(imgURI01);
            img1.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(imgURI01).into(img1);
        }
        if (requestCode == PICK_IMAGE_REQUEST02 && resultCode == -1 && data != null && data.getData() != null){
            imgURI02 = data.getData();
            img2.setImageURI(imgURI02);
            img2.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(imgURI02).into(img2);
        }
        if (requestCode == PICK_IMAGE_REQUEST03 && resultCode == -1 && data != null && data.getData() != null){
            imgURI03 = data.getData();
            img3.setImageURI(imgURI03);
            img3.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(imgURI03).into(img3);
        }
        if (requestCode == PICK_FILE_REQUEST && resultCode == -1 && data != null && data.getData() != null){
            fileURI = data.getData();
            uploadDialog = ProgressDialog.show(getContext(), "",
                    "Uploading PDF. Please wait...", true);
            presenter.performPostFileUpload(fileURI,getExtension(fileURI));
        }

    }

    private String getExtension(Uri uri){
        ContentResolver cr = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

}
