package com.madrefoca.alumnostango.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by fernando on 03/09/17.
 */
@DatabaseTable(tableName = "Coupons")
public class Coupon {

    @DatabaseField(generatedId = true)
    private Integer idCoupon;

    @DatabaseField(foreign = true, columnName = "idAttendee")
    private Attendee attendee;

    @DatabaseField
    private String description;

    public Coupon() {
    }

    public Coupon(Attendee attendee, String description) {
        this.attendee = attendee;
        this.description = description;
    }

    public Integer getIdCoupon() {
        return idCoupon;
    }

    public void setIdCoupon(Integer idCoupon) {
        this.idCoupon = idCoupon;
    }

    public Attendee getAttendee() {
        return attendee;
    }

    public void setAttendee(Attendee attendee) {
        this.attendee = attendee;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}