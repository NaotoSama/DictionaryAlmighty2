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
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;

import static com.example.android.dictionaryalmighty2.MainActivity.comboSearchButton;
import static com.example.android.dictionaryalmighty2.MainActivity.defaultSearchButton;
import static com.example.android.dictionaryalmighty2.MainActivity.mChildReferenceForVocabularyList;
import static com.example.android.dictionaryalmighty2.MainActivity.myVocabularyArrayList;
import static com.example.android.dictionaryalmighty2.MainActivity.username;
import static com.example.android.dictionaryalmighty2.MainActivity.wordInputView;
import static com.example.android.dictionaryalmighty2.UserInputHistory.presetNotificationTimingsList;

public class WordsToMemorize extends AppCompatActivity {

    RelativeLayout.LayoutParams layoutparams;   //用來客製化修改ActionBar
    TextView customActionBarTextviewforUserInputHistoryPage;
    androidx.appcompat.app.ActionBar actionBar;

    TextView wordsToMemorizeTextView;
    ImageView clickHereFingerWordsToMemorizeImageView;

    Button clearMyVocabularyList;
                                                                    //    Button aboutMemorizingWordsButton;
    String selectedMyVocabularyListviewItemValue;

    EditText wordsToMemorizeSearchBox;

    ListView myVocabularyListview;
    ArrayAdapter myVocabularyArrayAdapter;

    Calendar c;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.words_to_memorize);


        /**
         * 設置ActionBar
         */
        actionBar = getSupportActionBar();
        customActionBarTextviewforUserInputHistoryPage = new TextView(this);
        layoutparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        customActionBarForWordsToMemorizePage();   //Helper Method


        //findViewById
        myVocabularyListview = findViewById(R.id.my_vocabulary_listview);
        clearMyVocabularyList = findViewById(R.id.clear_my_vocabulary_list_button);
                                                                    //        aboutMemorizingWordsButton = findViewById(R.id.about_memorizing_words_button);
        wordsToMemorizeSearchBox = findViewById(R.id.words_to_memorize_search_box);
        wordsToMemorizeTextView = findViewById(R.id.words_to_memorize_textView);
        clickHereFingerWordsToMemorizeImageView = findViewById(R.id.click_here_finger_words_to_memorize);


        //Initialize the adapter
        myVocabularyArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myVocabularyArrayList);
        myVocabularyListview.setAdapter(myVocabularyArrayAdapter);

        if (username!=null && !username.equals("")) {  //檢查有用戶有登入，才能跑以下程式碼
            mChildReferenceForVocabularyList.child(username).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        //Get the data from snapshot
                        String myVocabulary = postSnapshot.getValue(String.class);

                        //Add the data to the arraylist
                        myVocabularyArrayList.add(myVocabulary);

                        //透過HashSet自動過濾掉userInputArraylist中重複的字
                        HashSet<String> myVocabularyArraylistHashSet = new HashSet<>();
                        myVocabularyArraylistHashSet.addAll(myVocabularyArrayList);
                        myVocabularyArrayList.clear();
                        myVocabularyArrayList.addAll(myVocabularyArraylistHashSet);

                        //Alphabetic sorting
                        Collections.sort(myVocabularyArrayList);

                        myVocabularyArrayAdapter.notifyDataSetChanged();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }


        /**
         * 設置使用說明
         */
        wordsToMemorizeTextView.setText(R.string.Instructions);

        wordsToMemorizeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                wordsToMemorizeInstructionsAlertDialog();
            }
        });

        clickHereFingerWordsToMemorizeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                wordsToMemorizeInstructionsAlertDialog();
            }
        });


        /**
         * 讓用戶搜尋列表
         */
        wordsToMemorizeSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                (WordsToMemorize.this).myVocabularyArrayAdapter.getFilter().filter(charSequence);

            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        /**
         * 讓用戶清空列表
         */
        clearMyVocabularyList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //這邊設置AlertDialog讓用戶確認是否真要清除列表
                CFAlertDialog.Builder doYouReallyWantToClearListAlertDialog = new CFAlertDialog.Builder(WordsToMemorize.this)
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                        .setDialogBackgroundColor(Color.parseColor("#fafcd7"))
                        .setCornerRadius(50)
                        .setTitle(getString(R.string.Do_you_really_want_to_clear_the_list)
                        )
                        .setTextColor(Color.BLUE)
                        .setMessage(getString(R.string.Clear_user_vocabulary_list_confirmation_message))
                        .setCancelable(false)  //按到旁邊的空白處AlertDialog也不會消失

                        //AlertDialog的確定鈕，清除列表
                        .addButton(getString(R.string.Confirm)
                                , Color.WHITE, Color.GREEN, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {

                                    mChildReferenceForVocabularyList.child(username).removeValue(); //清除雲端用戶名稱的node

                                    myVocabularyArrayList.clear(); //同時清除本地的list
                                    myVocabularyArrayAdapter.notifyDataSetChanged();

                                    //將搜尋紀錄的列表存到SharedPreferences
                                    SharedPreferences.Editor editor = getSharedPreferences("myVocabularyArrayListSharedPreferences", MODE_PRIVATE).edit();
                                    editor.putInt("myVocabularyArrayListValues", myVocabularyArrayList.size());
                                    for (int i = 0; i < myVocabularyArrayList.size(); i++) {
                                        editor.putString("myVocabularyArrayListItem_" + i, myVocabularyArrayList.get(i));
                                    }
                                    editor.apply();

                                    Toast.makeText(getApplicationContext(), R.string.List_cleared, Toast.LENGTH_SHORT).show();

                                    dialog.dismiss();
                                })


                        //AlertDialog的取消鈕
                        .addButton(getString(R.string.Cancel)
                                , Color.CYAN, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                                    dialog.dismiss();
                                });


                doYouReallyWantToClearListAlertDialog.setHeaderView(R.layout.custom_alert_diaglog_question_mark);
                //把AlertDialog顯示出來
                doYouReallyWantToClearListAlertDialog.show();
            }

        });



        /**
         * Let the user click on an item and pass the item to WordInputView
         */
        myVocabularyListview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                selectedMyVocabularyListviewItemValue=myVocabularyListview.getItemAtPosition(position).toString();

                wordInputView.setText(selectedMyVocabularyListviewItemValue);

                finish(); //結束此Activity並返回上一個Activity

            }
        });



        /**
         * Let the user long click on an item
         */
        myVocabularyListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                selectedMyVocabularyListviewItemValue=myVocabularyListview.getItemAtPosition(position).toString();
                wordInputView.setText(selectedMyVocabularyListviewItemValue);

                setChooseQuickSearchOrComboSearchAlertDialog();

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

                                    String wordToDelete = listView.getItemAtPosition(position).toString();

                                    if (username!=null && !username.equals("")) {
                                        myVocabularyArrayList.remove(wordToDelete);  //同時本從地的list移除該字
                                        myVocabularyArrayAdapter.notifyDataSetChanged();
                                        Query query = mChildReferenceForVocabularyList.child(username).orderByValue().equalTo(wordToDelete); //在資料庫中尋找要刪除的字

                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                                    snapshot.getRef().removeValue(); //刪除該字

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                // throw databaseError.toException();  這句拿掉以免閃退
                                            }
                                        });
                                    }


                                                                                                    //myVocabularyArrayList.remove(wordToDelete);  //同時本從地的list移除該字
                                                                                                    //myVocabularyArrayAdapter.notifyDataSetChanged();
                                                                                                    //
                                                                                                    ////將搜尋紀錄的列表存到SharedPreferences
                                                                                                    //SharedPreferences.Editor editor = getSharedPreferences("myVocabularyArrayListSharedPreferences", MODE_PRIVATE).edit();
                                                                                                    //editor.putInt("myVocabularyArrayListValues", myVocabularyArrayList.size());
                                                                                                    //for (int i = 0; i < myVocabularyArrayList.size(); i++)
                                                                                                    //{
                                                                                                    //editor.putString("myVocabularyArrayListItem_"+i, myVocabularyArrayList.get(i));
                                                                                                    //}
                                                                                                    //editor.apply();

                                    Toast.makeText(getApplicationContext(), wordToDelete + getResources().getString(R.string.Has_benn_deleted), Toast.LENGTH_SHORT).show();
                                    reloadCurrentActivity();

                                }


                            }
                        });
        myVocabularyListview.setOnTouchListener(touchListener);



                                                                                                    ///**
                                                                                                    // * Take the user to the "About memorizing words" page
                                                                                                    // */
                                                                                                    //aboutMemorizingWordsButton.setOnClickListener(new View.OnClickListener() {
                                                                                                    //    @Override
                                                                                                    //    public void onClick(View v) {
                                                                                                    //
                                                                                                    //        Intent intent = new Intent(WordsToMemorize.this, AboutMemorizingWords.class);
                                                                                                    //        startActivity(intent);
                                                                                                    //    }
                                                                                                    //});


    }




    @Override
    protected void onStart() {
        super.onStart();

        c = Calendar.getInstance(); // 順便取得目前日期與時間，設置單字記憶通知要用的
    }



//==============================================================================================
// 所有helper methods
//==============================================================================================





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
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5")));
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(customActionBarTextviewforUserInputHistoryPage);

    }



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



    //==============================================================================================
    // 讓用戶選擇快搜模式或三連搜模式，或記憶單字的Helper Method
    //==============================================================================================
    public void setChooseQuickSearchOrComboSearchAlertDialog() {
        //這邊設置第一層AlertDialog讓用戶選擇快搜模式或三連搜模式，或記憶單字
        CFAlertDialog.Builder chooseQuickSearchOrComboSearchAlertDialogBuilder = new CFAlertDialog.Builder(WordsToMemorize.this)
        .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
        .setDialogBackgroundColor(Color.parseColor("#fafcd7"))
        .setCornerRadius(50)
        .setTitle(getString(R.string.Do_you_want_to))
        .setTextColor(Color.BLUE)
        .setMessage(getString(R.string.Quick_search_explanation) + System.getProperty("line.separator") + getString(R.string.Combo_search_explanation))
        .setCancelable(false) //按到旁邊的空白處AlertDialog不會消失

        //第一層AlertDialog的確定鈕，使用快搜模式
        .addButton(getString(R.string.Quick_search)
                , Color.WHITE, Color.GREEN, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (chooseQuickSearchOrComboSearchAlertDialog, which) -> {

                defaultSearchButton.performClick();

                //Bring MainActivity to the top of the stack
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);

                chooseQuickSearchOrComboSearchAlertDialog.dismiss();
        })

        //第一層AlertDialog的中立鈕，使用三連搜模式
        .addButton(getString(R.string.Combo_search)
                        , Color.BLACK, Color.YELLOW, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (chooseQuickSearchOrComboSearchAlertDialog, which) -> {

                comboSearchButton.performClick();

                chooseQuickSearchOrComboSearchAlertDialog.dismiss();
        })

        //第一層AlertDialog的取消鈕，記憶單字
        .addButton(getString(R.string.Memorize_this_word)
                        , Color.WHITE, Color.MAGENTA, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (chooseActionAlertDialog, whichLayer1) -> {

                            //這邊設置第二層AlertDialog讓用戶選擇自定義或預設的通知時機
                            final CFAlertDialog.Builder chooseCustomizedOrPredefinedNotificationAlertDialogBuilder  = new CFAlertDialog.Builder(WordsToMemorize.this)
                                    .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                    .setDialogBackgroundColor(Color.parseColor("#fafcd7"))
                                    .setCornerRadius(50)
                                    .setTitle(getString(R.string.Choose_customized_or_predefined_notification_timings))
                                    .setMessage(getString(R.string.Predefined_timing_explanation) + System.getProperty("line.separator") + getString(R.string.User_configured_timing_explanation))
                                    .setTextColor(Color.BLUE)
                                    .setCancelable(false) //按到旁邊的空白處AlertDialog也不會消失


                                    //第二層AlertDialog的確定鈕，預設的通知時機。
                                    .addButton(getString(R.string.Use_predefined_timing)
                                            , Color.WHITE, Color.GREEN, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (chooseCustomizedOrPredefinedNotificationAlertDialog, whichLayer2) -> {

                                                presetNotificationTimingsList = getResources().getStringArray(R.array.preset_notification_timings);

                                                //這邊設置第三層AlertDialog讓用戶選擇各種預設通知的時機點
                                                CFAlertDialog.Builder choosePresetNotificationTimingsAlertDialogBuilder = new CFAlertDialog.Builder(WordsToMemorize.this)
                                                        .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                                        .setDialogBackgroundColor(Color.parseColor("#fafcd7"))
                                                        .setCornerRadius(50)
                                                        .setTitle(getString(R.string.Choose_one_preset_timing))
                                                        .setTextColor(Color.BLUE)
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
                                                                        setPreDefinedNotificationTimings1Hour();
                                                                        choosePresetNotificationTimingsAlertDialog.dismiss();
                                                                        break;
                                                                    case 2:
                                                                        setPreDefinedNotificationTimings9Hours();
                                                                        choosePresetNotificationTimingsAlertDialog.dismiss();
                                                                        break;
                                                                    case 3:
                                                                        setPreDefinedNotificationTimings1Day();
                                                                        choosePresetNotificationTimingsAlertDialog.dismiss();
                                                                        break;
                                                                    case 4:
                                                                        setPreDefinedNotificationTimings2Days();
                                                                        choosePresetNotificationTimingsAlertDialog.dismiss();
                                                                        break;
                                                                    case 5:
                                                                        setPreDefinedNotificationTimings7Days();
                                                                        choosePresetNotificationTimingsAlertDialog.dismiss();
                                                                        break;
                                                                    case 6:
                                                                        setPreDefinedNotificationTimings1Month();
                                                                        choosePresetNotificationTimingsAlertDialog.dismiss();
                                                                        break;
                                                                    case 7:
                                                                        setPreDefinedNotificationTimingsHalfYear();
                                                                        choosePresetNotificationTimingsAlertDialog.dismiss();
                                                                        break;
                                                                    case 8:
                                                                        setPreDefinedNotificationTimingsOneYear();
                                                                        choosePresetNotificationTimingsAlertDialog.dismiss();
                                                                        break;
                                                                }
                                                            }
                                                        })

                                                        //第三層AlertDialog的取消鈕
                                                        .addButton(getString(R.string.Cancel)
                                                                , Color.WHITE, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (choosePresetNotificationTimingsAlertDialog, whichLayer3) ->{

                                                                    choosePresetNotificationTimingsAlertDialog.dismiss();
                                                                });

                                                //把第三層的AlertDialog顯示出來
                                                choosePresetNotificationTimingsAlertDialogBuilder.show();
                                                //同時讓第二層的AlertDialog消失
                                                chooseCustomizedOrPredefinedNotificationAlertDialog.dismiss();
                                            })


                                    //第二層AlertDialog的中立鈕，自定義通知時機
                                    .addButton(getString(R.string.Customize_timing)
                                            , Color.BLACK, Color.YELLOW, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (chooseCustomizedOrPredefinedNotificationAlertDialog, whichLayer2) ->{

                                                setCustomizedNotificationTiming();

                                                chooseCustomizedOrPredefinedNotificationAlertDialog.dismiss();
                                            })


                                    //第二層AlertDialog的取消鈕
                                    .addButton(getString(R.string.Cancel)
                                            , Color.CYAN, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (chooseCustomizedOrPredefinedNotificationAlertDialog, whichLayer2) ->{

                                                chooseCustomizedOrPredefinedNotificationAlertDialog.dismiss();
                                            });


                            chooseCustomizedOrPredefinedNotificationAlertDialogBuilder.setHeaderView(R.layout.custom_alert_dialog_clock);
                            //把第二層的AlertDialog顯示出來
                            chooseCustomizedOrPredefinedNotificationAlertDialogBuilder.show();
                            //同時讓第一層的AlertDialog消失
                            chooseActionAlertDialog.dismiss();

                        })

        //第一層AlertDialog的取消鈕
                .addButton(getString(R.string.Cancel)
                        , Color.WHITE, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (chooseQuickSearchOrComboSearchAlertDialog, which) -> {

                chooseQuickSearchOrComboSearchAlertDialog.dismiss();
        });


        chooseQuickSearchOrComboSearchAlertDialogBuilder.setHeaderView(R.layout.custom_alert_diaglog_question_mark);
        //將第一層AlertDialog顯示出來
        chooseQuickSearchOrComboSearchAlertDialogBuilder.show();
    }


    //==============================================================================================
    // 單字本頁面使用教學的Helper Method
    //==============================================================================================
    public void wordsToMemorizeInstructionsAlertDialog() {
        CFAlertDialog.Builder wordsToMemorizeInstructionsAlertDialogBuilder = new CFAlertDialog.Builder(WordsToMemorize.this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setDialogBackgroundColor(Color.parseColor("#fafcd7"))
                .setCornerRadius(50)
                .setTitle(getString(R.string.Instructions))
                .setTextColor(Color.BLUE)
                .setMessage(getString(R.string.After_saving_a_word) + System.getProperty("line.separator") + getString(R.string.Finger_tap_words_to_memorize) + System.getProperty("line.separator") + getString(R.string.Long_press_words_to_memorize) + System.getProperty("line.separator") + getString(R.string.Swipe)+ System.getProperty("line.separator") + getResources().getString(R.string.Your_vocabulary_list_will_be_stored_online))
                .setCancelable(false) //按到旁邊的空白處AlertDialog不會消失


                .addButton(getString(R.string.Got_it)
                        , Color.WHITE, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (wordsToMemorizeInstructionsAlertDialog, which) -> {

                            wordsToMemorizeInstructionsAlertDialog.dismiss();
                        });

        wordsToMemorizeInstructionsAlertDialogBuilder.show();
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
                            ContentValues event = new ContentValues();
                            event.put(CalendarContract.Events.DTSTART, c.getTimeInMillis());
                            event.put(CalendarContract.Events.DTEND, c.getTimeInMillis()+60*60*1000);
                            event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedMyVocabularyListviewItemValue);
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
        event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedMyVocabularyListviewItemValue);
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
        event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedMyVocabularyListviewItemValue);
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
        event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedMyVocabularyListviewItemValue);
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
        event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedMyVocabularyListviewItemValue);
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
        event.put(CalendarContract.Events.DTSTART, System.currentTimeMillis()+60*60*24*7*1000);  //抓現在系統的時間的7天後開始
        event.put(CalendarContract.Events.DTEND, System.currentTimeMillis()+60*60*24*7*1000+60*60*1000);
        event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedMyVocabularyListviewItemValue);
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
        event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedMyVocabularyListviewItemValue);
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
        event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedMyVocabularyListviewItemValue);
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
        event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + selectedMyVocabularyListviewItemValue);
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




///**
// * 讓用戶清空列表 (已換成別的外掛，目前用不到)
// */
//    clearMyVocabularyList.setOnClickListener(new View.OnClickListener() {
//    @Override
//    public void onClick(View v) {
//
//        //這邊設置AlertDialog讓用戶確認是否真要清除列表
//        AlertDialog.Builder doYouReallyWantToClearListAlertDialog = new AlertDialog.Builder(WordsToMemorize.this);
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
//                mChildReferenceForVocabularyList.child(username).removeValue(); //清除雲端用戶名稱的node
//
//                myVocabularyArrayList.clear(); //同時清除本地的list
//                myVocabularyArrayAdapter.notifyDataSetChanged();
//
//                //將搜尋紀錄的列表存到SharedPreferences
//                SharedPreferences.Editor editor = getSharedPreferences("myVocabularyArrayListSharedPreferences", MODE_PRIVATE).edit();
//                editor.putInt("myVocabularyArrayListValues", myVocabularyArrayList.size());
//                for (int i = 0; i < myVocabularyArrayList.size(); i++) {
//                    editor.putString("myVocabularyArrayListItem_" + i, myVocabularyArrayList.get(i));
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