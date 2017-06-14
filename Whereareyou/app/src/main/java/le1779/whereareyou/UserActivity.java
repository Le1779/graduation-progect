package le1779.whereareyou;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kevin on 2016/1/31.
 */
public class UserActivity extends Activity {

    private String TAG = "Login";
    private String user_name, user_email, user_id;
    private Bitmap user_picture;
    private int windowWidth;
    final DataBase dataBase = new DataBase();
    Data data;
    private static final int REQUEST_CODE  = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//無title bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//全螢幕
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        windowWidth = displaymetrics.widthPixels;


        if(checkAndRequestPermissions()) {
            data = new Data();
            if (data.readfile(UserActivity.this, "UserData")) {
                Log.d("User", "read success");
                user_email = data.getEmail();
                user_picture = data.getPicture();
                user_name = data.getName();
                user_id = data.getId();

                checkNetWork();
                initUserLayout();
            } else {//user data no found
                Log.d("User", "read error");
                Intent intentMap = new Intent();
                intentMap.setClass(UserActivity.this, FbLoginActivity.class);
                startActivity(intentMap);
                UserActivity.this.finish();
            }
        }

    }
    private  boolean checkAndRequestPermissions() {
        int ACCESS_NETWORK_STATE = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);
        int WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int GET_ACCOUNTS = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (ACCESS_NETWORK_STATE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if (WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (GET_ACCOUNTS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.GET_ACCOUNTS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_CODE);
            Log.d(TAG, "false");
            return false;
        }
        Log.d(TAG, "true");
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_CODE: {
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.ACCESS_NETWORK_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.GET_ACCOUNTS, PackageManager.PERMISSION_GRANTED);

                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
                        Data data = new Data();
                        if (data.readfile(UserActivity.this, "UserData")) {
                            Log.d("User", "read success");
                            user_email = data.getEmail();
                            user_picture = data.getPicture();
                            user_name = data.getName();
                            user_id = data.getId();
                            checkNetWork();
                            initUserLayout();
                        } else {//user data no found
                            Log.d("User", "read error");
                            Intent intentMap = new Intent();
                            intentMap.setClass(UserActivity.this, FbLoginActivity.class);
                            startActivity(intentMap);
                            UserActivity.this.finish();
                        }
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.GET_ACCOUNTS)) {
                            showDialogOK("未取的必要的權限無法繼續執行程式",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    UserActivity.this.finish();
                                                    break;
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }

    }
    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("再次確認", okListener)
                .setNegativeButton("結束", okListener)
                .create()
                .show();
    }
    private void initUserLayout() {
        setContentView(R.layout.layout_user);
        ImageView imageView = (ImageView) findViewById(R.id.userPhotoView);
        TextView textView = (TextView) findViewById(R.id.userInf);

        //imageView.setImageBitmap(user_picture);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.where_are_you_icon);
        int oldwidth = icon.getWidth();
        int oldheight = icon.getHeight();
        float scaleWidth = windowWidth/2 / (float)oldwidth;
        float scaleHeight = scaleWidth;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(icon, 0, 0, oldwidth,oldheight, matrix, true);

        imageView.setImageBitmap(resizedBitmap);
        textView.setTextSize(windowWidth / 40);
        //textView.setText(user_name);
        textView.setText("Where are you?");

        Animation Alphaanimation = new AlphaAnimation(0.0f, 1.0f);//淡出動畫(起始透明度,結束透明度)
        Alphaanimation.setDuration(4000);
        Alphaanimation.setRepeatCount(0);
        imageView.setAnimation(Alphaanimation);
        textView.setAnimation(Alphaanimation);
        Alphaanimation.startNow();

    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (!dataBase.checkDone) {
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (dataBase.isDataExist) {//檢查用戶是否存在
                Intent intentMap = new Intent();
                intentMap.setClass(UserActivity.this, MapsActivity.class);
                startActivity(intentMap);
                UserActivity.this.finish();
            } else {//新用戶
                dataBase.creatData(user_email, user_name, user_id, "24.0000000s121.0000000", null , null);
                new Thread(runnable).start();
            }
        }
    };

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
        if (isNetworkAvailable(UserActivity.this)) {
            dataBase.checkDB(user_email);
            new Thread(runnable).start();
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
                    UserActivity.this.finish();
                }
            }).show();
        }
    }
}
