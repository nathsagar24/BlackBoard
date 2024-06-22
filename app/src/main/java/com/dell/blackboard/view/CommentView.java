package com.dell.blackboard.view;

import com.dell.blackboard.objects.CommentObject;

import java.util.ArrayList;

public interface CommentView {

    void commmentLoaded(ArrayList<CommentObject> commentObjects);
    void commentNotify();
}
