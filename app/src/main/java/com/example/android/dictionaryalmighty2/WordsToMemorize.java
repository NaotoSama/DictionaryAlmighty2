package com.example.android.dictionaryalmighty2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;

import static com.example.android.dictionaryalmighty2.MainActivity.browserSwitch;
import static com.example.android.dictionaryalmighty2.MainActivity.comboSearchButton;
import static com.example.android.dictionaryalmighty2.MainActivity.defaultDictionaryListOriginal;
import static com.example.android.dictionaryalmighty2.MainActivity.defaultDictionaryListSimplified;
import static com.example.android.dictionaryalmighty2.MainActivity.defaultSearchButton;
import static com.example.android.dictionaryalmighty2.MainActivity.floatingActionButton;
import static com.example.android.dictionaryalmighty2.MainActivity.mChildReferenceForBoundDictionaryUrl;
import static com.example.android.dictionaryalmighty2.MainActivity.mChildReferenceForVocabularyList;
import static com.example.android.dictionaryalmighty2.MainActivity.myVocabularyArrayList;
import static com.example.android.dictionaryalmighty2.MainActivity.proOrSimplifiedLayoutSwitch;
import static com.example.android.dictionaryalmighty2.MainActivity.searchKeyword;
import static com.example.android.dictionaryalmighty2.MainActivity.searchResultWillBeDisplayedHere;
import static com.example.android.dictionaryalmighty2.MainActivity.username;
import static com.example.android.dictionaryalmighty2.MainActivity.webViewBrowser;
import static com.example.android.dictionaryalmighty2.MainActivity.wordInputView;
import static com.example.android.dictionaryalmighty2.UserInputHistory.presetNotificationTimingsList;

public class WordsToMemorize extends AppCompatActivity {

    RelativeLayout.LayoutParams layoutparams;   //用來客製化修改ActionBar
    TextView customActionBarTextviewforUserInputHistoryPage;
    androidx.appcompat.app.ActionBar actionBar;

    TextView wordsToMemorizeTextView;
    ImageView clickHereFingerWordsToMemorizeImageView;

    Button clearMyVocabularyList;
                                                                                                    //Button aboutMemorizingWordsButton;
    String selectedMyVocabularyListviewItemValue;
    String boundDictionaryUrl;
    String wordToDelete;
    String dictionaryTitle;

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
                        Collections.sort(myVocabularyArrayList, String.CASE_INSENSITIVE_ORDER);

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

                setChooseQuickSearchOrComboSearchAlertDialog();

                                                                                                    //selectedMyVocabularyListviewItemValue=myVocabularyListview.getItemAtPosition(position).toString();
                                                                                                    //wordInputView.setText(selectedMyVocabularyListviewItemValue);
                                                                                                    //finish(); //結束此Activity並返回上一個Activity

            }
        });



        /**
         * Let the user long click on an item
         */
        myVocabularyListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                //這邊設置第一層AlertDialog讓用戶綁定字典或使用綁定的字典來查單字
                CFAlertDialog.Builder bindDictionaryAlertDialogBuilder = new CFAlertDialog.Builder(WordsToMemorize.this)
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                        .setDialogBackgroundColor(Color.parseColor("#fafcd7"))
                        .setCornerRadius(50)
                        .setTitle(getResources().getString(R.string.Bind_dictionary_explanation) + System.getProperty("line.separator") + System.getProperty("line.separator") + getResources().getString(R.string.Do_you_want_to))
                        .setTextColor(Color.BLUE)
                        .setCancelable(false) //按到旁邊的空白處AlertDialog也不會消失
                        .setHeaderView(R.layout.custom_alert_diaglog_question_mark)

                        //AlertDialog的確定鈕，使用綁定的字典查字
                        .addButton(getResources().getString(R.string.Use_bound_dictionary_to_search)
                                , Color.BLACK, Color.GREEN, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (bindDictionaryAlertDialog, whichLayer1) -> {

                                    searchKeyword = myVocabularyListview.getItemAtPosition(position).toString();

                                    //檢查資料庫中是否有重複的字
                                    mChildReferenceForBoundDictionaryUrl.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.hasChild(searchKeyword)) {
                                                retrieveBoundDictionary();
                                            }
                                            else {
                                                Toast.makeText(getApplicationContext(),getString(R.string.Please_bind_a_dictionary_first),Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                                    //讓第一層AlertDialog消失
                                    bindDictionaryAlertDialog.dismiss();
                                })

                        .addButton(getResources().getString(R.string.Bind_this_word_with_a_dictionary)
                                , Color.BLACK, Color.YELLOW, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (bindDictionaryAlertDialog, whichLayer1) -> {

                                    if(proOrSimplifiedLayoutSwitch.isChecked()) {

                                        //載入專業版字典名單
                                        defaultDictionaryListOriginal = getResources().getStringArray(R.array.default_dictionary_list_original);

                                        //這邊設置第二層AlertDialog讓用戶綁定字典或使用綁定的字典來查單字
                                        CFAlertDialog.Builder chooseOneDictionaryToBindDialogBuilder = new CFAlertDialog.Builder(WordsToMemorize.this)
                                                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                                .setDialogBackgroundColor(Color.parseColor("#fafcd7"))
                                                .setCornerRadius(50)
                                                .setTitle(getResources().getString(R.string.Choose_a_dictionary))
                                                .setTextColor(Color.BLUE)
                                                .setCancelable(false) //按到旁邊的空白處AlertDialog也不會消失

                                                .setSingleChoiceItems(defaultDictionaryListOriginal, -1, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface chooseOneDictionaryToBindDialog, int dictionaryListPosition) {

                                                        searchKeyword = myVocabularyListview.getItemAtPosition(position).toString();

                                                        switch (dictionaryListPosition) {
                                                            case 0:
                                                                boundDictionaryUrl= "https://tw.dictionary.search.yahoo.com/search;_ylt=AwrtXGoL8vtcAQoAnHV9rolQ;_ylc=X1MDMTM1MTIwMDM4MQRfcgMyBGZyA3NmcARncHJpZAN0RjJnMS51MlNWU3NDZ1pfVC4zNUFBBG5fcnNsdAMwBG5fc3VnZwM0BG9yaWdpbgN0dy5kaWN0aW9uYXJ5LnNlYXJjaC55YWhvby5jb20EcG9zAzAEcHFzdHIDBHBxc3RybAMEcXN0cmwDMwRxdWVyeQNHQVkEdF9zdG1wAzE1NjAwMTU0MTE-?p="+searchKeyword+"&fr2=sb-top-tw.dictionary.search&fr=sfp";
                                                                dictionaryTitle = getString(R.string.Yahoo_Dictionary);
                                                                break;
                                                            case 1:
                                                                boundDictionaryUrl= "http://terms.naer.edu.tw/search/?q="+searchKeyword+"&field=ti&op=AND&group=&num=10";
                                                                dictionaryTitle = getString(R.string.National_Academy_for_Educational_Research);
                                                                break;
                                                            case 2:
                                                                boundDictionaryUrl= "http://dict.site/"+searchKeyword+".html";
                                                                dictionaryTitle = getString(R.string.Dict_site);
                                                                break;
                                                            case 3:
                                                                boundDictionaryUrl= "http://www.fastdict.net/hongkong/word.html?word="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Fast_dict);
                                                                break;
                                                            case 4:
                                                                boundDictionaryUrl= "http://dict.cn/big5/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Dict_dot_cn);
                                                                break;
                                                            case 5:
                                                                boundDictionaryUrl= "http://gdictchinese.freecollocation.com/search/?q="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Google_dictionary);
                                                                break;
                                                            case 6:
                                                                boundDictionaryUrl= "https://tw.voicetube.com/definition/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.VoiceTube);
                                                                break;
                                                            case 7:
                                                                boundDictionaryUrl= "https://dictionary.cambridge.org/zht/詞典/英語-漢語-繁體/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Cambridge_EN_CH);
                                                                break;
                                                            case 8:
                                                                boundDictionaryUrl= "https://www.wordreference.com/enzh/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Word_reference_en_to_ch);
                                                                break;
                                                            case 9:
                                                                boundDictionaryUrl= "https://www.wordreference.com/zhen/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Word_reference_ch_to_en);
                                                                break;
                                                            case 10:
                                                                boundDictionaryUrl= "https://www.merriam-webster.com/dictionary/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Merriam_Webster);
                                                                break;
                                                            case 11:
                                                                boundDictionaryUrl= "https://www.macmillandictionary.com/dictionary/british/"+searchKeyword+"_1";
                                                                dictionaryTitle = getString(R.string.Macmillan_dictionary);
                                                                break;
                                                            case 12:
                                                                boundDictionaryUrl= "https://www.collinsdictionary.com/dictionary/english/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Collins);
                                                                break;
                                                            case 13:
                                                                boundDictionaryUrl= "https://en.oxforddictionaries.com/definition/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Oxford);
                                                                break;
                                                            case 14:
                                                                boundDictionaryUrl= "https://www.vocabulary.com/dictionary/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Vocabulary);
                                                                break;
                                                            case 15:
                                                                boundDictionaryUrl= "https://www.dictionary.com/browse/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Dictionary);
                                                                break;
                                                            case 16:
                                                                boundDictionaryUrl= "https://www.thefreedictionary.com/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.The_Free_Dictionary);
                                                                break;
                                                            case 17:
                                                                boundDictionaryUrl= "http://www.finedictionary.com/"+searchKeyword+".html";
                                                                dictionaryTitle = getString(R.string.Fine_dictionary);
                                                                break;
                                                            case 18:
                                                                boundDictionaryUrl= "https://www.yourdictionary.com/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Your_Dictionary);
                                                                break;
                                                            case 19:
                                                                boundDictionaryUrl= "https://www.ldoceonline.com/dictionary/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Longman_Dictionary);
                                                                break;
                                                            case 20:
                                                                boundDictionaryUrl= "https://www.wordwebonline.com/search.pl?w="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.WordWeb_dictionary);
                                                                break;
                                                            case 21:
                                                                boundDictionaryUrl= "https://www.wordnik.com/words/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Wordnik);
                                                                break;
                                                            case 22:
                                                                boundDictionaryUrl= "https://en.wiktionary.org/wiki/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Wiki_Dictionary);
                                                                break;
                                                            case 23:
                                                                boundDictionaryUrl= "http://www.businessdictionary.com/definition/"+searchKeyword+".html";
                                                                dictionaryTitle = getString(R.string.Business_Dictionary);
                                                                break;
                                                            case 24:
                                                                boundDictionaryUrl= "http://www.yiym.com/?s="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Slang);
                                                                break;
                                                            case 25:
                                                                boundDictionaryUrl= "http://onlineslangdictionary.com/search/?q="+searchKeyword+"&sa=Search";
                                                                dictionaryTitle = getString(R.string.The_online_slang_dictionary);
                                                                break;
                                                            case 26:
                                                                boundDictionaryUrl= "http://www.idioms4you.com/tipsearch/search.html?q="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Idioms_4_you);
                                                                break;
                                                            case 27:
                                                                boundDictionaryUrl= "https://greensdictofslang.com/search/basic?q="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Greens_dictionary_of_slang);
                                                                break;
                                                            case 28:
                                                                boundDictionaryUrl= "https://www.etymonline.com/search?q="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Etymonline);
                                                                break;
                                                            case 29:
                                                                boundDictionaryUrl= "https://www.techdico.com/translation/english-chinese/"+searchKeyword+".html";
                                                                dictionaryTitle = getString(R.string.TechDico);
                                                                break;
                                                            case 30:
                                                                boundDictionaryUrl= "http://dict.bioon.com/search.asp?txtitle="+searchKeyword+"&searchButton=查词典&matchtype=0";
                                                                dictionaryTitle = getString(R.string.BioMedical_dictionary);
                                                                break;
                                                            case 31:
                                                                boundDictionaryUrl= "https://www.isplural.com/plural_singular/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Is_plural_dictionary);
                                                                break;
                                                            case 32:
                                                                boundDictionaryUrl= "https://lingohelp.me/q/?w="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Lingo_help_prepositions);
                                                                break;
                                                            case 33:
                                                                boundDictionaryUrl= "https://www.powerthesaurus.org/"+searchKeyword+"/synonyms";
                                                                dictionaryTitle = getString(R.string.Power_thesaurus_synonym);
                                                                break;
                                                            case 34:
                                                                boundDictionaryUrl= "https://www.powerthesaurus.org/"+searchKeyword+"/antonyms";
                                                                dictionaryTitle = getString(R.string.Power_thesaurus_antonym);
                                                                break;
                                                            case 35:
                                                                boundDictionaryUrl= "https://www.wordhippo.com/what-is/another-word-for/"+searchKeyword+".html";
                                                                dictionaryTitle = getString(R.string.Word_Hippo);
                                                                break;
                                                            case 36:
                                                                boundDictionaryUrl= "https://www.onelook.com/thesaurus/?s="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Onelook);
                                                                break;
                                                            case 37:
                                                                boundDictionaryUrl= "https://www.weblio.jp/content/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Weblio_JP);
                                                                break;
                                                            case 38:
                                                                boundDictionaryUrl= "https://cjjc.weblio.jp/content/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Weblio_CN);
                                                                break;
                                                            case 39:
                                                                boundDictionaryUrl= "https://ejje.weblio.jp/content/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Weblio_EN);
                                                                break;
                                                            case 40:
                                                                boundDictionaryUrl= "https://thesaurus.weblio.jp/content/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Weblio_Synonym);
                                                                break;
                                                            case 41:
                                                                boundDictionaryUrl= "https://tangorin.com/words?search="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Tangorin_Word);
                                                                break;
                                                            case 42:
                                                                boundDictionaryUrl= "https://tangorin.com/kanji?search="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Tangorin_Kanji);
                                                                break;
                                                            case 43:
                                                                boundDictionaryUrl= "https://tangorin.com/names?search="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Tangorin_Names);
                                                                break;
                                                            case 44:
                                                                boundDictionaryUrl= "https://tangorin.com/sentences?search="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Tangorin_Sentence);
                                                                break;
                                                            case 45:
                                                                boundDictionaryUrl= "http://dict.asia/jc/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.DA_JP_TW_Dictionary);
                                                                break;
                                                            case 46:
                                                                boundDictionaryUrl= "http://dict.asia/cj/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.DA_TW_JP_Dictionary);
                                                                break;
                                                            case 47:
                                                                boundDictionaryUrl= "https://dictionary.goo.ne.jp/srch/jn/"+searchKeyword+"/m0u/";
                                                                dictionaryTitle = getString(R.string.Goo);
                                                                break;
                                                            case 48:
                                                                boundDictionaryUrl= "http://www.sanseido.biz/sp/Search?target_words="+searchKeyword+"&search_type=0&start_index=0&selected_dic=";
                                                                dictionaryTitle = getString(R.string.Sanseido);
                                                                break;
                                                            case 49:
                                                                boundDictionaryUrl= "https://kotobank.jp/word/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Sanseido);
                                                                break;
                                                            case 50:
                                                                boundDictionaryUrl= "http://s.jlogos.com/list.html?keyword="+searchKeyword+"&opt_val=0";
                                                                dictionaryTitle = getString(R.string.J_Logos);
                                                                break;
                                                            case 51:
                                                                boundDictionaryUrl= "https://eow.alc.co.jp/sp/search.html?q="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Eijirou);
                                                                break;
                                                            case 52:
                                                                boundDictionaryUrl= "https://eikaiwa.dmm.com/uknow/search/?keyword="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.How_do_you_say_this_in_English);
                                                                break;
                                                            case 53:
                                                                boundDictionaryUrl= "https://jisho.org/search/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Jisho);
                                                                break;
                                                            case 54:
                                                                boundDictionaryUrl= "https://dictionary.cambridge.org/zht/詞典/japanese-english/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Cambridge_JP_EN);
                                                                break;
                                                            case 55:
                                                                boundDictionaryUrl= "https://dictionary.cambridge.org/zht/詞典/英語-日語/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Cambridge_EN_JP);
                                                                break;
                                                            case 56:
                                                                boundDictionaryUrl= "http://www.edrdg.org/cgi-bin/wwwjdic/wwwjdic?1MUJ"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.WWWJDIC_jp_en);
                                                                break;
                                                            case 57:
                                                                boundDictionaryUrl= "http://www.edrdg.org/cgi-bin/wwwjdic/wwwjdic?1MDE"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.WWWJDIC_en_jp);
                                                                break;
                                                            case 58:
                                                                boundDictionaryUrl= "https://www.wordreference.com/enja/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Word_reference_en_to_jp);
                                                                break;
                                                            case 59:
                                                                boundDictionaryUrl= "https://www.wordreference.com/jaen/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Word_reference_jp_to_en);
                                                                break;
                                                            case 60:
                                                                boundDictionaryUrl= "http://m.romajidesu.com/dictionary/meaning-of-"+searchKeyword+".html";
                                                                dictionaryTitle = getString(R.string.Romaji_desu_enjp_jpen_dictionary);
                                                                break;
                                                            case 61:
                                                                boundDictionaryUrl= "http://m.romajidesu.com/kanji/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Romaji_desu_kanji_dictionary);
                                                                break;
                                                            case 62:
                                                                boundDictionaryUrl= "https://www.japandict.com/?s="+searchKeyword+"&lang=eng";
                                                                dictionaryTitle = getString(R.string.Japan_dict);
                                                                break;
                                                            case 63:
                                                                boundDictionaryUrl= "https://www.japandict.com/kanji/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Japan_dict_kanji_dictionary);
                                                                break;
                                                            case 64:
                                                                boundDictionaryUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=zh-TW&text="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Google_translate_to_CHTW);
                                                                break;
                                                            case 65:
                                                                boundDictionaryUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=zh-CN&text="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Google_translate_to_CHCN);
                                                                break;
                                                            case 66:
                                                                boundDictionaryUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=en&text="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Google_translate_to_EN);
                                                                break;
                                                            case 67:
                                                                boundDictionaryUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=ja&text="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Google_translate_to_JP);
                                                                break;
                                                            case 68:
                                                                boundDictionaryUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=ko&text="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Google_translate_to_KR);
                                                                break;
                                                            case 69:
                                                                boundDictionaryUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=es&text="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Google_translate_to_SP);
                                                                break;
                                                            case 70:
                                                                boundDictionaryUrl= "http://images.google.com/search?tbm=isch&q="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Google_Image);
                                                                break;
                                                            case 71:
                                                                boundDictionaryUrl= "https://ludwig.guru/s/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Ludwig);
                                                                break;
                                                            case 72:
                                                                boundDictionaryUrl= "https://sentence.yourdictionary.com/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Your_Dictionary_Example_Sentences);
                                                                break;
                                                            case 73:
                                                                boundDictionaryUrl= "https://youglish.com/search/"+searchKeyword+"/all?";
                                                                dictionaryTitle = getString(R.string.YouGlish);
                                                                break;
                                                            case 74:
                                                                boundDictionaryUrl= "http://www.jukuu.com/search.php?q="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Word_Cool_EN_CH);
                                                                break;
                                                            case 75:
                                                                boundDictionaryUrl= "http://www.jukuu.com/jsearch.php?q="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Word_Cool_EN_JP);
                                                                break;
                                                            case 76:
                                                                boundDictionaryUrl= "http://www.jukuu.com/jcsearch.php?q="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Word_Cool_JP_CH);
                                                                break;
                                                            case 77:
                                                                boundDictionaryUrl= "https://cn.linguee.com/中文-英语/search?source=auto&query="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Linguee_CH_EN);
                                                                break;
                                                            case 78:
                                                                boundDictionaryUrl= "https://www.linguee.jp/日本語-英語/search?source=auto&query="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Linguee_JP_EN);
                                                                break;
                                                            case 79:
                                                                boundDictionaryUrl= "https://zh.wikipedia.org/wiki/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Wikipedia_TW);
                                                                break;
                                                            case 80:
                                                                boundDictionaryUrl= "https://en.wikipedia.org/wiki/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Wikipedia_EN);
                                                                break;
                                                            case 81:
                                                                boundDictionaryUrl= "https://www.encyclo.co.uk/meaning-of-"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.English_Encyclopedia);
                                                                break;
                                                            case 82:
                                                                boundDictionaryUrl= "https://forvo.com/search/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Forvo);
                                                                break;
                                                            case 83:
                                                                boundDictionaryUrl= "http://www.differencebetween.net/search/?cx=partner-pub-1911891147296207%3Aw80z4hjpu14&cof=FORID%3A9&ie=ISO-8859-1&q="+searchKeyword+"&sa=Search";
                                                                dictionaryTitle = getString(R.string.Difference_between_dot_net);
                                                                break;
                                                            case 84:
                                                                boundDictionaryUrl= "https://netspeak.org/#q="+searchKeyword+"&corpus=web-en";
                                                                dictionaryTitle = getString(R.string.Net_Speak);
                                                                break;
                                                            case 85:
                                                                boundDictionaryUrl= "http://www.just-the-word.com/main.pl?word="+searchKeyword+"+&mode=combinations";
                                                                dictionaryTitle = getString(R.string.Just_the_word);
                                                                break;
                                                            case 86:
                                                                boundDictionaryUrl= "https://yomikatawa.com/kanji/"+searchKeyword+"?search=1";
                                                                dictionaryTitle = getString(R.string.Yomikata);
                                                                break;
                                                            case 87:
                                                                boundDictionaryUrl= "https://cse.google.co.jp/cse?cx=partner-pub-1137871985589263%3A3025760782&ie=UTF-8&q="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Chigai);
                                                                break;
                                                            case 88:
                                                                boundDictionaryUrl= "http://www.gavo.t.u-tokyo.ac.jp/ojad/search/index/sortprefix:accent/narabi1:kata_asc/narabi2:accent_asc/narabi3:mola_asc/yure:visible/curve:invisible/details:invisible/limit:20/word:"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.OJAD);
                                                                break;
                                                        }


                                                    }
                                                })

                                                //第二層AlertDialog的確定鈕
                                                .addButton(getString(R.string.Confirm)
                                                        , Color.BLACK, Color.GREEN, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (chooseOneDictionaryToBindDialog, whichLayer2) -> {

                                                            //檢查資料庫中是否已經有綁定過字典
                                                            mChildReferenceForBoundDictionaryUrl.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if (snapshot.hasChild(searchKeyword)) {
                                                                        snapshot.getRef().child(searchKeyword).setValue("");

                                                                        //延遲5秒重啟App
                                                                        Runnable r = new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                mChildReferenceForBoundDictionaryUrl.child(username).child(searchKeyword).push().setValue(boundDictionaryUrl); //加入字典的查詢網址到資料庫
                                                                            }
                                                                        };
                                                                        Handler h =new Handler();
                                                                        h.postDelayed(r, 1000);

                                                                        Toast.makeText(WordsToMemorize.this,getString(R.string.Word_now_bound_with)+dictionaryTitle,Toast.LENGTH_LONG).show();

                                                                        chooseOneDictionaryToBindDialog.dismiss();
                                                                    }

                                                                    else {
                                                                        mChildReferenceForBoundDictionaryUrl.child(username).child(searchKeyword).push().setValue(boundDictionaryUrl); //加入字典的查詢網址到資料庫
                                                                        Toast.makeText(WordsToMemorize.this,getString(R.string.Word_now_bound_with)+dictionaryTitle,Toast.LENGTH_LONG).show();
                                                                        chooseOneDictionaryToBindDialog.dismiss();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                }
                                                            });

                                                })

                                                //第二層AlertDialog的取消鈕
                                                .addButton(getString(R.string.Cancel)
                                                        , Color.BLACK, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (chooseOneDictionaryToBindDialog, whichLayer2) -> {

                                                            chooseOneDictionaryToBindDialog.dismiss();
                                                });

                                                chooseOneDictionaryToBindDialogBuilder.show(); //顯示第二層AlertDialog
                                                bindDictionaryAlertDialog.dismiss();  //讓第一層AlertDialog消失

                                    } else {
                                        //載入簡易版字典名單
                                        defaultDictionaryListSimplified = getResources().getStringArray(R.array.default_dictionary_list_simplified);

                                        //這邊設置第二層AlertDialog讓用戶綁定字典或使用綁定的字典來查單字
                                        CFAlertDialog.Builder chooseOneDictionaryToBindDialogBuilder = new CFAlertDialog.Builder(WordsToMemorize.this)
                                                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                                .setDialogBackgroundColor(Color.parseColor("#fafcd7"))
                                                .setCornerRadius(50)
                                                .setTitle(getResources().getString(R.string.Choose_a_dictionary))
                                                .setTextColor(Color.BLUE)
                                                .setCancelable(false) //按到旁邊的空白處AlertDialog也不會消失

                                                .setSingleChoiceItems(defaultDictionaryListSimplified, -1, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface chooseOneDictionaryToBindDialog, int dictionaryListPosition) {

                                                        searchKeyword = myVocabularyListview.getItemAtPosition(position).toString();

                                                        switch (dictionaryListPosition) {
                                                            case 0:
                                                                boundDictionaryUrl= "https://tw.dictionary.search.yahoo.com/search;_ylt=AwrtXGoL8vtcAQoAnHV9rolQ;_ylc=X1MDMTM1MTIwMDM4MQRfcgMyBGZyA3NmcARncHJpZAN0RjJnMS51MlNWU3NDZ1pfVC4zNUFBBG5fcnNsdAMwBG5fc3VnZwM0BG9yaWdpbgN0dy5kaWN0aW9uYXJ5LnNlYXJjaC55YWhvby5jb20EcG9zAzAEcHFzdHIDBHBxc3RybAMEcXN0cmwDMwRxdWVyeQNHQVkEdF9zdG1wAzE1NjAwMTU0MTE-?p="+searchKeyword+"&fr2=sb-top-tw.dictionary.search&fr=sfp";
                                                                dictionaryTitle = getString(R.string.Yahoo_Dictionary);
                                                                break;
                                                            case 1:
                                                                boundDictionaryUrl= "http://terms.naer.edu.tw/search/?q="+searchKeyword+"&field=ti&op=AND&group=&num=10";
                                                                dictionaryTitle = getString(R.string.National_Academy_for_Educational_Research);
                                                                break;
                                                            case 2:
                                                                boundDictionaryUrl= "http://dict.site/"+searchKeyword+".html";
                                                                dictionaryTitle = getString(R.string.Dict_site);
                                                                break;
                                                            case 3:
                                                                boundDictionaryUrl= "http://www.fastdict.net/hongkong/word.html?word="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Fast_dict);
                                                                break;
                                                            case 4:
                                                                boundDictionaryUrl= "http://gdictchinese.freecollocation.com/search/?q="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Google_dictionary);
                                                                break;
                                                            case 5:
                                                                boundDictionaryUrl= "https://tw.voicetube.com/definition/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.VoiceTube);
                                                                break;
                                                            case 6:
                                                                boundDictionaryUrl= "https://dictionary.cambridge.org/zht/詞典/英語-漢語-繁體/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Cambridge_EN_CH);
                                                                break;
                                                            case 7:
                                                                boundDictionaryUrl= "https://www.weblio.jp/content/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Weblio_JP);
                                                                break;
                                                            case 8:
                                                                boundDictionaryUrl= "https://cjjc.weblio.jp/content/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Weblio_CN);
                                                                break;
                                                            case 9:
                                                                boundDictionaryUrl= "http://dict.asia/jc/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.DA_JP_TW_Dictionary);
                                                                break;
                                                            case 10:
                                                                boundDictionaryUrl= "http://dict.asia/cj/"+searchKeyword;
                                                                dictionaryTitle = getString(R.string.DA_TW_JP_Dictionary);
                                                                break;
                                                            case 11:
                                                                boundDictionaryUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=zh-TW&text="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Google_translate_to_CHTW);
                                                                break;
                                                            case 12:
                                                                boundDictionaryUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=zh-CN&text="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Google_translate_to_CHCN);
                                                                break;
                                                            case 13:
                                                                boundDictionaryUrl= "http://images.google.com/search?tbm=isch&q="+searchKeyword;
                                                                dictionaryTitle = getString(R.string.Google_Image);
                                                                break;
                                                        }


                                                    }
                                                })

                                                //第二層AlertDialog的確定鈕
                                                .addButton(getString(R.string.Confirm)
                                                        , Color.BLACK, Color.GREEN, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (chooseOneDictionaryToBindDialog, whichLayer2) -> {

                                                            //檢查資料庫中是否已經有綁定過字典
                                                            mChildReferenceForBoundDictionaryUrl.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if (snapshot.hasChild(searchKeyword)) {
                                                                        snapshot.getRef().child(searchKeyword).setValue("");

                                                                        //延遲5秒重啟App
                                                                        Runnable r = new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                mChildReferenceForBoundDictionaryUrl.child(username).child(searchKeyword).push().setValue(boundDictionaryUrl); //加入字典的查詢網址到資料庫
                                                                            }
                                                                        };
                                                                        Handler h =new Handler();
                                                                        h.postDelayed(r, 1000);

                                                                        Toast.makeText(WordsToMemorize.this,getString(R.string.Word_now_bound_with)+dictionaryTitle,Toast.LENGTH_LONG).show();

                                                                        chooseOneDictionaryToBindDialog.dismiss();
                                                                    }

                                                                    else {
                                                                        mChildReferenceForBoundDictionaryUrl.child(username).child(searchKeyword).push().setValue(boundDictionaryUrl); //加入字典的查詢網址到資料庫
                                                                        Toast.makeText(WordsToMemorize.this,getString(R.string.Word_now_bound_with)+dictionaryTitle,Toast.LENGTH_LONG).show();
                                                                        chooseOneDictionaryToBindDialog.dismiss();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                }
                                                            });

                                                })

                                                //第二層AlertDialog的取消鈕
                                                .addButton(getString(R.string.Cancel)
                                                        , Color.BLACK, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (chooseOneDictionaryToBindDialog, whichLayer2) -> {

                                                            chooseOneDictionaryToBindDialog.dismiss();
                                                });

                                        chooseOneDictionaryToBindDialogBuilder.show(); //顯示第二層AlertDialog
                                        bindDictionaryAlertDialog.dismiss();  //讓第一層AlertDialog消失

                                    }

                                })

                        //第一層AlertDialog的取消鈕
                        .addButton(getResources().getString(R.string.Cancel)
                                , Color.BLACK, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (bindDictionaryAlertDialog, whichLayer1) -> {

                                    bindDictionaryAlertDialog.dismiss();
                                });

                //把第一層AlertDialog顯示出來
                bindDictionaryAlertDialogBuilder.create().show();

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

                                    wordToDelete = listView.getItemAtPosition(position).toString();

                                    if (username!=null && !username.equals("")) {
                                        myVocabularyArrayList.remove(wordToDelete);  //同時從本地的list移除該字
                                        myVocabularyArrayAdapter.notifyDataSetChanged();
                                        Query query = mChildReferenceForVocabularyList.child(username).orderByValue().equalTo(wordToDelete); //在資料庫中尋找要刪除的字

                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                                    snapshot.getRef().removeValue(); //刪除該字

                                                    removeBoundDictionary();

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
    // 獲取綁定的字典的Helper Method
    //==============================================================================================
        public void retrieveBoundDictionary() {
        //去資料庫中調出綁定的字典
        mChildReferenceForBoundDictionaryUrl.child(username).child(searchKeyword).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    //Get the data from snapshot
                    String childValues = postSnapshot.getValue(String.class);

                    //Add the data to the arraylist
                    ArrayList<String> tempArraylist = new ArrayList<>();
                    tempArraylist.add(childValues);

                    wordInputView.setText(searchKeyword);
                    webViewBrowser.setVisibility(View.VISIBLE);
                    floatingActionButton.setVisibility(View.VISIBLE);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    browserSwitch.setChecked(true);
                    webViewBrowser.loadUrl(tempArraylist.get(0));
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }



    //==============================================================================================
    // 移除綁定的字典的Helper Method
    //==============================================================================================
    public void removeBoundDictionary() {
        //檢查資料庫中是否有重複的字
        mChildReferenceForBoundDictionaryUrl.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(wordToDelete)) {
                    snapshot.getRef().child(wordToDelete).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
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
        .setMessage(getString(R.string.Search_this_word_explanation) + System.getProperty("line.separator")
                + getString(R.string.Quick_search) +"/"+ getString(R.string.Combo_search) +"/"+ getString(R.string.Google_translate) + "：" + getString(R.string.Appwidget_text) + System.getProperty("line.separator")
                + getString(R.string.Memorize_this_word_explanation))
        .setCancelable(false) //按到旁邊的空白處AlertDialog不會消失

        //第一層AlertDialog的確定鈕，帶入單字到首頁
        .addButton(getString(R.string.Send_to_WordInputView)
                , Color.BLACK, Color.GREEN, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (chooseQuickSearchOrComboSearchAlertDialog, which) -> {

                    wordInputView.setText(selectedMyVocabularyListviewItemValue);
                    chooseQuickSearchOrComboSearchAlertDialog.dismiss();
                    finish();
        })

        //第一層AlertDialog的中立鈕，使用快搜模式
        .addButton(getString(R.string.Quick_search)
                , Color.BLACK, Color.MAGENTA, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (chooseQuickSearchOrComboSearchAlertDialog, which) -> {

                    defaultSearchButton.performClick();
                    chooseQuickSearchOrComboSearchAlertDialog.dismiss();
                    finish();
        })

        //第一層AlertDialog的中立鈕，使用三連搜模式
        .addButton(getString(R.string.Combo_search)
                        , Color.BLACK, Color.MAGENTA, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (chooseQuickSearchOrComboSearchAlertDialog, which) -> {

                    comboSearchButton.performClick();
                    chooseQuickSearchOrComboSearchAlertDialog.dismiss();
                    finish();
        })

        //第一層AlertDialog的中立鈕，使用Google翻譯
        .addButton(getString(R.string.Google_translate)
                , Color.BLACK, Color.MAGENTA, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (chooseQuickSearchOrComboSearchAlertDialog, which) -> {

                    wordInputView.setText(selectedMyVocabularyListviewItemValue);
                    String intentAutoTranslationURL = "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=auto&tl=zh-TW&text=" + selectedMyVocabularyListviewItemValue;
                    webViewBrowser.loadUrl(intentAutoTranslationURL);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);
                    floatingActionButton.setVisibility(View.VISIBLE);
                    chooseQuickSearchOrComboSearchAlertDialog.dismiss();
                    finish();
        })

        //第一層AlertDialog的取消鈕，記憶單字
        .addButton(getString(R.string.Memorize_this_word)
                        , Color.BLACK, Color.YELLOW, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (chooseQuickSearchOrComboSearchAlertDialog, whichLayer1) -> {

                                                                                                    ////先確認用戶手機系統是否為Android 9以上，否則不給用(因為會閃退)
                                                                                                    //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                        //這邊設置第二層AlertDialog讓用戶選擇自定義或預設的通知時機
                        final CFAlertDialog.Builder chooseCustomizedOrPredefinedNotificationAlertDialogBuilder = new CFAlertDialog.Builder(WordsToMemorize.this)
                                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                .setDialogBackgroundColor(Color.parseColor("#fafcd7"))
                                .setCornerRadius(50)
                                .setTitle(getString(R.string.Choose_customized_or_predefined_notification_timings))
                                .setMessage(getString(R.string.Predefined_timing_explanation) + System.getProperty("line.separator") + getString(R.string.User_configured_timing_explanation))
                                .setTextColor(Color.BLUE)
                                .setCancelable(false) //按到旁邊的空白處AlertDialog也不會消失


                                //第二層AlertDialog的確定鈕，預設的通知時機。
                                .addButton(getString(R.string.Use_predefined_timing)
                                        , Color.BLACK, Color.GREEN, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (chooseCustomizedOrPredefinedNotificationAlertDialog, whichLayer2) -> {

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
                                                                    Toast.makeText(getApplicationContext(), R.string.Will_send_the_notifications_on_8_preset_timings, Toast.LENGTH_LONG).show();
                                                                    break;
                                                                case 1:
                                                                    setPreDefinedNotificationTimings1Day();
                                                                    setPreDefinedNotificationTimings7Days();
                                                                    setPreDefinedNotificationTimings1Month();
                                                                    choosePresetNotificationTimingsAlertDialog.dismiss();
                                                                    Toast.makeText(getApplicationContext(), R.string.Will_send_the_notifications_on_3_preset_timings, Toast.LENGTH_LONG).show();
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
                                                            , Color.BLACK, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (choosePresetNotificationTimingsAlertDialog, whichLayer3) -> {

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
                                        , Color.BLACK, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (chooseCustomizedOrPredefinedNotificationAlertDialog, whichLayer2) -> {

                                            chooseCustomizedOrPredefinedNotificationAlertDialog.dismiss();
                                        });


                        chooseCustomizedOrPredefinedNotificationAlertDialogBuilder.setHeaderView(R.layout.custom_alert_dialog_clock);
                        //把第二層的AlertDialog顯示出來
                        chooseCustomizedOrPredefinedNotificationAlertDialogBuilder.show();
                        //同時讓第一層的AlertDialog消失
                        chooseQuickSearchOrComboSearchAlertDialog.dismiss();

                                                                                                    //}
                                                                                                    //else {
                                                                                                    //Toast.makeText(getApplicationContext(),getString(R.string.Restricted_use_on_android_9_pie),Toast.LENGTH_LONG).show();
                                                                                                    //}

        })

        //第一層AlertDialog的取消鈕
                .addButton(getString(R.string.Cancel)
                        , Color.BLACK, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (chooseQuickSearchOrComboSearchAlertDialog, which) -> {

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
                .setMessage(getString(R.string.After_saving_a_word) + System.getProperty("line.separator") + getString(R.string.Finger_tap) + System.getProperty("line.separator") + getString(R.string.Long_press_words_to_memorize) + System.getProperty("line.separator") + getString(R.string.Swipe)+ System.getProperty("line.separator") + getResources().getString(R.string.Your_vocabulary_list_will_be_stored_online))
                .setCancelable(false) //按到旁邊的空白處AlertDialog不會消失


                .addButton(getString(R.string.Got_it)
                        , Color.BLACK, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (wordsToMemorizeInstructionsAlertDialog, which) -> {

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