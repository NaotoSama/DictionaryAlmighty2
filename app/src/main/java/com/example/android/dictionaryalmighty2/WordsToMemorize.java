package com.example.android.dictionaryalmighty2;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.WorkManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Locale;

import br.com.goncalves.pugnotification.notification.PugNotification;

import static com.example.android.dictionaryalmighty2.MainActivity.myVocabularyArrayList;
import static com.example.android.dictionaryalmighty2.MainActivity.wordInputView;
import static com.example.android.dictionaryalmighty2.MainActivity.wordToMemorize1;
import static com.example.android.dictionaryalmighty2.MainActivity.wordToMemorize2;
import static com.example.android.dictionaryalmighty2.MainActivity.wordToMemorize3;

public class WordsToMemorize extends AppCompatActivity {

    RelativeLayout.LayoutParams layoutparams;   //用來客製化修改ActionBar
    TextView customActionBarTextviewforUserInputHistoryPage;
    androidx.appcompat.app.ActionBar actionBar;

    static LinkedList<String> vocabulariesToBeMemorized = new LinkedList<>();
    Button cancelAllNotifications;

    String selectedMyVocabularyListviewItemValue;

    TextView memoryTree1;
    TextView memoryTree2;
    TextView memoryTree3;

    Boolean wordToMemorize1NotificationIsOn;
    Boolean wordToMemorize2NotificationIsOn;
    Boolean wordToMemorize3NotificationIsOn;

    static SharedPreferences wordToMemorizeSharedPreferences;


    Calendar c;
    WorkManager mWorkManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.words_to_memorize);

        final ListView myVocabularyListview;
        final ArrayAdapter myVocabularyArrayAdapter;


        /**
         * 設置ActionBar
         */
        actionBar = getSupportActionBar();
        customActionBarTextviewforUserInputHistoryPage = new TextView(this);
        layoutparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        customActionBarForWordsToMemorizePage();   //Helper Method


        //findViewById
        myVocabularyListview = findViewById(R.id.my_vocabulary_listview);
        cancelAllNotifications = findViewById(R.id.cancel_all_notifications_button);
        memoryTree1 = findViewById(R.id.memory_tree_1);
        memoryTree2 = findViewById(R.id.memory_tree_2);
        memoryTree3 = findViewById(R.id.memory_tree_3);


        if (wordToMemorize1 != null) {
            memoryTree1.setText(wordToMemorize1);
        } else {memoryTree1.setText("Memory Tree 1");}

        if (wordToMemorize2 != null) {
            memoryTree2.setText(wordToMemorize2);
        } else {memoryTree2.setText("Memory Tree 2");}

        if (wordToMemorize3 != null) {
            memoryTree3.setText(wordToMemorize3);
        }else {memoryTree3.setText("Memory Tree 3");}


        //Initialize the WorkManager
        mWorkManager = WorkManager.getInstance();


        //Initialize the adapter
        myVocabularyArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myVocabularyArrayList);
        myVocabularyListview.setAdapter(myVocabularyArrayAdapter);



        /**
         * 讓用戶取消所有通知
         */
        cancelAllNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WorkManager.getInstance().cancelAllWork();

                Toast.makeText(getApplicationContext(), getString(R.string.All_notifications_cancelled),Toast.LENGTH_SHORT).show();

            }
        });


        /**
         * Let the user click on an item and set notification timings
         */
        myVocabularyListview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                selectedMyVocabularyListviewItemValue=myVocabularyListview.getItemAtPosition(position).toString();

                //這邊設置AlertDialog讓用戶選擇一顆記憶樹
                final AlertDialog.Builder chooseMemoryTreeAlertDialog = new AlertDialog.Builder(WordsToMemorize.this);
                chooseMemoryTreeAlertDialog.setTitle(getString(R.string.Choose_a_memory_tree));
                chooseMemoryTreeAlertDialog.setCancelable(false); //按到旁邊的空白處AlertDialog也不會消失
                chooseMemoryTreeAlertDialog.setView(R.layout.custom_alert_dialog_dictionary_providers); //沿用字典選單的佈局檔

                //checkedItem:-1的意思是指預設不選中任何項目，若要預設選種第一項則設置成0，第二項則為1...
                chooseMemoryTreeAlertDialog.setSingleChoiceItems((R.array.memory_tree), -1, new DialogInterface.OnClickListener() {

                    SharedPreferences.Editor editor = getSharedPreferences("memoryTreeSharedPreference", MODE_PRIVATE).edit();

                    @Override
                    public void onClick(DialogInterface chooseMemoryTreeAlertDialogInterface, int position) {

                        if (position == 0) {  //若用戶點選記憶樹1
                            wordToMemorizeSharedPreferences = getSharedPreferences("memoryTreeSharedPreference", MODE_PRIVATE);
                            editor.putString("memoryTree1", selectedMyVocabularyListviewItemValue);
                            editor.apply();
                            wordToMemorize1 = wordToMemorizeSharedPreferences.getString("memoryTree1", null);

                            memoryTree1.setText(wordToMemorize1);
                            wordToMemorize1NotificationIsOn = true;
                            setCustomizedNotificationTiming();
                            Toast.makeText(getApplicationContext(),wordToMemorize1,Toast.LENGTH_SHORT).show();
                        } else if (position == 1) { //若用戶點選記憶樹2
                            wordToMemorizeSharedPreferences = getSharedPreferences("memoryTreeSharedPreference", MODE_PRIVATE);
                            editor.putString("memoryTree2", selectedMyVocabularyListviewItemValue);
                            editor.apply();
                            wordToMemorize2 = wordToMemorizeSharedPreferences.getString("memoryTree2", null);

                            memoryTree2.setText(wordToMemorize2);
                            wordToMemorize2NotificationIsOn = true;
                            setCustomizedNotificationTiming();
                            Toast.makeText(getApplicationContext(),wordToMemorize2,Toast.LENGTH_SHORT).show();

                        } else if (position == 2) { //若用戶點選記憶樹3
                            wordToMemorizeSharedPreferences = getSharedPreferences("memoryTreeSharedPreference", MODE_PRIVATE);
                            editor.putString("memoryTree3", selectedMyVocabularyListviewItemValue);
                            editor.apply();
                            wordToMemorize3 = wordToMemorizeSharedPreferences.getString("memoryTree3", null);

                            memoryTree3.setText(wordToMemorize3);
                            wordToMemorize3NotificationIsOn = true;
                            setCustomizedNotificationTiming();
                            Toast.makeText(getApplicationContext(),wordToMemorize3,Toast.LENGTH_SHORT).show();
                        }

                        chooseMemoryTreeAlertDialogInterface.dismiss();  //點選記憶樹後讓AlertDialog的介面消失
                    }
                });

                //第一層AlertDialog的取消鈕
                chooseMemoryTreeAlertDialog.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                //把第一層的AlertDialog顯示出來
                chooseMemoryTreeAlertDialog.create().show();
            }
        });



        /**
         * Let the user long click on an item to cancel notification timings
         */
        myVocabularyListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

//                mWorkManager.cancelAllWorkByTag("UserDefinedNotificationTag" + " for " + wordToMemorize);
//                mWorkManager.cancelAllWorkByTag("preDefinedNotificationTag" + " for " + wordToMemorize);

                Toast.makeText(getApplicationContext(), getString(R.string.All_notifications_of_this_word_cancelled), Toast.LENGTH_SHORT).show();

                return true;
            }

        });



        /**
         * Let the user swipe on an item and delete the item
         */
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        myVocabularyListview,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    myVocabularyArrayList.remove(position);
                                    myVocabularyArrayAdapter.notifyDataSetChanged();

                                    //將搜尋紀錄的列表存到SharedPreferences
                                    SharedPreferences.Editor editor = getSharedPreferences("myVocabularyArrayListSharedPreferences", MODE_PRIVATE).edit();
                                    editor.putInt("myVocabularyArrayListValues", myVocabularyArrayList.size());
                                    for (int i = 0; i < myVocabularyArrayList.size(); i++)
                                    {
                                        editor.putString("myVocabularyArrayListItem_"+i, myVocabularyArrayList.get(i));
                                    }
                                    editor.apply();

                                    Toast.makeText(getApplicationContext(), R.string.Your_selected_item_has_benn_deleted, Toast.LENGTH_SHORT).show();

                                }


                            }
                        });
        myVocabularyListview.setOnTouchListener(touchListener);





    }



    //==============================================================================================
    // 設置客製化ActionBar的Helper Method
    //==============================================================================================
    public void customActionBarForWordsToMemorizePage() {

        customActionBarTextviewforUserInputHistoryPage.setLayoutParams(layoutparams);
        customActionBarTextviewforUserInputHistoryPage.setText(getString(R.string.Words_to_memorize));
        customActionBarTextviewforUserInputHistoryPage.setTextSize(20);
        customActionBarTextviewforUserInputHistoryPage.setGravity(Gravity.CENTER);
        customActionBarTextviewforUserInputHistoryPage.setTypeface(Typeface.DEFAULT_BOLD);
        customActionBarTextviewforUserInputHistoryPage.setTextColor(Color.parseColor("#58BE1B"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD700")));
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(customActionBarTextviewforUserInputHistoryPage);

    }


    @Override
    protected void onStart() {
        super.onStart();

        c = Calendar.getInstance(); // 取得目前日期與時間
        /*
        這行是一個隱式宣告，事實上是new了一個新的Calendar物件。
        關於c = Calendar.getInstance到底要寫在onCreate()還是onStart()各有優劣。
        寫在onCreate()裡面可以讓c的生命週期一直延續到onDestroy()，不需要去recheck c裡面的內容；

        寫在onStart()裡面則是每一次從背景被翻出來就產生一個新的物件，同時就需要對新的物件的內容(初值)
        做檢查與設定。同時這樣並沒有節省記憶體空間，如果我們沒有在onStop()主動釋放掉c的話，這個物件
        還是會被保留在記憶體中，然而在onStart()時建立新物件的時候，舊物件則被Garbage Collector回收。

        但是，在某幾次的經驗，如果APP被放到背景太久，OS有機會會去回收部分APP使用的資源，但又不回收整個
        APP。這會讓APP重新回到onStart()的時候c變成一個未定義的狀態而產生系統錯誤。因此我個人的習慣是
        在onStart()的時候重新整理所有的變數，檢查其數值是否正確。
         */
    }




    //==============================================================================================
    // 設置自定義通知時機的Helper Method
    //==============================================================================================
    public void setCustomizedNotificationTiming() {

        vocabulariesToBeMemorized.add(wordInputView.getText().toString());

        // on Time
        new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c.set(Calendar.MINUTE, minute);

                        long nowMillis = System.currentTimeMillis();  //抓現在系統的時間
                        long millis = c.getTimeInMillis() - nowMillis; //用戶預定時間 減掉 現在系統時間 的差等於 送出通知的延遲時間

                        if (c.before(Calendar.getInstance())) {        //若用戶設定成過去的時間則不給設定
                            Toast.makeText(getApplicationContext(), getString(R.string.Hey_thats_too_early),Toast.LENGTH_LONG).show();

                        } else {                                       //若用戶設定成未來的時間則排程發送通知
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.TAIWAN);
                            Long scheduledDateInMilliSeconds=c.getTimeInMillis();
                            String FormattedScheduledDate = dateFormat.format(scheduledDateInMilliSeconds);
                            Toast.makeText(getApplicationContext(), getString(R.string.Will_send_the_notification_at) + FormattedScheduledDate + getString(R.string.blank_space),Toast.LENGTH_LONG).show();

                            //排程要發送的通知
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    if (wordToMemorize1NotificationIsOn = true) {

                                        PugNotification.with(getApplicationContext())
                                                .load()
                                                .identifier(1)
                                                .title("1")
                                                .message("HHHH")
                                                .smallIcon(R.mipmap.ic_launcher)
                                                .largeIcon(R.mipmap.ic_launcher)
                                                .flags(Notification.DEFAULT_ALL)
                                                .color(R.color.blue)
                                                .autoCancel(true)
                                                .simple()
                                                .build();

                                        wordInputView.setText(wordToMemorize1);
                                    }

                                    else if (wordToMemorize2NotificationIsOn = true) {

                                        PugNotification.with(getApplicationContext())
                                                .load()
                                                .identifier(2)
                                                .title("2")
                                                .message("AAAA")
                                                .smallIcon(R.mipmap.ic_launcher)
                                                .largeIcon(R.mipmap.ic_launcher)
                                                .flags(Notification.DEFAULT_ALL)
                                                .color(R.color.blue)
                                                .autoCancel(true)
                                                .simple()
                                                .build();

                                        wordInputView.setText(wordToMemorize2);
                                    }

                                    else if (wordToMemorize3NotificationIsOn = true) {

                                        PugNotification.with(getApplicationContext())
                                                .load()
                                                .identifier(3)
                                                .title("3")
                                                .message("GGGG")
                                                .smallIcon(R.mipmap.ic_launcher)
                                                .largeIcon(R.mipmap.ic_launcher)
                                                .flags(Notification.DEFAULT_ALL)
                                                .color(R.color.blue)
                                                .autoCancel(true)
                                                .simple()
                                                .build();

                                        wordInputView.setText(wordToMemorize3);
                                    }

                                }
                            }, millis);

                        }

                    }
                },
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                false).show();

        // on Date
        new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, monthOfYear);
                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    }
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)).show();

    }


    //==============================================================================================
    // 設置預設通知時機的Helper Method
    //==============================================================================================

//    public void setPreDefinedNotificationTimings() {
//
//        vocabulariesToBeMemorized.add(MainActivity.wordInputView.getText().toString());
//
//        OneTimeWorkRequest preDefinedNotification1 = new OneTimeWorkRequest.Builder(NotificationWorker.class)
//                .addTag("preDefinedNotificationTag" + " for " + wordToMemorize)
//                .setInitialDelay(1, TimeUnit.SECONDS)
//                .build();
//
//        OneTimeWorkRequest preDefinedNotification5 = new OneTimeWorkRequest.Builder(NotificationWorker.class)
//                .addTag("preDefinedNotificationTag" + " for " + wordToMemorize)
//                .setInitialDelay(30, TimeUnit.SECONDS)
//                .build();
//
//        OneTimeWorkRequest preDefinedNotification60 = new OneTimeWorkRequest.Builder(NotificationWorker.class)
//                .addTag("preDefinedNotificationTag" + " for " + wordToMemorize)
//                .setInitialDelay(1, TimeUnit.HOURS)
//                .build();
//
//        OneTimeWorkRequest preDefinedNotificationRequestOneHour = new OneTimeWorkRequest.Builder(NotificationWorker.class)
//                .addTag("preDefinedNotificationTag" + " for " + wordToMemorize)
//                .setInitialDelay(1, TimeUnit.HOURS)
//                .build();
//
//        OneTimeWorkRequest preDefinedNotificationRequestTwelveHours = new OneTimeWorkRequest.Builder(NotificationWorker.class)
//                .addTag("preDefinedNotificationTag" + " for " + wordToMemorize)
//                .setInitialDelay(12, TimeUnit.HOURS)
//                .build();
//
//        OneTimeWorkRequest preDefinedNotificationRequestOneDay = new OneTimeWorkRequest.Builder(NotificationWorker.class)
//                .addTag("preDefinedNotificationTag" + " for " + wordToMemorize)
//                .setInitialDelay(1, TimeUnit.DAYS)
//                .build();
//
//        OneTimeWorkRequest preDefinedNotificationRequestOneWeek = new OneTimeWorkRequest.Builder(NotificationWorker.class)
//                .addTag("preDefinedNotificationTag" + " for " + wordToMemorize)
//                .setInitialDelay(7, TimeUnit.DAYS)
//                .build();
//
//        OneTimeWorkRequest preDefinedNotificationRequestOneMonth = new OneTimeWorkRequest.Builder(NotificationWorker.class)
//                .addTag("preDefinedNotificationTag" + " for " + wordToMemorize)
//                .setInitialDelay(30, TimeUnit.DAYS)
//                .build();
//
//        OneTimeWorkRequest preDefinedNotificationRequestOneYear = new OneTimeWorkRequest.Builder(NotificationWorker.class)
//                .addTag("preDefinedNotificationTag" + " for " + wordToMemorize)
//                .setInitialDelay(365, TimeUnit.DAYS)
//                .build();
//
//        mWorkManager.enqueue(preDefinedNotification1);
//        mWorkManager.enqueue(preDefinedNotification5);
//        mWorkManager.enqueue(preDefinedNotification60);
//        mWorkManager.enqueue(preDefinedNotificationRequestOneHour);
//        mWorkManager.enqueue(preDefinedNotificationRequestTwelveHours);
//        mWorkManager.enqueue(preDefinedNotificationRequestOneDay);
//        mWorkManager.enqueue(preDefinedNotificationRequestOneWeek);
//        mWorkManager.enqueue(preDefinedNotificationRequestOneMonth);
//        mWorkManager.enqueue(preDefinedNotificationRequestOneYear);
//
//        Toast.makeText(getApplicationContext(), getString(R.string.Will_send_the_notification_after_an_hour_halfDay_day_week_month_year),Toast.LENGTH_LONG).show();
//        Toast.makeText(getApplicationContext(), getString(R.string.You_can_cancel_the_notifications_any_time),Toast.LENGTH_SHORT).show();
//
//    }




}
