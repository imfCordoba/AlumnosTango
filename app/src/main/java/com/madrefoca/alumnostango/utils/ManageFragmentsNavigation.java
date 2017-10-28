package com.madrefoca.alumnostango.utils;


import android.app.Fragment;

import com.madrefoca.alumnostango.fragments.AttendeeEventPaymentFragment;
import com.madrefoca.alumnostango.fragments.AttendeeTypesFragment;
import com.madrefoca.alumnostango.fragments.AttendeesFragment;
import com.madrefoca.alumnostango.fragments.CouponsFragment;
import com.madrefoca.alumnostango.fragments.EventAccountsFragment;
import com.madrefoca.alumnostango.fragments.EventTypesFragment;
import com.madrefoca.alumnostango.fragments.EventsFragment;
import com.madrefoca.alumnostango.fragments.HomeFragment;
import com.madrefoca.alumnostango.fragments.NotificationsFragment;
import com.madrefoca.alumnostango.fragments.PaymentsFragment;
import com.madrefoca.alumnostango.fragments.PlacesFragment;
import com.madrefoca.alumnostango.fragments.SettingsFragment;
import com.madrefoca.alumnostango.model.AttendeeEventPayment;

/**
 * Created by fernando on 23/09/17.
 */

public class ManageFragmentsNavigation {

    public static int navItemIndex = 0;

    // individual index for each fragment
    public static int homeIndex = 0;
    public static int attendeesIndex = 1;
    public static int placesIndex = 2;
    public static int paymentsIndex = 3;
    public static int couponsIndex = 4;
    public static int notificationsIndex = 5;
    public static int settingsIndex = 6;
    public static int attendeeTypesIndex = 7;
    public static int eventTypesIndex = 8;
    public static int eventAccountsIndex = 9;
    public static int attendeeEventPaymentIndex = 10;


    // tags used to attach the fragments
    public static final String TAG_HOME = "home";
    public static final String TAG_ATTENDEES = "attendees";
    public static final String TAG_ATTENDEE_TYPES = "attendeeTypes";
    public static final String TAG_EVENT_TYPES = "eventTypes";
    public static final String TAG_PLACES = "places";
    public static final String TAG_PAYMENTS = "payments";
    public static final String TAG_COUPONS = "coupons";
    public static final String TAG_NOTIFICATIONS= "notifications";
    public static final String TAG_SETTINGS = "settings";
    public static final String TAG_ABOUTUS = "aboutUs";
    public static final String TAG_ATTENDEE_EVENT_PAYMENT = "attendeeEventPayment";
    public static final String TAG_EVENT_ACCOUNTS = "eventAccounts";

    public static String navItemTag = TAG_HOME;


    // TODO: 24/09/17 cambiar la forma de asignar los numeros a los fragments
    public static Fragment getHomeFragment() {
        switch (navItemTag) {
            case TAG_HOME:
                navItemIndex = homeIndex;
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case TAG_ATTENDEES:
                navItemIndex = attendeesIndex;
                AttendeesFragment attendeesFragment = new AttendeesFragment();
                return attendeesFragment;
            case TAG_PLACES:
                navItemIndex = placesIndex;
                PlacesFragment placesFragment = new PlacesFragment();
                return placesFragment;
            case TAG_PAYMENTS:
                navItemIndex = paymentsIndex;
                PaymentsFragment paymentsFragment = new PaymentsFragment();
                return paymentsFragment;
            case TAG_COUPONS:
                navItemIndex = couponsIndex;
                CouponsFragment couponsFragment = new CouponsFragment();
                return couponsFragment;
            case TAG_NOTIFICATIONS:
                navItemIndex = notificationsIndex;
                NotificationsFragment notificationsFragment = new NotificationsFragment();
                return notificationsFragment;
            case TAG_SETTINGS:
                navItemIndex = settingsIndex;
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;
            case TAG_ATTENDEE_TYPES:
                navItemIndex = attendeeTypesIndex;
                AttendeeTypesFragment attendeeTypesFragment = new AttendeeTypesFragment();
                return attendeeTypesFragment;
            case TAG_EVENT_TYPES:
                navItemIndex = eventTypesIndex;
                EventTypesFragment eventTypesFragment = new EventTypesFragment();
                return eventTypesFragment;
            case TAG_EVENT_ACCOUNTS:
                navItemIndex = eventAccountsIndex;
                EventAccountsFragment eventAccountsFragment = new EventAccountsFragment();
                return eventAccountsFragment;
            case TAG_ATTENDEE_EVENT_PAYMENT:
                navItemIndex = attendeeEventPaymentIndex;
                AttendeeEventPaymentFragment attendeeEventPaymentFragment = new AttendeeEventPaymentFragment();
                return attendeeEventPaymentFragment;
            default:
                return new HomeFragment();
        }
    }

    public static void setCurrentTag(String tag) {

        navItemTag = tag;

        switch (navItemTag) {
            case TAG_HOME:
                navItemIndex = homeIndex;
                break;
            case TAG_ATTENDEES:
                navItemIndex = attendeesIndex;
                break;
            case TAG_PLACES:
                navItemIndex = placesIndex;
                break;
            case TAG_PAYMENTS:
                navItemIndex = paymentsIndex;
                break;
            case TAG_COUPONS:
                navItemIndex = couponsIndex;
                break;
            case TAG_NOTIFICATIONS:
                navItemIndex = notificationsIndex;
                break;
            case TAG_SETTINGS:
                navItemIndex = settingsIndex;
                break;
            case TAG_ATTENDEE_TYPES:
                navItemIndex = attendeeTypesIndex;
                break;
            case TAG_EVENT_TYPES:
                navItemIndex = eventTypesIndex;
                break;
            case TAG_EVENT_ACCOUNTS:
                navItemIndex = eventAccountsIndex;
                break;
            case TAG_ATTENDEE_EVENT_PAYMENT:
                navItemIndex = attendeeEventPaymentIndex;
                break;
        }
    }
}
