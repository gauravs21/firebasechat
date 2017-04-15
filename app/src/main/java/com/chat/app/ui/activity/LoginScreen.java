package com.chat.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.chat.app.R;
import com.chat.app.model.User;
import com.chat.app.utility.PrefsUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginScreen extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final int RC_SIGN_IN = 1;
    GoogleApiClient mGoogleApiClient;
    FirebaseAuth firebaseAuth;
    DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    User user;
    boolean isPresent=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase.child("email");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        SignInButton signInButton = (SignInButton) findViewById(R.id.googleSignIn);
        signInButton.setColorScheme(SignInButton.COLOR_DARK);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton.setOnClickListener(this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "failed to connect", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.googleSignIn:
                googleSignIn();
                break;
        }
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (signInResult.isSuccess()) {
                GoogleSignInAccount signInAccount = signInResult.getSignInAccount();
                if (signInAccount != null) {
                    firebaseWithGoogle(signInAccount);
                }
            }
        }
    }

    private void firebaseWithGoogle(GoogleSignInAccount googleSignInAccount) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(LoginScreen.this, "failed to sign in", Toast.LENGTH_SHORT).show();
                } else {

                    final String email = String.valueOf((task.getResult().getUser().getEmail()));
                    PrefsUtil.saveUserEmail(LoginScreen.this, email);
                    PrefsUtil.saveUserId(LoginScreen.this, task.getResult().getUser().getUid());

                    user = new User(PrefsUtil.getEmail(LoginScreen.this),
                            PrefsUtil.getFcm(LoginScreen.this),
                            PrefsUtil.getUserId(LoginScreen.this));

                    final DatabaseReference newRef = firebaseDatabase.child("user");

                    newRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            DatabaseReference userRef=newRef.push();
                            if (dataSnapshot.getValue() != null) {
                                for (DataSnapshot data :
                                        dataSnapshot.getChildren()) {
                                    if (data.child("email").getValue().equals(user.getEmail())) {
                                        Log.e("DBSS", "value exists");
                                        isPresent=true;
                                        break;
                                    } else {
                                        Log.e("DB", "in else");
                                        isPresent=false;
//                            userRef.setValue(user);
                                    }
                                }
                                if (!isPresent)
                                    userRef.setValue(user);
                            }
                            else {
                                Log.e("DB", "in outer else");

                                userRef.setValue(user);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    Log.e("DB", String.valueOf((task.getResult().getUser().getEmail())));


                    Intent intent = new Intent(LoginScreen.this, HomeActivity.class);
                    startActivity(intent);

                }
            }
        });
    }
}
