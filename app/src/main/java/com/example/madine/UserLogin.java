package com.example.madine;

import static com.example.madine.MainActivity.user;
import static com.example.madine.MainActivity.email;
import static com.example.madine.MainActivity.noUnit;
import static com.example.madine.MainActivity.password;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.madine.helper.Simplify;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserLogin extends AppCompatActivity {
    private TextView admintxt;
    private EditText noUnitTextView, passwordTextView;
    private Button Btn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        // taking instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // initialising all views through id defined above
        noUnitTextView = findViewById(R.id.noUnitInput);
        passwordTextView = findViewById(R.id.password_inputText2);
        Btn = findViewById(R.id.btn_login_user);
        progressBar = findViewById(R.id.progressBar2);
        admintxt = findViewById(R.id.AdminPage);
        ImageView imageViewTogglePassword = findViewById(R.id.imageViewTogglePassword2);
//        noUnitTextView.setText("");
//        passwordTextView.setText("");

        // Set on Click Listener on Sign-in button
        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // Write a message to the database
                loginUserAccount();
            }
        });

        admintxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserLogin.this, AdminLogin.class);
                startActivity(i);
            }
        });

        imageViewTogglePassword.setOnClickListener(new View.OnClickListener() {
            boolean passwordVisible = false;

            @Override
            public void onClick(View v) {
                if (passwordVisible) {
                    // Mengubah ke mode password tersembunyi
                    passwordTextView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imageViewTogglePassword.setImageResource(R.drawable.ic_password_hidden);
                } else {
                    // Mengubah ke mode password terlihat
                    passwordTextView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    imageViewTogglePassword.setImageResource(R.drawable.ic_password_visible);
                }
                passwordVisible = !passwordVisible;

                // Setel kursor ke akhir teks
                passwordTextView.setSelection(passwordTextView.getText().length());
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view instanceof EditText) {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    view.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
        return super.onTouchEvent(event);
    }
    private void loginUserAccount()
    {

        // Take the value of two edit texts in Strings
        noUnit= noUnitTextView.getText().toString();
        email = user + "@gmail.com";
        password = passwordTextView.getText().toString();

        // validations for input email and password
        if (TextUtils.isEmpty(noUnit)) {
            Simplify.showToastMessageWHITE(getApplicationContext(),"Please enter no Unit!!");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Simplify.showToastMessageWHITE(getApplicationContext(),"Please enter password!!");
            return;
        }


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //fitur menampilkan nama pengguna
        DatabaseReference myRef = database.getReference("user").child(noUnit).child("email");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Ambil data dari dataSnapshot
                // Misalnya, dapatkan nilai dari field tertentu
                email = dataSnapshot.getValue(String.class);

                // Lakukan tindakan yang sesuai dengan data yang Anda dapatkan
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Tangani kesalahan jika ada
            }
        });
        // show the visibility of progress bar to show loading
        progressBar.setVisibility(View.VISIBLE);

        // signin existing user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(
                                    @NonNull Task<AuthResult> task)
                            {
                                if (task.isSuccessful()) {
                                    Simplify.showToastMessageWHITE(getApplicationContext(),"Login successful!!");
                                    // hide the progress bar
                                    progressBar.setVisibility(View.GONE);

                                    // if sign-in is successful
                                    // intent to home activity
                                    Intent intent;
                                        intent = new Intent(UserLogin.this,
                                                Monitoring.class);
                                    startActivity(intent);

                                }

                                else {

                                    // sign-in failed
                                    Simplify.showToastMessageWHITE(getApplicationContext(),"Login failed!!");
                                    Simplify.showToastMessageWHITE(getApplicationContext(),email);
                                    Simplify.showToastMessageWHITE(getApplicationContext(),password);

                                    // hide the progress bar
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
    }

}