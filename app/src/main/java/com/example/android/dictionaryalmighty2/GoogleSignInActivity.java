package com.example.android.dictionaryalmighty2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.jakewharton.processphoenix.ProcessPhoenix;

import static com.example.android.dictionaryalmighty2.MainActivity.localOrCloudSaveSwitchPreferences;
import static com.example.android.dictionaryalmighty2.MainActivity.mChildReferenceForInputHistory;
import static com.example.android.dictionaryalmighty2.MainActivity.mChildReferenceForVocabularyList;
import static com.example.android.dictionaryalmighty2.MainActivity.userScreenName;
import static com.example.android.dictionaryalmighty2.MainActivity.userScreenNameSharedPreferences;
import static com.example.android.dictionaryalmighty2.MainActivity.username;
import static com.example.android.dictionaryalmighty2.MainActivity.usernameSharedPreferences;

/**
 * Demonstrate Firebase Authentication using a Google ID Token.
 */
public class GoogleSignInActivity extends BaseActivity implements
        View.OnClickListener {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private GoogleSignInClient mGoogleSignInClient;
    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private TextView mScreenNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);

        // Views
        mStatusTextView = findViewById(R.id.status);
        mDetailTextView = findViewById(R.id.detail);
        mScreenNameTextView = findViewById(R.id.screen_name_textView);
        setProgressBar(R.id.progressBar);

        // Button listeners
        findViewById(R.id.signInButton).setOnClickListener(this);
        findViewById(R.id.signOutButton).setOnClickListener(this);
        findViewById(R.id.unregisterButton).setOnClickListener(this);
        findViewById(R.id.disconnectButton).setOnClickListener(this);findViewById(R.id.disconnectButton).setVisibility(View.GONE); //這個按鈕用不到 先隱藏起來

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

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
                        localOrCloudSaveSwitchPreferences.edit().putInt("CloudSaveMode", 1).apply();

                        relaunchApp();
                    }
                };
                Handler h =new Handler();
                h.postDelayed(r, 5000);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressBar();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressBar();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);

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

                                //同時把用戶使用雲端存儲單字紀錄的設定(localOrCloudSaveSwitchPreferences=1)存入SharedPreferences
                                localOrCloudSaveSwitchPreferences = getSharedPreferences("localOrCloudSaveSwitchPreferences", MODE_PRIVATE);
                                localOrCloudSaveSwitchPreferences.edit().putInt("CloudSaveMode", 0).apply();
                                relaunchApp();
                            }
                        };
                        Handler h =new Handler();
                        h.postDelayed(r, 5000);
                    }
                });
    }

    private void signOutAndDeleteUser() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);

                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_deleted) + " " + getResources().getString(R.string.You_are_using_local_storage) + " " + getResources().getString(R.string.Relaunching_app), Toast.LENGTH_LONG).show();

                        //延遲5秒重啟App
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                //清除雲端用戶Firebase UID
                                mChildReferenceForInputHistory.child(username).removeValue();
                                mChildReferenceForVocabularyList.child(username).removeValue();

                                //清除本地用戶Firebase UID和暱稱
                                username = null;
                                userScreenName = null;

                                //儲存用戶Firebase UID和暱稱
                                usernameSharedPreferences = getSharedPreferences("usernameSharedPreferences", MODE_PRIVATE);
                                usernameSharedPreferences.edit().putString("userName", username).apply();

                                userScreenNameSharedPreferences = getSharedPreferences("userScreenNameSharedPreferences", MODE_PRIVATE);
                                userScreenNameSharedPreferences.edit().putString("userScreenName", userScreenName).apply();

                                //同時把用戶使用雲端存儲單字紀錄的設定(localOrCloudSaveSwitchPreferences=1)存入SharedPreferences
                                localOrCloudSaveSwitchPreferences = getSharedPreferences("localOrCloudSaveSwitchPreferences", MODE_PRIVATE);
                                localOrCloudSaveSwitchPreferences.edit().putInt("CloudSaveMode", 0).apply();
                                relaunchApp();
                            }
                        };
                        Handler h =new Handler();
                        h.postDelayed(r, 5000);
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        hideProgressBar();
        if (user != null) {
            mStatusTextView.setText(getString(R.string.Google_status_fmt, user.getEmail()));
            mDetailTextView.setText(getString(R.string.Firebase_status_fmt, user.getUid()));
            mScreenNameTextView.setText(String.format("%s:%s", getResources().getString(R.string.Username), user.getDisplayName()));

            findViewById(R.id.signInButton).setVisibility(View.GONE);
            findViewById(R.id.signOutAndDisconnect).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.You_are_not_signed_in);
            mDetailTextView.setText(null);
            mScreenNameTextView.setText(null);

            findViewById(R.id.signInButton).setVisibility(View.VISIBLE);
                                            //findViewById(R.id.signOutAndDisconnect).setVisibility(View.GONE); 這個暫時用不到
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signInButton) {
            signIn();
        } else if (i == R.id.signOutButton) {
            signOut();
        } else if (i == R.id.unregisterButton) {
            signOutAndDeleteUser();
        } else if (i == R.id.disconnectButton) {
            revokeAccess();
        }
    }


    //==========================================================================================
    // 重啟App的helper methods
    //==========================================================================================
    public void relaunchApp() {
        Intent relaunchAppIntent = new Intent(getApplicationContext(), MainActivity.class);
        ProcessPhoenix.triggerRebirth(getApplicationContext(), relaunchAppIntent);
        Runtime.getRuntime().exit(0);
    }


}