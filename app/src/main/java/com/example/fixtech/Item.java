package com.example.fixtech;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Item implements Serializable {
    private String ID;
    private String customer_name;
    private String customer_phone;
    private String customer_email;
    private String device_name;
    private String device_ID;
    private String device_issue;
    private String device_password;
    private String issue_status;
    private long book_date;
    private long delivery_date;
    private String image_url;
    private boolean isPaid;
    private float price = 0;

    private boolean isExpanded;


    public Item() {
    }

    public Item(String ID, String customer_name, String customer_phone, String customer_email
            , String device_name, String device_ID, String device_issue, String device_password
            , String issue_status, long book_date, long delivery_date, String image_url, boolean isPaid, float price) {
        this.ID = ID;
        this.customer_name = customer_name;
        this.customer_phone = customer_phone;
        this.customer_email = customer_email;
        this.device_name = device_name;
        this.device_ID = device_ID;
        this.device_issue = device_issue;
        this.device_password = device_password;
        this.issue_status = issue_status;
        this.book_date = book_date;
        this.delivery_date = delivery_date;
        this.image_url = image_url;
        this.isPaid = isPaid;
        this.price =price;

        isExpanded = false;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_phone() {
        return customer_phone;
    }

    public void setCustomer_phone(String customer_phone) {
        this.customer_phone = customer_phone;
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDevice_ID() {
        return device_ID;
    }

    public void setDevice_ID(String device_ID) {
        this.device_ID = device_ID;
    }

    public String getDevice_issue() {
        return device_issue;
    }

    public void setDevice_issue(String device_issue) {
        this.device_issue = device_issue;
    }

    public String getDevice_password() {
        return device_password;
    }

    public void setDevice_password(String device_password) {
        this.device_password = device_password;
    }

    public String getIssue_status() {
        return issue_status;
    }

    public void setIssue_status(String issue_status) {
        this.issue_status = issue_status;
    }

    public long getBook_date() {
        return book_date;
    }

    public void setBook_date(long book_date) {
        this.book_date = book_date;
    }

    public long getDelivery_date() {
        return delivery_date;
    }

    public void setDelivery_date(long delivery_date) {
        this.delivery_date = delivery_date;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Exclude
    public boolean isExpanded() {
        return isExpanded;
    }

    @Exclude
    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }


}
