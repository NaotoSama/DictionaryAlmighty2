package com.dictionaryalmighty.android.dictionaryalmighty2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.example.android.dictionaryalmighty2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ComboSearchActivity extends AppCompatActivity {

//==============================================================================================
// 所有變數Variables
//==============================================================================================

    static WebView comboSearchWebViewBrowser1, comboSearchWebViewBrowser2, comboSearchWebViewBrowser3;    //網頁框

    ProgressBar comboSearchProgressBar1, comboSearchProgressBar2, comboSearchProgressBar3;   //網頁載入的進度條

    FloatingActionButton floatingActionButtonforWebView1, floatingActionButtonforWebView2, floatingActionButtonforWebView3; //浮動按鈕


//==============================================================================================
// onCreate
//==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.combo_search_page);


        /**
         * findViewById
         */
        comboSearchWebViewBrowser1 = findViewById(R.id.combo_search_webView_1);
        comboSearchWebViewBrowser2 = findViewById(R.id.combo_search_webView_2);
        comboSearchWebViewBrowser3 = findViewById(R.id.combo_search_webView_3);

        comboSearchProgressBar1 = findViewById(R.id.combo_search_webView_1_progressBar_1);
        comboSearchProgressBar2 = findViewById(R.id.combo_search_webView_2_progressBar_2);
        comboSearchProgressBar3 = findViewById(R.id.combo_search_webView_3_progressBar_3);

        floatingActionButtonforWebView1 = findViewById(R.id.floating_action_button_for_webView1);
        floatingActionButtonforWebView2 = findViewById(R.id.floating_action_button_for_webView2);
        floatingActionButtonforWebView3 = findViewById(R.id.floating_action_button_for_webView3);



        WebSettings comboSearchWebSettings1 = comboSearchWebViewBrowser1.getSettings(); //WebSettings 是用來設定 WebView 屬性的類別
        comboSearchWebSettings1.setJavaScriptEnabled(true); //針對 WebSettings 去做設定，WebView 預設下是限制 JavaScript 的，若要啟用需要做此設定
        comboSearchWebSettings1.setDomStorageEnabled(true); //enable dom storage
        comboSearchWebSettings1.setJavaScriptCanOpenWindowsAutomatically(true); //enable javascript
        comboSearchWebSettings1.setDatabaseEnabled(true); //enable database
        comboSearchWebSettings1.setSupportZoom(true); //內部網頁支援縮放
        comboSearchWebSettings1.setBuiltInZoomControls(true); //顯示縮放控制項
        comboSearchWebSettings1.setLoadWithOverviewMode(false);
        comboSearchWebSettings1.setUseWideViewPort(false);
        comboSearchWebViewBrowser1.setWebViewClient(new comboSearchWebViewClientImpl1());
        comboSearchWebViewBrowser1.requestFocus();
        //Webview裡面的網頁，如果有input需要輸入，但是點上去卻沒反應，輸入法不出來。這種情況是因為webview沒有獲取焦點。
        //需要在java裡面給webview設置一下requestFocus() 就行了。


        WebSettings comboSearchWebSettings2 = comboSearchWebViewBrowser2.getSettings();
        comboSearchWebSettings2.setJavaScriptEnabled(true);
        comboSearchWebSettings2.setDomStorageEnabled(true); //enable dom storage
        comboSearchWebSettings2.setJavaScriptCanOpenWindowsAutomatically(true); //enable javascript
        comboSearchWebSettings2.setDatabaseEnabled(true); //enable database
        comboSearchWebSettings2.setSupportZoom(true);
        comboSearchWebSettings2.setBuiltInZoomControls(true);
        comboSearchWebSettings2.setLoadWithOverviewMode(false);
        comboSearchWebSettings2.setUseWideViewPort(false);
        comboSearchWebViewBrowser2.setWebViewClient(new comboSearchWebViewClientImpl2());
        comboSearchWebViewBrowser2.requestFocus();


        WebSettings comboSearchWebSettings3 = comboSearchWebViewBrowser3.getSettings();
        comboSearchWebSettings3.setUserAgentString("Android");
        comboSearchWebSettings3.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        comboSearchWebSettings3.setJavaScriptEnabled(true);
        comboSearchWebSettings3.setDomStorageEnabled(true); //enable dom storage
        comboSearchWebSettings3.setJavaScriptCanOpenWindowsAutomatically(true); //enable javascript
        comboSearchWebSettings3.setDatabaseEnabled(true); //enable database
        comboSearchWebSettings3.setSupportZoom(true);
        comboSearchWebSettings3.setBuiltInZoomControls(true);
        comboSearchWebSettings3.setLoadWithOverviewMode(true);
        comboSearchWebSettings3.setUseWideViewPort(true);
        comboSearchWebViewBrowser3.setWebViewClient(new comboSearchWebViewClientImpl3());
        comboSearchWebViewBrowser3.requestFocus();


        loadFirstDefaultDictionaries();
        loadSecondDefaultDictionaries();
        loadThirdDefaultDictionaries();


        /**
         * 設定點擊浮動按鈕時要顯示或隱藏的物件
         */
        floatingActionButtonforWebView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (floatingActionButtonforWebView2.isShown()) {

                    floatingActionButtonforWebView1.setImageResource(R.drawable.minimize_browser_icon);
                    floatingActionButtonforWebView1.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(192,192,192)));

                    comboSearchWebViewBrowser2.setVisibility(View.GONE);
                    comboSearchWebViewBrowser3.setVisibility(View.GONE);
                    comboSearchProgressBar1.setVisibility(View.GONE);
                    comboSearchProgressBar2.setVisibility(View.GONE);
                    comboSearchProgressBar3.setVisibility(View.GONE);
                    floatingActionButtonforWebView2.setVisibility(View.GONE);
                    floatingActionButtonforWebView3.setVisibility(View.GONE);
                }
                else {

                    floatingActionButtonforWebView1.setImageResource(R.drawable.maximize_browser_icon);
                    floatingActionButtonforWebView1.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.spring_green)));

                    comboSearchWebViewBrowser2.setVisibility(View.VISIBLE);
                    comboSearchWebViewBrowser3.setVisibility(View.VISIBLE);
                    comboSearchProgressBar1.setVisibility(View.VISIBLE);
                    comboSearchProgressBar2.setVisibility(View.VISIBLE);
                    comboSearchProgressBar3.setVisibility(View.VISIBLE);
                    floatingActionButtonforWebView2.setVisibility(View.VISIBLE);
                    floatingActionButtonforWebView3.setVisibility(View.VISIBLE);
                }
            }
        });

        floatingActionButtonforWebView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (floatingActionButtonforWebView3.isShown()) {

                    floatingActionButtonforWebView2.setImageResource(R.drawable.minimize_browser_icon);
                    floatingActionButtonforWebView2.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(192,192,192)));

                    comboSearchWebViewBrowser1.setVisibility(View.GONE);
                    comboSearchWebViewBrowser3.setVisibility(View.GONE);
                    comboSearchProgressBar1.setVisibility(View.GONE);
                    comboSearchProgressBar2.setVisibility(View.GONE);
                    comboSearchProgressBar3.setVisibility(View.GONE);
                    floatingActionButtonforWebView1.setVisibility(View.GONE);
                    floatingActionButtonforWebView3.setVisibility(View.GONE);
                }
                else {

                    floatingActionButtonforWebView2.setImageResource(R.drawable.maximize_browser_icon);
                    floatingActionButtonforWebView2.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_yellow)));

                    comboSearchWebViewBrowser1.setVisibility(View.VISIBLE);
                    comboSearchWebViewBrowser3.setVisibility(View.VISIBLE);
                    comboSearchProgressBar1.setVisibility(View.VISIBLE);
                    comboSearchProgressBar2.setVisibility(View.VISIBLE);
                    comboSearchProgressBar3.setVisibility(View.VISIBLE);
                    floatingActionButtonforWebView1.setVisibility(View.VISIBLE);
                    floatingActionButtonforWebView3.setVisibility(View.VISIBLE);
                }
            }
        });

        floatingActionButtonforWebView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (floatingActionButtonforWebView1.isShown()) {

                    floatingActionButtonforWebView3.setImageResource(R.drawable.minimize_browser_icon);
                    floatingActionButtonforWebView3.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(192,192,192)));

                    comboSearchWebViewBrowser1.setVisibility(View.GONE);
                    comboSearchWebViewBrowser2.setVisibility(View.GONE);
                    comboSearchProgressBar1.setVisibility(View.GONE);
                    comboSearchProgressBar2.setVisibility(View.GONE);
                    comboSearchProgressBar3.setAlpha(0);  //有Bug只好這樣寫

                    //斷開comboSearchProgressBar2的下方constraint才不會在全螢幕時卡在中間
                    ConstraintSet set = new ConstraintSet();
                    ConstraintLayout layout;
                    layout = (ConstraintLayout) findViewById(R.id.combo_search_activity_layout);
                    set.clone(layout);
                    // The following breaks the connection.
                    set.clear(R.id.combo_search_webView_2_progressBar_2, ConstraintSet.BOTTOM);
                    // Comment out line above and uncomment line below to make the connection.
                    // set.connect(R.id.bottomText, ConstraintSet.TOP, R.id.imageView, ConstraintSet.BOTTOM, 0);
                    set.applyTo(layout);

                    floatingActionButtonforWebView1.setVisibility(View.GONE);
                    floatingActionButtonforWebView2.setVisibility(View.GONE);
                }
                else {

                    floatingActionButtonforWebView3.setImageResource(R.drawable.maximize_browser_icon);
                    floatingActionButtonforWebView3.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.design_default_color_primary)));

                    comboSearchWebViewBrowser1.setVisibility(View.VISIBLE);
                    comboSearchWebViewBrowser2.setVisibility(View.VISIBLE);
                    comboSearchProgressBar1.setVisibility(View.VISIBLE);
                    comboSearchProgressBar2.setVisibility(View.VISIBLE);
                    comboSearchProgressBar3.setAlpha(1); //有Bug只好這樣寫

                    //從全螢幕狀態返回原狀態時要把斷開的comboSearchProgressBar2下方constraint補回去
                    ConstraintSet set = new ConstraintSet();
                    ConstraintLayout layout;
                    layout = (ConstraintLayout) findViewById(R.id.combo_search_activity_layout);
                    set.clone(layout);
                    // The following breaks the connection.
                    // set.clear(R.id.combo_search_webView_2_progressBar_2, ConstraintSet.BOTTOM);
                    // Comment out line above and uncomment line below to make the connection.
                    set.connect(R.id.combo_search_webView_2_progressBar_2, ConstraintSet.BOTTOM, R.id.combo_search_webView_3_progressBar_3, ConstraintSet.BOTTOM, 620);
                    set.applyTo(layout);

                    floatingActionButtonforWebView1.setVisibility(View.VISIBLE);
                    floatingActionButtonforWebView2.setVisibility(View.VISIBLE);
                }
            }
        });



    }



    //==============================================================================================
    // 在OnCreate外面另外設置網頁框的相關設定

    //Inner class for WebViewClientImpl.
    //在 WebView 畫面中，用戶無論點選了什麼超連結，都會開啟新的瀏覽器，想在自己的 WebView 中跳轉頁面，就必須建立一個 WebViewClient，同時若想知道接下來將前往哪個連結，也必須透過這個方法
    //By default, whenever the user clicks a hyperlink within a WebView, the system will respond by launching the user’s preferred web browser app and then loading the URL inside this browser.
    //While this is usually the preferred behaviour, there may be certain links that you do want to load inside your WebView.
    //If there are specific URLs that you want your application to handle internally, then you’ll need to create a subclass of WebViewClient and then use the shouldOverrideUrlLoading method to check whether the user has clicked a “whitelisted” URL.
    //其實我們沒必要自訂 WebViewClient 並重寫其 shouldOverrideUrlLoading 方法，
    //也就是說我們需要針對點擊事件添加額外控制時才需要自訂shouldOverrideUrlLoading，設定網址含那些特定文字時需要調用調用流覽器載入。
    //WebViewClient 源碼中 shouldOverrideUrlLoading 方法已經預設返回 false，
    //所以只要你設置了上面的WebViewClient 就可以實現在WebView中載入新的連結而不去調用流覽器載入。

    //==============================================================================================

    private class comboSearchWebViewClientImpl1 extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);

            comboSearchProgressBar1.setVisibility(View.VISIBLE);   //在啟動網頁框時顯示網頁框

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
                        comboSearchProgressBar1.setProgress(i);
                    }
                }
            }.start();
        }
    }


    private class comboSearchWebViewClientImpl2 extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);

            comboSearchProgressBar2.setVisibility(View.VISIBLE);   //在啟動網頁框時顯示網頁框

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
                        comboSearchProgressBar2.setProgress(i);
                    }
                }
            }.start();
        }
    }


    private class comboSearchWebViewClientImpl3 extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);

            comboSearchProgressBar3.setVisibility(View.VISIBLE);   //在啟動網頁框時顯示網頁框

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
                        comboSearchProgressBar3.setProgress(i);
                    }
                }
            }.start();
        }
    }



    public void loadFirstDefaultDictionaries() {

                MainActivity.searchKeyword= MainActivity.wordInputView.getText().toString(); //抓用戶輸入的關鍵字

                switch(MainActivity.defaultComboSearchCodeFirstDictionary){
                    case "Yahoo Dictionary":
                        String yahooDictionaryUrl= "https://tw.dictionary.search.yahoo.com/search;_ylt=AwrtXGoL8vtcAQoAnHV9rolQ;_ylc=X1MDMTM1MTIwMDM4MQRfcgMyBGZyA3NmcARncHJpZAN0RjJnMS51MlNWU3NDZ1pfVC4zNUFBBG5fcnNsdAMwBG5fc3VnZwM0BG9yaWdpbgN0dy5kaWN0aW9uYXJ5LnNlYXJjaC55YWhvby5jb20EcG9zAzAEcHFzdHIDBHBxc3RybAMEcXN0cmwDMwRxdWVyeQNHQVkEdF9zdG1wAzE1NjAwMTU0MTE-?p="+ MainActivity.searchKeyword+"&fr2=sb-top-tw.dictionary.search&fr=sfp";
                        comboSearchWebViewBrowser1.loadUrl(yahooDictionaryUrl);
                        break;
                    case "National Academy for Educational Research":
                        String naerUrl= "http://terms.naer.edu.tw/m/search/?q="+ MainActivity.searchKeyword+"&field=text&op=AND&page=";
                        comboSearchWebViewBrowser1.loadUrl(naerUrl);
                        break;
                    case "Dict Site":
                        String dictDotSiteUrl= "http://dict.site/"+ MainActivity.searchKeyword+".html";
                        comboSearchWebViewBrowser1.loadUrl(dictDotSiteUrl);
                        break;
                    case "Fast Dict":
                        String fastDictUrl= "http://www.fastdict.net/hongkong/word.html?word="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(fastDictUrl);
                        break;
                    case "Dict CN":
                        String DictDotCnUrl= "http://dict.cn/big5/"+ MainActivity.searchKeyword;;
                        comboSearchWebViewBrowser1.loadUrl(DictDotCnUrl);
                        break;
                    case "Google Dictionary":
                        String googleDictionaryUrl= "http://gdictchinese.freecollocation.com/search/?q="+ MainActivity.searchKeyword;;
                        comboSearchWebViewBrowser1.loadUrl(googleDictionaryUrl);
                        break;
                    case "VoiceTube":
                        String voicetubeUrl= "https://tw.voicetube.com/definition/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(voicetubeUrl);
                        break;
                    case "Cambridge EN-CH":
                        String cambridgeDictionaryUrl= "https://dictionary.cambridge.org/zht/詞典/英語-漢語-繁體/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(cambridgeDictionaryUrl);
                        break;
                    case "WordReference EN-CH":
                        String wordReferenceEnChDictionaryUrl= "https://www.wordreference.com/enzh/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(wordReferenceEnChDictionaryUrl);
                        break;
                    case "WordReference CH-EN":
                        String wordReferenceChEnDictionaryUrl= "https://www.wordreference.com/zhen/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(wordReferenceChEnDictionaryUrl);
                        break;
                    case "Merriam Webster":
                        String merriamDictionaryUrl= "https://www.merriam-webster.com/dictionary/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(merriamDictionaryUrl);
                        break;
                    case "Macmillan Dictionary":
                        String macmillanDictionaryUrl= "https://www.macmillandictionary.com/dictionary/british/"+ MainActivity.searchKeyword+"_1";
                        comboSearchWebViewBrowser1.loadUrl(macmillanDictionaryUrl);
                        break;
                    case "Collins":
                        String collinsDictionaryUrl= "https://www.collinsdictionary.com/dictionary/english/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(collinsDictionaryUrl);
                        break;
                    case "Oxford":
                        String oxfordDictionaryUrl= "https://en.oxforddictionaries.com/definition/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(oxfordDictionaryUrl);
                        break;
                    case "Vocabulary":
                        String vocabularyDotComUrl= "https://www.vocabulary.com/dictionary/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(vocabularyDotComUrl);
                        break;
                    case "Dictionary.com":
                        String dictionaryDotComUrl= "https://www.dictionary.com/browse/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(dictionaryDotComUrl);
                        break;
                    case "The Free Dictionary":
                        String theFreeDictionaryUrl= "https://www.thefreedictionary.com/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(theFreeDictionaryUrl);
                        break;
                    case "Fine Dictionary":
                        String fineDictionaryUrl= "http://www.finedictionary.com/"+ MainActivity.searchKeyword+".html";
                        comboSearchWebViewBrowser1.loadUrl(fineDictionaryUrl);
                        break;
                    case "Your_Dictionary":
                        String yourDictionaryUrl= "https://www.yourdictionary.com/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(yourDictionaryUrl);
                        break;
                    case "Longman Dictionary":
                        String longmanDictionaryUrl= "https://www.ldoceonline.com/dictionary/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(longmanDictionaryUrl);
                        break;
                    case "American Heritage Dictionary":
                        String americanHeritageDictionaryUrl= "https://ahdictionary.com/word/search.html?q="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(americanHeritageDictionaryUrl);
                        break;
                    case "Word Sense":
                        String wordSenseDictionaryUrl= "https://www.wordsense.eu/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(wordSenseDictionaryUrl);
                        break;
                    case "WordWeb":
                        String wordWebUrl= "https://www.wordwebonline.com/search.pl?w="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(wordWebUrl);
                        break;
                    case "WordNik":
                        String wordNikUrl= "https://www.wordnik.com/words/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(wordNikUrl);
                        break;
                    case "Wiki Dictionary":
                        String wiktionaryUrl= "https://en.wiktionary.org/wiki/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(wiktionaryUrl);
                        break;
                    case "Business Dictionary":
                        String businessDictionaryUrl= "http://www.businessdictionary.com/definition/"+ MainActivity.searchKeyword+".html";
                        comboSearchWebViewBrowser1.loadUrl(businessDictionaryUrl);
                        break;
                    case "Idioms and Slang":
                        String idiomsAndSlangDictionaryUrl= "https://www.idiomsandslang.com/?s="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(idiomsAndSlangDictionaryUrl);
                        break;
                    case "Slang":
                        String slangDictionary= "http://www.yiym.com/?s="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(slangDictionary);
                        break;
                    case "The Online Slang Dictionary":
                        String theOnlineSlangDictionaryUrl= "http://onlineslangdictionary.com/search/?q="+ MainActivity.searchKeyword+"&sa=Search";
                        comboSearchWebViewBrowser1.loadUrl(theOnlineSlangDictionaryUrl);
                        break;
                    case "Idioms 4 You":
                        String idioms4YouUrl= "http://www.idioms4you.com/tipsearch/search.html?q="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(idioms4YouUrl);
                        break;
                    case "Greens dictionary of slang":
                        String greensDictionaryOfSlangUrl= "https://greensdictofslang.com/search/basic?q="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(greensDictionaryOfSlangUrl);
                        break;
                    case "The Idioms":
                        String theIdiomsUrl= "https://www.theidioms.com/?s="+MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(theIdiomsUrl);
                        break;
                    case "Etymology Dictionary":
                        String etymologyDictionaryUrl= "https://www.etymonline.com/search?q="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(etymologyDictionaryUrl);
                        break;
                    case "TechDico":
                        String TechDicoUrl= "https://www.techdico.com/translation/english-chinese/"+ MainActivity.searchKeyword+".html";
                        comboSearchWebViewBrowser1.loadUrl(TechDicoUrl);
                        break;
                    case "BioMedical Dictionary":
                        String BioMedicalDictionaryUrl= "http://dict.bioon.com/search.asp?txtitle="+ MainActivity.searchKeyword+"&searchButton=查词典&matchtype=0";
                        comboSearchWebViewBrowser1.loadUrl(BioMedicalDictionaryUrl);
                        break;
                    case "Buddhism Dictionary":
                        //這邊設置AlertDialog提醒用戶24小時內只能查10字，輸入"guest"登入使用
                        CFAlertDialog.Builder logInToUseBuddhismDictionaryAlertDialogBuilder = new CFAlertDialog.Builder(ComboSearchActivity.this)
                                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                .setDialogBackgroundColor(Color.parseColor("#fafcd7"))
                                .setCornerRadius(50)
                                .setTitle(getResources().getString(R.string.Buddhism_dictionary_explanation))
                                .setTextColor(Color.BLUE)
                                .setCancelable(false) //按到旁邊的空白處AlertDialog也不會消失
                                .setHeaderView(R.layout.custom_alert_diaglog_question_mark)

                                //AlertDialog的確定鈕，查佛學字典
                                .addButton(getResources().getString(R.string.Confirm)
                                        , Color.BLACK, Color.GREEN, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {

                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://buddhism-dict.net/cgi-bin/xpr-ddb.pl?q="+ MainActivity.searchKeyword))); //Fail-safe for when Buddhism Dictionary fails to render in the webView.
                                                                                                    //String buddhismDictionaryDictionaryUrl= "http://buddhism-dict.net/cgi-bin/xpr-ddb.pl?q="+searchKeyword;
                                                                                                    //comboSearchWebViewBrowser1.loadUrl(buddhismDictionaryDictionaryUrl);
                                            dialog.dismiss();
                                        })

                                //AlertDialog的取消鈕
                                .addButton(getResources().getString(R.string.Cancel)
                                        , Color.BLACK, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {

                                            dialog.dismiss();
                                        })

                                //AlertDialog的中立鈕，看教學
                                .addButton(getString(R.string.Watch_tutorial)
                                        , Color.BLACK, Color.LTGRAY, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {

                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/M0k8TnEMHY0")));
                                            dialog.dismiss();
                                        });

                        //把AlertDialog顯示出來
                        logInToUseBuddhismDictionaryAlertDialogBuilder.create().show();
                        break;
                    case "IsPlural Dictionary":
                        String IsPluralDictionaryUrl= "https://www.isplural.com/plural_singular/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(IsPluralDictionaryUrl);
                        break;
                    case "LingoHelpPrepositions":
                        String lingoHelpPrepositionsUrl= "https://lingohelp.me/q/?w="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(lingoHelpPrepositionsUrl);
                        break;
                    case "Power Thesaurus Synonym":
                        String powerThesaurusSynonymUrl= "https://www.powerthesaurus.org/"+ MainActivity.searchKeyword+"/synonyms";
                        comboSearchWebViewBrowser1.loadUrl(powerThesaurusSynonymUrl);
                        break;
                    case "Power Thesaurus Antonym":
                        String powerThesaurusAntonymsUrl= "https://www.powerthesaurus.org/"+ MainActivity.searchKeyword+"/antonyms";
                        comboSearchWebViewBrowser1.loadUrl(powerThesaurusAntonymsUrl);
                        break;
                    case "Word Hippo":
                        String wordHippoUrl= "https://www.wordhippo.com/what-is/another-word-for/"+ MainActivity.searchKeyword+".html";
                        comboSearchWebViewBrowser1.loadUrl(wordHippoUrl);
                        break;
                    case "Onelook":
                        String onelookUrl= "https://www.onelook.com/thesaurus/?s="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(onelookUrl);
                        break;
                    case "ozdic collocation":
                        String ozdicCollocationUrl= "http://www.ozdic.com/collocation-dictionary/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(ozdicCollocationUrl);
                        break;
                    case "Stack Exchange English Learners":
                        String StackExchangeEnglishLearnersUrl= "https://ell.stackexchange.com/search?q="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(StackExchangeEnglishLearnersUrl);
                        break;
                    case "Stack Exchange English Language and Usage":
                        String StackExchangeEnglishLanguageAndUsageUrl= "https://english.stackexchange.com/search?q="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(StackExchangeEnglishLanguageAndUsageUrl);
                        break;
                    case "Weblio JP":
                        String weblioJPUrl= "https://www.weblio.jp/content/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(weblioJPUrl);
                        break;
                    case "Weblio CN":
                        String weblioCHUrl= "https://cjjc.weblio.jp/content/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(weblioCHUrl);
                        break;
                    case "Weblio EN":
                        String weblioENUrl= "https://ejje.weblio.jp/content/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(weblioENUrl);
                        break;
                    case "Weblio Synonym":
                        String weblioThesaurusUrl= "https://thesaurus.weblio.jp/content/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(weblioThesaurusUrl);
                        break;
                    case "Tangorin Word":
                        String tangorinDictionaryUrl= "https://tangorin.com/words?search="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(tangorinDictionaryUrl);
                        break;
                    case "Tangorin Kanji":
                        String tangorinKanjiUrl= "https://tangorin.com/kanji?search="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(tangorinKanjiUrl);
                        break;
                    case "Tangorin Names":
                        String tangorinNamesUrl= "https://tangorin.com/names?search="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(tangorinNamesUrl);
                        break;
                    case "Tangorin Sentence":
                        String tangorinSentencesUrl= "https://tangorin.com/sentences?search="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(tangorinSentencesUrl);
                        break;
                    case "DA JP-TW Dictionary":
                        String DaJPtoCHDictionaryUrl= "http://dict.asia/jc/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(DaJPtoCHDictionaryUrl);
                        break;
                    case "DA TW-JP Dictionary":
                        String DaCHtoJPDictionaryUrl= "http://dict.asia/cj/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(DaCHtoJPDictionaryUrl);
                        break;
                    case "Goo":
                        String gooDictionaryUrl= "https://dictionary.goo.ne.jp/srch/jn/"+ MainActivity.searchKeyword+"/m0u/";
                        comboSearchWebViewBrowser1.loadUrl(gooDictionaryUrl);
                        break;
                                                                                                    //case "Sanseido":
                                                                                                    //    String sanseidoUrl= "http://www.sanseido.biz/sp/Search?target_words="+ MainActivity.searchKeyword+"&search_type=0&start_index=0&selected_dic=";
                                                                                                    //    comboSearchWebViewBrowser1.loadUrl(sanseidoUrl);
                                                                                                    //    break;
                    case "Kotoba Bank":
                        String kotobank= "https://kotobank.jp/word/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(kotobank);
                        break;
                    case "J Logos":
                        String jlogosUrl= "http://s.jlogos.com/list.html?keyword="+ MainActivity.searchKeyword+"&opt_val=0";
                        comboSearchWebViewBrowser1.loadUrl(jlogosUrl);
                        break;
                    case "Eijirou":
                        String eijiroDictionryUrl= "https://eow.alc.co.jp/sp/search.html?q="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(eijiroDictionryUrl);
                        break;
                    case "How do you say this in English":
                        String whatIsItInEnglishUrl= "https://eikaiwa.dmm.com/uknow/search/?keyword="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(whatIsItInEnglishUrl);
                        break;
                    case "Jisho":
                        String jishoUrl= "https://jisho.org/search/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(jishoUrl);
                        break;
                    case "Cambridge JP-EN":
                        String CambridgeJPtoENUrl= "https://dictionary.cambridge.org/zht/詞典/japanese-english/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(CambridgeJPtoENUrl);
                        break;
                    case "Cambridge EN-JP":
                        String CambridgeENtoJPUrl= "https://dictionary.cambridge.org/zht/詞典/英語-日語/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(CambridgeENtoJPUrl);
                        break;
                    case "WWW JDIC JP-EN":
                        String wwwjdicJpToEnUrl= "http://www.edrdg.org/cgi-bin/wwwjdic/wwwjdic?1MUJ"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(wwwjdicJpToEnUrl);
                        break;
                    case "WWW JDIC EN-JP":
                        String wwwjdicEnToJaUrl= "http://www.edrdg.org/cgi-bin/wwwjdic/wwwjdic?1MDE"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(wwwjdicEnToJaUrl);
                        break;
                    case "WordReference EN-JP":
                        String wordReferenceEnJpDictionaryUrl= "https://www.wordreference.com/enja/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(wordReferenceEnJpDictionaryUrl);
                        break;
                    case "WordReference JP-EN":
                        String wordReferenceJpEnDictionaryUrl= "https://www.wordreference.com/jaen/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(wordReferenceJpEnDictionaryUrl);
                        break;
                    case "RomajiDesu JP-EN EN-JP":
                        String RomajiDesuJpEnEnJpDictionaryUrl= "http://m.romajidesu.com/dictionary/meaning-of-"+ MainActivity.searchKeyword+".html";
                        comboSearchWebViewBrowser1.loadUrl(RomajiDesuJpEnEnJpDictionaryUrl);
                        break;
                    case "RomajiDesu Kanji":
                        String RomajiDesuKanjiDictionaryUrl= "http://m.romajidesu.com/kanji/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(RomajiDesuKanjiDictionaryUrl);
                        break;
                    case "JapanDict":
                        String JapanDictDictionaryUrl= "https://www.japandict.com/?s="+ MainActivity.searchKeyword+"&lang=eng";
                        comboSearchWebViewBrowser1.loadUrl(JapanDictDictionaryUrl);
                        break;
                    case "JapanDict Kanji":
                        String JapanDictKanjiDictionaryUrl= "https://www.japandict.com/kanji/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(JapanDictKanjiDictionaryUrl);
                        break;
                    case "Japanese Name Dictionary":
                        String JapaneseNameDictionaryUrl= "https://kanji.reader.bz/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(JapaneseNameDictionaryUrl);
                        break;
                    case "Stack Exchange Japanese Language":
                        String StackExchangeJapaneseLanguageUrl= "https://japanese.stackexchange.com/search?q="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(StackExchangeJapaneseLanguageUrl);
                        break;
                    case "Metapedia":
                        String MetapediaUrl= "http://metapedia.jp/?q="+MainActivity.searchKeyword+"#gsc.tab=0&gsc.q="+MainActivity.searchKeyword+"&gsc.page=1";
                        comboSearchWebViewBrowser1.loadUrl(MetapediaUrl);
                        break;
                                                                                                                //                    case "Word Plus Chinese":
                                                                                                                //                        String googlePlusChinese= "http://www.google.com/search?q="+searchKeyword+"+中文";
                                                                                                                //                        comboSearchWebViewBrowser1.loadUrl(googlePlusChinese);
                                                                                                                //                        break;
                                                                                                                //                    case "Word Plus English 1":
                                                                                                                //                        String googlePlusENglish1= "http://www.google.com/search?q="+searchKeyword+"+英文";
                                                                                                                //                        comboSearchWebViewBrowser1.loadUrl(googlePlusENglish1);
                                                                                                                //                        break;
                                                                                                                //                    case "Word Plus English 2":
                                                                                                                //                        String googlePlusENglish2= "http://www.google.com/search?q="+searchKeyword+"+英語";
                                                                                                                //                        comboSearchWebViewBrowser1.loadUrl(googlePlusENglish2);
                                                                                                                //                        break;
                                                                                                                //                    case "Word Plus Translation":
                                                                                                                //                        String googlePlusTranslation= "http://www.google.com/search?q="+searchKeyword+"+翻譯";
                                                                                                                //                        comboSearchWebViewBrowser1.loadUrl(googlePlusTranslation);
                                                                                                                //                        break;
                                                                                                                //                    case "Word Plus Japanese 1":
                                                                                                                //                        String googlePlusJapanese1= "http://www.google.com/search?q="+searchKeyword+"+日文";
                                                                                                                //                        comboSearchWebViewBrowser1.loadUrl(googlePlusJapanese1);
                                                                                                                //                        break;
                                                                                                                //                    case "Word Plus Japanese 2":
                                                                                                                //                        String googlePlusJapanese2= "http://www.google.com/search?q="+searchKeyword+"+日語";
                                                                                                                //                        comboSearchWebViewBrowser1.loadUrl(googlePlusJapanese2);
                                                                                                                //                        break;
                                                                                                                //                    case "Word Plus Japanese 3":
                                                                                                                //                        String googlePlusJapanese3= "http://www.google.com/search?q="+searchKeyword+"+日本語";
                                                                                                                //                        comboSearchWebViewBrowser1.loadUrl(googlePlusJapanese3);
                                                                                                                //                        break;
                                                                                                                //                    case "Word Plus Meaning 1":
                                                                                                                //                        String googlePlusMeaning1= "http://www.google.com/search?q="+searchKeyword+"+意思";
                                                                                                                //                        comboSearchWebViewBrowser1.loadUrl(googlePlusMeaning1);
                                                                                                                //                        break;
                                                                                                                //                    case "Word Plus Meaning 2":
                                                                                                                //                        String googlePlusMeaning2 = "http://www.google.com/search?q="+searchKeyword+"+meaning";
                                                                                                                //                        comboSearchWebViewBrowser1.loadUrl(googlePlusMeaning2);
                                                                                                                //                        break;
                    case "Google translate to CHTW":
                        String GoogleTranslateToCHTWUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=zh-TW&text="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(GoogleTranslateToCHTWUrl);
                        break;
                    case "Google translate to CHCN":
                        String GoogleTranslateToCHCNUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=zh-CN&text="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(GoogleTranslateToCHCNUrl);
                        break;
                    case "Google translate to EN":
                        String GoogleTranslateToENUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=en&text="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(GoogleTranslateToENUrl);
                        break;
                    case "Google translate to JP":
                        String GoogleTranslateToJPUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=ja&text="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(GoogleTranslateToJPUrl);
                        break;
                    case "Google translate to KR":
                        String GoogleTranslateToKRUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=ko&text="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(GoogleTranslateToKRUrl);
                        break;
                    case "Google translate to SP":
                        String GoogleTranslateToSPUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=es&text="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(GoogleTranslateToSPUrl);
                        break;
                    case "Google Image":
                        String imageSearchUrl= "http://images.google.com/search?tbm=isch&q="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(imageSearchUrl);
                        break;
                    case "Ludwig":
                        String ludwigUrl= "https://ludwig.guru/s/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(ludwigUrl);
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ludwig.guru/s/"+ MainActivity.searchKeyword))); //Fail-safe for when the Ludwig fails to render in the webView.
                        Toast.makeText(getApplicationContext(),R.string.Technical_difficulty_in_rendering_web_links,Toast.LENGTH_LONG).show();
                        break;
                    case "Search Sentences":
                        String searchSentencesUrl= "https://searchsentences.com/words/"+ MainActivity.searchKeyword+"-in-a-sentence";
                        comboSearchWebViewBrowser1.loadUrl(searchSentencesUrl);
                        break;
                    case "Your Dictionary Example Sentences":
                        String yourDictionarySentenceUrl= "https://sentence.yourdictionary.com/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(yourDictionarySentenceUrl);
                        break;
                    case "YouGlish":
                        String youglishUrl= "https://youglish.com/search/"+ MainActivity.searchKeyword+"/all?";
                        comboSearchWebViewBrowser1.loadUrl(youglishUrl);
                        break;
                    case "Word Cool EN-CH":
                        String jukuuUrlCHEN= "http://www.jukuu.com/search.php?q="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(jukuuUrlCHEN);
                        break;
                    case "Word Cool EN-JP":
                        String jukuuUrlJPEN= "http://www.jukuu.com/jsearch.php?q="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(jukuuUrlJPEN);
                        break;
                    case "Word Cool JP-CH":
                        String jukuuUrlCHJP= "http://www.jukuu.com/jcsearch.php?q="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(jukuuUrlCHJP);
                        break;
                    case "Linguee CH-EN":
                        String LingueeUrlCHEN= "https://cn.linguee.com/中文-英语/search?source=auto&query="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(LingueeUrlCHEN);
                        break;
                    case "Linguee JP-EN":
                        String LingueeUrlJPEN= "https://www.linguee.jp/日本語-英語/search?source=auto&query="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(LingueeUrlJPEN);
                        break;
                    case "Wikipedia TW":
                        String wikipediaTWUrl= "https://zh.wikipedia.org/wiki/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(wikipediaTWUrl);
                        break;
                    case "Wikipedia EN":
                        String wikipediaENUrl= "https://en.wikipedia.org/wiki/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(wikipediaENUrl);
                        break;
                    case "English Encyclopedia":
                        String enEncyclopediaUrl= "https://www.encyclo.co.uk/meaning-of-"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(enEncyclopediaUrl);
                        break;
                    case "Forvo":
                        String forvoUrl= "https://zh.forvo.com/search/"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(forvoUrl);
                        break;
                    case "Difference Between":
                        String differenceBetweenUrl= "http://www.differencebetween.net/search/?cx=partner-pub-1911891147296207%3Aw80z4hjpu14&cof=FORID%3A9&ie=ISO-8859-1&q="+ MainActivity.searchKeyword+"&sa=Search";
                        comboSearchWebViewBrowser1.loadUrl(differenceBetweenUrl);
                        break;
                    case "Net Speak":
                        String netspeakUrl= "https://netspeak.org/#q="+ MainActivity.searchKeyword+"&corpus=web-en";
                        comboSearchWebViewBrowser1.loadUrl(netspeakUrl);
                        break;
                    case "Just the Word":
                        String justTheWordUrl= "http://www.just-the-word.com/main.pl?word="+ MainActivity.searchKeyword+"+&mode=combinations";
                        comboSearchWebViewBrowser1.loadUrl(justTheWordUrl);
                        break;
                    case "Yomikata":
                        String yomikatawaUrl= "https://yomikatawa.com/kanji/"+ MainActivity.searchKeyword+"?search=1";
                        comboSearchWebViewBrowser1.loadUrl(yomikatawaUrl);
                        break;
                    case "Chigai":
                        String ChigaihaUrl= "https://cse.google.co.jp/cse?cx=partner-pub-1137871985589263%3A3025760782&ie=UTF-8&q="+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(ChigaihaUrl);
                        break;
                    case "OJAD":
                        String suzukikunUrl= "http://www.gavo.t.u-tokyo.ac.jp/ojad/search/index/sortprefix:accent/narabi1:kata_asc/narabi2:accent_asc/narabi3:mola_asc/yure:visible/curve:invisible/details:invisible/limit:20/word:"+ MainActivity.searchKeyword;
                        comboSearchWebViewBrowser1.loadUrl(suzukikunUrl);
                        break;
                }


                MainActivity.saveKeywordtoUserInputListView ();
                saveUserInputArrayListToSharedPreferences ();
            }



    public void loadSecondDefaultDictionaries() {

        MainActivity.searchKeyword= MainActivity.wordInputView.getText().toString(); //抓用戶輸入的關鍵字

        switch(MainActivity.defaultComboSearchCodeSecondDictionary){
            case "Yahoo Dictionary":
                String yahooDictionaryUrl= "https://tw.dictionary.search.yahoo.com/search;_ylt=AwrtXGoL8vtcAQoAnHV9rolQ;_ylc=X1MDMTM1MTIwMDM4MQRfcgMyBGZyA3NmcARncHJpZAN0RjJnMS51MlNWU3NDZ1pfVC4zNUFBBG5fcnNsdAMwBG5fc3VnZwM0BG9yaWdpbgN0dy5kaWN0aW9uYXJ5LnNlYXJjaC55YWhvby5jb20EcG9zAzAEcHFzdHIDBHBxc3RybAMEcXN0cmwDMwRxdWVyeQNHQVkEdF9zdG1wAzE1NjAwMTU0MTE-?p="+ MainActivity.searchKeyword+"&fr2=sb-top-tw.dictionary.search&fr=sfp";
                comboSearchWebViewBrowser2.loadUrl(yahooDictionaryUrl);
                break;
            case "National Academy for Educational Research":
                String naerUrl= "http://terms.naer.edu.tw/m/search/?q="+ MainActivity.searchKeyword+"&field=text&op=AND&page=";
                comboSearchWebViewBrowser2.loadUrl(naerUrl);
                break;
            case "Dict Site":
                String dictDotSiteUrl= "http://dict.site/"+ MainActivity.searchKeyword+".html";
                comboSearchWebViewBrowser2.loadUrl(dictDotSiteUrl);
                break;
            case "Fast Dict":
                String fastDictUrl= "http://www.fastdict.net/hongkong/word.html?word="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(fastDictUrl);
                break;
            case "Dict CN":
                String DictDotCnUrl= "http://dict.cn/big5/"+ MainActivity.searchKeyword;;
                comboSearchWebViewBrowser2.loadUrl(DictDotCnUrl);
                break;
            case "Google Dictionary":
                String googleDictionaryUrl= "http://gdictchinese.freecollocation.com/search/?q="+ MainActivity.searchKeyword;;
                comboSearchWebViewBrowser2.loadUrl(googleDictionaryUrl);
                break;
            case "VoiceTube":
                String voicetubeUrl= "https://tw.voicetube.com/definition/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(voicetubeUrl);
                break;
            case "Cambridge EN-CH":
                String cambridgeDictionaryUrl= "https://dictionary.cambridge.org/zht/詞典/英語-漢語-繁體/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(cambridgeDictionaryUrl);
                break;
            case "WordReference EN-CH":
                String wordReferenceEnChDictionaryUrl= "https://www.wordreference.com/enzh/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(wordReferenceEnChDictionaryUrl);
                break;
            case "WordReference CH-EN":
                String wordReferenceChEnDictionaryUrl= "https://www.wordreference.com/zhen/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(wordReferenceChEnDictionaryUrl);
                break;
            case "Merriam Webster":
                String merriamDictionaryUrl= "https://www.merriam-webster.com/dictionary/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(merriamDictionaryUrl);
                break;
            case "Macmillan Dictionary":
                String macmillanDictionaryUrl= "https://www.macmillandictionary.com/dictionary/british/"+ MainActivity.searchKeyword+"_1";
                comboSearchWebViewBrowser2.loadUrl(macmillanDictionaryUrl);
                break;
            case "Collins":
                String collinsDictionaryUrl= "https://www.collinsdictionary.com/dictionary/english/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(collinsDictionaryUrl);
                break;
            case "Oxford":
                String oxfordDictionaryUrl= "https://en.oxforddictionaries.com/definition/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(oxfordDictionaryUrl);
                break;
            case "Vocabulary":
                String vocabularyDotComUrl= "https://www.vocabulary.com/dictionary/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(vocabularyDotComUrl);
                break;
            case "Dictionary.com":
                String dictionaryDotComUrl= "https://www.dictionary.com/browse/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(dictionaryDotComUrl);
                break;
            case "The Free Dictionary":
                String theFreeDictionaryUrl= "https://www.thefreedictionary.com/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(theFreeDictionaryUrl);
                break;
            case "Fine Dictionary":
                String fineDictionaryUrl= "http://www.finedictionary.com/"+ MainActivity.searchKeyword+".html";
                comboSearchWebViewBrowser2.loadUrl(fineDictionaryUrl);
                break;
            case "Your_Dictionary":
                String yourDictionaryUrl= "https://www.yourdictionary.com/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(yourDictionaryUrl);
                break;
            case "Longman Dictionary":
                String longmanDictionaryUrl= "https://www.ldoceonline.com/dictionary/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(longmanDictionaryUrl);
                break;
            case "American Heritage Dictionary":
                String americanHeritageDictionaryUrl= "https://ahdictionary.com/word/search.html?q="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(americanHeritageDictionaryUrl);
                break;
            case "Word Sense":
                String wordSenseDictionaryUrl= "https://www.wordsense.eu/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(wordSenseDictionaryUrl);
                break;
            case "WordWeb":
                String wordWebUrl= "https://www.wordwebonline.com/search.pl?w="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(wordWebUrl);
                break;
            case "WordNik":
                String wordNikUrl= "https://www.wordnik.com/words/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(wordNikUrl);
                break;
            case "Wiki Dictionary":
                String wiktionaryUrl= "https://en.wiktionary.org/wiki/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(wiktionaryUrl);
                break;
            case "Business Dictionary":
                String businessDictionaryUrl= "http://www.businessdictionary.com/definition/"+ MainActivity.searchKeyword+".html";
                comboSearchWebViewBrowser2.loadUrl(businessDictionaryUrl);
                break;
            case "Idioms and Slang":
                String idiomsAndSlangDictionary= "https://www.idiomsandslang.com/?s="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(idiomsAndSlangDictionary);
                break;
            case "Slang":
                String slangDictionary= "http://www.yiym.com/?s="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(slangDictionary);
                break;
            case "The Online Slang Dictionary":
                String theOnlineSlangDictionaryUrl= "http://onlineslangdictionary.com/search/?q="+ MainActivity.searchKeyword+"&sa=Search";
                comboSearchWebViewBrowser2.loadUrl(theOnlineSlangDictionaryUrl);
                break;
            case "Idioms 4 You":
                String idioms4YouUrl= "http://www.idioms4you.com/tipsearch/search.html?q="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(idioms4YouUrl);
                break;
            case "Greens dictionary of slang":
                String greensDictionaryOfSlangUrl= "https://greensdictofslang.com/search/basic?q="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(greensDictionaryOfSlangUrl);
                break;
            case "The Idioms":
                String theIdiomsUrl= "https://www.theidioms.com/?s="+MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(theIdiomsUrl);
                break;
            case "Etymology Dictionary":
                String etymologyDictionaryUrl= "https://www.etymonline.com/search?q="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(etymologyDictionaryUrl);
                break;
            case "TechDico":
                String TechDicoUrl= "https://www.techdico.com/translation/english-chinese/"+ MainActivity.searchKeyword+".html";
                comboSearchWebViewBrowser2.loadUrl(TechDicoUrl);
                break;
            case "BioMedical Dictionary":
                String BioMedicalDictionaryUrl= "http://dict.bioon.com/search.asp?txtitle="+ MainActivity.searchKeyword+"&searchButton=查词典&matchtype=0";
                comboSearchWebViewBrowser2.loadUrl(BioMedicalDictionaryUrl);
                break;
            case "Buddhism Dictionary":
                //這邊設置AlertDialog提醒用戶24小時內只能查10字，輸入"guest"登入使用
                CFAlertDialog.Builder logInToUseBuddhismDictionaryAlertDialogBuilder = new CFAlertDialog.Builder(ComboSearchActivity.this)
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                        .setDialogBackgroundColor(Color.parseColor("#fafcd7"))
                        .setCornerRadius(50)
                        .setTitle(getResources().getString(R.string.Buddhism_dictionary_explanation))
                        .setTextColor(Color.BLUE)
                        .setCancelable(false) //按到旁邊的空白處AlertDialog也不會消失
                        .setHeaderView(R.layout.custom_alert_diaglog_question_mark)

                        //AlertDialog的確定鈕，查佛學字典
                        .addButton(getResources().getString(R.string.Confirm)
                                , Color.BLACK, Color.GREEN, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {

                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://buddhism-dict.net/cgi-bin/xpr-ddb.pl?q="+ MainActivity.searchKeyword))); //Fail-safe for when Buddhism Dictionary fails to render in the webView.
                                                                                                    //String buddhismDictionaryDictionaryUrl= "http://buddhism-dict.net/cgi-bin/xpr-ddb.pl?q="+searchKeyword;
                                                                                                    //comboSearchWebViewBrowser2.loadUrl(buddhismDictionaryDictionaryUrl);
                                    dialog.dismiss();
                                })

                        //AlertDialog的取消鈕
                        .addButton(getResources().getString(R.string.Cancel)
                                , Color.BLACK, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {

                                    dialog.dismiss();
                                })

                        //AlertDialog的中立鈕，看教學
                        .addButton(getString(R.string.Watch_tutorial)
                                , Color.BLACK, Color.LTGRAY, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {

                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/M0k8TnEMHY0")));
                                    dialog.dismiss();
                                });

                //把AlertDialog顯示出來
                logInToUseBuddhismDictionaryAlertDialogBuilder.create().show();
                break;
            case "IsPlural Dictionary":
                String IsPluralDictionaryUrl= "https://www.isplural.com/plural_singular/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(IsPluralDictionaryUrl);
                break;
            case "LingoHelpPrepositions":
                String lingoHelpPrepositionsUrl= "https://lingohelp.me/q/?w="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(lingoHelpPrepositionsUrl);
                break;
            case "Power Thesaurus Synonym":
                String powerThesaurusSynonymUrl= "https://www.powerthesaurus.org/"+ MainActivity.searchKeyword+"/synonyms";
                comboSearchWebViewBrowser2.loadUrl(powerThesaurusSynonymUrl);
                break;
            case "Power Thesaurus Antonym":
                String powerThesaurusAntonymsUrl= "https://www.powerthesaurus.org/"+ MainActivity.searchKeyword+"/antonyms";
                comboSearchWebViewBrowser2.loadUrl(powerThesaurusAntonymsUrl);
                break;
            case "Word Hippo":
                String wordHippoUrl= "https://www.wordhippo.com/what-is/another-word-for/"+ MainActivity.searchKeyword+".html";
                comboSearchWebViewBrowser2.loadUrl(wordHippoUrl);
                break;
            case "Onelook":
                String onelookUrl= "https://www.onelook.com/thesaurus/?s="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(onelookUrl);
                break;
            case "ozdic collocation":
                String ozdicCollocationUrl= "http://www.ozdic.com/collocation-dictionary/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(ozdicCollocationUrl);
                break;
            case "Stack Exchange English Learners":
                String StackExchangeEnglishLearnersUrl= "https://ell.stackexchange.com/search?q="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(StackExchangeEnglishLearnersUrl);
                break;
            case "Stack Exchange English Language and Usage":
                String StackExchangeEnglishLanguageAndUsageUrl= "https://english.stackexchange.com/search?q="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(StackExchangeEnglishLanguageAndUsageUrl);
                break;
            case "Weblio JP":
                String weblioJPUrl= "https://www.weblio.jp/content/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(weblioJPUrl);
                break;
            case "Weblio CN":
                String weblioCHUrl= "https://cjjc.weblio.jp/content/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(weblioCHUrl);
                break;
            case "Weblio EN":
                String weblioENUrl= "https://ejje.weblio.jp/content/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(weblioENUrl);
                break;
            case "Weblio Synonym":
                String weblioThesaurusUrl= "https://thesaurus.weblio.jp/content/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(weblioThesaurusUrl);
                break;
            case "Tangorin Word":
                String tangorinDictionaryUrl= "https://tangorin.com/words?search="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(tangorinDictionaryUrl);
                break;
            case "Tangorin Kanji":
                String tangorinKanjiUrl= "https://tangorin.com/kanji?search="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(tangorinKanjiUrl);
                break;
            case "Tangorin Names":
                String tangorinNamesUrl= "https://tangorin.com/names?search="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(tangorinNamesUrl);
                break;
            case "Tangorin Sentence":
                String tangorinSentencesUrl= "https://tangorin.com/sentences?search="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(tangorinSentencesUrl);
                break;
            case "DA JP-TW Dictionary":
                String DaJPtoCHDictionaryUrl= "http://dict.asia/jc/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(DaJPtoCHDictionaryUrl);
                break;
            case "DA TW-JP Dictionary":
                String DaCHtoJPDictionaryUrl= "http://dict.asia/cj/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(DaCHtoJPDictionaryUrl);
                break;
            case "Goo":
                String gooDictionaryUrl= "https://dictionary.goo.ne.jp/srch/jn/"+ MainActivity.searchKeyword+"/m0u/";
                comboSearchWebViewBrowser2.loadUrl(gooDictionaryUrl);
                break;
                                                                                                    //case "Sanseido":
                                                                                                    //    String sanseidoUrl= "http://www.sanseido.biz/sp/Search?target_words="+ MainActivity.searchKeyword+"&search_type=0&start_index=0&selected_dic=";
                                                                                                    //    comboSearchWebViewBrowser2.loadUrl(sanseidoUrl);
                                                                                                    //    break;
            case "Kotoba Bank":
                String kotobank= "https://kotobank.jp/word/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(kotobank);
                break;
            case "J Logos":
                String jlogosUrl= "http://s.jlogos.com/list.html?keyword="+ MainActivity.searchKeyword+"&opt_val=0";
                comboSearchWebViewBrowser2.loadUrl(jlogosUrl);
                break;
            case "Eijirou":
                String eijiroDictionryUrl= "https://eow.alc.co.jp/sp/search.html?q="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(eijiroDictionryUrl);
                break;
            case "How do you say this in English":
                String whatIsItInEnglishUrl= "https://eikaiwa.dmm.com/uknow/search/?keyword="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(whatIsItInEnglishUrl);
                break;
            case "Jisho":
                String jishoUrl= "https://jisho.org/search/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(jishoUrl);
                break;
            case "Cambridge JP-EN":
                String CambridgeJPtoENUrl= "https://dictionary.cambridge.org/zht/詞典/japanese-english/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(CambridgeJPtoENUrl);
                break;
            case "Cambridge EN-JP":
                String CambridgeENtoJPUrl= "https://dictionary.cambridge.org/zht/詞典/英語-日語/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(CambridgeENtoJPUrl);
                break;
            case "WWW JDIC JP-EN":
                String wwwjdicJpToEnUrl= "http://www.edrdg.org/cgi-bin/wwwjdic/wwwjdic?1MUJ"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(wwwjdicJpToEnUrl);
                break;
            case "WWW JDIC EN-JP":
                String wwwjdicEnToJaUrl= "http://www.edrdg.org/cgi-bin/wwwjdic/wwwjdic?1MDE"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(wwwjdicEnToJaUrl);
                break;
            case "WordReference EN-JP":
                String wordReferenceEnJpDictionaryUrl= "https://www.wordreference.com/enja/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(wordReferenceEnJpDictionaryUrl);
                break;
            case "WordReference JP-EN":
                String wordReferenceJpEnDictionaryUrl= "https://www.wordreference.com/jaen/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(wordReferenceJpEnDictionaryUrl);
                break;
            case "RomajiDesu JP-EN EN-JP":
                String RomajiDesuJpEnEnJpDictionaryUrl= "http://m.romajidesu.com/dictionary/meaning-of-"+ MainActivity.searchKeyword+".html";
                comboSearchWebViewBrowser2.loadUrl(RomajiDesuJpEnEnJpDictionaryUrl);
                break;
            case "RomajiDesu Kanji":
                String RomajiDesuKanjiDictionaryUrl= "http://m.romajidesu.com/kanji/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(RomajiDesuKanjiDictionaryUrl);
                break;
            case "JapanDict":
                String JapanDictDictionaryUrl= "https://www.japandict.com/?s="+ MainActivity.searchKeyword+"&lang=eng";
                comboSearchWebViewBrowser2.loadUrl(JapanDictDictionaryUrl);
                break;
            case "JapanDict Kanji":
                String JapanDictKanjiDictionaryUrl= "https://www.japandict.com/kanji/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(JapanDictKanjiDictionaryUrl);
                break;
            case "Japanese Name Dictionary":
                String JapaneseNameDictionaryUrl= "https://kanji.reader.bz/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(JapaneseNameDictionaryUrl);
                break;
            case "Stack Exchange Japanese Language":
                String StackExchangeJapaneseLanguageUrl= "https://japanese.stackexchange.com/search?q="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(StackExchangeJapaneseLanguageUrl);
                break;
            case "Metapedia":
                String MetapediaUrl= "http://metapedia.jp/?q="+MainActivity.searchKeyword+"#gsc.tab=0&gsc.q="+MainActivity.searchKeyword+"&gsc.page=1";
                comboSearchWebViewBrowser2.loadUrl(MetapediaUrl);
                break;
                                                                                                        //            case "Word Plus Chinese":
                                                                                                        //                String googlePlusChinese= "http://www.google.com/search?q="+searchKeyword+"+中文";
                                                                                                        //                comboSearchWebViewBrowser2.loadUrl(googlePlusChinese);
                                                                                                        //                break;
                                                                                                        //            case "Word Plus English 1":
                                                                                                        //                String googlePlusENglish1= "http://www.google.com/search?q="+searchKeyword+"+英文";
                                                                                                        //                comboSearchWebViewBrowser2.loadUrl(googlePlusENglish1);
                                                                                                        //                break;
                                                                                                        //            case "Word Plus English 2":
                                                                                                        //                String googlePlusENglish2= "http://www.google.com/search?q="+searchKeyword+"+英語";
                                                                                                        //                comboSearchWebViewBrowser2.loadUrl(googlePlusENglish2);
                                                                                                        //                break;
                                                                                                        //            case "Word Plus Translation":
                                                                                                        //                String googlePlusTranslation= "http://www.google.com/search?q="+searchKeyword+"+翻譯";
                                                                                                        //                comboSearchWebViewBrowser2.loadUrl(googlePlusTranslation);
                                                                                                        //                break;
                                                                                                        //            case "Word Plus Japanese 1":
                                                                                                        //                String googlePlusJapanese1= "http://www.google.com/search?q="+searchKeyword+"+日文";
                                                                                                        //                comboSearchWebViewBrowser2.loadUrl(googlePlusJapanese1);
                                                                                                        //                break;
                                                                                                        //            case "Word Plus Japanese 2":
                                                                                                        //                String googlePlusJapanese2= "http://www.google.com/search?q="+searchKeyword+"+日語";
                                                                                                        //                comboSearchWebViewBrowser2.loadUrl(googlePlusJapanese2);
                                                                                                        //                break;
                                                                                                        //            case "Word Plus Japanese 3":
                                                                                                        //                String googlePlusJapanese3= "http://www.google.com/search?q="+searchKeyword+"+日本語";
                                                                                                        //                comboSearchWebViewBrowser2.loadUrl(googlePlusJapanese3);
                                                                                                        //                break;
                                                                                                        //            case "Word Plus Meaning 1":
                                                                                                        //                String googlePlusMeaning1= "http://www.google.com/search?q="+searchKeyword+"+意思";
                                                                                                        //                comboSearchWebViewBrowser2.loadUrl(googlePlusMeaning1);
                                                                                                        //                break;
                                                                                                        //            case "Word Plus Meaning 2":
                                                                                                        //                String googlePlusMeaning2 = "http://www.google.com/search?q="+searchKeyword+"+meaning";
                                                                                                        //                comboSearchWebViewBrowser2.loadUrl(googlePlusMeaning2);
                                                                                                        //                break;
            case "Google translate to CHTW":
                String GoogleTranslateToCHTWUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=zh-TW&text="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(GoogleTranslateToCHTWUrl);
                break;
            case "Google translate to CHCN":
                String GoogleTranslateToCHCNUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=zh-CN&text="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(GoogleTranslateToCHCNUrl);
                break;
            case "Google translate to EN":
                String GoogleTranslateToENUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=en&text="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(GoogleTranslateToENUrl);
                break;
            case "Google translate to JP":
                String GoogleTranslateToJPUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=ja&text="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(GoogleTranslateToJPUrl);
                break;
            case "Google translate to KR":
                String GoogleTranslateToKRUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=ko&text="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(GoogleTranslateToKRUrl);
                break;
            case "Google translate to SP":
                String GoogleTranslateToSPUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=es&text="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(GoogleTranslateToSPUrl);
                break;
            case "Google Image":
                String imageSearchUrl= "http://images.google.com/search?tbm=isch&q="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(imageSearchUrl);
                break;
            case "Ludwig":
                String ludwigUrl= "https://ludwig.guru/s/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(ludwigUrl);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ludwig.guru/s/"+ MainActivity.searchKeyword))); //Fail-safe for when the Ludwig fails to render in the webView.
                Toast.makeText(getApplicationContext(),R.string.Technical_difficulty_in_rendering_web_links,Toast.LENGTH_LONG).show();
                break;
            case "Search Sentences":
                String searchSentencesUrl= "https://searchsentences.com/words/"+ MainActivity.searchKeyword+"-in-a-sentence";
                comboSearchWebViewBrowser2.loadUrl(searchSentencesUrl);
                break;
            case "Your Dictionary Example Sentences":
                String yourDictionarySentenceUrl= "https://sentence.yourdictionary.com/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(yourDictionarySentenceUrl);
                break;
            case "YouGlish":
                String youglishUrl= "https://youglish.com/search/"+ MainActivity.searchKeyword+"/all?";
                comboSearchWebViewBrowser2.loadUrl(youglishUrl);
                break;
            case "Word Cool EN-CH":
                String jukuuUrlCHEN= "http://www.jukuu.com/search.php?q="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(jukuuUrlCHEN);
                break;
            case "Word Cool EN-JP":
                String jukuuUrlJPEN= "http://www.jukuu.com/jsearch.php?q="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(jukuuUrlJPEN);
                break;
            case "Word Cool JP-CH":
                String jukuuUrlCHJP= "http://www.jukuu.com/jcsearch.php?q="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(jukuuUrlCHJP);
                break;
            case "Linguee CH-EN":
                String LingueeUrlCHEN= "https://cn.linguee.com/中文-英语/search?source=auto&query="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(LingueeUrlCHEN);
                break;
            case "Linguee JP-EN":
                String LingueeUrlJPEN= "https://www.linguee.jp/日本語-英語/search?source=auto&query="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(LingueeUrlJPEN);
                break;
            case "Wikipedia TW":
                String wikipediaTWUrl= "https://zh.wikipedia.org/wiki/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(wikipediaTWUrl);
                break;
            case "Wikipedia EN":
                String wikipediaENUrl= "https://en.wikipedia.org/wiki/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(wikipediaENUrl);
                break;
            case "English Encyclopedia":
                String enEncyclopediaUrl= "https://www.encyclo.co.uk/meaning-of-"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(enEncyclopediaUrl);
                break;
            case "Forvo":
                String forvoUrl= "https://zh.forvo.com/search/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(forvoUrl);
                break;
            case "Difference Between":
                String differenceBetweenUrl= "http://www.differencebetween.net/search/?cx=partner-pub-1911891147296207%3Aw80z4hjpu14&cof=FORID%3A9&ie=ISO-8859-1&q="+ MainActivity.searchKeyword+"&sa=Search";
                comboSearchWebViewBrowser2.loadUrl(differenceBetweenUrl);
                break;
            case "Net Speak":
                String netspeakUrl= "https://netspeak.org/#q="+ MainActivity.searchKeyword+"&corpus=web-en";
                comboSearchWebViewBrowser2.loadUrl(netspeakUrl);
                break;
            case "Just the Word":
                String justTheWordUrl= "http://www.just-the-word.com/main.pl?word="+ MainActivity.searchKeyword+"+&mode=combinations";
                comboSearchWebViewBrowser2.loadUrl(justTheWordUrl);
                break;
            case "Yomikata":
                String yomikatawaUrl= "https://yomikatawa.com/kanji/"+ MainActivity.searchKeyword+"?search=1";
                comboSearchWebViewBrowser2.loadUrl(yomikatawaUrl);
                break;
            case "Chigai":
                String ChigaihaUrl= "https://cse.google.co.jp/cse?cx=partner-pub-1137871985589263%3A3025760782&ie=UTF-8&q="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(ChigaihaUrl);
                break;
            case "OJAD":
                String suzukikunUrl= "http://www.gavo.t.u-tokyo.ac.jp/ojad/search/index/sortprefix:accent/narabi1:kata_asc/narabi2:accent_asc/narabi3:mola_asc/yure:visible/curve:invisible/details:invisible/limit:20/word:"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser2.loadUrl(suzukikunUrl);
                break;
        }


        MainActivity.saveKeywordtoUserInputListView ();
        saveUserInputArrayListToSharedPreferences ();
    }



    public void loadThirdDefaultDictionaries() {

        MainActivity.searchKeyword= MainActivity.wordInputView.getText().toString(); //抓用戶輸入的關鍵字

        switch(MainActivity.defaultComboSearchCodeThirdDictionary){
            case "Yahoo Dictionary":
                String yahooDictionaryUrl= "https://tw.dictionary.search.yahoo.com/search;_ylt=AwrtXGoL8vtcAQoAnHV9rolQ;_ylc=X1MDMTM1MTIwMDM4MQRfcgMyBGZyA3NmcARncHJpZAN0RjJnMS51MlNWU3NDZ1pfVC4zNUFBBG5fcnNsdAMwBG5fc3VnZwM0BG9yaWdpbgN0dy5kaWN0aW9uYXJ5LnNlYXJjaC55YWhvby5jb20EcG9zAzAEcHFzdHIDBHBxc3RybAMEcXN0cmwDMwRxdWVyeQNHQVkEdF9zdG1wAzE1NjAwMTU0MTE-?p="+ MainActivity.searchKeyword+"&fr2=sb-top-tw.dictionary.search&fr=sfp";
                comboSearchWebViewBrowser3.loadUrl(yahooDictionaryUrl);
                break;
            case "National Academy for Educational Research":
                String naerUrl= "http://terms.naer.edu.tw/m/search/?q="+ MainActivity.searchKeyword+"&field=text&op=AND&page=";
                comboSearchWebViewBrowser3.loadUrl(naerUrl);
                break;
            case "Dict Site":
                String dictDotSiteUrl= "http://dict.site/"+ MainActivity.searchKeyword+".html";
                comboSearchWebViewBrowser3.loadUrl(dictDotSiteUrl);
                break;
            case "Fast Dict":
                String fastDictUrl= "http://www.fastdict.net/hongkong/word.html?word="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(fastDictUrl);
                break;
            case "Dict CN":
                String DictDotCnUrl= "http://dict.cn/big5/"+ MainActivity.searchKeyword;;
                comboSearchWebViewBrowser3.loadUrl(DictDotCnUrl);
                break;
            case "Google Dictionary":
                String googleDictionaryUrl= "http://gdictchinese.freecollocation.com/search/?q="+ MainActivity.searchKeyword;;
                comboSearchWebViewBrowser1.loadUrl(googleDictionaryUrl);
                break;
            case "VoiceTube":
                String voicetubeUrl= "https://tw.voicetube.com/definition/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(voicetubeUrl);
                break;
            case "Cambridge EN-CH":
                String cambridgeDictionaryUrl= "https://dictionary.cambridge.org/zht/詞典/英語-漢語-繁體/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(cambridgeDictionaryUrl);
                break;
            case "WordReference EN-CH":
                String wordReferenceEnChDictionaryUrl= "https://www.wordreference.com/enzh/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(wordReferenceEnChDictionaryUrl);
                break;
            case "WordReference CH-EN":
                String wordReferenceChEnDictionaryUrl= "https://www.wordreference.com/zhen/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(wordReferenceChEnDictionaryUrl);
                break;
            case "Merriam Webster":
                String merriamDictionaryUrl= "https://www.merriam-webster.com/dictionary/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(merriamDictionaryUrl);
                break;
            case "Macmillan Dictionary":
                String macmillanDictionaryUrl= "https://www.macmillandictionary.com/dictionary/british/"+ MainActivity.searchKeyword+"_1";
                comboSearchWebViewBrowser3.loadUrl(macmillanDictionaryUrl);
                break;
            case "Collins":
                String collinsDictionaryUrl= "https://www.collinsdictionary.com/dictionary/english/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(collinsDictionaryUrl);
                break;
            case "Oxford":
                String oxfordDictionaryUrl= "https://en.oxforddictionaries.com/definition/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(oxfordDictionaryUrl);
                break;
            case "Vocabulary":
                String vocabularyDotComUrl= "https://www.vocabulary.com/dictionary/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(vocabularyDotComUrl);
                break;
            case "Dictionary.com":
                String dictionaryDotComUrl= "https://www.dictionary.com/browse/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(dictionaryDotComUrl);
                break;
            case "The Free Dictionary":
                String theFreeDictionaryUrl= "https://www.thefreedictionary.com/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(theFreeDictionaryUrl);
                break;
            case "Fine Dictionary":
                String fineDictionaryUrl= "http://www.finedictionary.com/"+ MainActivity.searchKeyword+".html";
                comboSearchWebViewBrowser3.loadUrl(fineDictionaryUrl);
                break;
            case "Your_Dictionary":
                String yourDictionaryUrl= "https://www.yourdictionary.com/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(yourDictionaryUrl);
                break;
            case "Longman Dictionary":
                String longmanDictionaryUrl= "https://www.ldoceonline.com/dictionary/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(longmanDictionaryUrl);
                break;
            case "American Heritage Dictionary":
                String americanHeritageDictionaryUrl= "https://ahdictionary.com/word/search.html?q="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(americanHeritageDictionaryUrl);
                break;
            case "Word Sense":
                String wordSenseDictionaryUrl= "https://www.wordsense.eu/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(wordSenseDictionaryUrl);
                break;
            case "WordWeb":
                String wordWebUrl= "https://www.wordwebonline.com/search.pl?w="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(wordWebUrl);
                break;
            case "WordNik":
                String wordNikUrl= "https://www.wordnik.com/words/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(wordNikUrl);
                break;
            case "Wiki Dictionary":
                String wiktionaryUrl= "https://en.wiktionary.org/wiki/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(wiktionaryUrl);
                break;
            case "Business Dictionary":
                String businessDictionaryUrl= "http://www.businessdictionary.com/definition/"+ MainActivity.searchKeyword+".html";
                comboSearchWebViewBrowser3.loadUrl(businessDictionaryUrl);
                break;
            case "Idioms and Slang":
                String idiomsAndSlangDictionary= "https://www.idiomsandslang.com/?s="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(idiomsAndSlangDictionary);
                break;
            case "Slang":
                String slangDictionary= "http://www.yiym.com/?s="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(slangDictionary);
                break;
            case "The Online Slang Dictionary":
                String theOnlineSlangDictionaryUrl= "http://onlineslangdictionary.com/search/?q="+ MainActivity.searchKeyword+"&sa=Search";
                comboSearchWebViewBrowser3.loadUrl(theOnlineSlangDictionaryUrl);
                break;
            case "Idioms 4 You":
                String idioms4YouUrl= "http://www.idioms4you.com/tipsearch/search.html?q="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(idioms4YouUrl);
                break;
            case "Greens dictionary of slang":
                String greensDictionaryOfSlangUrl= "https://greensdictofslang.com/search/basic?q="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(greensDictionaryOfSlangUrl);
                break;
            case "The Idioms":
                String theIdiomsUrl= "https://www.theidioms.com/?s="+MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(theIdiomsUrl);
                break;
            case "Etymology Dictionary":
                String etymologyDictionaryUrl= "https://www.etymonline.com/search?q="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(etymologyDictionaryUrl);
                break;
            case "TechDico":
                String TechDicoUrl= "https://www.techdico.com/translation/english-chinese/"+ MainActivity.searchKeyword+".html";
                comboSearchWebViewBrowser3.loadUrl(TechDicoUrl);
                break;
            case "BioMedical Dictionary":
                String BioMedicalDictionaryUrl= "http://dict.bioon.com/search.asp?txtitle="+ MainActivity.searchKeyword+"&searchButton=查词典&matchtype=0";
                comboSearchWebViewBrowser3.loadUrl(BioMedicalDictionaryUrl);
                break;
            case "Buddhism Dictionary":
                //這邊設置AlertDialog提醒用戶24小時內只能查10字，輸入"guest"登入使用
                CFAlertDialog.Builder logInToUseBuddhismDictionaryAlertDialogBuilder = new CFAlertDialog.Builder(ComboSearchActivity.this)
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                        .setDialogBackgroundColor(Color.parseColor("#fafcd7"))
                        .setCornerRadius(50)
                        .setTitle(getResources().getString(R.string.Buddhism_dictionary_explanation))
                        .setTextColor(Color.BLUE)
                        .setCancelable(false) //按到旁邊的空白處AlertDialog也不會消失
                        .setHeaderView(R.layout.custom_alert_diaglog_question_mark)

                        //AlertDialog的確定鈕，查佛學字典
                        .addButton(getResources().getString(R.string.Confirm)
                                , Color.BLACK, Color.GREEN, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {

                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://buddhism-dict.net/cgi-bin/xpr-ddb.pl?q="+ MainActivity.searchKeyword))); //Fail-safe for when Buddhism Dictionary fails to render in the webView.
                                                                                                    //String buddhismDictionaryDictionaryUrl= "http://buddhism-dict.net/cgi-bin/xpr-ddb.pl?q="+searchKeyword;
                                                                                                    //comboSearchWebViewBrowser3.loadUrl(buddhismDictionaryDictionaryUrl);
                                    dialog.dismiss();
                                })

                        //AlertDialog的取消鈕
                        .addButton(getResources().getString(R.string.Cancel)
                                , Color.BLACK, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {

                                    dialog.dismiss();
                                })

                        //AlertDialog的中立鈕，看教學
                        .addButton(getString(R.string.Watch_tutorial)
                                , Color.BLACK, Color.LTGRAY, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {

                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/M0k8TnEMHY0")));
                                    dialog.dismiss();
                                });

                //把AlertDialog顯示出來
                logInToUseBuddhismDictionaryAlertDialogBuilder.create().show();
                break;
            case "IsPlural Dictionary":
                String IsPluralDictionaryUrl= "https://www.isplural.com/plural_singular/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(IsPluralDictionaryUrl);
                break;
            case "LingoHelpPrepositions":
                String lingoHelpPrepositionsUrl= "https://lingohelp.me/q/?w="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(lingoHelpPrepositionsUrl);
                break;
            case "Power Thesaurus Synonym":
                String powerThesaurusSynonymUrl= "https://www.powerthesaurus.org/"+ MainActivity.searchKeyword+"/synonyms";
                comboSearchWebViewBrowser3.loadUrl(powerThesaurusSynonymUrl);
                break;
            case "Power Thesaurus Antonym":
                String powerThesaurusAntonymsUrl= "https://www.powerthesaurus.org/"+ MainActivity.searchKeyword+"/antonyms";
                comboSearchWebViewBrowser3.loadUrl(powerThesaurusAntonymsUrl);
                break;
            case "Word Hippo":
                String wordHippoUrl= "https://www.wordhippo.com/what-is/another-word-for/"+ MainActivity.searchKeyword+".html";
                comboSearchWebViewBrowser3.loadUrl(wordHippoUrl);
                break;
            case "Onelook":
                String onelookUrl= "https://www.onelook.com/thesaurus/?s="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(onelookUrl);
                break;
            case "ozdic collocation":
                String ozdicCollocationUrl= "http://www.ozdic.com/collocation-dictionary/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(ozdicCollocationUrl);
                break;
            case "Stack Exchange English Learners":
                String StackExchangeEnglishLearnersUrl= "https://ell.stackexchange.com/search?q="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(StackExchangeEnglishLearnersUrl);
                break;
            case "Stack Exchange English Language and Usage":
                String StackExchangeEnglishLanguageAndUsageUrl= "https://english.stackexchange.com/search?q="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(StackExchangeEnglishLanguageAndUsageUrl);
                break;
            case "Weblio JP":
                String weblioJPUrl= "https://www.weblio.jp/content/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(weblioJPUrl);
                break;
            case "Weblio CN":
                String weblioCHUrl= "https://cjjc.weblio.jp/content/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(weblioCHUrl);
                break;
            case "Weblio EN":
                String weblioENUrl= "https://ejje.weblio.jp/content/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(weblioENUrl);
                break;
            case "Weblio Synonym":
                String weblioThesaurusUrl= "https://thesaurus.weblio.jp/content/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(weblioThesaurusUrl);
                break;
            case "Tangorin Word":
                String tangorinDictionaryUrl= "https://tangorin.com/words?search="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(tangorinDictionaryUrl);
                break;
            case "Tangorin Kanji":
                String tangorinKanjiUrl= "https://tangorin.com/kanji?search="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(tangorinKanjiUrl);
                break;
            case "Tangorin Names":
                String tangorinNamesUrl= "https://tangorin.com/names?search="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(tangorinNamesUrl);
                break;
            case "Tangorin Sentence":
                String tangorinSentencesUrl= "https://tangorin.com/sentences?search="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(tangorinSentencesUrl);
                break;
            case "DA JP-TW Dictionary":
                String DaJPtoCHDictionaryUrl= "http://dict.asia/jc/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(DaJPtoCHDictionaryUrl);
                break;
            case "DA TW-JP Dictionary":
                String DaCHtoJPDictionaryUrl= "http://dict.asia/cj/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(DaCHtoJPDictionaryUrl);
                break;
            case "Goo":
                String gooDictionaryUrl= "https://dictionary.goo.ne.jp/srch/jn/"+ MainActivity.searchKeyword+"/m0u/";
                comboSearchWebViewBrowser3.loadUrl(gooDictionaryUrl);
                break;
                                                                                                    //case "Sanseido":
                                                                                                    //    String sanseidoUrl= "http://www.sanseido.biz/sp/Search?target_words="+ MainActivity.searchKeyword+"&search_type=0&start_index=0&selected_dic=";
                                                                                                    //    comboSearchWebViewBrowser3.loadUrl(sanseidoUrl);
                                                                                                    //    break;
            case "Kotoba Bank":
                String kotobank= "https://kotobank.jp/word/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(kotobank);
                break;
            case "J Logos":
                String jlogosUrl= "http://s.jlogos.com/list.html?keyword="+ MainActivity.searchKeyword+"&opt_val=0";
                comboSearchWebViewBrowser3.loadUrl(jlogosUrl);
                break;
            case "Eijirou":
                String eijiroDictionryUrl= "https://eow.alc.co.jp/sp/search.html?q="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(eijiroDictionryUrl);
                break;
            case "How do you say this in English":
                String whatIsItInEnglishUrl= "https://eikaiwa.dmm.com/uknow/search/?keyword="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(whatIsItInEnglishUrl);
                break;
            case "Jisho":
                String jishoUrl= "https://jisho.org/search/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(jishoUrl);
                break;
            case "Cambridge JP-EN":
                String CambridgeJPtoENUrl= "https://dictionary.cambridge.org/zht/詞典/japanese-english/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(CambridgeJPtoENUrl);
                break;
            case "Cambridge EN-JP":
                String CambridgeENtoJPUrl= "https://dictionary.cambridge.org/zht/詞典/英語-日語/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(CambridgeENtoJPUrl);
                break;
            case "WWW JDIC JP-EN":
                String wwwjdicJpToEnUrl= "http://www.edrdg.org/cgi-bin/wwwjdic/wwwjdic?1MUJ"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(wwwjdicJpToEnUrl);
                break;
            case "WWW JDIC EN-JP":
                String wwwjdicEnToJaUrl= "http://www.edrdg.org/cgi-bin/wwwjdic/wwwjdic?1MDE"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(wwwjdicEnToJaUrl);
                break;
            case "WordReference EN-JP":
                String wordReferenceEnJpDictionaryUrl= "https://www.wordreference.com/enja/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(wordReferenceEnJpDictionaryUrl);
                break;
            case "WordReference JP-EN":
                String wordReferenceJpEnDictionaryUrl= "https://www.wordreference.com/jaen/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(wordReferenceJpEnDictionaryUrl);
                break;
            case "RomajiDesu JP-EN EN-JP":
                String RomajiDesuJpEnEnJpDictionaryUrl= "http://m.romajidesu.com/dictionary/meaning-of-"+ MainActivity.searchKeyword+".html";
                comboSearchWebViewBrowser3.loadUrl(RomajiDesuJpEnEnJpDictionaryUrl);
                break;
            case "RomajiDesu Kanji":
                String RomajiDesuKanjiDictionaryUrl= "http://m.romajidesu.com/kanji/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(RomajiDesuKanjiDictionaryUrl);
                break;
            case "JapanDict":
                String JapanDictDictionaryUrl= "https://www.japandict.com/?s="+ MainActivity.searchKeyword+"&lang=eng";
                comboSearchWebViewBrowser3.loadUrl(JapanDictDictionaryUrl);
                break;
            case "JapanDict Kanji":
                String JapanDictKanjiDictionaryUrl= "https://www.japandict.com/kanji/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(JapanDictKanjiDictionaryUrl);
                break;
            case "Japanese Name Dictionary":
                String JapaneseNameDictionaryUrl= "https://kanji.reader.bz/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(JapaneseNameDictionaryUrl);
                break;
            case "Stack Exchange Japanese Language":
                String StackExchangeJapaneseLanguageUrl= "https://japanese.stackexchange.com/search?q="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(StackExchangeJapaneseLanguageUrl);
                break;
            case "Metapedia":
                String MetapediaUrl= "http://metapedia.jp/?q="+MainActivity.searchKeyword+"#gsc.tab=0&gsc.q="+MainActivity.searchKeyword+"&gsc.page=1";
                comboSearchWebViewBrowser3.loadUrl(MetapediaUrl);
                break;
                                                                                                        //            case "Word Plus Chinese":
                                                                                                        //                String googlePlusChinese= "http://www.google.com/search?q="+searchKeyword+"+中文";
                                                                                                        //                comboSearchWebViewBrowser3.loadUrl(googlePlusChinese);
                                                                                                        //                break;
                                                                                                        //            case "Word Plus English 1":
                                                                                                        //                String googlePlusENglish1= "http://www.google.com/search?q="+searchKeyword+"+英文";
                                                                                                        //                comboSearchWebViewBrowser3.loadUrl(googlePlusENglish1);
                                                                                                        //                break;
                                                                                                        //            case "Word Plus English 2":
                                                                                                        //                String googlePlusENglish2= "http://www.google.com/search?q="+searchKeyword+"+英語";
                                                                                                        //                comboSearchWebViewBrowser3.loadUrl(googlePlusENglish2);
                                                                                                        //                break;
                                                                                                        //            case "Word Plus Translation":
                                                                                                        //                String googlePlusTranslation= "http://www.google.com/search?q="+searchKeyword+"+翻譯";
                                                                                                        //                comboSearchWebViewBrowser3.loadUrl(googlePlusTranslation);
                                                                                                        //                break;
                                                                                                        //            case "Word Plus Japanese 1":
                                                                                                        //                String googlePlusJapanese1= "http://www.google.com/search?q="+searchKeyword+"+日文";
                                                                                                        //                comboSearchWebViewBrowser3.loadUrl(googlePlusJapanese1);
                                                                                                        //                break;
                                                                                                        //            case "Word Plus Japanese 2":
                                                                                                        //                String googlePlusJapanese2= "http://www.google.com/search?q="+searchKeyword+"+日語";
                                                                                                        //                comboSearchWebViewBrowser3.loadUrl(googlePlusJapanese2);
                                                                                                        //                break;
                                                                                                        //            case "Word Plus Japanese 3":
                                                                                                        //                String googlePlusJapanese3= "http://www.google.com/search?q="+searchKeyword+"+日本語";
                                                                                                        //                comboSearchWebViewBrowser3.loadUrl(googlePlusJapanese3);
                                                                                                        //                break;
                                                                                                        //            case "Word Plus Meaning 1":
                                                                                                        //                String googlePlusMeaning1= "http://www.google.com/search?q="+searchKeyword+"+意思";
                                                                                                        //                comboSearchWebViewBrowser3.loadUrl(googlePlusMeaning1);
                                                                                                        //                break;
                                                                                                        //            case "Word Plus Meaning 2":
                                                                                                        //                String googlePlusMeaning2 = "http://www.google.com/search?q="+searchKeyword+"+meaning";
                                                                                                        //                comboSearchWebViewBrowser3.loadUrl(googlePlusMeaning2);
                                                                                                        //                break;
            case "Google translate to CHTW":
                String GoogleTranslateToCHTWUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=zh-TW&text="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(GoogleTranslateToCHTWUrl);
                break;
            case "Google translate to CHCN":
                String GoogleTranslateToCHCNUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=zh-CN&text="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(GoogleTranslateToCHCNUrl);
                break;
            case "Google translate to EN":
                String GoogleTranslateToENUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=en&text="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(GoogleTranslateToENUrl);
                break;
            case "Google translate to JP":
                String GoogleTranslateToJPUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=ja&text="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(GoogleTranslateToJPUrl);
                break;
            case "Google translate to KR":
                String GoogleTranslateToKRUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=ko&text="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(GoogleTranslateToKRUrl);
                break;
            case "Google translate to SP":
                String GoogleTranslateToSPUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=es&text="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(GoogleTranslateToSPUrl);
                break;
            case "Google Image":
                String imageSearchUrl= "http://images.google.com/search?tbm=isch&q="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(imageSearchUrl);
                break;
            case "Ludwig":
                String ludwigUrl= "https://ludwig.guru/s/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(ludwigUrl);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ludwig.guru/s/"+ MainActivity.searchKeyword))); //Fail-safe for when the Ludwig fails to render in the webView.
                Toast.makeText(getApplicationContext(),R.string.Technical_difficulty_in_rendering_web_links,Toast.LENGTH_LONG).show();
                break;
            case "Search Sentences":
                String searchSentencesUrl= "https://searchsentences.com/words/"+ MainActivity.searchKeyword+"-in-a-sentence";
                comboSearchWebViewBrowser3.loadUrl(searchSentencesUrl);
                break;
            case "Your Dictionary Example Sentences":
                String yourDictionarySentenceUrl= "https://sentence.yourdictionary.com/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(yourDictionarySentenceUrl);
                break;
            case "YouGlish":
                String youglishUrl= "https://youglish.com/search/"+ MainActivity.searchKeyword+"/all?";
                comboSearchWebViewBrowser3.loadUrl(youglishUrl);
                break;
            case "Word Cool EN-CH":
                String jukuuUrlCHEN= "http://www.jukuu.com/search.php?q="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(jukuuUrlCHEN);
                break;
            case "Word Cool EN-JP":
                String jukuuUrlJPEN= "http://www.jukuu.com/jsearch.php?q="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(jukuuUrlJPEN);
                break;
            case "Word Cool JP-CH":
                String jukuuUrlCHJP= "http://www.jukuu.com/jcsearch.php?q="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(jukuuUrlCHJP);
                break;
            case "Linguee CH-EN":
                String LingueeUrlCHEN= "https://cn.linguee.com/中文-英语/search?source=auto&query="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(LingueeUrlCHEN);
                break;
            case "Linguee JP-EN":
                String LingueeUrlJPEN= "https://www.linguee.jp/日本語-英語/search?source=auto&query="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(LingueeUrlJPEN);
                break;
            case "Wikipedia TW":
                String wikipediaTWUrl= "https://zh.wikipedia.org/wiki/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(wikipediaTWUrl);
                break;
            case "Wikipedia EN":
                String wikipediaENUrl= "https://en.wikipedia.org/wiki/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(wikipediaENUrl);
                break;
            case "English Encyclopedia":
                String enEncyclopediaUrl= "https://www.encyclo.co.uk/meaning-of-"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(enEncyclopediaUrl);
                break;
            case "Forvo":
                String forvoUrl= "https://zh.forvo.com/search/"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(forvoUrl);
                break;
            case "Difference Between":
                String differenceBetweenUrl= "http://www.differencebetween.net/search/?cx=partner-pub-1911891147296207%3Aw80z4hjpu14&cof=FORID%3A9&ie=ISO-8859-1&q="+ MainActivity.searchKeyword+"&sa=Search";
                comboSearchWebViewBrowser3.loadUrl(differenceBetweenUrl);
                break;
            case "Net Speak":
                String netspeakUrl= "https://netspeak.org/#q="+ MainActivity.searchKeyword+"&corpus=web-en";
                comboSearchWebViewBrowser3.loadUrl(netspeakUrl);
                break;
            case "Just the Word":
                String justTheWordUrl= "http://www.just-the-word.com/main.pl?word="+ MainActivity.searchKeyword+"+&mode=combinations";
                comboSearchWebViewBrowser3.loadUrl(justTheWordUrl);
                break;
            case "Yomikata":
                String yomikatawaUrl= "https://yomikatawa.com/kanji/"+ MainActivity.searchKeyword+"?search=1";
                comboSearchWebViewBrowser3.loadUrl(yomikatawaUrl);
                break;
            case "Chigai":
                String ChigaihaUrl= "https://cse.google.co.jp/cse?cx=partner-pub-1137871985589263%3A3025760782&ie=UTF-8&q="+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(ChigaihaUrl);
                break;
            case "OJAD":
                String suzukikunUrl= "http://www.gavo.t.u-tokyo.ac.jp/ojad/search/index/sortprefix:accent/narabi1:kata_asc/narabi2:accent_asc/narabi3:mola_asc/yure:visible/curve:invisible/details:invisible/limit:20/word:"+ MainActivity.searchKeyword;
                comboSearchWebViewBrowser3.loadUrl(suzukikunUrl);
                break;
        }


        MainActivity.saveKeywordtoUserInputListView ();
        saveUserInputArrayListToSharedPreferences ();
    }



    /**
     * Helper method for saving UserInputArrayList to SharedPreferences
     */
    public void saveUserInputArrayListToSharedPreferences() {
        SharedPreferences.Editor editor = getSharedPreferences("userInputArrayListSharedPreferences", MODE_PRIVATE).edit();
        editor.putInt("userInputArrayListValues", MainActivity.userInputArraylist.size());
        for (int i = 0; i < MainActivity.userInputArraylist.size(); i++)
        {
            editor.putString("userInputArrayListItem_"+i, MainActivity.userInputArraylist.get(i));
        }
        editor.apply();
    }



}
