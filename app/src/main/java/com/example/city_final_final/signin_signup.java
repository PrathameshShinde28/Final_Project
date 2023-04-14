package com.example.city_final_final;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class signin_signup extends AppCompatActivity {

    private Button signin_button, signup_button;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_signup);

        signin_button = (Button)findViewById(R.id.signin_btn);
        signup_button = (Button)findViewById(R.id.signup_btn);

        signin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (signin_signup.this,signin_activity.class);
                startActivity(intent);
            }
        });

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (signin_signup.this,signup_activity.class);
                startActivity(intent);
            }
        });

    }
}