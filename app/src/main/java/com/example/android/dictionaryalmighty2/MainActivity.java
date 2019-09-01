package com.example.android.dictionaryalmighty2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import cn.zhaiyifan.rememberedittext.RememberEditText;
import pl.droidsonroids.gif.GifImageView;


public class MainActivity extends AppCompatActivity {

    GifImageView gifImageView; //用來準備給用戶更換背景圖
    ImageView backGroundImageView;
    ImageView browserNavigateBack;
    ImageView browserNavigateForward;
    public static EditText wordInputView;    //關鍵字輸入框
    String searchKeyword;      //用戶輸入的關鍵字
    WebView webViewBrowser;    //網頁框
    Switch browserSwitch;      //網頁框的開關
    ProgressBar progressBar;   //網頁載入的進度條
    TextView searchResultWillBeDisplayedHere;
    private static int RESULT_LOAD_IMAGE = 1;
    private static final int WRITE_PERMISSION = 0x01; //用來準備設置運行中的權限要求
    String LOG_TAG;  //Log tag for the external storage permission request error message
    String speechAutoTranslationCode; //用於載入自動語音翻譯之網頁的代碼
    String changeBackgroundButtonIsPressed; //更換背景時附加的代碼，以免與語音辨識的程式碼衝突
    ImageView ocr;
    public static String tesseract_lang_code;  // The recognition language of tesseract

    File tempOutputFileForBackgroundImage;
    Uri imageForBackground;                                //相簿中的原始圖檔
    Bitmap m_phone_for_background;                           // Bitmap圖像



    @RequiresApi(api = Build.VERSION_CODES.M)  //要加上這條限定Api等級，requestWritePermission()才不會報錯
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestWritePermission();  //在程式運行中要求存取的權限

        gifImageView = findViewById(R.id.GIF_imageView);
        backGroundImageView = findViewById(R.id.background_image_view);
        wordInputView = findViewById(R.id.Word_Input_View);
        searchResultWillBeDisplayedHere = findViewById(R.id.search_result_textView);
        browserNavigateBack = findViewById(R.id.browser_navigate_back_imageView);
        browserNavigateForward = findViewById(R.id.browser_navigate_forward_imageView);


        backGroundImageView.setVisibility(View.GONE);
        browserNavigateBack.setVisibility(View.GONE);
        browserNavigateForward.setVisibility(View.GONE);



        /**
         * 設置背景圖的更換
         */
        final Spinner otherFunctionsSpinner = findViewById(R.id.Other_functions_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> OtherFunctionsSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.Other_functions_spinner_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        OtherFunctionsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        otherFunctionsSpinner.setAdapter(OtherFunctionsSpinnerAdapter);

        otherFunctionsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0){
                    return;

                } else if (position == 1) {
                    changeBackgroundButtonIsPressed="yes";
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, TesseractOpenCVCaptureActivity.IMAGE_UNSPECIFIED);
                    startActivityForResult(intent, TesseractOpenCVCaptureActivity.PHOTOALBUM);

                } else if (position == 2) {
                    RememberEditText.clearCache(MainActivity.this);  //呼叫外掛的RememberEditText功能並清除wordInputView中的用戶搜尋紀錄
                    Toast.makeText(getApplicationContext(), getString(R.string.Clear_search_history_after_app_closed), Toast.LENGTH_LONG).show();

                } else if (position == 3) {
                    //呼叫第三方「日本食物字典」app
                    Intent callJapaneseFoodDcitionaryAppIntent = getPackageManager().getLaunchIntentForPackage("com.st.japanfooddictionaryfree");
                    if (callJapaneseFoodDcitionaryAppIntent != null) {
                        // If the TextScanner app is found, start the app.
                        callJapaneseFoodDcitionaryAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(callJapaneseFoodDcitionaryAppIntent);
                    } else {
                        // Bring user to the market or let them choose an app.
                        callJapaneseFoodDcitionaryAppIntent = new Intent(Intent.ACTION_VIEW);
                        callJapaneseFoodDcitionaryAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        callJapaneseFoodDcitionaryAppIntent.setData(Uri.parse("market://details?id=" + "com.st.japanfooddictionaryfree"));
                        startActivity(callJapaneseFoodDcitionaryAppIntent);
                        Toast.makeText(getApplicationContext(), getString(R.string.Must_get_TextScanner_app), Toast.LENGTH_LONG).show();
                    }

                }

                otherFunctionsSpinner.setAdapter(OtherFunctionsSpinnerAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });




                                                            /* 以下功能廢除不使用了
                                                            // 設置OCR文字辨識
                                                            ocr=findViewById(R.id.ocr_imageView);
                                                            ocr.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    Intent intent = new Intent(MainActivity.this, OcrCaptureActivity.class);
                                                                    startActivity(intent);

                                                                }
                                                            });
                                                            */



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
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webViewBrowser.setWebViewClient(new WebViewClientImpl());
        webViewBrowser.requestFocus();
        //Webview裡面的網頁，如果有input需要輸入，但是點上去卻沒反應，輸入法不出來。這種情況是因為webview沒有獲取焦點。
        //需要在java裡面給webview設置一下requestFocus() 就行了。



        /**
         * 設置網頁框的開關
         */
        browserSwitch = findViewById(R.id.browser_switch);

        browserSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(browserSwitch.isChecked()) {                     //用isChecked()檢視開關的開啟狀態
                    webViewBrowser.setVisibility(View.VISIBLE);
                    browserNavigateBack.setVisibility(View.VISIBLE);
                    browserNavigateForward.setVisibility(View.VISIBLE);
                }
                else {
                    webViewBrowser.setVisibility(View.INVISIBLE);
                    browserNavigateBack.setVisibility(View.INVISIBLE);
                    browserNavigateForward.setVisibility(View.INVISIBLE);
                }
            }
        });



        /**
         * 設置網頁框的返回上一頁與前進下一頁按鈕
         */
        browserNavigateBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(webViewBrowser.canGoBack()) {
                    webViewBrowser.goBack();
                }
            }
        });

        browserNavigateForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(webViewBrowser.canGoForward()) {
                    webViewBrowser.goForward();
                }
            }
        });



        /**
         * 設置接收其他外部App傳來的資料
         */
        //Get the received intent from other apps (例如「文字掃描儀」app)
        Intent received3rdPartyAppIntent = getIntent();

        //Get the action that was used when starting the MainActivity.
        String receivedAction = received3rdPartyAppIntent.getAction();

        //Find out what intent we are dealing with.
        //This will let us determine whether the app has launched from the device menu or from the share list.
        //If the app has been launched with the SEND Action, there will also be a MIME type indicated within the Intent that started it.
        //If the app was launched from the main device menu rather than from a share list, this will be null.
        //If it was launched with the SEND Action, we will be able to use this to handle the received data, tailoring the app response to the type of data received.
        String receivedType = received3rdPartyAppIntent.getType();

        //Make sure it's an action and type we can handle.
        //Now we have 2 possibilities: the app has been launched from the device in the default fashion and is not receiving any incoming data;
        //the app has been launched to share content. Add the conditional statement as below to handle these two scenarios.
        if(receivedAction.equals(Intent.ACTION_SEND)){
            //Content is being shared. Handle received data of the MIME types.
            if(receivedType.startsWith("text/")){
                //Handle sent text
                String intentReceivedText = received3rdPartyAppIntent.getStringExtra(Intent.EXTRA_TEXT);  //Get the received text
                if (intentReceivedText != null) {              //Check we have a string
                    wordInputView.setText(intentReceivedText); //Set the text to the search box in MainActivity
                    //In the meantime, perform auto translation
                    String intentAutoTranslationURL= "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=auto&tl=zh-TW&text="+intentReceivedText;
                    webViewBrowser.loadUrl(intentAutoTranslationURL);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);
                }
            }
        }
        else if(receivedAction.equals(Intent.ACTION_MAIN)){
            //app has been launched directly, not from share list
        }



        /**
         * 設置下拉式選單
         */

        /**
         * OCR Spinner & Spinner Adapters
         */
        final Spinner OCRModeSpinner = findViewById(R.id.OCR_mode_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> OCRModeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.OCR_spinner_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        OCRModeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        OCRModeSpinner.setAdapter(OCRModeSpinnerAdapter);

        OCRModeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0){
                    return;

                }
                else if (position == 1) {
                    //呼叫第三方「文字掃描儀」app
                    Intent callTextScannerAppIntent = getPackageManager().getLaunchIntentForPackage("com.peace.TextScanner");
                    if (callTextScannerAppIntent != null) {
                        // If the TextScanner app is found, start the app.
                        callTextScannerAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(callTextScannerAppIntent);
                    } else {
                        // Bring user to the market or let them choose an app.
                        callTextScannerAppIntent = new Intent(Intent.ACTION_VIEW);
                        callTextScannerAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        callTextScannerAppIntent.setData(Uri.parse("market://details?id=" + "com.peace.TextScanner"));
                        startActivity(callTextScannerAppIntent);
                        Toast.makeText(getApplicationContext(), getString(R.string.Must_get_TextScanner_app), Toast.LENGTH_LONG).show();
                    }

                }
                else if (position == 2) {
                    //呼叫第三方「Google翻譯」app
                    Intent callGgoogleTranslateAppIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.translate");
                    if (callGgoogleTranslateAppIntent != null) {
                        // If the TextScanner app is found, start the app.
                        callGgoogleTranslateAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(callGgoogleTranslateAppIntent);
                    } else {
                        // Bring user to the market or let them choose an app.
                        callGgoogleTranslateAppIntent = new Intent(Intent.ACTION_VIEW);
                        callGgoogleTranslateAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        callGgoogleTranslateAppIntent.setData(Uri.parse("market://details?id=" + "com.google.android.apps.translate"));
                        startActivity(callGgoogleTranslateAppIntent);
                        Toast.makeText(getApplicationContext(), getString(R.string.Must_get_GoogleTranslate_app), Toast.LENGTH_LONG).show();
                    }

                }
                else if (position == 3) {
                    //呼叫第三方「微軟翻譯」app
                    Intent callMicrosoftTranslateAppIntent = getPackageManager().getLaunchIntentForPackage("com.microsoft.translator");
                    if (callMicrosoftTranslateAppIntent != null) {
                        // If the TextScanner app is found, start the app.
                        callMicrosoftTranslateAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(callMicrosoftTranslateAppIntent);
                    } else {
                        // Bring user to the market or let them choose an app.
                        callMicrosoftTranslateAppIntent = new Intent(Intent.ACTION_VIEW);
                        callMicrosoftTranslateAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        callMicrosoftTranslateAppIntent.setData(Uri.parse("market://details?id=" + "com.microsoft.translator"));
                        startActivity(callMicrosoftTranslateAppIntent);
                        Toast.makeText(getApplicationContext(), getString(R.string.Must_get_MicrosoftTranslate_app), Toast.LENGTH_LONG).show();
                    }

                }
                else if (position == 4) {
                    //呼叫第三方「Yomiwa」app
                    Intent callYomiwaAppIntent = getPackageManager().getLaunchIntentForPackage("com.yomiwa.yomiwa");
                    if (callYomiwaAppIntent != null) {
                        // If the TextScanner app is found, start the app.
                        callYomiwaAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(callYomiwaAppIntent);
                    } else {
                        // Bring user to the market or let them choose an app.
                        callYomiwaAppIntent = new Intent(Intent.ACTION_VIEW);
                        callYomiwaAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        callYomiwaAppIntent.setData(Uri.parse("market://details?id=" + "com.yomiwa.yomiwa"));
                        startActivity(callYomiwaAppIntent);
                        Toast.makeText(getApplicationContext(), getString(R.string.Must_get_Yomiwa_app), Toast.LENGTH_LONG).show();
                    }

                }


                                                                /* 以下功能廢除不使用了
                                                                else if (position == 4) {
                                                                    Intent GoogleOCRIntent = new Intent();
                                                                    GoogleOCRIntent.setClass(MainActivity.this, OcrCaptureActivity.class);
                                                                    startActivity(GoogleOCRIntent);

                                                                }else if (position == 5) {
                                                                    tesseract_lang_code="eng";
                                                                    Intent tesscvEnglishIntent = new Intent();
                                                                    tesscvEnglishIntent.setClass(MainActivity.this, TesseractOpenCVCaptureActivity.class);
                                                                    startActivity(tesscvEnglishIntent);

                                                                }else if (position == 6) {
                                                                    tesseract_lang_code = "chi_tra";
                                                                    Intent tesscvCHTWIntent = new Intent();
                                                                    tesscvCHTWIntent.setClass(MainActivity.this, TesseractOpenCVCaptureActivity.class);
                                                                    startActivity(tesscvCHTWIntent);

                                                                }else if (position == 7) {
                                                                    tesseract_lang_code = "chi_sim";
                                                                    Intent tesscvCHCNIntent = new Intent();
                                                                    tesscvCHCNIntent.setClass(MainActivity.this, TesseractOpenCVCaptureActivity.class);
                                                                    startActivity(tesscvCHCNIntent);

                                                                }else if (position == 8) {
                                                                    tesseract_lang_code = "jpn";
                                                                    Intent tesscvJPIntent = new Intent();
                                                                    tesscvJPIntent.setClass(MainActivity.this, TesseractOpenCVCaptureActivity.class);
                                                                    startActivity(tesscvJPIntent);

                                                                }else if (position == 9) {
                                                                    tesseract_lang_code = "kor";
                                                                    Intent tesscvKRIntent = new Intent();
                                                                    tesscvKRIntent.setClass(MainActivity.this, TesseractOpenCVCaptureActivity.class);
                                                                    startActivity(tesscvKRIntent);

                                                                }
                                                                */

                OCRModeSpinner.setAdapter(OCRModeSpinnerAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });



        /**
         * Speech Recognition Spinner & Spinner Adapters
         */
        final Spinner SpeechRecognitionSpinner = findViewById(R.id.Speech_recognition_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> SpeechRecognitionAdapter = ArrayAdapter.createFromResource(this,
                R.array.Speech_recognition_spinner_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        SpeechRecognitionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        SpeechRecognitionSpinner.setAdapter(SpeechRecognitionAdapter);

        SpeechRecognitionSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0){     //此行以下設置語音辨識選單
                    return;

                }
                if (position == 1){
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh_TW");

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, 10);
                    } else {
                        Toast.makeText(getApplicationContext(), "Your Device Doesn't Support Speech Input", Toast.LENGTH_SHORT).show();
                    }

                    speechAutoTranslationCode="disabled"; //設定一個無效化的代碼以免被自動翻譯的speechAutoTranslationCode代碼干擾

                }else if (position == 2) {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en_US");

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, 10);
                    } else {
                        Toast.makeText(getApplicationContext(), "Your Device Doesn't Support Speech Input", Toast.LENGTH_SHORT).show();
                    }

                    speechAutoTranslationCode="disabled"; //設定一個無效化的代碼以免被自動翻譯的speechAutoTranslationCode代碼干擾

                }else if (position == 3){
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh_CN");

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, 10);
                    } else {
                        Toast.makeText(getApplicationContext(), "Your Device Doesn't Support Speech Input", Toast.LENGTH_SHORT).show();
                    }

                    speechAutoTranslationCode="disabled"; //設定一個無效化的代碼以免被自動翻譯的speechAutoTranslationCode代碼干擾

                }else if (position == 4) {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ja_JP");

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, 10);
                    } else {
                        Toast.makeText(getApplicationContext(), "Your Device Doesn't Support Speech Input", Toast.LENGTH_SHORT).show();
                    }

                    speechAutoTranslationCode="disabled"; //設定一個無效化的代碼以免被自動翻譯的speechAutoTranslationCode代碼干擾

                }else if (position == 5) {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko_KR");

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, 10);
                    } else {
                        Toast.makeText(getApplicationContext(), "Your Device Doesn't Support Speech Input", Toast.LENGTH_SHORT).show();
                    }

                    speechAutoTranslationCode="disabled"; //設定一個無效化的代碼以免被自動翻譯的speechAutoTranslationCode代碼干擾

                }else if (position == 6) {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es_ES");

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, 10);
                    } else {
                        Toast.makeText(getApplicationContext(), "Your Device Doesn't Support Speech Input", Toast.LENGTH_SHORT).show();
                    }

                    speechAutoTranslationCode="disabled"; //設定一個無效化的代碼以免被自動翻譯的speechAutoTranslationCode代碼干擾


                }else if (position == 7) {    //此行以下設置語音辨識 + 自動翻譯
                    return;

                }else if (position == 8) {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh_TW");

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, 10);
                    } else {
                        Toast.makeText(getApplicationContext(), "Your Device Doesn't Support Speech Input", Toast.LENGTH_SHORT).show();
                    }

                    speechAutoTranslationCode="CHtoEN"; //設定一個特定代碼，在下面的onActivityResult執行完畢後，再以此代碼加載其所屬網址，因此不用再設定10秒延遲載入網頁

                    browserSwitch.setChecked(true);
                    browserNavigateBack.setVisibility(View.VISIBLE);
                    browserNavigateForward.setVisibility(View.VISIBLE);


                                                                    /*  ↓ ↓ ↓ 此作法已取消，不必使用↓ ↓ ↓
                                                                    設置run()並讓自動翻譯的網頁延遲10秒載入
                                                                    Runnable r = new Runnable() {
                                                                        @Override
                                                                        public void run() {

                                                                            searchKeyword = wordInputView.getText().toString();
                                                                            String url63 = "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=zh-CN&tl=en&text="+searchKeyword;
                                                                            webViewBrowser.loadUrl(url63);
                                                                            searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                                                                            webViewBrowser.setVisibility(View.VISIBLE);

                                                                        }
                                                                    };

                                                                    //設置Handler來延後執行run()
                                                                    Handler h =new Handler();
                                                                    h.postDelayed(r, 10000); //延後10秒執行
                                                                    ↑ ↑ ↑ 此作法已取消，不必使用 ↑ ↑ ↑ */


                }else if (position == 9) {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh_TW");

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, 10);
                    } else {
                        Toast.makeText(getApplicationContext(), "Your Device Doesn't Support Speech Input", Toast.LENGTH_SHORT).show();
                    }

                    speechAutoTranslationCode="CHtoJP";

                    browserSwitch.setChecked(true);
                    browserNavigateBack.setVisibility(View.VISIBLE);
                    browserNavigateForward.setVisibility(View.VISIBLE);

                }else if (position == 10) {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh_TW");

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, 10);
                    } else {
                        Toast.makeText(getApplicationContext(), "Your Device Doesn't Support Speech Input", Toast.LENGTH_SHORT).show();
                    }

                    speechAutoTranslationCode="CHtoKR";

                    browserSwitch.setChecked(true);
                    browserNavigateBack.setVisibility(View.VISIBLE);
                    browserNavigateForward.setVisibility(View.VISIBLE);

                }else if (position == 11) {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh_TW");

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, 10);
                    } else {
                        Toast.makeText(getApplicationContext(), "Your Device Doesn't Support Speech Input", Toast.LENGTH_SHORT).show();
                    }

                    speechAutoTranslationCode="CHtoES";

                    browserSwitch.setChecked(true);
                    browserNavigateBack.setVisibility(View.VISIBLE);
                    browserNavigateForward.setVisibility(View.VISIBLE);

                }else if (position == 12) {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en_US");

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, 10);
                    } else {
                        Toast.makeText(getApplicationContext(), "Your Device Doesn't Support Speech Input", Toast.LENGTH_SHORT).show();
                    }

                    speechAutoTranslationCode="ENtoCH";

                    browserSwitch.setChecked(true);
                    browserNavigateBack.setVisibility(View.VISIBLE);
                    browserNavigateForward.setVisibility(View.VISIBLE);

                }else if (position == 13) {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ja_JP");

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, 10);
                    } else {
                        Toast.makeText(getApplicationContext(), "Your Device Doesn't Support Speech Input", Toast.LENGTH_SHORT).show();
                    }

                    speechAutoTranslationCode="JPtoCH";

                    browserSwitch.setChecked(true);
                    browserNavigateBack.setVisibility(View.VISIBLE);
                    browserNavigateForward.setVisibility(View.VISIBLE);

                }else if (position == 14) {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko_KR");

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, 10);
                    } else {
                        Toast.makeText(getApplicationContext(), "Your Device Doesn't Support Speech Input", Toast.LENGTH_SHORT).show();
                    }

                    speechAutoTranslationCode="KRtoCH";

                    browserSwitch.setChecked(true);
                    browserNavigateBack.setVisibility(View.VISIBLE);
                    browserNavigateForward.setVisibility(View.VISIBLE);

                }else if (position == 15) {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es_ES");

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, 10);
                    } else {
                        Toast.makeText(getApplicationContext(), "Your Device Doesn't Support Speech Input", Toast.LENGTH_SHORT).show();
                    }

                    speechAutoTranslationCode="EStoCH";

                    browserSwitch.setChecked(true);
                    browserNavigateBack.setVisibility(View.VISIBLE);
                    browserNavigateForward.setVisibility(View.VISIBLE);

                }

                SpeechRecognitionSpinner.setAdapter(SpeechRecognitionAdapter);

            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });



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
                    String yahooDictionaryUrl= "https://tw.dictionary.search.yahoo.com/search;_ylt=AwrtXGoL8vtcAQoAnHV9rolQ;_ylc=X1MDMTM1MTIwMDM4MQRfcgMyBGZyA3NmcARncHJpZAN0RjJnMS51MlNWU3NDZ1pfVC4zNUFBBG5fcnNsdAMwBG5fc3VnZwM0BG9yaWdpbgN0dy5kaWN0aW9uYXJ5LnNlYXJjaC55YWhvby5jb20EcG9zAzAEcHFzdHIDBHBxc3RybAMEcXN0cmwDMwRxdWVyeQNHQVkEdF9zdG1wAzE1NjAwMTU0MTE-?p="+searchKeyword+"&fr2=sb-top-tw.dictionary.search&fr=sfp";
                    webViewBrowser.loadUrl(yahooDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 2) {
                    String naerUrl= "http://terms.naer.edu.tw/search/?q="+searchKeyword+"&field=ti&op=AND&group=&num=10";
                    webViewBrowser.loadUrl(naerUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 3) {
                    String dictDotSiteUrl= "http://dict.site/"+searchKeyword+".html";
                    webViewBrowser.loadUrl(dictDotSiteUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 4) {
                    String voicetubeUrl= "https://tw.voicetube.com/definition/"+searchKeyword;
                    webViewBrowser.loadUrl(voicetubeUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 5) {
                    String cambridgeDictionaryUrl= "https://dictionary.cambridge.org/zht/詞典/英語-漢語-繁體/"+searchKeyword;
                    webViewBrowser.loadUrl(cambridgeDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 6){
                    String merriamDictionaryUrl= "https://www.merriam-webster.com/dictionary/"+searchKeyword;
                    webViewBrowser.loadUrl(merriamDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 7) {
                    String collinsDictionaryUrl= "https://www.collinsdictionary.com/dictionary/english/"+searchKeyword;
                    webViewBrowser.loadUrl(collinsDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 8) {
                    String oxfordDictionaryUrl= "https://en.oxforddictionaries.com/definition/"+searchKeyword;
                    webViewBrowser.loadUrl(oxfordDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 9) {
                    String vocabularyDotComUrl= "https://www.vocabulary.com/dictionary/"+searchKeyword;
                    webViewBrowser.loadUrl(vocabularyDotComUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 10) {
                    String dictionaryDotComUrl= "https://www.dictionary.com/browse/"+searchKeyword;
                    webViewBrowser.loadUrl(dictionaryDotComUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 11) {
                    String theFreeDictionaryUrl= "https://www.thefreedictionary.com/"+searchKeyword;
                    webViewBrowser.loadUrl(theFreeDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 12) {
                    String yourDictionaryUrl= "https://www.yourdictionary.com/"+searchKeyword;
                    webViewBrowser.loadUrl(yourDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 13) {
                    String longmanDictionaryUrl= "https://www.ldoceonline.com/dictionary/"+searchKeyword;
                    webViewBrowser.loadUrl(longmanDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 14) {
                    String greensDictionaryOfSlangUrl= "https://greensdictofslang.com/search/basic?q="+searchKeyword;
                    webViewBrowser.loadUrl(greensDictionaryOfSlangUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 15) {
                    String wiktionaryUrl= "https://en.wiktionary.org/wiki/"+searchKeyword;
                    webViewBrowser.loadUrl(wiktionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 16) {
                    String wordHippoUrl= "https://www.wordhippo.com/what-is/another-word-for/"+searchKeyword+".html";
                    webViewBrowser.loadUrl(wordHippoUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 17) {
                    String onelookUrl= "https://www.onelook.com/thesaurus/?s="+searchKeyword;
                    webViewBrowser.loadUrl(onelookUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 18) {
                    String businessDictionaryUrl= "http://www.businessdictionary.com/definition/"+searchKeyword+".html";
                    webViewBrowser.loadUrl(businessDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 19) {
                    String slangDictionary= "http://www.yiym.com/?s="+searchKeyword;
                    webViewBrowser.loadUrl(slangDictionary);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 20) {
                    String youglishUrl= "https://youglish.com/search/"+searchKeyword+"/all?";
                    webViewBrowser.loadUrl(youglishUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 21) {
                    String academiaDictionaryUrl= "http://www.scidict.org/index.aspx?word="+searchKeyword;
                    webViewBrowser.loadUrl(academiaDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 22) {
                    String TechDicoUrl= "https://www.techdico.com/translation/english-chinese/"+searchKeyword+".html";
                    webViewBrowser.loadUrl(TechDicoUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 23) {
                    String BioMedicalDictionaryUrl= "http://dict.bioon.com/search.asp?txtitle="+searchKeyword+"&searchButton=查词典&matchtype=0";
                    webViewBrowser.loadUrl(BioMedicalDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 24) {
                    String carDictionaryUrl= "http://www.agosto.com.tw/dictionary.aspx?search="+searchKeyword;
                    webViewBrowser.loadUrl(carDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }

                EnDictionarySpinner.setAdapter(EnDictionarySpinnerAdapter);
                //再生成一次Adapter防止點按過的選項失效無法使用，以下同。

                browserSwitch.setChecked(true);
                //把網頁框開關狀態設定成"開啟"，以免載入網頁時開關沒有變成開啟的狀態
                browserNavigateBack.setVisibility(View.VISIBLE);
                browserNavigateForward.setVisibility(View.VISIBLE);

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
                    String weblioJPUrl= "https://www.weblio.jp/content/"+searchKeyword;
                    webViewBrowser.loadUrl(weblioJPUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 2) {
                    String weblioCHUrl= "https://cjjc.weblio.jp/content/"+searchKeyword;
                    webViewBrowser.loadUrl(weblioCHUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 3){
                    String weblioENUrl= "https://ejje.weblio.jp/content/"+searchKeyword;
                    webViewBrowser.loadUrl(weblioENUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 4) {
                    String weblioThesaurusUrl= "https://thesaurus.weblio.jp/content/"+searchKeyword;
                    webViewBrowser.loadUrl(weblioThesaurusUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 5) {
                    String tangorinDictionaryUrl= "https://tangorin.com/words?search="+searchKeyword;
                    webViewBrowser.loadUrl(tangorinDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 6) {
                    String tangorinKanjiUrl= "https://tangorin.com/kanji?search="+searchKeyword;
                    webViewBrowser.loadUrl(tangorinKanjiUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 7) {
                    String tangorinNamesUrl= "https://tangorin.com/names?search="+searchKeyword;
                    webViewBrowser.loadUrl(tangorinNamesUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 8) {
                    String tangorinSentencesUrl= "https://tangorin.com/sentences?search="+searchKeyword;
                    webViewBrowser.loadUrl(tangorinSentencesUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 9) {
                    String DaJPtoCHDictionaryUrl= "http://dict.asia/jc/"+searchKeyword;
                    webViewBrowser.loadUrl(DaJPtoCHDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 10) {
                    String DaCHtoJPDictionaryUrl= "http://dict.asia/cj/"+searchKeyword;
                    webViewBrowser.loadUrl(DaCHtoJPDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 11) {
                    String gooDictionaryUrl= "https://dictionary.goo.ne.jp/srch/jn/"+searchKeyword+"/m0u/";
                    webViewBrowser.loadUrl(gooDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 12) {
                    String sanseidoUrl= "https://www.sanseido.biz/User/Dic/Index.aspx?TWords="+searchKeyword+"&st=0&DORDER=151617&DailyJJ=checkbox&DailyEJ=checkbox&DailyJE=checkbox";
                    webViewBrowser.loadUrl(sanseidoUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 13) {
                    String kotobank= "https://kotobank.jp/word/"+searchKeyword;
                    webViewBrowser.loadUrl(kotobank);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 14) {
                    String jlogosUrl= "http://s.jlogos.com/list.html?keyword="+searchKeyword+"&opt_val=0";
                    webViewBrowser.loadUrl(jlogosUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 15) {
                    String industryDictionaryUrl= "https://www.sangyo-honyaku.jp/dictionaries/index/search_info:"+searchKeyword+"_ＩＴ・機械・電気電子";
                    webViewBrowser.loadUrl(industryDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 16) {
                    String KanjiDictionaryUrl= "https://kanji.jitenon.jp/cat/search.php?getdata="+searchKeyword+"&search=fpart&search2=twin";
                    webViewBrowser.loadUrl(KanjiDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 17) {
                    String eijiroDictionryUrl= "https://eow.alc.co.jp/search?q="+searchKeyword;
                    webViewBrowser.loadUrl(eijiroDictionryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 18) {
                    String whatIsItInEnglishUrl= "https://eikaiwa.dmm.com/uknow/search/?keyword="+searchKeyword;
                    webViewBrowser.loadUrl(whatIsItInEnglishUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 19) {
                    String jishoUrl= "https://jisho.org/search/"+searchKeyword;
                    webViewBrowser.loadUrl(jishoUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }

                JpDictionarySpinner.setAdapter(JpDictionarySpinnerAdapter);

                browserSwitch.setChecked(true);
                browserNavigateBack.setVisibility(View.VISIBLE);
                browserNavigateForward.setVisibility(View.VISIBLE);

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
                    String googlePlusChinese= "http://www.google.com/search?q="+searchKeyword+"+中文";
                    webViewBrowser.loadUrl(googlePlusChinese);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 2) {
                    String googlePlusENglish1= "http://www.google.com/search?q="+searchKeyword+"+英文";
                    webViewBrowser.loadUrl(googlePlusENglish1);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 3){
                    String googlePlusENglish2= "http://www.google.com/search?q="+searchKeyword+"+英語";
                    webViewBrowser.loadUrl(googlePlusENglish2);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 4) {
                    String googlePlusTranslation= "http://www.google.com/search?q="+searchKeyword+"+翻譯";
                    webViewBrowser.loadUrl(googlePlusTranslation);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 5) {
                    String googlePlusJapanese1= "http://www.google.com/search?q="+searchKeyword+"+日文";
                    webViewBrowser.loadUrl(googlePlusJapanese1);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 6) {
                    String googlePlusJapanese2= "http://www.google.com/search?q="+searchKeyword+"+日語";
                    webViewBrowser.loadUrl(googlePlusJapanese2);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 7) {
                    String googlePlusJapanese3= "http://www.google.com/search?q="+searchKeyword+"+日本語";
                    webViewBrowser.loadUrl(googlePlusJapanese3);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 8) {
                    String googlePlusMeaning1= "http://www.google.com/search?q="+searchKeyword+"+意思";
                    webViewBrowser.loadUrl(googlePlusMeaning1);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 9) {
                    String googlePlusMeaning2 = "http://www.google.com/search?q="+searchKeyword+"+meaning";
                    webViewBrowser.loadUrl(googlePlusMeaning2);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 10) {
                    String GoogleTranslateToCHTWUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=zh-TW&text="+searchKeyword;
                    webViewBrowser.loadUrl(GoogleTranslateToCHTWUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 11) {
                    String GoogleTranslateToCHCNUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=zh-CN&text="+searchKeyword;
                    webViewBrowser.loadUrl(GoogleTranslateToCHCNUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 12) {
                    String GoogleTranslateToENUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=en&text="+searchKeyword;
                    webViewBrowser.loadUrl(GoogleTranslateToENUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 13) {
                    String GoogleTranslateToJPUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=ja&text="+searchKeyword;
                    webViewBrowser.loadUrl(GoogleTranslateToJPUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 14) {
                    String GoogleTranslateToKRUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=ko&text="+searchKeyword;
                    webViewBrowser.loadUrl(GoogleTranslateToKRUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 15) {
                    String GoogleTranslateToSPUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=es&text="+searchKeyword;
                    webViewBrowser.loadUrl(GoogleTranslateToSPUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 16) {
                    String imageSearchUrl= "http://images.google.com/search?tbm=isch&q="+searchKeyword;
                    webViewBrowser.loadUrl(imageSearchUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }

                GoogleWordSearchSpinner.setAdapter(GoogleWordSearchSpinnerAdapter);

                browserSwitch.setChecked(true);
                browserNavigateBack.setVisibility(View.VISIBLE);
                browserNavigateForward.setVisibility(View.VISIBLE);

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
                    String ludwigUrl= "https://ludwig.guru/s/"+searchKeyword;
                    webViewBrowser.loadUrl(ludwigUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 2) {
                    String yourDictionarySentenceUrl= "https://sentence.yourdictionary.com/"+searchKeyword;
                    webViewBrowser.loadUrl(yourDictionarySentenceUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 3){
                    String jukuuUrlCHEN= "http://www.jukuu.com/search.php?q="+searchKeyword;
                    webViewBrowser.loadUrl(jukuuUrlCHEN);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 4) {
                    String jukuuUrlJPEN= "http://www.jukuu.com/jsearch.php?q="+searchKeyword;
                    webViewBrowser.loadUrl(jukuuUrlJPEN);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 5) {
                    String jukuuUrlCHJP= "http://www.jukuu.com/jcsearch.php?q="+searchKeyword;
                    webViewBrowser.loadUrl(jukuuUrlCHJP);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 6) {
                    String LingueeUrlCHEN= "https://cn.linguee.com/中文-英语/search?source=auto&query="+searchKeyword;
                    webViewBrowser.loadUrl(LingueeUrlCHEN);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 7) {
                    String LingueeUrlJPEN= "https://www.linguee.jp/日本語-英語/search?source=auto&query="+searchKeyword;
                    webViewBrowser.loadUrl(LingueeUrlJPEN);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }

                SentenceSearchSpinner.setAdapter(SentenceSearchSpinnerAdapter);

                browserSwitch.setChecked(true);
                browserNavigateBack.setVisibility(View.VISIBLE);
                browserNavigateForward.setVisibility(View.VISIBLE);

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
                else if (position == 1){
                    String wikipediaTWUrl= "https://zh.wikipedia.org/wiki/"+searchKeyword;
                    webViewBrowser.loadUrl(wikipediaTWUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 2){
                    String wikipediaENUrl= "https://en.wikipedia.org/wiki/"+searchKeyword;
                    webViewBrowser.loadUrl(wikipediaENUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 3){
                    String enEncyclopediaUrl= "https://www.encyclo.co.uk/meaning-of-"+searchKeyword;
                    webViewBrowser.loadUrl(enEncyclopediaUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 4) {
                    String forvoUrl= "https://forvo.com/search/"+searchKeyword;
                    webViewBrowser.loadUrl(forvoUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 5){
                    String wikidiffUrl= "https://wikidiff.com/"+searchKeyword;
                    webViewBrowser.loadUrl(wikidiffUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 6) {
                    String netspeakUrl= "http://www.netspeak.org/#query="+searchKeyword;
                    webViewBrowser.loadUrl(netspeakUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 7) {
                    String yomikatawaUrl= "https://yomikatawa.com/kanji/"+searchKeyword+"?search=1";
                    webViewBrowser.loadUrl(yomikatawaUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 8) {
                    String ChigaihaUrl= "https://cse.google.co.jp/cse?cx=partner-pub-1137871985589263%3A3025760782&ie=UTF-8&q="+searchKeyword;
                    webViewBrowser.loadUrl(ChigaihaUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 9) {
                    String suzukikunUrl= "http://www.gavo.t.u-tokyo.ac.jp/ojad/search/index/sortprefix:accent/narabi1:kata_asc/narabi2:accent_asc/narabi3:mola_asc/yure:visible/curve:invisible/details:invisible/limit:20/word:"+searchKeyword;
                    webViewBrowser.loadUrl(suzukikunUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }

                MiscellaneousSpinner.setAdapter(MiscellaneousSpinnerAdapter);

                browserSwitch.setChecked(true);
                browserNavigateBack.setVisibility(View.VISIBLE);
                browserNavigateForward.setVisibility(View.VISIBLE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });




        /**
         * 用戶在OCR識別頁面(TesseractOpenCVCaptureActivity)選取文字並彈跳出客製選單後的跳轉設定
         */
        Bundle extras = getIntent().getExtras();
        if (TesseractOpenCVCaptureActivity.UrlKey=="Translate OcrSelectedText to CHTW") {
            String UrlOcrSelectedTextToCHTW= extras.getString(TesseractOpenCVCaptureActivity.UrlKey);
            webViewBrowser.loadUrl(UrlOcrSelectedTextToCHTW);
            searchResultWillBeDisplayedHere.setVisibility(View.GONE);
            webViewBrowser.setVisibility(View.VISIBLE);

        } else if (TesseractOpenCVCaptureActivity.UrlKey=="Translate OcrSelectedText to CHCN") {
            String UrlOcrSelectedTextToCHCN= extras.getString(TesseractOpenCVCaptureActivity.UrlKey);
            webViewBrowser.loadUrl(UrlOcrSelectedTextToCHCN);
            searchResultWillBeDisplayedHere.setVisibility(View.GONE);
            webViewBrowser.setVisibility(View.VISIBLE);

        }else if (TesseractOpenCVCaptureActivity.UrlKey=="Translate OcrSelectedText to EN") {
            String UrlOcrSelectedTextToEN= extras.getString(TesseractOpenCVCaptureActivity.UrlKey);
            webViewBrowser.loadUrl(UrlOcrSelectedTextToEN);
            searchResultWillBeDisplayedHere.setVisibility(View.GONE);
            webViewBrowser.setVisibility(View.VISIBLE);

        }else if (TesseractOpenCVCaptureActivity.UrlKey=="Translate OcrSelectedText to JP") {
            String UrlOcrSelectedTextToJP= extras.getString(TesseractOpenCVCaptureActivity.UrlKey);
            webViewBrowser.loadUrl(UrlOcrSelectedTextToJP);
            searchResultWillBeDisplayedHere.setVisibility(View.GONE);
            webViewBrowser.setVisibility(View.VISIBLE);

        }else if (TesseractOpenCVCaptureActivity.UrlKey=="Translate OcrSelectedText to KR") {
            String UrlOcrSelectedTextToKR= extras.getString(TesseractOpenCVCaptureActivity.UrlKey);
            webViewBrowser.loadUrl(UrlOcrSelectedTextToKR);
            searchResultWillBeDisplayedHere.setVisibility(View.GONE);
            webViewBrowser.setVisibility(View.VISIBLE);

        }else if (TesseractOpenCVCaptureActivity.UrlKey=="Translate OcrSelectedText to SP") {
            String UrlOcrSelectedTextToSP= extras.getString(TesseractOpenCVCaptureActivity.UrlKey);
            webViewBrowser.loadUrl(UrlOcrSelectedTextToSP);
            searchResultWillBeDisplayedHere.setVisibility(View.GONE);
            webViewBrowser.setVisibility(View.VISIBLE);

        }



    }


    /**
     * 在OnCreate外面設置語音辨識的相關設定
     */

    public void cropRawPhotoForBackgroundImage (Uri image) {

        // 修改設定
        UCrop.Options options = new UCrop.Options();

        // 圖片格式
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);

        // 設定圖片壓縮質量
        options.setCompressionQuality(100);

        // 允許手指縮放、旋轉圖片，開放所有裁切框的長寬比例
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.NONE);

        // 是否讓使用者調整範圍(預設false)，如果開啟，可能會造成剪下的圖片的長寬比不是設定的
        // 如果不開啟，使用者不能拖動選框，只能縮放圖片
        options.setFreeStyleCropEnabled(false);

        // 設定原圖及目標暫存位置
        UCrop.of(image, Uri.fromFile(tempOutputFileForBackgroundImage))
                // 導入客製化設定
                .withOptions(options)
                .withAspectRatio(9, 16)
                .start(this);
    }



     /**
     * 在OnCreate外面設置語音輸入的相關設定
     * 以及在OnCreate外面另外設置用戶選取背景圖時的相關設定
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //設置語音輸入的相關設定
        switch (requestCode) {
            case 10:    //必須等同上面getSpeechInput方法中的requestCode:10
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    wordInputView.setText(result.get(0));
                }
                break;
        }

        //抓SpeechRecognitionSpinner中的speechAutoTranslationCode代碼，然後載入自動語音翻譯的網頁
        if (speechAutoTranslationCode=="CHtoEN") {
            searchKeyword = wordInputView.getText().toString();
            String speechUrl1 = "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=zh-CN&tl=en&text="+searchKeyword;
            webViewBrowser.loadUrl(speechUrl1);
            searchResultWillBeDisplayedHere.setVisibility(View.GONE);
            webViewBrowser.setVisibility(View.VISIBLE);

        }else if (speechAutoTranslationCode=="CHtoJP") {
            searchKeyword = wordInputView.getText().toString();
            String speechUrl2 = "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=zh-CN&tl=ja&text="+searchKeyword;
            webViewBrowser.loadUrl(speechUrl2);
            searchResultWillBeDisplayedHere.setVisibility(View.GONE);
            webViewBrowser.setVisibility(View.VISIBLE);

        }else if (speechAutoTranslationCode=="CHtoKR") {
            searchKeyword = wordInputView.getText().toString();
            String speechUrl3 = "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=zh-CN&tl=ko&text="+searchKeyword;
            webViewBrowser.loadUrl(speechUrl3);
            searchResultWillBeDisplayedHere.setVisibility(View.GONE);
            webViewBrowser.setVisibility(View.VISIBLE);

        }else if (speechAutoTranslationCode=="CHtoES") {
            searchKeyword = wordInputView.getText().toString();
            String speechUrl4 = "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=zh-CN&tl=es&text="+searchKeyword;
            webViewBrowser.loadUrl(speechUrl4);
            searchResultWillBeDisplayedHere.setVisibility(View.GONE);
            webViewBrowser.setVisibility(View.VISIBLE);

        }else if (speechAutoTranslationCode=="ENtoCH") {
            searchKeyword = wordInputView.getText().toString();
            String speechUrl5 = "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=en&tl=zh-TW&text="+searchKeyword;
            webViewBrowser.loadUrl(speechUrl5);
            searchResultWillBeDisplayedHere.setVisibility(View.GONE);
            webViewBrowser.setVisibility(View.VISIBLE);

        }else if (speechAutoTranslationCode=="JPtoCH") {
            searchKeyword = wordInputView.getText().toString();
            String speechUrl6 = "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=ja&tl=zh-TW&text="+searchKeyword;
            webViewBrowser.loadUrl(speechUrl6);
            searchResultWillBeDisplayedHere.setVisibility(View.GONE);
            webViewBrowser.setVisibility(View.VISIBLE);

        }else if (speechAutoTranslationCode=="KRtoCH") {
            searchKeyword = wordInputView.getText().toString();
            String speechUrl7 = "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=ko&tl=zh-TW&text="+searchKeyword;
            webViewBrowser.loadUrl(speechUrl7);
            searchResultWillBeDisplayedHere.setVisibility(View.GONE);
            webViewBrowser.setVisibility(View.VISIBLE);

        }else if (speechAutoTranslationCode=="EStoCH") {
            searchKeyword = wordInputView.getText().toString();
            String speechUrl8 = "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=es&tl=zh-TW&text="+searchKeyword;
            webViewBrowser.loadUrl(speechUrl8);
            searchResultWillBeDisplayedHere.setVisibility(View.GONE);
            webViewBrowser.setVisibility(View.VISIBLE);
        }


        //設置用戶選取背景圖時的相關設定
        if (changeBackgroundButtonIsPressed=="yes") {
            if (resultCode == 0 || data == null) {
                return;
            }
            // 相簿
            if (requestCode == TesseractOpenCVCaptureActivity.PHOTOALBUM) {
                imageForBackground = data.getData();
                try {
                    tempOutputFileForBackgroundImage = new File(getExternalCacheDir(), "temp-background_image.jpg");;
                    m_phone_for_background = MediaStore.Images.Media.getBitmap(getContentResolver(), imageForBackground);

                    cropRawPhotoForBackgroundImage(imageForBackground);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
                final Uri resultUri = UCrop.getOutput(data);
                try {
                    Bitmap croppedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    m_phone_for_background = croppedBitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == UCrop.RESULT_ERROR) {
                final Throwable cropError = UCrop.getError(data);
            }

            gifImageView.setVisibility(View.GONE);
            backGroundImageView.setImageBitmap(m_phone_for_background);
            backGroundImageView.setVisibility(View.VISIBLE);
        } else {
            return;
        }

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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



}




