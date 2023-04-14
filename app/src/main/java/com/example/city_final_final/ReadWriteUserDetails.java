package com.example.city_final_final;

public class ReadWriteUserDetails {
    public String email, dob, gender, mobile, pass;

    public ReadWriteUserDetails(String text_email, String text_mobile, String text_dob, String text_gender, String text_pass){
        this.email = text_email;
        this.dob = text_dob;
        this.gender = text_gender;
        this.mobile = text_mobile;
        this.pass = text_pass;
    }
}
