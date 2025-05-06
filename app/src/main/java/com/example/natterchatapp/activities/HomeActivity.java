package com.example.natterchatapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.natterchatapp.adapters.RecentConversionAdapter;
import com.example.natterchatapp.databinding.ActivityHomeBinding;
import com.example.natterchatapp.listeners.ConversionListener;
import com.example.natterchatapp.models.ChatMessage;
import com.example.natterchatapp.models.User;
import com.example.natterchatapp.utilities.KEYS;
import com.example.natterchatapp.utilities.Preference;
import com.example.natterchatapp.utilities.ShowToast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class HomeActivity extends BaseActivity implements ConversionListener {

    private Preference pref;
    private ShowToast toast;
    private ActivityHomeBinding binding;
    private ArrayList<ChatMessage> conversations;
    private RecentConversionAdapter recentConversionAdapter;
    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        loadUser();
        getToken();

        binding.acivLogout.setOnClickListener(v -> logout());
        binding.fabAdd.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), UsersActivity.class)));
        listenConversations();
    }

    private void logout() {

        toast.showToast("Logging Out....");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dr = db.collection(KEYS.KEY_COLLECTION_USER)
                .document(
                        pref.getString(KEYS.KEY_USER_ID)
                );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(KEYS.KEY_FCM_TOKEN, FieldValue.delete());
        dr.update(updates)
                .addOnSuccessListener(unused -> {
                    pref.clear();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> toast.showToast("Unable to sign out"));
    }

    private void loadUser() {
        binding.tvHomeName.setText(pref.getString(KEYS.KEY_USER_NAME));
        byte[] bytes = Base64.decode(pref.getString(KEYS.KEY_USER_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.rivProfile.setImageBitmap(bitmap);
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void listenConversations() {
        database.collection(KEYS.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(KEYS.KEY_SENDER_ID,pref.getString(KEYS.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(KEYS.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(KEYS.KEY_RECEIVER_ID,pref.getString(KEYS.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderId = documentChange.getDocument().getString(KEYS.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(KEYS.KEY_RECEIVER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setSenderId(senderId);
                    chatMessage.setReceiverId(receiverId);
                    if (pref.getString(KEYS.KEY_USER_ID).equals(senderId)) {
                        chatMessage.setConversionImage(documentChange.getDocument().getString(KEYS.KEY_RECEIVER_IMAGE));
                        chatMessage.setConversionName(documentChange.getDocument().getString(KEYS.KEY_RECEIVER_NAME));
                        chatMessage.setConversionId(documentChange.getDocument().getString(KEYS.KEY_RECEIVER_ID));
                    } else {
                        chatMessage.setConversionImage(documentChange.getDocument().getString(KEYS.KEY_SENDER_IMAGE));
                        chatMessage.setConversionName(documentChange.getDocument().getString(KEYS.KEY_SENDER_NAME));
                        chatMessage.setConversionId(documentChange.getDocument().getString(KEYS.KEY_SENDER_ID));
                    }
                    chatMessage.setMessage(documentChange.getDocument().getString(KEYS.KEY_LAST_MESSAGE));
                    chatMessage.setDateObj(documentChange.getDocument().getDate(KEYS.KEY_TIMESTAMP));
                    conversations.add(chatMessage);
                } else {
                    for (int i = 0; i < conversations.size(); i++) {
                        String senderId = documentChange.getDocument().getString(KEYS.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(KEYS.KEY_RECEIVER_ID);
                        if (conversations.get(i).getSenderId().equals(senderId) &&
                                conversations.get(i).getReceiverId().equals(receiverId)
                        ) {
                            conversations.get(i).setMessage(documentChange.getDocument().getString(KEYS.KEY_LAST_MESSAGE));
                            conversations.get(i).setDateObj(documentChange.getDocument().getDate(KEYS.KEY_TIMESTAMP));
                            break;
                        }
                    }
                }
            }
            Collections.sort(conversations, (obj1, obj2) -> obj2.getDateObj().compareTo(obj1.getDateObj()));
            recentConversionAdapter.notifyDataSetChanged();
            binding.conversationRecyclerView.smoothScrollToPosition(0);
            binding.conversationRecyclerView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    };

    private void updateToken(String token) {
        pref.putString(KEYS.KEY_FCM_TOKEN,token);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dr = db.collection(KEYS.KEY_COLLECTION_USER)
                .document(
                        pref.getString(KEYS.KEY_USER_ID)
                );
        dr.update(KEYS.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> {
                    toast.showToast("Token no");
                });
    }

    private void init() {
        pref = new Preference(HomeActivity.this);
        toast = new ShowToast(HomeActivity.this);
        conversations = new ArrayList<>();
        recentConversionAdapter = new RecentConversionAdapter(conversations,this);
        binding.conversationRecyclerView.setAdapter(recentConversionAdapter);
        database = FirebaseFirestore.getInstance();


    }


    @Override
    public void onConversionClicked(User user) {
        Intent intent=new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra(KEYS.KEY_USER,user);
        startActivity(intent);
    }
}