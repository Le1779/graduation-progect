package le1779.whereareyou;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private Button button;
    private Button MapBtn;
    private Button UpdataBtn;
    private Button signoutBtn;
    private TextView textView;
    private TextView googleplustv;
    private EditText editText;
    private GoogleApiClient mGoogleApiClient;
    //private AQuery mAQuery;
    private SignInButton mSignInButton;

    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private static final int RC_SIGN_IN = 0;
    private String personName;
    private String personEmail;
    private String phpUrl = "http://le1779.net16.net/test.php";
    private String TAG = "Main";
    private String NAME = "";
    private String NUM = "";
    private String COORDINATE = "";
    private String ADDRESS = "";
    private String TEXT = "";
    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    //繁重執行序用的 (時間超過3秒的)
    private Handler mThreadHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button)findViewById(R.id.button);
        MapBtn = (Button)findViewById(R.id.button2);
        UpdataBtn = (Button)findViewById(R.id.button3);
        textView = (TextView)findViewById(R.id.textView3);
        editText = (EditText)findViewById(R.id.editText);
        mSignInButton = (SignInButton)findViewById(R.id.signInBtn);
        googleplustv = (TextView)findViewById(R.id.textView4);
        signoutBtn = (Button)findViewById(R.id.signOutBtn);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
        signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mGoogleApiClient.isConnecting()) {
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();

                }
                //googleplustv.setText(personName+"\n"+personEmail);
                //signoutBtn.setVisibility(View.GONE);
                //mSignInButton.setVisibility(View.VISIBLE);
            }
        });
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
                //googleplustv.setText(personName+"\n"+personEmail);
            }
        });
        MapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMap = new Intent();
                intentMap.setClass(MainActivity.this, FbLoginActivity.class);
                startActivity(intentMap);
                //startActivityForResult(intentMap, 0);
            }
        });
        UpdataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMap = new Intent();
                intentMap.setClass(MainActivity.this, UserActivity.class);
                startActivity(intentMap);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NUM = editText.getText().toString();
                mThread = new HandlerThread("net");
                mThread.start();
                mThreadHandler = new Handler(mThread.getLooper());
                mThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // TODO 自動產生的方法 Stub
                        final String jsonString = executeQuery("SELECT * FROM  `test_friend` WHERE number =  '" + NUM +"' LIMIT 0 , 30");
                        mUI_Handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // TODO 自動產生的方法 Stub
                                try {
                                    JSONArray jArray = new JSONArray(jsonString);
                                    for (int i = 0; i < jArray.length(); i++) {
                                        JSONObject json_data = jArray.getJSONObject(i);
                                        Log.i("name", json_data.getString("name"));
                                        NAME = json_data.getString("name");
                                        NUM = json_data.getString("number");
                                        COORDINATE = json_data.getString("coordinate");
                                        ADDRESS = json_data.getString("address");
                                        TEXT = json_data.getString("text");
                                        textView.setText("name:" + NAME + "\nnumber:" + NUM + "\ncoordinate:" + COORDINATE + "\naddress:" + ADDRESS + "\ntext:" + TEXT);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        });
    }
    private String executeQuery(String query) {
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(phpUrl);
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("query", query));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpResponse = httpClient.execute(post);
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream inputStream = httpEntity.getContent();
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = bufReader.readLine()) != null) {
                builder.append(line + "\n");
            }
            inputStream.close();
            result = builder.toString();
            Log.i("result", result);
        } catch (Exception e) {
            Log.e("log_tag", e.toString());
        }
        return result;
    }
    private void DBUpdate(final String coordinate, final String num) {
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(phpUrl);
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("query", "UPDATE  `a1643572_Friend`.`test_friend` SET  `coordinate` = '"+ coordinate +"' WHERE CONVERT(`test_friend`.`number` USING utf8 ) = '" + num + "'"));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    httpClient.execute(post);
                } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }
                mUI_Handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // TODO 自動產生的方法 Stub

                    }
                });
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if (requestCode == RC_SIGN_IN) {
        //    if (!mGoogleApiClient.isConnecting()) {
        //        mGoogleApiClient.connect();
        //    }
        //}
        switch(resultCode){
            case 1:
                COORDINATE = data.getExtras().getString("coordinate");
                Log.i(TAG, String.valueOf(COORDINATE));
                Toast.makeText(getApplicationContext(), COORDINATE, Toast.LENGTH_LONG).show();
                break;
            case 2:

                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        //mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                personName = currentPerson.getDisplayName();
                personEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
                int gender = currentPerson.getGender();
                String plusId = currentPerson.getId();
                String personPhotoUrl = currentPerson.getImage().getUrl();

                String content = "PersonName:"+personName+"\n";
                content += "PersonEmail:"+personEmail+"\n";
                content += "Gender:"+gender+"\n";
                content += "PlusId:"+plusId;
                googleplustv.setText(content);
                Log.d(TAG,personPhotoUrl );
                //mAQuery.id(mPhotoIv).image(personPhotoUrl, true, true, 0, android.R.drawable.ic_menu_gallery);

                //mSignInButton.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        try {
            connectionResult.startResolutionForResult(this, RC_SIGN_IN);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }
}
