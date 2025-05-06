package com.example.natterchatapp.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.natterchatapp.utilities.KEYS;
import com.example.natterchatapp.utilities.Preference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BaseActivity extends AppCompatActivity {
    private DocumentReference documentReference;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preference pref=new Preference(getApplicationContext());
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        documentReference=database.collection(KEYS.KEY_COLLECTION_USER)
                .document(pref.getString(KEYS.KEY_USER_ID));
    }

    @Override
    protected void onPause() {
        super.onPause();
        documentReference.update(KEYS.KEY_AVAILABILITY,0);
    }
    @Override
    protected void onResume() {
        super.onResume();
        documentReference.update(KEYS.KEY_AVAILABILITY,1);
    }
}
