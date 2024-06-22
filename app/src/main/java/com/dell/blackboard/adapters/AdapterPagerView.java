package com.dell.blackboard.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.dell.blackboard.fragments.ClassTabFragment;
import com.dell.blackboard.fragments.HomeTabFragment;
import com.dell.blackboard.fragments.NotifTabFragment;

public class AdapterPagerView extends FragmentPagerAdapter {


    public AdapterPagerView(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = new NotifTabFragment();
                break;
            case 1:
                fragment = new HomeTabFragment();
                break;
            case 2:
                fragment = new ClassTabFragment();
                break;
            default:
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

}
