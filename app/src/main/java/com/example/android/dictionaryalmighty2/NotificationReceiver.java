//package com.example.android.dictionaryalmighty2;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Handler;
//
//import static com.example.android.dictionaryalmighty2.MainActivity.comboSearchButton;
//import static com.example.android.dictionaryalmighty2.WordsToMemorize.vocabulariesToBeMemorized;
//
//public class NotificationReceiver extends BroadcastReceiver{
//    static String wordToMemorize = "";
//    @Override
//    public void onReceive(final Context context, Intent intent) {
//
//
//        if (vocabulariesToBeMemorized.peek() != null) {
//            wordToMemorize = vocabulariesToBeMemorized.poll();
//        }
//
//
//        Intent launchMainActivityIntent = context.getPackageManager().getLaunchIntentForPackage("com.example.android.dictionaryalmighty2");
//        if (launchMainActivityIntent != null) {
//            context.startActivity(launchMainActivityIntent);//null pointer check in case package name was not found
//        }
//
//
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//
//                MainActivity.wordInputView.setText(wordToMemorize);
//                comboSearchButton.performClick();
//
//            }
//        }, 1000);   //1 second delay
//
//    }
//
//
//
//}
