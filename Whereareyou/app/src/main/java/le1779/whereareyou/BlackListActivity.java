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
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class BlackListActivity extends AppCompatActivity implements OnMapReadyCallback {

    GPSTracker gpsTracker;
    GoogleMap mMap;
    InputMethodManager imm;
    AQuery aQuery = new AQuery(this);
    EditText keyword_edit;
    Button search_again_btn;
    TableRow blacklist_tablerow;
    //當下地圖上的標記
    ArrayList<String> place_id;
    ArrayList<String> place_name;
    ArrayList<String> place_address;
    ArrayList<LatLng> place_latlng;
    ArrayList<Bitmap> place_icon;
    //目前選擇的黑單，刪掉的會變null，size不變
    ArrayList<View> blacklist_View = new ArrayList<>();
    ArrayList<String> blacklist_id = new ArrayList<>();
    ArrayList<String> blacklist_name = new ArrayList<>();
    ArrayList<String> blacklist_address = new ArrayList<>();
    ArrayList<LatLng> blacklist_latlng = new ArrayList<>();
    ArrayList<Bitmap> blacklist_icon = new ArrayList<>();

    ArrayList<String> blacklist;
    ProgressDialog creat_group_dialog;
    private int windowWidth;
    private int windowHeight;
    int TextViewMaxWidth;
    int REQUEST_PLACE_PICKER  = 1;
    boolean IS_FIRST_TIME_CHANGE_CAMERA = false;
    int btn_count=0;
    String group_name;
    String user_id;
    String update_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);
        gpsTracker = new GPSTracker(this);
        imm = ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.black_list_map);
        mapFragment.getMapAsync(this);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        windowWidth = displaymetrics.widthPixels;
        windowHeight = displaymetrics.heightPixels;
        TextViewMaxWidth = 71 / displaymetrics.widthPixels;

        Bundle bundle =this.getIntent().getExtras();
        group_name = bundle.getString("group_name");
        user_id = bundle.getString("user_id");

        blacklist_tablerow = (TableRow)findViewById(R.id.black_list_tableRow);
        keyword_edit = (EditText) findViewById(R.id.keyword_editText);
        search_again_btn = (Button)findViewById(R.id.search_again_button);
        search_again_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_keyword();
            }
        });
        ImageButton search_imgbtn = (ImageButton) findViewById(R.id.sarch_keyword_imageButton);
        search_imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_keyword();
            }
        });
        Button add_all_btn = (Button)findViewById(R.id.add_all_button);
        add_all_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int add=0;
                int skip=0;
                try {

                    for (int i = 0; i < place_name.size(); i++) {
                        if (!blacklist_name.contains(place_name.get(i))) {
                            add++;
                            LinearLayout blacklist_info = new LinearLayout(BlackListActivity.this);
                            blacklist_info.setOrientation(LinearLayout.VERTICAL);
                            ImageButton blacklist_icon_imagebtn = new ImageButton(BlackListActivity.this);
                            final int finalI = i;
                            blacklist_icon_imagebtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    moveMap(place_latlng.get(finalI));
                                }
                            });
                            blacklist_icon_imagebtn.setBackground(null);
                            blacklist_icon_imagebtn.setImageBitmap(place_icon.get(i));

                            TextView blacklist_name_text = new TextView(BlackListActivity.this);
                            blacklist_name_text.setGravity(Gravity.CENTER_HORIZONTAL);
                            blacklist_name_text.setTypeface(null, Typeface.BOLD);
                            blacklist_name_text.setTextColor(Color.BLACK);
                            blacklist_name_text.setText(place_name.get(i));
                            blacklist_name_text.setMaxWidth(TextViewMaxWidth);
                            blacklist_name_text.setSingleLine();

                            Button cancel_btn = new Button(BlackListActivity.this);
                            cancel_btn.setBackgroundResource(R.drawable.button_background);
                            cancel_btn.setTextColor(Color.WHITE);
                            cancel_btn.setText("刪除");
                            cancel_btn.setId(btn_count++);
                            cancel_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.d("black", "del name=" + blacklist_name.get(view.getId()));
                                    blacklist_tablerow.removeView(blacklist_View.get(view.getId()));
                                    blacklist_name.set(view.getId(), "null");
                                }
                            });

                            blacklist_info.addView(blacklist_icon_imagebtn);
                            blacklist_info.addView(blacklist_name_text);
                            blacklist_info.addView(cancel_btn);
                            blacklist_tablerow.addView(blacklist_info);
                            blacklist_View.add(blacklist_info);
                            blacklist_name.add(place_name.get(i));
                            blacklist_id.add(place_id.get(i));
                            blacklist_address.add(place_address.get(i));
                            blacklist_latlng.add(place_latlng.get(i));
                            Log.d("black", place_id.get(i));
                            Log.d("black", "size=" + blacklist_View.size());
                        } else {
                            skip++;
                        }
                    }
                    Toast.makeText(getApplicationContext(), "新增" + add + "個，並且略過" + skip + "個", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Log.e("black", e.toString());
                }
            }
        });
    }
     void search_keyword(){
         IS_FIRST_TIME_CHANGE_CAMERA=true;
         imm.hideSoftInputFromWindow(keyword_edit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
         String keyword = keyword_edit.getText().toString();
         Point center = new Point(windowWidth, windowHeight / 2);
         Point right = new Point(windowWidth, windowHeight / 2);
         Point lift = new Point(0, windowHeight / 2);
         LatLng centerLatLng = mMap.getProjection().fromScreenLocation(center);
         LatLng rightLatLng = mMap.getProjection().fromScreenLocation(right);
         LatLng liftLatLng = mMap.getProjection().fromScreenLocation(lift);
         float results[] = new float[1];
         Location.distanceBetween(rightLatLng.latitude, rightLatLng.longitude, liftLatLng.latitude, liftLatLng.longitude, results);
         int area_metre = (int)results[0];
         place_id = new ArrayList<>();
         place_name = new ArrayList<>();
         place_address = new ArrayList<>();
         place_latlng = new ArrayList<>();
         place_icon = new ArrayList<>();
         //設定API
         String url = null;
         try {
             url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + centerLatLng.latitude + "," + centerLatLng.longitude + "&radius="+area_metre+"&name="+ URLEncoder.encode(keyword, "UTF-8")+"&key=AIzaSyDrMZtkbN_2Vu1u9bdyh3WObqMFk1dkvAs";
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
                             final String vicinity = info.getString("vicinity");
                             final String lat = new JSONObject(new JSONObject(info.getString("geometry")).getString("location")).getString("lat");
                             final String lng = new JSONObject(new JSONObject(info.getString("geometry")).getString("location")).getString("lng");
                             Log.d("black", name);
                             Log.d("black", id);
                             Log.d("black", lat);
                             Log.d("black", lng);
                             Log.d("black", info.getString("icon"));
                             aQuery.ajax(info.getString("icon"), null, Bitmap.class, new AjaxCallback<Bitmap>() {
                                 @Override
                                 public void callback(String url, Bitmap bitmap, AjaxStatus status) {
                                     super.callback(url, bitmap, status);
                                     bitmap = getRoundedCornerBitmap(drawBgBitmap(changeColor(bitmap)));
                                     BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
                                     mMap.addMarker(new MarkerOptions()
                                             .anchor(0.5f, 0.5f)
                                             .title(name)
                                             .snippet(vicinity + "\n點擊新增至黑單")
                                             .icon(icon)
                                             .position(new LatLng(Double.valueOf(lat), Double.valueOf(lng))));
                                     place_name.add(name);
                                     place_id.add(id);
                                     place_icon.add(bitmap);
                                     place_address.add(vicinity);
                                     place_latlng.add(new LatLng(Double.valueOf(lat), Double.valueOf(lng)));
                                 }
                             });
                         }
                     } else {
                         Toast.makeText(getApplicationContext(), "沒有結果", Toast.LENGTH_SHORT).show();
                     }
                     if (search_again_btn.getVisibility() == View.VISIBLE) {
                         search_again_btn.setVisibility(View.INVISIBLE);
                     }
                 } catch (JSONException e) {
                     e.printStackTrace();
                     Log.e("black", e.toString());
                 }

             }
         });
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_black_list, menu);
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
            title.setText(group_name+"-黑單群組");
            title.setTextColor(Color.WHITE);
            ImageButton back_btn = (ImageButton)mActionBarView.findViewById(R.id.back_btn);
            back_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BlackListActivity.this.finish();
                }
            });
            ImageButton next_btn = (ImageButton)mActionBarView.findViewById(R.id.goto_next_page);
            next_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    blacklist = new ArrayList<String>();
                    JSONArray jarray = new JSONArray();
                    int count = 0;
                    for(int i=0; i<blacklist_name.size(); i++){
                        if(!blacklist_name.get(i).equals("null")){
                            JSONObject jobject = new JSONObject();
                            try {
                                jobject.put("id", blacklist_id.get(i));
                                jobject.put("name", blacklist_name.get(i));
                                jobject.put("address", blacklist_address.get(i));
                                jobject.put("latitude", blacklist_latlng.get(i).latitude);
                                jobject.put("longitude", blacklist_latlng.get(i).longitude);
                                jarray.put(jobject);
                                count++;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Log.d("black", jarray.toString());
                    update_str = jarray.toString();
                    if(count>0&&count<51) {
                        creat_group_dialog = ProgressDialog.show(BlackListActivity.this, " 更新中", "請等待...", true);
                        new Thread(wait_update_runnable).start();
                    } else if(count==0){
                        Toast.makeText(getApplicationContext(), "最少要一個黑單地點", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "已經超過上限，最多50個黑單地點", Toast.LENGTH_SHORT).show();
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
            dataBase.creatBlackListGroup(group_name, user_id, update_str);
            while(dataBase.iscreatBlackListGroup){
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
            BlackListActivity.this.finish();
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
                RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.black_list_layout);
                DrawView view = new DrawView(BlackListActivity.this, windowWidth/2, windowHeight/2);
                view.invalidate();
                relativeLayout.addView(view);
            }
            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    if (search_again_btn.getVisibility() == View.INVISIBLE && IS_FIRST_TIME_CHANGE_CAMERA) {
                        search_again_btn.setVisibility(View.VISIBLE);
                    }
                }
            });

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(final Marker marker) {
                    final int i = place_name.indexOf(marker.getTitle());

                    if(!blacklist_name.contains(place_name.get(i))){
                        LinearLayout blacklist_info = new LinearLayout(BlackListActivity.this);
                        blacklist_info.setOrientation(LinearLayout.VERTICAL);
                        ImageButton blacklist_icon_imagebtn = new ImageButton(BlackListActivity.this);
                        blacklist_icon_imagebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                moveMap(marker.getPosition());
                            }
                        });
                        blacklist_icon_imagebtn.setBackground(null);
                        blacklist_icon_imagebtn.setImageBitmap(place_icon.get(i));

                        TextView blacklist_name_text = new TextView(BlackListActivity.this);
                        blacklist_name_text.setGravity(Gravity.CENTER_HORIZONTAL);
                        blacklist_name_text.setTypeface(null, Typeface.BOLD);
                        blacklist_name_text.setTextColor(Color.BLACK);
                        blacklist_name_text.setText(marker.getTitle());
                        blacklist_name_text.setMaxWidth(TextViewMaxWidth);
                        blacklist_name_text.setSingleLine();

                        Button cancel_btn = new Button(BlackListActivity.this);
                        cancel_btn.setBackgroundResource(R.drawable.button_background);
                        cancel_btn.setTextColor(Color.WHITE);
                        cancel_btn.setText("刪除");
                        cancel_btn.setId(btn_count++);
                        cancel_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d("black", "del name=" + blacklist_name.get(view.getId()));
                                blacklist_tablerow.removeView(blacklist_View.get(view.getId()));
                                blacklist_name.set(view.getId(), "null");
                            }
                        });

                        blacklist_info.addView(blacklist_icon_imagebtn);
                        blacklist_info.addView(blacklist_name_text);
                        blacklist_info.addView(cancel_btn);
                        blacklist_tablerow.addView(blacklist_info);
                        blacklist_View.add(blacklist_info);
                        blacklist_name.add(place_name.get(i));
                        blacklist_id.add(place_id.get(i));
                        blacklist_address.add(place_address.get(i));
                        blacklist_latlng.add(place_latlng.get(i));
                        Log.d("black", place_id.get(i));
                        Log.d("black", "size=" + blacklist_View.size());
                    }else {
                        Toast.makeText(getApplicationContext(), "已經在黑名單中", Toast.LENGTH_SHORT).show();
                    }
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
        paint.setColor(Color.rgb(0, 0, 0));
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

                if(alpha != 0)
                r = 255;
                g = 255;
                b = 255;

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

        public DrawView(Context context, int x, int y) {
            super(context);
            this.x=x;
            this.y=y;
        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Paint paint = new Paint();
            paint.setColor(Color.rgb(255, 192, 203));
            paint.setAntiAlias(true);
            canvas.drawCircle(x, y, 10, paint);
        }
    }
}
