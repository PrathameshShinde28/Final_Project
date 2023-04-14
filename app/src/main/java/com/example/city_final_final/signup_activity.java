package com.example.city_final_final;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class signup_activity extends AppCompatActivity {

    String[] items = {"Male","Female","Others"};
    AutoCompleteTextView autoCompleteTxt;
    ArrayAdapter<String> adapterItems;
    EditText dp1;

    private ImageView back_img;


    //Registration View Declaration
    private EditText TextView_email_layout, TextView_mobile_layout, TextView_pass_layout, TextView_conformpass_layout;
    private ProgressBar progress_bar;
    private Button signup_button;
    private static final String TAG= "signup_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        // Gender Section
        autoCompleteTxt = findViewById(R.id.gender_selection);
        adapterItems = new ArrayAdapter<String>(this,R.layout.gender_dropdown,items);
        autoCompleteTxt.setAdapter(adapterItems);
        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
            }
        });


        // Date Of Birth Section
        dp1 = findViewById(R.id.dob_selection);
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.YEAR, year);

                updateCalender();
            }
            private void updateCalender(){
                String Format = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(Format, Locale.US);

                dp1.setText(sdf.format(calendar.getTime()));
            }
        };

        dp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(signup_activity.this,date,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        // Back Arrow Image Intent
        back_img = (ImageView) findViewById(R.id.back_arrow);
        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (signup_activity.this,signin_signup.class);
                startActivity(intent);
            }
        });


        // Authentication Section
        progress_bar = findViewById(R.id.progress_bar);
        TextView_email_layout = findViewById(R.id.regEmail);
        TextView_mobile_layout = findViewById(R.id.regMobile);
        dp1 = findViewById(R.id.dob_selection);
        autoCompleteTxt = findViewById(R.id.gender_selection);
        TextView_pass_layout = findViewById(R.id.regPassword);
        TextView_conformpass_layout = findViewById(R.id.regConformPassword);
        Button signup_button = findViewById(R.id.signup_btn);
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Obtain Entered Data
                String text_email = TextView_email_layout.getText().toString();
                String text_mobile = TextView_mobile_layout.getText().toString();
                String text_dob = dp1.getText().toString();
                String text_gender = autoCompleteTxt.getText().toString();
                String text_pass = TextView_pass_layout.getText().toString();
                String text_conform_pass = TextView_conformpass_layout.getText().toString();

                // Validate Mobile Number
                String mobileRegex = "[6-9][0-9]{9}";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(text_mobile);

                // Validation
                if (text_email.isEmpty()){
                    TextView_email_layout.requestFocus();
                    TextView_email_layout.setError("Field Cannot Be Empty");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(text_email).matches()) {
                    TextView_email_layout.requestFocus();
                    TextView_email_layout.setError("Invalid Email");
                } else if (text_mobile.length()!=10) {
                    TextView_mobile_layout.requestFocus();
                    TextView_mobile_layout.setError("Mobile Number Should Be of 10 Digits");
                } else if (text_dob.isEmpty()) {
                    dp1.requestFocus();
                    dp1.setError("Enter Date of Birth");
                } else if (text_gender.isEmpty()) {
                    autoCompleteTxt.requestFocus();
                    autoCompleteTxt.setError("Enter your Gender");
                } else if (text_pass.isEmpty()) {
                    TextView_pass_layout.requestFocus();
                    TextView_pass_layout.setError("Please set a Password");
                } else if (text_conform_pass.isEmpty()) {
                    TextView_conformpass_layout.requestFocus();
                    TextView_conformpass_layout.setError("Enter the same Password as Above");
                } else if (!text_pass.equals(text_conform_pass)) {
                    TextView_conformpass_layout.requestFocus();
                    TextView_conformpass_layout.setError("Password do not match");
                } else if (text_pass.length() < 6) {
                    TextView_pass_layout.requestFocus();
                    TextView_pass_layout.setError("Password Length should be more than 6 Digits");
                } else if (!mobileMatcher.find()) {
                    TextView_mobile_layout.requestFocus();
                    TextView_mobile_layout.setError("Mobile Number is not valid");
                } else {
                    progress_bar.setVisibility(View.VISIBLE);
                    registerUser(text_email, text_mobile, text_dob, text_gender, text_pass);
                }
            }
        });
    }

    // Register User Using the Credentials entered
    private void registerUser(String text_email, String text_mobile, String text_dob, String text_gender, String text_pass) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(text_email, text_pass).addOnCompleteListener(signup_activity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseUser firebaseuser = auth.getCurrentUser();

                    // Enter User Data into the Firebase Realtime Database
                    ReadWriteUserDetails writeuserdetails = new ReadWriteUserDetails (text_email, text_mobile, text_dob, text_gender, text_pass);

                    //Extracting user reference from database for "Registered Users"
                    DatabaseReference refernceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                    refernceProfile.child(firebaseuser.getUid()).setValue(writeuserdetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                //Send Verification to the user
                                firebaseuser.sendEmailVerification();

                                Toast.makeText(signup_activity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();

                                //Open Main UI after Successful registration
                                Intent intent = new Intent(signup_activity.this, main_ui.class);

                                // To prevent user from returning back to the Sign up activity if he clicks back button
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish(); //To close the SignUp Activity
                            } else {
                                Toast.makeText(signup_activity.this, "Registration Failed !, Please try again", Toast.LENGTH_SHORT).show();
                            }

                            // Hide the Progress Bar
                            progress_bar.setVisibility(View.GONE);
                        }
                    });

                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e){
                        TextView_pass_layout.setError("Password too weak");
                        TextView_pass_layout.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        TextView_email_layout.setError("Invalid Email");
                        TextView_email_layout.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e){
                        TextView_email_layout.setError("User Already registered using this email");
                        TextView_email_layout.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(signup_activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    progress_bar.setVisibility(View.GONE);
                }
            }
        });
    }
}