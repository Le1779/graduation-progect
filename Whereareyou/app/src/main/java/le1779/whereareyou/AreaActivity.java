package le1779.whereareyou;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import le1779.whereareyou.AreaInfo.FamilyGroupInfoActivity;

public class AreaActivity extends AppCompatActivity  implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String group_name;
    private int group_type;
    private String user_id;
    private double latitude;
    private double longitude;
    private int windowWidth;
    private int windowHeight;
    private int area_metre;
    private String area_distance;
    private RelativeLayout relativeLayout;
    private LinearLayout linearLayout;
    private boolean isTouchScreen = true;
    private int seekProgress=300;
    private TextView area_meter;
    EditText search_address;
    DrawView view;
    public static  Activity Area;
    String[] groupType = {
            "自訂群組","家人群組", "寵物群組", "朋友群組", "老師群組", "黑單群組"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);
        Area=this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.area_map);
        mapFragment.getMapAsync(this);


        Bundle bundle =this.getIntent().getExtras();
        group_name = bundle.getString("group_name");
        group_type = bundle.getInt("group_type");
        user_id = bundle.getString("user_id");
        latitude = bundle.getDouble("latitude");
        longitude = bundle.getDouble("longitude");
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        windowWidth = displaymetrics.widthPixels;
        windowHeight = displaymetrics.heightPixels;
        search_address = (EditText)findViewById(R.id.address_editText);
        relativeLayout = (RelativeLayout)findViewById(R.id.area_layout);
        linearLayout =(LinearLayout)findViewById(R.id.draw_linearlayout);
        area_meter = (TextView)findViewById(R.id.area_meter_textView);
        view=new DrawView(AreaActivity.this, windowWidth/2, windowHeight/2, 300);
        view.invalidate();
        relativeLayout.addView(view);
        initSeekBar();
        initImageButton();
    }

    private void initSeekBar(){
        SeekBar seekBar = (SeekBar)findViewById(R.id.area_seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekProgress = seekBar.getProgress() + 100;
                relativeLayout.removeView(view);
                view = new DrawView(AreaActivity.this, windowWidth / 2, windowHeight / 2, seekProgress);
                view.invalidate();
                relativeLayout.addView(view);
                Point right = new Point((windowWidth / 2) + seekProgress, windowHeight / 2);
                Point lift = new Point((windowWidth / 2) - seekProgress, windowHeight / 2);
                LatLng rightLatLng = mMap.getProjection().fromScreenLocation(right);
                LatLng liftLatLng = mMap.getProjection().fromScreenLocation(lift);
                float results[] = new float[1];
                Location.distanceBetween(rightLatLng.latitude, rightLatLng.longitude, liftLatLng.latitude, liftLatLng.longitude, results);
                area_metre = (int)results[0];
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
                view = new DrawView(AreaActivity.this, windowWidth / 2, windowHeight / 2, seekProgress);
                view.invalidate();
                relativeLayout.addView(view);
                Point right = new Point((windowWidth / 2) + seekProgress, windowHeight / 2);
                Point lift = new Point((windowWidth / 2) - seekProgress, windowHeight / 2);
                LatLng rightLatLng = mMap.getProjection().fromScreenLocation(right);
                LatLng liftLatLng = mMap.getProjection().fromScreenLocation(lift);
                float results[] = new float[1];
                Location.distanceBetween(rightLatLng.latitude, rightLatLng.longitude, liftLatLng.latitude, liftLatLng.longitude, results);
                area_metre = (int)results[0];
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
                Geocoder coder = new Geocoder(AreaActivity.this);
                try {
                    ArrayList<Address> address = (ArrayList<Address>) coder.getFromLocationName(search_address.getText().toString(), 50);
                    if (address!=null) {
                        Address location=address.get(0);
                        double lat = location.getLatitude();
                        double log = location.getLongitude();
                        Log.d("Area", "" + lat);
                        LatLng place = new LatLng(lat, log);
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(place).zoom(mMap.getCameraPosition().zoom).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        ImageButton updateAddress_imgbtn = (ImageButton)findViewById(R.id.update_imageButton);
        updateAddress_imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Point center = new Point(windowWidth / 2, windowHeight / 2);
                LatLng centerLatLng = mMap.getProjection().fromScreenLocation(center);
                Geocoder geocoder = new Geocoder(AreaActivity.this, Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(centerLatLng.latitude, centerLatLng.longitude, 1);
                    String address = addresses.get(0).getAddressLine(0);
                    search_address.setText(address);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_area, menu);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("設定 " + group_name + " 的區域");
            actionBar.setTitle("設定 " + groupType[group_type] + " 的區域");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AreaActivity.this.finish();
                return true;

            case R.id.area_nextpage:
                Point center = new Point(windowWidth / 2, windowHeight / 2);
                LatLng centerLatLng = mMap.getProjection().fromScreenLocation(center);
                Point right = new Point((windowWidth / 2) + seekProgress, windowHeight / 2);
                Point lift = new Point((windowWidth / 2) - seekProgress, windowHeight / 2);
                LatLng rightLatLng = mMap.getProjection().fromScreenLocation(right);
                LatLng liftLatLng = mMap.getProjection().fromScreenLocation(lift);
                Geocoder geocoder = new Geocoder(AreaActivity.this, Locale.getDefault());
                List<Address> addresses;
                String area_address = null;
                try {
                    addresses = geocoder.getFromLocation(centerLatLng.latitude, centerLatLng.longitude, 1);
                    area_address = addresses.get(0).getAddressLine(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intentArea = new Intent();
                if(group_type==0){
                    //自訂群組
                    Log.d("Area", "自訂群組");
                    intentArea.setClass(AreaActivity.this, AreaInfoActivity.class);
                } else if(group_type==1){
                    //家人群組
                    Log.d("Area", "家人群組");
                    intentArea.setClass(AreaActivity.this, FamilyGroupInfoActivity.class);
                } else if(group_type==2){
                    //寵物群組
                    Log.d("Area", "寵物群組");
                    intentArea.setClass(AreaActivity.this, AreaInfoActivity.class);
                } else if(group_type==3){
                    //朋友群組
                    Log.d("Area", "朋友群組");
                    intentArea.setClass(AreaActivity.this, AreaInfoActivity.class);
                } else if(group_type==4){
                    //老師群組
                    Log.d("Area", "老師群組");
                    intentArea.setClass(AreaActivity.this, AreaInfoActivity.class);
                } else if(group_type==5){
                    //黑單群組
                    Log.d("Area", "黑單群組");
                    intentArea.setClass(AreaActivity.this, AreaInfoActivity.class);
                }

                Bundle bundle = new Bundle();
                bundle.putString("group_name", group_name);
                bundle.putString("user_id", user_id);
                bundle.putDouble("center_latitude", centerLatLng.latitude );
                bundle.putDouble("center_longitude", centerLatLng.longitude);
                bundle.putDouble("right_latitude", rightLatLng.latitude );
                bundle.putDouble("right_longitude", rightLatLng.longitude);
                bundle.putDouble("lift_latitude", liftLatLng.latitude );
                bundle.putDouble("lift_longitude", liftLatLng.longitude);
                bundle.putInt("area_distance", area_metre);
                bundle.putString("area_address", area_address);

                intentArea.putExtras(bundle);
                startActivity(intentArea);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null) {
            LatLng place = new LatLng(latitude, longitude);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(place).zoom(15).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            mMap.addMarker(new MarkerOptions()
                    .anchor(0.5f, 0.5f)
                    .title("你的位置")
                    .position(place));
            Geocoder geocoder = new Geocoder(AreaActivity.this, Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocation(place.latitude, place.longitude, 1);
                String address = addresses.get(0).getAddressLine(0);
                search_address.setText(address);
                //area_meter.setText(address);
            } catch (IOException e) {
                e.printStackTrace();
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
            paint.setColor(Color.rgb(255, 192, 203));
            paint.setAntiAlias(true);
            canvas.drawCircle(x, y, 10, paint);
            canvas.drawCircle(x-m, y, 10, paint);
            canvas.drawCircle(x + m, y, 10, paint);
            //Log.d("draw", "draw");
        }
    }
}