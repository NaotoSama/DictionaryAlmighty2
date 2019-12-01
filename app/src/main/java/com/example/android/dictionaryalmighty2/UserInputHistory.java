package com.example.android.dictionaryalmighty2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Collections;
import java.util.HashSet;

import static com.example.android.dictionaryalmighty2.MainActivity.myVocabularyArrayList;

public class UserInputHistory extends AppCompatActivity {

    RelativeLayout.LayoutParams layoutparams;   //用來客製化修改ActionBar
    TextView customActionBarTextviewforUserInputHistoryPage;
    androidx.appcompat.app.ActionBar actionBar;
    Button wordToMemorizeButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_input_history);

        final ListView userInputListview;
        final ArrayAdapter userInputArrayAdapter;


        //findViewById
        wordToMemorizeButton = findViewById(R.id.cancel_all_notifications_button);
        userInputListview = findViewById(R.id.user_input_listview);

        //Initialize the adapter
        userInputArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, MainActivity.userInputArraylist);
        userInputListview.setAdapter(userInputArrayAdapter);



        /**
         * 設置ActionBar
         */
        actionBar = getSupportActionBar();
        customActionBarTextviewforUserInputHistoryPage = new TextView(this);
        layoutparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        customActionBarForUserInputHistoryPage();   //Helper Method


        wordToMemorizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(UserInputHistory.this, WordsToMemorize.class);
                startActivity(intent);

            }
        });



        /**
         * Let the user click on an item and pass the item value to wordInputView
         */


        userInputListview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedListviewItemValue=userInputListview.getItemAtPosition(position).toString();
                MainActivity.wordInputView.setText(selectedListviewItemValue);

                finish(); //結束此Activity並返回上一個Activity

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

                                    MainActivity.userInputArraylist.remove(position);
                                    userInputArrayAdapter.notifyDataSetChanged();

                                    //將搜尋紀錄的列表存到SharedPreferences
                                    SharedPreferences.Editor editor = getSharedPreferences("userInputArrayListSharedPreferences", MODE_PRIVATE).edit();
                                    editor.putInt("userInputArrayListValues", MainActivity.userInputArraylist.size());
                                    for (int i = 0; i < MainActivity.userInputArraylist.size(); i++)
                                    {
                                        editor.putString("userInputArrayListItem_"+i, MainActivity.userInputArraylist.get(i));
                                    }
                                    editor.apply();

                                    Toast.makeText(getApplicationContext(), R.string.Your_selected_item_has_benn_deleted, Toast.LENGTH_SHORT).show();

                                }


                            }
                        });
        userInputListview.setOnTouchListener(touchListener);


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


    /**
     * Helper method for saving myVocabularyArrayList to SharedPreferences
     */
    public void saveMyVocabularyArrayListToSharedPreferences() {
        SharedPreferences.Editor editor = getSharedPreferences("myVocabularyArrayListSharedPreferences", MODE_PRIVATE).edit();
        editor.putInt("myVocabularyArrayListValues", myVocabularyArrayList.size());
        for (int i = 0; i < myVocabularyArrayList.size(); i++)
        {
            editor.putString("myVocabularyArrayListValues"+i, myVocabularyArrayList.get(i));
        }
        editor.apply();

    }


}
