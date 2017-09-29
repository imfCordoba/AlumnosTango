package com.madrefoca.alumnostango.adapters;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.madrefoca.alumnostango.fragments.DatePickerFragment;
import com.madrefoca.alumnostango.fragments.TimePickerFragment;

import com.madrefoca.alumnostango.R;

/**
 * Created by fernando on 26/09/17.
 */

public class PickerAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 2;
    Fragment timePickerFragment;
    Fragment datePickerFragment;

    public PickerAdapter(FragmentManager fm) {
        super(fm);
        timePickerFragment = new TimePickerFragment();
        datePickerFragment = new DatePickerFragment();
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return timePickerFragment;
            case 1:
            default:
                return datePickerFragment;
        }
    }

    int getTitle(int position) {
        switch(position) {
            case 0:
                return R.string.tab_title_time;
            case 1:
            default:
                return R.string.tab_title_date;
        }
    }
}
