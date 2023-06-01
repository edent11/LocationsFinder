package com.example.locationsfinder.Activities;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.locationsfinder.Fragments.RegisterFragment;
import com.example.locationsfinder.Models.MySingleton;
import com.example.locationsfinder.Models.User;
import com.example.locationsfinder.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import androidx.fragment.app.FragmentTransaction;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private static final String TAG = "EmailPassword";
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private GoogleSignInAccount account;

    private TextView register;
    private ImageView google_btn;
    private MaterialButton loginBtn;
    private EditText email_txt;
    private EditText password_txt;
    private String user_email;
    private ImageButton logo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);




        register = (TextView) findViewById(R.id.register);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);

        loginBtn = (MaterialButton) findViewById(R.id.loginbtn);


        email_txt = (EditText) findViewById(R.id.login_email);
        password_txt = (EditText) findViewById(R.id.login_password);
        logo = (ImageButton)findViewById(R.id.logo);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this ,"LOCATIONS PRODUCTIONS", Toast.LENGTH_SHORT).show();

            }
        });


        mAuth = FirebaseAuth.getInstance();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signInWithEmailAndPassword(email_txt.getText().toString(), password_txt.getText().toString())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    user_email = user.getEmail();
                                    setUser();
                                    Toast.makeText(LoginActivity.this, "LoginSuccessfully.",
                                            Toast.LENGTH_SHORT).show();
                                    goToMainActivity();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
                            }
                        });

            }
        });



        google_btn = (ImageView) findViewById(R.id.googleBtn);

        google_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();

            }
        });

    }

    private void googleSignIn() {

        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, 1000);

    }

    private void setUser(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    //If email exists then toast shows else store the data on new key
                    if (data.getValue(User.class).getEmail().equals(email_txt.getText().toString())) {

                        MySingleton var = MySingleton.getInstance();
                        var.setUser(data.getValue(User.class));

                        break;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });


    }


    private void loadToDB(GoogleSignInAccount account) {



      FirebaseDatabase database = FirebaseDatabase.getInstance();
       DatabaseReference myRef = database.getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                boolean flag = false;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    //If email exists then toast shows else store the data on new key
                    if (data.getValue(User.class).getEmail().equals(account.getEmail().toString())) {

                        User user = new User ((data.getValue(User.class)));
                        MySingleton.getInstance().setUser(user);
                        user.setUserId(data.getKey().toString());
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    User newGoogleUser = new User(account.getEmail().toString(), account.getGivenName().toString(), account.getFamilyName().toString(), true);
                    myRef.child(myRef.push().getKey()).setValue(newGoogleUser);
                    MySingleton.getInstance().setUser(newGoogleUser);
                    newGoogleUser.setUserId(myRef.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        try {
            task.getResult(ApiException.class);
            account = GoogleSignIn.getLastSignedInAccount(this);
            user_email = account.getEmail();
            loadToDB(account);
            goToMainActivity();
        }catch (ApiException e){
            Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }

        }
    }

    private void goToMainActivity() {

        finish();
        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.putExtra("email", user_email);
        startActivity(myIntent);

    }

    public void registerClick (View view){

        loginBtn = (MaterialButton) findViewById(R.id.loginbtn);
        loginBtn.setVisibility(View.GONE);
        RegisterFragment registerFragment = new RegisterFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.Sign_Container, registerFragment).commit();
    }
}
