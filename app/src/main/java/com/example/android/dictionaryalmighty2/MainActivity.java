package com.example.android.dictionaryalmighty2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import pl.droidsonroids.gif.GifImageView;


public class MainActivity extends AppCompatActivity {

    GifImageView gifImageView; //用來準備給用戶更換背景圖
    EditText wordInputView;    //關鍵字輸入框
    String searchKeyword;      //用戶輸入的關鍵字
    WebView webViewBrowser;    //網頁框
    ProgressBar progressBar;   //網頁載入的進度條
    TextView searchResultWillBeDisplayedHere;
    ImageView exitApp;         //退出程式鈕
    ImageView changeBackground;//更換背景鈕
    private static int RESULT_LOAD_IMAGE = 1;
    private static final int WRITE_PERMISSION = 0x01; //用來準備設置運行中的權限要求
    String LOG_TAG;  //Log tag for the external storage permission request error message


    @RequiresApi(api = Build.VERSION_CODES.M)  //要加上這條限定Api等級，requestWritePermission()才不會報錯
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestWritePermission();  //在程式運行中要求存取的權限

        gifImageView = findViewById(R.id.GIF_imageView);
        wordInputView = findViewById(R.id.Word_Input_View);
        searchResultWillBeDisplayedHere = findViewById(R.id.search_result_textView);
        exitApp = findViewById(R.id.Exit_app_imageView);
        changeBackground = findViewById(R.id.Change_background_imageView);


        /**
         * 設置背景圖的更換
         */
        changeBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    //點擊更換背景鈕時觸發監聽器
                //打開相簿讓用戶選圖
                Intent i = new
                        Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });


        String picturePath = DataManager.getInstance().getImageUrl();  //獲取圖片位址
        if(picturePath == null || picturePath.trim().equals("")){
            //Set some default image that will be visible before selecting image
        }else{
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);  //讀取圖片
            BitmapDrawable background = new BitmapDrawable(bitmap);
            gifImageView.setBackgroundDrawable(background);         //把用戶選擇的圖片設置為新的背景
        }



        /**
         * 設置程式的退出鈕
         */
        exitApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {        //點擊退出程式鈕時觸發監聽器
                //Finish method is used to close all open activities.
                finish();                            //退出畫面 (關閉程式)
            }
        });



        /**
         * 設置網頁框
         */
        webViewBrowser = null;  //預設網頁框為空

        //Get a reference to the WebView//
        webViewBrowser = findViewById(R.id.webview_browser);
        progressBar = findViewById(R.id.progressBar);

        webViewBrowser.setVisibility(View.INVISIBLE);  //預設程式開啟時隱藏網頁框 (才能看到背景圖)
        progressBar.setVisibility(View.GONE);  ////預設程式開啟時隱藏進度條

        WebSettings webSettings = webViewBrowser.getSettings(); //WebSettings 是用來設定 WebView 屬性的類別
        webSettings.setJavaScriptEnabled(true); //針對 WebSettings 去做設定，WebView 預設下是限制 JavaScript 的，若要啟用需要做此設定
        webSettings.setSupportZoom(true); //內部網頁支援縮放
        webSettings.setBuiltInZoomControls(true); //顯示縮放控制項
        webViewBrowser.setWebViewClient(new WebViewClientImpl());
        webViewBrowser.requestFocus();
        //Webview裡面的網頁，如果有input需要輸入，但是點上去卻沒反應，輸入法不出來。這種情況是因為webview沒有獲取焦點。
        //需要在java裡面給webview設置一下requestFocus() 就行了。



        /**
         * 設置下拉式選單
         */

        /**
         * EnDictionarySpinner & Spinner Adapters
         */
        final Spinner EnDictionarySpinner = findViewById(R.id.EN_dictionary_providers_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> EnDictionarySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.EN_dictionary_providers_spinner_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        EnDictionarySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        EnDictionarySpinner.setAdapter(EnDictionarySpinnerAdapter);

        EnDictionarySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                searchKeyword = wordInputView.getText().toString(); //EditText的getText()方法只能在監聽事件(例如onItemSelected或onCLick)中才能夠實現，若放在外面就會獲取不到EditText中你輸入的值。以下同。

                if (position == 0){
                    return;

                }else if (position == 1){
                    String url1= "https://tw.dictionary.search.yahoo.com/search;_ylt=AwrtXGoL8vtcAQoAnHV9rolQ;_ylc=X1MDMTM1MTIwMDM4MQRfcgMyBGZyA3NmcARncHJpZAN0RjJnMS51MlNWU3NDZ1pfVC4zNUFBBG5fcnNsdAMwBG5fc3VnZwM0BG9yaWdpbgN0dy5kaWN0aW9uYXJ5LnNlYXJjaC55YWhvby5jb20EcG9zAzAEcHFzdHIDBHBxc3RybAMEcXN0cmwDMwRxdWVyeQNHQVkEdF9zdG1wAzE1NjAwMTU0MTE-?p="+searchKeyword+"&fr2=sb-top-tw.dictionary.search&fr=sfp";
                    webViewBrowser.loadUrl(url1);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 2) {
                    String url2= "https://dictionary.cambridge.org/zht/詞典/英語-漢語-繁體/"+searchKeyword;
                    webViewBrowser.loadUrl(url2);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 3){
                    String url3= "https://www.merriam-webster.com/dictionary/"+searchKeyword;
                    webViewBrowser.loadUrl(url3);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 4) {
                    String url4= "https://www.collinsdictionary.com/dictionary/english/"+searchKeyword;
                    webViewBrowser.loadUrl(url4);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 5) {
                    String url5= "https://en.oxforddictionaries.com/definition/"+searchKeyword;
                    webViewBrowser.loadUrl(url5);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 6) {
                    String url6= "https://www.vocabulary.com/dictionary/"+searchKeyword;
                    webViewBrowser.loadUrl(url6);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 7) {
                    String url7= "https://www.dictionary.com/browse/"+searchKeyword;
                    webViewBrowser.loadUrl(url7);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 8) {
                    String url8= "https://www.thefreedictionary.com/"+searchKeyword;
                    webViewBrowser.loadUrl(url8);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 9) {
                    String url9= "https://www.yourdictionary.com/"+searchKeyword;
                    webViewBrowser.loadUrl(url9);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 10) {
                    String url10= "https://www.ldoceonline.com/dictionary/"+searchKeyword;
                    webViewBrowser.loadUrl(url10);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 11) {
                    String url11= "http://dict.site/"+searchKeyword+".html";
                    webViewBrowser.loadUrl(url11);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 12) {
                    String url12= "https://en.wiktionary.org/wiki/"+searchKeyword;
                    webViewBrowser.loadUrl(url12);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 13) {
                    String url13= "https://www.wordhippo.com/what-is/another-word-for/"+searchKeyword+".html";
                    webViewBrowser.loadUrl(url13);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 14) {
                    String url14= "https://www.onelook.com/thesaurus/?s="+searchKeyword;
                    webViewBrowser.loadUrl(url14);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 15) {
                    String url15= "http://www.businessdictionary.com/definition/"+searchKeyword+".html";
                    webViewBrowser.loadUrl(url15);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 16) {
                    String url16= "http://www.agosto.com.tw/dictionary.aspx?search="+searchKeyword;
                    webViewBrowser.loadUrl(url16);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 17) {
                    String url17= "http://terms.naer.edu.tw/search/?q="+searchKeyword+"&field=ti&op=AND&group=&num=10";
                    webViewBrowser.loadUrl(url17);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 18) {
                    String url18= "http://www.yiym.com/?s="+searchKeyword;
                    webViewBrowser.loadUrl(url18);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 19) {
                    String url19= "https://tw.voicetube.com/definition/"+searchKeyword;
                    webViewBrowser.loadUrl(url19);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 20) {
                    String url20= "https://youglish.com/search/"+searchKeyword+"/all?";
                    webViewBrowser.loadUrl(url20);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 21) {
                    String url60= "http://www.scidict.org/index.aspx?word="+searchKeyword;
                    webViewBrowser.loadUrl(url60);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }

                EnDictionarySpinner.setAdapter(EnDictionarySpinnerAdapter);
                //再生成一次Adapter防止點按過的選項失效無法使用，以下同。

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });



        /**
         * JpDictionarySpinner & Spinner Adapters
         */
        final Spinner JpDictionarySpinner = findViewById(R.id.JP_dictionary_providers_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> JpDictionarySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.JP_dictionary_providers_spinner_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        JpDictionarySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        JpDictionarySpinner.setAdapter(JpDictionarySpinnerAdapter);

        JpDictionarySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                searchKeyword = wordInputView.getText().toString();

                if (position == 0){
                    return;

                }
                if (position == 1){
                    String url19= "https://www.weblio.jp/content/"+searchKeyword;
                    webViewBrowser.loadUrl(url19);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 2) {
                    String url20= "https://cjjc.weblio.jp/content/"+searchKeyword;
                    webViewBrowser.loadUrl(url20);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 3){
                    String url21= "https://ejje.weblio.jp/content/"+searchKeyword;
                    webViewBrowser.loadUrl(url21);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 4) {
                    String url22= "https://thesaurus.weblio.jp/content/"+searchKeyword;
                    webViewBrowser.loadUrl(url22);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 5) {
                    String url23= "https://tangorin.com/words?search="+searchKeyword;
                    webViewBrowser.loadUrl(url23);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 6) {
                    String url24= "https://tangorin.com/kanji?search="+searchKeyword;
                    webViewBrowser.loadUrl(url24);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 7) {
                    String url25= "https://tangorin.com/names?search="+searchKeyword;
                    webViewBrowser.loadUrl(url25);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 8) {
                    String url26= "https://tangorin.com/sentences?search="+searchKeyword;
                    webViewBrowser.loadUrl(url26);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 9) {
                    String url27= "https://www.sanseido.biz/User/Dic/Index.aspx?TWords="+searchKeyword+"&st=0&DORDER=151617&DailyJJ=checkbox&DailyEJ=checkbox&DailyJE=checkbox";
                    webViewBrowser.loadUrl(url27);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 10) {
                    String url28= "https://kotobank.jp/word/"+searchKeyword;
                    webViewBrowser.loadUrl(url28);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 11) {
                    String url29= "https://www.sangyo-honyaku.jp/dictionaries/index/search_info:"+searchKeyword+"_ＩＴ・機械・電気電子";
                    webViewBrowser.loadUrl(url29);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 12) {
                    String url30= "https://kanji.jitenon.jp/cat/search.php?getdata="+searchKeyword+"&search=fpart&search2=twin";
                    webViewBrowser.loadUrl(url30);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 13) {
                    String url31= "https://eow.alc.co.jp/search?q="+searchKeyword;
                    webViewBrowser.loadUrl(url31);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 14) {
                    String url32= "https://eikaiwa.dmm.com/uknow/search/?keyword="+searchKeyword;
                    webViewBrowser.loadUrl(url32);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 15) {
                    String url33= "https://dictionary.goo.ne.jp/srch/jn/"+searchKeyword+"/m0u/";
                    webViewBrowser.loadUrl(url33);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 16) {
                    String url34= "https://jisho.org/search/"+searchKeyword;
                    webViewBrowser.loadUrl(url34);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 17) {
                    String url59= "http://s.jlogos.com/list.html?keyword="+searchKeyword+"&opt_val=0";
                    webViewBrowser.loadUrl(url59);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 18) {
                    String url61= "http://dict.asia/jc/"+searchKeyword;
                    webViewBrowser.loadUrl(url61);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 19) {
                    String url62= "http://dict.asia/cj/"+searchKeyword;
                    webViewBrowser.loadUrl(url62);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }

                JpDictionarySpinner.setAdapter(JpDictionarySpinnerAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });



        /**
         * GoogleWordSearchSpinner & Spinner Adapters
         */
        final Spinner GoogleWordSearchSpinner = findViewById(R.id.Google_word_searcher_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> GoogleWordSearchSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.Google_word_searcher_spinner_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        GoogleWordSearchSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        GoogleWordSearchSpinner.setAdapter(GoogleWordSearchSpinnerAdapter);

        GoogleWordSearchSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                searchKeyword = wordInputView.getText().toString();

                if (position == 0){
                    return;

                }
                if (position == 1){
                    String url35= "http://www.google.com/search?q="+searchKeyword+"+中文";
                    webViewBrowser.loadUrl(url35);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 2) {
                    String url36= "http://www.google.com/search?q="+searchKeyword+"+英文";
                    webViewBrowser.loadUrl(url36);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 3){
                    String url37= "http://www.google.com/search?q="+searchKeyword+"+英語";
                    webViewBrowser.loadUrl(url37);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 4) {
                    String url38= "http://www.google.com/search?q="+searchKeyword+"+翻譯";
                    webViewBrowser.loadUrl(url38);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 5) {
                    String url39= "http://www.google.com/search?q="+searchKeyword+"+日文";
                    webViewBrowser.loadUrl(url39);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);
                }else if (position == 6) {
                    String url40= "http://www.google.com/search?q="+searchKeyword+"+日語";
                    webViewBrowser.loadUrl(url40);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 7) {
                    String url41= "http://www.google.com/search?q="+searchKeyword+"+日本語";
                    webViewBrowser.loadUrl(url41);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 8) {
                    String url42= "http://www.google.com/search?q="+searchKeyword+"+意思";
                    webViewBrowser.loadUrl(url42);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 9) {
                    String url43 = "http://www.google.com/search?q="+searchKeyword+"+meaning";
                    webViewBrowser.loadUrl(url43);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }

                GoogleWordSearchSpinner.setAdapter(GoogleWordSearchSpinnerAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });



        /**
         * SentenceSearchSpinner & Spinner Adapters
         */
        final Spinner SentenceSearchSpinner = findViewById(R.id.Sentence_searcher_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> SentenceSearchSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.Sentence_searcher_spinner_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        SentenceSearchSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        SentenceSearchSpinner.setAdapter(SentenceSearchSpinnerAdapter);

        SentenceSearchSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                searchKeyword = wordInputView.getText().toString();

                if (position == 0){
                    return;

                }
                if (position == 1){
                    String url44= "https://ludwig.guru/s/"+searchKeyword;
                    webViewBrowser.loadUrl(url44);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 2) {
                    String url45= "https://sentence.yourdictionary.com/"+searchKeyword;
                    webViewBrowser.loadUrl(url45);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 3){
                    String url46= "http://www.jukuu.com/search.php?q="+searchKeyword;
                    webViewBrowser.loadUrl(url46);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 4) {
                    String url47= "http://www.jukuu.com/jsearch.php?q="+searchKeyword;
                    webViewBrowser.loadUrl(url47);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 5) {
                    String url48= "http://www.jukuu.com/jcsearch.php?q="+searchKeyword;
                    webViewBrowser.loadUrl(url48);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }

                SentenceSearchSpinner.setAdapter(SentenceSearchSpinnerAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });



        /**
         * MiscellaneousSpinner & Spinner Adapters
         */
        final Spinner MiscellaneousSpinner = findViewById(R.id.Miscellaneous_searcher_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> MiscellaneousSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.Miscellaneous_searcher_spinner_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        MiscellaneousSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        MiscellaneousSpinner.setAdapter(MiscellaneousSpinnerAdapter);

        MiscellaneousSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                searchKeyword = wordInputView.getText().toString();

                if (position == 0){
                    return;

                }
                if (position == 1){
                    String url49= "https://en.wikipedia.org/wiki/"+searchKeyword;
                    webViewBrowser.loadUrl(url49);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 2) {
                    String url50= "https://forvo.com/search/"+searchKeyword;
                    webViewBrowser.loadUrl(url50);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 3){
                    String url51= "https://wikidiff.com/"+searchKeyword;
                    webViewBrowser.loadUrl(url51);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 4) {
                    String url52= "http://www.netspeak.org/#query="+searchKeyword;
                    webViewBrowser.loadUrl(url52);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 5) {
                    String url53= "https://yomikatawa.com/kanji/"+searchKeyword+"?search=1";
                    webViewBrowser.loadUrl(url53);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 6) {
                    String url54= "https://cse.google.co.jp/cse?cx=partner-pub-1137871985589263%3A3025760782&ie=UTF-8&q="+searchKeyword;
                    webViewBrowser.loadUrl(url54);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 7) {
                    String url55= "http://www.gavo.t.u-tokyo.ac.jp/ojad/search/index/sortprefix:accent/narabi1:kata_asc/narabi2:accent_asc/narabi3:mola_asc/yure:visible/curve:invisible/details:invisible/limit:20/word:"+searchKeyword;
                    webViewBrowser.loadUrl(url55);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 8) {
                    String url56= "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=auto&tl=zh-CN&text="+searchKeyword;
                    webViewBrowser.loadUrl(url56);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 9) {
                    String url57= "https://translate.google.com.tw/?hl=zh-CN&tab=TT#view=home&op=translate&sl=auto&tl=zh-TW&text="+searchKeyword;
                    webViewBrowser.loadUrl(url57);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 10) {
                    String url58= "http://images.google.com/search?tbm=isch&q="+searchKeyword;
                    webViewBrowser.loadUrl(url58);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }

                MiscellaneousSpinner.setAdapter(MiscellaneousSpinnerAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });



    }


    /**
     * 在OnCreate外面另外設置用戶選取背景圖時的相關設定
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            DataManager.getInstance().setImageUrl(picturePath);
            cursor.close();
        }

        //Recreate this Activity
        recreate();// 直接調用Activity的recreate()方法重啟Activity

    }



    /**
     * 在OnCreate外面另外設置網頁框的相關設定
     */
    //Inner class for WebViewClientImpl.
    //在 WebView 畫面中，用戶無論點選了什麼超連結，都會開啟新的瀏覽器，想在自己的 WebView 中跳轉頁面，就必須建立一個 WebViewClient，同時若想知道接下來將前往哪個連結，也必須透過這個方法
    //By default, whenever the user clicks a hyperlink within a WebView, the system will respond by launching the user’s preferred web browser app and then loading the URL inside this browser.
    //While this is usually the preferred behaviour, there may be certain links that you do want to load inside your WebView.
    //If there are specific URLs that you want your application to handle internally, then you’ll need to create a subclass of WebViewClient and then use the shouldOverrideUrlLoading method to check whether the user has clicked a “whitelisted” URL.
    //其實我們沒必要自訂 WebViewClient 並重寫其 shouldOverrideUrlLoading 方法，
    //也就是說我們需要針對點擊事件添加額外控制時才需要自訂shouldOverrideUrlLoading，設定網址含那些特定文字時需要調用調用流覽器載入。
    //WebViewClient 源碼中 shouldOverrideUrlLoading 方法已經預設返回 false，
    //所以只要你設置了上面的WebViewClient 就可以實現在WebView中載入新的連結而不去調用流覽器載入。

    private class WebViewClientImpl extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);

            progressBar.setVisibility(View.VISIBLE);   //在啟動網頁框時顯示網頁框

            //設置啟動網頁框時的進度條加載進度
            new Thread(){
                @Override
                public void run() {
                    int i=0;
                    while(i<100){
                        i++;
                        try {
                            Thread.sleep(80);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        progressBar.setProgress(i);
                    }
                }
            }.start();
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);

            progressBar.setVisibility(View.GONE);   //網頁框內容加載完成時隱藏進度條
        }

    }


    //Check whether there’s any WebView history that the user can navigate back to//
    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK) && webViewBrowser.canGoBack()) {
            webViewBrowser.goBack();
            //If there is history, then the canGoBack method will return ‘true’//
            return true;
        }

        //If the button that’s been pressed wasn’t the ‘Back’ button, or there’s currently no
        //WebView history, then the system should resort to its default behavior and return
        //the user to the previous Activity//
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 在OnCreate外面另外設置存取相簿的相關設定
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == WRITE_PERMISSION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(LOG_TAG, "Write Permission Failed");
                Toast.makeText(this,getString(R.string.External_storage_permission), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M) //要加上這條限定Api等級才不會報錯
    private void requestWritePermission(){
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_PERMISSION);
        }
    }


}




