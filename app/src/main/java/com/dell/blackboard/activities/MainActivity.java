package com.dell.blackboard.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.dell.blackboard.adapters.AdapterConnectionList;
import com.dell.blackboard.adapters.AdapterPagerView;
import com.dell.blackboard.fragments.ChatListDialogFragment;
import com.dell.blackboard.model.ChatListModel;
import com.dell.blackboard.model.LiveMainModel;
import com.dell.blackboard.objects.ChatListMasterObject;
import com.dell.blackboard.objects.ClassObject;
import com.dell.blackboard.R;
import com.dell.blackboard.objects.UserObject;
import com.dell.blackboard.presenter.MainPresenter;
import com.dell.blackboard.view.MainActivityView;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.dell.blackboard.Common.ADAPTER_CLASS_LIST;
import static com.dell.blackboard.Common.ATTD_PERCENTAGE_LIST;
import static com.dell.blackboard.Common.CHAT_LIST_HASH_MAP;
import static com.dell.blackboard.Common.CHECK_NEW_COMMENT;
import static com.dell.blackboard.Common.CHECK_NEW_COMMENT_POST;
import static com.dell.blackboard.Common.CURRENT_ADMIN_CLASS_LIST;
import static com.dell.blackboard.Common.CURRENT_CLASS_ID_LIST;
import static com.dell.blackboard.Common.CURRENT_USER;
import static com.dell.blackboard.Common.CURRENT_USER_CLASS_LIST;
import static com.dell.blackboard.Common.CURRENT_USER_CLASS_LIST_ID;
import static com.dell.blackboard.Common.FIREBASE_DATABASE;
import static com.dell.blackboard.Common.FIREBASE_USER;
import static com.dell.blackboard.Common.HELLO_REQUEST_USERS;
import static com.dell.blackboard.Common.ROLL_LIST;
import static com.dell.blackboard.Common.SELECTED_PROFILE_UID;
import static com.dell.blackboard.Common.SHARED_PREFERENCES;
import static com.dell.blackboard.Common.TEMP01_LIST;
import static com.dell.blackboard.Common.mAUTH;
import static com.dell.blackboard.Common.mREF_admin_classList;
import static com.dell.blackboard.Common.mREF_chat;
import static com.dell.blackboard.Common.mREF_classList;
import static com.dell.blackboard.Common.mREF_connections;
import static com.dell.blackboard.Common.mREF_oldRecords;
import static com.dell.blackboard.Common.mREF_student_classList;
import static com.dell.blackboard.Common.mREF_users;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainActivityView {

    public static MainPresenter presenter;
    NavigationView navigationView;
    ProgressDialog progressDialog;
    View headerView;
    Dialog connectionListDialog;
    AdapterPagerView adapterPagerView;
    TabLayout tabLayout;
    ViewPager viewPager;
    Menu menu;
    boolean flag;
    ChatListModel chatListModel;
    DrawerLayout drawer;
    ChatListDialogFragment chatListDialogFragment;
    CircleImageView circleImageView;
    ChatListMasterObject chatListMasterObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        closeDrawer();
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);
        headerView.findViewById(R.id.headerImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ProfileNewActivity.class);
                SELECTED_PROFILE_UID = CURRENT_USER.UID;
                Toast.makeText(MainActivity.this, "Work is in Progress, Please Go back", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                closeDrawer();
                finish();
            }
        });
        viewPager = findViewById(R.id.fragment_container);
        adapterPagerView = new AdapterPagerView(getSupportFragmentManager());
        tabLayout = findViewById(R.id.tabbar);
        tabLayout.setupWithViewPager(viewPager);

    }


    @Override
    public void onBackPressed() {
        closeDrawer();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_signout) {
            mREF_users.child(mAUTH.getUid()).child("token").setValue("");
            mAUTH.signOut();
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        if (id == R.id.action_helloList){
            Intent intent = new Intent(getApplicationContext(),HelloActivity.class);
            if (HELLO_REQUEST_USERS.isEmpty()){
                intent.putExtra("tab",0);
            }
            else {
                intent.putExtra("tab",1);
            }
            startActivity(intent);
        }
        if (id == R.id.action_chatList){
            CHAT_LIST_HASH_MAP = chatListModel.getChatListMasterObject();
            chatListDialogFragment = ChatListDialogFragment.newInstance("Chat List");
            //chatListDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);
            FragmentManager fm = getSupportFragmentManager();
            menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_chat_bubble_outline_white_24dp));
            chatListDialogFragment.show(fm, "Chat List");
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawer.closeDrawers();
        int id = item.getItemId();
        if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        else if(id==R.id.nav_about){
            startActivity(new Intent(this, AboutActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean hasPermission = (ContextCompat.checkSelfPermission(getBaseContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            if (!hasPermission) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        },1);
            }
        }

        //Variable Initialization
        mAUTH = FirebaseAuth.getInstance();
        FIREBASE_DATABASE = FirebaseDatabase.getInstance();

        mREF_users = FIREBASE_DATABASE.getReference("users");
        mREF_connections = FIREBASE_DATABASE.getReference("connections");
        mREF_classList = FIREBASE_DATABASE.getReference("class_list");
        mREF_oldRecords = FIREBASE_DATABASE.getReference("old_records");
        mREF_chat = FIREBASE_DATABASE.getReference("chat");
        mREF_users.keepSynced(true);
        mREF_classList.keepSynced(true);
        mREF_connections.keepSynced(true);
        CURRENT_CLASS_ID_LIST = new ArrayList<>();
        CURRENT_ADMIN_CLASS_LIST = new ArrayList<>();
        ATTD_PERCENTAGE_LIST = new ArrayList<>();
        TEMP01_LIST = new ArrayList<>();
        ROLL_LIST = new ArrayList<>();
        CURRENT_USER_CLASS_LIST = new ArrayList<>();
        CURRENT_USER_CLASS_LIST_ID = new ArrayList<>();
        HELLO_REQUEST_USERS = new HashMap<>();
        CHECK_NEW_COMMENT_POST = new ArrayList<>();
        CHECK_NEW_COMMENT = new HashMap<>();
        //Logged in Check and perform DataLoad
        FIREBASE_USER = mAUTH.getCurrentUser();
        FirebaseUser currentUser = mAUTH.getCurrentUser();
        if(currentUser == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }else {
            mREF_student_classList = mREF_users.child(mAUTH.getUid()).child("class_list"); //FIREBASE_DATABASE.getReference("student_class_list");
            mREF_admin_classList = mREF_users.child(mAUTH.getUid()).child("class_list");

            SHARED_PREFERENCES = getSharedPreferences("", Context.MODE_PRIVATE);


            presenter = new MainPresenter(this);
            progressDialog = ProgressDialog.show(MainActivity.this, "",
                    "Loading. Please wait...", true);
            presenter.performDataDownload();
        }
    }

    @Override
    public void downloadDataSuccess() {
        TextView textViewName = headerView.findViewById(R.id.tv_header_title);
        circleImageView = headerView.findViewById(R.id.headerImage);
        Menu menu = navigationView.getMenu();
        if(CURRENT_USER.profilePicLink != null){
            Glide.with(this).load(CURRENT_USER.profilePicLink)
                    .into(circleImageView);
        }
        textViewName.setText(CURRENT_USER.Name);
        TextView textViewEmail = headerView.findViewById(R.id.tv_header_email);
        textViewEmail.setText(CURRENT_USER.Email);
        if (CURRENT_USER.AdminLevel.equals("admin")){
            presenter.performAdminClassListDownload();
        }
        if (CURRENT_USER.AdminLevel.equals("user")){
            presenter.performUserClassListDownload();
        }
        loadNewHelloRequest();
        loadChatList();
    }

    @Override
    public void downloadDataFailed() {
        Toast.makeText(MainActivity.this,"Data Load Failed",Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
    }

    @Override
    public void addAdminClassSuccess() {
        presenter.performAdminClassListDownload();
        Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void addAdminClassFailed() {
        Toast.makeText(MainActivity.this,"Failed! Try Again",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void endSession(int index) {
        presenter.endSession(index);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void loadAdminClassList(ArrayList<ClassObject> classObjectArrayList) {
        CURRENT_ADMIN_CLASS_LIST = classObjectArrayList;
        viewPager.setAdapter(adapterPagerView);
        checkNewPost(CURRENT_CLASS_ID_LIST);
        viewPager.setCurrentItem(2);
        tabLayout.getTabAt(0).setIcon(R.drawable.bell);
        tabLayout.getTabAt(1).setIcon(R.drawable.house);
        tabLayout.getTabAt(2).setIcon(R.drawable.blackboard_icon);
        /*if(!flag){viewPager.setCurrentItem(1);flag = true;}
        else {viewPager.setCurrentItem(2);}*/
        progressDialog.dismiss();
    }

    @SuppressLint("WrongConstant")
    @Override
    public void loadUserClassList(ArrayList<ClassObject> classObjectsList,
                                  ArrayList<String> userAttendanceList) {
        CURRENT_ADMIN_CLASS_LIST = classObjectsList;
        viewPager.setAdapter(adapterPagerView);
        viewPager.setCurrentItem(2);
        tabLayout.getTabAt(0).setIcon(R.drawable.bell);
        tabLayout.getTabAt(1).setIcon(R.drawable.house);
        tabLayout.getTabAt(2).setIcon(R.drawable.blackboard_icon);
        checkNewPost(CURRENT_CLASS_ID_LIST);
        /*if(!flag){viewPager.setCurrentItem(1);flag = true;}
        else {viewPager.setCurrentItem(2);}*/

        progressDialog.dismiss();
    }

    @Override
    public void addCollab(int index, String email) {
        presenter.addCollab(index,email,false);
    }

    @Override
    public void transferClass(int index, String email) {
        presenter.transferClass(index,email);
    }

    @Override
    public void transferClassFailed() {
        Toast.makeText(this,"Failed Transfer",Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("WrongConstant")
    @Override
    public void connectionListDownloadSuccess(ArrayList<UserObject> userList) {
        if(userList.size() != 0){
            LinearLayoutManager llm = new LinearLayoutManager(this);
            connectionListDialog = new Dialog(this);
            connectionListDialog.setContentView(R.layout.connection_list_dialog);
            AdapterConnectionList adapterConnectionList = new AdapterConnectionList(userList,connectionListDialog);
            RecyclerView recyclerView = connectionListDialog.findViewById(R.id.recycler_connection_list);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(llm);
            recyclerView.setAdapter(adapterConnectionList);
            adapterConnectionList.notifyDataSetChanged();
            connectionListDialog.show();
        }
        else{
            Toast.makeText(MainActivity.this,"Add Class First",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void connectionListDownloadFailed() {
        Toast.makeText(this, "Connection List Load Failed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadHelloSuccess() {
        if (HELLO_REQUEST_USERS == null || HELLO_REQUEST_USERS.isEmpty()){
            menu.getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_people_outline_white_24dp));
        }
        else {
            menu.getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_people_outline_blue_24dp));
        }
    }

    @Override
    public void loadHelloFailed() {
        Toast.makeText(this,"Failed To Load Hello Request", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void newChatNotif(ChatListMasterObject chatListMasterObject) {
        menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.chat_new));
    }

    @Override
    public void notifNewComment() {
        if(CHECK_NEW_COMMENT_POST.size()>0){
            flag = false;
            for (int i:CHECK_NEW_COMMENT_POST){
                if (i == 1 || i == 3){
                    flag = true;
                }
            }
            if (flag){
                tabLayout.getTabAt(2).setIcon(R.drawable.blackboard_icon_new);
                ADAPTER_CLASS_LIST.notifyDataSetChanged();
            }
        }
        if (CHECK_NEW_COMMENT.size()>0){
                tabLayout.getTabAt(2).setIcon(R.drawable.blackboard_icon_new);
                ADAPTER_CLASS_LIST.notifyDataSetChanged();
        }
    }

    public void closeDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    private void loadNewHelloRequest(){
        presenter.loadHelloRequest();
    }

    private void loadChatList(){
        HashMap<String, ChatListMasterObject> chatListHashMap = new HashMap<>();
        chatListModel = new ChatListModel(this,chatListHashMap);
        chatListModel.performChatListLoad(CURRENT_USER.UID);
    }

    private void checkNewPost(ArrayList<String> classID){
        // 1 = new post    2 = new comment   3 = new post+comment
        LiveMainModel liveMainModel = new LiveMainModel(this);
        liveMainModel.performCheckRead(CURRENT_USER.AdminLevel,CURRENT_USER.UID);
    }

}
