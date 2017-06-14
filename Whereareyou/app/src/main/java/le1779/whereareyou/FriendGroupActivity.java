package le1779.whereareyou;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FriendGroupActivity extends AppCompatActivity implements OnMapReadyCallback {

    GPSTracker gpsTracker;
    GoogleMap mMap;
    InputMethodManager imm;
    int windowWidth;
    int windowHeight;
    String group_name;
    String user_id;

    int seekProgress=300;
    ProgressDialog creat_group_dialog;

    private RelativeLayout relativeLayout;
    private TextView area_meter;
    private TextView class_time;
    private EditText search_address;
    private EditText date_name;
    private Button set_time_btn;
    private DrawView view;
    AQuery aQuery = new AQuery(this);

    private int area_metre;
    private String area_distance;

    private int[] select_day = new int[7];
    private int[] select_time = new int[2];
    String update_str="";
    private String latitude;
    private String longitude;
    private String address;
    private String area_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_group);
        gpsTracker = new GPSTracker(this);
        imm = ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.friend_map);
        mapFragment.getMapAsync(this);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        windowWidth = displaymetrics.widthPixels;
        windowHeight = displaymetrics.heightPixels;
        Bundle bundle =this.getIntent().getExtras();
        group_name = bundle.getString("group_name");
        user_id = bundle.getString("user_id");

        date_name = (EditText)findViewById(R.id.date_name_editText);
        search_address = (EditText)findViewById(R.id.address_editText);
        relativeLayout = (RelativeLayout)findViewById(R.id.friend_area_layout);
        area_meter = (TextView)findViewById(R.id.area_meter_textView);
        class_time = (TextView)findViewById(R.id.friend_group_time_textView);
        set_time_btn = (Button)findViewById(R.id.set_friend_time_button);
        set_time_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int month_creat_new = c.get(Calendar.MONTH);
                int day_creat_new = c.get(Calendar.DAY_OF_MONTH);
                int hour_creat_new = c.get(Calendar.HOUR_OF_DAY);
                int minute_creat_new = c.get(Calendar.MINUTE);
                showAlertDialog(month_creat_new, day_creat_new, hour_creat_new, minute_creat_new);
            }
        });
        view=new DrawView(FriendGroupActivity.this, windowWidth/2, windowHeight/2, 300);
        view.invalidate();
        relativeLayout.addView(view);

        initSeekBar();
        initImageButton();
    }
    private void initSeekBar(){
        SeekBar seekBar = (SeekBar)findViewById(R.id.friend_area_seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekProgress = seekBar.getProgress() + 100;
                relativeLayout.removeView(view);
                view = new DrawView(FriendGroupActivity.this, windowWidth / 2, windowHeight / 2, seekProgress);
                view.invalidate();
                relativeLayout.addView(view);
                Point right = new Point((windowWidth / 2) + seekProgress, windowHeight / 2);
                Point lift = new Point((windowWidth / 2) - seekProgress, windowHeight / 2);
                LatLng rightLatLng = mMap.getProjection().fromScreenLocation(right);
                LatLng liftLatLng = mMap.getProjection().fromScreenLocation(lift);
                float results[] = new float[1];
                Location.distanceBetween(rightLatLng.latitude, rightLatLng.longitude, liftLatLng.latitude, liftLatLng.longitude, results);
                area_metre = (int) results[0];
                area_distance = NumberFormat.getInstance().format(results[0]);
                area_meter.setText(area_distance + "m");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekProgress = seekBar.getProgress() + 100;
                relativeLayout.removeView(view);
                view = new DrawView(FriendGroupActivity.this, windowWidth / 2, windowHeight / 2, seekProgress);
                view.invalidate();
                relativeLayout.addView(view);
                Point right = new Point((windowWidth / 2) + seekProgress, windowHeight / 2);
                Point lift = new Point((windowWidth / 2) - seekProgress, windowHeight / 2);
                LatLng rightLatLng = mMap.getProjection().fromScreenLocation(right);
                LatLng liftLatLng = mMap.getProjection().fromScreenLocation(lift);
                float results[] = new float[1];
                Location.distanceBetween(rightLatLng.latitude, rightLatLng.longitude, liftLatLng.latitude, liftLatLng.longitude, results);
                area_metre = (int) results[0];
                area_distance = NumberFormat.getInstance().format(results[0]);
                area_meter.setText(area_distance + "m");
            }
        });
    }

    private void initImageButton(){
        ImageButton searchAddress_imgbtn = (ImageButton)findViewById(R.id.sarch_imageButton);
        searchAddress_imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyword = search_address.getText().toString();
                imm.hideSoftInputFromWindow(search_address.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                String url = null;
                try {
                    url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + URLEncoder.encode(keyword, "UTF-8") + "&key=AIzaSyDrMZtkbN_2Vu1u9bdyh3WObqMFk1dkvAs";
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                aQuery.ajax(url, null, JSONObject.class, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject object, AjaxStatus status) {
                        super.callback(url, object, status);
                        Log.d("black", url);
                        try {
                            JSONArray jArray = object.getJSONArray("results");
                            String sta = object.getString("status");
                            mMap.clear();
                            if (sta.equals("OK")) {
                                for (int i = 0; i < jArray.length(); i++) {
                                    JSONObject info = jArray.getJSONObject(i);
                                    final String name = info.getString("name");
                                    final String id = info.getString("place_id");
                                    final String address = info.getString("formatted_address");
                                    final String lat = new JSONObject(new JSONObject(info.getString("geometry")).getString("location")).getString("lat");
                                    final String lng = new JSONObject(new JSONObject(info.getString("geometry")).getString("location")).getString("lng");
                                    Log.d("black", name);
                                    Log.d("black", id);
                                    Log.d("black", lat);
                                    Log.d("black", lng);
                                    Log.d("black", info.getString("icon"));
                                    final int finalI = i;
                                    aQuery.ajax(info.getString("icon"), null, Bitmap.class, new AjaxCallback<Bitmap>() {
                                        @Override
                                        public void callback(String url, Bitmap bitmap, AjaxStatus status) {
                                            super.callback(url, bitmap, status);
                                            bitmap = getRoundedCornerBitmap(drawBgBitmap(changeColor(bitmap)));
                                            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
                                            mMap.addMarker(new MarkerOptions()
                                                    .anchor(0.5f, 0.5f)
                                                    .title(name)
                                                    .snippet(address)
                                                    .icon(icon)
                                                    .position(new LatLng(Double.valueOf(lat), Double.valueOf(lng))));
                                            if (finalI == 0) {
                                                moveMap(new LatLng(Double.valueOf(lat), Double.valueOf(lng)));
                                            }
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "沒有結果", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("black", e.toString());
                        }
                    }
                });
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            View mActionBarView = getLayoutInflater().inflate(R.layout.black_list_actionbar, null);
            actionBar.setCustomView(mActionBarView);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

            TextView title = (TextView)mActionBarView.findViewById(R.id.title_blacklist_action_bar);
            title.setText(group_name+"-朋友群組");
            title.setTextColor(Color.WHITE);
            ImageButton back_btn = (ImageButton)mActionBarView.findViewById(R.id.back_btn);
            back_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FriendGroupActivity.this.finish();
                }
            });
            ImageButton next_btn = (ImageButton)mActionBarView.findViewById(R.id.goto_next_page);
            next_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Point right = new Point((windowWidth / 2) + seekProgress, windowHeight / 2);
                    Point lift = new Point((windowWidth / 2) - seekProgress, windowHeight / 2);
                    LatLng rightLatLng = mMap.getProjection().fromScreenLocation(right);
                    LatLng liftLatLng = mMap.getProjection().fromScreenLocation(lift);
                    float results[] = new float[1];
                    Location.distanceBetween(rightLatLng.latitude, rightLatLng.longitude, liftLatLng.latitude, liftLatLng.longitude, results);
                    area_metre = (int)results[0];
                    area_distance = NumberFormat.getInstance().format(results[0]);
                    Point center = new Point(windowWidth / 2, windowHeight / 2);
                    LatLng centerLatLng = mMap.getProjection().fromScreenLocation(center);
                    latitude = String.valueOf(centerLatLng.latitude);
                    longitude = String.valueOf(centerLatLng.longitude);
                    Geocoder geocoder = new Geocoder(FriendGroupActivity.this, Locale.getDefault());
                    List<Address> addresses;
                    try {
                        addresses = geocoder.getFromLocation(centerLatLng.latitude, centerLatLng.longitude, 1);
                        address = addresses.get(0).getAddressLine(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    area_name = date_name.getText().toString();
                    if(area_name.equals("") || area_name.equals(null))
                        area_name = group_name;
                    if(!update_str.equals("")&&!update_str.equals(null)) {
                        creat_group_dialog = ProgressDialog.show(FriendGroupActivity.this, " 更新中", "請等待...", true);
                        new Thread(wait_update_runnable).start();
                        Toast.makeText(getApplicationContext(), update_str, Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "你還沒設定約會的時間", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        return true;
    }
    Runnable wait_update_runnable = new Runnable() {
        @Override
        public void run() {
            DataBase dataBase = new DataBase();
            dataBase.creatFriendGroup(group_name, user_id, latitude, longitude, area_distance, update_str, area_name, address);
            while(dataBase.iscreatFriendGroup){
                try {
                    Log.d("friend_group_creat", "wait");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            wait_update_handler.sendEmptyMessage(0);
        }
    };
    Handler wait_update_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            creat_group_dialog.dismiss();
            FriendGroupActivity.this.finish();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null) {
            if (gpsTracker.canGetLocation()) {
                //取得經緯度
                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();
                LatLng place = new LatLng(latitude, longitude);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(place).zoom(15).build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mMap.addMarker(new MarkerOptions()
                        .anchor(0.5f, 0.5f)
                        .title("你的位置")
                        .position(place));
            }

            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    Point right = new Point((windowWidth / 2) + seekProgress, windowHeight / 2);
                    Point lift = new Point((windowWidth / 2) - seekProgress, windowHeight / 2);
                    LatLng rightLatLng = mMap.getProjection().fromScreenLocation(right);
                    LatLng liftLatLng = mMap.getProjection().fromScreenLocation(lift);
                    float results[] = new float[1];
                    Location.distanceBetween(rightLatLng.latitude, rightLatLng.longitude, liftLatLng.latitude, liftLatLng.longitude, results);
                    area_metre = (int)results[0];
                    area_distance = NumberFormat.getInstance().format(results[0]);
                    Log.d("Area", "" + area_distance);
                    area_meter.setText(area_distance + "m");
                }
            });
        }
    }
    private void moveMap(LatLng place) {
        // 建立地圖攝影機的位置物件
        CameraPosition cameraPosition;
        if(mMap.getCameraPosition().zoom<15)
            cameraPosition = new CameraPosition.Builder().target(place).zoom(15).build();
        else
            cameraPosition = new CameraPosition.Builder().target(place).zoom(mMap.getCameraPosition().zoom).build();
        // 使用動畫的效果移動地圖
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    public Bitmap drawBgBitmap(Bitmap orginBitmap) {
        Paint paint = new Paint();
        paint.setColor(Color.rgb(91, 91, 174));
        Bitmap bitmap = Bitmap.createBitmap(orginBitmap.getWidth(),
                orginBitmap.getHeight(), orginBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0, 0, orginBitmap.getWidth(), orginBitmap.getHeight(), paint);
        canvas.drawBitmap(orginBitmap, 0, 0, paint);
        return bitmap;
    }
    private Bitmap changeColor(Bitmap src){
        int bmWidth  = src.getWidth();
        int bmHeight = src.getHeight();
        int[] newBitmap = new int[bmWidth * bmHeight];

        src.getPixels(newBitmap, 0, bmWidth, 0, 0, bmWidth, bmHeight);

        for (int h = 0; h < bmHeight; h++){
            for (int w = 0; w < bmWidth; w++){
                int index = h * bmWidth + w;
                int alpha = newBitmap[index] & 0xff000000;
                int r = (newBitmap[index] >> 16) & 0xff;
                int g = (newBitmap[index] >> 8) & 0xff;
                int b = newBitmap[index] & 0xff;

                if(alpha != 0){
                    r = 255;
                    g = 255;
                    b = 255;
                }

                newBitmap[index] = alpha | (r << 16) | (g << 8) | b;
            }
        }

        Bitmap bm = Bitmap.createBitmap(bmWidth, bmHeight, Bitmap.Config.ARGB_8888);
        bm.setPixels(newBitmap, 0, bmWidth, 0, 0, bmWidth, bmHeight);

        return bm;
    }
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        //final float roundPx = 12;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);
        //canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
    private void showAlertDialog(final int mon, final int day, final int h, final int m){
        Log.d("showAlertDialog", "get" + h + " " + m + "\n");
        LayoutInflater inflater = LayoutInflater.from(FriendGroupActivity.this);
        final View v = inflater.inflate(R.layout.date_time_dialog, null);
        final NumberPicker month_numPic = (NumberPicker)v.findViewById(R.id.month_numberPicker);
        final NumberPicker day_numPic = (NumberPicker)v.findViewById(R.id.day_numberPicker);
        final NumberPicker hour_numPic = (NumberPicker)v.findViewById(R.id.hour_numberPicker);
        final NumberPicker minute_numPic = (NumberPicker)v.findViewById(R.id.min_numberPicker);

        month_numPic.setMaxValue(11);
        month_numPic.setMinValue(0);
        month_numPic.setValue(mon);
        String monStr[] = new String[]{"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"};
        String setmon[] = new String[12];
        for(int i = 0; i<12; i++){
            if(i<mon)
                setmon[i]="明年"+monStr[i];
            else
                setmon[i]="今年"+monStr[i];
        }
        month_numPic.setDisplayedValues(setmon);



        month_numPic.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                int mm = numberPicker.getValue();
                Log.d("mon", mm+"mon");
                mm++;
                if (mm == 2 || mm == 4 || mm == 6 || mm == 9 || mm == 11) {
                    day_numPic.setMaxValue(30);
                } else {
                    day_numPic.setMaxValue(31);
                }
            }
        });

        String dayStr[] = new String[31];
        for(int i =0; i<31; i++){
            dayStr[i]=String.valueOf(i+1)+"號";
        }
        day_numPic.setMaxValue(31);
        day_numPic.setMinValue(1);
        day_numPic.setValue(day);
        day_numPic.setDisplayedValues(dayStr);

        hour_numPic.setMaxValue(23);
        hour_numPic.setMinValue(0);
        hour_numPic.setValue(h);
        hour_numPic.setDisplayedValues(new String[]{"0點", "1點", "2點", "3點", "4點", "5點", "6點", "7點", "8點", "9點", "10點", "11點", "12點", "13點", "14點", "15點", "16點", "17點", "18點", "19點", "20點", "21點", "22點", "23點"});

        minute_numPic.setMaxValue(59);
        minute_numPic.setMinValue(0);
        minute_numPic.setValue(m);

        new AlertDialog.Builder(FriendGroupActivity.this)
                .setTitle("設定約會時間")
                .setView(v)
                .setCancelable(false)
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                })
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int alertDialog_hour = hour_numPic.getValue();
                        int alertDialog_minute = minute_numPic.getValue();
                        select_time[0] = alertDialog_hour;
                        select_time[1] = alertDialog_minute;
                        String minute = String.valueOf(alertDialog_minute);
                        if (alertDialog_minute < 10)
                            minute = "0" + String.valueOf(alertDialog_minute);


                        update_str = "";
                        update_str+=month_numPic.getValue()+1;
                        update_str+="/";
                        update_str+=day_numPic.getValue();
                        update_str+=",";
                        update_str+=hour_numPic.getValue();
                        update_str+=":";
                        update_str+=minute_numPic.getValue();

                        String mStr = update_str.substring(0,update_str.indexOf("/"));
                        String dStr = update_str.substring(update_str.indexOf("/")+1,update_str.indexOf(","));
                        String hStr = update_str.substring(update_str.indexOf(",")+1,update_str.indexOf(":"));
                        String minStr = update_str.substring(update_str.indexOf(":")+1);
                        //class_time.setText(mStr+"月"+output.substring(output.indexOf(",")+1)+"日");

                        class_time.setText(mStr+"月"+dStr+"日"+hStr+"點"+minStr);
                    }
                }).show();
    }
    public class DrawView extends View {
        int x;
        int y;
        int m;
        int meter;

        public DrawView(Context context, int x, int y, int m) {
            super(context);
            this.x=x;
            this.y=y;
            this.m=m;
            this.meter = meter;
        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Paint paint = new Paint();
            paint.setColor(Color.argb(200, 255, 255, 255));
            paint.setAntiAlias(true);
            canvas.drawCircle(x, y, m, paint);
            paint.setColor(Color.rgb(255, 145, 36));
            paint.setAntiAlias(true);
            canvas.drawCircle(x, y, 10, paint);
            canvas.drawCircle(x - m, y, 10, paint);
            canvas.drawCircle(x + m, y, 10, paint);
            //Log.d("draw", "draw");
        }
    }
}