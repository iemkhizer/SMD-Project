package com.example.natterchatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.natterchatapp.R;
import com.example.natterchatapp.utilities.KEYS;
import com.example.natterchatapp.utilities.Preference;
import com.example.natterchatapp.utilities.ShowToast;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPass;
    private Button btnLogin;
    private TextView tvNewAccount;
    private ShowToast toast;
    private Preference preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        if (preference.getBoolean(KEYS.KEY_USER_IS_SIGNED_IN)) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidDetail()) {
                    logIn();
                }
            }
        });
        tvNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void logIn() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(KEYS.KEY_COLLECTION_USER)
                .whereEqualTo(KEYS.KEY_USER_EMAIL, etEmail.getText().toString().trim())
                .whereEqualTo(KEYS.KEY_USER_PASSWORD, etPass.getText().toString().trim())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null
                            && !task.getResult().getDocuments().isEmpty()) {
                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                        preference.puBoolean(KEYS.KEY_USER_IS_SIGNED_IN, true);
                        preference.putString(KEYS.KEY_USER_NAME, doc.getString(KEYS.KEY_USER_NAME));
                        preference.putString(KEYS.KEY_USER_ID, doc.getId());
                        preference.putString(KEYS.KEY_USER_IMAGE, doc.getString(KEYS.KEY_USER_IMAGE));

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        toast.showToast("Welcome " + preference.getString(KEYS.KEY_USER_NAME));
                        finish();


                    } else {
                        toast.showToast("Invalid Email or Password");
                    }
                });
    }

    private boolean isValidDetail() {

        boolean flag = true;
        if (etEmail.getText().toString().trim().isEmpty()) {
            flag = false;
            toast.showToast("Email Field cannot be Empty");
        } else if (etPass.getText().toString().isEmpty()) {
            flag = false;
            toast.showToast("Password Field cannot be Empty");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()) {
            flag = false;
            toast.showToast("Invalid Email format");
        }
        return flag;
    }

    private void init() {
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);
        btnLogin = findViewById(R.id.btnLogin);
        tvNewAccount = findViewById(R.id.tvNewAccount);

        toast = new ShowToast(LoginActivity.this);

        preference = new Preference(LoginActivity.this);
    }
}