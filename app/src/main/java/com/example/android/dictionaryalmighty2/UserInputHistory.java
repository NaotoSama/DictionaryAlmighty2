package com.example.android.dictionaryalmighty2;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserInputHistory extends AppCompatActivity {

    RelativeLayout.LayoutParams layoutparams;   //用來客製化修改ActionBar
    TextView customActionBarTextviewforUserInputHistoryPage;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_input_history);

        final ListView userInputListview;
        final ArrayAdapter userInputArrayAdapter;


        /**
         * 設置ActionBar
         */
        actionBar = getSupportActionBar();
        customActionBarTextviewforUserInputHistoryPage = new TextView(this);
        layoutparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        customActionBarForUserInputHistoryPage();   //Helper Method


        userInputListview = findViewById(R.id.user_input_listview);


        /**
         * Let the user click on an item and pass the item value to wordInputView
         */
        userInputArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, MainActivity.userInputArraylist);
        userInputListview.setAdapter(userInputArrayAdapter);

        userInputListview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedListviewItemValue=userInputListview.getItemAtPosition(position).toString();
                MainActivity.wordInputView.setText(selectedListviewItemValue);

                finish(); //結束此Activity並返回上一個Activity

            }
        });



        /**
         * Let the user long click on an item and delete the item
         */
        userInputListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

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

                Toast.makeText(getApplicationContext(), R.string.Your_selected_item_has_benn_deleted, Toast.LENGTH_LONG).show();

                return true;
            }

        });


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


}
