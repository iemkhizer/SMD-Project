package com.example.natterchatapp.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natterchatapp.databinding.ItemContainerRecieveMessageBinding;
import com.example.natterchatapp.databinding.ItemContainerSentMessageBinding;
import com.example.natterchatapp.models.ChatMessage;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ArrayList<ChatMessage> chatMessages;
    private final String senderId;
    private  Bitmap receiverProfileImage;

    public void setReceiverProfileImage(Bitmap receiverProfileImage) {
        this.receiverProfileImage = receiverProfileImage;
    }

    public static int VIEW_TYPE_SENT = 1;
    public static int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(ArrayList<ChatMessage> chatMessages, String senderId, Bitmap receiverProfileImage) {
        this.chatMessages = chatMessages;
        this.senderId = senderId;
        this.receiverProfileImage = receiverProfileImage;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent, false
                    )
            );
        } else {
            return new ReceiverMessageViewHolder(
                    ItemContainerRecieveMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent, false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)==VIEW_TYPE_SENT){
            ((SentMessageViewHolder)holder).setData(chatMessages.get(position));
        }else {
            ((ReceiverMessageViewHolder)holder).setData(chatMessages.get(position),receiverProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).getSenderId().equals(senderId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setData(ChatMessage message) {
            binding.tvSentMessage.setText(message.getMessage());
            binding.tvSentDateTime.setText(message.getDateTime());
        }
    }

    static class ReceiverMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerRecieveMessageBinding binding;

        ReceiverMessageViewHolder(ItemContainerRecieveMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setData(ChatMessage message, Bitmap profile) {
            if(profile!=null) {
                binding.rivReceivedMessageProfile.setImageBitmap(profile);
            }
            binding.tvReceivedMessage.setText(message.getMessage());
            binding.tvReceivedDateTime.setText(message.getDateTime());
        }
    }
}