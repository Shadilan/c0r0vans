package utility.sign;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;

import utility.GATracker;
import utility.MainThread;

/**
 * Created by Shadilan on 24.07.2016.
 */
public class SignIn {
    private static Activity activity;
    private static SignInListener listener;
    private static int signCall=0;
    private static GoogleApiClient mGoogleApiClient;
    //Запрос токена
    public static void init(Activity act){
        activity=act;
    }
    public static void setListener(SignInListener signInListener){
        listener=signInListener;
    }

    public static void getToken(){
        if (signCall>3){
            signCall=0;
            MainThread.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getToken();
                }
            },30000);
        }


        GATracker.trackTimeStart("System","SignIn");

        SharedPreferences sharedPreferences=activity.getApplicationContext().getSharedPreferences("SpiritProto", Context.MODE_PRIVATE);
        String accountName=sharedPreferences.getString("AccountName", "");

        GoogleSignInOptions gso;

        if ("".equals(accountName)) {
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("818299087088-ooq951dsv5btv7361u4obhlse0apt3al.apps.googleusercontent.com")
                    .requestEmail()
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(activity.getApplicationContext())
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            activity.startActivityForResult(signInIntent, 123);
        }
        else {
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("818299087088-ooq951dsv5btv7361u4obhlse0apt3al.apps.googleusercontent.com")
                    .requestEmail()
                    .setAccountName(accountName)
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(activity.getApplicationContext())
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            Log.d("SignIn","Enter");
            Thread signing=new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d("SignIn","Start");
                        ConnectionResult res = mGoogleApiClient.blockingConnect();
                        OptionalPendingResult<GoogleSignInResult> pendingResult = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);

                        if (pendingResult != null) {
                            Log.d("SignIn","pending");
                            if (pendingResult.isDone()) {
                                Log.d("SignIn","Done");
                                GoogleSignInResult signInResult = pendingResult.get();
                                doSignIn(signInResult);
                            } else if (pendingResult.isCanceled()){
                                Log.d("SignIn","Canceled");
                                doSignIn(null);
                            }
                            else {
                                Log.d("SignIn","NotDone");
                                getToken();

                            }
                        } else {
                            Log.d("SignIn","Noresult");
                            doSignIn(null);
                        }
                    } finally {
                        mGoogleApiClient.disconnect();
                    }
                }
            });
            signing.start();
            //Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            //startActivityForResult(signInIntent, 123);

        }
    }

    private static String idToken;
    private static void doSignIn(GoogleSignInResult result){
        if (result!=null && result.isSuccess()) {

            GoogleSignInAccount acct = result.getSignInAccount();
            // Get account information
            //String accountNAme = acct.get();
            if (acct!=null) {
                idToken = acct.getIdToken();
                String mEmail = acct.getEmail();
                SharedPreferences sharedPreferences = activity.getApplicationContext().getSharedPreferences("SpiritProto", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("AccountName", mEmail);
                editor.apply();
                GATracker.trackTimeEnd("System","SignIn");
                //Login To Server
                MainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener!=null) listener.onComplete(idToken);
                    }
                });

                //initStart();
            } else getToken();
        } else {
            getToken();
        }

    }

    //Результат из чужого интента.
    public static void intentResult(GoogleSignInResult result){
        doSignIn(result);
    }
    //Листенер с запрошеным токеном.




}
