package com.example.android.dictionaryalmighty2;

public class FirebaseDataBaseUserInputHistory {

    private String vocabulary;
    private String timestamp;


    //Constructor (Select none)
    public FirebaseDataBaseUserInputHistory() {
    }


    //Constructor for all strings
    public FirebaseDataBaseUserInputHistory(String vocabulary, String timestamp) {
        this.vocabulary = vocabulary;
        this.timestamp = timestamp;
    }


    //Vocabulary getter and setter
    public String getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(String vocabulary) {
        this.vocabulary = vocabulary;
    }


    //Timestamp getter and setter
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }



}
