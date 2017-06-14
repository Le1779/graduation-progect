package le1779.whereareyou;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.text.SimpleDateFormat;

public class GpsService extends Service implements LocationListener {
    public GpsService() {
    }

    int count = 0;
    String time = "";
    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 30 * 1; // 1 minute
    // Declaring a Location Manager
    protected LocationManager locationManager;
    private String TAG = "GPSTracker";
    private String user_name, user_email, user_id;
    private boolean isNameGet = false;
    DataBase dataBase = new DataBase();
    Data data = new Data();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getLocation();
        if (data.readfile(this, "UserData")) {
            user_email = data.getEmail();
            user_name = data.getName();
            user_id = data.getId();
            isNameGet = true;
        }
        return super.onStartCommand(intent, flags, startId);
    }
    private String getTimeString(long timeInMilliseconds){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(timeInMilliseconds);
    }
    public Location getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
            time = getTimeString(location.getTime());
            Intent intent = new Intent("FilterString");
            intent.putExtra("Lat", location.getLatitude());
            intent.putExtra("Long", location.getLongitude());
            intent.putExtra("Accuracy", location.getAccuracy());
            intent.putExtra("Bearing", location.getBearing());
            intent.putExtra("Speed", location.getSpeed());
            intent.putExtra("Time", time);//location.getTime()
            intent.putExtra("Count", count);
            sendBroadcast(intent);
            count++;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS() {

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(this);
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){

    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        Log.d("Gps", "onLocationChanged");
        getLatitude();
        getLongitude();
        time = getTimeString(location.getTime());
        Intent intent = new Intent("FilterString");
        intent.putExtra("Lat", location.getLatitude());
        intent.putExtra("Long", location.getLongitude());
        intent.putExtra("Accuracy", location.getAccuracy());
        intent.putExtra("Bearing", location.getBearing());
        intent.putExtra("Speed", location.getSpeed());
        intent.putExtra("Time", time);//location.getTime()
        intent.putExtra("Count", count);
        sendBroadcast(intent);
        count++;
        Log.d("Gps", "count" + Integer.toString(count));
        if(isNameGet){
            String coordinate = String.valueOf(location.getLatitude()) + "s" +String.valueOf(location.getLongitude());
            dataBase.updateDB(user_name, user_id, coordinate, user_email, time);
        }

        //new Thread(Notification_runnable).start();
        /*
        SQLiteDatabase sqLiteDatabase = LocalDataBase.getDatabase(this);
        Cursor cursor_group = sqLiteDatabase.query(true,
                "group_table",//資料表名稱
                new String[]{"g_id"},//欄位名稱
                "member_in=1",//WHERE
                null, // WHERE 的參數
                null, // GROUP BY
                null, // HAVING
                null, // ORDOR BY
                null  // 限制回傳的rows數量
        );
        Cursor cursor = sqLiteDatabase.query(true,
                "area_table",//資料表名稱
                new String[]{"area_lat", "area_long", "area_meter", "area_name", "g_id", "a_id"},//欄位名稱
                null,//WHERE
                null, // WHERE 的參數
                null, // GROUP BY
                null, // HAVING
                null, // ORDOR BY
                null  // 限制回傳的rows數量
        );
        LatLng now_latLng = new LatLng(location.getLatitude(), location.getLongitude());
        String output = "";
        if(cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                LatLng area_latLng = new LatLng(cursor.getDouble(0), cursor.getDouble(1));
                Log.d("Gps", "area_latLng" + cursor.getDouble(0)+"s"+cursor.getDouble(1));
                float results[] = new float[1];
                Location.distanceBetween(now_latLng.latitude, now_latLng.longitude, area_latLng.latitude, area_latLng.longitude, results);
                int metre = (int)results[0];
                if(cursor.getDouble(2)/2>metre) {
                    Log.d("Gps", "in" + cursor.getString(3));
                    output = output + "," +cursor.getString(3);
                    dataBase.updateArea(cursor.getString(5), user_id, "1");
                    //通知自己在哪個群組的哪個區域內
                }else{
                    //通知自己不在哪個群組的哪個區域內
                    dataBase.updateArea(cursor.getString(5), user_id, "0");
                }
                cursor.moveToNext();
            }

            Log.d("Gps", output);
        }

        Log.e("Gps", "Notification");
        Notification.InboxStyle inboxStyle = new Notification.InboxStyle();
        inboxStyle.setBigContentTitle("陳弘 以離線");
        inboxStyle.addLine("成員以離線10分鐘以上，無法取得成員真實位置。");

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.where_are_you_icon);
        try {
            long[] vibratepattern = {0, 100};//setVibrate時使用
            int notifyID = 1; // 通知的識別號碼
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // 通知音效的URI，在這裡使用系統內建的通知音效
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
            @SuppressWarnings("deprecation")
            Notification notification = new Notification.Builder(getApplicationContext())
                    .setSmallIcon(R.mipmap.where_are_you_icon)
                    .setLargeIcon(icon)
                    .setContentTitle("陳弘 以離線")
                    .setContentText("成員以離線10分鐘以上，無法取得真實位置。")
                    .setSound(soundUri)
                    .setVibrate(vibratepattern)//振動頻率
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setStyle(inboxStyle)
                    .build(); // 建立通知
            notificationManager.notify(notifyID, notification); // 發送通知
        } catch (Exception e){
            Log.e("Gps", e.toString());
        }
        */
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    Runnable initGroup_runnable = new Runnable() {
        @Override
        public void run() {
            dataBase.searchGroup(user_id, GpsService.this);
            while(dataBase.isSearchGroup){
                try {
                    Log.d(TAG, "wait");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            initGroup_handler.sendEmptyMessage(0);
        }
    };
    Handler initGroup_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            new Thread(initMember_runnable).start();
        }
    };
    Runnable initMember_runnable = new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < dataBase.group_id_list.size(); i++) {
                dataBase.getGroupMember(dataBase.group_id_list.get(i), user_id, GpsService.this);
            }
            while(dataBase.isgetMember){
                try {
                    Log.d(TAG, "wait get member");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "get member");
            intitMember_handler.sendEmptyMessage(0);
        }
    };
    Handler intitMember_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };

    Runnable Notification_runnable = new Runnable() {
        @Override
        public void run() {
            dataBase.getMemberIsIn(user_id);
            while(dataBase.isgetMemberIsIn){
                try {
                    Log.d(TAG, "Notification_wait");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Notification_handler.sendEmptyMessage(0);
        }
    };
    Handler Notification_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e("Gps", "Notification");
            Notification.InboxStyle inboxStyle = new Notification.InboxStyle();
            inboxStyle.setBigContentTitle("區域內成員更動");
            if(dataBase.Notification_output.size()>5) {
                inboxStyle.setSummaryText("更多新訊息(" + (dataBase.Notification_output.size()-5) + "+)");
            }
            for (int i=0; i < dataBase.Notification_output.size(); i++) {
                inboxStyle.addLine(dataBase.Notification_output.get(i));
            }
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.where_are_you_icon);
            try {
                long[] vibratepattern = {0, 100};//setVibrate時使用
                int notifyID = 1; // 通知的識別號碼
                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // 通知音效的URI，在這裡使用系統內建的通知音效
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
                @SuppressWarnings("deprecation")
                Notification notification = new Notification.Builder(getApplicationContext())
                        .setSmallIcon(R.mipmap.where_are_you_icon)
                        .setLargeIcon(icon)
                        .setContentTitle("黑單群組")
                        //.setContentText("下拉檢視" + dataBase.Notification_output.size() + "個訊息")
                        .setContentText("你有寵物離開安全區域")
                        .setSound(soundUri)
                        .setVibrate(vibratepattern)//振動頻率
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setStyle(inboxStyle)
                        .build(); // 建立通知
                notificationManager.notify(notifyID, notification); // 發送通知
            } catch (Exception e){
                Log.e("Gps", e.toString());
            }
        }
    };
}
