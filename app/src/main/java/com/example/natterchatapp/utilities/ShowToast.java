package com.example.natterchatapp.utilities;

import android.content.Context;
import android.widget.Toast;

public class ShowToast {
    Context context;

    public ShowToast(Context context) {
        this.context = context;
    }

    public void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
