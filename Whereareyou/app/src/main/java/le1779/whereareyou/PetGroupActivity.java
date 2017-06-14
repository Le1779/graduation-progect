package le1779.whereareyou;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.List;
import java.util.Locale;

public class PetGroupActivity extends AppCompatActivity implements OnMapReadyCallback {

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
    private Button set_time_btn;
    private DrawView view;
    AQuery aQuery = new AQuery(this);

    private int area_metre;
    private String area_distance;

    private int[] select_day = new int[7];
    private int[] select_time = new int[2];
    String update_str;
    private String latitude;
    private String longitude;
    private String pet_imei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_group);
        gpsTracker = new GPSTracker(this);
        imm = ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.pet_map);
        mapFragment.getMapAsync(this);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        windowWidth = displaymetrics.widthPixels;
        windowHeight = displaymetrics.heightPixels;
        Bundle bundle =this.getIntent().getExtras();
        group_name = bundle.getString("group_name");
        user_id = bundle.getString("user_id");

        search_address = (EditText)findViewById(R.id.address_editText);
        relativeLayout = (RelativeLayout)findViewById(R.id.pet_layout);
        area_meter = (TextView)findViewById(R.id.area_meter_textView);

        view=new DrawView(PetGroupActivity.this, windowWidth/2, windowHeight/2, 300);
        view.invalidate();
        relativeLayout.addView(view);

        initSeekBar();
        initImageButton();
        Button search = (Button)findViewById(R.id.seach_pet_button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                creat_group_dialog = ProgressDialog.show(PetGroupActivity.this, " 搜尋中", "請等待...", true);
                EditText imei_edit = (EditText)findViewById(R.id.area_name_editText);
                pet_imei=imei_edit.getText().toString();
                new Thread(search_runnable).start();
            }
        });
    }
    private void initSeekBar(){
        SeekBar seekBar = (SeekBar)findViewById(R.id.pet_area_seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekProgress = seekBar.getProgress() + 100;
                relativeLayout.removeView(view);
                view = new DrawView(PetGroupActivity.this, windowWidth / 2, windowHeight / 2, seekProgress);
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
                view = new DrawView(PetGroupActivity.this, windowWidth / 2, windowHeight / 2, seekProgress);
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
            title.setText(group_name+"-寵物群組");
            title.setTextColor(Color.WHITE);
            ImageButton back_btn = (ImageButton)mActionBarView.findViewById(R.id.back_btn);
            back_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PetGroupActivity.this.finish();
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
                    update_str = "";
                    Geocoder geocoder = new Geocoder(PetGroupActivity.this, Locale.getDefault());
                    List<Address> addresses;
                    try {
                        addresses = geocoder.getFromLocation(centerLatLng.latitude, centerLatLng.longitude, 1);
                        update_str = addresses.get(0).getAddressLine(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(!update_str.equals("") && !update_str.equals(null)) {
                        creat_group_dialog = ProgressDialog.show(PetGroupActivity.this, " 更新中", "請等待...", true);
                        new Thread(wait_update_runnable).start();
                        //Toast.makeText(getApplicationContext(), update_str, Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "你還沒設定區域的備註", Toast.LENGTH_SHORT).show();
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
            dataBase.creatPetGroup(group_name, user_id, latitude, longitude, area_distance, update_str);
            while(dataBase.iscreatPetGroup){
                try {
                    Log.d("black", "wait");
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
            PetGroupActivity.this.finish();
        }
    };
    Runnable search_runnable = new Runnable() {
        @Override
        public void run() {
            DataBase dataBase = new DataBase();
            dataBase.searchPet(pet_imei);
            while(dataBase.iscreatPetGroup){
                try {
                    Log.d("search pet", "wait");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            pet_imei = dataBase.search_pet_imei;
            search_handler.sendEmptyMessage(0);
        }
    };
    Handler search_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            creat_group_dialog.dismiss();
            Toast.makeText(getApplicationContext(), pet_imei, Toast.LENGTH_SHORT).show();
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
        paint.setColor(Color.rgb(41, 136, 102));
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
            paint.setColor(Color.rgb(41, 136, 102));
            paint.setAntiAlias(true);
            canvas.drawCircle(x, y, 10, paint);
            canvas.drawCircle(x - m, y, 10, paint);
            canvas.drawCircle(x + m, y, 10, paint);
            //Log.d("draw", "draw");
        }
    }
}
