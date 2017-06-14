package le1779.whereareyou;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class FbLoginActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    private AccessToken accessToken;

    private String user_name = null, user_email = null, user_id = null;
    private String user_data = "";
    private Bitmap user_picture;
    int windowWidth;
    private String TAG = "FbLoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_fb_login);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        windowWidth = displaymetrics.widthPixels;
        callbackManager = CallbackManager.Factory.create();
        final ImageButton fb_login = (ImageButton) findViewById(R.id.fb_login_imageButton);
        TextView textView = (TextView)findViewById(R.id.login_textView);

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, windowWidth /4);
        textView.setPadding(0,0,0,windowWidth /4);
        Bitmap icon_Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.facebook_icon);//抓icon
        int width = icon_Bitmap.getWidth();
        int height = icon_Bitmap.getHeight();
        float scale = (float)(windowWidth /4)/width;
        Matrix matrix = new Matrix();// 取得想要缩放的matrix參數
        matrix.postScale(scale, scale);
        Bitmap newbm = Bitmap.createBitmap(icon_Bitmap, 0, 0, width, height, matrix,true);// 得到新的icon
        fb_login.setImageBitmap(newbm);

        fb_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(FbLoginActivity.this, Arrays.asList("public_profile", "user_friends", "email"));
            }
        });
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            //登入成功
            @Override
            public void onSuccess(LoginResult loginResult) {
                //accessToken之後或許還會用到 先存起來
                accessToken = loginResult.getAccessToken();
                Log.d("FB", "access token got.");
                //send request and call graph api
                GraphRequest request = GraphRequest.newMeRequest(accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            //當RESPONSE回來的時候
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                //讀出姓名 ID FB個人頁面連結
                                if (response.getError() != null) {
                                    // handle error
                                } else {
                                    user_email = object.optString("email");
                                    user_data += user_email + "\n";
                                    user_id = object.optString("id");
                                    user_data += user_id + "\n";
                                    user_name = object.optString("name");
                                    user_data += user_name + "\n";

                                    new Thread(runnable).start();//取的圖片&儲存使用者資料、圖片

                                    Log.d("FB", "complete");
                                    Log.d("FB", user_email);
                                    Log.d("FB", user_name);
                                    Log.d("FB", object.optString("link"));
                                    Log.d("FB", user_id);
                                    Log.d("FB", "https://graph.facebook.com/" + user_id + "/picture?type=large");
                                }

                            }
                        });
                //包入你想要得到的資料 送出request
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            //登入取消
            @Override
            public void onCancel() {
                // App code
                Log.d("FB", "CANCEL");
            }

            //登入失敗
            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d(TAG, "onError");
                Toast.makeText(getApplicationContext(), "登入失敗，稍後再試。", Toast.LENGTH_LONG).show();
                Log.d("FB", exception.toString());
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL("https://graph.facebook.com/" + user_id + "/picture?type=large");
                user_picture = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                user_picture = getRoundedBitmap(user_picture);
            } catch (IOException e) {
                e.printStackTrace();
            }
            handler.sendEmptyMessage(0);
        }
    };
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Data data = new Data();
            if(data.saveData(user_data, FbLoginActivity.this, "UserData", user_picture)){
                Intent intent = new Intent();
                intent.setClass(FbLoginActivity.this, UserActivity.class);
                startActivity(intent);
                FbLoginActivity.this.finish();
            } else {
                Toast.makeText(FbLoginActivity.this, "儲存錯誤請再試一次", Toast.LENGTH_LONG);
            }
        }
    };
    private Bitmap getRoundedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getWidth(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getWidth());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
    Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left,top,right,bottom,dst_left,dst_top,dst_right,dst_bottom;
        width = windowWidth/5;
        height = windowWidth/5;
        if (width >= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int)left, (int)top, (int)right, (int)bottom);
        final Rect dst = new Rect((int)dst_left, (int)dst_top, (int)dst_right, (int)dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }
}
