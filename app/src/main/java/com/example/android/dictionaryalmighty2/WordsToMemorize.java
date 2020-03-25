package com.example.android.dictionaryalmighty2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.HashSet;

import static com.example.android.dictionaryalmighty2.MainActivity.comboSearchButton;
import static com.example.android.dictionaryalmighty2.MainActivity.defaultSearchButton;
import static com.example.android.dictionaryalmighty2.MainActivity.mChildReferenceForVocabularyList;
import static com.example.android.dictionaryalmighty2.MainActivity.myVocabularyArrayList;
import static com.example.android.dictionaryalmighty2.MainActivity.username;
import static com.example.android.dictionaryalmighty2.MainActivity.wordInputView;

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
                CFAlertDialog.Builder doYouReallyWantToClearListAlertDialog
                        = new CFAlertDialog.Builder(WordsToMemorize.this)
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

                String selectedMyVocabularyListviewItemValue=myVocabularyListview.getItemAtPosition(position).toString();
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


                                    myVocabularyArrayList.remove(position);  //同時本從地的list移除該字
                                    myVocabularyArrayAdapter.notifyDataSetChanged();

                                    //將搜尋紀錄的列表存到SharedPreferences
                                    SharedPreferences.Editor editor = getSharedPreferences("myVocabularyArrayListSharedPreferences", MODE_PRIVATE).edit();
                                    editor.putInt("myVocabularyArrayListValues", myVocabularyArrayList.size());
                                    for (int i = 0; i < myVocabularyArrayList.size(); i++)
                                    {
                                        editor.putString("myVocabularyArrayListItem_"+i, myVocabularyArrayList.get(i));
                                    }
                                    editor.apply();

                                    Toast.makeText(getApplicationContext(), wordToDelete + getResources().getString(R.string.Has_benn_deleted), Toast.LENGTH_SHORT).show();

                                }


                            }
                        });
        myVocabularyListview.setOnTouchListener(touchListener);



                                                                    //        /**
                                                                    //         * Take the user to the "About memorizing words" page
                                                                    //         */
                                                                    //        aboutMemorizingWordsButton.setOnClickListener(new View.OnClickListener() {
                                                                    //            @Override
                                                                    //            public void onClick(View v) {
                                                                    //
                                                                    //                Intent intent = new Intent(WordsToMemorize.this, AboutMemorizingWords.class);
                                                                    //                startActivity(intent);
                                                                    //            }
                                                                    //        });


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



    public void setChooseQuickSearchOrComboSearchAlertDialog() {
        //這邊設置AlertDialog讓用戶選擇快搜模式或三連搜模式，或估狗翻譯
        CFAlertDialog.Builder chooseQuickSearchOrComboSearchAlertDialogBuilder = new CFAlertDialog.Builder(WordsToMemorize.this)
        .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
        .setDialogBackgroundColor(Color.parseColor("#fafcd7"))
        .setCornerRadius(50)
        .setTitle(getString(R.string.Do_you_want_to))
        .setTextColor(Color.BLUE)
        .setMessage(getString(R.string.Quick_search_explanation) + System.getProperty("line.separator") + getString(R.string.Combo_search_explanation))
        .setCancelable(false) //按到旁邊的空白處AlertDialog不會消失

        //AlertDialog的確定鈕，使用快搜模式
        .addButton(getString(R.string.Quick_search)
                , Color.WHITE, Color.GREEN, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (chooseQuickSearchOrComboSearchAlertDialog, which) -> {

                defaultSearchButton.performClick();

                //Bring MainActivity to the top of the stack
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);

                chooseQuickSearchOrComboSearchAlertDialog.dismiss();
        })

        //AlertDialog的中立鈕，使用三連搜模式
                .addButton(getString(R.string.Combo_search)
                        , Color.BLACK, Color.YELLOW, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (chooseQuickSearchOrComboSearchAlertDialog, which) -> {

                comboSearchButton.performClick();

                chooseQuickSearchOrComboSearchAlertDialog.dismiss();
        })

        //AlertDialog的取消鈕
                .addButton(getString(R.string.Cancel)
                        , Color.WHITE, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (chooseQuickSearchOrComboSearchAlertDialog, which) -> {

                chooseQuickSearchOrComboSearchAlertDialog.dismiss();
        });


        chooseQuickSearchOrComboSearchAlertDialogBuilder.setHeaderView(R.layout.custom_alert_diaglog_question_mark);
        //將AlertDialog顯示出來
        chooseQuickSearchOrComboSearchAlertDialogBuilder.show();
    }


    public void wordsToMemorizeInstructionsAlertDialog() {
        CFAlertDialog.Builder wordsToMemorizeInstructionsAlertDialogBuilder = new CFAlertDialog.Builder(WordsToMemorize.this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setDialogBackgroundColor(Color.parseColor("#fafcd7"))
                .setCornerRadius(50)
                .setTitle(getString(R.string.Instructions))
                .setTextColor(Color.BLUE)
                .setMessage(getString(R.string.After_saving_a_word) + System.getProperty("line.separator") + getString(R.string.Finger_tap_words_to_memorize) + System.getProperty("line.separator") + getString(R.string.Long_press_words_to_memorize) + System.getProperty("line.separator") + getString(R.string.Swipe))
                .setCancelable(false) //按到旁邊的空白處AlertDialog不會消失


                .addButton(getString(R.string.Got_it)
                        , Color.WHITE, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (wordsToMemorizeInstructionsAlertDialog, which) -> {

                            wordsToMemorizeInstructionsAlertDialog.dismiss();
                        });

        wordsToMemorizeInstructionsAlertDialogBuilder.show();
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