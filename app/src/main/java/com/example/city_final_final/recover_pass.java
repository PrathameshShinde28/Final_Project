package com.example.city_final_final;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class recover_pass extends AppCompatActivity {

    private ImageView back_img;
    EditText email_recover;
    private Button recover_btn;
    private FirebaseAuth authProfile;
    private final static String TAG = "recover_pass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_pass);

        back_img = (ImageView) findViewById(R.id.back_arrow);

        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(recover_pass.this, signin_activity.class);
                startActivity(intent);
            }
        });

        // Authentication Section
        email_recover = findViewById(R.id.login_email);
        recover_btn = findViewById(R.id.recover_btn);
        recover_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Obtain Entered Data
                String text_email = email_recover.getText().toString();

                // Validation
                if (text_email.isEmpty()) {
                    email_recover.requestFocus();
                    email_recover.setError("Field Cannot Be Empty");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(text_email).matches()) {
                    email_recover.requestFocus();
                    email_recover.setError("Invalid Email");
                } else {
                    resetPassword(text_email);
                }
            }
        });
    }

    private void resetPassword(String text_email) {
        authProfile = FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(text_email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(recover_pass.this, "Please Check your Inbox for password reset link", Toast.LENGTH_SHORT).show();

                    //Open Main UI after Successful registration
                    Intent intent = new Intent(recover_pass.this, signin_activity.class);

                    // To prevent user from returning back to the Sign up activity if he clicks back button
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); //To close the SignUp Activity
                }
                else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e){
                        email_recover.setError("Email does not exists or not valid. Please Register again!");
                        email_recover.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(recover_pass.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}