package com.example.android.dictionaryalmighty2;

import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.android.dictionaryalmighty2.MainActivity.comboSearchButton;
import static com.example.android.dictionaryalmighty2.MainActivity.defaultSearchButton;
import static com.example.android.dictionaryalmighty2.MainActivity.myVocabularyArrayList;
import static com.example.android.dictionaryalmighty2.MainActivity.wordInputView;

public class WordsToMemorize extends AppCompatActivity {

    RelativeLayout.LayoutParams layoutparams;   //用來客製化修改ActionBar
    TextView customActionBarTextviewforUserInputHistoryPage;
    androidx.appcompat.app.ActionBar actionBar;

    Button clearMyVocabularyList;
    Button aboutMemorizingWordsButton;
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
        aboutMemorizingWordsButton = findViewById(R.id.about_memorizing_words_button);
        wordsToMemorizeSearchBox = findViewById(R.id.words_to_memorize_search_box);


        //Initialize the adapter
        myVocabularyArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myVocabularyArrayList);
        myVocabularyListview.setAdapter(myVocabularyArrayAdapter);



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
                AlertDialog.Builder doYouReallyWantToClearListAlertDialog = new AlertDialog.Builder(WordsToMemorize.this);
                doYouReallyWantToClearListAlertDialog.setTitle(getString(R.string.Do_you_really_want_to_clear_the_list));
                doYouReallyWantToClearListAlertDialog.setCancelable(false); //按到旁邊的空白處AlertDialog也不會消失
                doYouReallyWantToClearListAlertDialog.setView(R.layout.custom_alert_dialog_dictionary_providers); //沿用字典選單的佈局檔

                //AlertDialog的確定鈕，清除列表
                doYouReallyWantToClearListAlertDialog.setPositiveButton(R.string.Confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        myVocabularyArrayList.clear();
                        myVocabularyArrayAdapter.notifyDataSetChanged();

                        //將搜尋紀錄的列表存到SharedPreferences
                        SharedPreferences.Editor editor = getSharedPreferences("myVocabularyArrayListSharedPreferences", MODE_PRIVATE).edit();
                        editor.putInt("myVocabularyArrayListValues", myVocabularyArrayList.size());
                        for (int i = 0; i < myVocabularyArrayList.size(); i++) {
                            editor.putString("myVocabularyArrayListItem_" + i, myVocabularyArrayList.get(i));
                        }
                        editor.apply();

                        Toast.makeText(getApplicationContext(), R.string.List_cleared, Toast.LENGTH_SHORT).show();

                    }
                });


                //AlertDialog的取消鈕
                doYouReallyWantToClearListAlertDialog.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                //把AlertDialog顯示出來
                doYouReallyWantToClearListAlertDialog.create().show();

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



        /**
         * Take the user to the "About memorizing words" page
         */
        aboutMemorizingWordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(WordsToMemorize.this, AboutMemorizingWords.class);
                startActivity(intent);
            }
        });


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
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5")));
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(customActionBarTextviewforUserInputHistoryPage);

    }



    public void setChooseQuickSearchOrComboSearchAlertDialog() {
        //這邊設置AlertDialog讓用戶選擇快搜模式或三連搜模式，或估狗翻譯
        AlertDialog.Builder chooseQuickSearchOrComboSearchAlertDialog = new AlertDialog.Builder(this);
        chooseQuickSearchOrComboSearchAlertDialog.setTitle(getString(R.string.Do_you_want_to));
        chooseQuickSearchOrComboSearchAlertDialog.setCancelable(false); //按到旁邊的空白處AlertDialog不會消失
        chooseQuickSearchOrComboSearchAlertDialog.setView(R.layout.custom_alert_dialog_dictionary_providers); //沿用字典選單的佈局檔

        //AlertDialog的確定鈕，使用快搜模式
        chooseQuickSearchOrComboSearchAlertDialog.setPositiveButton(R.string.Quick_lookup, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                defaultSearchButton.performClick();

                //Bring MainActivity to the top of the stack
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        //AlertDialog的中立鈕，使用三連搜模式
        chooseQuickSearchOrComboSearchAlertDialog.setNeutralButton(R.string.Combo_search, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                comboSearchButton.performClick();
            }
        });

        //AlertDialog的取消鈕
        chooseQuickSearchOrComboSearchAlertDialog.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });


        //將AlertDialog顯示出來
        chooseQuickSearchOrComboSearchAlertDialog.create().show();
    }




}