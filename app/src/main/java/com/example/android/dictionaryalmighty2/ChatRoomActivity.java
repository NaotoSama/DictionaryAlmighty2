package com.example.android.dictionaryalmighty2;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

import static com.example.android.dictionaryalmighty2.MainActivity.mChildReferenceForChatMessages;

public class ChatRoomActivity extends AppCompatActivity {

    private EditText editMessage;
    private RecyclerView mChatMessageList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);


        editMessage = findViewById(R.id.edit_message);
        mChatMessageList = (RecyclerView) findViewById(R.id.message_recyclerView);
        mChatMessageList.setHasFixedSize(true);
        Context context;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true); //讓用戶入的新訊息出現在RecyclerView尾端
        mChatMessageList.setLayoutManager(linearLayoutManager);



    }



    public void sendButtonClicked (View view) {

        final String messageValue= editMessage.getText().toString().trim();

        if (!TextUtils.isEmpty(messageValue)) {
            final DatabaseReference newPost = mChildReferenceForChatMessages.push();
            newPost.child("content").setValue(messageValue);
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter <Message,MessageViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                Message.class, R.layout.single_message_layout,MessageViewHolder.class,mChildReferenceForChatMessages) {

            @Override
            protected void populateViewHolder(MessageViewHolder messageViewHolder, Message message, int position) {
                messageViewHolder.setContent(message.getContent());
            }
        };
        mChatMessageList.setAdapter(firebaseRecyclerAdapter);

    }



    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setContent(String content) {
            TextView chatMessage = (TextView) mView.findViewById(R.id.message_text);
            chatMessage.setText(content);

        }
    }



}
