package com.dell.blackboard.view;

import com.dell.blackboard.objects.ResourceBucketObject;

import java.util.ArrayList;

public interface ResourceBucketView {


    void uploadSuccess();
    void loadSuccess(ArrayList<ResourceBucketObject> bucketObjects);

}
