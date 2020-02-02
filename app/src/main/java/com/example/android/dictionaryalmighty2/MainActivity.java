package com.example.android.dictionaryalmighty2;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.sachinvarma.easypermission.EasyPermissionInit;
import com.sachinvarma.easypermission.EasyPermissionList;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;

import static com.example.android.dictionaryalmighty2.UserInputHistory.presetNotificationTimingsList;
import static com.example.android.dictionaryalmighty2.UserInputHistory.userInputArrayAdapter;


public class MainActivity extends AppCompatActivity {

//==============================================================================================
// 所有變數Variables
//==============================================================================================

    GifImageView gifImageView; //用來準備給用戶更換背景圖
    ImageView voiceRecognitionImageView; //語音識別選單鈕
    ImageView ocrImageView;              //掃描文字選單鈕
    ImageView otherFunctionsImageView;   //其他功能選單鈕
    ImageView backGroundImageView;       //背景圖
    ImageView browserNavigateBack;       //瀏覽器上一頁鈕
    ImageView browserNavigateForward;    //瀏覽器下一頁鈕
    ImageView ocr;

    public static EditText wordInputView;    //關鍵字輸入框

    Button deleteUserInput;        //關鍵字輸入框清除鈕
    Button userInputHistoryButton; //用戶搜尋紀錄鈕
    static Button defaultSearchButton;      //快搜按鈕
    static Button comboSearchButton; //三連搜按鈕

    static String searchKeyword;      //用戶輸入的關鍵字
    static String username;      //用戶輸入的用戶名稱
    String LOG_TAG;  //Log tag for the external storage permission request error message
    String speechAutoTranslationCode; //用於載入自動語音翻譯之網頁的代碼
    String changeBackgroundButtonIsPressed; //更換背景時附加的代碼，以免與語音辨識的程式碼衝突
    String defaultSingleSearchCode; //用以設定單一預設快搜字典
    static String defaultComboSearchCodeFirstDictionary; //用以設定第一個預設快搜字典
    static String defaultComboSearchCodeSecondDictionary; //用以設定第二個預設快搜字典
    static String defaultComboSearchCodeThirdDictionary; //用以設定第三個預設快搜字典
    String[] defaultDictionaryListOriginal; //專業版自訂預設字典的名單
    String[] defaultDictionaryListSimplified; //簡易版自訂預設字典的名單
    String[] quickSearchComboSearchOrGoogleTranslateList; //讓用戶選擇快搜模式、三連搜模式或估狗翻譯
    public static final String IMAGE_UNSPECIFIED = "image/*";
    static String intentReceivedText;  //接收其他外部App傳來的文字
    String FriebaseUrl; //接收Firebase傳來的URL

    AlertDialog.Builder defaultSearchAlertDialog; //專業版自訂單一預設字典名單的對話方塊
    AlertDialog.Builder defaultComboSearchAlertDialogFirstDictionary; //專業版自訂第一個預設字典名單的對話方塊
    AlertDialog.Builder defaultComboSearchAlertDialogSecondDictionary; //專業版自訂第二個預設字典名單的對話方塊
    AlertDialog.Builder defaultComboSearchAlertDialogThirdDictionary; //專業版自訂第三個預設字典名單的對話方塊

    public static final int PHOTOALBUM = 1;   // 相簿
    int proOrSimplifiedSwitchCode; //專業版或簡易版切換的代碼

    Spinner otherFunctionsSpinner;    //All spinners
    Spinner SpeechRecognitionSpinner;
    Spinner OCRModeSpinner;
    Spinner EnDictionarySpinner;
    Spinner JpDictionarySpinner;
    Spinner GoogleWordSearchSpinner;
    Spinner SentenceSearchSpinner;
    Spinner MiscellaneousSpinner;

    ActionBar actionBar;
    LayoutParams layoutparams; //用來客製化修改ActionBar

    static WebView webViewBrowser;    //網頁框

    Switch browserSwitch;      //網頁框的開關
    Switch proOrSimplifiedLayoutSwitch; //專業版或簡易版開關

    ProgressBar progressBar;   //網頁載入的進度條

    static TextView searchResultWillBeDisplayedHere;
    TextView selectSentenceSearcherView;
    TextView miscellaneousView;
    TextView customActionBarTextview;

    private static int RESULT_LOAD_IMAGE = 1;
    private static final int WRITE_PERMISSION = 0x01; //用來準備設置運行中的權限要求
    static int localOrCloudSaveSwitchCode; //本地端或雲端存儲單字紀錄的切換代碼

    File tempOutputFileForBackgroundImage;

    Uri imageForBackground;                                //相簿中的原始圖檔

    Bitmap m_phone_for_background;                           // Bitmap圖像

    Calendar c;

    static SharedPreferences localOrCloudSaveSwitchPreferences;  //儲存用戶使用本地端或雲端存儲單字紀錄的SharedPreferences
    SharedPreferences userInputArrayListSharedPreferences;  //儲存用戶搜尋紀錄的SharedPreferences
    static SharedPreferences usernameSharedPreferences;  //儲存用戶名稱的SharedPreferences
    SharedPreferences proOrSimplifiedSwitchCodePreferences; //儲存用戶使用專業版或簡易版的SharedPreferences
    SharedPreferences defaultDictionarySearchSharedPreferences;//儲存單一預設字典的SharedPreferences
    SharedPreferences defaultComboDictionarySearchSharedPreferences;//儲存三個預設字典的SharedPreferences

    private ArrayList<DictionaryItem> mOcrSpinnerItemListOriginal;  //客製化Spinner選單列
    private ArrayList<DictionaryItem> mOcrSpinnerItemListSimplified;
    private ArrayList<DictionaryItem> mEnglishDictionarySpinnerItemListOriginal;
    private ArrayList<DictionaryItem> mEnglishDictionarySpinnerItemListSimplified;
    private ArrayList<DictionaryItem> mJapaneseDictionarySpinnerItemListOriginal;
    private ArrayList<DictionaryItem> mJapaneseDictionarySpinnerItemListSimplified;
    private ArrayList<DictionaryItem> mGoogleWordSearchSpinnerItemListOriginal;
    private ArrayList<DictionaryItem> mGoogleWordSearchSpinnerItemListSimplified;
    private ArrayList<DictionaryItem> mSentenceSearchSpinnerItemListOriginal;
    private ArrayList<DictionaryItem> mMiscellaneousSpinnerItemListOriginal;

    private DictionayItemAdapter mEnglishDictionarySpinnerAdapter; //客製化Spinner的Adapter
    private DictionayItemAdapter mJapaneseDictionarySpinnerAdapter;
    private DictionayItemAdapter mGoogleWordSearchSpinnerAdapter;
    private DictionayItemAdapter mSentenceSearchSpinnerAdapter;
    private DictionayItemAdapter mMiscellaneousSpinnerAdapter;
    private DictionayItemAdapter mOcrSpinnerAdapter;

    public static final ArrayList<String> userInputArraylist = new ArrayList<>(); //用戶搜尋紀錄的ArrayList
    public static final ArrayList<String> myVocabularyArrayList = new ArrayList<>();

    public static DatabaseReference mRootReference = FirebaseDatabase.getInstance().getReference();
    public static DatabaseReference mChildReferenceForInputHistory = mRootReference.child("Users' Input History");
    public static DatabaseReference mChildReferenceForVocabularyList = mRootReference.child("Users' Vocabulary List");



//==============================================================================================
// onCreate
//==============================================================================================

    @RequiresApi(api = Build.VERSION_CODES.M)  //要加上這條限定Api等級，requestWritePermission()才不會報錯
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestWritePermission();  //在程式運行中要求存取的權限


        /**
         * 要求讀取月曆的權限
         */
        List<String> permission = new ArrayList<>();
        permission.add(EasyPermissionList.READ_CALENDAR);
        permission.add(EasyPermissionList.WRITE_CALENDAR);
        new EasyPermissionInit(this, permission);


        /**
         * findViewById
         */
        gifImageView = findViewById(R.id.GIF_imageView);
        voiceRecognitionImageView = findViewById(R.id.btnSpeak);
        ocrImageView = findViewById(R.id.ocr_imageView);
        otherFunctionsImageView = findViewById(R.id.Other_functions_image);
        backGroundImageView = findViewById(R.id.background_image_view);
        wordInputView = findViewById(R.id.Word_Input_View);
        deleteUserInput = findViewById(R.id.delete_user_input_button);
        userInputHistoryButton = findViewById(R.id.user_input_history_button);
        defaultSearchButton = findViewById(R.id.default_search_button);
        comboSearchButton = findViewById(R.id.combo_search_button);
        searchResultWillBeDisplayedHere = findViewById(R.id.search_result_textView);
        browserNavigateBack = findViewById(R.id.browser_navigate_back_imageView);
        browserNavigateForward = findViewById(R.id.browser_navigate_forward_imageView);
        selectSentenceSearcherView = findViewById(R.id.Select_Sentence_Searcher_View);
        miscellaneousView = findViewById(R.id.Miscellaneous_View);


        /**
         * 設定程式開啟時預設使用簡易版的客製化ActionBar
         */
        actionBar = getSupportActionBar();
        customActionBarTextview = new TextView(MainActivity.this); //宣告客製化ActionBar的新文字框
        layoutparams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); //設置客製化ActionBar的Layout
        customActionBarSimplified(); //Helper method


        /**
         * 設定程式開啟時預設遮蔽的物件
         */
        backGroundImageView.setVisibility(View.GONE);
        browserNavigateBack.setVisibility(View.GONE);
        browserNavigateForward.setVisibility(View.GONE);


        /**
         * 讓用戶清除關鍵字輸入框內的字
         */
        deleteUserInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordInputView.setText("");  //讓文字框內變成空白字元
            }
        });


        /**
         * 讓用戶點擊紀錄鈕時跳轉到搜尋紀錄頁面
         */
        userInputHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserInputHistory.class);
                startActivity(intent);
            }
        });


        /**
         * 頁面生成時讀取用戶設定的用戶名稱
         */
        usernameSharedPreferences = getSharedPreferences("usernameSharedPreferences", Context.MODE_PRIVATE);
        username = usernameSharedPreferences.getString("userName", ""); //Here we need to specify a defaultValue for the 2nd argument in case the data we are trying to retrieve does not exist, and the defaultValue will be used as a fallBack.



        /**
         * 頁面生成時讀取用戶搜尋紀錄userInputArrayList
         */
        userInputArrayListSharedPreferences = getSharedPreferences("userInputArrayListSharedPreferences", MODE_PRIVATE);
        int userInputArrayListValues = userInputArrayListSharedPreferences.getInt("userInputArrayListValues", 0);
        for (int i = 0; i < userInputArrayListValues; i++) {
            String userInputArrayListItem = userInputArrayListSharedPreferences.getString("userInputArrayListItem_" + i, null);
            userInputArraylist.add(userInputArrayListItem);
        }


        /**
         * 頁面生成時讀取用戶搜尋紀錄myVocabularyArrayList
         */
        SharedPreferences wordsToMemorizeSharedPreferences = getSharedPreferences("myVocabularyArrayListSharedPreferences", MODE_PRIVATE);
        int myVocabularyArrayListValues = wordsToMemorizeSharedPreferences.getInt("myVocabularyArrayListValues", 0);
        for (int i = 0; i < myVocabularyArrayListValues; i++) {
            String myVocabularyArrayListItem = wordsToMemorizeSharedPreferences.getString("myVocabularyArrayListValues" + i, null);
            myVocabularyArrayList.add(myVocabularyArrayListItem);
        }


        /**
         * 頁面生成時讀取用戶設定的預設字典，點選快搜按鈕時會搜尋預設字典
         */
        defaultDictionarySearchSharedPreferences = getSharedPreferences("saveChosenDefaultSingleDictionary", Context.MODE_PRIVATE);
        defaultSingleSearchCode = defaultDictionarySearchSharedPreferences.getString("DefaultSingleDictionaryCode", ""); //Here we need to specify a defaultValue for the 2nd argument in case the data we are trying to retrieve does not exist, and the defaultValue will be used as a fallBack.

        loadDefaultDictionaries(); //載入預設字典的網址


        /**
         * 頁面生成時讀取用戶設定的三連搜預設字典
         */
        defaultComboDictionarySearchSharedPreferences = getSharedPreferences("saveComboDefaultDictionary", Context.MODE_PRIVATE);
        defaultComboSearchCodeFirstDictionary = defaultComboDictionarySearchSharedPreferences.getString("ComboSearchCodeForFirstDictionary", "");
        defaultComboSearchCodeSecondDictionary = defaultComboDictionarySearchSharedPreferences.getString("ComboSearchCodeForSecondDictionary", "");
        defaultComboSearchCodeThirdDictionary = defaultComboDictionarySearchSharedPreferences.getString("ComboSearchCodeForThirdDictionary", "");

        /**
         * 點選快搜按鈕時會搜尋三個預設字典
         */
        comboSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //若用戶未設定預設字典(三個預設字典的代碼是空的)
                if (defaultComboSearchCodeFirstDictionary == "" || defaultComboSearchCodeSecondDictionary == "" || defaultComboSearchCodeThirdDictionary == "") {

                    if (proOrSimplifiedLayoutSwitch.isChecked()) { //檢查若已已啟動專業版版面
                        setDefaultDictionariesOriginal();         //則載入專業版預設字典選單
                    } else {
                        setDefaultDictionariesSimplified();       //反之，載入簡易版預設字典選單
                    }
                    Toast.makeText(getApplicationContext(), R.string.Please_set_3_default_dictionaries, Toast.LENGTH_LONG).show(); //通知須設定預設字典

                } else {
                    Intent fireComboSearchActivity = new Intent();  //若三個代碼不是空的，載入三連搜頁面
                    fireComboSearchActivity.setClass(MainActivity.this, ComboSearchActivity.class);
                    startActivity(fireComboSearchActivity);
                }

            }
        });


        /**
         * 頁面生成時存取用戶設定的背景圖
         */
        SharedPreferences sharedPreferences = getSharedPreferences("testSP", Context.MODE_PRIVATE);
        //第一步:取出字符串形式的Bitmap
        String imageString = sharedPreferences.getString("image", "");
        //第二步:利用Base64將字符串轉換為ByteArrayInputStream
        byte[] byteArray = Base64.decode(imageString, Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        //第三步:利用ByteArrayInputStream生成Bitmap
        Bitmap backgroundBitmap = BitmapFactory.decodeStream(byteArrayInputStream);
        gifImageView.setImageBitmap(backgroundBitmap);


        /**
         * Initialize the ArrayList used for the custom spinners
         */
        initList();


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
        webSettings.setLoadWithOverviewMode(false);
        webSettings.setUseWideViewPort(false);
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

                if (browserSwitch.isChecked()) {                     //用isChecked()檢視開關的開啟狀態
                    webViewBrowser.setVisibility(View.VISIBLE);
                    browserNavigateBack.setVisibility(View.VISIBLE);
                    browserNavigateForward.setVisibility(View.VISIBLE);
                } else {
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

                if (webViewBrowser.canGoBack()) {
                    webViewBrowser.goBack();
                }
            }
        });

        browserNavigateForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (webViewBrowser.canGoForward()) {
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
        if (Objects.equals(receivedAction, Intent.ACTION_SEND)) {
            //Content is being shared. Handle received data of the MIME types.
            if (receivedType.startsWith("text/")) {
                //Handle sent text
                intentReceivedText = received3rdPartyAppIntent.getStringExtra(Intent.EXTRA_TEXT);  //Get the received text
                if (intentReceivedText != null) {              //Check we have a string
                    searchKeyword = intentReceivedText;
                    wordInputView.setText(searchKeyword); //Set the text to the search box in MainActivity

                    chooseActionAlertDialog();

                    saveKeywordtoUserInputListView();            //Helper method。把用戶查的單字存到搜尋紀錄頁面
                    saveUserInputArrayListToSharedPreferences(); //Helper method。把用戶查的單字(整個列表)存到SharedPreferences

                }
            }
        } else if (Objects.equals(receivedAction, Intent.ACTION_MAIN)) {
            //app has been launched directly, not from share list
        }


        /**
         * 設置接收Firebase dynamic links
         */
        checkForDynamicLinks();


        //==============================================================================================
        // 設置專業版或簡易版的開關，以及所有Spinners
        //==============================================================================================

        spinnersForSimplifiedLayout(); //先讓App啟動時預設加載簡易版面

        proOrSimplifiedLayoutSwitch = findViewById(R.id.pro_or_simplified_layout_switch);

        proOrSimplifiedLayoutSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (proOrSimplifiedLayoutSwitch.isChecked()) {         //用isChecked()檢視開關的開啟狀態

                    spinnersForOriginalLayout();  //加載專業版spinners
                    customActionBarPro();         //加載專業版ActionBar

                    //把用戶開啟專業版的設定(proOrSimplifiedSwitchCodePreferences=1 已開啟專業版)存入SharedPreferences
                    proOrSimplifiedSwitchCodePreferences = getSharedPreferences("proOrSimplifiedSwitchCodeSharedPreferences", MODE_PRIVATE);
                    proOrSimplifiedSwitchCodePreferences.edit().putInt("ProMode", 1).apply();

                    Toast.makeText(MainActivity.this, getString(R.string.You_are_using_pro_version), Toast.LENGTH_SHORT).show();

                } else {

                    spinnersForSimplifiedLayout();  //加載簡易版spinners

                    customActionBarSimplified();    //加載簡易版ActionBar

                    //把用戶開啟簡易版的設定(proOrSimplifiedSwitchCodePreferences=0 已關閉專業版)存入SharedPreferences
                    proOrSimplifiedSwitchCodePreferences = getSharedPreferences("proOrSimplifiedSwitchCodeSharedPreferences", MODE_PRIVATE);
                    proOrSimplifiedSwitchCodePreferences.edit().putInt("ProMode", 0).apply();

                    Toast.makeText(MainActivity.this, getString(R.string.You_are_using_simplified_version), Toast.LENGTH_SHORT).show();

                }

            }
        });


        //設置App啟動時檢查代碼為專業版或簡易版，並載入對應的Spinners
        proOrSimplifiedSwitchCode = getSharedPreferences("proOrSimplifiedSwitchCodeSharedPreferences", MODE_PRIVATE).getInt("ProMode", 2);

        if (proOrSimplifiedSwitchCode == 1) {                  //若用代碼為1=已開啟專業版
            spinnersForOriginalLayout();                    //加載專業版spinners
            proOrSimplifiedLayoutSwitch.setChecked(true);   //設定開關按鈕為開啟的狀態

            customActionBarPro();                           //加載客製化專業版ActionBar

        } else if (proOrSimplifiedSwitchCode == 0) {            //若用代碼為0=已關閉專業版
            spinnersForSimplifiedLayout();                  //加載簡易版spinners
            proOrSimplifiedLayoutSwitch.setChecked(false);  //設定開關按鈕為關閉的狀態

            customActionBarSimplified();                    //加載客製化簡易版ActionBar

        }


    }




    //==============================================================================================
    // 在OnCreate外面設置接收Firebase Dynamic Links
    //==============================================================================================

    @Override
    protected void onStart() {
        super.onStart();

        checkForDynamicLinks();


        c = Calendar.getInstance(); // 順便取得目前日期與時間，設置單字記憶通知要用的
    }


    //==============================================================================================
    // 在OnCreate外面設置 客製化Spinner選單列的項目(圖+文字) (包括簡易版與專業版的項目)
    //==============================================================================================

    private void initList() {

        mOcrSpinnerItemListOriginal = new ArrayList<>();
        mOcrSpinnerItemListOriginal.add(new DictionaryItem(R.string.Select_an_OCR_third_party_app, R.mipmap.hand_pointing_down));
        mOcrSpinnerItemListOriginal.add(new DictionaryItem(R.string.Call_TextScanner_app, R.mipmap.text_scanner));
        mOcrSpinnerItemListOriginal.add(new DictionaryItem(R.string.Call_google_translate_app, R.mipmap.google_translate));
        mOcrSpinnerItemListOriginal.add(new DictionaryItem(R.string.Call_microsoft_translator_app_ocr_recognition, R.mipmap.microsoft_translator));
        mOcrSpinnerItemListOriginal.add(new DictionaryItem(R.string.Call_Yomiwa_app, R.mipmap.yomiwa));


        mOcrSpinnerItemListSimplified = new ArrayList<>();
        mOcrSpinnerItemListSimplified.add(new DictionaryItem(R.string.Select_an_OCR_third_party_app, R.mipmap.hand_pointing_down));
        mOcrSpinnerItemListSimplified.add(new DictionaryItem(R.string.Call_TextScanner_app, R.mipmap.text_scanner));
        mOcrSpinnerItemListSimplified.add(new DictionaryItem(R.string.Call_google_translate_app, R.mipmap.google_translate));


        mEnglishDictionarySpinnerItemListOriginal = new ArrayList<>();
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Select_one_of_the_following, R.mipmap.hand_pointing_down));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Yahoo_Dictionary, R.mipmap.yahoo_dictionary));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.National_Academy_for_Educational_Research, R.mipmap.national_academy_for_educational_research));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Dict_site, R.mipmap.dict_dot_site));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Cidian8, R.mipmap.cidian8));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.VoiceTube, R.mipmap.voicetube));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Cambridge_EN_CH, R.mipmap.cambridge));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Merriam_Webster, R.mipmap.merriam_wester));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Collins, R.mipmap.collins));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Oxford, R.mipmap.oxford_dictionary));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Vocabulary, R.mipmap.vocabulary_dot_com));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Dictionary, R.mipmap.dictionary_dot_com));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.The_Free_Dictionary, R.mipmap.the_free_dictionary));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Your_Dictionary, R.mipmap.your_dictionary));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Longman_Dictionary, R.mipmap.longman));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Greens_dictionary_of_slang, R.mipmap.greens_dictionary_of_slang));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Wiki_Dictionary, R.mipmap.wikctionary));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Word_Hippo, R.mipmap.word_hippo));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Onelook, R.mipmap.onelook));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Business_Dictionary, R.mipmap.business_dictionary));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Slang, R.mipmap.yiym));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.YouGlish, R.mipmap.youglish));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.SCI_Dictionary, R.mipmap.sci_dict));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Academic_dictionaries_and_encyclopedias, R.mipmap.academic_dictionaries));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.TechDico, R.mipmap.tech_dico));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.BioMedical_dictionary, R.mipmap.bio_medical_dictionary));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Is_plural_dictionary, R.mipmap.is_plural_dictionary));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Lingo_help_prepositions, R.mipmap.lingo_help_prepositions));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.WordWeb_dictionary, R.mipmap.word_web));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Power_thesaurus_synonym, R.mipmap.power_thesaurus));
        mEnglishDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Power_thesaurus_antonym, R.mipmap.power_thesaurus));


        mEnglishDictionarySpinnerItemListSimplified = new ArrayList<>();
        mEnglishDictionarySpinnerItemListSimplified.add(new DictionaryItem(R.string.Select_one_of_the_following, R.mipmap.hand_pointing_down));
        mEnglishDictionarySpinnerItemListSimplified.add(new DictionaryItem(R.string.Yahoo_Dictionary, R.mipmap.yahoo_dictionary));
        mEnglishDictionarySpinnerItemListSimplified.add(new DictionaryItem(R.string.National_Academy_for_Educational_Research, R.mipmap.national_academy_for_educational_research));
        mEnglishDictionarySpinnerItemListSimplified.add(new DictionaryItem(R.string.Dict_site, R.mipmap.dict_dot_site));
        mEnglishDictionarySpinnerItemListSimplified.add(new DictionaryItem(R.string.VoiceTube, R.mipmap.voicetube));
        mEnglishDictionarySpinnerItemListSimplified.add(new DictionaryItem(R.string.Cambridge_EN_CH, R.mipmap.cambridge));


        mJapaneseDictionarySpinnerItemListOriginal = new ArrayList<>();
        mJapaneseDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Select_one_of_the_following, R.mipmap.hand_pointing_down));
        mJapaneseDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Weblio_JP, R.mipmap.weblio));
        mJapaneseDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Weblio_CN, R.mipmap.weblio));
        mJapaneseDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Weblio_EN, R.mipmap.weblio));
        mJapaneseDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Weblio_Synonym, R.mipmap.weblio));
        mJapaneseDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Tangorin_Word, R.mipmap.tangorin));
        mJapaneseDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Tangorin_Kanji, R.mipmap.tangorin));
        mJapaneseDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Tangorin_Names, R.mipmap.tangorin));
        mJapaneseDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Tangorin_Sentence, R.mipmap.tangorin));
        mJapaneseDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.DA_JP_TW_Dictionary, R.mipmap.da));
        mJapaneseDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.DA_TW_JP_Dictionary, R.mipmap.da));
        mJapaneseDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Cidianwang, R.mipmap.cidian_wang));
        mJapaneseDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Goo, R.mipmap.goo_dictionary));
        mJapaneseDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Sanseido, R.mipmap.sanseido));
        mJapaneseDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Kotoba_Bank, R.mipmap.kotoba_bank));
        mJapaneseDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.J_Logos, R.mipmap.jlogos));
        mJapaneseDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Japanese_Industry_Terms, R.mipmap.industry_dictionary));
        mJapaneseDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Kanji_Dictionary_Online, R.mipmap.kanji_dictionary));
        mJapaneseDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Eijirou, R.mipmap.eigiro));
        mJapaneseDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.How_do_you_say_this_in_English, R.mipmap.dmm_eikaiwa));
        mJapaneseDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Jisho, R.mipmap.jisho));
        mJapaneseDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Cambridge_JP_EN, R.mipmap.cambridge));
        mJapaneseDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Cambridge_EN_JP, R.mipmap.cambridge));
        mJapaneseDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.WWWJDIC_jp_en, R.mipmap.www_jdic));
        mJapaneseDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.WWWJDIC_en_jp, R.mipmap.www_jdic));
        mJapaneseDictionarySpinnerItemListOriginal.add(new DictionaryItem(R.string.Kanji_recognizer, R.mipmap.kanji_recognizer));


        mJapaneseDictionarySpinnerItemListSimplified = new ArrayList<>();
        mJapaneseDictionarySpinnerItemListSimplified.add(new DictionaryItem(R.string.Select_one_of_the_following, R.mipmap.hand_pointing_down));
        mJapaneseDictionarySpinnerItemListSimplified.add(new DictionaryItem(R.string.Weblio_JP, R.mipmap.weblio));
        mJapaneseDictionarySpinnerItemListSimplified.add(new DictionaryItem(R.string.Weblio_CN, R.mipmap.weblio));
        mJapaneseDictionarySpinnerItemListSimplified.add(new DictionaryItem(R.string.DA_JP_TW_Dictionary, R.mipmap.da));
        mJapaneseDictionarySpinnerItemListSimplified.add(new DictionaryItem(R.string.DA_TW_JP_Dictionary, R.mipmap.da));


        mGoogleWordSearchSpinnerItemListOriginal = new ArrayList<>();
        mGoogleWordSearchSpinnerItemListOriginal.add(new DictionaryItem(R.string.Select_one_of_the_following, R.mipmap.hand_pointing_down));
        mGoogleWordSearchSpinnerItemListOriginal.add(new DictionaryItem(R.string.Word_Plus_Chinese, R.mipmap.google));
        mGoogleWordSearchSpinnerItemListOriginal.add(new DictionaryItem(R.string.Word_Plus_English1, R.mipmap.google));
        mGoogleWordSearchSpinnerItemListOriginal.add(new DictionaryItem(R.string.Word_Plus_English2, R.mipmap.google));
        mGoogleWordSearchSpinnerItemListOriginal.add(new DictionaryItem(R.string.Word_Plus_Translation, R.mipmap.google));
        mGoogleWordSearchSpinnerItemListOriginal.add(new DictionaryItem(R.string.Word_Plus_Japanese1, R.mipmap.google));
        mGoogleWordSearchSpinnerItemListOriginal.add(new DictionaryItem(R.string.Word_Plus_Japanese2, R.mipmap.google));
        mGoogleWordSearchSpinnerItemListOriginal.add(new DictionaryItem(R.string.Word_Plus_Japanese3, R.mipmap.google));
        mGoogleWordSearchSpinnerItemListOriginal.add(new DictionaryItem(R.string.Word_Plus_Meaning1, R.mipmap.google));
        mGoogleWordSearchSpinnerItemListOriginal.add(new DictionaryItem(R.string.Word_Plus_Meaning2, R.mipmap.google));
        mGoogleWordSearchSpinnerItemListOriginal.add(new DictionaryItem(R.string.Google_translate_to_CHTW, R.mipmap.google_translate));
        mGoogleWordSearchSpinnerItemListOriginal.add(new DictionaryItem(R.string.Google_translate_to_CHCN, R.mipmap.google_translate));
        mGoogleWordSearchSpinnerItemListOriginal.add(new DictionaryItem(R.string.Google_translate_to_EN, R.mipmap.google_translate));
        mGoogleWordSearchSpinnerItemListOriginal.add(new DictionaryItem(R.string.Google_translate_to_JP, R.mipmap.google_translate));
        mGoogleWordSearchSpinnerItemListOriginal.add(new DictionaryItem(R.string.Google_translate_to_KR, R.mipmap.google_translate));
        mGoogleWordSearchSpinnerItemListOriginal.add(new DictionaryItem(R.string.Google_translate_to_SP, R.mipmap.google_translate));
        mGoogleWordSearchSpinnerItemListOriginal.add(new DictionaryItem(R.string.Google_Image, R.mipmap.google));


        mGoogleWordSearchSpinnerItemListSimplified = new ArrayList<>();
        mGoogleWordSearchSpinnerItemListSimplified.add(new DictionaryItem(R.string.Select_one_of_the_following, R.mipmap.hand_pointing_down));
        mGoogleWordSearchSpinnerItemListSimplified.add(new DictionaryItem(R.string.Google_translate_to_CHTW, R.mipmap.google_translate));
        mGoogleWordSearchSpinnerItemListSimplified.add(new DictionaryItem(R.string.Google_translate_to_CHCN, R.mipmap.google_translate));
        mGoogleWordSearchSpinnerItemListSimplified.add(new DictionaryItem(R.string.Google_Image, R.mipmap.google));


        mSentenceSearchSpinnerItemListOriginal = new ArrayList<>();
        mSentenceSearchSpinnerItemListOriginal.add(new DictionaryItem(R.string.Select_one_of_the_following, R.mipmap.hand_pointing_down));
        mSentenceSearchSpinnerItemListOriginal.add(new DictionaryItem(R.string.Ludwig, R.mipmap.ludwig));
        mSentenceSearchSpinnerItemListOriginal.add(new DictionaryItem(R.string.Your_Dictionary_Example_Sentences, R.mipmap.your_dictionary));
        mSentenceSearchSpinnerItemListOriginal.add(new DictionaryItem(R.string.Word_Cool_EN_CH, R.mipmap.jukuu));
        mSentenceSearchSpinnerItemListOriginal.add(new DictionaryItem(R.string.Word_Cool_EN_JP, R.mipmap.jukuu));
        mSentenceSearchSpinnerItemListOriginal.add(new DictionaryItem(R.string.Word_Cool_JP_CH, R.mipmap.jukuu));
        mSentenceSearchSpinnerItemListOriginal.add(new DictionaryItem(R.string.Linguee_CH_EN, R.mipmap.linguee));
        mSentenceSearchSpinnerItemListOriginal.add(new DictionaryItem(R.string.Linguee_JP_EN, R.mipmap.linguee));


        mMiscellaneousSpinnerItemListOriginal = new ArrayList<>();
        mMiscellaneousSpinnerItemListOriginal.add(new DictionaryItem(R.string.Select_one_of_the_following, R.mipmap.hand_pointing_down));
        mMiscellaneousSpinnerItemListOriginal.add(new DictionaryItem(R.string.Wikipedia_TW, R.mipmap.wikipedia));
        mMiscellaneousSpinnerItemListOriginal.add(new DictionaryItem(R.string.Wikipedia_EN, R.mipmap.wikipedia));
        mMiscellaneousSpinnerItemListOriginal.add(new DictionaryItem(R.string.English_Encyclopedia, R.mipmap.encyclo));
        mMiscellaneousSpinnerItemListOriginal.add(new DictionaryItem(R.string.Britannica_encyclopedia, R.mipmap.britannica));
        mMiscellaneousSpinnerItemListOriginal.add(new DictionaryItem(R.string.Forvo, R.mipmap.forvo));
        mMiscellaneousSpinnerItemListOriginal.add(new DictionaryItem(R.string.Wiki_Diff, R.mipmap.wikidiff));
        mMiscellaneousSpinnerItemListOriginal.add(new DictionaryItem(R.string.Net_Speak, R.mipmap.netspeak));
        mMiscellaneousSpinnerItemListOriginal.add(new DictionaryItem(R.string.Kotonoha_japanese_corpus, R.mipmap.kotonoha_japanese_corpus));
        mMiscellaneousSpinnerItemListOriginal.add(new DictionaryItem(R.string.Tatoeba, R.mipmap.tatoeba));
        mMiscellaneousSpinnerItemListOriginal.add(new DictionaryItem(R.string.Yomikata, R.mipmap.yomikatawa));
        mMiscellaneousSpinnerItemListOriginal.add(new DictionaryItem(R.string.Chigai, R.mipmap.chigaiwa));
        mMiscellaneousSpinnerItemListOriginal.add(new DictionaryItem(R.string.OJAD, R.mipmap.ojad));
        mMiscellaneousSpinnerItemListOriginal.add(new DictionaryItem(R.string.Learn_with_kak, R.mipmap.learn_with_kak));
        mMiscellaneousSpinnerItemListOriginal.add(new DictionaryItem(R.string.Home_of_english, R.mipmap.home_of_english));
        mMiscellaneousSpinnerItemListOriginal.add(new DictionaryItem(R.string.BBC_learning_english, R.mipmap.bbc_learning_english));
        mMiscellaneousSpinnerItemListOriginal.add(new DictionaryItem(R.string.Voice_of_america, R.mipmap.voice_of_america));
        mMiscellaneousSpinnerItemListOriginal.add(new DictionaryItem(R.string.Teacher_sammy_english_training, R.mipmap.teacher_sammy_english_training));
        mMiscellaneousSpinnerItemListOriginal.add(new DictionaryItem(R.string.Michael_chugani_column, R.mipmap.michael_chugani_column));
        mMiscellaneousSpinnerItemListOriginal.add(new DictionaryItem(R.string.Grammarist, R.mipmap.grammarist));
        mMiscellaneousSpinnerItemListOriginal.add(new DictionaryItem(R.string.Kenny_english, R.mipmap.kenny_english));

    }



    //==============================================================================================
    // 在OnCreate外面設置 裁切背景圖 的相關設定
    //==============================================================================================

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



    //==============================================================================================
    // 在OnCreate外面設置 語音輸入 的相關設定
    //==============================================================================================

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //設置語音輸入的相關設定
        switch (requestCode) {
            case 10:    //必須等同上面getSpeechInput方法中的requestCode:10
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    wordInputView.setText(result.get(0));           //讓關鍵字輸入框顯示用戶說的文字。以下同。

                    searchKeyword = wordInputView.getText().toString();
                    saveKeywordtoUserInputListView ();              //Helper method。把用戶查的單字存到搜尋紀錄頁面。以下同。
                    saveUserInputArrayListToSharedPreferences ();   //Helper method。把用戶查的單字(整個列表)存到SharedPreferences
                }

                break;
        }

        //抓SpeechRecognitionSpinner中的speechAutoTranslationCode代碼，然後載入自動語音翻譯的網頁
        if (speechAutoTranslationCode=="CHtoEN") {
            searchKeyword = wordInputView.getText().toString();

            if (searchKeyword != null && !searchKeyword.equals("")) {
                String speechUrl1 = "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=zh-CN&tl=en&text="+searchKeyword;
                webViewBrowser.loadUrl(speechUrl1);
                searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                webViewBrowser.setVisibility(View.VISIBLE);

                saveKeywordtoUserInputListView ();
                saveUserInputArrayListToSharedPreferences ();
            }


        }else if (speechAutoTranslationCode=="CHtoJP") {
            searchKeyword = wordInputView.getText().toString();

            if (searchKeyword != null && !searchKeyword.equals("")) {
                String speechUrl2 = "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=zh-CN&tl=ja&text=" + searchKeyword;
                webViewBrowser.loadUrl(speechUrl2);
                searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                webViewBrowser.setVisibility(View.VISIBLE);

                saveKeywordtoUserInputListView();
                saveUserInputArrayListToSharedPreferences();
            }

        }else if (speechAutoTranslationCode=="CHtoKR") {
            searchKeyword = wordInputView.getText().toString();

            if (searchKeyword != null && !searchKeyword.equals("")) {
                String speechUrl3 = "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=zh-CN&tl=ko&text=" + searchKeyword;
                webViewBrowser.loadUrl(speechUrl3);
                searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                webViewBrowser.setVisibility(View.VISIBLE);

                saveKeywordtoUserInputListView();
                saveUserInputArrayListToSharedPreferences();
            }

        }else if (speechAutoTranslationCode=="CHtoES") {
            searchKeyword = wordInputView.getText().toString();

            if (searchKeyword != null && !searchKeyword.equals("")) {
                String speechUrl4 = "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=zh-CN&tl=es&text=" + searchKeyword;
                webViewBrowser.loadUrl(speechUrl4);
                searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                webViewBrowser.setVisibility(View.VISIBLE);

                saveKeywordtoUserInputListView();
                saveUserInputArrayListToSharedPreferences();
            }

        }else if (speechAutoTranslationCode=="ENtoCH") {
            searchKeyword = wordInputView.getText().toString();

            if (searchKeyword != null && !searchKeyword.equals("")) {
                String speechUrl5 = "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=en&tl=zh-TW&text=" + searchKeyword;
                webViewBrowser.loadUrl(speechUrl5);
                searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                webViewBrowser.setVisibility(View.VISIBLE);

                saveKeywordtoUserInputListView();
                saveUserInputArrayListToSharedPreferences();
            }

        }else if (speechAutoTranslationCode=="JPtoCH") {
            searchKeyword = wordInputView.getText().toString();

            if (searchKeyword != null && !searchKeyword.equals("")) {
                String speechUrl6 = "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=ja&tl=zh-TW&text=" + searchKeyword;
                webViewBrowser.loadUrl(speechUrl6);
                searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                webViewBrowser.setVisibility(View.VISIBLE);

                saveKeywordtoUserInputListView();
                saveUserInputArrayListToSharedPreferences();
            }

        }else if (speechAutoTranslationCode=="KRtoCH") {
            searchKeyword = wordInputView.getText().toString();

            if (searchKeyword != null && !searchKeyword.equals("")) {
                String speechUrl7 = "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=ko&tl=zh-TW&text=" + searchKeyword;
                webViewBrowser.loadUrl(speechUrl7);
                searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                webViewBrowser.setVisibility(View.VISIBLE);

                saveKeywordtoUserInputListView();
                saveUserInputArrayListToSharedPreferences();
            }

        }else if (speechAutoTranslationCode=="EStoCH") {
            searchKeyword = wordInputView.getText().toString();

            if (searchKeyword != null && !searchKeyword.equals("")) {
                String speechUrl8 = "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=es&tl=zh-TW&text=" + searchKeyword;
                webViewBrowser.loadUrl(speechUrl8);
                searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                webViewBrowser.setVisibility(View.VISIBLE);

                saveKeywordtoUserInputListView();
                saveUserInputArrayListToSharedPreferences();
            }
        }



        //==============================================================================================
        // 在OnCreate外面設置 用戶選取背景圖時 的相關設定
        //==============================================================================================

        if (changeBackgroundButtonIsPressed=="yes") {
            if (resultCode == 0 || data == null) {
                return;
            }
            // 相簿
            if (requestCode == PHOTOALBUM) {
                imageForBackground = data.getData();
                try {
                    tempOutputFileForBackgroundImage = new File(getExternalCacheDir(), "temp-background_image.jpg");
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
                    //第一步:將Bitmap壓縮至字節數组輸出流ByteArrayOutputStream
                    ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                    m_phone_for_background.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
                    //第二步:利用Base64將字節數组輸出流中的數據轉換成字符串String
                    byte[] byteArray=byteArrayOutputStream.toByteArray();
                    String imageString= Base64.encodeToString(byteArray, Base64.DEFAULT);
                    //第三步:將String保存至SharedPreferences
                    SharedPreferences sharedPreferences=getSharedPreferences("testSP", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("image", imageString);
                    editor.apply();
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



    //==============================================================================================
    // 在OnCreate外面設置 存取相簿 的相關設定
    //==============================================================================================

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

            super.onPageFinished(view, url);

            progressBar.setVisibility(View.GONE);   //網頁框內容加載完成時隱藏進度條
        }

    }



    //==============================================================================================
    // 在OnCreate外面設置 按返回鍵時返回到前一個Activity 而非webView網頁的前一頁
    //==============================================================================================
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    //==============================================================================================
    // 在OnCreate外面設置 用戶點擊關鍵字輸入框以外的任一處時 收起軟鍵盤
    // The soft keyboard is hidden when a touch is done anywhere outside the "wordInputView" EditText.
    //==============================================================================================

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int[] scrcoords = new int[2];
            if (w != null) {
                w.getLocationOnScreen(scrcoords);
            }
            float x = 0;
            if (w != null) {
                x = event.getRawX() + w.getLeft() - scrcoords[0];
            }
            float y = 0;
            if (w != null) {
                y = event.getRawY() + w.getTop() - scrcoords[1];
            }

            if (w != null) {
                Log.d("Activity", "Touch event "+event.getRawX()+","+event.getRawY()+" "+x+","+y+" rect "+w.getLeft()+","+w.getTop()+","+w.getRight()+","+w.getBottom()+" coords "+scrcoords[0]+","+scrcoords[1]);
            }
            if (w != null && event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom())) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(Objects.requireNonNull(getWindow().getCurrentFocus()).getWindowToken(), 0);
            }
        }
        return ret;
    }



//==============================================================================================
// 所有helper methods
//==============================================================================================


    //==========================================================================================
    // 重啟App的helper methods
    //==========================================================================================
    public void relaunchApp() {
        Intent relaunchAppIntent = new Intent(getApplicationContext(), MainActivity.class);
        ProcessPhoenix.triggerRebirth(getApplicationContext(), relaunchAppIntent);
        Runtime.getRuntime().exit(0);
    }


    //==========================================================================================
    // 註冊登入、更改或刪除用戶名稱的helper methods
    //==========================================================================================
    public void registerLoginRenameDeleteUsername() {
        //這邊設置AlertDialog讓用戶輸入用戶名稱
        final AlertDialog.Builder registerLoginRenameDeleteUsernameAlertDialog = new AlertDialog.Builder(MainActivity.this);
        registerLoginRenameDeleteUsernameAlertDialog.setTitle(getString(R.string.Input_a_username));
        registerLoginRenameDeleteUsernameAlertDialog.setCancelable(true); //按到旁邊的空白處AlertDialog會消失
        final EditText usernameInputView = new EditText(getApplicationContext());
        registerLoginRenameDeleteUsernameAlertDialog.setView(usernameInputView);

        //AlertDialog的確定鈕，登入用戶名稱
        registerLoginRenameDeleteUsernameAlertDialog.setPositiveButton(R.string.Register_or_log_in_username, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (username!=null && !username.equals("")) { //若用戶名稱不是空的則提示已經登入了

                    Toast.makeText(getApplicationContext(), R.string.You_are_already_logged_in, Toast.LENGTH_LONG).show();

                }

                else
                {
                    String userInputUsername = usernameInputView.getText().toString();

                    if (userInputUsername!=null && !userInputUsername.equals("")) { //檢查用戶確實有輸入名稱時儲存該名稱

                        usernameSharedPreferences = getSharedPreferences("usernameSharedPreferences", MODE_PRIVATE);
                        usernameSharedPreferences.edit().putString("userName", userInputUsername).apply();

                        //同時把用戶使用雲端存儲單字紀錄的設定(localOrCloudSaveSwitchPreferences=1)存入SharedPreferences
                        localOrCloudSaveSwitchPreferences = getSharedPreferences("localOrCloudSaveSwitchPreferences", MODE_PRIVATE);
                        localOrCloudSaveSwitchPreferences.edit().putInt("CloudSaveMode", 1).apply();

                        //延遲2.5秒重啟App
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                relaunchApp();
                            }
                        };
                        Handler h =new Handler();
                        h.postDelayed(r, 2500);

                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.Logged_in_as) + userInputUsername + " " + getResources().getString(R.string.You_are_using_cloud_storage), Toast.LENGTH_LONG).show();
                    }

                    else {
                        Toast.makeText(getApplicationContext(), R.string.You_have_not_entered_any_username, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        //AlertDialog的中立鈕，更改用戶名稱
        registerLoginRenameDeleteUsernameAlertDialog.setNeutralButton(R.string.Rename_your_username, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (username!=null && !username.equals("")) {  //檢查若用戶有輸入username才執行更改名稱

                    final String temporaryUsername = usernameInputView.getText().toString();

                    if (temporaryUsername!=null && !temporaryUsername.equals("")) {


                        mChildReferenceForInputHistory.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(temporaryUsername)) {
                                    Toast.makeText(getApplicationContext(),R.string.This_username_is_already_taken,Toast.LENGTH_LONG).show();
                                }
                                else {

                                    // (1) 先創建一個新的child名為用戶新設立的名稱(temporaryUsername)
                                    mRootReference.child("Users' Input History").child(temporaryUsername).push().setValue("");
                                    mRootReference.child("Users' Vocabulary List").child(temporaryUsername).push().setValue("");

                                    // (2) 複製舊的child到新的child
                                    DatabaseReference usersInputHistorySourceNode = FirebaseDatabase.getInstance().getReference().child("Users' Input History").child(username);
                                    final DatabaseReference usersInputHistoryTargetNode = FirebaseDatabase.getInstance().getReference().child("Users' Input History").child(temporaryUsername);
                                    ValueEventListener valueEventListenerForUsersInputHistory = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            usersInputHistoryTargetNode.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isComplete()) {
                                                        // (3) 把舊的child砍掉
                                                        mChildReferenceForInputHistory.child(username).removeValue();
                                                        Log.d("User Input History copy", "Success!");
                                                    } else {
                                                        Log.d("User Input History copy", "Copy failed!");
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                                    };
                                    usersInputHistorySourceNode.addListenerForSingleValueEvent(valueEventListenerForUsersInputHistory);


                                    DatabaseReference usersVocabularyListSourceNode = FirebaseDatabase.getInstance().getReference().child("Users' Vocabulary List").child(username);
                                    final DatabaseReference usersVocabularyListTargetNode = FirebaseDatabase.getInstance().getReference().child("Users' Vocabulary List").child(temporaryUsername);
                                    ValueEventListener valueEventListenerForUsersVocabularyList = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            usersVocabularyListTargetNode.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isComplete()) {
                                                        // (3) 把舊的child砍掉
                                                        mChildReferenceForVocabularyList.child(username).removeValue();
                                                        Log.d("User Vocab List copy", "Success!");
                                                    } else {
                                                        Log.d("User Vocab List copy", "Copy failed!");
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                                    };
                                    usersVocabularyListSourceNode.addListenerForSingleValueEvent(valueEventListenerForUsersVocabularyList);


                                    //讓用戶輸入的新名稱(temporaryUsername)變成username，並存入sharedPreferences
                                    usernameSharedPreferences = getSharedPreferences("usernameSharedPreferences", MODE_PRIVATE);
                                    usernameSharedPreferences.edit().putString("userName", temporaryUsername).apply();


                                    //同時把用戶使用雲端存儲單字紀錄的設定(localOrCloudSaveSwitchPreferences=1)存入SharedPreferences
                                    localOrCloudSaveSwitchPreferences = getSharedPreferences("localOrCloudSaveSwitchPreferences", MODE_PRIVATE);
                                    localOrCloudSaveSwitchPreferences.edit().putInt("CloudSaveMode", 1).apply();


                                    //延遲2.5秒重啟App
                                    Runnable r = new Runnable() {
                                        @Override
                                        public void run() {
                                            relaunchApp();
                                        }
                                    };
                                    Handler h =new Handler();
                                    h.postDelayed(r, 2500);

                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.Your_new_username_is) + temporaryUsername + " " + getResources().getString(R.string.You_are_using_cloud_storage), Toast.LENGTH_LONG).show();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

                    else {
                        Toast.makeText(getApplicationContext(), R.string.You_have_not_entered_any_username, Toast.LENGTH_LONG).show();
                    }
                }

                else {
                    Toast.makeText(getApplicationContext(), R.string.You_have_not_entered_any_username, Toast.LENGTH_LONG).show();
                }
            }
        });

        //AlertDialog的取消鈕，刪除用戶名稱
        registerLoginRenameDeleteUsernameAlertDialog.setNegativeButton(R.string.Delete_username, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (username!=null && !username.equals("")) { //檢查若用戶有輸入username才刪除名稱

                    mChildReferenceForInputHistory.child(username).removeValue();
                    mChildReferenceForVocabularyList.child(username).removeValue();

                    //清除用戶名稱
                    username=null;
                    usernameSharedPreferences = getSharedPreferences("usernameSharedPreferences", MODE_PRIVATE);
                    usernameSharedPreferences.edit().putString("userName", username).apply();

                    //同時把用戶使用雲端存儲單字紀錄的設定(localOrCloudSaveSwitchPreferences=0)存入SharedPreferences
                    localOrCloudSaveSwitchPreferences = getSharedPreferences("localOrCloudSaveSwitchPreferences", MODE_PRIVATE);
                    localOrCloudSaveSwitchPreferences.edit().putInt("CloudSaveMode", 0).apply();

                    //延遲2.5秒重啟App
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            relaunchApp();
                        }
                    };
                    Handler h =new Handler();
                    h.postDelayed(r, 2500);

                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.Username_deleted) + " " + getResources().getString(R.string.You_are_using_local_storage), Toast.LENGTH_LONG).show();
                }

                else {
                    Toast.makeText(getApplicationContext(), R.string.You_have_not_entered_any_username, Toast.LENGTH_LONG).show();
                }
            }
        });

        //把AlertDialog顯示出來
        registerLoginRenameDeleteUsernameAlertDialog.create().show();
    }


    //==========================================================================================
    // Spinner的helper methods
    //==========================================================================================

    /**
     * Other functions spinner & SpinnerAdapter
     */
    public void otherFunctionsSpinnerOriginal() {       //專業版

        otherFunctionsSpinner = findViewById(R.id.Other_functions_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> OtherFunctionsSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.Other_functions_spinner_array_original, R.layout.customized_spinner_item);
        // Specify the layout to use when the list of choices appears
        OtherFunctionsSpinnerAdapter.setDropDownViewResource(R.layout.customized_spinner_item);
        // Apply the adapter to the spinner
        otherFunctionsSpinner.setAdapter(OtherFunctionsSpinnerAdapter);

        otherFunctionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0){
                    return;

                } else if (position == 1) {  //更換背景圖

                    changeBackgroundButtonIsPressed="yes";
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
                    startActivityForResult(intent, PHOTOALBUM);

                } else if (position == 2) {  // 恢復成預設的背景圖

                    Bitmap defaultBackgroundBmp = BitmapFactory.decodeResource(getResources(), R.drawable.universe2);  //透過BitmapFactory把Drawable轉換成Bitmap
                    m_phone_for_background = defaultBackgroundBmp;
                    //第一步:將Bitmap壓縮至字節數组輸出流ByteArrayOutputStream
                    ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                    //第二步:利用Base64將字節數组輸出流中的數據轉換成字符串String
                    byte[] byteArray=byteArrayOutputStream.toByteArray();
                    String imageString= Base64.encodeToString(byteArray, Base64.DEFAULT);
                    //第三步:將String存至SharedPreferences
                    SharedPreferences sharedPreferences=getSharedPreferences("testSP", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("image", imageString);
                    editor.apply();

                    recreate(); //重新生成頁面
                    Toast.makeText(getApplicationContext(), R.string.Reset_to_default_backgorund_image_message, Toast.LENGTH_LONG).show();

                } else if (position == 3) {  //清除搜尋紀錄

                    //這邊設置AlertDialog讓用戶確認是否真要清除列表
                    AlertDialog.Builder doYouReallyWantToClearListAlertDialog = new AlertDialog.Builder(MainActivity.this);
                    doYouReallyWantToClearListAlertDialog.setTitle(getString(R.string.Do_you_really_want_to_clear_the_list));
                    doYouReallyWantToClearListAlertDialog.setCancelable(false); //按到旁邊的空白處AlertDialog也不會消失
                    doYouReallyWantToClearListAlertDialog.setView(R.layout.custom_alert_dialog_dictionary_providers); //沿用字典選單的佈局檔

                    //AlertDialog的確定鈕，清除列表
                    doYouReallyWantToClearListAlertDialog.setPositiveButton(R.string.Confirm, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            userInputArraylist.clear(); //清除userInputArraylsit中登錄的用戶搜尋紀錄
                            saveUserInputArrayListToSharedPreferences ();
                            Toast.makeText(getApplicationContext(), getString(R.string.Search_records_cleared), Toast.LENGTH_LONG).show();

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

                } else if (position == 4) {

                    setDefaultDictionariesOriginal(); //設置專業版預設字典

                } else if (position == 5) {//註冊登入、更改或刪除用戶名稱

                    registerLoginRenameDeleteUsername();

                } else if (position == 6) {
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

        // 透過OnClickListener將ImageView和Spinner綁定
        otherFunctionsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otherFunctionsSpinner.performClick();
            }
        });
    }


    public void otherFunctionsSpinnerSimplified() {     //簡易版

        final Spinner otherFunctionsSpinner = findViewById(R.id.Other_functions_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> OtherFunctionsSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.Other_functions_spinner_array_simplified, R.layout.customized_spinner_item);
        // Specify the layout to use when the list of choices appears
        OtherFunctionsSpinnerAdapter.setDropDownViewResource(R.layout.customized_spinner_item);
        // Apply the adapter to the spinner
        otherFunctionsSpinner.setAdapter(OtherFunctionsSpinnerAdapter);

        otherFunctionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0){
                    return;

                } else if (position == 1) {  //更換背景圖

                    changeBackgroundButtonIsPressed="yes";
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
                    startActivityForResult(intent, PHOTOALBUM);

                } else if (position == 2) {  // 恢復成預設的背景圖

                    Bitmap defaultBackgroundBmp = BitmapFactory.decodeResource(getResources(), R.drawable.universe2);  //透過BitmapFactory把Drawable轉換成Bitmap
                    m_phone_for_background = defaultBackgroundBmp;
                    //第一步:將Bitmap壓縮至字節數组輸出流ByteArrayOutputStream
                    ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                    //第二步:利用Base64將字節數组輸出流中的數據轉換成字符串String
                    byte[] byteArray=byteArrayOutputStream.toByteArray();
                    String imageString= Base64.encodeToString(byteArray, Base64.DEFAULT);
                    //第三步:將String存至SharedPreferences
                    SharedPreferences sharedPreferences=getSharedPreferences("testSP", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("image", imageString);
                    editor.apply();

                    recreate(); //重新生成頁面
                    Toast.makeText(getApplicationContext(), R.string.Reset_to_default_backgorund_image_message, Toast.LENGTH_LONG).show();

                } else if (position == 3) {

                    setDefaultDictionariesSimplified();  //設置簡易版預設字典

                } else if (position == 4) {//註冊登入、更改或刪除用戶名稱

                    registerLoginRenameDeleteUsername();

                } else if (position == 5) {  //清除搜尋紀錄

                    //這邊設置AlertDialog讓用戶確認是否真要清除列表
                    AlertDialog.Builder doYouReallyWantToClearListAlertDialog = new AlertDialog.Builder(MainActivity.this);
                    doYouReallyWantToClearListAlertDialog.setTitle(getString(R.string.Do_you_really_want_to_clear_the_list));
                    doYouReallyWantToClearListAlertDialog.setCancelable(false); //按到旁邊的空白處AlertDialog也不會消失
                    doYouReallyWantToClearListAlertDialog.setView(R.layout.custom_alert_dialog_dictionary_providers); //沿用字典選單的佈局檔

                    //AlertDialog的確定鈕，清除列表
                    doYouReallyWantToClearListAlertDialog.setPositiveButton(R.string.Confirm, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            userInputArraylist.clear(); //清除userInputArraylsit中登錄的用戶搜尋紀錄
                            saveUserInputArrayListToSharedPreferences ();
                            Toast.makeText(getApplicationContext(), getString(R.string.Search_records_cleared), Toast.LENGTH_LONG).show();

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

                otherFunctionsSpinner.setAdapter(OtherFunctionsSpinnerAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 透過OnClickListener將ImageView和Spinner綁定
        otherFunctionsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otherFunctionsSpinner.performClick();
            }
        });
    }


    /**
     * Speech Recognition Spinner & Spinner Adapters
     */
    public void speechRecognitionSpinnerOriginal() {        //專業版

        SpeechRecognitionSpinner = findViewById(R.id.Speech_recognition_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> SpeechRecognitionAdapter = ArrayAdapter.createFromResource(this,
                R.array.Speech_recognition_spinner_array_original, R.layout.customized_spinner_item);
        // Specify the layout to use when the list of choices appears
        SpeechRecognitionAdapter.setDropDownViewResource(R.layout.customized_spinner_item);
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

        voiceRecognitionImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpeechRecognitionSpinner.performClick();
            }
        });

    }


    public void speechRecognitionSpinnerSimplified() {      //簡易版

        final Spinner SpeechRecognitionSpinner = findViewById(R.id.Speech_recognition_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> SpeechRecognitionAdapter = ArrayAdapter.createFromResource(this,
                R.array.Speech_recognition_spinner_array_simplified, R.layout.customized_spinner_item);
        // Specify the layout to use when the list of choices appears
        SpeechRecognitionAdapter.setDropDownViewResource(R.layout.customized_spinner_item);
        // Apply the adapter to the spinner
        SpeechRecognitionSpinner.setAdapter(SpeechRecognitionAdapter);

        SpeechRecognitionSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {    //此行以下設置語音辨識 + 自動翻譯
                    return;

                }else if (position == 1) {
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

                }else if (position == 2) {
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

                }else if (position == 3) {
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

                }else if (position == 4) {
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

                }

                SpeechRecognitionSpinner.setAdapter(SpeechRecognitionAdapter);

            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        voiceRecognitionImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpeechRecognitionSpinner.performClick();
            }
        });

    }


    /**
     * ORCModeSpinner & Spinner Adapters
     */
    public void OCRModeSpinnerOriginal() {      //專業版

        OCRModeSpinner = findViewById(R.id.OCR_mode_spinner);
        // Create an customized Adapter using the specified ArrayList and a customized spinner layout
        mOcrSpinnerAdapter = new DictionayItemAdapter (this,mOcrSpinnerItemListOriginal);
        // Apply the adapter to the spinner
        OCRModeSpinner.setAdapter(mOcrSpinnerAdapter);

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

                OCRModeSpinner.setAdapter(mOcrSpinnerAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 透過OnClickListener將ImageView和Spinner綁定
        ocrImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OCRModeSpinner.performClick();
            }
        });

    }



    public void OCRModeSpinnerSimplified() {        //簡易版

        final Spinner OCRModeSpinner = findViewById(R.id.OCR_mode_spinner);
        // Create an customized Adapter using the specified ArrayList and a customized spinner layout
        mOcrSpinnerAdapter = new DictionayItemAdapter (this,mOcrSpinnerItemListSimplified);
        // Apply the adapter to the spinner
        OCRModeSpinner.setAdapter(mOcrSpinnerAdapter);

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

                OCRModeSpinner.setAdapter(mOcrSpinnerAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 透過OnClickListener將ImageView和Spinner綁定
        ocrImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OCRModeSpinner.performClick();
            }
        });

    }



    /**
     * EnDictionarySpinner & Spinner Adapters
     */
    public void EnDictionarySpinnerOriginal() {     //專業版

        EnDictionarySpinner = findViewById(R.id.EN_dictionary_providers_spinner);
        // Create an customized Adapter using the specified ArrayList and a customized spinner layout
        mEnglishDictionarySpinnerAdapter = new DictionayItemAdapter(this, mEnglishDictionarySpinnerItemListOriginal);
        EnDictionarySpinner.setAdapter(mEnglishDictionarySpinnerAdapter);

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
                    String cidian8Url= "http://www.cidian8.com/ec/?w="+searchKeyword;
                    webViewBrowser.loadUrl(cidian8Url);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 5) {
                    String voicetubeUrl= "https://tw.voicetube.com/definition/"+searchKeyword;
                    webViewBrowser.loadUrl(voicetubeUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 6) {
                    String cambridgeDictionaryUrl= "https://dictionary.cambridge.org/zht/詞典/英語-漢語-繁體/"+searchKeyword;
                    webViewBrowser.loadUrl(cambridgeDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 7){
                    String merriamDictionaryUrl= "https://www.merriam-webster.com/dictionary/"+searchKeyword;
                    webViewBrowser.loadUrl(merriamDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 8) {
                    String collinsDictionaryUrl= "https://www.collinsdictionary.com/dictionary/english/"+searchKeyword;
                    webViewBrowser.loadUrl(collinsDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 9) {
                    String oxfordDictionaryUrl= "https://en.oxforddictionaries.com/definition/"+searchKeyword;
                    webViewBrowser.loadUrl(oxfordDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 10) {
                    String vocabularyDotComUrl= "https://www.vocabulary.com/dictionary/"+searchKeyword;
                    webViewBrowser.loadUrl(vocabularyDotComUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 11) {
                    String dictionaryDotComUrl= "https://www.dictionary.com/browse/"+searchKeyword;
                    webViewBrowser.loadUrl(dictionaryDotComUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 12) {
                    String theFreeDictionaryUrl= "https://www.thefreedictionary.com/"+searchKeyword;
                    webViewBrowser.loadUrl(theFreeDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 13) {
                    String yourDictionaryUrl= "https://www.yourdictionary.com/"+searchKeyword;
                    webViewBrowser.loadUrl(yourDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 14) {
                    String longmanDictionaryUrl= "https://www.ldoceonline.com/dictionary/"+searchKeyword;
                    webViewBrowser.loadUrl(longmanDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 15) {
                    String greensDictionaryOfSlangUrl= "https://greensdictofslang.com/search/basic?q="+searchKeyword;
                    webViewBrowser.loadUrl(greensDictionaryOfSlangUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 16) {
                    String wiktionaryUrl= "https://en.wiktionary.org/wiki/"+searchKeyword;
                    webViewBrowser.loadUrl(wiktionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 17) {
                    String wordHippoUrl= "https://www.wordhippo.com/what-is/another-word-for/"+searchKeyword+".html";
                    webViewBrowser.loadUrl(wordHippoUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 18) {
                    String onelookUrl= "https://www.onelook.com/thesaurus/?s="+searchKeyword;
                    webViewBrowser.loadUrl(onelookUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 19) {
                    String businessDictionaryUrl= "http://www.businessdictionary.com/definition/"+searchKeyword+".html";
                    webViewBrowser.loadUrl(businessDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 20) {
                    String slangDictionary= "http://www.yiym.com/?s="+searchKeyword;
                    webViewBrowser.loadUrl(slangDictionary);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 21) {
                    String youglishUrl= "https://youglish.com/search/"+searchKeyword+"/all?";
                    webViewBrowser.loadUrl(youglishUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 22) {
                    String academiaDictionaryUrl= "http://www.scidict.org/index.aspx?word="+searchKeyword;
                    webViewBrowser.loadUrl(academiaDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 23) {
                    String academiaDictionaryAndEncyclopediaUrl= "https://en.academic.ru/searchall.php?SWord="+searchKeyword+"&from=xx&to=en&did=&stype=0#";
                    webViewBrowser.loadUrl(academiaDictionaryAndEncyclopediaUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 24) {
                    String techDicoUrl= "https://www.techdico.com/translation/english-chinese/"+searchKeyword+".html";
                    webViewBrowser.loadUrl(techDicoUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 25) {
                    String bioMedicalDictionaryUrl= "http://dict.bioon.com/search.asp?txtitle="+searchKeyword+"&searchButton=查词典&matchtype=0";
                    webViewBrowser.loadUrl(bioMedicalDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 26) {
                    String isPluralDictionaryUrl= "https://www.isplural.com/plural_singular/"+searchKeyword;
                    webViewBrowser.loadUrl(isPluralDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 27) {
                    String lingoHelpPrepositionUrl= "https://lingohelp.me/q/?w="+searchKeyword;
                    webViewBrowser.loadUrl(lingoHelpPrepositionUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 28) {
                    String wordWebUrl= "https://www.wordwebonline.com/search.pl?w="+searchKeyword;
                    webViewBrowser.loadUrl(wordWebUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 29) {
                    String powerThesaurusSynonymUrl= "https://www.powerthesaurus.org/"+searchKeyword+"/synonyms";
                    webViewBrowser.loadUrl(powerThesaurusSynonymUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 30) {
                    String powerThesaurusAntonymsUrl= "https://www.powerthesaurus.org/"+searchKeyword+"/antonyms";
                    webViewBrowser.loadUrl(powerThesaurusAntonymsUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }

                EnDictionarySpinner.setAdapter(mEnglishDictionarySpinnerAdapter);
                //再生成一次Adapter防止點按過的選項失效無法使用，以下同。

                saveKeywordtoUserInputListView ();
                saveUserInputArrayListToSharedPreferences ();

                browserSwitch.setChecked(true);
                //把網頁框開關狀態設定成"開啟"，以免載入網頁時開關沒有變成開啟的狀態
                browserNavigateBack.setVisibility(View.VISIBLE);
                browserNavigateForward.setVisibility(View.VISIBLE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }


    public void EnDictionarySpinnerSimplified() {       //簡易版

        final Spinner EnDictionarySpinner = findViewById(R.id.EN_dictionary_providers_spinner);
        // Create an customized Adapter using the specified ArrayList and a customized spinner layout
        mEnglishDictionarySpinnerAdapter = new DictionayItemAdapter(this, mEnglishDictionarySpinnerItemListSimplified);
        EnDictionarySpinner.setAdapter(mEnglishDictionarySpinnerAdapter);

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

                }

                EnDictionarySpinner.setAdapter(mEnglishDictionarySpinnerAdapter);
                //再生成一次Adapter防止點按過的選項失效無法使用，以下同。

                saveKeywordtoUserInputListView ();
                saveUserInputArrayListToSharedPreferences ();

                browserSwitch.setChecked(true);
                //把網頁框開關狀態設定成"開啟"，以免載入網頁時開關沒有變成開啟的狀態
                browserNavigateBack.setVisibility(View.VISIBLE);
                browserNavigateForward.setVisibility(View.VISIBLE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }


    /**
     * JpDictionarySpinner & Spinner Adapters
     */
    public void JpDictionarySpinnerOriginal() {     //專業版

        final Spinner JpDictionarySpinner = findViewById(R.id.JP_dictionary_providers_spinner);
        // Create an customized Adapter using the specified ArrayList and a customized spinner layout
        mJapaneseDictionarySpinnerAdapter = new DictionayItemAdapter(this, mJapaneseDictionarySpinnerItemListOriginal);
        JpDictionarySpinner.setAdapter(mJapaneseDictionarySpinnerAdapter);

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
                    String CidianwangUrl= "http://m.cidianwang.com/search/jp/?q="+searchKeyword+"&y=1";
                    webViewBrowser.loadUrl(CidianwangUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 12) {
                    String gooDictionaryUrl= "https://dictionary.goo.ne.jp/srch/jn/"+searchKeyword+"/m0u/";
                    webViewBrowser.loadUrl(gooDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 13) {
                    String sanseidoUrl= "https://www.sanseido.biz/User/Dic/Index.aspx?TWords="+searchKeyword+"&st=0&DORDER=151617&DailyJJ=checkbox&DailyEJ=checkbox&DailyJE=checkbox";
                    webViewBrowser.loadUrl(sanseidoUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 14) {
                    String kotobank= "https://kotobank.jp/word/"+searchKeyword;
                    webViewBrowser.loadUrl(kotobank);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 15) {
                    String jlogosUrl= "http://s.jlogos.com/list.html?keyword="+searchKeyword+"&opt_val=0";
                    webViewBrowser.loadUrl(jlogosUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 16) {
                    String industryDictionaryUrl= "https://www.sangyo-honyaku.jp/dictionaries/index/search_info:"+searchKeyword+"_ＩＴ・機械・電気電子";
                    webViewBrowser.loadUrl(industryDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 17) {
                    String KanjiDictionaryUrl= "https://kanji.jitenon.jp/cat/search.php?getdata="+searchKeyword+"&search=fpart&search2=twin";
                    webViewBrowser.loadUrl(KanjiDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 18) {
                    String eijiroDictionryUrl= "https://eow.alc.co.jp/search?q="+searchKeyword;
                    webViewBrowser.loadUrl(eijiroDictionryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 19) {
                    String whatIsItInEnglishUrl= "https://eikaiwa.dmm.com/uknow/search/?keyword="+searchKeyword;
                    webViewBrowser.loadUrl(whatIsItInEnglishUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 20) {
                    String jishoUrl= "https://jisho.org/search/"+searchKeyword;
                    webViewBrowser.loadUrl(jishoUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 21) {
                    String CambridgeJPtoENUrl= "https://dictionary.cambridge.org/zht/詞典/japanese-english/"+searchKeyword;
                    webViewBrowser.loadUrl(CambridgeJPtoENUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 22) {
                    String CambridgeENtoJPUrl= "https://dictionary.cambridge.org/zht/詞典/英語-日語/"+searchKeyword;
                    webViewBrowser.loadUrl(CambridgeENtoJPUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 23) {
                    String wwwjdicJpToEnUrl= "http://www.edrdg.org/cgi-bin/wwwjdic/wwwjdic?1MUJ"+searchKeyword;
                    webViewBrowser.loadUrl(wwwjdicJpToEnUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 24) {
                    String wwwjdicEnToJaUrl= "http://www.edrdg.org/cgi-bin/wwwjdic/wwwjdic?1MDE"+searchKeyword;
                    webViewBrowser.loadUrl(wwwjdicEnToJaUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 25) {
                    String kanjiRecognizerUrl= "https://kanji.sljfaq.org/draw-canvas.html";
                    webViewBrowser.loadUrl(kanjiRecognizerUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }

                JpDictionarySpinner.setAdapter(mJapaneseDictionarySpinnerAdapter);

                saveKeywordtoUserInputListView ();
                saveUserInputArrayListToSharedPreferences ();

                browserSwitch.setChecked(true);
                browserNavigateBack.setVisibility(View.VISIBLE);
                browserNavigateForward.setVisibility(View.VISIBLE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    public void JpDictionarySpinnerSimplified() {       //簡易版

        JpDictionarySpinner = findViewById(R.id.JP_dictionary_providers_spinner);
        // Create an customized Adapter using the specified ArrayList and a customized spinner layout
        mJapaneseDictionarySpinnerAdapter = new DictionayItemAdapter(this, mJapaneseDictionarySpinnerItemListSimplified);
        JpDictionarySpinner.setAdapter(mJapaneseDictionarySpinnerAdapter);

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

                }else if (position == 3) {
                    String DaJPtoCHDictionaryUrl= "http://dict.asia/jc/"+searchKeyword;
                    webViewBrowser.loadUrl(DaJPtoCHDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 4) {
                    String DaCHtoJPDictionaryUrl= "http://dict.asia/cj/"+searchKeyword;
                    webViewBrowser.loadUrl(DaCHtoJPDictionaryUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }

                JpDictionarySpinner.setAdapter(mJapaneseDictionarySpinnerAdapter);

                saveKeywordtoUserInputListView ();
                saveUserInputArrayListToSharedPreferences ();

                browserSwitch.setChecked(true);
                browserNavigateBack.setVisibility(View.VISIBLE);
                browserNavigateForward.setVisibility(View.VISIBLE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }



    /**
     * GoogleWordSearchSpinner & Spinner Adapters
     */
    public void GoogleWordSearchSpinnerOriginal() {     //專業版

        final Spinner GoogleWordSearchSpinner = findViewById(R.id.Google_word_searcher_spinner);
        // Create an customized Adapter using the specified ArrayList and a customized spinner layout
        mGoogleWordSearchSpinnerAdapter = new DictionayItemAdapter(this, mGoogleWordSearchSpinnerItemListOriginal);
        GoogleWordSearchSpinner.setAdapter(mGoogleWordSearchSpinnerAdapter);

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

                GoogleWordSearchSpinner.setAdapter(mGoogleWordSearchSpinnerAdapter);

                saveKeywordtoUserInputListView ();
                saveUserInputArrayListToSharedPreferences ();

                browserSwitch.setChecked(true);
                browserNavigateBack.setVisibility(View.VISIBLE);
                browserNavigateForward.setVisibility(View.VISIBLE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }


    public void GoogleWordSearchSpinnerSimplified() {       //簡易版

        GoogleWordSearchSpinner = findViewById(R.id.Google_word_searcher_spinner);
        // Create an customized Adapter using the specified ArrayList and a customized spinner layout
        mGoogleWordSearchSpinnerAdapter = new DictionayItemAdapter(this, mGoogleWordSearchSpinnerItemListSimplified);
        GoogleWordSearchSpinner.setAdapter(mGoogleWordSearchSpinnerAdapter);

        GoogleWordSearchSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                searchKeyword = wordInputView.getText().toString();

                if (position == 0){
                    return;

                }
                if (position == 1) {
                    String GoogleTranslateToCHTWUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=zh-TW&text="+searchKeyword;
                    webViewBrowser.loadUrl(GoogleTranslateToCHTWUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 2) {
                    String GoogleTranslateToCHCNUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=zh-CN&text="+searchKeyword;
                    webViewBrowser.loadUrl(GoogleTranslateToCHCNUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 3) {
                    String imageSearchUrl= "http://images.google.com/search?tbm=isch&q="+searchKeyword;
                    webViewBrowser.loadUrl(imageSearchUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }

                GoogleWordSearchSpinner.setAdapter(mGoogleWordSearchSpinnerAdapter);

                saveKeywordtoUserInputListView ();
                saveUserInputArrayListToSharedPreferences ();

                browserSwitch.setChecked(true);
                browserNavigateBack.setVisibility(View.VISIBLE);
                browserNavigateForward.setVisibility(View.VISIBLE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }


    /**
     * SentenceSearchSpinner & Spinner Adapters
     */
    public void sentenceSearchSpinnerOriginal() {       //專業版

        SentenceSearchSpinner = findViewById(R.id.Sentence_searcher_spinner);
        // Create an customized Adapter using the specified ArrayList and a customized spinner layout
        mSentenceSearchSpinnerAdapter = new DictionayItemAdapter(this, mSentenceSearchSpinnerItemListOriginal);
        SentenceSearchSpinner.setAdapter(mSentenceSearchSpinnerAdapter);

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

                SentenceSearchSpinner.setAdapter(mSentenceSearchSpinnerAdapter);

                saveKeywordtoUserInputListView ();
                saveUserInputArrayListToSharedPreferences ();

                browserSwitch.setChecked(true);
                browserNavigateBack.setVisibility(View.VISIBLE);
                browserNavigateForward.setVisibility(View.VISIBLE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }


    /**
     * MiscellaneousSpinner & Spinner Adapters
     */
    public void MiscellaneousSpinnerOriginal() {        //專業版

        MiscellaneousSpinner = findViewById(R.id.Miscellaneous_searcher_spinner);
        // Create an customized Adapter using the specified ArrayList and a customized spinner layout
        mMiscellaneousSpinnerAdapter = new DictionayItemAdapter(this, mMiscellaneousSpinnerItemListOriginal);
        MiscellaneousSpinner.setAdapter(mMiscellaneousSpinnerAdapter);

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

                }else if (position == 4){
                    String britannicaUrl= "https://www.britannica.com/search?query="+searchKeyword;
                    webViewBrowser.loadUrl(britannicaUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 5) {
                    String forvoUrl= "https://forvo.com/search/"+searchKeyword;
                    webViewBrowser.loadUrl(forvoUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 6){
                    String wikidiffUrl= "https://wikidiff.com/"+searchKeyword;
                    webViewBrowser.loadUrl(wikidiffUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 7) {
                    String netspeakUrl= "http://www.netspeak.org/#query="+searchKeyword;
                    webViewBrowser.loadUrl(netspeakUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 8) {
                    String kotonohaJapaneseCorpusUrl= "http://www.kotonoha.gr.jp/shonagon/search_form";
                    webViewBrowser.loadUrl(kotonohaJapaneseCorpusUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 9) {
                    String tatoebaUrl= "https://tatoeba.org/eng/sentences/search?query=%3D\"" + searchKeyword + "\"&from=und&to=none&user=&orphans=no&unapproved=no&has_audio=&tags=&list=&native=&trans_filter=limit&trans_to=und&trans_link=&trans_user=&trans_orphan=&trans_unapproved=&trans_has_audio=&sort=relevance&sort_reverse=";
                    webViewBrowser.loadUrl(tatoebaUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 10) {
                    String yomikatawaUrl= "https://yomikatawa.com/kanji/"+searchKeyword+"?search=1";
                    webViewBrowser.loadUrl(yomikatawaUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 11) {
                    String ChigaihaUrl= "https://cse.google.co.jp/cse?cx=partner-pub-1137871985589263%3A3025760782&ie=UTF-8&q="+searchKeyword;
                    webViewBrowser.loadUrl(ChigaihaUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 12) {
                    String suzukikunUrl= "http://www.gavo.t.u-tokyo.ac.jp/ojad/search/index/sortprefix:accent/narabi1:kata_asc/narabi2:accent_asc/narabi3:mola_asc/yure:visible/curve:invisible/details:invisible/limit:20/word:"+searchKeyword;
                    webViewBrowser.loadUrl(suzukikunUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 13) {
                    String learnWithKakUrl= "https://www.learnwithkak.com/?s="+searchKeyword;
                    webViewBrowser.loadUrl(learnWithKakUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 14) {
                    String homeOfEnglishUrl= "https://cse.google.com.tw/cse?cx=partner-pub-2581231837251838:8766161994&ie=UTF-8&q=" +searchKeyword + "&ref=";
                    webViewBrowser.loadUrl(homeOfEnglishUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 15) {
                    String bbcLearningEnglishUrl= "https://elt.rti.org.tw/?s=" +searchKeyword;
                    webViewBrowser.loadUrl(bbcLearningEnglishUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 16) {
                    String voiceOfAmericaUrl= "https://www.voachinese.com/s?k=" + searchKeyword + "&tab=all&pi=1&r=any&pp=10";
                    webViewBrowser.loadUrl(voiceOfAmericaUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 17) {
                    String teacherSammyUrl= "https://www.google.com.tw/search?client=chrome&ei=V1oXXpr3JofY0gTApKWICA&q=" + searchKeyword + "+site%3Ablogs.teachersammy.com&oq=" + searchKeyword + "+site%3Ablogs.teachersammy.com&gs_l=psy-ab.12...375586.375896..377504...0.0..0.46.162.4......0....1..gws-wiz.5Ti7HhS0HMM&ved=0ahUKEwja_o6y_fbmAhUHrJQKHUBSCYEQ4dUDCAo";
                    webViewBrowser.loadUrl(teacherSammyUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 18) {
                    String michaelChuganiUrl= "https://www.google.com.tw/search?client=chrome&ei=QFwXXvmxNqGvmAXw86SYAg&q=" + searchKeyword + "+site%3Ahd.stheadline.com%2Fnews%2Fcolumns%2F126%2F&oq=" + searchKeyword + "+site%3Ahd.stheadline.com%2Fnews%2Fcolumns%2F126%2F&gs_l=psy-ab.3...581494.590145..590655...0.0..0.42.186.5......0....2j1..gws-wiz.iK_8eHTvAxM&ved=0ahUKEwj50bSb__bmAhWhF6YKHfA5CSMQ4dUDCAo&uact=5";
                    webViewBrowser.loadUrl(michaelChuganiUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 19) {
                    String grammaristUrl= "https://www.google.com.tw/search?client=chrome&ei=K2EXXrXbJ4G0mAWxs6K4DQ&q=" + searchKeyword + "+site%3Agrammarist.com&oq=" + searchKeyword + "+site%3Agrammarist.com&gs_l=psy-ab.3...42452.42877..43383...0.0..0.40.192.5......0....2j1..gws-wiz.1BMyHFH7WrY&ved=0ahUKEwj1nNHzg_fmAhUBGqYKHbGZCNcQ4dUDCAo&uact=5";
                    webViewBrowser.loadUrl(grammaristUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }else if (position == 20) {
                    String kennyEnglishUrl= "https://www.google.com.tw/search?client=chrome&hs=4qz&ei=jZ0YXvyACcOsmAX26bLYCA&q=" + searchKeyword + "++site%3Ablog.udn.com%2Feuoy789&oq=" + searchKeyword + "++site%3Ablog.udn.com%2Feuoy789&gs_l=psy-ab.3...9983.10536..10934...0.0..0.36.176.5......0....1..gws-wiz.tZEzxZ6ZaDc&ved=0ahUKEwi8-6vQsfnmAhVDFqYKHfa0DIsQ4dUDCAo&uact=5";
                    webViewBrowser.loadUrl(kennyEnglishUrl);
                    searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                    webViewBrowser.setVisibility(View.VISIBLE);

                }

                MiscellaneousSpinner.setAdapter(mMiscellaneousSpinnerAdapter);

                saveKeywordtoUserInputListView ();
                saveUserInputArrayListToSharedPreferences ();

                browserSwitch.setChecked(true);
                browserNavigateBack.setVisibility(View.VISIBLE);
                browserNavigateForward.setVisibility(View.VISIBLE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }



    //==============================================================================================
    // 把作為helper method的Spinners都統包起來
    //==============================================================================================

    /**
     * 專業版的Spinners
     */
    public void spinnersForOriginalLayout() {

        otherFunctionsSpinnerOriginal();
        speechRecognitionSpinnerOriginal();
        OCRModeSpinnerOriginal();
        EnDictionarySpinnerOriginal();
        JpDictionarySpinnerOriginal();
        GoogleWordSearchSpinnerOriginal();
        sentenceSearchSpinnerOriginal();
        MiscellaneousSpinnerOriginal();

        shownItemsInOriginalLayout();  //專業版時要顯示的物件

    }


    /**
     * 簡易版的Spinners
     */
    public void spinnersForSimplifiedLayout() {

        otherFunctionsSpinnerSimplified();
        OCRModeSpinnerSimplified();
        EnDictionarySpinnerSimplified();
        JpDictionarySpinnerSimplified();
        GoogleWordSearchSpinnerSimplified();
        speechRecognitionSpinnerSimplified();

        hiddenItemsInSimplifiedLayout(); //簡易版時要隱藏的物件

    }


    //==============================================================================================
    // 設置專業版和簡易版時各自要顯示或隱藏之物件的 helper methods
    //==============================================================================================

    /**
     * 專業版介面要顯示的物件
     */
    public void shownItemsInOriginalLayout() {

        MiscellaneousSpinner.setVisibility(View.VISIBLE);
        SentenceSearchSpinner.setVisibility(View.VISIBLE);
        selectSentenceSearcherView.setVisibility(View.VISIBLE);
        miscellaneousView.setVisibility(View.VISIBLE);

    }


    /**
     * 專業版介面要隱藏的物件
     */
    public void hiddenItemsInSimplifiedLayout() {
        selectSentenceSearcherView = findViewById(R.id.Select_Sentence_Searcher_View);
        miscellaneousView = findViewById(R.id.Miscellaneous_View);
        SentenceSearchSpinner = findViewById(R.id.Sentence_searcher_spinner);
        MiscellaneousSpinner = findViewById(R.id.Miscellaneous_searcher_spinner);

        selectSentenceSearcherView.setVisibility(View.GONE);
        miscellaneousView.setVisibility(View.GONE);
        SentenceSearchSpinner.setVisibility(View.GONE);
        MiscellaneousSpinner.setVisibility(View.GONE);

    }



    //==============================================================================================
    // 客製化轉頁板與簡易版ActionBar的Helper method
    //==============================================================================================

    /**
     * 專業版ActionBar介面
     */
    public void customActionBarPro() {

        customActionBarTextview.setLayoutParams(layoutparams);
        if (username!=null && !username.equals("")) {
            customActionBarTextview.setText(getString(R.string.Dictionary_almighty_pro) + "  " + getString(R.string.Logged_in_as) + username + ")");
        } else {
            customActionBarTextview.setText(getString(R.string.Dictionary_almighty_pro) + "  " + getString(R.string.Unregistered_user));
        }
        customActionBarTextview.setTextSize(20);
        customActionBarTextview.setTextColor(Color.parseColor("#00ff7b"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff0000")));
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(customActionBarTextview);

    }


    /**
     * 簡易版ActionBar介面
     */
    public void customActionBarSimplified() {

        customActionBarTextview.setLayoutParams(layoutparams);
        if (username!=null && !username.equals("")) {
            customActionBarTextview.setText(getString(R.string.Dictionary_almighty_simplified) + "  " + getString(R.string.Logged_in_as) + username + ")");
        } else {
            customActionBarTextview.setText(getString(R.string.Dictionary_almighty_simplified) + "  " + getString(R.string.Unregistered_user));
        }
        customActionBarTextview.setTextSize(20);
        customActionBarTextview.setTextColor(Color.parseColor("#ffffff"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#018577")));
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(customActionBarTextview);

    }



    //==============================================================================================
    // Helper method for saving users' input
    //==============================================================================================

    /**
     * Helper method for saving Keywords to UserInputListView
     */
    public static void saveKeywordtoUserInputListView() {
        if (searchKeyword != null && !searchKeyword.equals("")) {

            if (username!=null && !username.equals("") && localOrCloudSaveSwitchCode==1) {

                if (!userInputArraylist.contains(searchKeyword)){
               mRootReference.child("Users' Input History").child(username).push().setValue(searchKeyword);}
            }

            userInputArraylist.add(searchKeyword);
        }

        //透過HashSet自動過濾掉userInputArraylist中重複的字
        HashSet<String> userInputArraylistHashSet = new HashSet<>();
        userInputArraylistHashSet.addAll(userInputArraylist);
        userInputArraylist.clear();
        userInputArraylist.addAll(userInputArraylistHashSet);

        //Alphabetic sorting
        Collections.sort(userInputArraylist);

        userInputArrayAdapter.notifyDataSetChanged();
    }


    /**
     * Helper method for saving UserInputArrayList to SharedPreferences
     */
    public void saveUserInputArrayListToSharedPreferences() {
        SharedPreferences.Editor editor = getSharedPreferences("userInputArrayListSharedPreferences", MODE_PRIVATE).edit();
        editor.putInt("userInputArrayListValues", userInputArraylist.size());
        for (int i = 0; i < userInputArraylist.size(); i++)
        {
            editor.putString("userInputArrayListItem_"+i, userInputArraylist.get(i));
        }
        editor.apply();
    }



    //==============================================================================================
    // 設置並載入預設字典的 Helper Methods
    //==============================================================================================

    /**
     * 設置預設字典與其AlertDialog
     */
    public void setDefaultDictionariesOriginal() {

        /**
         * 讓用戶快速搜尋預設字典
         */
        //這邊設置第一層AlertDialog讓用戶選擇"設置一個預設字典"或"設置同時搜尋三個預設字典"
        AlertDialog.Builder chooseSingleOrComboDefaultDictionaryAlertDialog = new AlertDialog.Builder(MainActivity.this);
        chooseSingleOrComboDefaultDictionaryAlertDialog.setTitle(R.string.Set_default_dictionary);
        chooseSingleOrComboDefaultDictionaryAlertDialog.setCancelable(false); //按到旁邊的空白處AlertDialog也不會消失
        chooseSingleOrComboDefaultDictionaryAlertDialog.setView(R.layout.custom_alert_dialog_dictionary_providers);

        //這邊設置第二層AlertDialog讓用戶細部設置預設字典。checkedItem:-1的意思是指預設不選中任何項目，若要預設選種第一項則設置成0，第二項則為1...
        chooseSingleOrComboDefaultDictionaryAlertDialog.setSingleChoiceItems(new String[]{getString(R.string.Set_a_single_default_dictionary), getString(R.string.Set_combo_default_dictionaries)}, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface chooseSingleOrComboDefaultDictionaryDialogInterface, int position) {

                //若用戶點選"設置一個預設字典"
                if (position==0){

                    defaultDictionaryListOriginal = getResources().getStringArray(R.array.default_dictionary_list_original);      //初始化專業版預設字典名單

                    defaultSearchAlertDialog = new AlertDialog.Builder(MainActivity.this);
                    defaultSearchAlertDialog.setTitle(R.string.Choose_one_default_dictionary);
                    defaultSearchAlertDialog.setCancelable(false); //按到旁邊的空白處AlertDialog也不會消失

                    setDefaultSingleSearchCodeOriginal();   //設置專業版單一預設字典的代碼

                    //單一預設字典的確定鈕
                    defaultSearchAlertDialog.setPositiveButton(R.string.Confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            //把選取的字典代碼存到sharedPreferences
                            defaultDictionarySearchSharedPreferences=getSharedPreferences("saveChosenDefaultSingleDictionary", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=defaultDictionarySearchSharedPreferences.edit();
                            editor.putString("DefaultSingleDictionaryCode", defaultSingleSearchCode);
                            editor.apply();

                            Toast.makeText(getApplicationContext(),R.string.Settings_complete,Toast.LENGTH_LONG).show(); //告知預設字典設定成功
                        }
                    });

                    //單一預設字典的取消鈕
                    defaultSearchAlertDialog.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            defaultDictionarySearchSharedPreferences=getSharedPreferences("saveChosenDefaultSingleDictionary", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=defaultDictionarySearchSharedPreferences.edit();
                            editor.putString("DefaultSingleDictionaryCode", defaultSingleSearchCode = defaultDictionarySearchSharedPreferences.getString("DefaultSingleDictionaryCode", ""));
                            editor.apply();

                            dialog.dismiss();
                        }
                    });

                    //使用建議的單一預設字典
                    defaultSearchAlertDialog.setNeutralButton(R.string.Use_default_settings, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            defaultSingleSearchCode="Yahoo Dictionary";

                            defaultDictionarySearchSharedPreferences=getSharedPreferences("saveChosenDefaultSingleDictionary", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=defaultDictionarySearchSharedPreferences.edit();
                            editor.putString("DefaultSingleDictionaryCode", defaultSingleSearchCode);
                            editor.apply();

                            dialog.dismiss();

                            Toast.makeText(getApplicationContext(),R.string.Settings_complete,Toast.LENGTH_LONG).show(); //告知預設字典設定成功
                        }
                    });


                    //把第二層的AlertDialog顯示出來
                    defaultSearchAlertDialog.create();
                    defaultSearchAlertDialog.show();
                    //同時讓第一層的AlertDialog消失
                    chooseSingleOrComboDefaultDictionaryDialogInterface.dismiss();


                }
                //若用戶點選"設置同時搜尋三個預設字典"
                else if (position==1){

                    //跳出設定第一個字典的對話框
                    defaultDictionaryListOriginal = getResources().getStringArray(R.array.default_dictionary_list_original);      //初始化專業版預設字典名單

                    defaultComboSearchAlertDialogFirstDictionary = new AlertDialog.Builder(MainActivity.this);
                    defaultComboSearchAlertDialogFirstDictionary.setTitle("請設定第一個預設字典");
                    defaultComboSearchAlertDialogFirstDictionary.setCancelable(false); //按到旁邊的空白處AlertDialog也不會消失

                    setDefaultComboSearchCodeFirstDictionaryOriginal();   //設置專業版三連搜預設字典的代碼

                    //第一個預設字典的確定鈕
                    defaultComboSearchAlertDialogFirstDictionary.setPositiveButton("儲存第一個預設字典", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            //把選取的字典代碼存到sharedPreferences
                            defaultComboDictionarySearchSharedPreferences=getSharedPreferences("saveComboDefaultDictionary", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=defaultComboDictionarySearchSharedPreferences.edit();
                            editor.putString("ComboSearchCodeForFirstDictionary", defaultComboSearchCodeFirstDictionary);
                            editor.apply();


                                //跳出設定第二個預設字典的對話框
                                defaultDictionaryListOriginal = getResources().getStringArray(R.array.default_dictionary_list_original);      //初始化專業版預設字典名單

                                defaultComboSearchAlertDialogSecondDictionary = new AlertDialog.Builder(MainActivity.this);
                                defaultComboSearchAlertDialogSecondDictionary.setTitle("請設定第二個預設字典");
                                defaultComboSearchAlertDialogSecondDictionary.setCancelable(false); //按到旁邊的空白處AlertDialog也不會消失

                                setDefaultComboSearchCodeSecondDictionaryOriginal();   //設置專業版三連搜預設字典的代碼


                                //第二個預設字典的確定鈕
                                defaultComboSearchAlertDialogSecondDictionary.setPositiveButton("儲存第二個預設字典", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        //把選取的字典代碼存到sharedPreferences
                                        defaultComboDictionarySearchSharedPreferences=getSharedPreferences("saveComboDefaultDictionary", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor=defaultComboDictionarySearchSharedPreferences.edit();
                                        editor.putString("ComboSearchCodeForSecondDictionary", defaultComboSearchCodeSecondDictionary);
                                        editor.apply();


                                            //跳出設定第三個字典的對話框
                                            defaultDictionaryListOriginal = getResources().getStringArray(R.array.default_dictionary_list_original);      //初始化專業版預設字典名單

                                            defaultComboSearchAlertDialogThirdDictionary = new AlertDialog.Builder(MainActivity.this);
                                            defaultComboSearchAlertDialogThirdDictionary.setTitle("請設定第三個預設字典");
                                            defaultComboSearchAlertDialogThirdDictionary.setCancelable(false); //按到旁邊的空白處AlertDialog也不會消失

                                            setDefaultComboSearchCodeThirdDictionaryOriginal();   //設置專業版三連搜預設字典的代碼


                                            //第三個預設字典的確定鈕
                                            defaultComboSearchAlertDialogThirdDictionary.setPositiveButton("儲存第三個預設字典", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {

                                                    //把選取的字典代碼存到sharedPreferences
                                                    defaultComboDictionarySearchSharedPreferences=getSharedPreferences("saveComboDefaultDictionary", Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor=defaultComboDictionarySearchSharedPreferences.edit();
                                                    editor.putString("ComboSearchCodeForThirdDictionary", defaultComboSearchCodeThirdDictionary);
                                                    editor.apply();

                                                    Toast.makeText(getApplicationContext(),R.string.Settings_complete,Toast.LENGTH_LONG).show(); //告知預設字典設定成功
                                                }
                                            });

                                            //第三個預設字典的取消鈕
                                            defaultComboSearchAlertDialogThirdDictionary.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {

                                                    defaultComboDictionarySearchSharedPreferences=getSharedPreferences("saveComboDefaultDictionary", Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor=defaultComboDictionarySearchSharedPreferences.edit();
                                                    editor.putString("ComboSearchCodeForThirdDictionary", defaultComboSearchCodeThirdDictionary = defaultComboDictionarySearchSharedPreferences.getString("ComboSearchCodeForThirdDictionary", ""));
                                                    editor.apply();

                                                    dialog.dismiss();
                                                }
                                            });


                                        //把第三預設字典的AlertDialog顯示出來
                                        defaultComboSearchAlertDialogThirdDictionary.create();
                                        defaultComboSearchAlertDialogThirdDictionary.show();

                                    }
                                });

                                //第二個預設字典的取消鈕
                                defaultComboSearchAlertDialogSecondDictionary.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        defaultComboDictionarySearchSharedPreferences=getSharedPreferences("saveChosenDefaultSingleDictionary", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor=defaultComboDictionarySearchSharedPreferences.edit();
                                        editor.putString("ComboSearchCodeForFirstDictionary", defaultComboSearchCodeFirstDictionary = defaultComboDictionarySearchSharedPreferences.getString("ComboSearchCodeForFirstDictionary", ""));
                                        editor.apply();

                                        dialog.dismiss();
                                    }
                                });


                            //把第二預設字典的AlertDialog顯示出來
                            defaultComboSearchAlertDialogSecondDictionary.create();
                            defaultComboSearchAlertDialogSecondDictionary.show();

                        }
                    });

                    //第一個預設字典的取消鈕
                    defaultComboSearchAlertDialogFirstDictionary.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            defaultComboDictionarySearchSharedPreferences=getSharedPreferences("saveChosenDefaultSingleDictionary", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=defaultComboDictionarySearchSharedPreferences.edit();
                            editor.putString("ComboSearchCodeForFirstDictionary", defaultComboSearchCodeFirstDictionary = defaultComboDictionarySearchSharedPreferences.getString("ComboSearchCodeForFirstDictionary", ""));
                            editor.apply();

                            dialog.dismiss();
                        }
                    });

                    //使用建議的三連搜預設字典
                    defaultComboSearchAlertDialogFirstDictionary.setNeutralButton(R.string.Use_default_settings, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            defaultComboSearchCodeFirstDictionary="Yahoo Dictionary";
                            defaultComboSearchCodeSecondDictionary="Forvo";
                            defaultComboSearchCodeThirdDictionary="Word Cool EN-CH";

                            defaultComboDictionarySearchSharedPreferences=getSharedPreferences("saveComboDefaultDictionary", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=defaultComboDictionarySearchSharedPreferences.edit();
                            editor.putString("ComboSearchCodeForFirstDictionary", defaultComboSearchCodeFirstDictionary);
                            editor.putString("ComboSearchCodeForSecondDictionary", defaultComboSearchCodeSecondDictionary);
                            editor.putString("ComboSearchCodeForThirdDictionary", defaultComboSearchCodeThirdDictionary);
                            editor.apply();

                            dialog.dismiss();

                            Toast.makeText(getApplicationContext(),R.string.Settings_complete,Toast.LENGTH_LONG).show(); //告知預設字典設定成功
                        }
                    });


                    //把第一預設字典的AlertDialog顯示出來
                    defaultComboSearchAlertDialogFirstDictionary.create();
                    defaultComboSearchAlertDialogFirstDictionary.show();
                    //同時讓第一層的AlertDialog消失
                    chooseSingleOrComboDefaultDictionaryDialogInterface.dismiss();

                }

            }
        });


        //第一層AlertDialog的取消鈕
        chooseSingleOrComboDefaultDictionaryAlertDialog.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //把第一層的AlertDialog顯示出來
        chooseSingleOrComboDefaultDictionaryAlertDialog.create();
        chooseSingleOrComboDefaultDictionaryAlertDialog.show();

    }


    public void setDefaultDictionariesSimplified() {

        /**
         * 讓用戶快速搜尋預設字典
         */
        //這邊設置第一層AlertDialog讓用戶選擇"設置一個預設字典"或"設置同時搜尋三個預設字典"
        AlertDialog.Builder chooseSingleOrComboDefaultDictionaryAlertDialog = new AlertDialog.Builder(MainActivity.this);
        chooseSingleOrComboDefaultDictionaryAlertDialog.setTitle(R.string.Set_default_dictionary);
        chooseSingleOrComboDefaultDictionaryAlertDialog.setCancelable(false); //按到旁邊的空白處AlertDialog也不會消失
        chooseSingleOrComboDefaultDictionaryAlertDialog.setView(R.layout.custom_alert_dialog_dictionary_providers);

        //這邊設置第二層AlertDialog讓用戶細部設置預設字典。checkedItem:-1的意思是指預設不選中任何項目，若要預設選種第一項則設置成0，第二項則為1...
        chooseSingleOrComboDefaultDictionaryAlertDialog.setSingleChoiceItems(new String[]{getString(R.string.Set_a_single_default_dictionary), getString(R.string.Set_combo_default_dictionaries)}, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface chooseSingleOrComboDefaultDictionaryDialogInterface, int position) {

                //若用戶點選"設置一個預設字典"
                if (position==0){

                    defaultDictionaryListSimplified = getResources().getStringArray(R.array.default_dictionary_list_simplified);  //初始化簡易版預設字典名單

                    defaultSearchAlertDialog = new AlertDialog.Builder(MainActivity.this);
                    defaultSearchAlertDialog.setTitle(R.string.Choose_one_default_dictionary);
                    defaultSearchAlertDialog.setCancelable(false); //按到旁邊的空白處AlertDialog也不會消失

                    setDefaultSingleSearchCodeSimplified();   //設置專業版單一預設字典的代碼

                    //單一預設字典的確定鈕
                    defaultSearchAlertDialog.setPositiveButton(R.string.Confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            //把選取的字典代碼存到sharedPreferences
                            defaultDictionarySearchSharedPreferences=getSharedPreferences("saveChosenDefaultSingleDictionary", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=defaultDictionarySearchSharedPreferences.edit();
                            editor.putString("DefaultSingleDictionaryCode", defaultSingleSearchCode);
                            editor.apply();

                            Toast.makeText(getApplicationContext(),R.string.Settings_complete,Toast.LENGTH_LONG).show(); //告知預設字典設定成功
                        }
                    });

                    //單一預設字典的取消鈕
                    defaultSearchAlertDialog.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            defaultDictionarySearchSharedPreferences=getSharedPreferences("saveChosenDefaultSingleDictionary", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=defaultDictionarySearchSharedPreferences.edit();
                            editor.putString("DefaultSingleDictionaryCode", defaultSingleSearchCode = defaultDictionarySearchSharedPreferences.getString("DefaultSingleDictionaryCode", ""));
                            editor.apply();

                            dialog.dismiss();
                        }
                    });

                    //使用建議的單一預設字典
                    defaultSearchAlertDialog.setNeutralButton(R.string.Use_default_settings, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            defaultSingleSearchCode="Yahoo Dictionary";

                            defaultDictionarySearchSharedPreferences=getSharedPreferences("saveChosenDefaultSingleDictionary", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=defaultDictionarySearchSharedPreferences.edit();
                            editor.putString("DefaultSingleDictionaryCode", defaultSingleSearchCode);
                            editor.apply();

                            dialog.dismiss();

                            Toast.makeText(getApplicationContext(),R.string.Settings_complete,Toast.LENGTH_LONG).show(); //告知預設字典設定成功
                        }
                    });


                    //把第二層的AlertDialog顯示出來
                    defaultSearchAlertDialog.create();
                    defaultSearchAlertDialog.show();
                    //同時讓第一層的AlertDialog消失
                    chooseSingleOrComboDefaultDictionaryDialogInterface.dismiss();


                }
                //若用戶點選"設置同時搜尋三個預設字典"
                else if (position==1){

                    //跳出設定第一個字典的對話框
                    defaultDictionaryListSimplified = getResources().getStringArray(R.array.default_dictionary_list_simplified);      //初始化專業版預設字典名單

                    defaultComboSearchAlertDialogFirstDictionary = new AlertDialog.Builder(MainActivity.this);
                    defaultComboSearchAlertDialogFirstDictionary.setTitle("請設定第一個預設字典");
                    defaultComboSearchAlertDialogFirstDictionary.setCancelable(false); //按到旁邊的空白處AlertDialog也不會消失

                    setDefaultComboSearchCodeFirstDictionarySimplified();   //設置專業版三連搜預設字典的代碼

                    //第一個預設字典的確定鈕
                    defaultComboSearchAlertDialogFirstDictionary.setPositiveButton("儲存第一個預設字典", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            //把選取的字典代碼存到sharedPreferences
                            defaultComboDictionarySearchSharedPreferences=getSharedPreferences("saveComboDefaultDictionary", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=defaultComboDictionarySearchSharedPreferences.edit();
                            editor.putString("ComboSearchCodeForFirstDictionary", defaultComboSearchCodeFirstDictionary);
                            editor.apply();


                            //跳出設定第二個預設字典的對話框
                            defaultDictionaryListOriginal = getResources().getStringArray(R.array.default_dictionary_list_original);      //初始化專業版預設字典名單

                            defaultComboSearchAlertDialogSecondDictionary = new AlertDialog.Builder(MainActivity.this);
                            defaultComboSearchAlertDialogSecondDictionary.setTitle("請設定第二個預設字典");
                            defaultComboSearchAlertDialogSecondDictionary.setCancelable(false); //按到旁邊的空白處AlertDialog也不會消失

                            setDefaultComboSearchCodeSecondDictionarySimplified();   //設置專業版三連搜預設字典的代碼


                            //第二個預設字典的確定鈕
                            defaultComboSearchAlertDialogSecondDictionary.setPositiveButton("儲存第二個預設字典", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    //把選取的字典代碼存到sharedPreferences
                                    defaultComboDictionarySearchSharedPreferences=getSharedPreferences("saveComboDefaultDictionary", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor=defaultComboDictionarySearchSharedPreferences.edit();
                                    editor.putString("ComboSearchCodeForSecondDictionary", defaultComboSearchCodeSecondDictionary);
                                    editor.apply();


                                    //跳出設定第三個字典的對話框
                                    defaultDictionaryListOriginal = getResources().getStringArray(R.array.default_dictionary_list_original);      //初始化專業版預設字典名單

                                    defaultComboSearchAlertDialogThirdDictionary = new AlertDialog.Builder(MainActivity.this);
                                    defaultComboSearchAlertDialogThirdDictionary.setTitle("請設定第三個預設字典");
                                    defaultComboSearchAlertDialogThirdDictionary.setCancelable(false); //按到旁邊的空白處AlertDialog也不會消失

                                    setDefaultComboSearchCodeThirdDictionarySimplified();   //設置專業版三連搜預設字典的代碼


                                    //第三個預設字典的確定鈕
                                    defaultComboSearchAlertDialogThirdDictionary.setPositiveButton("儲存第三個預設字典", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            //把選取的字典代碼存到sharedPreferences
                                            defaultComboDictionarySearchSharedPreferences=getSharedPreferences("saveComboDefaultDictionary", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor=defaultComboDictionarySearchSharedPreferences.edit();
                                            editor.putString("ComboSearchCodeForThirdDictionary", defaultComboSearchCodeThirdDictionary);
                                            editor.apply();

                                            Toast.makeText(getApplicationContext(),R.string.Settings_complete,Toast.LENGTH_LONG).show(); //告知預設字典設定成功
                                        }
                                    });

                                    //第三個預設字典的取消鈕
                                    defaultComboSearchAlertDialogThirdDictionary.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            defaultComboDictionarySearchSharedPreferences=getSharedPreferences("saveComboDefaultDictionary", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor=defaultComboDictionarySearchSharedPreferences.edit();
                                            editor.putString("ComboSearchCodeForThirdDictionary", defaultComboSearchCodeThirdDictionary = defaultComboDictionarySearchSharedPreferences.getString("ComboSearchCodeForThirdDictionary", ""));
                                            editor.apply();

                                            dialog.dismiss();
                                        }
                                    });


                                    //把第三預設字典的AlertDialog顯示出來
                                    defaultComboSearchAlertDialogThirdDictionary.create();
                                    defaultComboSearchAlertDialogThirdDictionary.show();

                                }
                            });

                            //第二個預設字典的取消鈕
                            defaultComboSearchAlertDialogSecondDictionary.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    defaultComboDictionarySearchSharedPreferences=getSharedPreferences("saveChosenDefaultSingleDictionary", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor=defaultComboDictionarySearchSharedPreferences.edit();
                                    editor.putString("ComboSearchCodeForFirstDictionary", defaultComboSearchCodeFirstDictionary = defaultComboDictionarySearchSharedPreferences.getString("ComboSearchCodeForFirstDictionary", ""));
                                    editor.apply();

                                    dialog.dismiss();
                                }
                            });


                            //把第二預設字典的AlertDialog顯示出來
                            defaultComboSearchAlertDialogSecondDictionary.create();
                            defaultComboSearchAlertDialogSecondDictionary.show();

                        }
                    });

                    //第一個預設字典的取消鈕
                    defaultComboSearchAlertDialogFirstDictionary.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            defaultComboDictionarySearchSharedPreferences=getSharedPreferences("saveChosenDefaultSingleDictionary", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=defaultComboDictionarySearchSharedPreferences.edit();
                            editor.putString("ComboSearchCodeForFirstDictionary", defaultComboSearchCodeFirstDictionary = defaultComboDictionarySearchSharedPreferences.getString("ComboSearchCodeForFirstDictionary", ""));
                            editor.apply();

                            dialog.dismiss();
                        }
                    });

                    //使用建議的三連搜預設字典
                    defaultComboSearchAlertDialogFirstDictionary.setNeutralButton(R.string.Use_default_settings, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            defaultComboSearchCodeFirstDictionary="Yahoo Dictionary";
                            defaultComboSearchCodeSecondDictionary="Forvo";
                            defaultComboSearchCodeThirdDictionary="Word Cool EN-CH";

                            defaultComboDictionarySearchSharedPreferences=getSharedPreferences("saveComboDefaultDictionary", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=defaultComboDictionarySearchSharedPreferences.edit();
                            editor.putString("ComboSearchCodeForFirstDictionary", defaultComboSearchCodeFirstDictionary);
                            editor.putString("ComboSearchCodeForSecondDictionary", defaultComboSearchCodeSecondDictionary);
                            editor.putString("ComboSearchCodeForThirdDictionary", defaultComboSearchCodeThirdDictionary);
                            editor.apply();

                            dialog.dismiss();

                            Toast.makeText(getApplicationContext(),R.string.Settings_complete,Toast.LENGTH_LONG).show(); //告知預設字典設定成功
                        }
                    });


                    //把第一預設字典的AlertDialog顯示出來
                    defaultComboSearchAlertDialogFirstDictionary.create();
                    defaultComboSearchAlertDialogFirstDictionary.show();
                    //同時讓第一層的AlertDialog消失
                    chooseSingleOrComboDefaultDictionaryDialogInterface.dismiss();

                }

            }
        });


        //第一層AlertDialog的取消鈕
        chooseSingleOrComboDefaultDictionaryAlertDialog.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //把第一層的AlertDialog顯示出來
        chooseSingleOrComboDefaultDictionaryAlertDialog.create();
        chooseSingleOrComboDefaultDictionaryAlertDialog.show();

    }


    /**
     * 設置單一預設字典的代碼
     */
    public void setDefaultSingleSearchCodeOriginal(){

        defaultSearchAlertDialog.setSingleChoiceItems(defaultDictionaryListOriginal, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                switch (position) {
                    case 0:
                        defaultSingleSearchCode= "Yahoo Dictionary";
                        break;
                    case 1:
                        defaultSingleSearchCode= "National Academy for Educational Research";
                        break;
                    case 2:
                        defaultSingleSearchCode= "Dict Site";
                        break;
                    case 3:
                        defaultSingleSearchCode= "Cidian8";
                        break;
                    case 4:
                        defaultSingleSearchCode= "VoiceTube";
                        break;
                    case 5:
                        defaultSingleSearchCode= "Cambridge EN-CH";
                        break;
                    case 6:
                        defaultSingleSearchCode= "Merriam Webster";
                        break;
                    case 7:
                        defaultSingleSearchCode= "Collins";
                        break;
                    case 8:
                        defaultSingleSearchCode= "Oxford";
                        break;
                    case 9:
                        defaultSingleSearchCode= "Vocabulary";
                        break;
                    case 10:
                        defaultSingleSearchCode= "Dictionary.com";
                        break;
                    case 11:
                        defaultSingleSearchCode= "The Free Dictionary";
                        break;
                    case 12:
                        defaultSingleSearchCode= "Your_Dictionary";
                        break;
                    case 13:
                        defaultSingleSearchCode= "Longman Dictionary";
                        break;
                    case 14:
                        defaultSingleSearchCode= "Greens dictionary of slang";
                        break;
                    case 15:
                        defaultSingleSearchCode= "Wiki Dictionary";
                        break;
                    case 16:
                        defaultSingleSearchCode= "Word Hippo";
                        break;
                    case 17:
                        defaultSingleSearchCode= "Onelook";
                        break;
                    case 18:
                        defaultSingleSearchCode= "Business Dictionary";
                        break;
                    case 19:
                        defaultSingleSearchCode= "Slang";
                        break;
                    case 20:
                        defaultSingleSearchCode= "YouGlish";
                        break;
                    case 21:
                        defaultSingleSearchCode= "SCI Dictionary";
                        break;
                    case 22:
                        defaultSingleSearchCode= "TechDico";
                        break;
                    case 23:
                        defaultSingleSearchCode= "BioMedical Dictionary";
                        break;
                    case 24:
                        defaultSingleSearchCode= "IsPlural Dictionary";
                        break;
                    case 25:
                        defaultSingleSearchCode= "LingoHelpPrepositions";
                        break;
                    case 26:
                        defaultSingleSearchCode= "WordWeb";
                        break;
                    case 27:
                        defaultSingleSearchCode= "Weblio JP";
                        break;
                    case 28:
                        defaultSingleSearchCode= "Weblio CN";
                        break;
                    case 29:
                        defaultSingleSearchCode= "Weblio EN";
                        break;
                    case 30:
                        defaultSingleSearchCode= "Weblio Synonym";
                        break;
                    case 31:
                        defaultSingleSearchCode= "Tangorin Word";
                        break;
                    case 32:
                        defaultSingleSearchCode= "Tangorin Kanji";
                        break;
                    case 33:
                        defaultSingleSearchCode= "Tangorin Names";
                        break;
                    case 34:
                        defaultSingleSearchCode= "Tangorin Sentence";
                        break;
                    case 35:
                        defaultSingleSearchCode= "DA JP-TW Dictionary";
                        break;
                    case 36:
                        defaultSingleSearchCode= "DA TW-JP Dictionary";
                        break;
                    case 37:
                        defaultSingleSearchCode= "Cidianwang";
                        break;
                    case 38:
                        defaultSingleSearchCode= "Goo";
                        break;
                    case 39:
                        defaultSingleSearchCode= "Sanseido";
                        break;
                    case 40:
                        defaultSingleSearchCode= "Kotoba Bank";
                        break;
                    case 41:
                        defaultSingleSearchCode= "J Logos";
                        break;
                    case 42:
                        defaultSingleSearchCode= "Japanese Industry Terms";
                        break;
                    case 43:
                        defaultSingleSearchCode= "Kanji Dictionary Online";
                        break;
                    case 44:
                        defaultSingleSearchCode= "Eijirou";
                        break;
                    case 45:
                        defaultSingleSearchCode= "How do you say this in English";
                        break;
                    case 46:
                        defaultSingleSearchCode= "Jisho";
                        break;
                    case 47:
                        defaultSingleSearchCode= "Word Plus Chinese";
                        break;
                    case 48:
                        defaultSingleSearchCode= "Word Plus English 1";
                        break;
                    case 49:
                        defaultSingleSearchCode= "Word Plus English 2";
                        break;
                    case 50:
                        defaultSingleSearchCode= "Word Plus Translation";
                        break;
                    case 51:
                        defaultSingleSearchCode= "Word Plus Japanese 1";
                        break;
                    case 52:
                        defaultSingleSearchCode= "Word Plus Japanese 2";
                        break;
                    case 53:
                        defaultSingleSearchCode= "Word Plus Japanese 3";
                        break;
                    case 54:
                        defaultSingleSearchCode= "Word Plus Meaning 1";
                        break;
                    case 55:
                        defaultSingleSearchCode= "Word Plus Meaning 2";
                        break;
                    case 56:
                        defaultSingleSearchCode= "Google translate to CHTW";
                        break;
                    case 57:
                        defaultSingleSearchCode= "Google translate to CHCN";
                        break;
                    case 58:
                        defaultSingleSearchCode= "Google translate to EN";
                        break;
                    case 59:
                        defaultSingleSearchCode= "Google translate to JP";
                        break;
                    case 60:
                        defaultSingleSearchCode= "Google translate to KR";
                        break;
                    case 61:
                        defaultSingleSearchCode= "Google translate to SP";
                        break;
                    case 62:
                        defaultSingleSearchCode= "Google Image";
                        break;
                    case 63:
                        defaultSingleSearchCode= "Ludwig";
                        break;
                    case 64:
                        defaultSingleSearchCode= "Your Dictionary Example Sentences";
                        break;
                    case 65:
                        defaultSingleSearchCode= "Word Cool EN-CH";
                        break;
                    case 66:
                        defaultSingleSearchCode= "Word Cool EN-JP";
                        break;
                    case 67:
                        defaultSingleSearchCode= "Word Cool JP-CH";
                        break;
                    case 68:
                        defaultSingleSearchCode= "Linguee CH-EN";
                        break;
                    case 69:
                        defaultSingleSearchCode= "Linguee JP-EN";
                        break;
                    case 70:
                        defaultSingleSearchCode= "Wikipedia TW";
                        break;
                    case 71:
                        defaultSingleSearchCode= "Wikipedia EN";
                        break;
                    case 72:
                        defaultSingleSearchCode= "English Encyclopedia";
                        break;
                    case 73:
                        defaultSingleSearchCode= "Forvo";
                        break;
                    case 74:
                        defaultSingleSearchCode= "Wiki Diff";
                        break;
                    case 75:
                        defaultSingleSearchCode= "Net Speak";
                        break;
                    case 76:
                        defaultSingleSearchCode= "Yomikata";
                        break;
                    case 77:
                        defaultSingleSearchCode= "Chigai";
                        break;
                    case 78:
                        defaultSingleSearchCode= "OJAD";
                        break;
                }

            }
        });

    }


    public void setDefaultSingleSearchCodeSimplified(){

        defaultSearchAlertDialog.setSingleChoiceItems(defaultDictionaryListSimplified, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                switch (position) {
                    case 0:
                        defaultSingleSearchCode= "Yahoo Dictionary";
                        break;
                    case 1:
                        defaultSingleSearchCode= "National Academy for Educational Research";
                        break;
                    case 2:
                        defaultSingleSearchCode= "Dict Site";
                        break;
                    case 3:
                        defaultSingleSearchCode= "VoiceTube";
                        break;
                    case 4:
                        defaultSingleSearchCode= "Cambridge EN-CH";
                        break;
                    case 5:
                        defaultSingleSearchCode= "Weblio JP";
                        break;
                    case 6:
                        defaultSingleSearchCode= "Weblio CN";
                        break;
                    case 7:
                        defaultSingleSearchCode= "DA JP-TW Dictionary";
                        break;
                    case 8:
                        defaultSingleSearchCode= "DA TW-JP Dictionary";
                        break;
                    case 9:
                        defaultSingleSearchCode= "Google translate to CHTW";
                        break;
                    case 10:
                        defaultSingleSearchCode= "Google translate to CHCN";
                        break;
                    case 11:
                        defaultSingleSearchCode= "Google Image";
                        break;
                }

            }
        });

    }



    /**
     * 設置三連搜預設字典的代碼
     */
    public void setDefaultComboSearchCodeFirstDictionaryOriginal(){

        defaultComboSearchAlertDialogFirstDictionary.setSingleChoiceItems(defaultDictionaryListOriginal, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                switch (position) {
                    case 0:
                        defaultComboSearchCodeFirstDictionary= "Yahoo Dictionary";
                        break;
                    case 1:
                        defaultComboSearchCodeFirstDictionary= "National Academy for Educational Research";
                        break;
                    case 2:
                        defaultComboSearchCodeFirstDictionary= "Dict Site";
                        break;
                    case 3:
                        defaultComboSearchCodeFirstDictionary= "Cidian8";
                        break;
                    case 4:
                        defaultComboSearchCodeFirstDictionary= "VoiceTube";
                        break;
                    case 5:
                        defaultComboSearchCodeFirstDictionary= "Cambridge EN-CH";
                        break;
                    case 6:
                        defaultComboSearchCodeFirstDictionary= "Merriam Webster";
                        break;
                    case 7:
                        defaultComboSearchCodeFirstDictionary= "Collins";
                        break;
                    case 8:
                        defaultComboSearchCodeFirstDictionary= "Oxford";
                        break;
                    case 9:
                        defaultComboSearchCodeFirstDictionary= "Vocabulary";
                        break;
                    case 10:
                        defaultComboSearchCodeFirstDictionary= "Dictionary.com";
                        break;
                    case 11:
                        defaultComboSearchCodeFirstDictionary= "The Free Dictionary";
                        break;
                    case 12:
                        defaultComboSearchCodeFirstDictionary= "Your_Dictionary";
                        break;
                    case 13:
                        defaultComboSearchCodeFirstDictionary= "Longman Dictionary";
                        break;
                    case 14:
                        defaultComboSearchCodeFirstDictionary= "Greens dictionary of slang";
                        break;
                    case 15:
                        defaultComboSearchCodeFirstDictionary= "Wiki Dictionary";
                        break;
                    case 16:
                        defaultComboSearchCodeFirstDictionary= "Word Hippo";
                        break;
                    case 17:
                        defaultComboSearchCodeFirstDictionary= "Onelook";
                        break;
                    case 18:
                        defaultComboSearchCodeFirstDictionary= "Business Dictionary";
                        break;
                    case 19:
                        defaultComboSearchCodeFirstDictionary= "Slang";
                        break;
                    case 20:
                        defaultComboSearchCodeFirstDictionary= "YouGlish";
                        break;
                    case 21:
                        defaultComboSearchCodeFirstDictionary= "SCI Dictionary";
                        break;
                    case 22:
                        defaultComboSearchCodeFirstDictionary= "TechDico";
                        break;
                    case 23:
                        defaultComboSearchCodeFirstDictionary= "BioMedical Dictionary";
                        break;
                    case 24:
                        defaultComboSearchCodeFirstDictionary= "IsPlural Dictionary";
                        break;
                    case 25:
                        defaultComboSearchCodeFirstDictionary= "LingoHelpPrepositions";
                        break;
                    case 26:
                        defaultComboSearchCodeFirstDictionary= "WordWeb";
                        break;
                    case 27:
                        defaultComboSearchCodeFirstDictionary= "Weblio JP";
                        break;
                    case 28:
                        defaultComboSearchCodeFirstDictionary= "Weblio CN";
                        break;
                    case 29:
                        defaultComboSearchCodeFirstDictionary= "Weblio EN";
                        break;
                    case 30:
                        defaultComboSearchCodeFirstDictionary= "Weblio Synonym";
                        break;
                    case 31:
                        defaultComboSearchCodeFirstDictionary= "Tangorin Word";
                        break;
                    case 32:
                        defaultComboSearchCodeFirstDictionary= "Tangorin Kanji";
                        break;
                    case 33:
                        defaultComboSearchCodeFirstDictionary= "Tangorin Names";
                        break;
                    case 34:
                        defaultComboSearchCodeFirstDictionary= "Tangorin Sentence";
                        break;
                    case 35:
                        defaultComboSearchCodeFirstDictionary= "DA JP-TW Dictionary";
                        break;
                    case 36:
                        defaultComboSearchCodeFirstDictionary= "DA TW-JP Dictionary";
                        break;
                    case 37:
                        defaultComboSearchCodeFirstDictionary= "Cidianwang";
                        break;
                    case 38:
                        defaultComboSearchCodeFirstDictionary= "Goo";
                        break;
                    case 39:
                        defaultComboSearchCodeFirstDictionary= "Sanseido";
                        break;
                    case 40:
                        defaultComboSearchCodeFirstDictionary= "Kotoba Bank";
                        break;
                    case 41:
                        defaultComboSearchCodeFirstDictionary= "J Logos";
                        break;
                    case 42:
                        defaultComboSearchCodeFirstDictionary= "Japanese Industry Terms";
                        break;
                    case 43:
                        defaultComboSearchCodeFirstDictionary= "Kanji Dictionary Online";
                        break;
                    case 44:
                        defaultComboSearchCodeFirstDictionary= "Eijirou";
                        break;
                    case 45:
                        defaultComboSearchCodeFirstDictionary= "How do you say this in English";
                        break;
                    case 46:
                        defaultComboSearchCodeFirstDictionary= "Jisho";
                        break;
                    case 47:
                        defaultComboSearchCodeFirstDictionary= "Word Plus Chinese";
                        break;
                    case 48:
                        defaultComboSearchCodeFirstDictionary= "Word Plus English 1";
                        break;
                    case 49:
                        defaultComboSearchCodeFirstDictionary= "Word Plus English 2";
                        break;
                    case 50:
                        defaultComboSearchCodeFirstDictionary= "Word Plus Translation";
                        break;
                    case 51:
                        defaultComboSearchCodeFirstDictionary= "Word Plus Japanese 1";
                        break;
                    case 52:
                        defaultComboSearchCodeFirstDictionary= "Word Plus Japanese 2";
                        break;
                    case 53:
                        defaultComboSearchCodeFirstDictionary= "Word Plus Japanese 3";
                        break;
                    case 54:
                        defaultComboSearchCodeFirstDictionary= "Word Plus Meaning 1";
                        break;
                    case 55:
                        defaultComboSearchCodeFirstDictionary= "Word Plus Meaning 2";
                        break;
                    case 56:
                        defaultComboSearchCodeFirstDictionary= "Google translate to CHTW";
                        break;
                    case 57:
                        defaultComboSearchCodeFirstDictionary= "Google translate to CHCN";
                        break;
                    case 58:
                        defaultComboSearchCodeFirstDictionary= "Google translate to EN";
                        break;
                    case 59:
                        defaultComboSearchCodeFirstDictionary= "Google translate to JP";
                        break;
                    case 60:
                        defaultComboSearchCodeFirstDictionary= "Google translate to KR";
                        break;
                    case 61:
                        defaultComboSearchCodeFirstDictionary= "Google translate to SP";
                        break;
                    case 62:
                        defaultComboSearchCodeFirstDictionary= "Google Image";
                        break;
                    case 63:
                        defaultComboSearchCodeFirstDictionary= "Ludwig";
                        break;
                    case 64:
                        defaultComboSearchCodeFirstDictionary= "Your Dictionary Example Sentences";
                        break;
                    case 65:
                        defaultComboSearchCodeFirstDictionary= "Word Cool EN-CH";
                        break;
                    case 66:
                        defaultComboSearchCodeFirstDictionary= "Word Cool EN-JP";
                        break;
                    case 67:
                        defaultComboSearchCodeFirstDictionary= "Word Cool JP-CH";
                        break;
                    case 68:
                        defaultComboSearchCodeFirstDictionary= "Linguee CH-EN";
                        break;
                    case 69:
                        defaultComboSearchCodeFirstDictionary= "Linguee JP-EN";
                        break;
                    case 70:
                        defaultComboSearchCodeFirstDictionary= "Wikipedia TW";
                        break;
                    case 71:
                        defaultComboSearchCodeFirstDictionary= "Wikipedia EN";
                        break;
                    case 72:
                        defaultComboSearchCodeFirstDictionary= "English Encyclopedia";
                        break;
                    case 73:
                        defaultComboSearchCodeFirstDictionary= "Forvo";
                        break;
                    case 74:
                        defaultComboSearchCodeFirstDictionary= "Wiki Diff";
                        break;
                    case 75:
                        defaultComboSearchCodeFirstDictionary= "Net Speak";
                        break;
                    case 76:
                        defaultComboSearchCodeFirstDictionary= "Yomikata";
                        break;
                    case 77:
                        defaultComboSearchCodeFirstDictionary= "Chigai";
                        break;
                    case 78:
                        defaultComboSearchCodeFirstDictionary= "OJAD";
                        break;
                }

            }
        });

    }


    public void setDefaultComboSearchCodeFirstDictionarySimplified(){

        defaultComboSearchAlertDialogFirstDictionary.setSingleChoiceItems(defaultDictionaryListSimplified, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                switch (position) {
                    case 0:
                        defaultComboSearchCodeFirstDictionary= "Yahoo Dictionary";
                        break;
                    case 1:
                        defaultComboSearchCodeFirstDictionary= "National Academy for Educational Research";
                        break;
                    case 2:
                        defaultComboSearchCodeFirstDictionary= "Dict Site";
                        break;
                    case 3:
                        defaultComboSearchCodeFirstDictionary= "VoiceTube";
                        break;
                    case 4:
                        defaultComboSearchCodeFirstDictionary= "Cambridge EN-CH";
                        break;
                    case 5:
                        defaultComboSearchCodeFirstDictionary= "Weblio JP";
                        break;
                    case 6:
                        defaultComboSearchCodeFirstDictionary= "Weblio CN";
                        break;
                    case 7:
                        defaultComboSearchCodeFirstDictionary= "DA JP-TW Dictionary";
                        break;
                    case 8:
                        defaultComboSearchCodeFirstDictionary= "DA TW-JP Dictionary";
                        break;
                    case 9:
                        defaultComboSearchCodeFirstDictionary= "Google translate to CHTW";
                        break;
                    case 10:
                        defaultComboSearchCodeFirstDictionary= "Google translate to CHCN";
                        break;
                    case 11:
                        defaultComboSearchCodeFirstDictionary= "Google Image";
                        break;
                }

            }
        });

    }


    public void setDefaultComboSearchCodeSecondDictionaryOriginal(){

        defaultComboSearchAlertDialogSecondDictionary.setSingleChoiceItems(defaultDictionaryListOriginal, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                switch (position) {
                    case 0:
                        defaultComboSearchCodeSecondDictionary= "Yahoo Dictionary";
                        break;
                    case 1:
                        defaultComboSearchCodeSecondDictionary= "National Academy for Educational Research";
                        break;
                    case 2:
                        defaultComboSearchCodeSecondDictionary= "Dict Site";
                        break;
                    case 3:
                        defaultComboSearchCodeSecondDictionary= "Cidian8";
                        break;
                    case 4:
                        defaultComboSearchCodeSecondDictionary= "VoiceTube";
                        break;
                    case 5:
                        defaultComboSearchCodeSecondDictionary= "Cambridge EN-CH";
                        break;
                    case 6:
                        defaultComboSearchCodeSecondDictionary= "Merriam Webster";
                        break;
                    case 7:
                        defaultComboSearchCodeSecondDictionary= "Collins";
                        break;
                    case 8:
                        defaultComboSearchCodeSecondDictionary= "Oxford";
                        break;
                    case 9:
                        defaultComboSearchCodeSecondDictionary= "Vocabulary";
                        break;
                    case 10:
                        defaultComboSearchCodeSecondDictionary= "Dictionary.com";
                        break;
                    case 11:
                        defaultComboSearchCodeSecondDictionary= "The Free Dictionary";
                        break;
                    case 12:
                        defaultComboSearchCodeSecondDictionary= "Your_Dictionary";
                        break;
                    case 13:
                        defaultComboSearchCodeSecondDictionary= "Longman Dictionary";
                        break;
                    case 14:
                        defaultComboSearchCodeSecondDictionary= "Greens dictionary of slang";
                        break;
                    case 15:
                        defaultComboSearchCodeSecondDictionary= "Wiki Dictionary";
                        break;
                    case 16:
                        defaultComboSearchCodeSecondDictionary= "Word Hippo";
                        break;
                    case 17:
                        defaultComboSearchCodeSecondDictionary= "Onelook";
                        break;
                    case 18:
                        defaultComboSearchCodeSecondDictionary= "Business Dictionary";
                        break;
                    case 19:
                        defaultComboSearchCodeSecondDictionary= "Slang";
                        break;
                    case 20:
                        defaultComboSearchCodeSecondDictionary= "YouGlish";
                        break;
                    case 21:
                        defaultComboSearchCodeSecondDictionary= "SCI Dictionary";
                        break;
                    case 22:
                        defaultComboSearchCodeSecondDictionary= "TechDico";
                        break;
                    case 23:
                        defaultComboSearchCodeSecondDictionary= "BioMedical Dictionary";
                        break;
                    case 24:
                        defaultComboSearchCodeSecondDictionary= "IsPlural Dictionary";
                        break;
                    case 25:
                        defaultComboSearchCodeSecondDictionary= "LingoHelpPrepositions";
                        break;
                    case 26:
                        defaultComboSearchCodeSecondDictionary= "WordWeb";
                        break;
                    case 27:
                        defaultComboSearchCodeSecondDictionary= "Weblio JP";
                        break;
                    case 28:
                        defaultComboSearchCodeSecondDictionary= "Weblio CN";
                        break;
                    case 29:
                        defaultComboSearchCodeSecondDictionary= "Weblio EN";
                        break;
                    case 30:
                        defaultComboSearchCodeSecondDictionary= "Weblio Synonym";
                        break;
                    case 31:
                        defaultComboSearchCodeSecondDictionary= "Tangorin Word";
                        break;
                    case 32:
                        defaultComboSearchCodeSecondDictionary= "Tangorin Kanji";
                        break;
                    case 33:
                        defaultComboSearchCodeSecondDictionary= "Tangorin Names";
                        break;
                    case 34:
                        defaultComboSearchCodeSecondDictionary= "Tangorin Sentence";
                        break;
                    case 35:
                        defaultComboSearchCodeSecondDictionary= "DA JP-TW Dictionary";
                        break;
                    case 36:
                        defaultComboSearchCodeSecondDictionary= "DA TW-JP Dictionary";
                        break;
                    case 37:
                        defaultComboSearchCodeSecondDictionary= "Cidianwang";
                        break;
                    case 38:
                        defaultComboSearchCodeSecondDictionary= "Goo";
                        break;
                    case 39:
                        defaultComboSearchCodeSecondDictionary= "Sanseido";
                        break;
                    case 40:
                        defaultComboSearchCodeSecondDictionary= "Kotoba Bank";
                        break;
                    case 41:
                        defaultComboSearchCodeSecondDictionary= "J Logos";
                        break;
                    case 42:
                        defaultComboSearchCodeSecondDictionary= "Japanese Industry Terms";
                        break;
                    case 43:
                        defaultComboSearchCodeSecondDictionary= "Kanji Dictionary Online";
                        break;
                    case 44:
                        defaultComboSearchCodeSecondDictionary= "Eijirou";
                        break;
                    case 45:
                        defaultComboSearchCodeSecondDictionary= "How do you say this in English";
                        break;
                    case 46:
                        defaultComboSearchCodeSecondDictionary= "Jisho";
                        break;
                    case 47:
                        defaultComboSearchCodeSecondDictionary= "Word Plus Chinese";
                        break;
                    case 48:
                        defaultComboSearchCodeSecondDictionary= "Word Plus English 1";
                        break;
                    case 49:
                        defaultComboSearchCodeSecondDictionary= "Word Plus English 2";
                        break;
                    case 50:
                        defaultComboSearchCodeSecondDictionary= "Word Plus Translation";
                        break;
                    case 51:
                        defaultComboSearchCodeSecondDictionary= "Word Plus Japanese 1";
                        break;
                    case 52:
                        defaultComboSearchCodeSecondDictionary= "Word Plus Japanese 2";
                        break;
                    case 53:
                        defaultComboSearchCodeSecondDictionary= "Word Plus Japanese 3";
                        break;
                    case 54:
                        defaultComboSearchCodeSecondDictionary= "Word Plus Meaning 1";
                        break;
                    case 55:
                        defaultComboSearchCodeSecondDictionary= "Word Plus Meaning 2";
                        break;
                    case 56:
                        defaultComboSearchCodeSecondDictionary= "Google translate to CHTW";
                        break;
                    case 57:
                        defaultComboSearchCodeSecondDictionary= "Google translate to CHCN";
                        break;
                    case 58:
                        defaultComboSearchCodeSecondDictionary= "Google translate to EN";
                        break;
                    case 59:
                        defaultComboSearchCodeSecondDictionary= "Google translate to JP";
                        break;
                    case 60:
                        defaultComboSearchCodeSecondDictionary= "Google translate to KR";
                        break;
                    case 61:
                        defaultComboSearchCodeSecondDictionary= "Google translate to SP";
                        break;
                    case 62:
                        defaultComboSearchCodeSecondDictionary= "Google Image";
                        break;
                    case 63:
                        defaultComboSearchCodeSecondDictionary= "Ludwig";
                        break;
                    case 64:
                        defaultComboSearchCodeSecondDictionary= "Your Dictionary Example Sentences";
                        break;
                    case 65:
                        defaultComboSearchCodeSecondDictionary= "Word Cool EN-CH";
                        break;
                    case 66:
                        defaultComboSearchCodeSecondDictionary= "Word Cool EN-JP";
                        break;
                    case 67:
                        defaultComboSearchCodeSecondDictionary= "Word Cool JP-CH";
                        break;
                    case 68:
                        defaultComboSearchCodeSecondDictionary= "Linguee CH-EN";
                        break;
                    case 69:
                        defaultComboSearchCodeSecondDictionary= "Linguee JP-EN";
                        break;
                    case 70:
                        defaultComboSearchCodeSecondDictionary= "Wikipedia TW";
                        break;
                    case 71:
                        defaultComboSearchCodeSecondDictionary= "Wikipedia EN";
                        break;
                    case 72:
                        defaultComboSearchCodeSecondDictionary= "English Encyclopedia";
                        break;
                    case 73:
                        defaultComboSearchCodeSecondDictionary= "Forvo";
                        break;
                    case 74:
                        defaultComboSearchCodeSecondDictionary= "Wiki Diff";
                        break;
                    case 75:
                        defaultComboSearchCodeSecondDictionary= "Net Speak";
                        break;
                    case 76:
                        defaultComboSearchCodeSecondDictionary= "Yomikata";
                        break;
                    case 77:
                        defaultComboSearchCodeSecondDictionary= "Chigai";
                        break;
                    case 78:
                        defaultComboSearchCodeSecondDictionary= "OJAD";
                        break;
                }

            }
        });

    }


    public void setDefaultComboSearchCodeSecondDictionarySimplified(){

        defaultComboSearchAlertDialogSecondDictionary.setSingleChoiceItems(defaultDictionaryListSimplified, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                switch (position) {
                    case 0:
                        defaultComboSearchCodeSecondDictionary= "Yahoo Dictionary";
                        break;
                    case 1:
                        defaultComboSearchCodeSecondDictionary= "National Academy for Educational Research";
                        break;
                    case 2:
                        defaultComboSearchCodeSecondDictionary= "Dict Site";
                        break;
                    case 3:
                        defaultComboSearchCodeSecondDictionary= "VoiceTube";
                        break;
                    case 4:
                        defaultComboSearchCodeSecondDictionary= "Cambridge EN-CH";
                        break;
                    case 5:
                        defaultComboSearchCodeSecondDictionary= "Weblio JP";
                        break;
                    case 6:
                        defaultComboSearchCodeSecondDictionary= "Weblio CN";
                        break;
                    case 7:
                        defaultComboSearchCodeSecondDictionary= "DA JP-TW Dictionary";
                        break;
                    case 8:
                        defaultComboSearchCodeSecondDictionary= "DA TW-JP Dictionary";
                        break;
                    case 9:
                        defaultComboSearchCodeSecondDictionary= "Google translate to CHTW";
                        break;
                    case 10:
                        defaultComboSearchCodeSecondDictionary= "Google translate to CHCN";
                        break;
                    case 11:
                        defaultComboSearchCodeSecondDictionary= "Google Image";
                        break;
                }

            }
        });

    }


    public void setDefaultComboSearchCodeThirdDictionaryOriginal(){

        defaultComboSearchAlertDialogThirdDictionary.setSingleChoiceItems(defaultDictionaryListOriginal, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                switch (position) {
                    case 0:
                        defaultComboSearchCodeThirdDictionary= "Yahoo Dictionary";
                        break;
                    case 1:
                        defaultComboSearchCodeThirdDictionary= "National Academy for Educational Research";
                        break;
                    case 2:
                        defaultComboSearchCodeThirdDictionary= "Dict Site";
                        break;
                    case 3:
                        defaultComboSearchCodeThirdDictionary= "Cidian8";
                        break;
                    case 4:
                        defaultComboSearchCodeThirdDictionary= "VoiceTube";
                        break;
                    case 5:
                        defaultComboSearchCodeThirdDictionary= "Cambridge EN-CH";
                        break;
                    case 6:
                        defaultComboSearchCodeThirdDictionary= "Merriam Webster";
                        break;
                    case 7:
                        defaultComboSearchCodeThirdDictionary= "Collins";
                        break;
                    case 8:
                        defaultComboSearchCodeThirdDictionary= "Oxford";
                        break;
                    case 9:
                        defaultComboSearchCodeThirdDictionary= "Vocabulary";
                        break;
                    case 10:
                        defaultComboSearchCodeThirdDictionary= "Dictionary.com";
                        break;
                    case 11:
                        defaultComboSearchCodeThirdDictionary= "The Free Dictionary";
                        break;
                    case 12:
                        defaultComboSearchCodeThirdDictionary= "Your_Dictionary";
                        break;
                    case 13:
                        defaultComboSearchCodeThirdDictionary= "Longman Dictionary";
                        break;
                    case 14:
                        defaultComboSearchCodeThirdDictionary= "Greens dictionary of slang";
                        break;
                    case 15:
                        defaultComboSearchCodeThirdDictionary= "Wiki Dictionary";
                        break;
                    case 16:
                        defaultComboSearchCodeThirdDictionary= "Word Hippo";
                        break;
                    case 17:
                        defaultComboSearchCodeThirdDictionary= "Onelook";
                        break;
                    case 18:
                        defaultComboSearchCodeThirdDictionary= "Business Dictionary";
                        break;
                    case 19:
                        defaultComboSearchCodeThirdDictionary= "Slang";
                        break;
                    case 20:
                        defaultComboSearchCodeThirdDictionary= "YouGlish";
                        break;
                    case 21:
                        defaultComboSearchCodeThirdDictionary= "SCI Dictionary";
                        break;
                    case 22:
                        defaultComboSearchCodeThirdDictionary= "TechDico";
                        break;
                    case 23:
                        defaultComboSearchCodeThirdDictionary= "BioMedical Dictionary";
                        break;
                    case 24:
                        defaultComboSearchCodeThirdDictionary= "IsPlural Dictionary";
                        break;
                    case 25:
                        defaultComboSearchCodeThirdDictionary= "LingoHelpPrepositions";
                        break;
                    case 26:
                        defaultComboSearchCodeThirdDictionary= "WordWeb";
                        break;
                    case 27:
                        defaultComboSearchCodeThirdDictionary= "Weblio JP";
                        break;
                    case 28:
                        defaultComboSearchCodeThirdDictionary= "Weblio CN";
                        break;
                    case 29:
                        defaultComboSearchCodeThirdDictionary= "Weblio EN";
                        break;
                    case 30:
                        defaultComboSearchCodeThirdDictionary= "Weblio Synonym";
                        break;
                    case 31:
                        defaultComboSearchCodeThirdDictionary= "Tangorin Word";
                        break;
                    case 32:
                        defaultComboSearchCodeThirdDictionary= "Tangorin Kanji";
                        break;
                    case 33:
                        defaultComboSearchCodeThirdDictionary= "Tangorin Names";
                        break;
                    case 34:
                        defaultComboSearchCodeThirdDictionary= "Tangorin Sentence";
                        break;
                    case 35:
                        defaultComboSearchCodeThirdDictionary= "DA JP-TW Dictionary";
                        break;
                    case 36:
                        defaultComboSearchCodeThirdDictionary= "DA TW-JP Dictionary";
                        break;
                    case 37:
                        defaultComboSearchCodeThirdDictionary= "Cidianwang";
                        break;
                    case 38:
                        defaultComboSearchCodeThirdDictionary= "Goo";
                        break;
                    case 39:
                        defaultComboSearchCodeThirdDictionary= "Sanseido";
                        break;
                    case 40:
                        defaultComboSearchCodeThirdDictionary= "Kotoba Bank";
                        break;
                    case 41:
                        defaultComboSearchCodeThirdDictionary= "J Logos";
                        break;
                    case 42:
                        defaultComboSearchCodeThirdDictionary= "Japanese Industry Terms";
                        break;
                    case 43:
                        defaultComboSearchCodeThirdDictionary= "Kanji Dictionary Online";
                        break;
                    case 44:
                        defaultComboSearchCodeThirdDictionary= "Eijirou";
                        break;
                    case 45:
                        defaultComboSearchCodeThirdDictionary= "How do you say this in English";
                        break;
                    case 46:
                        defaultComboSearchCodeThirdDictionary= "Jisho";
                        break;
                    case 47:
                        defaultComboSearchCodeThirdDictionary= "Word Plus Chinese";
                        break;
                    case 48:
                        defaultComboSearchCodeThirdDictionary= "Word Plus English 1";
                        break;
                    case 49:
                        defaultComboSearchCodeThirdDictionary= "Word Plus English 2";
                        break;
                    case 50:
                        defaultComboSearchCodeThirdDictionary= "Word Plus Translation";
                        break;
                    case 51:
                        defaultComboSearchCodeThirdDictionary= "Word Plus Japanese 1";
                        break;
                    case 52:
                        defaultComboSearchCodeThirdDictionary= "Word Plus Japanese 2";
                        break;
                    case 53:
                        defaultComboSearchCodeThirdDictionary= "Word Plus Japanese 3";
                        break;
                    case 54:
                        defaultComboSearchCodeThirdDictionary= "Word Plus Meaning 1";
                        break;
                    case 55:
                        defaultComboSearchCodeThirdDictionary= "Word Plus Meaning 2";
                        break;
                    case 56:
                        defaultComboSearchCodeThirdDictionary= "Google translate to CHTW";
                        break;
                    case 57:
                        defaultComboSearchCodeThirdDictionary= "Google translate to CHCN";
                        break;
                    case 58:
                        defaultComboSearchCodeThirdDictionary= "Google translate to EN";
                        break;
                    case 59:
                        defaultComboSearchCodeThirdDictionary= "Google translate to JP";
                        break;
                    case 60:
                        defaultComboSearchCodeThirdDictionary= "Google translate to KR";
                        break;
                    case 61:
                        defaultComboSearchCodeThirdDictionary= "Google translate to SP";
                        break;
                    case 62:
                        defaultComboSearchCodeThirdDictionary= "Google Image";
                        break;
                    case 63:
                        defaultComboSearchCodeThirdDictionary= "Ludwig";
                        break;
                    case 64:
                        defaultComboSearchCodeThirdDictionary= "Your Dictionary Example Sentences";
                        break;
                    case 65:
                        defaultComboSearchCodeThirdDictionary= "Word Cool EN-CH";
                        break;
                    case 66:
                        defaultComboSearchCodeThirdDictionary= "Word Cool EN-JP";
                        break;
                    case 67:
                        defaultComboSearchCodeThirdDictionary= "Word Cool JP-CH";
                        break;
                    case 68:
                        defaultComboSearchCodeThirdDictionary= "Linguee CH-EN";
                        break;
                    case 69:
                        defaultComboSearchCodeThirdDictionary= "Linguee JP-EN";
                        break;
                    case 70:
                        defaultComboSearchCodeThirdDictionary= "Wikipedia TW";
                        break;
                    case 71:
                        defaultComboSearchCodeThirdDictionary= "Wikipedia EN";
                        break;
                    case 72:
                        defaultComboSearchCodeThirdDictionary= "English Encyclopedia";
                        break;
                    case 73:
                        defaultComboSearchCodeThirdDictionary= "Forvo";
                        break;
                    case 74:
                        defaultComboSearchCodeThirdDictionary= "Wiki Diff";
                        break;
                    case 75:
                        defaultComboSearchCodeThirdDictionary= "Net Speak";
                        break;
                    case 76:
                        defaultComboSearchCodeThirdDictionary= "Yomikata";
                        break;
                    case 77:
                        defaultComboSearchCodeThirdDictionary= "Chigai";
                        break;
                    case 78:
                        defaultComboSearchCodeThirdDictionary= "OJAD";
                        break;
                }

            }
        });

    }


    public void setDefaultComboSearchCodeThirdDictionarySimplified(){

        defaultComboSearchAlertDialogThirdDictionary.setSingleChoiceItems(defaultDictionaryListSimplified, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                switch (position) {
                    case 0:
                        defaultComboSearchCodeThirdDictionary= "Yahoo Dictionary";
                        break;
                    case 1:
                        defaultComboSearchCodeThirdDictionary= "National Academy for Educational Research";
                        break;
                    case 2:
                        defaultComboSearchCodeThirdDictionary= "Dict Site";
                        break;
                    case 3:
                        defaultComboSearchCodeThirdDictionary= "VoiceTube";
                        break;
                    case 4:
                        defaultComboSearchCodeThirdDictionary= "Cambridge EN-CH";
                        break;
                    case 5:
                        defaultComboSearchCodeThirdDictionary= "Weblio JP";
                        break;
                    case 6:
                        defaultComboSearchCodeThirdDictionary= "Weblio CN";
                        break;
                    case 7:
                        defaultComboSearchCodeThirdDictionary= "DA JP-TW Dictionary";
                        break;
                    case 8:
                        defaultComboSearchCodeThirdDictionary= "DA TW-JP Dictionary";
                        break;
                    case 9:
                        defaultComboSearchCodeThirdDictionary= "Google translate to CHTW";
                        break;
                    case 10:
                        defaultComboSearchCodeThirdDictionary= "Google translate to CHCN";
                        break;
                    case 11:
                        defaultComboSearchCodeThirdDictionary= "Google Image";
                        break;
                }

            }
        });

    }


    /**
     * 載入預設字典
     */
    public void loadDefaultDictionaries() {

        defaultSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchKeyword=wordInputView.getText().toString(); //抓用戶輸入的關鍵字

                switch(defaultSingleSearchCode){
                    case "":  //若用戶尚未設置任何預設字典(預設字典的代碼是空的)
                        if(proOrSimplifiedLayoutSwitch.isChecked()) {  //檢查若已已啟動專業版版面
                            setDefaultDictionariesOriginal();          //則載入專業版預設字典選單
                        } else {
                            setDefaultDictionariesSimplified();        //反之，載入簡易版預設字典選單
                        }
                        Toast.makeText(getApplicationContext(),R.string.Please_set_a_default_dictionary,Toast.LENGTH_LONG).show();
                        return;
                    case "Yahoo Dictionary":
                        String yahooDictionaryUrl= "https://tw.dictionary.search.yahoo.com/search;_ylt=AwrtXGoL8vtcAQoAnHV9rolQ;_ylc=X1MDMTM1MTIwMDM4MQRfcgMyBGZyA3NmcARncHJpZAN0RjJnMS51MlNWU3NDZ1pfVC4zNUFBBG5fcnNsdAMwBG5fc3VnZwM0BG9yaWdpbgN0dy5kaWN0aW9uYXJ5LnNlYXJjaC55YWhvby5jb20EcG9zAzAEcHFzdHIDBHBxc3RybAMEcXN0cmwDMwRxdWVyeQNHQVkEdF9zdG1wAzE1NjAwMTU0MTE-?p="+searchKeyword+"&fr2=sb-top-tw.dictionary.search&fr=sfp";
                        webViewBrowser.loadUrl(yahooDictionaryUrl);
                        break;
                    case "National Academy for Educational Research":
                        String naerUrl= "http://terms.naer.edu.tw/search/?q="+searchKeyword+"&field=ti&op=AND&group=&num=10";
                        webViewBrowser.loadUrl(naerUrl);
                        break;
                    case "Dict Site":
                        String dictDotSiteUrl= "http://dict.site/"+searchKeyword+".html";
                        webViewBrowser.loadUrl(dictDotSiteUrl);
                        break;
                    case "Cidian8":
                        String cidian8Url= "http://www.cidian8.com/ec/?w="+searchKeyword;
                        webViewBrowser.loadUrl(cidian8Url);
                        break;
                    case "VoiceTube":
                        String voicetubeUrl= "https://tw.voicetube.com/definition/"+searchKeyword;
                        webViewBrowser.loadUrl(voicetubeUrl);
                        break;
                    case "Cambridge EN-CH":
                        String cambridgeDictionaryUrl= "https://dictionary.cambridge.org/zht/詞典/英語-漢語-繁體/"+searchKeyword;
                        webViewBrowser.loadUrl(cambridgeDictionaryUrl);
                        break;
                    case "Merriam Webster":
                        String merriamDictionaryUrl= "https://www.merriam-webster.com/dictionary/"+searchKeyword;
                        webViewBrowser.loadUrl(merriamDictionaryUrl);
                        break;
                    case "Collins":
                        String collinsDictionaryUrl= "https://www.collinsdictionary.com/dictionary/english/"+searchKeyword;
                        webViewBrowser.loadUrl(collinsDictionaryUrl);
                        break;
                    case "Oxford":
                        String oxfordDictionaryUrl= "https://en.oxforddictionaries.com/definition/"+searchKeyword;
                        webViewBrowser.loadUrl(oxfordDictionaryUrl);
                        break;
                    case "Vocabulary":
                        String vocabularyDotComUrl= "https://www.vocabulary.com/dictionary/"+searchKeyword;
                        webViewBrowser.loadUrl(vocabularyDotComUrl);
                        break;
                    case "Dictionary.com":
                        String dictionaryDotComUrl= "https://www.dictionary.com/browse/"+searchKeyword;
                        webViewBrowser.loadUrl(dictionaryDotComUrl);
                        break;
                    case "The Free Dictionary":
                        String theFreeDictionaryUrl= "https://www.thefreedictionary.com/"+searchKeyword;
                        webViewBrowser.loadUrl(theFreeDictionaryUrl);
                        break;
                    case "Your_Dictionary":
                        String yourDictionaryUrl= "https://www.yourdictionary.com/"+searchKeyword;
                        webViewBrowser.loadUrl(yourDictionaryUrl);
                        break;
                    case "Longman Dictionary":
                        String longmanDictionaryUrl= "https://www.ldoceonline.com/dictionary/"+searchKeyword;
                        webViewBrowser.loadUrl(longmanDictionaryUrl);
                        break;
                    case "Greens dictionary of slang":
                        String greensDictionaryOfSlangUrl= "https://greensdictofslang.com/search/basic?q="+searchKeyword;
                        webViewBrowser.loadUrl(greensDictionaryOfSlangUrl);
                        break;
                    case "Wiki Dictionary":
                        String wiktionaryUrl= "https://en.wiktionary.org/wiki/"+searchKeyword;
                        webViewBrowser.loadUrl(wiktionaryUrl);
                        break;
                    case "Word Hippo":
                        String wordHippoUrl= "https://www.wordhippo.com/what-is/another-word-for/"+searchKeyword+".html";
                        webViewBrowser.loadUrl(wordHippoUrl);
                        break;
                    case "Onelook":
                        String onelookUrl= "https://www.onelook.com/thesaurus/?s="+searchKeyword;
                        webViewBrowser.loadUrl(onelookUrl);
                        break;
                    case "Business Dictionary":
                        String businessDictionaryUrl= "http://www.businessdictionary.com/definition/"+searchKeyword+".html";
                        webViewBrowser.loadUrl(businessDictionaryUrl);
                        break;
                    case "Slang":
                        String slangDictionary= "http://www.yiym.com/?s="+searchKeyword;
                        webViewBrowser.loadUrl(slangDictionary);
                        break;
                    case "YouGlish":
                        String youglishUrl= "https://youglish.com/search/"+searchKeyword+"/all?";
                        webViewBrowser.loadUrl(youglishUrl);
                        break;
                    case "SCI Dictionary":
                        String academiaDictionaryUrl= "http://www.scidict.org/index.aspx?word="+searchKeyword;
                        webViewBrowser.loadUrl(academiaDictionaryUrl);
                        break;
                    case "TechDico":
                        String TechDicoUrl= "https://www.techdico.com/translation/english-chinese/"+searchKeyword+".html";
                        webViewBrowser.loadUrl(TechDicoUrl);
                        break;
                    case "BioMedical Dictionary":
                        String BioMedicalDictionaryUrl= "http://dict.bioon.com/search.asp?txtitle="+searchKeyword+"&searchButton=查词典&matchtype=0";
                        webViewBrowser.loadUrl(BioMedicalDictionaryUrl);
                        break;
                    case "IsPlural Dictionary":
                        String IsPluralDictionaryUrl= "https://www.isplural.com/plural_singular/"+searchKeyword;
                        webViewBrowser.loadUrl(IsPluralDictionaryUrl);
                        break;
                    case "LingoHelpPrepositions":
                        String lingoHelpPrepositionsUrl= "https://lingohelp.me/q/?w="+searchKeyword;
                        webViewBrowser.loadUrl(lingoHelpPrepositionsUrl);
                        break;
                    case "WordWeb":
                        String wordWebUrl= "https://www.wordwebonline.com/search.pl?w="+searchKeyword;
                        webViewBrowser.loadUrl(wordWebUrl);
                        break;
                    case "Weblio JP":
                        String weblioJPUrl= "https://www.weblio.jp/content/"+searchKeyword;
                        webViewBrowser.loadUrl(weblioJPUrl);
                        break;
                    case "Weblio CN":
                        String weblioCHUrl= "https://cjjc.weblio.jp/content/"+searchKeyword;
                        webViewBrowser.loadUrl(weblioCHUrl);
                        break;
                    case "Weblio EN":
                        String weblioENUrl= "https://ejje.weblio.jp/content/"+searchKeyword;
                        webViewBrowser.loadUrl(weblioENUrl);
                        break;
                    case "Weblio Synonym":
                        String weblioThesaurusUrl= "https://thesaurus.weblio.jp/content/"+searchKeyword;
                        webViewBrowser.loadUrl(weblioThesaurusUrl);
                        break;
                    case "Tangorin Word":
                        String tangorinDictionaryUrl= "https://tangorin.com/words?search="+searchKeyword;
                        webViewBrowser.loadUrl(tangorinDictionaryUrl);
                        break;
                    case "Tangorin Kanji":
                        String tangorinKanjiUrl= "https://tangorin.com/kanji?search="+searchKeyword;
                        webViewBrowser.loadUrl(tangorinKanjiUrl);
                        break;
                    case "Tangorin Names":
                        String tangorinNamesUrl= "https://tangorin.com/names?search="+searchKeyword;
                        webViewBrowser.loadUrl(tangorinNamesUrl);
                        break;
                    case "Tangorin Sentence":
                        String tangorinSentencesUrl= "https://tangorin.com/sentences?search="+searchKeyword;
                        webViewBrowser.loadUrl(tangorinSentencesUrl);
                        break;
                    case "DA JP-TW Dictionary":
                        String DaJPtoCHDictionaryUrl= "http://dict.asia/jc/"+searchKeyword;
                        webViewBrowser.loadUrl(DaJPtoCHDictionaryUrl);
                        break;
                    case "DA TW-JP Dictionary":
                        String DaCHtoJPDictionaryUrl= "http://dict.asia/cj/"+searchKeyword;
                        webViewBrowser.loadUrl(DaCHtoJPDictionaryUrl);
                        break;
                    case "Cidianwang":
                        String CidianwangUrl= "http://m.cidianwang.com/search/jp/?q="+searchKeyword+"&y=1";
                        webViewBrowser.loadUrl(CidianwangUrl);
                        break;
                    case "Goo":
                        String gooDictionaryUrl= "https://dictionary.goo.ne.jp/srch/jn/"+searchKeyword+"/m0u/";
                        webViewBrowser.loadUrl(gooDictionaryUrl);
                        break;
                    case "Sanseido":
                        String sanseidoUrl= "https://www.sanseido.biz/User/Dic/Index.aspx?TWords="+searchKeyword+"&st=0&DORDER=151617&DailyJJ=checkbox&DailyEJ=checkbox&DailyJE=checkbox";
                        webViewBrowser.loadUrl(sanseidoUrl);
                        break;
                    case "Kotoba Bank":
                        String kotobank= "https://kotobank.jp/word/"+searchKeyword;
                        webViewBrowser.loadUrl(kotobank);
                        break;
                    case "J Logos":
                        String jlogosUrl= "http://s.jlogos.com/list.html?keyword="+searchKeyword+"&opt_val=0";
                        webViewBrowser.loadUrl(jlogosUrl);
                        break;
                    case "Japanese Industry Terms":
                        String industryDictionaryUrl= "https://www.sangyo-honyaku.jp/dictionaries/index/search_info:"+searchKeyword+"_ＩＴ・機械・電気電子";
                        webViewBrowser.loadUrl(industryDictionaryUrl);
                        break;
                    case "Kanji Dictionary Online":
                        String KanjiDictionaryUrl= "https://kanji.jitenon.jp/cat/search.php?getdata="+searchKeyword+"&search=fpart&search2=twin";
                        webViewBrowser.loadUrl(KanjiDictionaryUrl);
                        break;
                    case "Eijirou":
                        String eijiroDictionryUrl= "https://eow.alc.co.jp/search?q="+searchKeyword;
                        webViewBrowser.loadUrl(eijiroDictionryUrl);
                        break;
                    case "How do you say this in English":
                        String whatIsItInEnglishUrl= "https://eikaiwa.dmm.com/uknow/search/?keyword="+searchKeyword;
                        webViewBrowser.loadUrl(whatIsItInEnglishUrl);
                        break;
                    case "Jisho":
                        String jishoUrl= "https://jisho.org/search/"+searchKeyword;
                        webViewBrowser.loadUrl(jishoUrl);
                        break;
                    case "Word Plus Chinese":
                        String googlePlusChinese= "http://www.google.com/search?q="+searchKeyword+"+中文";
                        webViewBrowser.loadUrl(googlePlusChinese);
                        break;
                    case "Word Plus English 1":
                        String googlePlusENglish1= "http://www.google.com/search?q="+searchKeyword+"+英文";
                        webViewBrowser.loadUrl(googlePlusENglish1);
                        break;
                    case "Word Plus English 2":
                        String googlePlusENglish2= "http://www.google.com/search?q="+searchKeyword+"+英語";
                        webViewBrowser.loadUrl(googlePlusENglish2);
                        break;
                    case "Word Plus Translation":
                        String googlePlusTranslation= "http://www.google.com/search?q="+searchKeyword+"+翻譯";
                        webViewBrowser.loadUrl(googlePlusTranslation);
                        break;
                    case "Word Plus Japanese 1":
                        String googlePlusJapanese1= "http://www.google.com/search?q="+searchKeyword+"+日文";
                        webViewBrowser.loadUrl(googlePlusJapanese1);
                        break;
                    case "Word Plus Japanese 2":
                        String googlePlusJapanese2= "http://www.google.com/search?q="+searchKeyword+"+日語";
                        webViewBrowser.loadUrl(googlePlusJapanese2);
                        break;
                    case "Word Plus Japanese 3":
                        String googlePlusJapanese3= "http://www.google.com/search?q="+searchKeyword+"+日本語";
                        webViewBrowser.loadUrl(googlePlusJapanese3);
                        break;
                    case "Word Plus Meaning 1":
                        String googlePlusMeaning1= "http://www.google.com/search?q="+searchKeyword+"+意思";
                        webViewBrowser.loadUrl(googlePlusMeaning1);
                        break;
                    case "Word Plus Meaning 2":
                        String googlePlusMeaning2 = "http://www.google.com/search?q="+searchKeyword+"+meaning";
                        webViewBrowser.loadUrl(googlePlusMeaning2);
                        break;
                    case "Google translate to CHTW":
                        String GoogleTranslateToCHTWUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=zh-TW&text="+searchKeyword;
                        webViewBrowser.loadUrl(GoogleTranslateToCHTWUrl);
                        break;
                    case "Google translate to CHCN":
                        String GoogleTranslateToCHCNUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=zh-CN&text="+searchKeyword;
                        webViewBrowser.loadUrl(GoogleTranslateToCHCNUrl);
                        break;
                    case "Google translate to EN":
                        String GoogleTranslateToENUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=en&text="+searchKeyword;
                        webViewBrowser.loadUrl(GoogleTranslateToENUrl);
                        break;
                    case "Google translate to JP":
                        String GoogleTranslateToJPUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=ja&text="+searchKeyword;
                        webViewBrowser.loadUrl(GoogleTranslateToJPUrl);
                        break;
                    case "Google translate to KR":
                        String GoogleTranslateToKRUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=ko&text="+searchKeyword;
                        webViewBrowser.loadUrl(GoogleTranslateToKRUrl);
                        break;
                    case "Google translate to SP":
                        String GoogleTranslateToSPUrl = "https://translate.google.com/?hl=zh-TW#view=home&op=translate&sl=auto&tl=es&text="+searchKeyword;
                        webViewBrowser.loadUrl(GoogleTranslateToSPUrl);
                        break;
                    case "Google Image":
                        String imageSearchUrl= "http://images.google.com/search?tbm=isch&q="+searchKeyword;
                        webViewBrowser.loadUrl(imageSearchUrl);
                        break;
                    case "Ludwig":
                        String ludwigUrl= "https://ludwig.guru/s/"+searchKeyword;
                        webViewBrowser.loadUrl(ludwigUrl);
                        break;
                    case "Your Dictionary Example Sentences":
                        String yourDictionarySentenceUrl= "https://sentence.yourdictionary.com/"+searchKeyword;
                        webViewBrowser.loadUrl(yourDictionarySentenceUrl);
                        break;
                    case "Word Cool EN-CH":
                        String jukuuUrlCHEN= "http://www.jukuu.com/search.php?q="+searchKeyword;
                        webViewBrowser.loadUrl(jukuuUrlCHEN);
                        break;
                    case "Word Cool EN-JP":
                        String jukuuUrlJPEN= "http://www.jukuu.com/jsearch.php?q="+searchKeyword;
                        webViewBrowser.loadUrl(jukuuUrlJPEN);
                        break;
                    case "Word Cool JP-CH":
                        String jukuuUrlCHJP= "http://www.jukuu.com/jcsearch.php?q="+searchKeyword;
                        webViewBrowser.loadUrl(jukuuUrlCHJP);
                        break;
                    case "Linguee CH-EN":
                        String LingueeUrlCHEN= "https://cn.linguee.com/中文-英语/search?source=auto&query="+searchKeyword;
                        webViewBrowser.loadUrl(LingueeUrlCHEN);
                        break;
                    case "Linguee JP-EN":
                        String LingueeUrlJPEN= "https://www.linguee.jp/日本語-英語/search?source=auto&query="+searchKeyword;
                        webViewBrowser.loadUrl(LingueeUrlJPEN);
                        break;
                    case "Wikipedia TW":
                        String wikipediaTWUrl= "https://zh.wikipedia.org/wiki/"+searchKeyword;
                        webViewBrowser.loadUrl(wikipediaTWUrl);
                        break;
                    case "Wikipedia EN":
                        String wikipediaENUrl= "https://en.wikipedia.org/wiki/"+searchKeyword;
                        webViewBrowser.loadUrl(wikipediaENUrl);
                        break;
                    case "English Encyclopedia":
                        String enEncyclopediaUrl= "https://www.encyclo.co.uk/meaning-of-"+searchKeyword;
                        webViewBrowser.loadUrl(enEncyclopediaUrl);
                        break;
                    case "Forvo":
                        String forvoUrl= "https://forvo.com/search/"+searchKeyword;
                        webViewBrowser.loadUrl(forvoUrl);
                        break;
                    case "Wiki Diff":
                        String wikidiffUrl= "https://wikidiff.com/"+searchKeyword;
                        webViewBrowser.loadUrl(wikidiffUrl);
                        break;
                    case "Net Speak":
                        String netspeakUrl= "http://www.netspeak.org/#query="+searchKeyword;
                        webViewBrowser.loadUrl(netspeakUrl);
                        break;
                    case "Yomikata":
                        String yomikatawaUrl= "https://yomikatawa.com/kanji/"+searchKeyword+"?search=1";
                        webViewBrowser.loadUrl(yomikatawaUrl);
                        break;
                    case "Chigai":
                        String ChigaihaUrl= "https://cse.google.co.jp/cse?cx=partner-pub-1137871985589263%3A3025760782&ie=UTF-8&q="+searchKeyword;
                        webViewBrowser.loadUrl(ChigaihaUrl);
                        break;
                    case "OJAD":
                        String suzukikunUrl= "http://www.gavo.t.u-tokyo.ac.jp/ojad/search/index/sortprefix:accent/narabi1:kata_asc/narabi2:accent_asc/narabi3:mola_asc/yure:visible/curve:invisible/details:invisible/limit:20/word:"+searchKeyword;
                        webViewBrowser.loadUrl(suzukikunUrl);
                        break;
                }

                searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                webViewBrowser.setVisibility(View.VISIBLE);

                browserSwitch.setChecked(true);
                browserNavigateBack.setVisibility(View.VISIBLE);
                browserNavigateForward.setVisibility(View.VISIBLE);

                saveKeywordtoUserInputListView ();
                saveUserInputArrayListToSharedPreferences ();
            }
        });

    }


    public void chooseActionAlertDialog() {
        //這邊設置第一層AlertDialog讓用戶選擇要查此單字、快搜模式或三連搜模式、估狗翻譯，或記憶單字
        AlertDialog.Builder chooseActionAlertDialog = new AlertDialog.Builder(this);
        chooseActionAlertDialog.setTitle(getString(R.string.Do_you_want_to));
        chooseActionAlertDialog.setCancelable(true); //按到旁邊的空白處AlertDialog會消失
        chooseActionAlertDialog.setView(R.layout.custom_alert_dialog_dictionary_providers); //沿用字典選單的佈局檔

        //第一層AlertDialog的確定鈕，把單字傳送到wordInputView查此單字
        chooseActionAlertDialog.setPositiveButton(R.string.Send_to_WordInputView, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                MainActivity.wordInputView.setText(intentReceivedText);
            }
        });

        //第一層AlertDialog的中立鈕，記憶單字
        chooseActionAlertDialog.setNeutralButton(R.string.Memorize_this_word, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                //這邊設置第二層AlertDialog讓用戶選擇自定義或預設的通知時機
                final AlertDialog.Builder chooseCustomizedOrPredefinedNotificationAlertDialog = new AlertDialog.Builder(MainActivity.this);
                chooseCustomizedOrPredefinedNotificationAlertDialog.setTitle(getString(R.string.Choose_customized_or_predefined_notification_timings));
                chooseCustomizedOrPredefinedNotificationAlertDialog.setCancelable(false); //按到旁邊的空白處AlertDialog也不會消失
                chooseCustomizedOrPredefinedNotificationAlertDialog.setView(R.layout.custom_alert_dialog_dictionary_providers); //沿用字典選單的佈局檔


                //第二層AlertDialog的確定鈕，自定義通知時機
                chooseCustomizedOrPredefinedNotificationAlertDialog.setPositiveButton(R.string.Customize_timing, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        setCustomizedNotificationTiming();

                    }
                });

                //第二層AlertDialog的中立鈕，預設的通知時機。
                chooseCustomizedOrPredefinedNotificationAlertDialog.setNeutralButton(R.string.Use_predefined_timing, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface chooseCustomizedOrPredefinedNotificationAlertDialog, int which) {

                        presetNotificationTimingsList = getResources().getStringArray(R.array.preset_notification_timings);

                        //這邊設置第三層AlertDialog讓用戶選擇各種預設通知的時機點
                        AlertDialog.Builder choosePresetNotificationTimingsAlertDialog = new AlertDialog.Builder(MainActivity.this);
                        choosePresetNotificationTimingsAlertDialog.setTitle(getString(R.string.Choose_one_preset_timing));
                        choosePresetNotificationTimingsAlertDialog.setCancelable(false); //按到旁邊的空白處AlertDialog也不會消失

                        choosePresetNotificationTimingsAlertDialog.setSingleChoiceItems(presetNotificationTimingsList, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface choosePresetNotificationTimingsAlertDialog, int position) {
                                switch (position) {
                                    case 0:
                                        setPreDefinedNotificationTimings1Hour();
                                        setPreDefinedNotificationTimings9Hours();
                                        setPreDefinedNotificationTimings1Day();
                                        setPreDefinedNotificationTimings2Days();
                                        setPreDefinedNotificationTimings6Days();
                                        setPreDefinedNotificationTimings1Month();
                                        setPreDefinedNotificationTimingsHalfYear();
                                        setPreDefinedNotificationTimingsOneYear();
                                        //點擊子項目後讓第三層的AlertDialog消失
                                        choosePresetNotificationTimingsAlertDialog.dismiss();
                                        Toast.makeText(getApplicationContext(),R.string.Will_send_the_notifications_on_8_preset_timings,Toast.LENGTH_LONG).show();
                                        break;
                                    case 1:
                                        setPreDefinedNotificationTimings1Hour();
                                        choosePresetNotificationTimingsAlertDialog.dismiss();
                                        break;
                                    case 2:
                                        setPreDefinedNotificationTimings9Hours();
                                        choosePresetNotificationTimingsAlertDialog.dismiss();
                                        break;
                                    case 3:
                                        setPreDefinedNotificationTimings1Day();
                                        choosePresetNotificationTimingsAlertDialog.dismiss();
                                        break;
                                    case 4:
                                        setPreDefinedNotificationTimings2Days();
                                        choosePresetNotificationTimingsAlertDialog.dismiss();
                                        break;
                                    case 5:
                                        setPreDefinedNotificationTimings6Days();
                                        choosePresetNotificationTimingsAlertDialog.dismiss();
                                        break;
                                    case 6:
                                        setPreDefinedNotificationTimings1Month();
                                        choosePresetNotificationTimingsAlertDialog.dismiss();
                                        break;
                                    case 7:
                                        setPreDefinedNotificationTimingsHalfYear();
                                        choosePresetNotificationTimingsAlertDialog.dismiss();
                                        break;
                                    case 8:
                                        setPreDefinedNotificationTimingsOneYear();
                                        choosePresetNotificationTimingsAlertDialog.dismiss();
                                        break;
                                }
                            }
                        });

                        //第三層AlertDialog的取消鈕
                        choosePresetNotificationTimingsAlertDialog.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        //把第三層的AlertDialog顯示出來
                        choosePresetNotificationTimingsAlertDialog.create().show();
                        //同時讓第二層的AlertDialog消失
                        chooseCustomizedOrPredefinedNotificationAlertDialog.dismiss();
                    }
                });

                //第二層AlertDialog的取消鈕
                chooseCustomizedOrPredefinedNotificationAlertDialog.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                //把第二層的AlertDialog顯示出來
                chooseCustomizedOrPredefinedNotificationAlertDialog.create().show();

            }
        });

        //第一層AlertDialog的取消鈕，使用快搜模式、三連搜模式或估狗翻譯
        chooseActionAlertDialog.setNegativeButton(R.string.Quick_search_or_combo_search, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface chooseActionAlertDialog, int which) {

                //這邊設置第二層AlertDialog讓用戶選擇快搜模式、三連搜模式或估狗翻譯
                quickSearchComboSearchOrGoogleTranslateList = getResources().getStringArray(R.array.quick_search_combo_search_or_google_translate);

                AlertDialog.Builder chooseQuickSearchComboSearchOrGoogleTranslateAlertDialog = new AlertDialog.Builder(MainActivity.this);
                chooseQuickSearchComboSearchOrGoogleTranslateAlertDialog.setTitle(getString(R.string.Do_you_want_to));
                chooseQuickSearchComboSearchOrGoogleTranslateAlertDialog.setCancelable(true); //按到旁邊的空白處AlertDialog會消失
                chooseQuickSearchComboSearchOrGoogleTranslateAlertDialog.setView(R.layout.custom_alert_dialog_dictionary_providers); //沿用字典選單的佈局檔

                chooseQuickSearchComboSearchOrGoogleTranslateAlertDialog.setSingleChoiceItems(quickSearchComboSearchOrGoogleTranslateList, -1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface chooseQuickSearchComboSearchOrGoogleTranslateAlertDialog, int position) {
                        switch (position) {
                            case 0:  //快搜
                                defaultSearchButton.performClick();
                                //點擊子項目後讓第三層的AlertDialog消失
                                chooseQuickSearchComboSearchOrGoogleTranslateAlertDialog.dismiss();
                                break;
                            case 1:  //三連搜
                                comboSearchButton.performClick();
                                chooseQuickSearchComboSearchOrGoogleTranslateAlertDialog.dismiss();
                                break;
                            case 2:  //Google翻譯
                                String intentAutoTranslationURL = "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=auto&tl=zh-TW&text=" + intentReceivedText;
                                webViewBrowser.loadUrl(intentAutoTranslationURL);
                                searchResultWillBeDisplayedHere.setVisibility(View.GONE);
                                webViewBrowser.setVisibility(View.VISIBLE);
                                chooseQuickSearchComboSearchOrGoogleTranslateAlertDialog.dismiss();
                                break;
                        }
                    }
                });

                //把第二層的AlertDialog顯示出來
                chooseQuickSearchComboSearchOrGoogleTranslateAlertDialog.create().show();
                //同時讓第一層的AlertDialog消失
                chooseActionAlertDialog.dismiss();

            }
        });


        //將第一層的AlertDialog顯示出來
        chooseActionAlertDialog.create().show();

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
                            event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + intentReceivedText);
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
        event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + intentReceivedText);
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
        event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + intentReceivedText);
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
        event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + intentReceivedText);
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
        event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + intentReceivedText);
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

    public void setPreDefinedNotificationTimings6Days() {

        //設置單字的通知事件
        ContentResolver cr = getContentResolver();
        ContentValues event = new ContentValues();
        event.put(CalendarContract.Events.DTSTART, System.currentTimeMillis()+60*60*24*6*1000);  //抓現在系統的時間的6天後開始
        event.put(CalendarContract.Events.DTEND, System.currentTimeMillis()+60*60*24*6*1000+60*60*1000);
        event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + intentReceivedText);
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
        event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + intentReceivedText);
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
        event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + intentReceivedText);
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
        event.put(CalendarContract.Events.TITLE, getResources().getString(R.string.Do_you_remember_this_word) + intentReceivedText);
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



    //==============================================================================================
    // 設置接收Firebase dynamic links
    //==============================================================================================
    private void checkForDynamicLinks() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Log.i("MainActivity","We have a dynamic link!");
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }


                        // Handle the deep link.
                        if (deepLink != null) {
                            Log.i("MainActivity","Here's the deep link url:\n" + deepLink.toString());
                        }

                        //順便利用此接收功能打開透過Firebase傳送的普通URL(若放在其他位置會導致proOrSimplifiedLayoutSwitch功能失效，原因未知)
                        if (getIntent().getExtras() != null && getIntent().getExtras().getString("URL", null) != null && !getIntent().getExtras().getString("URL", null).equals("")) {
                            //檢查URL有沒有含http或Https，否則Intent會報錯
                            if (Objects.requireNonNull(getIntent().getExtras().getString("URL")).contains("http")) {
                                FriebaseUrl = getIntent().getExtras().getString("URL");
                            } else {
                                FriebaseUrl = "http://" + getIntent().getExtras().getString("URL");
                            }
                            if (FriebaseUrl != null && !FriebaseUrl.startsWith("https://") && !FriebaseUrl.startsWith("http://")) {
                                FriebaseUrl = "http://" + FriebaseUrl;
                            }
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(FriebaseUrl)));
                        }


                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("MainActivity", "Oops, we couldn't retrieve that dynamic link data.", e);
                    }
                });
    }



}




