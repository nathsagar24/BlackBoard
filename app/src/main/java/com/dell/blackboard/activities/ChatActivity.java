package com.dell.blackboard.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.dell.blackboard.R;
import com.dell.blackboard.adapters.AdapterChat;
import com.dell.blackboard.model.ChatModel;
import com.dell.blackboard.objects.ChatObject;
import com.dell.blackboard.objects.UserObject;
import com.dell.blackboard.view.ChatView;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.dell.blackboard.Common.CURRENT_USER;
import static com.dell.blackboard.Common.SELECTED_CHAT_UID;

public class ChatActivity extends AppCompatActivity implements ChatView {

    EmojiEditText et_chatBox;
    Button bt_chatSend;
    ChatModel model;
    String node;
    ImageButton emojiButton;
    ArrayList<ChatObject> chatObjects;
    RecyclerView recyclerView;
    LinearLayoutManager llm;
    AdapterChat adapterChat;
    UserObject chatUser;
    String referenceTEXT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(findViewById(R.id.root_chat_activity))
                .build(findViewById(R.id.et_chatbox));
        et_chatBox = findViewById(R.id.et_chatbox);
        bt_chatSend = findViewById(R.id.bt_chatbox_send);
        referenceTEXT = getIntent().getStringExtra("suffix");
        bt_chatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!et_chatBox.getText().toString().isEmpty()){
                    if (!et_chatBox.getText().toString().equals("")){
                        String time = new SimpleDateFormat("h:mm a MMM d, ''yy").format(new Date());
                        int compare = CURRENT_USER.UID.compareTo(SELECTED_CHAT_UID);
                        String node = "";
                        if (compare<0){
                            node = CURRENT_USER.UID+":"+SELECTED_CHAT_UID;
                        }
                        else {
                            node = SELECTED_CHAT_UID+":"+CURRENT_USER.UID;
                        }
                        ChatObject chatObject;
                        if (referenceTEXT != null) {
                            chatObject = new ChatObject(
                                    et_chatBox.getText().toString() + "\nAssignment: " + referenceTEXT,
                                    CURRENT_USER.UID,
                                    SELECTED_CHAT_UID,
                                    time);
                        }
                        else{
                            chatObject = new ChatObject(
                                    et_chatBox.getText().toString(),
                                    CURRENT_USER.UID,
                                    SELECTED_CHAT_UID,
                                    time);
                        }
                        model.performMessageSent(chatObject,node,SELECTED_CHAT_UID);
                        et_chatBox.setText("");
                    }
                }
            }
        });
        emojiButton = findViewById(R.id.imgbt_emoji_chatActivity);
        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emojiPopup.isShowing()){
                    emojiPopup.dismiss(); // Dismisses the Popup.
                }
                else{
                    emojiPopup.toggle(); // Toggles visibility of the Popup.
                }
            }
        });

    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onStart() {
        super.onStart();
        int compare = CURRENT_USER.UID.compareTo(SELECTED_CHAT_UID);
        node = "";
        if (compare<0){
            node = CURRENT_USER.UID+":"+SELECTED_CHAT_UID;
        }
        else {
            node = SELECTED_CHAT_UID +":"+CURRENT_USER.UID;
        }
        model = new ChatModel(this);
        model.performChatCountReset(node);
        model.performChatUserDataLoad(SELECTED_CHAT_UID);
        chatObjects = new ArrayList<>();
        recyclerView = findViewById(R.id.reyclerview_message_list);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        model.performChatLoad(node,chatObjects);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        model.removeListener();
    }

    @Override
    public void chatLoadSuccess(ArrayList<ChatObject> chatObjects) {
        adapterChat = new AdapterChat(chatObjects,chatUser);
        recyclerView.setAdapter(adapterChat);
    }

    @Override
    public void chatUpdate() {
        llm.scrollToPosition(chatObjects.size()-1);
        adapterChat.notifyDataSetChanged();
    }

    @Override
    public void sendSuccess() {

    }

    @Override
    public void chatUserDataLoad(UserObject userObject) {
        setTitle(userObject.Name);
        chatUser = userObject;
    }
}
