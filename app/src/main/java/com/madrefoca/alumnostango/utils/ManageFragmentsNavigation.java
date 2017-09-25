package com.madrefoca.alumnostango.utils;

import android.support.v4.app.Fragment;

import com.madrefoca.alumnostango.fragments.AttendeeTypesFragment;
import com.madrefoca.alumnostango.fragments.AttendeesFragment;
import com.madrefoca.alumnostango.fragments.CouponsFragment;
import com.madrefoca.alumnostango.fragments.EventTypesFragment;
import com.madrefoca.alumnostango.fragments.EventsFragment;
import com.madrefoca.alumnostango.fragments.HomeFragment;
import com.madrefoca.alumnostango.fragments.NotificationsFragment;
import com.madrefoca.alumnostango.fragments.PaymentsFragment;
import com.madrefoca.alumnostango.fragments.PlacesFragment;
import com.madrefoca.alumnostango.fragments.SettingsFragment;

/**
 * Created by fernando on 23/09/17.
 */

public class ManageFragmentsNavigation {


    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    public static final String TAG_HOME = "home";
    public static final String TAG_ATTENDEES = "attendees";
    public static final String TAG_ATTENDEE_TYPES = "attendeeTypes";
    public static final String TAG_EVENTS = "events";
    public static final String TAG_EVENT_TYPES = "eventTypes";
    public static final String TAG_PLACES = "places";
    public static final String TAG_PAYMENTS = "payments";
    public static final String TAG_COUPONS = "coupons";
    public static final String TAG_NOTIFICATIONS= "notifications";
    public static final String TAG_SETTINGS = "settings";
    public static final String TAG_ABOUTUS = "aboutUs";
    public static String CURRENT_TAG = TAG_HOME;
    // TODO: 24/09/17 cambiar la forma de asignar los numeros a los fragments
    public static Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                AttendeesFragment attendeesFragment = new AttendeesFragment();
                return attendeesFragment;
            case 2:
                EventsFragment eventsFragment = new EventsFragment();
                return eventsFragment;
            case 3:
                PlacesFragment placesFragment = new PlacesFragment();
                return placesFragment;
            case 4:
                PaymentsFragment paymentsFragment = new PaymentsFragment();
                return paymentsFragment;
            case 5:
                CouponsFragment couponsFragment = new CouponsFragment();
                return couponsFragment;
            case 6:
                NotificationsFragment notificationsFragment = new NotificationsFragment();
                return notificationsFragment;
            case 7:
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;
            case 8:
                AttendeeTypesFragment attendeeTypesFragment = new AttendeeTypesFragment();
                return attendeeTypesFragment;
            case 9:
                EventTypesFragment eventTypesFragment = new EventTypesFragment();
                return eventTypesFragment;
            default:
                return new HomeFragment();
        }
    }
}
