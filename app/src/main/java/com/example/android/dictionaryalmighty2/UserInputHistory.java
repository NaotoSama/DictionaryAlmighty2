package com.example.android.dictionaryalmighty2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class UserInputHistory extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_input_history);

        final ListView userInputListview;


        userInputListview = findViewById(R.id.user_input_listview);
        String getInput = MainActivity.wordInputView.getText().toString();

        ArrayAdapter userInputArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, MainActivity.userInputArraylist);
        userInputListview.setAdapter(userInputArrayAdapter);

        userInputListview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedListviewItemValue=userInputListview.getItemAtPosition(position).toString();
                MainActivity.wordInputView.setText(selectedListviewItemValue);

                finish(); //結束此Activity並返回上一個Activity

            }
        });


    }


}
