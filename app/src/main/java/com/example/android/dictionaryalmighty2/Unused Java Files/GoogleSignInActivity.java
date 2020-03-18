//package com.example.android.dictionaryalmighty2;
//
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInClient;
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.common.api.ApiException;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.android.material.snackbar.Snackbar;
//import com.google.firebase.auth.AuthCredential;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.EmailAuthProvider;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.auth.GoogleAuthProvider;
//import com.jakewharton.processphoenix.ProcessPhoenix;
//
//import static com.example.android.dictionaryalmighty2.MainActivity.googleIdToken;
//import static com.example.android.dictionaryalmighty2.MainActivity.googleIdTokenSharedPreferences;
//import static com.example.android.dictionaryalmighty2.MainActivity.localOrCloudSaveSwitchPreferences;
//import static com.example.android.dictionaryalmighty2.MainActivity.logInProviderCheckCode;
//import static com.example.android.dictionaryalmighty2.MainActivity.logInProviderCheckCodeSharedPreferences;
//import static com.example.android.dictionaryalmighty2.MainActivity.mChildReferenceForInputHistory;
//import static com.example.android.dictionaryalmighty2.MainActivity.mChildReferenceForVocabularyList;
//import static com.example.android.dictionaryalmighty2.MainActivity.userInputLoginEmail;
//import static com.example.android.dictionaryalmighty2.MainActivity.userInputLoginEmailSharedPreferences;
//import static com.example.android.dictionaryalmighty2.MainActivity.userInputLoginPassword;
//import static com.example.android.dictionaryalmighty2.MainActivity.userInputLoginPasswordSharedPreferences;
//import static com.example.android.dictionaryalmighty2.MainActivity.userScreenName;
//import static com.example.android.dictionaryalmighty2.MainActivity.userScreenNameSharedPreferences;
//import static com.example.android.dictionaryalmighty2.MainActivity.username;
//import static com.example.android.dictionaryalmighty2.MainActivity.usernameSharedPreferences;
//
///**
// * Demonstrate Firebase Authentication using a Google ID Token.
// */
//public class GoogleSignInActivity extends BaseActivity implements
//        View.OnClickListener {
//
//    private static final String TAG = "GoogleActivity";
//    private static final int RC_SIGN_IN = 9001;
//
//    // [START declare_auth]
//    private FirebaseAuth mAuth;
//    private FirebaseAuth.AuthStateListener mAuthListener;
//    // [END declare_auth]
//
//    private GoogleSignInClient mGoogleSignInClient;
//    private TextView mStatusTextView; //顯示用戶信箱
//    private TextView mDetailTextView; //顯示用戶UID
//    private TextView mScreenNameTextView; //顯示用戶暱稱
//    private TextView firebaseUidTextView; //UID標題
//
//    private EditText mEmail, mPassword;
//    private Button emailSignInButton, emailSignOutButton, emailUnregisterButton;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_google_sign_in);
//
//        // Views
//        mStatusTextView = findViewById(R.id.status);
//        mDetailTextView = findViewById(R.id.detail);
//        mScreenNameTextView = findViewById(R.id.screen_name_textView);
//        firebaseUidTextView = findViewById(R.id.firebase_UID_textView);
//
//        mEmail = (EditText) findViewById(R.id.email);
//        mPassword = (EditText) findViewById(R.id.password);
//        emailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
//                                                                //        emailSignOutButton = (Button) findViewById(R.id.email_sign_out_button);
//                                                                //        emailUnregisterButton = (Button) findViewById(R.id.email_unregister_button);
//        setProgressBar(R.id.progressBar);
//
//        // Button listeners
//        findViewById(R.id.signInButton).setOnClickListener(this);
//        findViewById(R.id.google_sign_out_button).setOnClickListener(this);
//        findViewById(R.id.google_unregister_button).setOnClickListener(this);
//                                                                //        findViewById(R.id.email_sign_out_button).setOnClickListener(this);
//                                                                //        findViewById(R.id.email_unregister_button).setOnClickListener(this);
//
//                                                                //        findViewById(R.id.disconnectButton).setOnClickListener(this);findViewById(R.id.disconnectButton).setVisibility(View.GONE); //這個按鈕用不到 先隱藏起來
//
//        // [START config_signin]
//        // Configure Google Sign In
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        // [END config_signin]
//
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//
//        // [START initialize_auth]
//        // Initialize Firebase Auth
//        mAuth = FirebaseAuth.getInstance();
//        // [END initialize_auth]
//
//
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    // User is signed in
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                                                                        //                    toastMessage("Successfully signed in with: " + user.getEmail());
//                    mStatusTextView.setText(getString(R.string.Google_status_fmt, user.getEmail()));
//                    mDetailTextView.setText(getString(R.string.Firebase_status_fmt, user.getUid()));
//
//                    //延遲5秒重啟App
//                    Runnable r = new Runnable() {
//                        @Override
//                        public void run() {
//                            //抓用戶Firebase UID和暱稱
//                            username = mDetailTextView.getText().toString();
//                            userScreenName = mScreenNameTextView.getText().toString();
//
//                            //儲存用戶Firebase UID和暱稱
//                            usernameSharedPreferences = getSharedPreferences("usernameSharedPreferences", MODE_PRIVATE);
//                            usernameSharedPreferences.edit().putString("userName", username).apply();
//
//                            userScreenNameSharedPreferences = getSharedPreferences("userScreenNameSharedPreferences", MODE_PRIVATE);
//                            userScreenNameSharedPreferences.edit().putString("userScreenName", userScreenName).apply();
//
//                            //同時把用戶使用雲端存儲單字紀錄的設定(localOrCloudSaveSwitchPreferences=1)存入SharedPreferences
//                            localOrCloudSaveSwitchPreferences = getSharedPreferences("localOrCloudSaveSwitchPreferences", MODE_PRIVATE);
//                            localOrCloudSaveSwitchPreferences.edit().putString("CloudSaveMode", "1").apply();
//
//                                                                        //                            relaunchApp();
//                        }
//                    };
//                    Handler h =new Handler();
//                    h.postDelayed(r, 5000);
//
//                }
//                                                                        //                else {
//                                                                        //                    // User is signed out
//                                                                        //                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                                                                        //                    toastMessage("Successfully signed out.");
//                                                                        //
//                                                                        //                    //延遲5秒重啟App
//                                                                        //                    Runnable r = new Runnable() {
//                                                                        //                        @Override
//                                                                        //                        public void run() {
//                                                                        //                            //清除本地用戶Firebase UID和暱稱
//                                                                        //                            username = null;
//                                                                        //                            userScreenName = null;
//                                                                        //
//                                                                        //                            //儲存用戶Firebase UID和暱稱
//                                                                        //                            usernameSharedPreferences = getSharedPreferences("usernameSharedPreferences", MODE_PRIVATE);
//                                                                        //                            usernameSharedPreferences.edit().putString("userName", username).apply();
//                                                                        //
//                                                                        //                            userScreenNameSharedPreferences = getSharedPreferences("userScreenNameSharedPreferences", MODE_PRIVATE);
//                                                                        //                            userScreenNameSharedPreferences.edit().putString("userScreenName", userScreenName).apply();
//                                                                        //
//                                                                        //                            //同時把用戶使用雲端存儲單字紀錄的設定(localOrCloudSaveSwitchPreferences=0)存入SharedPreferences
//                                                                        //                            localOrCloudSaveSwitchPreferences = getSharedPreferences("localOrCloudSaveSwitchPreferences", MODE_PRIVATE);
//                                                                        //                            localOrCloudSaveSwitchPreferences.edit().putString("CloudSaveMode", "0").apply();
//                                                                        //                            relaunchApp();
//                                                                        //                        }
//                                                                        //                    };
//                                                                        //                    Handler h =new Handler();
//                                                                        //                    h.postDelayed(r, 5000);
//                                                                        //
//                                                                        //                }
//                                                                                        // ...
//            }
//        };
//
//
//        emailSignInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String email = mEmail.getText().toString();
//                String password = mPassword.getText().toString();
//
//                userInputLoginEmailSharedPreferences = getSharedPreferences("userInputLoginEmailSharedPreferences", MODE_PRIVATE);
//                userInputLoginEmailSharedPreferences.edit().putString("userInputLoginEmail", email).apply();
//
//                userInputLoginPasswordSharedPreferences = getSharedPreferences("userInputLoginPasswordSharedPreferences", MODE_PRIVATE);
//                userInputLoginPasswordSharedPreferences.edit().putString("userInputLoginPassword", password).apply();
//
//                if(!email.equals("") && !password.equals("")){
//                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (!task.isSuccessful()){
//                                Log.d("onComplete", "登入失敗");
//                                register(email, password);
//                                findViewById(R.id.google_loggedIn_textView).setVisibility(View.GONE);
//                                findViewById(R.id.emailPasswordLoggedIn_textView).setVisibility(View.GONE);
//                            }
//                            else {
//                                findViewById(R.id.google_unregister_button).setVisibility(View.GONE);
//                                findViewById(R.id.google_loggedIn_textView).setVisibility(View.GONE);
//                                findViewById(R.id.emailPasswordLoggedIn_textView).setVisibility(View.VISIBLE);
//
//                                logInProviderCheckCodeSharedPreferences = getSharedPreferences("logInProviderCheckCodeSharedPreferences", MODE_PRIVATE);
//                                logInProviderCheckCodeSharedPreferences.edit().putString("logInProviderCheckCode", "Signed in with email and password input").apply();
//
//                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.Login_successful) + " " + getResources().getString(R.string.You_are_using_cloud_storage) + " " + getResources().getString(R.string.Relaunching_app), Toast.LENGTH_LONG).show();
//
//                                //延遲5秒重啟App
//                                Runnable r = new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        relaunchApp();
//                                    }
//                                };
//                                Handler h =new Handler();
//                                h.postDelayed(r, 5000);
//                            }
//                        }
//                    });
//                }else{
//                    Toast.makeText(getApplicationContext(),getString(R.string.You_have_not_entered_anything),Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//
//                                                                        //        emailSignOutButton.setOnClickListener(new View.OnClickListener() {
//                                                                        //            @Override
//                                                                        //            public void onClick(View view) {
//                                                                        //                mAuth.signOut();
//                                                                        //                Toast.makeText(getApplicationContext(),getString(R.string.Sign_out),Toast.LENGTH_LONG).show();
//                                                                        //            }
//                                                                        //        });
//
//
//
//
//    }
//
//
//
//
//    // [START on_start_check_user]
//    @Override
//    public void onStart() {
//        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
//    }
//    // [END on_start_check_user]
//
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
//    }
//
//
//    // [START onActivityResult]
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                firebaseAuthWithGoogle(account);
//
//                Toast.makeText(getApplicationContext(), getResources().getString(R.string.Login_successful) + " " + getResources().getString(R.string.You_are_using_cloud_storage) + " " + getResources().getString(R.string.Relaunching_app), Toast.LENGTH_LONG).show();
//
//                //延遲5秒重啟App
//                Runnable r = new Runnable() {
//                    @Override
//                    public void run() {
//                        //抓用戶Firebase UID和暱稱
//                        username = mDetailTextView.getText().toString();
//                        userScreenName = mScreenNameTextView.getText().toString();
//
//                        //儲存用戶Firebase UID和暱稱
//                        usernameSharedPreferences = getSharedPreferences("usernameSharedPreferences", MODE_PRIVATE);
//                        usernameSharedPreferences.edit().putString("userName", username).apply();
//
//                        userScreenNameSharedPreferences = getSharedPreferences("userScreenNameSharedPreferences", MODE_PRIVATE);
//                        userScreenNameSharedPreferences.edit().putString("userScreenName", userScreenName).apply();
//
//                        //同時把用戶使用雲端存儲單字紀錄的設定(localOrCloudSaveSwitchPreferences=1)存入SharedPreferences
//                        localOrCloudSaveSwitchPreferences = getSharedPreferences("localOrCloudSaveSwitchPreferences", MODE_PRIVATE);
//                        localOrCloudSaveSwitchPreferences.edit().putString("CloudSaveMode", "1").apply();
//
//                        relaunchApp();
//                    }
//                };
//                Handler h =new Handler();
//                h.postDelayed(r, 5000);
//
//            } catch (ApiException e) {
//                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e);
//                // [START_EXCLUDE]
//                updateUI(null);
//                // [END_EXCLUDE]
//            }
//        }
//    }
//    // [END onActivityResult]
//
//    // [START auth_with_google]
//    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
//        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
//        // [START_EXCLUDE silent]
//        showProgressBar();
//        // [END_EXCLUDE]
//
//        googleIdToken = acct.getIdToken();
//        googleIdTokenSharedPreferences = getSharedPreferences("googleIdTokenSharedPreferences", MODE_PRIVATE);
//        googleIdTokenSharedPreferences.edit().putString("googleIdToken", googleIdToken).apply();
//
//        AuthCredential credential = GoogleAuthProvider.getCredential(googleIdToken, null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in successfull, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//
//                            findViewById(R.id.google_loggedIn_textView).setVisibility(View.VISIBLE);
//                            findViewById(R.id.emailPasswordLoggedIn_textView).setVisibility(View.GONE);
//
//                            logInProviderCheckCodeSharedPreferences = getSharedPreferences("logInProviderCheckCodeSharedPreferences", MODE_PRIVATE);
//                            logInProviderCheckCodeSharedPreferences.edit().putString("logInProviderCheckCode", "Signed in with Google").apply();
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Snackbar.make(findViewById(R.id.google_sign_in_activity_layout), R.string.status_verification_failed, Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
//                            findViewById(R.id.google_loggedIn_textView).setVisibility(View.GONE);
//                            findViewById(R.id.emailPasswordLoggedIn_textView).setVisibility(View.GONE);
//                        }
//
//                        // [START_EXCLUDE]
//                        hideProgressBar();
//                        // [END_EXCLUDE]
//                    }
//                });
//    }
//    // [END auth_with_google]
//
//
//    // [START signin]
//    private void signIn() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }
//    // [END signin]
//
//    private void signOut() {
//        // Firebase sign out
//        mAuth.signOut();
//
//        // Google sign out
//        mGoogleSignInClient.signOut().addOnCompleteListener(this,
//                new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        updateUI(null);
//
//                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.You_are_not_signed_in) + " " + getResources().getString(R.string.You_are_using_local_storage) + " " + getResources().getString(R.string.Relaunching_app), Toast.LENGTH_LONG).show();
//
//                        //延遲5秒重啟App
//                        Runnable r = new Runnable() {
//                            @Override
//                            public void run() {
//                                //清除本地用戶Firebase UID和暱稱
//                                username = null;
//                                userScreenName = null;
//
//                                //儲存用戶Firebase UID和暱稱
//                                usernameSharedPreferences = getSharedPreferences("usernameSharedPreferences", MODE_PRIVATE);
//                                usernameSharedPreferences.edit().putString("userName", username).apply();
//
//                                userScreenNameSharedPreferences = getSharedPreferences("userScreenNameSharedPreferences", MODE_PRIVATE);
//                                userScreenNameSharedPreferences.edit().putString("userScreenName", userScreenName).apply();
//
//                                //同時把用戶使用雲端存儲單字紀錄的設定(localOrCloudSaveSwitchPreferences=0)存入SharedPreferences
//                                localOrCloudSaveSwitchPreferences = getSharedPreferences("localOrCloudSaveSwitchPreferences", MODE_PRIVATE);
//                                localOrCloudSaveSwitchPreferences.edit().putString("CloudSaveMode", "0").apply();
//                                relaunchApp();
//                            }
//                        };
//                        Handler h =new Handler();
//                        h.postDelayed(r, 5000);
//                    }
//                });
//    }
//
//    private void signOutAndDeleteUser() {
//
//        //先清除雲端用戶Firebase UID
//        mChildReferenceForInputHistory.child(username).removeValue();
//        mChildReferenceForVocabularyList.child(username).removeValue();
//
//        // Firebase sign out
//        mAuth.signOut();
//
//        // Google sign out
//        mGoogleSignInClient.signOut().addOnCompleteListener(this,
//                new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        updateUI(null);
//
//                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_deleted) + " " + getResources().getString(R.string.You_are_using_local_storage) + " " + getResources().getString(R.string.Relaunching_app), Toast.LENGTH_LONG).show();
//
//                        //延遲5秒重啟App
//                        Runnable r = new Runnable() {
//                            @Override
//                            public void run() {
//
//                                //清除本地用戶Firebase UID和暱稱
//                                username = null;
//                                userScreenName = null;
//
//                                //儲存用戶Firebase UID和暱稱
//                                usernameSharedPreferences = getSharedPreferences("usernameSharedPreferences", MODE_PRIVATE);
//                                usernameSharedPreferences.edit().putString("userName", username).apply();
//
//                                userScreenNameSharedPreferences = getSharedPreferences("userScreenNameSharedPreferences", MODE_PRIVATE);
//                                userScreenNameSharedPreferences.edit().putString("userScreenName", userScreenName).apply();
//
//                                //同時把用戶使用雲端存儲單字紀錄的設定(localOrCloudSaveSwitchPreferences=0)存入SharedPreferences
//                                localOrCloudSaveSwitchPreferences = getSharedPreferences("localOrCloudSaveSwitchPreferences", MODE_PRIVATE);
//                                localOrCloudSaveSwitchPreferences.edit().putString("CloudSaveMode", "0").apply();
//                                relaunchApp();
//                            }
//                        };
//                        Handler h =new Handler();
//                        h.postDelayed(r, 5000);
//                    }
//                });
//    }
//
//
//    private void register(final String email, final String password) {
//        new AlertDialog.Builder(GoogleSignInActivity.this)
//                .setTitle(getResources().getString(R.string.Login_problem))
//                .setMessage(getResources().getString(R.string.Account_not_found))
//                .setPositiveButton(R.string.Register,
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                createUser(email, password);
//                            }
//                        })
//                .setNeutralButton(R.string.Cancel, null)
//                .show();
//    }
//
//
//    private void createUser(String email, String password) {
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(
//                        new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                String message =
//                                        task.isSuccessful() ? getResources().getString(R.string.Registration_completed) : getResources().getString(R.string.Registration_failed);
//                                new AlertDialog.Builder(GoogleSignInActivity.this)
//                                        .setMessage(message)
//                                        .setPositiveButton("OK", null)
//                                        .show();
//
//                                emailSignInButton.performClick();
//                            }
//                        });
//    }
//
//
//    private void revokeAccess() {
//        // Firebase sign out
//        mAuth.signOut();
//
//        // Google revoke access
//        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
//                new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        updateUI(null);
//                    }
//                });
//    }
//
//    private void updateUI(FirebaseUser user) {
//        hideProgressBar();
//        if (user != null) {
//            mStatusTextView.setText(getString(R.string.Google_status_fmt, user.getEmail()));
//            mDetailTextView.setText(getString(R.string.Firebase_status_fmt, user.getUid()));
//            mScreenNameTextView.setText(String.format("%s%s", getResources().getString(R.string.Username), user.getDisplayName()));
//
//            firebaseUidTextView.setVisibility(View.VISIBLE);
//
//            findViewById(R.id.signInButton).setVisibility(View.GONE);
//            findViewById(R.id.google_sign_out_button).setVisibility(View.VISIBLE);
//            findViewById(R.id.google_unregister_button).setVisibility(View.VISIBLE);
//
//            try {
//                if (logInProviderCheckCode.equals("Signed in with Google")) {
//                    findViewById(R.id.google_loggedIn_textView).setVisibility(View.VISIBLE);
//                    findViewById(R.id.emailPasswordLoggedIn_textView).setVisibility(View.GONE);
//                    mScreenNameTextView.setVisibility(View.VISIBLE);
//                } else if (logInProviderCheckCode.equals("Signed in with email and password input")){
//                    findViewById(R.id.google_loggedIn_textView).setVisibility(View.GONE);
//                    findViewById(R.id.emailPasswordLoggedIn_textView).setVisibility(View.VISIBLE);
//                    mScreenNameTextView.setVisibility(View.INVISIBLE);
//                }
//            } catch (Exception e) {
//                if (logInProviderCheckCode==null) {
//                    findViewById(R.id.google_loggedIn_textView).setVisibility(View.GONE);
//                    findViewById(R.id.emailPasswordLoggedIn_textView).setVisibility(View.GONE);
//                }
//            }
//
//            findViewById(R.id.email_sign_in_button).setVisibility(View.GONE);
//                                                                        //            findViewById(R.id.email_sign_out_button).setVisibility(View.VISIBLE);
//                                                                        //            findViewById(R.id.email_unregister_button).setVisibility(View.VISIBLE);
//                                                                        //            findViewById(R.id.signOutAndDisconnect).setVisibility(View.VISIBLE);
//        } else {
//            mStatusTextView.setText(R.string.You_are_not_signed_in);
//            mDetailTextView.setText(null);
//            mScreenNameTextView.setText(null);
//
//            firebaseUidTextView.setVisibility(View.GONE);
//
//            findViewById(R.id.signInButton).setVisibility(View.VISIBLE);
//            findViewById(R.id.google_sign_out_button).setVisibility(View.INVISIBLE);
//            findViewById(R.id.google_unregister_button).setVisibility(View.INVISIBLE);
//
//            findViewById(R.id.google_loggedIn_textView).setVisibility(View.GONE);
//            findViewById(R.id.emailPasswordLoggedIn_textView).setVisibility(View.GONE);
//
//            findViewById(R.id.email_sign_in_button).setVisibility(View.VISIBLE);
////            findViewById(R.id.email_sign_out_button).setVisibility(View.INVISIBLE);
////            findViewById(R.id.email_unregister_button).setVisibility(View.INVISIBLE);
//
//                                            //findViewById(R.id.signOutAndDisconnect).setVisibility(View.GONE); 這個暫時用不到
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        int i = v.getId();
//        if (i == R.id.signInButton) {
//            signIn();
//        } else if (i == R.id.google_sign_out_button) {
//            signOut();
//        } else if (i == R.id.google_unregister_button) {
//            if (findViewById(R.id.google_loggedIn_textView).isShown()) {
//                googleReauthenticate();
//            } else if (findViewById(R.id.emailPasswordLoggedIn_textView).isShown()) {
//                emailPasswordReauthenticate();
//            }
//            signOutAndDeleteUser();
//        }
//                                                                        //        else if (i == R.id.email_sign_out_button) {
//                                                                        //            signOut();
//                                                                        //        } else if (i == R.id.email_unregister_button) {
//                                                                        //            emailPasswordReauthenticate();
//                                                                        //            signOutAndDeleteUser();
//                                                                        //        }
//                                                                        //        else if (i == R.id.disconnectButton) {
//                                                                        //            revokeAccess();
//                                                                        //        }
//    }
//
//
//    //==========================================================================================
//    // 重啟App的helper method
//    //==========================================================================================
//    public void relaunchApp() {
//        Intent relaunchAppIntent = new Intent(getApplicationContext(), GoogleSignInActivity.class);
//        ProcessPhoenix.triggerRebirth(getApplicationContext(), relaunchAppIntent);
//        Runtime.getRuntime().exit(0);
//    }
//
//
//    //==========================================================================================
//    // Prompt the user to re-provide their sign-in credentials
//    //==========================================================================================
//    public void googleReauthenticate() {
//        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        // Get auth credentials from the user for re-authentication.
//        AuthCredential credential = GoogleAuthProvider.getCredential(googleIdToken,null);
//
//        // Prompt the user to re-provide their sign-in credentials
//        if (user != null) {
//            user.reauthenticate(credential)
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            user.delete()
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isSuccessful()) {
//                                                Log.d(TAG, "User account deleted.");
//                                            }
//                                        }
//                                    });
//                        }
//                    });
//        }
//    }
//
//
//    public void emailPasswordReauthenticate() {
//        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        // Get auth credentials from the user for re-authentication.
//        AuthCredential credential = EmailAuthProvider
//                .getCredential(userInputLoginEmail, userInputLoginPassword);
//
//        // Prompt the user to re-provide their sign-in credentials
//        if (user != null) {
//            user.reauthenticate(credential)
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            user.delete()
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isSuccessful()) {
//                                                Log.d(TAG, "User account deleted.");
//                                            }
//                                        }
//                                    });
//                        }
//                    });
//        }
//    }
//
//
//    //==========================================================================================
//    // Helper method for Toast messages
//    //==========================================================================================
//    private void toastMessage(String message){
//        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
//    }
//
//
//}