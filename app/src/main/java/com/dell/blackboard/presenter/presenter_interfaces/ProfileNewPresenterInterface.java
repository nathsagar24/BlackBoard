package com.dell.blackboard.presenter.presenter_interfaces;

import com.dell.blackboard.objects.UserObject;

public interface ProfileNewPresenterInterface {

    void profilePicUploadSuccess();
    void profilePicUploadFailed();

    void helloSendSuccess();
    void helloSendFailed();

    void profileLoadSuccess(UserObject object);
    void profilePictureLoadFailed();

    void helloStatusCheckSuccess(int hello);


}
