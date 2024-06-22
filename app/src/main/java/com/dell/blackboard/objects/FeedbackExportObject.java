package com.dell.blackboard.objects;

public class FeedbackExportObject {
     public String ok,vgood,good,vbad,bad;

    public FeedbackExportObject(String ok, String vgood, String good, String vbad, String bad) {
        this.ok = ok;
        this.vgood = vgood;
        this.good = good;
        this.vbad = vbad;
        this.bad = bad;
    }

    public FeedbackExportObject() {
    }
}
