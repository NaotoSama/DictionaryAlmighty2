package com.example.android.dictionaryalmighty2;

import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import static com.example.android.dictionaryalmighty2.MainActivity.localOrCloudSaveSwitchPreferences;
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

                    //同時把用戶使用雲端存儲單字紀錄的設定(localOrCloudSaveSwitchPreferences=1)存入SharedPreferences
                    localOrCloudSaveSwitchPreferences = getSharedPreferences("localOrCloudSaveSwitchPreferences", MODE_PRIVATE);
                    localOrCloudSaveSwitchPreferences.edit().putString("CloudSaveMode", "1").apply();


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

                    //同時把用戶使用雲端存儲單字紀錄的設定(localOrCloudSaveSwitchPreferences=0)存入SharedPreferences
                    localOrCloudSaveSwitchPreferences = getSharedPreferences("localOrCloudSaveSwitchPreferences", MODE_PRIVATE);
                    localOrCloudSaveSwitchPreferences.edit().putString("CloudSaveMode", "0").apply();


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
                AlertDialog.Builder signOutAlertDialog = new AlertDialog.Builder(SignInActivity.this);
                signOutAlertDialog.setTitle(getResources().getString(R.string.Do_you_really_want_to_log_out));
                signOutAlertDialog.setCancelable(false); //按到旁邊的空白處AlertDialog也不會消失
                signOutAlertDialog.setView(R.layout.custom_alert_dialog_dictionary_providers); //沿用字典選單的佈局檔

                //AlertDialog的確定鈕，登出用戶
                signOutAlertDialog.setPositiveButton(R.string.Confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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

                                //同時把用戶使用雲端存儲單字紀錄的設定(localOrCloudSaveSwitchPreferences=0)存入SharedPreferences
                                localOrCloudSaveSwitchPreferences = getSharedPreferences("localOrCloudSaveSwitchPreferences", MODE_PRIVATE);
                                localOrCloudSaveSwitchPreferences.edit().putString("CloudSaveMode", "0").apply();

                                onBackPressed();   //要加入這句把多餘的登入畫面退出，才不會卡在登入畫面
                                relaunchApp();
                            }
                        };
                        Handler h =new Handler();
                        h.postDelayed(r, 5000);
                    }
                });

                //AlertDialog的取消鈕，取消登出
                signOutAlertDialog.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                //把AlertDialog顯示出來
                signOutAlertDialog.create().show();

            }
        });


        /**
         * 用戶資訊更新鈕
         */
        updateUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //這邊設置第一層AlertDialog讓用戶更改帳戶資訊
                AlertDialog.Builder updateUserAccountAlertDialog = new AlertDialog.Builder(SignInActivity.this);
                updateUserAccountAlertDialog.setTitle(getResources().getString(R.string.Change_user_info));
                updateUserAccountAlertDialog.setCancelable(true); //按到旁邊的空白處AlertDialog也不會消失
                updateUserAccountAlertDialog.setView(R.layout.custom_alert_dialog_dictionary_providers); //沿用字典選單的佈局檔


                //第一層AlertDialog的中立鈕，更改顯示暱稱
                updateUserAccountAlertDialog.setNeutralButton(getResources().getString(R.string.Change_user_screen_name), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder updateDisplayNameDialog = new AlertDialog.Builder(SignInActivity.this);
                        updateDisplayNameDialog.setTitle(getResources().getString(R.string.Type_your_new_screen_name));
                        updateDisplayNameDialog.setCancelable(false); //按到旁邊的空白處AlertDialog不會消失
                        final EditText userInputView = new EditText(getApplicationContext()); //在對話框內創建文字輸入框
                        updateDisplayNameDialog.setView(userInputView);


                        //第二層AlertDialog的確定鈕
                        updateDisplayNameDialog.setPositiveButton(getString(R.string.Confirm), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
                            }
                        });


                        //第二層AlertDialog的取消鈕
                        updateDisplayNameDialog.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });


                        //把第二層AlertDialog顯示出來
                        updateDisplayNameDialog.create().show();
                    }
                });


                //第一層AlertDialog的確定鈕，更改密碼
                updateUserAccountAlertDialog.setPositiveButton(getResources().getString(R.string.Change_password), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder updatePasswordDialog = new AlertDialog.Builder(SignInActivity.this);
                        updatePasswordDialog.setTitle(getResources().getString(R.string.Type_your_new_password) + getResources().getString(R.string.Please_log_in_and_out_again));
                        updatePasswordDialog.setCancelable(false); //按到旁邊的空白處AlertDialog不會消失
                        final EditText userInputView = new EditText(getApplicationContext()); //在對話框內創建文字輸入框
                        updatePasswordDialog.setView(userInputView);


                        //第二層AlertDialog的確定鈕
                        updatePasswordDialog.setPositiveButton(getString(R.string.Confirm), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
                            }
                        });


                        //第二層AlertDialog的取消鈕
                        updatePasswordDialog.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });


                        //把第二層AlertDialog顯示出來
                        updatePasswordDialog.create().show();
                    }
                });


                //第一層AlertDialog的取消鈕，更改信箱
                updateUserAccountAlertDialog.setNegativeButton(getResources().getString(R.string.Change_email), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder updateEmailDialog = new AlertDialog.Builder(SignInActivity.this);
                        updateEmailDialog.setTitle(getResources().getString(R.string.Type_your_new_email) + getResources().getString(R.string.Please_log_in_and_out_again));
                        updateEmailDialog.setCancelable(false); //按到旁邊的空白處AlertDialog不會消失
                        final EditText userInputView = new EditText(getApplicationContext()); //在對話框內創建文字輸入框
                        updateEmailDialog.setView(userInputView);


                        //第二層AlertDialog的確定鈕
                        updateEmailDialog.setPositiveButton(getString(R.string.Confirm), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
                            }
                        });


                        //第二層AlertDialog的取消鈕
                        updateEmailDialog.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });


                        //把第二層AlertDialog顯示出來
                        updateEmailDialog.create().show();
                    }
                });


                //把第一層AlertDialog顯示出來
                updateUserAccountAlertDialog.create().show();
            }
        });


        /**
         * 用戶註銷鈕
         */
        unregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //這邊設置AlertDialog讓用戶確認是否真要刪除帳號
                AlertDialog.Builder doYouReallyWantToDeleteYourAccountAlertDialog = new AlertDialog.Builder(SignInActivity.this);
                doYouReallyWantToDeleteYourAccountAlertDialog.setTitle(getString(R.string.Do_you_really_want_to_delete_your_account));
                doYouReallyWantToDeleteYourAccountAlertDialog.setCancelable(false); //按到旁邊的空白處AlertDialog也不會消失
                doYouReallyWantToDeleteYourAccountAlertDialog.setView(R.layout.custom_alert_dialog_dictionary_providers); //沿用字典選單的佈局檔

                //AlertDialog的確定鈕，刪除帳號
                doYouReallyWantToDeleteYourAccountAlertDialog.setPositiveButton(R.string.Confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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

                    }
                });


                //AlertDialog的取消鈕
                doYouReallyWantToDeleteYourAccountAlertDialog.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                //把AlertDialog顯示出來
                doYouReallyWantToDeleteYourAccountAlertDialog.create().show();
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

                        //同時把用戶使用雲端存儲單字紀錄的設定(localOrCloudSaveSwitchPreferences=1)存入SharedPreferences
                        localOrCloudSaveSwitchPreferences = getSharedPreferences("localOrCloudSaveSwitchPreferences", MODE_PRIVATE);
                        localOrCloudSaveSwitchPreferences.edit().putString("CloudSaveMode", "1").apply();

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


    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }
}