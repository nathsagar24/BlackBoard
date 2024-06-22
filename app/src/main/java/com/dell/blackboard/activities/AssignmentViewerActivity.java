package com.dell.blackboard.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.dell.blackboard.R;
import com.dell.blackboard.adapters.AdapterPagerAssignmentViewer;

import static com.dell.blackboard.Common.SELECTED_ASSIGNMENT_ID;

public class AssignmentViewerActivity extends AppCompatActivity {
    ViewPager viewPager;
    AdapterPagerAssignmentViewer adapterPagerAssignmentViewer;
    TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_viewer);

        SELECTED_ASSIGNMENT_ID = getIntent().getStringExtra("assignment_id");

        final ActionBar abar = getSupportActionBar();
        if(abar!=null) {
            abar.setDisplayHomeAsUpEnabled(true);
            abar.setTitle("Assignment Viewer");
        }
        viewPager = findViewById(R.id.fragment_container_assignmentviewer);
        adapterPagerAssignmentViewer = new AdapterPagerAssignmentViewer(getSupportFragmentManager());
        tabLayout = findViewById(R.id.tab_bar_assignmentviewer);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(adapterPagerAssignmentViewer);
        viewPager.setCurrentItem(0);

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return  false;
    }
}
