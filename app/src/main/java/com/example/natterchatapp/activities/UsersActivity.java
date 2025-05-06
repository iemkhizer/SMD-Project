package com.example.natterchatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.natterchatapp.R;
import com.example.natterchatapp.adapters.UserAdapter;
import com.example.natterchatapp.listeners.UserListener;
import com.example.natterchatapp.models.User;
import com.example.natterchatapp.utilities.KEYS;
import com.example.natterchatapp.utilities.Preference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class UsersActivity extends BaseActivity implements UserListener {

    AppCompatImageView rivBack;
    TextView tvErrorMessage;
    ProgressBar progressBar;
    RecyclerView userRecyclerView;
    Preference pref;
    UserAdapter userAdapter;
    ArrayList<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        init();

        getUsers();
        rivBack.setOnClickListener(v -> finish());

    }

    private void getUsers() {
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(KEYS.KEY_COLLECTION_USER)
                .get().addOnCompleteListener(task -> {
                    loading(false);
                    String currUser = pref.getString(KEYS.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currUser.equals(queryDocumentSnapshot.getId())) {
                                continue;
                            }
                            User user = new User();
                            user.setName(queryDocumentSnapshot.getString(KEYS.KEY_USER_NAME));
                            user.setEmail(queryDocumentSnapshot.getString(KEYS.KEY_USER_EMAIL));
                            user.setImage(queryDocumentSnapshot.getString(KEYS.KEY_USER_IMAGE));
                            user.setToken(queryDocumentSnapshot.getString(KEYS.KEY_FCM_TOKEN));
                            user.setId(queryDocumentSnapshot.getId());

                            users.add(user);
                        }
                        if (!users.isEmpty()) {
                            userAdapter = new UserAdapter(users, this);
                            userRecyclerView.setAdapter(userAdapter);
                            userRecyclerView.setLayoutManager(new LinearLayoutManager(UsersActivity.this));
                            userRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            showErrorMessage();
                        }
                    }

                });


    }

    private void showErrorMessage() {
        tvErrorMessage.setText(String.format("%s", "No User available"));
        tvErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    public void init() {
        rivBack = findViewById(R.id.backImage);
        tvErrorMessage = findViewById(R.id.textErrorMessage);
        progressBar = findViewById(R.id.progressBar);
        userRecyclerView = findViewById(R.id.usersRecyclerView);
        pref = new Preference(UsersActivity.this);

        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(UsersActivity.this, ChatActivity.class);
        intent.putExtra(KEYS.KEY_USER, user);
        startActivity(intent);
        finish();

    }
}