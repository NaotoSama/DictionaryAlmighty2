package com.example.android.dictionaryalmighty2;

public class Message {

    private String content;


    //Empty constructor
    public Message () {

    }

    public Message (String content) {
        this.content = content;
    }

    public  String getContent() {
        return content;
    }

    public void setContent (String content) {
        this.content = content;
    }
}
