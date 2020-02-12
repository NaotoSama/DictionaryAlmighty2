//編寫未完成  暫停

//package com.example.android.dictionaryalmighty2;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.facebook.AccessToken;
//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.login.LoginManager;
//import com.facebook.login.LoginResult;
//import com.facebook.login.widget.LoginButton;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthCredential;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FacebookAuthProvider;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//import java.util.Arrays;
//
//public class FacebookSignInActivity extends AppCompatActivity {
//
//    private LoginButton loginButton;
//    private CallbackManager callbackManager;
//
//    private FirebaseAuth firebaseAuth;
//    private FirebaseAuth.AuthStateListener firebaseAuthListener;
//
//    private ProgressBar progressBar;
//
//    private TextView mStatusTextView;
//    private TextView mDetailTextView;
//    private TextView mScreenNameTextView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_facebook_sign_in);
//
//
//        progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        loginButton = (LoginButton) findViewById(R.id.loginButton);
//        mStatusTextView = findViewById(R.id.status);
//        mDetailTextView = findViewById(R.id.detail);
//        mScreenNameTextView = findViewById(R.id.screen_name_textView);
//
//        callbackManager = CallbackManager.Factory.create();
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//
//
//        if (user != null) {
//            mStatusTextView.setText(user.getEmail());
//            mDetailTextView.setText(user.getUid());
//            mScreenNameTextView.setText(user.getDisplayName());
//        } else {
//            goLoginScreen();
//        }
//
//
//
//        loginButton.setReadPermissions(Arrays.asList("email"));
//
//        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                handleFacebookAccessToken(loginResult.getAccessToken());
//            }
//
//            @Override
//            public void onCancel() {
//                Toast.makeText(getApplicationContext(), R.string.status_log_in_cancelled, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Toast.makeText(getApplicationContext(), R.string.status_sign_in_failed, Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        firebaseAuth = FirebaseAuth.getInstance();
//        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    goMainScreen();
//                }
//            }
//        };
//
//
//
//    }
//
//
//
//    private void goLoginScreen() {
//        Intent intent = new Intent(this, FacebookSignInActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//    }
//
//    public void logout(View view) {
//        FirebaseAuth.getInstance().signOut();
//        LoginManager.getInstance().logOut();
//        goLoginScreen();
//    }
//
//    private void handleFacebookAccessToken(AccessToken accessToken) {
//        progressBar.setVisibility(View.VISIBLE);
//        loginButton.setVisibility(View.GONE);
//
//        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
//        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (!task.isSuccessful()) {
//                    Toast.makeText(getApplicationContext(), R.string.status_sign_in_failed, Toast.LENGTH_LONG).show();
//                }
//                progressBar.setVisibility(View.GONE);
//                loginButton.setVisibility(View.VISIBLE);
//            }
//        });
//    }
//
//    private void goMainScreen() {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        firebaseAuth.addAuthStateListener(firebaseAuthListener);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        firebaseAuth.removeAuthStateListener(firebaseAuthListener);
//    }
//
//
//
//}
