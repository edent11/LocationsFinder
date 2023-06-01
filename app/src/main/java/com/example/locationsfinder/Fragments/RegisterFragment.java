package com.example.locationsfinder.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.locationsfinder.Activities.MainActivity;
import com.example.locationsfinder.Models.MySingleton;
import com.example.locationsfinder.Models.User;
import com.example.locationsfinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterFragment extends Fragment {

    private FirebaseAuth mAuth;
    private EditText firstName_box;
    private EditText lastName_box;
    private EditText email_box;
    private EditText password_box;
    private String user_id;
    private MaterialButton reg_btn;
    private static final String TAG = "EmailPassword";
    private String user_email;

    TextView register;
    MaterialButton loginBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mAuth = FirebaseAuth.getInstance();


        View view = inflater.inflate(R.layout.fragment_register, container, false);
        reg_btn = (MaterialButton) view.findViewById(R.id.register);


        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerFunc();
            }
        });

        return view;
    }

    private void loadToDB(FirebaseUser user) {

        email_box = (EditText) getView().findViewById(R.id.email);
        firstName_box = (EditText) getView().findViewById(R.id.FirstName);
        lastName_box = (EditText) getView().findViewById(R.id.LastName);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        User user_model = new User(user.getUid(), email_box.getText().toString(), firstName_box.getText().toString(), lastName_box.getText().toString(),false);
        myRef.child(user.getUid().toString()).setValue(user_model);

        user_model.setUserId(user_model.getUserId());
        MySingleton.getInstance().setUser(user_model);
        
        Log.d(TAG, user.getUid());

    }

    public void registerFunc(){

        email_box = (EditText) getView().findViewById(R.id.email);
         password_box = (EditText) getView().findViewById(R.id.password);

        String email = email_box.getText().toString();
        String password = password_box.getText().toString();

        Activity activity = getActivity();
        mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
                                                FirebaseUser user = mAuth.getCurrentUser();

                                                Toast.makeText(activity, "RegisterSuccessfully.",
                                                        Toast.LENGTH_SHORT).show();
                                                loadToDB(user);
                                                goToMainActivity();
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Toast.makeText(activity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });


    }

    private void goToMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("email", user_email);
        startActivity(intent);

        ((Activity) getActivity()).overridePendingTransition(0, 0);
    }
}