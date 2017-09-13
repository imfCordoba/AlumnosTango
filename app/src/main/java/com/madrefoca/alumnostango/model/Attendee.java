package com.madrefoca.alumnostango.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by fernando on 03/09/17.
 */
@DatabaseTable(tableName = "Attendees")
public class Attendee {

    @DatabaseField(generatedId = true)
    private Integer attendeeId;

    @DatabaseField(foreign = true, columnName = "idAttendeeType")
    private AttendeeType attendeeType;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = false)
    private String lastName;

    @DatabaseField
    private Integer age;

    @DatabaseField
    private Integer cellphoneNumber;

    @DatabaseField
    private String facebookProfile;

    @DatabaseField
    private String email;

    public Attendee() {

    }

    public Attendee(String name, String lastName, Integer age, Integer cellphoneNumber,
                    String facebookProfile, String email) {
        this.name = name;
        this.lastName = lastName;
        this.age = age;
        this.cellphoneNumber = cellphoneNumber;
        this.facebookProfile = facebookProfile;
        this.email = email;
    }

    public Attendee(String name, String lastName, AttendeeType attendeeType, Integer cellphoneNumber) {
        this.name = name;
        this.lastName = lastName;
        this.attendeeType = attendeeType;
        this.cellphoneNumber = cellphoneNumber;
    }

    public Attendee(String name, Integer cellphoneNumber) {
        this.name = name;
        this.cellphoneNumber = cellphoneNumber;
    }

    public Integer getAttendeeId() {
        return attendeeId;
    }

    public void setAttendeeId(Integer attendeeId) {
        this.attendeeId = attendeeId;
    }

    public AttendeeType getAttendeeType() {
        return attendeeType;
    }

    public void setAttendeeType(AttendeeType attendeeType) {
        this.attendeeType = attendeeType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getCellphoneNumber() {
        return cellphoneNumber;
    }

    public void setCellphoneNumber(Integer cellphoneNumber) {
        this.cellphoneNumber = cellphoneNumber;
    }

    public String getFacebookProfile() {
        return facebookProfile;
    }

    public void setFacebookProfile(String facebookProfile) {
        this.facebookProfile = facebookProfile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
