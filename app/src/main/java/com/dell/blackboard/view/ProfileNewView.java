package com.dell.blackboard.view;

import com.dell.blackboard.objects.UserObject;

public interface ProfileNewView {

    void profilePicUploadSuccess();
    void profilePicUploadFailed();

    void helloSendSuccess();
    void helloSendFailed();

    void profileLoadSuccess(UserObject userObject);
    void profilePictureLoadFailed();

    void helloStatusCheckSuccess(int hello);
}
