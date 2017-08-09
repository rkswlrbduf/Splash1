package samsung.membership.splash;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;
    private SessionCallback callback;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private ImageView fakeKakao;
    private ImageView fakeFacebook;
    private ImageView fakeGoogle;

    private com.kakao.usermgmt.LoginButton kakaoLogin;
    private LoginButton facebookLogin;
    private SignInButton googleLogin;


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fake_kakao:
                kakaoLogin.performClick();
                break;
            case R.id.fake_facebook:
                facebookLogin.performClick();
                break;
            case R.id.fake_google:
                signIn();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setTheme(R.style.NoAction);
        setContentView(R.layout.activity_login);

        fakeKakao = (ImageView)findViewById(R.id.fake_kakao);
        fakeFacebook = (ImageView)findViewById(R.id.fake_facebook);
        fakeGoogle = (ImageView)findViewById(R.id.fake_google);

        fakeKakao.setOnClickListener(this);
        fakeFacebook.setOnClickListener(this);
        fakeGoogle.setOnClickListener(this);

        kakaoLogin = (com.kakao.usermgmt.LoginButton)findViewById(R.id.kakao_login);
        googleLogin = (SignInButton)findViewById(R.id.google_login);

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        if (!Session.getCurrentSession().isClosed()) {
            redirectWifiCheckActivity();
        }
        //LoginManager.getInstance().
        /*if(!AccessToken.getCurrentAccessToken().isExpired()) {
            redirectMainActivity();
        }
*/
        if (AccessToken.getCurrentAccessToken() != null) {
            redirectWifiCheckActivity();
        }

        callbackManager = CallbackManager.Factory.create();

        facebookLogin = (LoginButton) findViewById(R.id.facebook_login);
        facebookLogin.setReadPermissions(Arrays.asList("public_profile", "email"));
        facebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("result", object.toString());
                        redirectWifiCheckActivity();
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }


            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.d("LoginErr", error.toString());
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();
        googleLogin.setSize(SignInButton.SIZE_STANDARD);
        googleLogin.setScopes(gso.getScopeArray());
        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if(opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            redirectSignupActivity();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Logger.e(exception);
            }
            Log.d("SESSION ERROR", "");
            setContentView(R.layout.activity_login);
        }
    }

    protected void redirectSignupActivity() {
        final Intent intent = new Intent(this, KakaoSignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    protected void redirectWifiCheckActivity() {
        final Intent intent = new Intent(this, WifiCheckActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        LoginActivity.this.finish();
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("TAG", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            //updateUI(true);
            redirectWifiCheckActivity();
        } else {
            //updateUI(false);
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        //updateUI(false);
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        //updateUI(false);
                    }
                });
    }
    // [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("TAG", "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("LOADING");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        /*if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.signed_out);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }*/
    }

}
