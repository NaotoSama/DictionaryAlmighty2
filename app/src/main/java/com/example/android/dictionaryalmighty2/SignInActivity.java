package com.example.android.dictionaryalmighty2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.processphoenix.ProcessPhoenix;

import java.util.Arrays;

import static com.example.android.dictionaryalmighty2.MainActivity.mChildReferenceForInputHistory;
import static com.example.android.dictionaryalmighty2.MainActivity.mChildReferenceForVocabularyList;
import static com.example.android.dictionaryalmighty2.MainActivity.userScreenName;
import static com.example.android.dictionaryalmighty2.MainActivity.userScreenNameSharedPreferences;
import static com.example.android.dictionaryalmighty2.MainActivity.username;
import static com.example.android.dictionaryalmighty2.MainActivity.usernameSharedPreferences;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";

    public static final int RC_SIGN_IN = 1;

    private TextView mStatusTextView; //顯示用戶信箱
    private TextView mDetailTextView; //顯示用戶UID
    private TextView mScreenNameTextView; //顯示用戶暱稱
    private TextView firebaseUidTextView; //UID標題
    private ProgressBar mProgressBar;
    private Button signOutButton;
    private Button updateUserButton;
    private Button unregisterButton;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        // Initialize references to views
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mStatusTextView = findViewById(R.id.status);
        mDetailTextView = findViewById(R.id.detail);
        mScreenNameTextView = findViewById(R.id.screen_name_textView);
        firebaseUidTextView = findViewById(R.id.firebase_UID_textView);
        signOutButton = findViewById(R.id.sign_out_button);
        updateUserButton = findViewById(R.id.update_user_button);
        unregisterButton = findViewById(R.id.unregister_button);


        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);


        /**
         * 監聽用戶登入狀態
         */
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    //                    toastMessage("Successfully signed in with: " + user.getEmail());
                    mStatusTextView.setText(getString(R.string.Google_status_fmt, user.getEmail()));
                    mDetailTextView.setText(getString(R.string.Firebase_status_fmt, user.getUid()));
                    mScreenNameTextView.setVisibility(View.VISIBLE);
                    mScreenNameTextView.setText(getResources().getString(R.string.User) + user.getDisplayName());


                    //抓用戶Firebase UID和暱稱
                    username = mDetailTextView.getText().toString();
                    userScreenName = mScreenNameTextView.getText().toString();

                    //儲存用戶Firebase UID和暱稱
                    usernameSharedPreferences = getSharedPreferences("usernameSharedPreferences", MODE_PRIVATE);
                    usernameSharedPreferences.edit().putString("userName", username).apply();

                    userScreenNameSharedPreferences = getSharedPreferences("userScreenNameSharedPreferences", MODE_PRIVATE);
                    userScreenNameSharedPreferences.edit().putString("userScreenName", userScreenName).apply();


                    //播放特效
                    YoYo.with(Techniques.ZoomIn)
                            .duration(700)
                            .repeat(0)
                            .playOn(findViewById(R.id.AppIcon));
                    YoYo.with(Techniques.FadeIn)
                            .duration(1000)
                            .repeat(0)
                            .playOn(findViewById(R.id.titleText));
                    YoYo.with(Techniques.FadeIn)
                            .duration(1000)
                            .repeat(0)
                            .playOn(findViewById(R.id.status));
                    YoYo.with(Techniques.FadeIn)
                            .duration(1000)
                            .repeat(0)
                            .playOn(findViewById(R.id.firebase_UID_textView));
                    YoYo.with(Techniques.FadeIn)
                            .duration(1000)
                            .repeat(0)
                            .playOn(findViewById(R.id.detail));
                    YoYo.with(Techniques.FadeIn)
                            .duration(1000)
                            .repeat(0)
                            .playOn(findViewById(R.id.screen_name_textView));
                }

                else {
                    // User is signed out

                    //清除本地用戶Firebase UID和暱稱
                    username = null;
                    userScreenName = null;

                    //儲存用戶Firebase UID和暱稱
                    usernameSharedPreferences = getSharedPreferences("usernameSharedPreferences", MODE_PRIVATE);
                    usernameSharedPreferences.edit().putString("userName", username).apply();

                    userScreenNameSharedPreferences = getSharedPreferences("userScreenNameSharedPreferences", MODE_PRIVATE);
                    userScreenNameSharedPreferences.edit().putString("userScreenName", userScreenName).apply();


                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.FacebookBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.PhoneBuilder().build()
//                                            ,
//                                            new AuthUI.IdpConfig.TwitterBuilder().build()
                                    ))
                                    .setTosAndPrivacyPolicyUrls("https://www.websitepolicies.com/policies/view/TG8EcGnL",
                                            "https://www.websitepolicies.com/policies/view/lSxepPF4")
                                    .build(),
                            RC_SIGN_IN);
                }

            }
        };


        /**
         * 登出鈕
         */
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //這邊設置AlertDialog讓用戶確認登出
                CFAlertDialog.Builder signOutAlertDialogBuilder = new CFAlertDialog.Builder(SignInActivity.this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setDialogBackgroundColor(Color.parseColor("#fafcd7"))
                .setCornerRadius(50)
                .setTitle(getResources().getString(R.string.Do_you_really_want_to_log_out))
                .setTextColor(Color.BLUE)
                .setCancelable(false) //按到旁邊的空白處AlertDialog也不會消失
                .setHeaderView(R.layout.custom_alert_diaglog_question_mark)

                //AlertDialog的確定鈕，登出用戶
                .addButton(getResources().getString(R.string.Confirm)
                        , Color.WHITE, Color.GREEN, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {

                        // Firebase sign out
                        mFirebaseAuth.signOut();

                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.You_are_not_signed_in) + " " + getResources().getString(R.string.You_are_using_local_storage) + " " + getResources().getString(R.string.Relaunching_app), Toast.LENGTH_LONG).show();

                        //延遲5秒重啟App
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                //清除本地用戶Firebase UID和暱稱
                                username = null;
                                userScreenName = null;

                                //儲存用戶Firebase UID和暱稱
                                usernameSharedPreferences = getSharedPreferences("usernameSharedPreferences", MODE_PRIVATE);
                                usernameSharedPreferences.edit().putString("userName", username).apply();

                                userScreenNameSharedPreferences = getSharedPreferences("userScreenNameSharedPreferences", MODE_PRIVATE);
                                userScreenNameSharedPreferences.edit().putString("userScreenName", userScreenName).apply();

                                onBackPressed();   //要加入這句把多餘的登入畫面退出，才不會卡在登入畫面
                                relaunchApp();
                            }
                        };
                        Handler h =new Handler();
                        h.postDelayed(r, 5000);

                        dialog.dismiss();
                })

                //AlertDialog的取消鈕，取消登出
                .addButton(getResources().getString(R.string.Cancel)
                        , Color.CYAN, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {

                        dialog.dismiss();
                });

                //把AlertDialog顯示出來
                signOutAlertDialogBuilder.create().show();

            }
        });


        /**
         * 用戶資訊更新鈕
         */
        updateUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //這邊設置第一層AlertDialog讓用戶更改帳戶資訊
                CFAlertDialog.Builder updateUserAccountAlertDialogBuilder = new CFAlertDialog.Builder(SignInActivity.this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setDialogBackgroundColor(Color.parseColor("#fafcd7"))
                .setCornerRadius(50)
                .setTitle(getResources().getString(R.string.Change_user_info))
                .setTextColor(Color.BLUE)
                .setCancelable(true) //按到旁邊的空白處AlertDialog也不會消失
                .setHeaderView(R.layout.custom_alert_alert_dialog_edit_user_info)


                //第一層AlertDialog的中立鈕，更改顯示暱稱
                .addButton(getResources().getString(R.string.Change_user_screen_name)
                        , Color.BLACK, Color.YELLOW, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (updateUserAccountAlertDialog, whichLayer1) -> {

                        //這邊設置第二層AlertDialog
                        final EditText userInputView = new EditText(getApplicationContext()); //在對話框內創建文字輸入框
                        userInputView.setLines(2);
                        userInputView.setHint(getString(R.string.Type_your_new_screen_name));
                        CFAlertDialog.Builder updateDisplayNameDialogBuilder = new CFAlertDialog.Builder(SignInActivity.this)
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                        .setDialogBackgroundColor(Color.parseColor("#fafcd7"))
                        .setCornerRadius(50)
                        .setCancelable(false) //按到旁邊的空白處AlertDialog不會消失
                        .setHeaderView(userInputView)


                        //第二層AlertDialog的確定鈕
                        .addButton(getString(R.string.Confirm)
                                , Color.WHITE, Color.GREEN, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (updateDisplayNameDialog, whichLayer2) -> {

                                String userInputDisplayName = userInputView.getText().toString();

                                if (!userInputDisplayName.equals("")) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(userInputDisplayName)
                                            .build();

                                    if (user != null) {
                                        user.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "顯示暱稱更新成功");

                                                            //把顯示暱稱改成用樹輸入的新暱稱並儲存起來
                                                            mScreenNameTextView.setText(getResources().getString(R.string.User) + userInputDisplayName);
                                                            userScreenNameSharedPreferences = getSharedPreferences("userScreenNameSharedPreferences", MODE_PRIVATE);
                                                            userScreenNameSharedPreferences.edit().putString("userScreenName", userInputDisplayName).apply();

                                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Your_screen_name_is_changed_successfully), Toast.LENGTH_LONG).show();

                                                            //延遲5秒重啟App
                                                            Runnable r = new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    relaunchApp();
                                                                }
                                                            };
                                                            Handler h =new Handler();
                                                            h.postDelayed(r, 5000);
                                                        }

                                                    }
                                                });
                                    }

                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.You_have_not_entered_anything, Toast.LENGTH_LONG).show();
                                }

                                    updateDisplayNameDialog.dismiss();
                        })


                        //第二層AlertDialog的取消鈕
                        .addButton(getString(R.string.Cancel)
                                , Color.CYAN, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (updateDisplayNameDialog, whichLayer2) -> {

                                    updateDisplayNameDialog.dismiss();
                        });


                        //把第二層AlertDialog顯示出來
                        updateDisplayNameDialogBuilder.show();
                        //同時讓第一層AlertDialog消失
                        updateUserAccountAlertDialog.dismiss();
                })


                //第一層AlertDialog的確定鈕，更改密碼
                .addButton(getResources().getString(R.string.Change_password)
                        , Color.WHITE, Color.GREEN, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (updateUserAccountAlertDialog, whichLayer1) -> {

                        //這邊設置第二層AlertDialog
                        final EditText userInputView = new EditText(getApplicationContext()); //在對話框內創建文字輸入框
                        userInputView.setLines(4);
                        userInputView.setHint(getString(R.string.Type_your_new_password) + getString(R.string.Please_log_in_and_out_again));
                        CFAlertDialog.Builder updatePasswordDialogBuilder = new CFAlertDialog.Builder(SignInActivity.this)
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                        .setDialogBackgroundColor(Color.parseColor("#fafcd7"))
                        .setCornerRadius(50)
                        .setCancelable(false) //按到旁邊的空白處AlertDialog不會消失
                        .setHeaderView(userInputView)


                        //第二層AlertDialog的確定鈕
                        .addButton(getString(R.string.Confirm)
                                , Color.WHITE, Color.GREEN, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (updatePasswordDialog, whichLayer2) -> {

                                String userInputPassword = userInputView.getText().toString();

                                if (!userInputPassword.equals("")) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    if (user != null) {

                                        user.updatePassword(userInputPassword)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Your_passwod_is_changed_successfully), Toast.LENGTH_LONG).show();

                                                            //延遲5秒重啟App
                                                            Runnable r = new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    relaunchApp();
                                                                }
                                                            };
                                                            Handler h =new Handler();
                                                            h.postDelayed(r, 5000);
                                                        }
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_password_change_failed), Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    }

                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.You_have_not_entered_anything, Toast.LENGTH_LONG).show();
                                }

                                    updatePasswordDialog.dismiss();
                        })


                        //第二層AlertDialog的取消鈕
                        .addButton(getString(R.string.Cancel), Color.CYAN, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (updatePasswordDialog, whichLayer2) -> {

                            updatePasswordDialog.dismiss();
                        });


                        //把第二層AlertDialog顯示出來
                        updatePasswordDialogBuilder.show();
                        //同時讓第一層AlertDialog消失
                        updateUserAccountAlertDialog.dismiss();
                })


                //第一層AlertDialog的中立鈕，更改信箱
                .addButton(getResources().getString(R.string.Change_email)
                        , Color.BLACK, Color.MAGENTA, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (updateUserAccountAlertDialog, whichLayer1) -> {

                        //這邊設置第二層AlertDialog
                        final EditText userInputView = new EditText(getApplicationContext()); //在對話框內創建文字輸入框
                        userInputView.setLines(4);
                        userInputView.setHint(getString(R.string.Type_your_new_email) + getString(R.string.Please_log_in_and_out_again));
                        CFAlertDialog.Builder updateEmailDialogBuilder = new CFAlertDialog.Builder(SignInActivity.this)
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                        .setDialogBackgroundColor(Color.parseColor("#fafcd7"))
                        .setCornerRadius(50)
                        .setCancelable(false) //按到旁邊的空白處AlertDialog不會消失
                        .setHeaderView(userInputView)


                        //第二層AlertDialog的確定鈕
                        .addButton(getString(R.string.Confirm)
                                , Color.WHITE, Color.GREEN, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (updateEmailDialog, whichLayer2) -> {

                                String userInputEmail = userInputView.getText().toString();

                                if (!userInputEmail.equals("") && userInputEmail.matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$")) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    if (user != null) {
                                        user.updateEmail(userInputEmail)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Your_email_is_changed_successfully), Toast.LENGTH_LONG).show();
                                                            mStatusTextView.setText(getString(R.string.Google_status_fmt, user.getEmail()));

                                                            //延遲5秒重啟App
                                                            Runnable r = new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    relaunchApp();
                                                                }
                                                            };
                                                            Handler h =new Handler();
                                                            h.postDelayed(r, 5000);
                                                        }
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_email_change_failed), Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    }

                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.You_have_not_entered_anything, Toast.LENGTH_LONG).show();
                                }

                                    updateEmailDialog.dismiss();
                        })


                        //第二層AlertDialog的取消鈕
                        .addButton(getString(R.string.Cancel)
                                , Color.CYAN, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (updateEmailDialog, whichLayer2) -> {

                                    updateEmailDialog.dismiss();
                        });


                        //把第二層AlertDialog顯示出來
                        updateEmailDialogBuilder.show();
                        //同時讓第一層AlertDialog消失
                        updateUserAccountAlertDialog.dismiss();
                })


                //第一層AlertDialog的取消鈕
                        .addButton(getString(R.string.Cancel)
                                , Color.CYAN, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (updateUserAccountAlertDialog, whichLayer1) -> {
                                    updateUserAccountAlertDialog.dismiss();
                                });


                //把第一層AlertDialog顯示出來
                updateUserAccountAlertDialogBuilder.show();
            }
        });


        /**
         * 用戶註銷鈕
         */
        unregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //這邊設置AlertDialog讓用戶確認是否真要刪除帳號
                CFAlertDialog.Builder doYouReallyWantToDeleteYourAccountAlertDialogBuilder = new CFAlertDialog.Builder(SignInActivity.this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setDialogBackgroundColor(Color.parseColor("#fafcd7"))
                .setCornerRadius(50)
                .setTitle(getString(R.string.Do_you_really_want_to_delete_account) + getString(R.string.Please_log_in_and_out_again))
                .setTextColor(Color.BLUE)
                .setCancelable(false) //按到旁邊的空白處AlertDialog也不會消失
                .setHeaderView(R.layout.custom_alert_diaglog_question_mark)

                //AlertDialog的確定鈕，刪除帳號
                .addButton(getResources().getString(R.string.Confirm)
                        , Color.WHITE, Color.GREEN, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {

                        //先清除雲端用戶Firebase UID
                        mChildReferenceForInputHistory.child(username).removeValue();
                        mChildReferenceForVocabularyList.child(username).removeValue();

                        // Get the current user
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        // Firebase sign out
                        mFirebaseAuth.signOut();

                        //Delete the user from Firebase
                        if (user != null) {

                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_deleted) + " " + getResources().getString(R.string.You_are_using_local_storage) + " " + getResources().getString(R.string.Relaunching_app), Toast.LENGTH_LONG).show();

                            //延遲5秒重啟App
                            Runnable r = new Runnable() {
                                @Override
                                public void run() {
                                    onBackPressed();  //要加入這句把多餘的登入畫面退出，才不會卡在登入畫面
                                    relaunchApp();
                                }
                            };
                            Handler h =new Handler();
                            h.postDelayed(r, 5000);

                            //刪除Firebase用戶
                            user.delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                                                            //這段編碼根本不會執行到所以用不到
                                                                                            //延遲5秒重啟App
                                                                                            //Runnable r = new Runnable() {
                                                                                            //    @Override
                                                                                            //    public void run() {
                                                                                            //
                                                                                            //        //清除本地用戶Firebase UID和暱稱
                                                                                            //        username = null;
                                                                                            //        userScreenName = null;
                                                                                            //
                                                                                            //        //儲存用戶Firebase UID和暱稱
                                                                                            //        usernameSharedPreferences = getSharedPreferences("usernameSharedPreferences", MODE_PRIVATE);
                                                                                            //        usernameSharedPreferences.edit().putString("userName", username).apply();
                                                                                            //
                                                                                            //        userScreenNameSharedPreferences = getSharedPreferences("userScreenNameSharedPreferences", MODE_PRIVATE);
                                                                                            //        userScreenNameSharedPreferences.edit().putString("userScreenName", userScreenName).apply();
                                                                                            //
                                                                                            //        //同時把用戶使用雲端存儲單字紀錄的設定(localOrCloudSaveSwitchPreferences=0)存入SharedPreferences
                                                                                            //        localOrCloudSaveSwitchPreferences = getSharedPreferences("localOrCloudSaveSwitchPreferences", MODE_PRIVATE);
                                                                                            //        localOrCloudSaveSwitchPreferences.edit().putString("CloudSaveMode", "0").apply();
                                                                                            //        relaunchApp();
                                                                                            //    }
                                                                                            //};
                                                                                            //Handler h =new Handler();
                                                                                            //h.postDelayed(r, 5000);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "用戶刪除失敗，用戶憑證過期，請手動登出並重新登入後，再立刻重試。", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }

                            dialog.dismiss();
                })


                //AlertDialog的取消鈕
                .addButton(getResources().getString(R.string.Cancel)
                        , Color.CYAN, Color.RED, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {

                        dialog.dismiss();
                });

                //把AlertDialog顯示出來
                doYouReallyWantToDeleteYourAccountAlertDialogBuilder.show();
            }
        });




    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.Login_successful) + " " + getResources().getString(R.string.You_are_using_cloud_storage) + " " + getResources().getString(R.string.Relaunching_app), Toast.LENGTH_LONG).show();

                //延遲5秒重啟App
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        //抓用戶Firebase UID和暱稱
                        username = mDetailTextView.getText().toString();
                        userScreenName = mScreenNameTextView.getText().toString();

                        //儲存用戶Firebase UID和暱稱
                        usernameSharedPreferences = getSharedPreferences("usernameSharedPreferences", MODE_PRIVATE);
                        usernameSharedPreferences.edit().putString("userName", username).apply();

                        userScreenNameSharedPreferences = getSharedPreferences("userScreenNameSharedPreferences", MODE_PRIVATE);
                        userScreenNameSharedPreferences.edit().putString("userScreenName", userScreenName).apply();

                        relaunchApp();
                    }
                };
                Handler h =new Handler();
                h.postDelayed(r, 5000);

            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, getString(R.string.status_log_in_cancelled), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Attach AuthStateListener when the app is in the foreground
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Remove AuthStateListener when the app is no longer in the foreground
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }




    //==========================================================================================
    // 重啟App的helper method
    //==========================================================================================
    public void relaunchApp() {
        Intent relaunchAppIntent = new Intent(getApplicationContext(), SignInActivity.class);
        ProcessPhoenix.triggerRebirth(getApplicationContext(), relaunchAppIntent);
        Runtime.getRuntime().exit(0);
    }


}