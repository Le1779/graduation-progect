package le1779.whereareyou;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

/**
 * Created by kevin on 2015/12/13.
 */
public class LoginActivity extends Activity{

    private String TAG = "Login";
    private String user_name, user_email, user_id;
    private String user_data = "";
    private Bitmap user_picture;
    private int windowWidth;
    private int windowHeight;
    private ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//無title bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//全螢幕
        //get window height & width
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        windowHeight = displaymetrics.heightPixels;
        windowWidth = displaymetrics.widthPixels;

        initLoginLayout();
    }

    private void initLoginLayout(){
        setContentView(R.layout.activity_login);
        icon = (ImageView)findViewById(R.id.icon_imageView);

        Bitmap icon_Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon);//抓icon
        int width = icon_Bitmap.getWidth();
        int height = icon_Bitmap.getHeight();
        float scale = (float)(windowWidth/2)/width;
        Matrix matrix = new Matrix();// 取得想要缩放的matrix參數
        matrix.postScale(scale, scale);
        Bitmap newbm = Bitmap.createBitmap(icon_Bitmap, 0, 0, width, height, matrix,true);// 得到新的icon
        icon.setImageBitmap(newbm);//show icon

        Animation animation = new ScaleAnimation(1, 2, 1, 2, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);//以自身為中心點向外縮放2倍
        animation.setDuration(4000);//動畫開始到結束的時間，2秒
        animation.setRepeatCount(-1);// 動畫重覆次數 (-1表示一直重覆，0表示不重覆執行，所以只會執行一次)
        animation.setRepeatMode(2);
        animation.setInterpolator(new DecelerateInterpolator());
        icon.setAnimation(animation);
        animation.startNow();//開始動畫

        checkNetWork();
    }
    //Runnable runnable = new Runnable() {
    //    @Override
    //    public void run() {
    //        try {
    //            Thread.sleep(500);
    //        } catch (InterruptedException e) {
    //            e.printStackTrace();
    //        }
    //        handler.sendEmptyMessage(0);
    //    }
    //};
    //Handler handler = new Handler() {
    //    @Override
    //    public void handleMessage(Message msg) {
    //        super.handleMessage(msg);
    //        Log.d("Login", "handler");
    //        checkNetWork();
    //        //new Handler().postDelayed(r, 2000);
    //    }
    //};
    //Runnable r = new Runnable() {
    //    @Override
    //    public void run() {
    //    }
    //};

    boolean isNetworkAvailable(Activity activity) {//check network
        Context context = activity.getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;
        else {
            NetworkInfo[] networkInfo = cm.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++)
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
            }
        }
        return false;
    }

    void checkNetWork() {
        if (isNetworkAvailable(LoginActivity.this)) {
            checkLocalData();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("錯誤")
                    .setMessage("沒有網路連結")
                    .setPositiveButton("重試", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkNetWork();
                        }
                    }).setNegativeButton("結束", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LoginActivity.this.finish();
                }
            }).show();
        }
    }
    void checkLocalData(){
        final Data data = new Data();
        //user_picture = data.readBitmap(LoginActivity.this);
        //if(data.readfile(LoginActivity.this, "UserData") && user_picture != null){
        //    user_id = data.getId();
        //    user_name = data.getName();
        //    user_email = data.getEmail();
//
        //    final DataBase dataBase = new DataBase(user_name, user_id, user_email, null, null, null);
        //    dataBase.checkDB(user_id);
//
        //    new Thread(new Runnable(){
        //        @Override
        //        public void run() {
        //            // TODO Auto-generated method stub
        //            while(!dataBase.checkDone){
        //                try{
        //                    Thread.sleep(500);
        //                }
        //                catch(Exception e){
        //                    e.printStackTrace();
        //                }
        //            }
//
        //            Intent intent = new Intent();
        //            intent.setClass(LoginActivity.this, UserActivity.class);
        //            startActivity(intent);
        //            LoginActivity.this.finish();
        //        }
        //    }).start();
        //    Log.i(TAG, user_data);
        //} else{
        //    Log.e(TAG, "file no found");
        //    Intent intentMap = new Intent();
        //    intentMap.setClass(LoginActivity.this, FbLoginActivity.class);
        //    startActivity(intentMap);
        //    finish();
        //}
    }

}
