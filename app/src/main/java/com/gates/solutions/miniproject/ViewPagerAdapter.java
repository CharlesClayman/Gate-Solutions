package com.gates.solutions.miniproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Rent_Fragment();
            case 1:
                return new Sales_Fragment();
            case 2:
                return new MyPost_Fragment();
            case 3:
                return new Chat_Fragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
