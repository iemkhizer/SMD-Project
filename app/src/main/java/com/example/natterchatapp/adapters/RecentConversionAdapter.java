package com.example.natterchatapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natterchatapp.databinding.ItemContainerRecentConversionBinding;
import com.example.natterchatapp.listeners.ConversionListener;
import com.example.natterchatapp.models.ChatMessage;
import com.example.natterchatapp.models.User;

import java.util.ArrayList;

public class RecentConversionAdapter extends RecyclerView.Adapter<RecentConversionAdapter.ConversionViewHolder>{
    private final ArrayList<ChatMessage> chatMessages;
    private final ConversionListener conversionListener;

    public RecentConversionAdapter(ArrayList<ChatMessage> chatMessages, ConversionListener conversionListener) {
        this.chatMessages = chatMessages;
        this.conversionListener = conversionListener;
    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(
                ItemContainerRecentConversionBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {
        holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }


    class ConversionViewHolder extends RecyclerView.ViewHolder{
        ItemContainerRecentConversionBinding binding;


        ConversionViewHolder( ItemContainerRecentConversionBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }

        void setData(ChatMessage chatMessage){
            binding.rivProfileUser.setImageBitmap(getBitmapFromString(chatMessage.getConversionImage()));
            binding.tvNameUser.setText(chatMessage.getConversionName());
            binding.tvRecentMessage.setText(chatMessage.getMessage());
            binding.getRoot().setOnClickListener(v->{
                User user=new User();
                user.setId(chatMessage.getConversionId());
                user.setName(chatMessage.getConversionName());
                user.setImage(chatMessage.getConversionImage());
                conversionListener.onConversionClicked(user);
            });
        }

    }

    private Bitmap getBitmapFromString(String encoded) {
        byte[] bytes = Base64.decode(encoded, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


}