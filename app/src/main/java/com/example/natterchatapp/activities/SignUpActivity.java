package com.example.natterchatapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.natterchatapp.R;
import com.example.natterchatapp.utilities.KEYS;
import com.example.natterchatapp.utilities.Preference;
import com.example.natterchatapp.utilities.ShowToast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPass, etConfirmPass;
    private Button btnSignUp;
    private TextView tvBackToLogin, tvAddImage;
    private RoundedImageView riProfilePic;
    private FrameLayout layoutImage;
    private String encodedImage = "";
    private Preference sharedPref;
    private ShowToast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    storeToFirebase();

                }
            }
        });
        tvBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        layoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });
    }

    private void storeToFirebase() {
        final String email = etEmail.getText().toString().trim();
        final String name = etName.getText().toString().trim();
        final String password = etPass.getText().toString().trim();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(KEYS.KEY_COLLECTION_USER)
                .whereEqualTo(KEYS.KEY_USER_EMAIL, email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean userExists = !task.getResult().isEmpty();
                            if (userExists) {
                                // User with this email already exists
                                toast.showToast("Email already exists!");
                            } else {
                                // User with this email does not exist, proceed with registration
                                HashMap<String, Object> data = new HashMap<>();
                                data.put(KEYS.KEY_USER_NAME, name);
                                data.put(KEYS.KEY_USER_EMAIL, email);
                                data.put(KEYS.KEY_USER_PASSWORD, password);
                                data.put(KEYS.KEY_USER_IMAGE, encodedImage); // Assuming 'encodedImage' is already set

                                db.collection(KEYS.KEY_COLLECTION_USER)
                                        .add(data)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                // Registration successful
                                                toast.showToast("Registration Successful");
                                                sharedPref.puBoolean(KEYS.KEY_USER_IS_SIGNED_IN, true);
                                                sharedPref.putString(KEYS.KEY_USER_ID, documentReference.getId());
                                                sharedPref.putString(KEYS.KEY_USER_NAME, name);
                                                sharedPref.putString(KEYS.KEY_USER_IMAGE, encodedImage);

                                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                toast.showToast("Welcome " + sharedPref.getString(KEYS.KEY_USER_NAME));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Registration failed
                                                toast.showToast("Registration Failed");
                                            }
                                        });
                            }
                        } else {
                            // Error occurred while checking for user
                            Log.w("TAG", "Error checking for user", task.getException());
                            toast.showToast("Error checking for user");
                        }
                    }
                });
    }


    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            if (imageUri != null) {
                                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                riProfilePic.setImageBitmap(bitmap);
                                tvAddImage.setVisibility(View.GONE);
                                encodedImage = encodeImage(bitmap);
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private String encodeImage(Bitmap bitmap) {
        int width = 150;
        int height = bitmap.getHeight() * width / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private boolean validate() {
        boolean flag = true;
        if (etName.getText().toString().trim().isEmpty()) {
            toast.showToast("Name field can not be empty");
            flag = false;
        } else if (etEmail.getText().toString().trim().isEmpty()) {
            toast.showToast("Email field can not be empty");
            flag = false;
        } else if (etPass.getText().toString().trim().isEmpty()) {
            flag = false;
            toast.showToast("Password field can not be empty");
        } else if (etConfirmPass.getText().toString().trim().isEmpty()) {
            flag = false;
            toast.showToast("Confirm Password field can not be empty");
        } else if (!etPass.getText().toString().trim().equals(etConfirmPass.getText().toString().trim())) {
            flag = false;
            toast.showToast("Password does not match");
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()) {
            flag = false;
            toast.showToast("Incorrect Email Format");
        } else if (encodedImage.isEmpty()) {
            flag = false;
            toast.showToast("Add Image");
        }

        return flag;
    }

    private void init() {
        etName = findViewById(R.id.etSName);
        etEmail = findViewById(R.id.etSEmail);
        etPass = findViewById(R.id.etSPass);
        etConfirmPass = findViewById(R.id.etSConfirmPass);

        btnSignUp = findViewById(R.id.btnSignUp);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        tvAddImage = findViewById(R.id.tvAddImage);
        riProfilePic = findViewById(R.id.riProfilePic);
        layoutImage = findViewById(R.id.layoutImage);

        toast = new ShowToast(SignUpActivity.this);

        sharedPref = new Preference(getApplicationContext());
    }
}