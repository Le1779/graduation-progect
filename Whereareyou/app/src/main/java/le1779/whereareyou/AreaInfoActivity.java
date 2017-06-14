package le1779.whereareyou;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AreaInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String group_name;
    private String user_id;
    private String deadline = "null";
    double latitude;
    double longitude;
    double right_latitude;
    double right_longitude;
    double lift_latitude;
    double lift_longitude;
    int area_distance;
    String area_address;
    private ArrayAdapter<String> arrayList;
    EditText area_name_edit, area_snippet;
    TextView area_time_text;
    Switch area_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.area_info_map);
        mapFragment.getMapAsync(this);
        area_name_edit = (EditText)findViewById(R.id.area_name_editText);
        area_snippet = (EditText)findViewById(R.id.area_snippet_editText);
        Bundle bundle =this.getIntent().getExtras();
        group_name = bundle.getString("group_name");
        user_id = bundle.getString("user_id");
        latitude = bundle.getDouble("center_latitude");
        longitude = bundle.getDouble("center_longitude");
        right_latitude = bundle.getDouble("right_latitude");
        right_longitude = bundle.getDouble("right_longitude");
        lift_latitude = bundle.getDouble("lift_latitude");
        lift_longitude = bundle.getDouble("lift_longitude");
        area_distance = bundle.getInt("area_distance");
        area_address = bundle.getString("area_address");
        area_snippet.setText(area_address);
        area_time_text = (TextView)findViewById(R.id.area_time_textView);
        area_time = (Switch)findViewById(R.id.area_time_switch);
        area_time.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    Calendar c = Calendar.getInstance();
                    int day_creat_new = c.get(Calendar.DATE);
                    int hour_creat_new = c.get(Calendar.HOUR_OF_DAY);
                    int minute_creat_new = c.get(Calendar.MINUTE);
                    showAlertDialog(day_creat_new, hour_creat_new, minute_creat_new);
                    area_time_text.setVisibility(View.VISIBLE);
                } else {
                    area_time_text.setVisibility(View.INVISIBLE);
                    deadline = "null";
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_area_info, menu);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("設定區域");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AreaInfoActivity.this.finish();
                return true;

            case R.id.area_info_nextpage:
                Log.d("areaInfo", "next");
                String area_name = area_name_edit.getText().toString();
                if(area_name.equals(null)||area_name.equals("")){//if no area name
                    area_name = group_name;
                }
                DataBase dataBase = new DataBase();
                dataBase.creatGroup(group_name, "0", user_id, latitude+"s"+longitude, Integer.toString(area_distance), area_name, area_snippet.getText().toString(), deadline);
                Intent intentArea = new Intent();
                intentArea.setClass(AreaInfoActivity.this, MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("center_latitude", latitude);
                bundle.putDouble("center_longitude", longitude);
                bundle.putInt("area_distance", area_distance);
                bundle.putString("area_address", area_snippet.getText().toString());
                bundle.putString("area_name", area_name_edit.getText().toString());
                bundle.putString("area_time", area_time_text.getText().toString());
                intentArea.putExtras(bundle);
                //setResult(0, intentArea);
                startActivity(intentArea);
                if(!AreaActivity.Area.isFinishing())
                    AreaActivity.Area.finish();
                AreaInfoActivity.this.finish();
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
            LatLngBounds.Builder b = new LatLngBounds.Builder();
            b.include(new LatLng(right_latitude, right_longitude));
            b.include(new LatLng(lift_latitude, lift_longitude));
            LatLngBounds bounds = b.build();
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngBounds(bounds, 0);
            mMap.animateCamera(yourLocation);

            Circle circle_marker = mMap.addCircle(new CircleOptions()
                    .center(place)
                    .fillColor(Color.argb(200, 255, 255, 255))
                    .strokeColor(Color.rgb(255, 192, 203))
                    .strokeWidth(5)
                    .radius(area_distance/2));
            //BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(FriendtoRoundBitmap(textAsBitmap("  ", windowWidth / 10, Color.rgb(255, 192, 203)), windowWidth / 15));
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .anchor(0.5f, 0.5f)
                            //.icon(icon)
                    .position(place));
        }
    }
    private void showAlertDialog(final int d, final int h, final int m){
        Log.d("showAlertDialog", "get" + d + " " + h + " " + m + "\n");
        LayoutInflater inflater = LayoutInflater.from(AreaInfoActivity.this);
        final View v = inflater.inflate(R.layout.area_time_dialog, null);
        final NumberPicker day_numPic = (NumberPicker)v.findViewById(R.id.day_numberPicker);
        final NumberPicker hour_numPic = (NumberPicker)v.findViewById(R.id.hour_numberPicker);
        final NumberPicker minute_numPic = (NumberPicker)v.findViewById(R.id.min_numberPicker);

        day_numPic.setMaxValue(10);
        day_numPic.setMinValue(0);
        day_numPic.setValue(0);
        day_numPic.setDisplayedValues(new String[]{"今天的", "明天的", "後天的", "3天後的", "4天後的", "5天後的", "6天後的", "7天後的", "8天後的", "9天後的", "10天後的"});

        hour_numPic.setMaxValue(23);
        hour_numPic.setMinValue(0);
        hour_numPic.setValue(h);
        hour_numPic.setDisplayedValues(new String[]{"24點", "1點", "2點", "3點", "4點", "5點", "6點", "7點", "8點", "9點", "10點", "11點", "12點", "13點", "14點", "15點", "16點", "17點", "18點", "19點", "20點", "21點", "22點", "23點"});

        minute_numPic.setMaxValue(59);
        minute_numPic.setMinValue(0);
        minute_numPic.setValue(m);

        new AlertDialog.Builder(AreaInfoActivity.this)
                .setTitle("設定期限")
                .setView(v)
                .setCancelable(false)
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        area_time.setChecked(false);
                    }
                })
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int alertDialog_day = day_numPic.getValue();
                        int alertDialog_hour = hour_numPic.getValue();
                        int alertDialog_minute = minute_numPic.getValue();
                        String minute = String.valueOf(alertDialog_minute);
                        if (alertDialog_minute < 10)
                            minute = "0" + String.valueOf(alertDialog_minute);

                        Calendar c = Calendar.getInstance();
                        c.add(Calendar.DAY_OF_MONTH, alertDialog_day);
                        c.set(Calendar.HOUR_OF_DAY, alertDialog_hour);
                        c.set(Calendar.MINUTE, alertDialog_minute);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                        deadline = simpleDateFormat.format(c.getTime());
                        Toast.makeText(getApplicationContext(), simpleDateFormat.format(c.getTime()), Toast.LENGTH_SHORT).show();
                        area_time_text.setText(simpleDateFormat.format(c.getTime()));
                        //Toast.makeText(AreaInfoActivity.this, alertDialog_day + alertDialog_hour + ":" + minute, Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }
}
