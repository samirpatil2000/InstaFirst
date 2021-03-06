package com.example.loginul;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    EditText username_register,email_register,name_register,password_register;
    TextView  registerButton;
    FloatingActionButton fab;
    ProgressDialog pd;


    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        getWindow().setStatusBarColor(Color.TRANSPARENT);
//        Window w = getWindow();
//        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        getSupportActionBar().hide();

        name_register=findViewById(R.id.name_Register);
//        username_register=findViewById(R.id.username_Register);
        email_register=findViewById(R.id.email_register);
        password_register=findViewById(R.id.password_register);
        fab=findViewById(R.id.floating_Action_Register);

        auth=FirebaseAuth.getInstance();



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.hide();
                pd= new ProgressDialog(SignUpActivity.this);
                pd.setMessage("Please wait..");
                pd.show();


//                String username = username_register.getText().toString();
//                String fullName = name_register.getText().toString();
                String email = email_register.getText().toString();
                String password = password_register.getText().toString();


                // Conditions for correct input

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        showMessage(" Enter Correct Email ");
                        email_register.setError("Invalid Email");
                        email_register.setFocusable(true);
                        fab.show();
                        pd.hide();
                }
                else if (password.length() < 6  ) {
                    // every thing is ok then Create User Account
                    showMessage("Password length should be greater than 6 char and make it unique");
                    password_register.setError(" Password length at least 6 characters ");
                    password_register.setFocusable(true);
                    fab.show();
                    pd.hide();
                }else{
                    createUser(email,password);
                }
            }
        });
    }

    private void createUser( final String email, String password) {
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String email= firebaseUser.getEmail();
                            String userId = firebaseUser.getUid();

                            //path to store user date named "Users

                            HashMap<String,Object > hashMap = new HashMap<>();

                            hashMap.put("id",userId);
                            hashMap.put("FullName","");
                            hashMap.put("email",email);
                            hashMap.put("onlineStatus","online");
                            hashMap.put("bio","");
                            hashMap.put("coverImage","");
                            hashMap.put("imageUrl","https://firebasestorage.googleapis.com/v0/b/instafirst-a91d7.appspot.com/o/profilepic.png?alt=media&token=0f3ff98c-6c42-437a-8dcb-9bc28fc1d168");

                            reference = FirebaseDatabase.getInstance().getReference().child("Users");
                            reference.child(userId).setValue(hashMap);

                            pd.dismiss();
                            Intent intent= new Intent(SignUpActivity.this,BottomActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                            // Message
                            showMessage(" Register Successfully ");

 //                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if(task.isSuccessful()){
//                                        pd.dismiss();
//                                        Intent intent= new Intent(SignUpActivity.this,BottomActivity.class);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        startActivity(intent);
//                                    }
//                                }
//                            });

                        }else{
                            pd.dismiss();
                            showMessage("Authentication Failed"+task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                pd.dismiss();
                fab.show();
            }
        });
    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    public void OpenSignInPage(View view) {
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
    }

}
