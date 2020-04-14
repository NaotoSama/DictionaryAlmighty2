package com.example.android.dictionaryalmighty2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.processphoenix.ProcessPhoenix;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;

import static com.example.android.dictionaryalmighty2.MainActivity.mChildReferenceForInputHistory;
import static com.example.android.dictionaryalmighty2.MainActivity.mChildReferenceForVocabularyList;
import static com.example.android.dictionaryalmighty2.MainActivity.userInputArraylist;
import static com.example.android.dictionaryalmighty2.MainActivity.username;

public class UserInputHistory extends AppCompatActivity {

    RelativeLayout.LayoutParams layoutparams;   //用來客製化修改ActionBar
    TextView customActionBarTextviewforUserInputHistoryPage;
    androidx.appcompat.app.ActionBar actionBar;

    TextView searchRecordTextView;
    ImageView clickHereFingerUserInputHistoryImageView;

    static String selectedListviewItemValue;
    static String[] presetNotificationTimingsList;

    ListView userInputListview;
    static ArrayAdapter userInputArrayAdapter;

    Button clearUserInputList;
    Button goToWordsToMemorizePageButton;

    EditText userInputHistorySearchBox;

    Calendar c;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_input_history);


        //findViewById
        goToWordsToMemorizePageButton = findViewById(R.id.go_to_words_to_memorize_page);
        userInputListview = findViewById(R.id.user_input_listview);
        clearUserInputList = findViewById(R.id.clear_user_input_list);
        userInputHistorySearchBox = findViewById(R.id.user_input_history_search_box);
        searchRecordTextView = findViewById(R.id.search_record_textView);
        clickHereFingerUserInputHistoryImageView = findViewById(R.id.click_here_finger_user_input_history);


        //Initialize the adapter
        userInputArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userInputArraylist);
        userInputListview.setAdapter(userInputArrayAdapter);

        if (username!=null && !username.equals("")) {  //檢查有用戶有登入，才能跑以下程式碼
            mChildReferenceForInputHistory.child(username).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        //Get the data from snapshot
                        String userSearchHistory = postSnapshot.getValue(String.class);

                        //Add the data to the arraylist
                        userInputArraylist.add(userSearchHistory);

                        //透過HashSet自動過濾掉userInputArraylist中重複的字
                        HashSet<String> userInputHistoryArraylistHashSet = new HashSet<>();
                        userInputHistoryArraylistHashSet.addAll(userInputArraylist);
                        userInputArraylist.clear();
                        userInputArraylist.addAll(userInputHistoryArraylistHashSet);

                        //Alphabetic sorting
                        Collections.sort(userInputArraylist);

                        userInputArrayAdapter.notifyDataSetChanged();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }


        /**
         * 設置ActionBar
         */
        actionBar = getSupportActionBar();
        customActionBarTextviewforUserInputHistoryPage = new TextView(this);
        layoutparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        customActionBarForUserInputHistoryPage();   //Helper Method


        /**
         * 設置使用說明
         */
        searchRecordTextView.setText(R.string.Instructions);

        searchRecordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userInputHistoryInstructionsAlertDialog();
            }
        });

        clickHereFingerUserInputHistoryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userInputHistoryInstructionsAlertDialog();
            }
        });


        /**
         * 帶用戶前往單字庫頁面的按鈕
         */
        goToWordsToMemorizePageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (username!=null && !username.equals("")) {
                    Intent intent= new Intent(UserInputHistory.this, WordsToMemorize.class);
                    startActivity(intent);

                } else {
                    //這邊設置AlertDialog讓用戶選擇是否登入或註冊會員，是的話帶往註冊登入頁面
                    CFAlertDialog.Builder logInToUseVocabularyBookAlertDialogBuilder = new CFAlertDialog.Builder(UserInputHistory.this)
                            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                            .setDialogBackgroundColor(Color.parseColor("#fafcd7"))
                            .setCornerRadius(50)
                            .setTitle(getResources().getString(R.string.Please_log_in_to_access_your_vocabulary_list) + getResources().getString(R.string.Your_vocabulary_list_will_be_stored_online))
                            .setTextColor(Color.BLUE)
                            .setCancelable(false) //按到旁邊的空白處AlertDialog也不會消失
                            .setHeaderView(R.layout.custom_alert_diaglog_question_mark)

                            //AlertDialog的確定鈕，帶往註冊登入頁面
                            .addButton(getResources().getString(R.string.Take_me_to_register_sign_in_page)
                                    , Color.WHITE, Color.GREEN, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {

                                        Intent launchSignInActivity = new Intent(UserInputHistory.this, SignInActivity.class);
                                        startActivity(launchSignInActivity);
                                        dialog.dismiss();
                                    })

                            //AlertDialog的取消鈕
                            .addButton(getResources().getString(R.string.Cancel)
                                    , Color.CYAN, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {

                                        dialog.dismiss();
                                    });

                    //把AlertDialog顯示出來
                    logInToUseVocabularyBookAlertDialogBuilder.create().show();
                }

            }
        });



        /**
         * 讓用戶搜尋列表
         */
        userInputHistorySearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                (UserInputHistory.this).userInputArrayAdapter.getFilter().filter(charSequence);

            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        /**
         * 讓用戶清空列表
         */
        clearUserInputList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //這邊設置AlertDialog讓用戶確認是否真要清除列表
                CFAlertDialog.Builder doYouReallyWantToClearListAlertDialogBuilder = new CFAlertDialog.Builder(UserInputHistory.this)
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                        .setDialogBackgroundColor(Color.parseColor("#fafcd7"))
                        .setCornerRadius(50)
                        .setTitle(getString(R.string.Do_you_really_want_to_clear_the_list))
                        .setTextColor(Color.BLUE)
                        .setMessage(getString(R.string.Clear_user_search_history_confirmation_message))
                        .setCancelable(false)  //按到旁邊的空白處AlertDialog也不會消失

                        //AlertDialog的確定鈕，清除列表
                        .addButton(getString(R.string.Confirm)
                                , Color.WHITE, Color.GREEN, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (doYouReallyWantToClearListAlertDialog, which) -> {

                                    mChildReferenceForInputHistory.child(username).removeValue(); //清除雲端用戶名稱的node

                                    userInputArraylist.clear(); //同時清除本地的list
                                    userInputArrayAdapter.notifyDataSetChanged();

                                    //將搜尋紀錄的列表存到SharedPreferences
                                    SharedPreferences.Editor editor = getSharedPreferences("userInputArrayListSharedPreferences", MODE_PRIVATE).edit();
                                    editor.putInt("userInputArrayListValues", userInputArraylist.size());
                                    for (int i = 0; i < userInputArraylist.size(); i++) {
                                        editor.putString("userInputArrayListItem_" + i, userInputArraylist.get(i));
                                    }
                                    editor.apply();

                                    Toast.makeText(getApplicationContext(), R.string.List_cleared, Toast.LENGTH_SHORT).show();

                                    doYouReallyWantToClearListAlertDialog.dismiss();
                        })


                        //AlertDialog的取消鈕
                        .addButton(getString(R.string.Cancel)
                                , Color.WHITE, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (doYouReallyWantToClearListAlertDialog, which) -> {

                                doYouReallyWantToClearListAlertDialog.dismiss();
                        });


                doYouReallyWantToClearListAlertDialogBuilder.setHeaderView(R.layout.custom_alert_diaglog_question_mark);
                //把AlertDialog顯示出來
                doYouReallyWantToClearListAlertDialogBuilder.show();
            }

        });



        /**
         * Let the user click on an item to pass the item value to wordInputView, or fire a calendar event to memorize words.
         */
        userInputListview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                selectedListviewItemValue=userInputListview.getItemAtPosition(position).toString();

                //這邊設置第一層AlertDialog讓用戶選擇把單字傳送到首頁的wordInputView, 或發佈記憶單字的通知
                CFAlertDialog.Builder passToWordInputViewOrFireCalendarEventAlertDialogBuilder = new CFAlertDialog.Builder(UserInputHistory.this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setDialogBackgroundColor(Color.parseColor("#fafcd7"))
                .setCornerRadius(50)
                .setTitle(getString(R.string.Do_you_want_to))
                .setTextColor(Color.BLUE)
                .setMessage(getString(R.string.Search_this_word_explanation) + System.getProperty("line.separator") + getString(R.string.Memorize_this_word_explanation))
                .setCancelable(false) //按到旁邊的空白處AlertDialog也不會消失


                //第一層AlertDialog的確定鈕，把單字傳送到首頁的wordInputView
                .addButton(getString(R.string.Send_to_WordInputView)
                        , Color.WHITE, Color.GREEN, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (passToWordInputViewOrFireCalendarEventAlertDialog, whichLayer1) ->{

                        MainActivity.wordInputView.setText(selectedListviewItemValue);
                        finish(); //結束此Activity並返回上一個Activity

                        passToWordInputViewOrFireCalendarEventAlertDialog.dismiss();
                })

                //第一層AlertDialog的中立鈕，發佈記憶單字的通知。設置第二層AlertDialog讓用戶選擇自定義或預設的通知時機。
                        .addButton(getString(R.string.Memorize_this_word)
                                , Color.BLACK, Color.YELLOW, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (passToWordInputViewOrFireCalendarEventAlertDialog, whichLayer1) -> {

                        //這邊設置第二層AlertDialog讓用戶選擇自定義或預設的通知時機
                        final CFAlertDialog.Builder chooseCustomizedOrPredefinedNotificationAlertDialogBuilder = new CFAlertDialog.Builder(UserInputHistory.this)
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                        .setDialogBackgroundColor(Color.parseColor("#fafcd7"))
                        .setCornerRadius(50)
                        .setTitle(getString(R.string.Choose_customized_or_predefined_notification_timings))
                        .setTextColor(Color.BLUE)
                        .setMessage(getString(R.string.Predefined_timing_explanation) + System.getProperty("line.separator") + getString(R.string.User_configured_timing_explanation))
                        .setCancelable(false) //按到旁邊的空白處AlertDialog也不會消失


                        //第二層AlertDialog的確定鈕，預設的通知時機。
                        .addButton(getString(R.string.Use_predefined_timing)
                                            , Color.WHITE, Color.GREEN, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (chooseCustomizedOrPredefinedNotificationAlertDialog, whichLayer2) -> {


                                presetNotificationTimingsList = getResources().getStringArray(R.array.preset_notification_timings);

                                //這邊設置第三層AlertDialog讓用戶選擇各種預設通知的時機點
                                CFAlertDialog.Builder choosePresetNotificationTimingsAlertDialogBuilder = new CFAlertDialog.Builder(UserInputHistory.this)
                                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                .setDialogBackgroundColor(Color.parseColor("#fafcd7"))
                                .setCornerRadius(50)
                                .setTitle(getString(R.string.Choose_one_preset_timing))
                                .setTextColor(Color.BLUE)
                                .setMessage(getString(R.string.Use_with_caution))
                                .setCancelable(false) //按到旁邊的空白處AlertDialog也不會消失

                                .setSingleChoiceItems(presetNotificationTimingsList, -1, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface choosePresetNotificationTimingsAlertDialog, int position) {
                                        switch (position) {
                                            case 0:
                                                setPreDefinedNotificationTimings1Hour();
                                                setPreDefinedNotificationTimings9Hours();
                                                setPreDefinedNotificationTimings1Day();
                                                setPreDefinedNotificationTimings2Days();
                                                setPreDefinedNotificationTimings7Days();
                                                setPreDefinedNotificationTimings1Month();
                                                setPreDefinedNotificationTimingsHalfYear();
                                                setPreDefinedNotificationTimingsOneYear();
                                                //點擊子項目後讓第三層的AlertDialog消失
                                                choosePresetNotificationTimingsAlertDialog.dismiss();
                                                Toast.makeText(getApplicationContext(),R.string.Will_send_the_notifications_on_8_preset_timings,Toast.LENGTH_LONG).show();
                                                break;
                                            case 1:
                                                setPreDefinedNotificationTimings1Day();
                                                setPreDefinedNotificationTimings7Days();
                                                setPreDefinedNotificationTimings1Month();
                                                choosePresetNotificationTimingsAlertDialog.dismiss();
                                                Toast.makeText(getApplicationContext(),R.string.Will_send_the_notifications_on_3_preset_timings,Toast.LENGTH_LONG).show();
                                                break;
                                            case 2:
                                                setPreDefinedNotificationTimings1Hour();
                                                choosePresetNotificationTimingsAlertDialog.dismiss();
                                                break;
                                            case 3:
                                                setPreDefinedNotificationTimings9Hours();
                                                choosePresetNotificationTimingsAlertDialog.dismiss();
                                                break;
                                            case 4:
                                                setPreDefinedNotificationTimings1Day();
                                                choosePresetNotificationTimingsAlertDialog.dismiss();
                                                break;
                                            case 5:
                                                setPreDefinedNotificationTimings2Days();
                                                choosePresetNotificationTimingsAlertDialog.dismiss();
                                                break;
                                            case 6:
                                                setPreDefinedNotificationTimings7Days();
                                                choosePresetNotificationTimingsAlertDialog.dismiss();
                                                break;
                                            case 7:
                                                setPreDefinedNotificationTimings1Month();
                                                choosePresetNotificationTimingsAlertDialog.dismiss();
                                                break;
                                            case 8:
                                                setPreDefinedNotificationTimingsHalfYear();
                                                choosePresetNotificationTimingsAlertDialog.dismiss();
                                                break;
                                            case 9:
                                                setPreDefinedNotificationTimingsOneYear();
                                                choosePresetNotificationTimingsAlertDialog.dismiss();
                                                break;
                                        }

                                    }
                                })

                                //第三層AlertDialog的取消鈕
                                        .addButton(getString(R.string.Cancel)
                                                , Color.WHITE, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (choosePresetNotificationTimingsAlertDialog, whichLayer3) -> {

                                        choosePresetNotificationTimingsAlertDialog.dismiss();
                                });

                                //把第三層的AlertDialog顯示出來
                                choosePresetNotificationTimingsAlertDialogBuilder.show();
                                //同時讓第二層的AlertDialog消失
                                chooseCustomizedOrPredefinedNotificationAlertDialog.dismiss();
                        })

                        //第二層AlertDialog的中立鈕，自定義通知時機
                        .addButton(getString(R.string.Customize_timing)
                                            , Color.BLACK, Color.YELLOW, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (chooseCustomizedOrPredefinedNotificationAlertDialog, whichLayer2) -> {

                                setCustomizedNotificationTiming();

                                chooseCustomizedOrPredefinedNotificationAlertDialog.dismiss();
                        })

                        //第二層AlertDialog的取消鈕
                                .addButton(getString(R.string.Cancel)
                                        , Color.WHITE, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (chooseCustomizedOrPredefinedNotificationAlertDialog, whichLayer2) -> {

                                chooseCustomizedOrPredefinedNotificationAlertDialog.dismiss();
                        });


                        chooseCustomizedOrPredefinedNotificationAlertDialogBuilder.setHeaderView(R.layout.custom_alert_dialog_clock);
                        //把第二層的AlertDialog顯示出來
                        chooseCustomizedOrPredefinedNotificationAlertDialogBuilder.show();
                        //同時讓第一層的AlertDialog消失
                        passToWordInputViewOrFireCalendarEventAlertDialog.dismiss();

                })

                //第一層AlertDialog的取消鈕
                .addButton(getString(R.string.Cancel)
                        , Color.WHITE, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (passToWordInputViewOrFireCalendarEventAlertDialog, whichLayer1) -> {

                    passToWordInputViewOrFireCalendarEventAlertDialog.dismiss();
                });

                passToWordInputViewOrFireCalendarEventAlertDialogBuilder.setHeaderView(R.layout.custom_alert_diaglog_question_mark);
                //把第一層的AlertDialog顯示出來
                passToWordInputViewOrFireCalendarEventAlertDialogBuilder.show();

            }
        });



        /**
         * Let the user long click on an item to save the item to WordsToMemorize Page.
         */
        userInputListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedListviewItemValue=userInputListview.getItemAtPosition(position).toString();

                if (username!=null && !username.equals("")) {  //檢查有用戶名稱且雲端存儲的功能有打開，才能跑以下程式碼

                    //先送出文字
                    mChildReferenceForVocabularyList.child(username).push().setValue(selectedListviewItemValue); //加入單字到資料庫

                    //檢查資料庫中是否有重複的字
                    Query query = mChildReferenceForVocabularyList.child(username).orderByValue().equalTo(selectedListviewItemValue);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                snapshot.getRef().setValue(null); //若有，先移除該重複的字
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            throw databaseError.toException();
                        }
                    });


                    //延遲1秒再送出文字
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {

                            mChildReferenceForVocabularyList.child(username).push().setValue(selectedListviewItemValue); //加入單字到資料庫

                        }
                    };
                    Handler h =new Handler();
                    h.postDelayed(r, 1000);

                                                                                                    //if (!userInputArraylist.contains(searchKeyword)){
                                                                                                    //    mChildReferenceForVocabularyList.child(username).push().setValue(searchKeyword);
                                                                                                    //}
                }


                                                                                                    //myVocabularyArrayList.add(selectedListviewItemValue); //同時加入單字到本地的list
                                                                                                    //
                                                                                                    ////透過HashSet自動過濾掉myVocabularyArraylist中重複的字
                                                                                                    //HashSet<String> myVocabularyArraylistHashSet = new HashSet<>();
                                                                                                    //myVocabularyArraylistHashSet.addAll(myVocabularyArrayList);
                                                                                                    //myVocabularyArrayList.clear();
                                                                                                    //myVocabularyArrayList.addAll(myVocabularyArraylistHashSet);
                                                                                                    //
                                                                                                    ////Alphabetic sorting
                                                                                                    //Collections.sort(myVocabularyArrayList);
                                                                                                    //
                                                                                                    //saveMyVocabularyArrayListToSharedPreferences();

                Toast.makeText(getApplicationContext(),selectedListviewItemValue + getResources().getString(R.string.Word_saved_to_my_vocabulary_list),Toast.LENGTH_LONG).show();

                                                                                                    //Intent intent = new Intent(UserInputHistory.this, WordsToMemorize.class);
                                                                                                    //intent.putExtra("selectedListviewItemValue",myVocabularyArrayList);
                                                                                                    //startActivity(intent);

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

                                    String wordToDelete = listView.getItemAtPosition(position).toString();

                                    if (username!=null && !username.equals("")) {
                                        Query query = mChildReferenceForInputHistory.child(username).orderByValue().equalTo(wordToDelete); //在資料庫中尋找要刪除的字

                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                                    snapshot.getRef().removeValue();  //刪除該字
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                            // throw databaseError.toException();  這句拿掉以免閃退
                                            }
                                        });
                                    }


                                    userInputArraylist.remove(wordToDelete);  //同時本從地的list移除該字
                                    userInputArrayAdapter.notifyDataSetChanged();

                                    //將搜尋紀錄的列表存到SharedPreferences
                                    SharedPreferences.Editor editor = getSharedPreferences("userInputArrayListSharedPreferences", MODE_PRIVATE).edit();
                                    editor.putInt("userInputArrayListValues", userInputArraylist.size());
                                    for (int i = 0; i < userInputArraylist.size(); i++)
                                    {
                                        editor.putString("userInputArrayListItem_"+i, userInputArraylist.get(i));
                                    }
                                    editor.apply();

                                    Toast.makeText(getApplicationContext(), wordToDelete + getResources().getString(R.string.Has_benn_deleted), Toast.LENGTH_SHORT).show();
                                    reloadCurrentActivity();

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
    // 在OnCreate外面設置 action bar按鈕
    //==============================================================================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);  //先沿用舊有的佈局檔
        return super.onCreateOptionsMenu(menu);
    }

    // Override this method to do whatever change you want with the menu when it is recreated
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.log_in_button).setTitle(getResources().getString(R.string.About)+ System.getProperty("line.separator") + getResources().getString(R.string.Memorize_this_word));
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_in_button: //先沿用舊有的按鈕
                Intent goToAboutMemorizingWordsPageIntent = new Intent(UserInputHistory.this, AboutMemorizingWords.class);
                startActivity(goToAboutMemorizingWordsPageIntent);
        }
        return super.onOptionsItemSelected(item);
    }



//==============================================================================================
// 所有helper methods
//==============================================================================================


    //==============================================================================================
    // 重新載入當前頁面的Helper Method
    //==============================================================================================
    //Seamlessly reload the current activity without screen blinking
    public void reloadCurrentActivity() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }


    //==========================================================================================
    // 重啟App的helper methods
    //==========================================================================================
    public void relaunchApp() {
        Intent relaunchAppIntent = new Intent(getApplicationContext(), MainActivity.class);
        ProcessPhoenix.triggerRebirth(getApplicationContext(), relaunchAppIntent);
        Runtime.getRuntime().exit(0);
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
        customActionBarTextviewforUserInputHistoryPage.setTextColor(Color.parseColor("#ffffff"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff6600")));
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(customActionBarTextviewforUserInputHistoryPage);

    }


                                                                                                    ////==============================================================================================
                                                                                                    //// Helper method for saving myVocabularyArrayList to SharedPreferences
                                                                                                    ////==============================================================================================
                                                                                                    //public void saveMyVocabularyArrayListToSharedPreferences() {
                                                                                                    //    SharedPreferences.Editor editor = getSharedPreferences("myVocabularyArrayListSharedPreferences", MODE_PRIVATE).edit();
                                                                                                    //    editor.putInt("myVocabularyArrayListValues", myVocabularyArrayList.size());
                                                                                                    //    for (int i = 0; i < myVocabularyArrayList.size(); i++)
                                                                                                    //    {
                                                                                                    //        editor.putString("myVocabularyArrayListValues"+i, myVocabularyArrayList.get(i));
                                                                                                    //    }
                                                                                                    //    editor.apply();
                                                                                                    //
                                                                                                    //}


                                                                                                    //==============================================================================================
                                                                                                    // Helper method for registering login username
                                                                                                    //==============================================================================================
                                                                                                    //    public void registerLoginUsername() {
                                                                                                    //        //這邊設置AlertDialog讓用戶輸入用戶名稱
                                                                                                    //        final AlertDialog.Builder registerLoginRenameDeleteUsernameAlertDialog = new AlertDialog.Builder(UserInputHistory.this);
                                                                                                    //        registerLoginRenameDeleteUsernameAlertDialog.setTitle(getString(R.string.Input_a_username));
                                                                                                    //        registerLoginRenameDeleteUsernameAlertDialog.setCancelable(true); //按到旁邊的空白處AlertDialog會消失
                                                                                                    //        final EditText usernameInputView = new EditText(getApplicationContext());
                                                                                                    //        registerLoginRenameDeleteUsernameAlertDialog.setView(usernameInputView);
                                                                                                    //
                                                                                                    //        //AlertDialog的確定鈕，登入用戶名稱
                                                                                                    //        registerLoginRenameDeleteUsernameAlertDialog.setPositiveButton(R.string.Register_or_log_in_username, new DialogInterface.OnClickListener() {
                                                                                                    //
                                                                                                    //            @Override
                                                                                                    //            public void onClick(DialogInterface dialog, int which) {
                                                                                                    //
                                                                                                    //                if (username!=null && !username.equals("")) { //若用戶名稱不是空的則提示已經登入了
                                                                                                    //
                                                                                                    //                    Toast.makeText(getApplicationContext(), R.string.You_are_already_logged_in, Toast.LENGTH_LONG).show();
                                                                                                    //
                                                                                                    //                }
                                                                                                    //
                                                                                                    //                else
                                                                                                    //                {
                                                                                                    //                    String userInputUsername = usernameInputView.getText().toString();
                                                                                                    //
                                                                                                    //                    if (userInputUsername!=null && !userInputUsername.equals("")) { //檢查用戶確實有輸入名稱時儲存該名稱
                                                                                                    //
                                                                                                    //                        usernameSharedPreferences = getSharedPreferences("usernameSharedPreferences", MODE_PRIVATE);
                                                                                                    //                        usernameSharedPreferences.edit().putString("userName", userInputUsername).apply();
                                                                                                    //
                                                                                                    //                        //同時把用戶使用雲端存儲單字紀錄的設定(localOrCloudSaveSwitchPreferences=1)存入SharedPreferences
                                                                                                    //                        localOrCloudSaveSwitchPreferences = getSharedPreferences("localOrCloudSaveSwitchPreferences", MODE_PRIVATE);
                                                                                                    //                        localOrCloudSaveSwitchPreferences.edit().putString("CloudSaveMode", "1").apply();
                                                                                                    //
                                                                                                    //                        //延遲2.5秒重啟App
                                                                                                    //                        Runnable r = new Runnable() {
                                                                                                    //                            @Override
                                                                                                    //                            public void run() {
                                                                                                    //                                relaunchApp();
                                                                                                    //                            }
                                                                                                    //                        };
                                                                                                    //                        Handler h =new Handler();
                                                                                                    //                        h.postDelayed(r, 2500);
                                                                                                    //
                                                                                                    //                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.Logged_in_as) + userInputUsername + " " + getResources().getString(R.string.You_are_using_cloud_storage), Toast.LENGTH_LONG).show();
                                                                                                    //                    }
                                                                                                    //
                                                                                                    //                    else {
                                                                                                    //                        Toast.makeText(getApplicationContext(), R.string.You_have_not_entered_anything, Toast.LENGTH_LONG).show();
                                                                                                    //                    }
                                                                                                    //                }
                                                                                                    //            }
                                                                                                    //        });
                                                                                                    //
                                                                                                    //        //把AlertDialog顯示出來
                                                                                                    //        registerLoginRenameDeleteUsernameAlertDialog.create().show();
                                                                                                    //    }


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
                            ContentValues event = new ContentValues();
                            event.put(CalendarContract.Events.DTSTART, c.getTimeInMillis());
                            event.put(CalendarContract.Events.DTEND, c.getTimeInMillis()+60*60*1000);
                            event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedListviewItemValue);
                            event.put(CalendarContract.Events.DESCRIPTION, getResources().getString(R.string.app_name));
                            event.put(CalendarContract.Events.ALL_DAY, false);
                            event.put(CalendarContract.Events.CALENDAR_ID, 3);
                            event.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());


                            // get the event ID that is the last element in the Uri
                            Uri newEvent = cr.insert(CalendarContract.Events.CONTENT_URI, event);
                            assert newEvent != null;
                            Long eventID = Long.parseLong(Objects.requireNonNull(newEvent.getLastPathSegment())); // get the event ID that is the last element in the Uri
                            //
                            // ... do something with event ID
                            //
                            //

                            ContentValues reminder = new ContentValues();
                            reminder.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
                            reminder.put(CalendarContract.Reminders.MINUTES, 0);
                            reminder.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                            Uri newReminder = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminder);
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
    public void setPreDefinedNotificationTimings1Hour() {

        //設置單字的通知事件
        ContentResolver cr = getContentResolver();
        ContentValues event = new ContentValues();
        event.put(CalendarContract.Events.DTSTART, System.currentTimeMillis()+ 60*60*1000);  //抓現在系統的時間的1小時後開始
        event.put(CalendarContract.Events.DTEND, System.currentTimeMillis()+60*60*1000+60*60*1000);
        event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedListviewItemValue);
        event.put(CalendarContract.Events.DESCRIPTION, getResources().getString(R.string.app_name));
        event.put(CalendarContract.Events.ALL_DAY, false);
        event.put(CalendarContract.Events.CALENDAR_ID, 3);
        event.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        Uri newEvent = cr.insert(CalendarContract.Events.CONTENT_URI, event);
        assert newEvent != null;
        long eventID = Long.parseLong(Objects.requireNonNull(newEvent.getLastPathSegment())); // get the event ID that is the last element in the Uri

        ContentValues reminder = new ContentValues();
        reminder.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        reminder.put(CalendarContract.Reminders.MINUTES, 0);
        reminder.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri newReminder = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminder);

        Toast.makeText(getApplicationContext(), getString(R.string.Will_send_the_notification_at) + getString(R.string.In_1_hour) + getString(R.string.blank_space),Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), getString(R.string.You_can_cancel_the_notifications_any_time),Toast.LENGTH_SHORT).show();

    }

    public void setPreDefinedNotificationTimings9Hours() {

        //設置單字的通知事件
        ContentResolver cr = getContentResolver();
        ContentValues event = new ContentValues();
        event.put(CalendarContract.Events.DTSTART, System.currentTimeMillis()+60*60*9*1000);  //抓現在系統的時間的9小時後開始
        event.put(CalendarContract.Events.DTEND, System.currentTimeMillis()+60*60*9*1000+60*60*1000);
        event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedListviewItemValue);
        event.put(CalendarContract.Events.DESCRIPTION, getResources().getString(R.string.app_name));
        event.put(CalendarContract.Events.ALL_DAY, false);
        event.put(CalendarContract.Events.CALENDAR_ID, 3);
        event.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        Uri newEvent = cr.insert(CalendarContract.Events.CONTENT_URI, event);
        assert newEvent != null;
        long eventID = Long.parseLong(Objects.requireNonNull(newEvent.getLastPathSegment())); // get the event ID that is the last element in the Uri

        ContentValues reminder = new ContentValues();
        reminder.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        reminder.put(CalendarContract.Reminders.MINUTES, 0);
        reminder.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri newReminder = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminder);

        Toast.makeText(getApplicationContext(), getString(R.string.Will_send_the_notification_at) + getString(R.string.In_half_a_day) + getString(R.string.blank_space),Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), getString(R.string.You_can_cancel_the_notifications_any_time),Toast.LENGTH_SHORT).show();

    }

    public void setPreDefinedNotificationTimings1Day() {

        //設置單字的通知事件
        ContentResolver cr = getContentResolver();
        ContentValues event = new ContentValues();
        event.put(CalendarContract.Events.DTSTART, System.currentTimeMillis()+60*60*24*1000);  //抓現在系統的時間的1天後開始
        event.put(CalendarContract.Events.DTEND, System.currentTimeMillis()+60*60*24*1000+60*60*1000);
        event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedListviewItemValue);
        event.put(CalendarContract.Events.DESCRIPTION, getResources().getString(R.string.app_name));
        event.put(CalendarContract.Events.ALL_DAY, false);
        event.put(CalendarContract.Events.CALENDAR_ID, 3);
        event.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        Uri newEvent = cr.insert(CalendarContract.Events.CONTENT_URI, event);
        assert newEvent != null;
        long eventID = Long.parseLong(Objects.requireNonNull(newEvent.getLastPathSegment())); // get the event ID that is the last element in the Uri

        ContentValues reminder = new ContentValues();
        reminder.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        reminder.put(CalendarContract.Reminders.MINUTES, 0);
        reminder.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri newReminder = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminder);

        Toast.makeText(getApplicationContext(), getString(R.string.Will_send_the_notification_at) + getString(R.string.In_1_day) + getString(R.string.blank_space),Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), getString(R.string.You_can_cancel_the_notifications_any_time),Toast.LENGTH_SHORT).show();

    }

    public void setPreDefinedNotificationTimings2Days() {

        //設置單字的通知事件
        ContentResolver cr = getContentResolver();
        ContentValues event = new ContentValues();
        event.put(CalendarContract.Events.DTSTART, System.currentTimeMillis()+60*60*24*2*1000);  //抓現在系統的時間的2天後開始
        event.put(CalendarContract.Events.DTEND, System.currentTimeMillis()+60*60*24*2*1000+60*60*1000);
        event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedListviewItemValue);
        event.put(CalendarContract.Events.DESCRIPTION, getResources().getString(R.string.app_name));
        event.put(CalendarContract.Events.ALL_DAY, false);
        event.put(CalendarContract.Events.CALENDAR_ID, 3);
        event.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        Uri newEvent = cr.insert(CalendarContract.Events.CONTENT_URI, event);
        assert newEvent != null;
        long eventID = Long.parseLong(Objects.requireNonNull(newEvent.getLastPathSegment())); // get the event ID that is the last element in the Uri

        ContentValues reminder = new ContentValues();
        reminder.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        reminder.put(CalendarContract.Reminders.MINUTES, 0);
        reminder.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri newReminder = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminder);

        Toast.makeText(getApplicationContext(), getString(R.string.Will_send_the_notification_at) + getString(R.string.In_2_days) + getString(R.string.blank_space),Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), getString(R.string.You_can_cancel_the_notifications_any_time),Toast.LENGTH_SHORT).show();

    }

    public void setPreDefinedNotificationTimings7Days() {

        //設置單字的通知事件
        ContentResolver cr = getContentResolver();
        ContentValues event = new ContentValues();
        event.put(CalendarContract.Events.DTSTART, System.currentTimeMillis()+60*60*24*7*1000);  //抓現在系統的時間的6天後開始
        event.put(CalendarContract.Events.DTEND, System.currentTimeMillis()+60*60*24*7*1000+60*60*1000);
        event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedListviewItemValue);
        event.put(CalendarContract.Events.DESCRIPTION, getResources().getString(R.string.app_name));
        event.put(CalendarContract.Events.ALL_DAY, false);
        event.put(CalendarContract.Events.CALENDAR_ID, 3);
        event.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        Uri newEvent = cr.insert(CalendarContract.Events.CONTENT_URI, event);
        assert newEvent != null;
        long eventID = Long.parseLong(Objects.requireNonNull(newEvent.getLastPathSegment())); // get the event ID that is the last element in the Uri

        ContentValues reminder = new ContentValues();
        reminder.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        reminder.put(CalendarContract.Reminders.MINUTES, 0);
        reminder.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri newReminder = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminder);

        Toast.makeText(getApplicationContext(), getString(R.string.Will_send_the_notification_at) + getString(R.string.In_1_week) + getString(R.string.blank_space),Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), getString(R.string.You_can_cancel_the_notifications_any_time),Toast.LENGTH_SHORT).show();

    }

    public void setPreDefinedNotificationTimings1Month() {

        //避免發生"Numeric overflow in expression”問題，把integer改成long
        long oneSecond = 1000; long OneMinute = 60; long OneHour = 60; long oneDay = 24; long oneMonth = 30;

        //設置單字的通知事件
        ContentResolver cr = getContentResolver();
        ContentValues event = new ContentValues();
        event.put(CalendarContract.Events.DTSTART, System.currentTimeMillis()+OneMinute*OneHour*oneDay*oneMonth*oneSecond);  //抓現在系統的時間的1個月後開始
        event.put(CalendarContract.Events.DTEND, System.currentTimeMillis()+OneMinute*OneHour*oneDay*oneMonth*oneSecond+60*60*1000);
        event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedListviewItemValue);
        event.put(CalendarContract.Events.DESCRIPTION, getResources().getString(R.string.app_name));
        event.put(CalendarContract.Events.ALL_DAY, false);
        event.put(CalendarContract.Events.CALENDAR_ID, 3);
        event.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        Uri newEvent = cr.insert(CalendarContract.Events.CONTENT_URI, event);
        assert newEvent != null;
        long eventID = Long.parseLong(Objects.requireNonNull(newEvent.getLastPathSegment())); // get the event ID that is the last element in the Uri

        ContentValues reminder = new ContentValues();
        reminder.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        reminder.put(CalendarContract.Reminders.MINUTES, 0);
        reminder.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri newReminder = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminder);

        Toast.makeText(getApplicationContext(), getString(R.string.Will_send_the_notification_at) + getString(R.string.In_1_month) + getString(R.string.blank_space),Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), getString(R.string.You_can_cancel_the_notifications_any_time),Toast.LENGTH_SHORT).show();

    }

    public void setPreDefinedNotificationTimingsHalfYear() {

        //避免發生"Numeric overflow in expression”問題，把integer改成long
        long oneSecond = 1000; long OneMinute = 60; long OneHour = 60; long oneDay = 24; long halfYear = 182;

        //設置單字的通知事件
        ContentResolver cr = getContentResolver();
        ContentValues event = new ContentValues();
        event.put(CalendarContract.Events.DTSTART, System.currentTimeMillis()+OneMinute*OneHour*oneDay*halfYear*oneSecond);  //抓現在系統的時間的半年後開始
        event.put(CalendarContract.Events.DTEND, System.currentTimeMillis()+OneMinute*OneHour*oneDay*halfYear*oneSecond+60*60*1000);
        event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedListviewItemValue);
        event.put(CalendarContract.Events.DESCRIPTION, getResources().getString(R.string.app_name));
        event.put(CalendarContract.Events.ALL_DAY, false);
        event.put(CalendarContract.Events.CALENDAR_ID, 3);
        event.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        Uri newEvent = cr.insert(CalendarContract.Events.CONTENT_URI, event);
        assert newEvent != null;
        long eventID = Long.parseLong(Objects.requireNonNull(newEvent.getLastPathSegment())); // get the event ID that is the last element in the Uri

        ContentValues reminder = new ContentValues();
        reminder.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        reminder.put(CalendarContract.Reminders.MINUTES, 0);
        reminder.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri newReminder = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminder);

        Toast.makeText(getApplicationContext(), getString(R.string.Will_send_the_notification_at) + getString(R.string.In_6_months) + getString(R.string.blank_space),Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), getString(R.string.You_can_cancel_the_notifications_any_time),Toast.LENGTH_SHORT).show();

    }

    public void setPreDefinedNotificationTimingsOneYear() {

        //避免發生"Numeric overflow in expression”問題，把integer改成long
        long oneSecond = 1000; long OneMinute = 60; long OneHour = 60; long oneDay = 24; long oneYear = 365;

        //設置單字的通知事件
        ContentResolver cr = getContentResolver();
        ContentValues event = new ContentValues();
        event.put(CalendarContract.Events.DTSTART, System.currentTimeMillis()+OneMinute*OneHour*oneDay*oneYear*oneSecond);  //抓現在系統的時間的一年後開始
        event.put(CalendarContract.Events.DTEND, System.currentTimeMillis()+OneMinute*OneHour*oneDay*oneYear*oneSecond+60*60*1000);
        event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedListviewItemValue);
        event.put(CalendarContract.Events.DESCRIPTION, getResources().getString(R.string.app_name));
        event.put(CalendarContract.Events.ALL_DAY, false);
        event.put(CalendarContract.Events.CALENDAR_ID, 3);
        event.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        Uri newEvent = cr.insert(CalendarContract.Events.CONTENT_URI, event);
        assert newEvent != null;
        long eventID = Long.parseLong(Objects.requireNonNull(newEvent.getLastPathSegment())); // get the event ID that is the last element in the Uri

        ContentValues reminder = new ContentValues();
        reminder.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        reminder.put(CalendarContract.Reminders.MINUTES, 0);
        reminder.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri newReminder = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminder);

        Toast.makeText(getApplicationContext(), getString(R.string.Will_send_the_notification_at) + getString(R.string.In_1_year) + getString(R.string.blank_space),Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), getString(R.string.You_can_cancel_the_notifications_any_time),Toast.LENGTH_SHORT).show();

    }

                                                                                                        //實驗用
                                                                                                    //public void setPreDefinedNotificationTimings1Minute() {
                                                                                                    //
                                                                                                    //    //設置單字的通知事件
                                                                                                    //    ContentResolver cr = getContentResolver();
                                                                                                    //    ContentValues values = new ContentValues();
                                                                                                    //    values.put(CalendarContract.Events.DTSTART, System.currentTimeMillis()+60*1*1000);  //抓現在系統的時間的1分鐘後
                                                                                                    //    values.put(CalendarContract.Events.DTEND, System.currentTimeMillis()+60*1*1000+60*60*1000);
                                                                                                    //    values.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedListviewItemValue);
                                                                                                    //    values.put(CalendarContract.Events.DESCRIPTION, "字典譯指通");
                                                                                                    //    values.put(CalendarContract.Events.ALL_DAY, false);
                                                                                                    //    values.put(CalendarContract.Events.CALENDAR_ID, 3);
                                                                                                    //    values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());
                                                                                                    //
                                                                                                    //    Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
                                                                                                    //    assert uri != null;
                                                                                                    //    long eventID = Long.parseLong(Objects.requireNonNull(uri.getLastPathSegment())); // get the event ID that is the last element in the Uri
                                                                                                    //
                                                                                                    //    Toast.makeText(getApplicationContext(), getString(R.string.Will_send_the_notification_after_an_hour_halfDay_day_week_month_year),Toast.LENGTH_LONG).show();
                                                                                                    //    //Toast.makeText(getApplicationContext(), getString(R.string.You_can_cancel_the_notifications_any_time),Toast.LENGTH_SHORT).show();
                                                                                                    //
                                                                                                    //}
                                                                                                    //
                                                                                                    //public void setPreDefinedNotificationTimings2Minutes() {
                                                                                                    //
                                                                                                    //    //設置單字的通知事件
                                                                                                    //    ContentResolver cr = getContentResolver();
                                                                                                    //    ContentValues values = new ContentValues();
                                                                                                    //    values.put(CalendarContract.Events.DTSTART, System.currentTimeMillis()+60*2*1000);  //抓現在系統的時間的1分鐘後
                                                                                                    //    values.put(CalendarContract.Events.DTEND, System.currentTimeMillis()+60*2*1000+60*60*1000);
                                                                                                    //    values.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedListviewItemValue);
                                                                                                    //    values.put(CalendarContract.Events.DESCRIPTION, "字典譯指通");
                                                                                                    //    values.put(CalendarContract.Events.ALL_DAY, false);
                                                                                                    //    values.put(CalendarContract.Events.CALENDAR_ID, 3);
                                                                                                    //    values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());
                                                                                                    //
                                                                                                    //    Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
                                                                                                    //    assert uri != null;
                                                                                                    //    long eventID = Long.parseLong(Objects.requireNonNull(uri.getLastPathSegment())); // get the event ID that is the last element in the Uri
                                                                                                    //
                                                                                                    //    Toast.makeText(getApplicationContext(), getString(R.string.Will_send_the_notification_after_an_hour_halfDay_day_week_month_year),Toast.LENGTH_LONG).show();
                                                                                                    //    //Toast.makeText(getApplicationContext(), getString(R.string.You_can_cancel_the_notifications_any_time),Toast.LENGTH_SHORT).show();
                                                                                                    //
                                                                                                    //}



    //==============================================================================================
    // 搜尋記錄頁面使用教學的Helper Method
    //==============================================================================================
    public void userInputHistoryInstructionsAlertDialog() {
        CFAlertDialog.Builder userInputHistoryInstructionsAlertDialogBuilder = new CFAlertDialog.Builder(UserInputHistory.this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setDialogBackgroundColor(Color.parseColor("#fafcd7"))
                .setCornerRadius(50)
                .setTitle(getString(R.string.Instructions))
                .setTextColor(Color.BLUE)
                .setMessage(getString(R.string.After_performing_a_search) + System.getProperty("line.separator") + getString(R.string.Finger_tap_user_input_history) + System.getProperty("line.separator") + getString(R.string.Long_press_user_input_history) + System.getProperty("line.separator") + getString(R.string.Swipe))
                .setCancelable(false) //按到旁邊的空白處AlertDialog不會消失

                //了解了
                .addButton(getString(R.string.Got_it)
                        , Color.WHITE, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (userInputHistoryInstructionsAlertDialog, which) -> {

                            userInputHistoryInstructionsAlertDialog.dismiss();
                        })

                //看使用教學
                .addButton(getString(R.string.Watch_tutorial)
                        , Color.BLACK, Color.LTGRAY, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.END, (userInputHistoryInstructionsAlertDialog, which) -> {

                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/UQVNGoyuQJA")));
                            userInputHistoryInstructionsAlertDialog.dismiss();
                        });

        userInputHistoryInstructionsAlertDialogBuilder.show();
    }


                                                                                                    //將字母轉換成數字  A-Z ：1-26
                                                                                                    //    public long convertStringToLong() {
                                                                                                    //        int length = selectedListviewItemValue.length();
                                                                                                    //        int num = 0;
                                                                                                    //        int number = 0;
                                                                                                    //        for(int i = 0; i < length; i++) {
                                                                                                    //            char ch = selectedListviewItemValue.charAt(length - i - 1);
                                                                                                    //            num = ch - 'A' + 1;
                                                                                                    //            num *= Math.pow(26, i);
                                                                                                    //            number += num;
                                                                                                    //        }
                                                                                                    //        return eventID = (long) number;
                                                                                                    //    }
                                                                                                    //
                                                                                                    //
                                                                                                    //    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                                                                                                    //    void deleteEventById() {
                                                                                                    //        ContentResolver cr = getApplicationContext().getContentResolver();
                                                                                                    //        ContentValues values = new ContentValues();
                                                                                                    //        Uri deleteUri;
                                                                                                    //        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
                                                                                                    //        int rows = getApplicationContext().getContentResolver().delete(deleteUri, null, null);
                                                                                                    //        Log.i("TAG", "Rows deleted: " + rows);
                                                                                                    //    }



                                                                                                    ///**
                                                                                                    // * 讓用戶清空列表 (已換成別的外掛，目前用不到)
                                                                                                    // */
                                                                                                    //    clearUserInputList.setOnClickListener(new View.OnClickListener() {
                                                                                                    //    @Override
                                                                                                    //    public void onClick(View v) {
                                                                                                    //
                                                                                                    //        //這邊設置AlertDialog讓用戶確認是否真要清除列表
                                                                                                    //        AlertDialog.Builder doYouReallyWantToClearListAlertDialog = new AlertDialog.Builder(UserInputHistory.this);
                                                                                                    //        doYouReallyWantToClearListAlertDialog.setTitle(getString(R.string.Do_you_really_want_to_clear_the_list));
                                                                                                    //        doYouReallyWantToClearListAlertDialog.setCancelable(false); //按到旁邊的空白處AlertDialog也不會消失
                                                                                                    //        doYouReallyWantToClearListAlertDialog.setView(R.layout.custom_alert_dialog_dictionary_providers); //沿用字典選單的佈局檔
                                                                                                    //
                                                                                                    //        //AlertDialog的確定鈕，清除列表
                                                                                                    //        doYouReallyWantToClearListAlertDialog.setPositiveButton(R.string.Confirm, new DialogInterface.OnClickListener() {
                                                                                                    //
                                                                                                    //            @Override
                                                                                                    //            public void onClick(DialogInterface dialog, int which) {
                                                                                                    //
                                                                                                    //                mChildReferenceForInputHistory.child(username).removeValue(); //清除雲端用戶名稱的node
                                                                                                    //
                                                                                                    //                userInputArraylist.clear(); //同時清除本地的list
                                                                                                    //                userInputArrayAdapter.notifyDataSetChanged();
                                                                                                    //
                                                                                                    //                //將搜尋紀錄的列表存到SharedPreferences
                                                                                                    //                SharedPreferences.Editor editor = getSharedPreferences("userInputArrayListSharedPreferences", MODE_PRIVATE).edit();
                                                                                                    //                editor.putInt("userInputArrayListValues", userInputArraylist.size());
                                                                                                    //                for (int i = 0; i < userInputArraylist.size(); i++) {
                                                                                                    //                    editor.putString("userInputArrayListItem_" + i, userInputArraylist.get(i));
                                                                                                    //                }
                                                                                                    //                editor.apply();
                                                                                                    //
                                                                                                    //                Toast.makeText(getApplicationContext(), R.string.List_cleared, Toast.LENGTH_SHORT).show();
                                                                                                    //
                                                                                                    //            }
                                                                                                    //        });
                                                                                                    //
                                                                                                    //
                                                                                                    //        //AlertDialog的取消鈕
                                                                                                    //        doYouReallyWantToClearListAlertDialog.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                                                                                                    //
                                                                                                    //            @Override
                                                                                                    //            public void onClick(DialogInterface dialog, int which) {
                                                                                                    //                dialog.dismiss();
                                                                                                    //            }
                                                                                                    //        });
                                                                                                    //
                                                                                                    //        //把AlertDialog顯示出來
                                                                                                    //        doYouReallyWantToClearListAlertDialog.create().show();
                                                                                                    //
                                                                                                    //    }
                                                                                                    //
                                                                                                    //});




}

