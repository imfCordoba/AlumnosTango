package com.madrefoca.alumnostango.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by fernando on 03/09/17.
 */
@DatabaseTable(tableName = "Attendees")
public class Attendee {

    @DatabaseField(id = true, canBeNull = false)
    private Integer dni;

    @DatabaseField(foreign = true, columnName = "idAttendeeType")
    private AttendeeType attendeeType;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = false)
    private String lastName;

    @DatabaseField
    private Integer age;

    @DatabaseField
    private String address;

    @DatabaseField
    private String level;

    @DatabaseField
    private Integer cellphoneNumber;

    @DatabaseField
    private String facebookLink;

    public Attendee() {

    }

    public Attendee(Integer dni, String name, String lastName, Integer age, String address,
                    String level, Integer cellphoneNumber, String facebookLink) {
        this.dni = dni;
        this.name = name;
        this.lastName = lastName;
        this.age = age;
        this.address = address;
        this.level = level;
        this.cellphoneNumber = cellphoneNumber;
        this.facebookLink = facebookLink;
    }

    public Integer getDni() {
        return dni;
    }

    public void setDni(Integer dni) {
        this.dni = dni;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getCellphoneNumber() {
        return cellphoneNumber;
    }

    public void setCellphoneNumber(Integer cellphoneNumber) {
        this.cellphoneNumber = cellphoneNumber;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }
}