package com.madrefoca.alumnostango.fragments;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.madrefoca.alumnostango.R;
import com.madrefoca.alumnostango.utils.ManageFragmentsNavigation;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventAccountsFragment extends Fragment {

    private Bundle bundle;

    @Nullable
    @BindView(R.id.fabAddPaymentView)
    FloatingActionButton fabAddPaymentView;


    public EventAccountsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisFragment = inflater.inflate(R.layout.fragment_event_accounts, container, false);

        bundle = new Bundle();
        this.bundle = this.getArguments();

        ButterKnife.bind(this, thisFragment);
        return thisFragment;
    }

    @Optional
    @OnClick(R.id.fabAddPaymentView)
    public void onClickFabAddPaymentView() {

        ManageFragmentsNavigation.navItemIndex = 11;
        ManageFragmentsNavigation.CURRENT_TAG = ManageFragmentsNavigation.TAG_ATTENDEE_EVENT_PAYMENT;

        // update the main content by replacing fragments
        Fragment fragment = ManageFragmentsNavigation.getHomeFragment();
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.frame, fragment, ManageFragmentsNavigation.CURRENT_TAG);
        fragmentTransaction.commitAllowingStateLoss();

    }

}
