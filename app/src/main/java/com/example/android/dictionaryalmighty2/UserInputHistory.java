package com.example.android.dictionaryalmighty2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class UserInputHistory extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_input_history);

        final ListView userInputListview;
        final ArrayAdapter userInputArrayAdapter;


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


}
