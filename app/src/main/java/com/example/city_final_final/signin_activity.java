package com.example.city_final_final;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.regex.Pattern;

public class signin_activity extends AppCompatActivity {

    private ImageView back_img;
    private EditText email_login, pass_login;
    private Button sign_in_btn;
    private TextView pass_recover;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;
    private static final String TAG = "signin_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        back_img = (ImageView) findViewById(R.id.back_arrow);

        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signin_activity.this, signin_signup.class);
                startActivity(intent);
            }
        });


        // Recover Password Section
        pass_recover = (TextView) findViewById(R.id.pass_recover);
        pass_recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signin_activity.this, recover_pass.class);
                startActivity(intent);
            }
        });

        authProfile = FirebaseAuth.getInstance();

        // Authentication Section
        progressBar = findViewById(R.id.progress_bar);
        email_login = findViewById(R.id.login_email);
        pass_login = findViewById(R.id.login_password);
        sign_in_btn = findViewById(R.id.signin_btn);
        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Obtain Entered Data
                String text_email = email_login.getText().toString();
                String text_pass = pass_login.getText().toString();


                // Validation
                if (text_email.isEmpty()) {
                    email_login.requestFocus();
                    email_login.setError("Field Cannot Be Empty");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(text_email).matches()) {
                    email_login.requestFocus();
                    email_login.setError("Invalid Email");
                } else if (text_pass.isEmpty()) {
                    pass_login.requestFocus();
                    pass_login.setError("Enter the Password");
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(text_email,text_pass);
                }
            }
        });
    }

    private void loginUser(String text_email, String text_pass) {
        authProfile.signInWithEmailAndPassword(text_email, text_pass).addOnCompleteListener(signin_activity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(signin_activity.this, "Login Successful !", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(signin_activity.this, main_ui.class);
                    startActivity(intent);
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e){
                        email_login.setError("User not registered, Please SignUp");
                        email_login.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        email_login.setError("Invalid Credentials. Kindle check and re-enter");
                        email_login.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(signin_activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    Toast.makeText(signin_activity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
