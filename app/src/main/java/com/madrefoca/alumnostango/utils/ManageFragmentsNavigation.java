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
    public static final String TAG_HOME = "Inicio";
    public static final String TAG_ATTENDEES = "Asistentes";
    public static final String TAG_ATTENDEE_TYPES = "Tipos de asistentes";
    public static final String TAG_EVENT_TYPES = "Tipos de evento";
    public static final String TAG_PLACES = "Lugares";
    public static final String TAG_PAYMENTS = "Eventos y pagos";
    public static final String TAG_COUPONS = "Cupones";
    public static final String TAG_NOTIFICATIONS= "Notificaciones";
    public static final String TAG_SETTINGS = "Configuración";
    public static final String TAG_ABOUTUS = "Acerca de nosotros";
    public static final String TAG_ATTENDEE_EVENT_PAYMENT = "Pagos del evento";
    public static final String TAG_EVENT_ACCOUNTS = "Cuentas del evento";

    public static String navItemTag = TAG_HOME;

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
