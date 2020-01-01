package com.example.android.dictionaryalmighty2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;

import static com.example.android.dictionaryalmighty2.MainActivity.myVocabularyArrayList;
import static com.example.android.dictionaryalmighty2.MainActivity.userInputArraylist;

public class UserInputHistory extends AppCompatActivity {

    RelativeLayout.LayoutParams layoutparams;   //用來客製化修改ActionBar
    TextView customActionBarTextviewforUserInputHistoryPage;
    androidx.appcompat.app.ActionBar actionBar;

    String selectedListviewItemValue;

    Button clearUserInputList;

    Button goToWordsToMemorizePageButton;

    Calendar c;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_input_history);

        final ListView userInputListview;
        final ArrayAdapter userInputArrayAdapter;


        //findViewById
        goToWordsToMemorizePageButton = findViewById(R.id.go_to_words_to_memorize_page);
        userInputListview = findViewById(R.id.user_input_listview);
        clearUserInputList = findViewById(R.id.clear_user_input_list);

        //Initialize the adapter
        userInputArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userInputArraylist);
        userInputListview.setAdapter(userInputArrayAdapter);



        /**
         * 設置ActionBar
         */
        actionBar = getSupportActionBar();
        customActionBarTextviewforUserInputHistoryPage = new TextView(this);
        layoutparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        customActionBarForUserInputHistoryPage();   //Helper Method


        goToWordsToMemorizePageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(UserInputHistory.this, WordsToMemorize.class);
                startActivity(intent);

            }
        });


        /**
         * 讓用戶清空列表
         */
        clearUserInputList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userInputArraylist.clear();
                userInputArrayAdapter.notifyDataSetChanged();

                //將搜尋紀錄的列表存到SharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences("userInputArrayListSharedPreferences", MODE_PRIVATE).edit();
                editor.putInt("userInputArrayListValues", userInputArraylist.size());
                for (int i = 0; i < userInputArraylist.size(); i++) {
                    editor.putString("userInputArrayListItem_" + i, userInputArraylist.get(i));
                }
                editor.apply();

                Toast.makeText(getApplicationContext(), R.string.List_cleared, Toast.LENGTH_SHORT).show();
            }

        });



        /**
         * Let the user click on an item to pass the item value to wordInputView, or fire a calendar event to memorize words.
         */
        userInputListview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                selectedListviewItemValue=userInputListview.getItemAtPosition(position).toString();

                //這邊設置第一層AlertDialog讓用戶選擇把單字傳送到首頁的wordInputView, 或發佈記憶單字的通知
                AlertDialog.Builder passToWordInputViewOrFireCalendarEventAlertDialog = new AlertDialog.Builder(UserInputHistory.this);
                passToWordInputViewOrFireCalendarEventAlertDialog.setTitle(getString(R.string.Do_you_want_to));
                passToWordInputViewOrFireCalendarEventAlertDialog.setCancelable(false); //按到旁邊的空白處AlertDialog也不會消失
                passToWordInputViewOrFireCalendarEventAlertDialog.setView(R.layout.custom_alert_dialog_dictionary_providers); //沿用字典選單的佈局檔


                //第一層AlertDialog的確定鈕，把單字傳送到首頁的wordInputView
                passToWordInputViewOrFireCalendarEventAlertDialog.setPositiveButton(R.string.Send_to_WordInputView, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        MainActivity.wordInputView.setText(selectedListviewItemValue);

                        finish(); //結束此Activity並返回上一個Activity

                    }
                });

                //第一層AlertDialog的中立鈕，發佈記憶單字的通知。設置第二層AlertDialog讓用戶選擇自定義或預設的通知時機。
                passToWordInputViewOrFireCalendarEventAlertDialog.setNeutralButton(R.string.Memorize_this_word, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface CustomizedOrPredefinedNotificationDialogInterface, int which) {

                        //這邊設置第二層AlertDialog讓用戶選擇自定義或預設的通知時機
                        final AlertDialog.Builder chooseCustomizedOrPredefinedNotificationAlertDialog = new AlertDialog.Builder(UserInputHistory.this);
                        chooseCustomizedOrPredefinedNotificationAlertDialog.setTitle(getString(R.string.Choose_customized_or_predefined_notification_timings));
                        chooseCustomizedOrPredefinedNotificationAlertDialog.setCancelable(false); //按到旁邊的空白處AlertDialog也不會消失
                        chooseCustomizedOrPredefinedNotificationAlertDialog.setView(R.layout.custom_alert_dialog_dictionary_providers); //沿用字典選單的佈局檔


                        //第二層AlertDialog的確定鈕，自定義通知時機
                        chooseCustomizedOrPredefinedNotificationAlertDialog.setPositiveButton(R.string.Customize_timing, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                setCustomizedNotificationTiming();

                            }
                        });

                        //第二層AlertDialog的中立鈕，預設的通知時機。
                        chooseCustomizedOrPredefinedNotificationAlertDialog.setNeutralButton(R.string.Use_predefined_timing, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                setPreDefinedNotificationTimings1Minute();
                                setPreDefinedNotificationTimings2Minutes();

                            }
                        });

                        //第二層AlertDialog的取消鈕
                        chooseCustomizedOrPredefinedNotificationAlertDialog.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });


                        //把第二層的AlertDialog顯示出來
                        chooseCustomizedOrPredefinedNotificationAlertDialog.create().show();
                        //同時讓第一層的AlertDialog消失
                        CustomizedOrPredefinedNotificationDialogInterface.dismiss();

                    }
                });

                //第一層AlertDialog的取消鈕
                passToWordInputViewOrFireCalendarEventAlertDialog.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                //把第一層的AlertDialog顯示出來
                passToWordInputViewOrFireCalendarEventAlertDialog.create().show();

            }
        });



        /**
         * Let the user long click on an item, go to WordsToMemorize Page to save the item to that Page.
         */
        userInputListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedListviewItemValue=userInputListview.getItemAtPosition(position).toString();

                myVocabularyArrayList.add(selectedListviewItemValue);

                //透過HashSet自動過濾掉myVocabularyArraylist中重複的字
                HashSet<String> myVocabularyArraylistHashSet = new HashSet<>();
                myVocabularyArraylistHashSet.addAll(myVocabularyArrayList);
                myVocabularyArrayList.clear();
                myVocabularyArrayList.addAll(myVocabularyArraylistHashSet);

                //Alphabetic sorting
                Collections.sort(myVocabularyArrayList);

                saveMyVocabularyArrayListToSharedPreferences();

                Intent intent = new Intent(UserInputHistory.this, WordsToMemorize.class);
                intent.putExtra("selectedListviewItemValue",myVocabularyArrayList);
                startActivity(intent);

                return true;
            }

        });



        /**
         * Let the user swipe on an item and delete the item
         */
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        userInputListview,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    userInputArraylist.remove(position);
                                    userInputArrayAdapter.notifyDataSetChanged();

                                    //將搜尋紀錄的列表存到SharedPreferences
                                    SharedPreferences.Editor editor = getSharedPreferences("userInputArrayListSharedPreferences", MODE_PRIVATE).edit();
                                    editor.putInt("userInputArrayListValues", userInputArraylist.size());
                                    for (int i = 0; i < userInputArraylist.size(); i++)
                                    {
                                        editor.putString("userInputArrayListItem_"+i, userInputArraylist.get(i));
                                    }
                                    editor.apply();

                                    Toast.makeText(getApplicationContext(), R.string.Your_selected_item_has_benn_deleted, Toast.LENGTH_SHORT).show();

                                }


                            }
                        });
        userInputListview.setOnTouchListener(touchListener);


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
    // 設置客製化ActionBar的Helper Method
    //==============================================================================================
    public void customActionBarForUserInputHistoryPage() {

        customActionBarTextviewforUserInputHistoryPage.setLayoutParams(layoutparams);
        customActionBarTextviewforUserInputHistoryPage.setText(getString(R.string.Your_search_history));
        customActionBarTextviewforUserInputHistoryPage.setTextSize(20);
        customActionBarTextviewforUserInputHistoryPage.setGravity(Gravity.CENTER);
        customActionBarTextviewforUserInputHistoryPage.setTypeface(Typeface.DEFAULT_BOLD);
        customActionBarTextviewforUserInputHistoryPage.setTextColor(Color.parseColor("#6ac2eb"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD700")));
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(customActionBarTextviewforUserInputHistoryPage);

    }


    //==============================================================================================
    // Helper method for saving myVocabularyArrayList to SharedPreferences
    //==============================================================================================
    public void saveMyVocabularyArrayListToSharedPreferences() {
        SharedPreferences.Editor editor = getSharedPreferences("myVocabularyArrayListSharedPreferences", MODE_PRIVATE).edit();
        editor.putInt("myVocabularyArrayListValues", myVocabularyArrayList.size());
        for (int i = 0; i < myVocabularyArrayList.size(); i++)
        {
            editor.putString("myVocabularyArrayListValues"+i, myVocabularyArrayList.get(i));
        }
        editor.apply();

    }


    //==============================================================================================
    // 設置自定義通知時機的Helper Method
    //==============================================================================================
    public void setCustomizedNotificationTiming() {

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


                            //設置單字的通知事件
                            ContentResolver cr = getContentResolver();
                            ContentValues values = new ContentValues();
                            values.put(CalendarContract.Events.DTSTART, c.getTimeInMillis());
                            values.put(CalendarContract.Events.DTEND, c.getTimeInMillis()+60*60*1000);
                            values.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedListviewItemValue);
                            values.put(CalendarContract.Events.DESCRIPTION, "字典譯指通");
                            values.put(CalendarContract.Events.ALL_DAY, false);
                            values.put(CalendarContract.Events.CALENDAR_ID, 3);
                            values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());


                            // get the event ID that is the last element in the Uri
                            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
                            assert uri != null;
                            long eventID = Long.parseLong(Objects.requireNonNull(uri.getLastPathSegment()));
                            //
                            // ... do something with event ID
                            //
                            //
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

    public void setPreDefinedNotificationTimings1Minute() {

        //設置單字的通知事件
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, System.currentTimeMillis()+60*1*1000);  //抓現在系統的時間的1分鐘後
        values.put(CalendarContract.Events.DTEND, c.getTimeInMillis()+60*60*1000);
        values.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedListviewItemValue);
        values.put(CalendarContract.Events.DESCRIPTION, "字典譯指通");
        values.put(CalendarContract.Events.ALL_DAY, false);
        values.put(CalendarContract.Events.CALENDAR_ID, 3);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        assert uri != null;
        long eventID = Long.parseLong(Objects.requireNonNull(uri.getLastPathSegment())); // get the event ID that is the last element in the Uri

        Toast.makeText(getApplicationContext(), getString(R.string.Will_send_the_notification_after_an_hour_halfDay_day_week_month_year),Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), getString(R.string.You_can_cancel_the_notifications_any_time),Toast.LENGTH_SHORT).show();

    }

    public void setPreDefinedNotificationTimings2Minutes() {

        //設置單字的通知事件
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, System.currentTimeMillis()+60*2*1000);  //抓現在系統的時間的1分鐘後
        values.put(CalendarContract.Events.DTEND, c.getTimeInMillis()+60*60*1000);
        values.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedListviewItemValue);
        values.put(CalendarContract.Events.DESCRIPTION, "字典譯指通");
        values.put(CalendarContract.Events.ALL_DAY, false);
        values.put(CalendarContract.Events.CALENDAR_ID, 3);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        assert uri != null;
        long eventID = Long.parseLong(Objects.requireNonNull(uri.getLastPathSegment())); // get the event ID that is the last element in the Uri

        Toast.makeText(getApplicationContext(), getString(R.string.Will_send_the_notification_after_an_hour_halfDay_day_week_month_year),Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), getString(R.string.You_can_cancel_the_notifications_any_time),Toast.LENGTH_SHORT).show();

    }

    public void setPreDefinedNotificationTimings20Minutes() {

        //設置單字的通知事件
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, System.currentTimeMillis()+60*20*1000);  //抓現在系統的時間的20分鐘後
        values.put(CalendarContract.Events.DTEND, c.getTimeInMillis()+60*60*1000);
        values.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedListviewItemValue);
        values.put(CalendarContract.Events.DESCRIPTION, "字典譯指通");
        values.put(CalendarContract.Events.ALL_DAY, false);
        values.put(CalendarContract.Events.CALENDAR_ID, 3);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        assert uri != null;
        long eventID = Long.parseLong(Objects.requireNonNull(uri.getLastPathSegment())); // get the event ID that is the last element in the Uri

        Toast.makeText(getApplicationContext(), getString(R.string.Will_send_the_notification_after_an_hour_halfDay_day_week_month_year),Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), getString(R.string.You_can_cancel_the_notifications_any_time),Toast.LENGTH_SHORT).show();

    }

    public void setPreDefinedNotificationTimings1Hour() {

        //設置單字的通知事件
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, System.currentTimeMillis()+60*60*1000);  //抓現在系統的時間的1小時後
        values.put(CalendarContract.Events.DTEND, c.getTimeInMillis()+60*60*1000);
        values.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedListviewItemValue);
        values.put(CalendarContract.Events.DESCRIPTION, "字典譯指通");
        values.put(CalendarContract.Events.ALL_DAY, false);
        values.put(CalendarContract.Events.CALENDAR_ID, 3);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        assert uri != null;
        long eventID = Long.parseLong(Objects.requireNonNull(uri.getLastPathSegment())); // get the event ID that is the last element in the Uri

        Toast.makeText(getApplicationContext(), getString(R.string.Will_send_the_notification_after_an_hour_halfDay_day_week_month_year),Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), getString(R.string.You_can_cancel_the_notifications_any_time),Toast.LENGTH_SHORT).show();

    }

    public void setPreDefinedNotificationTimings9Hours() {

        //設置單字的通知事件
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, System.currentTimeMillis()+60*60*9*1000);  //抓現在系統的時間的9小時後
        values.put(CalendarContract.Events.DTEND, c.getTimeInMillis()+60*60*1000);
        values.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedListviewItemValue);
        values.put(CalendarContract.Events.DESCRIPTION, "字典譯指通");
        values.put(CalendarContract.Events.ALL_DAY, false);
        values.put(CalendarContract.Events.CALENDAR_ID, 3);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        assert uri != null;
        long eventID = Long.parseLong(Objects.requireNonNull(uri.getLastPathSegment())); // get the event ID that is the last element in the Uri

        Toast.makeText(getApplicationContext(), getString(R.string.Will_send_the_notification_after_an_hour_halfDay_day_week_month_year),Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), getString(R.string.You_can_cancel_the_notifications_any_time),Toast.LENGTH_SHORT).show();

    }

    public void setPreDefinedNotificationTimings1Day() {

        //設置單字的通知事件
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, System.currentTimeMillis()+60*60*24*1000);  //抓現在系統的時間的1天後
        values.put(CalendarContract.Events.DTEND, c.getTimeInMillis()+60*60*1000);
        values.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedListviewItemValue);
        values.put(CalendarContract.Events.DESCRIPTION, "字典譯指通");
        values.put(CalendarContract.Events.ALL_DAY, false);
        values.put(CalendarContract.Events.CALENDAR_ID, 3);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        assert uri != null;
        long eventID = Long.parseLong(Objects.requireNonNull(uri.getLastPathSegment())); // get the event ID that is the last element in the Uri

        Toast.makeText(getApplicationContext(), getString(R.string.Will_send_the_notification_after_an_hour_halfDay_day_week_month_year),Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), getString(R.string.You_can_cancel_the_notifications_any_time),Toast.LENGTH_SHORT).show();

    }

    public void setPreDefinedNotificationTimings2Days() {

        //設置單字的通知事件
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, System.currentTimeMillis()+60*60*24*2*1000);  //抓現在系統的時間的2天後
        values.put(CalendarContract.Events.DTEND, c.getTimeInMillis()+60*60*1000);
        values.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedListviewItemValue);
        values.put(CalendarContract.Events.DESCRIPTION, "字典譯指通");
        values.put(CalendarContract.Events.ALL_DAY, false);
        values.put(CalendarContract.Events.CALENDAR_ID, 3);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        assert uri != null;
        long eventID = Long.parseLong(Objects.requireNonNull(uri.getLastPathSegment())); // get the event ID that is the last element in the Uri

        Toast.makeText(getApplicationContext(), getString(R.string.Will_send_the_notification_after_an_hour_halfDay_day_week_month_year),Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), getString(R.string.You_can_cancel_the_notifications_any_time),Toast.LENGTH_SHORT).show();

    }

    public void setPreDefinedNotificationTimings6Days() {

        //設置單字的通知事件
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, System.currentTimeMillis()+60*60*24*6*1000);  //抓現在系統的時間的6天後
        values.put(CalendarContract.Events.DTEND, c.getTimeInMillis()+60*60*1000);
        values.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedListviewItemValue);
        values.put(CalendarContract.Events.DESCRIPTION, "字典譯指通");
        values.put(CalendarContract.Events.ALL_DAY, false);
        values.put(CalendarContract.Events.CALENDAR_ID, 3);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        assert uri != null;
        long eventID = Long.parseLong(Objects.requireNonNull(uri.getLastPathSegment())); // get the event ID that is the last element in the Uri

        Toast.makeText(getApplicationContext(), getString(R.string.Will_send_the_notification_after_an_hour_halfDay_day_week_month_year),Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), getString(R.string.You_can_cancel_the_notifications_any_time),Toast.LENGTH_SHORT).show();

    }

    public void setPreDefinedNotificationTimings1Month() {

        //避免發生"Numeric overflow in expression”問題，把integer改成long
        long oneSecond = 1000; long OneMinute = 60; long OneHour = 60; long oneDay = 24; long oneMonth = 30;

        //設置單字的通知事件
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, System.currentTimeMillis()+OneMinute*OneHour*oneDay*oneMonth*oneSecond);  //抓現在系統的時間的1個月後
        values.put(CalendarContract.Events.DTEND, c.getTimeInMillis()+60*60*1000);
        values.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedListviewItemValue);
        values.put(CalendarContract.Events.DESCRIPTION, "字典譯指通");
        values.put(CalendarContract.Events.ALL_DAY, false);
        values.put(CalendarContract.Events.CALENDAR_ID, 3);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        assert uri != null;
        long eventID = Long.parseLong(Objects.requireNonNull(uri.getLastPathSegment())); // get the event ID that is the last element in the Uri

        Toast.makeText(getApplicationContext(), getString(R.string.Will_send_the_notification_after_an_hour_halfDay_day_week_month_year),Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), getString(R.string.You_can_cancel_the_notifications_any_time),Toast.LENGTH_SHORT).show();

    }

    public void setPreDefinedNotificationTimingsHalfYear() {

        //避免發生"Numeric overflow in expression”問題，把integer改成long
        long oneSecond = 1000; long OneMinute = 60; long OneHour = 60; long oneDay = 24; long halfYear = 182;

        //設置單字的通知事件
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, System.currentTimeMillis()+OneMinute*OneHour*oneDay*halfYear*oneSecond);  //抓現在系統的時間的半年後
        values.put(CalendarContract.Events.DTEND, c.getTimeInMillis()+60*60*1000);
        values.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedListviewItemValue);
        values.put(CalendarContract.Events.DESCRIPTION, "字典譯指通");
        values.put(CalendarContract.Events.ALL_DAY, false);
        values.put(CalendarContract.Events.CALENDAR_ID, 3);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        assert uri != null;
        long eventID = Long.parseLong(Objects.requireNonNull(uri.getLastPathSegment())); // get the event ID that is the last element in the Uri

        Toast.makeText(getApplicationContext(), getString(R.string.Will_send_the_notification_after_an_hour_halfDay_day_week_month_year),Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), getString(R.string.You_can_cancel_the_notifications_any_time),Toast.LENGTH_SHORT).show();

    }

    public void setPreDefinedNotificationTimingsOneYear() {

        //避免發生"Numeric overflow in expression”問題，把integer改成long
        long oneSecond = 1000; long OneMinute = 60; long OneHour = 60; long oneDay = 24; long oneYear = 365;

        //設置單字的通知事件
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, System.currentTimeMillis()+OneMinute*OneHour*oneDay*oneYear*oneSecond);  //抓現在系統的時間的一年後
        values.put(CalendarContract.Events.DTEND, c.getTimeInMillis()+60*60*1000);
        values.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedListviewItemValue);
        values.put(CalendarContract.Events.DESCRIPTION, "字典譯指通");
        values.put(CalendarContract.Events.ALL_DAY, false);
        values.put(CalendarContract.Events.CALENDAR_ID, 3);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        assert uri != null;
        long eventID = Long.parseLong(Objects.requireNonNull(uri.getLastPathSegment())); // get the event ID that is the last element in the Uri

        Toast.makeText(getApplicationContext(), getString(R.string.Will_send_the_notification_after_an_hour_halfDay_day_week_month_year),Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), getString(R.string.You_can_cancel_the_notifications_any_time),Toast.LENGTH_SHORT).show();

    }




}

