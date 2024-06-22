package com.dell.blackboard.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.dell.blackboard.objects.ChatListMasterObject;
import com.dell.blackboard.objects.ChatObject;
import com.dell.blackboard.objects.UserObject;
import com.dell.blackboard.view.MainActivityView;

import java.util.HashMap;

import static com.dell.blackboard.Common.CURRENT_USER;
import static com.dell.blackboard.Common.mREF_chat;
import static com.dell.blackboard.Common.mREF_users;

public class ChatListModel {

    MainActivityView mainActivityView;
    HashMap<String,ChatListMasterObject> objectHashMap;
    boolean flag = false;
    public ChatListModel(MainActivityView mainActivityView, HashMap<String,ChatListMasterObject> objectHashMap) {
        this.mainActivityView = mainActivityView;
        this.objectHashMap = objectHashMap;
    }

    public void performChatListLoad(String UID){
        mREF_users.child(UID).child("chat_index").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatListMasterObject object =
                        new ChatListMasterObject(null,null,dataSnapshot.getValue().toString());
                objectHashMap.put(dataSnapshot.getKey(),object);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatListMasterObject object = objectHashMap.get(dataSnapshot.getKey());
                object.chatCounts = dataSnapshot.getValue().toString();
                objectHashMap.put(dataSnapshot.getKey(),object);
                chatUserObject();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mREF_users.child(UID).child("chat_index").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatUserObject();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void chatLastText(){
        for (String key: objectHashMap.keySet()) {
            mREF_chat.child(key).orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        if (dataSnapshot.getValue() != null) {
                            ChatObject chatObject = s.getValue(ChatObject.class);
                            ChatListMasterObject object = objectHashMap.get(key);
                            object.lastText = chatObject.message;
                            objectHashMap.put(key,object);
                        }
                    }
                    checkNewMessage();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //Handle possible errors.
                }
            });
        }

    }
    private void chatUserObject(){
        mREF_users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    for (String s: objectHashMap.keySet()){
                        String recieverUID = s.replace(CURRENT_USER.UID,"");
                        recieverUID = recieverUID.replace(":","");
                        ChatListMasterObject object = objectHashMap.get(s);
                        object.userObjects = dataSnapshot.child(recieverUID).getValue(UserObject.class);
                        objectHashMap.put(s,object);
                    }
                    chatLastText();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public HashMap<String,ChatListMasterObject> getChatListMasterObject(){
        return this.objectHashMap;
    }

    private void checkNewMessage(){
        for(String s : objectHashMap.keySet()){
            if (!objectHashMap.get(s).chatCounts.equals("0")){
                flag = true;
            }
        }
        if (flag){
            mainActivityView.newChatNotif(null);
            flag = false;
        }
    }
}
