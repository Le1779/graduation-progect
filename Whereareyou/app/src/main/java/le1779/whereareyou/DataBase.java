package le1779.whereareyou;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by kevin on 2016/1/31.
 */
public class DataBase {
    private String DBhost = "http://le1779.bixone.com/where_are_you/";
    private String TAG = "DataBaswe";
    private String user_id, user_name, user_email, user_coordinate, user_date, user_password;
    public String search_friend_name;
    public String search_friend_email;
    public String search_friend_id;
    public Bitmap search_fridnd_pic;
    public ArrayList<String> friend = new ArrayList<String>();
    public ArrayList<String> friend_id = new ArrayList<String>();
    public ArrayList<String> friend_coordinate = new ArrayList<String>();
    public ArrayList<String> friend_date = new ArrayList<String>();
    public ArrayList<Bitmap> friend_pic = new ArrayList<Bitmap>();

    public ArrayList<String> group_name_list = new ArrayList<>();
    public ArrayList<String> group_id_list = new ArrayList<>();
    public ArrayList<String> group_user_list = new ArrayList<>();
    public ArrayList<String> group_user_id_list = new ArrayList<>();
    public ArrayList<String> group_in_list = new ArrayList<>();
    public ArrayList<String> group_type_list = new ArrayList<>();
    //blacklist
    public ArrayList<String> blacklist_id = new ArrayList<>();
    public ArrayList<String> blacklist_name = new ArrayList<>();
    public ArrayList<String> blacklist_address = new ArrayList<>();
    public ArrayList<LatLng> blacklist_latlng = new ArrayList<>();
    //class
    public ArrayList<String> class_deadline = new ArrayList<>();
    public ArrayList<String> class_meter = new ArrayList<>();
    public ArrayList<LatLng> class_latlng = new ArrayList<>();
    //date
    public ArrayList<String> date_deadline = new ArrayList<>();
    public ArrayList<String> date_meter = new ArrayList<>();
    public ArrayList<LatLng> date_latlng = new ArrayList<>();
    public ArrayList<String> date_name = new ArrayList<>();
    public ArrayList<String> date_address = new ArrayList<>();
    //activity
    public ArrayList<String> activity_meter = new ArrayList<>();
    public ArrayList<LatLng> activity_latlng = new ArrayList<>();
    public ArrayList<String> activity_address = new ArrayList<>();
    public ArrayList<String> pet_update_time = new ArrayList<>();
    public ArrayList<LatLng> pet_latlng = new ArrayList<>();
    public ArrayList<String> pet_history_update_time = new ArrayList<>();
    public ArrayList<LatLng> pet_history_latlng = new ArrayList<>();
    //home
    public ArrayList<String> home_deadline = new ArrayList<>();
    public ArrayList<String> home_meter = new ArrayList<>();
    public ArrayList<LatLng> home_latlng = new ArrayList<>();

    public String search_pet_imei;
    public String[] area = new String[5];
    private HandlerThread mThread;
    //繁重執行序用的 (時間超過3秒的)
    private Handler mThreadHandler;
    public boolean checkDone = false;
    public boolean isCreatData = false;
    public boolean isDataExist = false;
    public boolean isCorrect = false;
    public boolean isSearchDone = false;
    public boolean isSearchGroup = false;
    public boolean isCreatGroup = false;
    public boolean isDeleteGroup = false;
    public boolean isJoinGroup = false;
    public boolean isgetMember = false;
    public boolean isSearchfriend = false;
    public boolean isgetMemberIsIn = false;
    public boolean iscreatBlackListGroup = false;
    public boolean isgetBlackList = false;
    public boolean iscreatTeacherGroup = false;
    public boolean isgetClass = false;
    public boolean iscreatFriendGroup = false;
    public boolean isgetFriend = false;
    public boolean iscreatPetGroup = false;
    public boolean issearchPet = false;
    public boolean isgetPet = false;
    public boolean iscreatFamilyGroup = false;
    public boolean isgetFamily = false;







    public String JoinGroupResult;
    public ArrayList<String>  Notification_output = new ArrayList<>();


    public void updateDB(final String name, final String id, final String coordinate, final String email, final String date) {
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                String jsonString = executeQuery("UPDATE  `my_friend` SET  `name` = '" + name + "',`FB_ID` = '" + id +"',`coordinate` = '"+ coordinate +"',`date` =  '" + date + "' WHERE `email`  = '" + email + "'");
                //mThread.getLooper().quit();
                Looper.myLooper().quit();
            }
        });
    }
    public void checkDB(final String email) {
        checkDone = false;
        isDataExist = false;
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                String jsonString = executeQuery("SELECT *  FROM `my_friend` WHERE `email` LIKE '" + email + "'");
                String lines[] = jsonString.split("\\r?\\n");
                Log.d(TAG, lines[0]);
                if (lines[0].equals("null")) {
                    Log.d(TAG, "DB data found!");
                    isDataExist = false;
                } else {
                    Log.d(TAG, "DB data found!");
                    isDataExist = true;
                }
                checkDone = true;
                Looper.myLooper().quit();
            }
        });
    }
    public void creatData(final String email, final String name, final String id, final String coordinate, final String date, final String password){
        checkDone = false;
        isCreatData = false;
        isDataExist = false;
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub

                String jsonString = executeQuery("SELECT *  FROM `my_friend` WHERE `email` LIKE '" + email + "'");
                String lines[] = jsonString.split("\\r?\\n");
                Log.d(TAG, lines[0]);
                if (lines[0].equals("null")) {
                    Log.d(TAG, "DB data no found!");
                    jsonString = executeQuery("INSERT INTO  `my_friend` (`name` ,`FB_ID` ,`email` ,`coordinate` ,`date` ,`password`,`group`)" +
                            "VALUES ('" + name + "',  '" + id + "',  '" + email + "',  '" + coordinate + "',  '" + date + "',  '" + password + "',  '" + "1" + "');"
                    );
                    isDataExist = true;
                    isCreatData = true;
                } else {
                    isDataExist = false;
                    isCreatData = false;
                    Log.d(TAG, "DB data found!");
                }
                checkDone = true;
                Looper.myLooper().quit();
            }
        });
        //String jsonString = executeQuery("INSERT INTO  `a1643572_Friend`.`my_friend` (`name` ,`FB_ID` ,`email` ,`coordinate` ,`date` ,`password`)" +
        //                "VALUES ('" + name + "',  '" + id + "',  '" + email + "',  '" + coordinate + "',  '" + date + "',  '"+ password +"');"
        //);

    }
    public void searchDB(final String group){
        isSearchDone = false;
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub

                String jsonString = executeQuery("SELECT *  FROM `my_friend` WHERE `group` LIKE '" + group + "'");
                try {
                    JSONArray jArray = new JSONArray(jsonString);
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        String email = json_data.getString("email");
                        friend.add(email);
                        String id = json_data.getString("FB_ID");
                        friend_id.add(id);
                        String coordinate = json_data.getString("coordinate");
                        friend_coordinate.add(coordinate);
                        try {
                            URL url = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                            friend_pic.add(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.i("friend", email);
                        Log.d("friend", id);
                        Log.d("friend", "https://graph.facebook.com/" + id + "/picture?type=large");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                isSearchDone = true;
                Looper.myLooper().quit();
            }
        });
    }

    private String executeQuery(String query) {
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(DBhost+"test.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("query", query));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

    public void creatGroup(final String group_name, final String group_type,final String user_id, final String area_coordinate, final String area_meter, final String area_name, final String area_snippet, final String area_deadline){
        Log.d(TAG, group_name + "\n" + user_id + "\n" + area_coordinate + "\n" + area_meter + "\n" + area_name + "\n" + area_snippet + "\n" + area_deadline);
        isCreatGroup = true;
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(DBhost+"group.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("group_name", group_name));
                    nameValuePairs.add(new BasicNameValuePair("group_type", group_type));
                    nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
                    nameValuePairs.add(new BasicNameValuePair("area_coordinate", area_coordinate));
                    nameValuePairs.add(new BasicNameValuePair("area_meter", area_meter));
                    nameValuePairs.add(new BasicNameValuePair("area_name", area_name));
                    nameValuePairs.add(new BasicNameValuePair("area_snippet", area_snippet));
                    nameValuePairs.add(new BasicNameValuePair("area_deadline", area_deadline));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                    httpClient.execute(post);
                } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }
                isCreatGroup = false;
                Looper.myLooper().quit();
            }
        });
    }
    public void creatFamilyGroup(final String group_name,final String user_id, final String latitude, final String longitude, final String meter, final String deadline){
        Log.d(TAG, group_name + "\n" + user_id);
        iscreatFamilyGroup = true;
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(DBhost+"family_group_creat.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("group_name", group_name));
                    nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
                    nameValuePairs.add(new BasicNameValuePair("latitude", latitude));
                    nameValuePairs.add(new BasicNameValuePair("longitude", longitude));
                    nameValuePairs.add(new BasicNameValuePair("meter", meter));
                    nameValuePairs.add(new BasicNameValuePair("class_time", deadline));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                    httpClient.execute(post);
                } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }
                iscreatFamilyGroup = false;
                Looper.myLooper().quit();
            }
        });
    }
    public void getFamilyGroup(final String group_id, final String user_id){
        Log.d(TAG, "group_id" + group_id);
        isgetFamily = true;
        friend = new ArrayList<>();
        friend_id = new ArrayList<>();
        friend_pic = new ArrayList<>();
        friend_coordinate = new ArrayList<>();
        friend_date = new ArrayList<>();

        home_deadline = new ArrayList<>();
        home_meter = new ArrayList<>();
        home_latlng = new ArrayList<>();
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(DBhost+"family_group_get.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("group_id", group_id));
                    nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

                    String jsonString = builder.toString();
                    try {
                        JSONArray jArray = new JSONArray(jsonString);
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jsonObject = jArray.getJSONObject(i);
                            if(i==0) {
                                Log.d("get friend", jsonObject.getString("H_latitude"));
                                Log.d("get friend", jsonObject.getString("H_longitude"));
                                Log.d("get friend", jsonObject.getString("H_meter"));
                                Log.d("get friend", jsonObject.getString("H_deadline"));

                                home_deadline.add(jsonObject.getString("H_deadline"));
                                home_meter.add(jsonObject.getString("H_meter"));
                                home_latlng.add(new LatLng(Double.valueOf(jsonObject.getString("H_latitude")), Double.valueOf(jsonObject.getString("H_longitude"))));
                            }else {
                                Log.i("fridne", jsonObject.getString("FB_ID"));
                                String id = jsonObject.getString("FB_ID");
                                if(!id.equals(user_id)) {
                                    String name = jsonObject.getString("name");
                                    String email = jsonObject.getString("email");
                                    String coordinate = jsonObject.getString("coordinate");
                                    String date = jsonObject.getString("date");
                                    Log.d(TAG, name + "\n" + email + "\n" + coordinate + "\n" + date);
                                    friend.add(name);
                                    friend_id.add(id);
                                    friend_coordinate.add(coordinate);
                                    friend_date.add(date);
                                    try {
                                        URL url = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                                        friend_pic.add(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Log.i("friend", email);
                                    Log.d("friend", id);
                                    Log.d("friend", "https://graph.facebook.com/" + id + "/picture?type=large");
                                    Log.d("friend", ""+friend.size());
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }
                isgetFamily = false;
                Looper.myLooper().quit();
            }
        });
    }
    public void delFamilyGroup(final String group_id, final String user_id){
        isDeleteGroup = true;
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(DBhost+"del_friend_group.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    Log.d("del group", "" + group_id + user_id);
                    nameValuePairs.add(new BasicNameValuePair("group_id", group_id));
                    nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                    httpClient.execute(post);
                } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }
                isDeleteGroup = false;
                Looper.myLooper().quit();
            }
        });
    }
    public void creatFriendGroup(final String group_name,final String user_id, final String latitude, final String longitude, final String meter, final String deadline, final String date_name, final String date_address){
        Log.d(TAG, group_name + "\n" + user_id);
        iscreatFriendGroup = true;
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(DBhost+"friend_group_creat.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("group_name", group_name));
                    nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
                    nameValuePairs.add(new BasicNameValuePair("latitude", latitude));
                    nameValuePairs.add(new BasicNameValuePair("longitude", longitude));
                    nameValuePairs.add(new BasicNameValuePair("meter", meter));
                    nameValuePairs.add(new BasicNameValuePair("class_time", deadline));
                    nameValuePairs.add(new BasicNameValuePair("name", date_name));
                    nameValuePairs.add(new BasicNameValuePair("address", date_address));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                    httpClient.execute(post);
                } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }
                iscreatFriendGroup = false;
                Looper.myLooper().quit();
            }
        });
    }
    public void getFriendGroup(final String group_id, final String user_id){
        Log.d(TAG, "group_id" + group_id);
        isgetFriend = true;
        friend = new ArrayList<>();
        friend_id = new ArrayList<>();
        friend_pic = new ArrayList<>();
        friend_coordinate = new ArrayList<>();

        date_deadline = new ArrayList<>();
        date_meter = new ArrayList<>();
        date_latlng = new ArrayList<>();
        date_name = new ArrayList<>();
        date_address = new ArrayList<>();
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(DBhost+"friend_group_get.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("group_id", group_id));
                    nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

                    String jsonString = builder.toString();
                    try {
                        JSONArray jArray = new JSONArray(jsonString);
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jsonObject = jArray.getJSONObject(i);
                            if(i==0) {
                                Log.d("get friend", jsonObject.getString("D_latitude"));
                                Log.d("get friend", jsonObject.getString("D_longitude"));
                                Log.d("get friend", jsonObject.getString("D_meter"));
                                Log.d("get friend", jsonObject.getString("D_deadline"));

                                date_deadline.add(jsonObject.getString("D_deadline"));
                                date_meter.add(jsonObject.getString("D_meter"));
                                date_latlng.add(new LatLng(Double.valueOf(jsonObject.getString("D_latitude")), Double.valueOf(jsonObject.getString("D_longitude"))));
                                date_name.add(jsonObject.getString("D_Name"));
                                date_address.add(jsonObject.getString("D_Address"));
                            }else {
                                Log.i("fridne", jsonObject.getString("FB_ID"));
                                String id = jsonObject.getString("FB_ID");
                                if(!id.equals(user_id)) {
                                    String name = jsonObject.getString("name");
                                    String email = jsonObject.getString("email");
                                    String coordinate = jsonObject.getString("coordinate");
                                    String date = jsonObject.getString("date");
                                    Log.d(TAG, name + "\n" + email + "\n" + coordinate + "\n" + date);
                                    friend.add(name);
                                    friend_id.add(id);
                                    friend_coordinate.add(coordinate);
                                    try {
                                        URL url = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                                        friend_pic.add(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Log.i("friend", email);
                                    Log.d("friend", id);
                                    Log.d("friend", "https://graph.facebook.com/" + id + "/picture?type=large");
                                    Log.d("friend", ""+friend.size());
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }
                isgetFriend = false;
                Looper.myLooper().quit();
            }
        });
    }
    public void delFriendGroup(final String group_id, final String user_id){
        isDeleteGroup = true;
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(DBhost+"del_friend_group.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    Log.d("del group", "" + group_id + user_id);
                    nameValuePairs.add(new BasicNameValuePair("group_id", group_id));
                    nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                    httpClient.execute(post);
                } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }
                isDeleteGroup = false;
                Looper.myLooper().quit();
            }
        });
    }
    public void creatTeacherGroup(final String group_name,final String user_id, final String latitude, final String longitude, final String meter, final String class_time){
        Log.d(TAG, group_name + "\n" + user_id);
        iscreatTeacherGroup = true;
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(DBhost+"teacher_group_creat.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("group_name", group_name));
                    nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
                    nameValuePairs.add(new BasicNameValuePair("latitude", latitude));
                    nameValuePairs.add(new BasicNameValuePair("longitude", longitude));
                    nameValuePairs.add(new BasicNameValuePair("meter", meter));
                    nameValuePairs.add(new BasicNameValuePair("class_time", class_time));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                    httpClient.execute(post);
                } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }
                iscreatTeacherGroup = false;
                Looper.myLooper().quit();
            }
        });
    }
    public void getTeacherGroup(final String group_id, final String user_id){
        Log.d(TAG, "group_id" + group_id);
        isgetClass = true;
        friend = new ArrayList<>();
        friend_id = new ArrayList<>();
        friend_pic = new ArrayList<>();
        friend_coordinate = new ArrayList<>();

        class_deadline = new ArrayList<>();
        class_meter = new ArrayList<>();
        class_latlng = new ArrayList<>();
        area = new String[5];
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(DBhost+"teacher_group_get.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("group_id", group_id));
                    nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

                    String jsonString = builder.toString();
                    Log.i("result", jsonString.substring(jsonString.indexOf(']') + 1));
                    try {
                        JSONArray jArray = new JSONArray(jsonString);
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jsonObject = jArray.getJSONObject(i);
                            if(i==0) {
                                Log.d("get teacher", jsonObject.getString("C_latitude"));
                                Log.d("get teacher", jsonObject.getString("C_longitude"));
                                Log.d("get teacher", jsonObject.getString("C_meter"));
                                Log.d("get teacher", jsonObject.getString("C_deadline"));

                                class_deadline.add(jsonObject.getString("C_deadline"));
                                class_meter.add(jsonObject.getString("C_meter"));
                                class_latlng.add(new LatLng(Double.valueOf(jsonObject.getString("C_latitude")), Double.valueOf(jsonObject.getString("C_longitude"))));
                            }else {
                                Log.i("fridne", jsonObject.getString("FB_ID"));
                                String id = jsonObject.getString("FB_ID");
                                if(!id.equals(user_id)) {
                                    String name = jsonObject.getString("name");
                                    String email = jsonObject.getString("email");
                                    String coordinate = jsonObject.getString("coordinate");
                                    String date = jsonObject.getString("date");
                                    Log.d(TAG, name + "\n" + email + "\n" + coordinate + "\n" + date);
                                    friend.add(name);
                                    friend_id.add(id);
                                    friend_coordinate.add(coordinate);
                                    try {
                                        URL url = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                                        friend_pic.add(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Log.i("friend", email);
                                    Log.d("friend", id);
                                    Log.d("friend", "https://graph.facebook.com/" + id + "/picture?type=large");
                                    Log.d("friend", ""+friend.size());
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }
                isgetClass = false;
                Looper.myLooper().quit();
            }
        });
    }
    public void delTeacherGroup(final String group_id, final String user_id){
        isDeleteGroup = true;
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(DBhost+"del_teacher_group.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    Log.d("del group", "" + group_id + user_id);
                    nameValuePairs.add(new BasicNameValuePair("group_id", group_id));
                    nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                    httpClient.execute(post);
                } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }
                isDeleteGroup = false;
                Looper.myLooper().quit();
            }
        });
    }
    public void creatPetGroup(final String group_name,final String user_id, final String latitude, final String longitude, final String meter, final String address){
        Log.d(TAG, group_name + "\n" + user_id);
        iscreatPetGroup = true;
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(DBhost+"pet_group_creat.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("group_name", group_name));
                    nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
                    nameValuePairs.add(new BasicNameValuePair("latitude", latitude));
                    nameValuePairs.add(new BasicNameValuePair("longitude", longitude));
                    nameValuePairs.add(new BasicNameValuePair("meter", meter));
                    nameValuePairs.add(new BasicNameValuePair("address", address));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                    httpClient.execute(post);
                } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }
                iscreatPetGroup = false;
                Looper.myLooper().quit();
            }
        });
    }
    public void getPetGroup(final String group_id, final String user_id){
        Log.d(TAG, "group_id" + group_id);
        isgetPet = true;
        pet_update_time = new ArrayList<>();
        pet_latlng = new ArrayList<>();
        pet_history_update_time = new ArrayList<>();
        pet_history_latlng = new ArrayList<>();

        activity_address = new ArrayList<>();
        activity_meter = new ArrayList<>();
        activity_latlng = new ArrayList<>();
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(DBhost+"pet_group_get.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("group_id", group_id));
                    nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

                    String jsonString = builder.toString();
                    try {
                        JSONArray jArray = new JSONArray(jsonString);
                        Log.i("result", jsonString);
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jsonObject = jArray.getJSONObject(i);
                            if(i==0) {
                                activity_address.add(jsonObject.getString("P_address"));
                                activity_meter.add(jsonObject.getString("P_meter"));
                                activity_latlng.add(new LatLng(Double.valueOf(jsonObject.getString("P_latitude")), Double.valueOf(jsonObject.getString("P_longitude"))));
                            }else if(i==1){
                                pet_update_time.add(jsonObject.getString("arduino_date"));
                                pet_latlng.add(new LatLng(Double.valueOf(jsonObject.getString("arduino_latitude")), Double.valueOf(jsonObject.getString("arduino_longitude"))));
                            }else{
                                pet_history_latlng.add(new LatLng(Double.valueOf(jsonObject.getString("A_latitude")), Double.valueOf(jsonObject.getString("A_longitude"))));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }
                isgetPet = false;
                Looper.myLooper().quit();
            }
        });
    }
    public void delPetGroup(final String group_id, final String user_id){
        isDeleteGroup = true;
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(DBhost+"del_teacher_group.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    Log.d("del group", "" + group_id + user_id);
                    nameValuePairs.add(new BasicNameValuePair("group_id", group_id));
                    nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                    httpClient.execute(post);
                } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }
                isDeleteGroup = false;
                Looper.myLooper().quit();
            }
        });
    }
    public void searchPet(final String PetIMEI){
        issearchPet = true;
        group_name_list = new ArrayList<>();
        group_id_list = new ArrayList<>();
        group_user_list = new ArrayList<>();
        group_user_id_list = new ArrayList<>();
        group_in_list = new ArrayList<>();
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(DBhost+"search_pet.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("imei", PetIMEI));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

                    String jsonString = builder.toString();
                    Log.i("result", jsonString);

                    try {
                        JSONArray jArray = new JSONArray(jsonString);
                        JSONObject json_data = jArray.getJSONObject(0);
                        search_pet_imei = json_data.getString("arduino_IMEI");
                        Log.i("result", search_pet_imei);
                    } catch (JSONException e) {
                        Log.i("result", "friend null");
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }
                issearchPet = false;
                Looper.myLooper().quit();
            }
        });
    }
    public void creatBlackListGroup(final String group_name,final String user_id, final String json){
        Log.d(TAG, group_name + "\n" + user_id);
        iscreatBlackListGroup = true;
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(DBhost+"black_list_group_creat.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("group_name", group_name));
                    nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
                    nameValuePairs.add(new BasicNameValuePair("json", json));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                    httpClient.execute(post);
                } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }
                iscreatBlackListGroup = false;
                Looper.myLooper().quit();
            }
        });
    }

    public void getBlackListGroup(final String group_id, Context context){
        Log.d(TAG, "group_id" + group_id);
        isgetBlackList = true;
        blacklist_id = new ArrayList<>();
        blacklist_name = new ArrayList<>();
        blacklist_address = new ArrayList<>();
        blacklist_latlng = new ArrayList<>();
        area = new String[5];
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(DBhost+"black_list_get.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("group_id", group_id));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

                    String jsonString = builder.toString();
                    Log.i("result", jsonString);
                    try {
                        JSONArray jArray = new JSONArray(jsonString);
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jsonObject = jArray.getJSONObject(i);
                            Log.d("black", jsonObject.getString("B_place_id"));
                            Log.d("black", jsonObject.getString("B_name"));
                            Log.d("black", jsonObject.getString("B_address"));
                            blacklist_id.add(jsonObject.getString("B_place_id"));
                            blacklist_name.add(jsonObject.getString("B_name"));
                            blacklist_address.add(jsonObject.getString("B_address"));
                            blacklist_latlng.add(new LatLng(Double.valueOf(jsonObject.getString("B_latitude")), Double.valueOf(jsonObject.getString("B_longitude"))));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }
                isgetBlackList = false;
                Looper.myLooper().quit();
            }
        });
    }
    public void getBlackListMember(final String group_id, final String user_id){
        Log.d(TAG, "group_id" + group_id);
        isgetMember = true;
        friend = new ArrayList<>();
        friend_id = new ArrayList<>();
        friend_pic = new ArrayList<>();
        friend_coordinate = new ArrayList<>();
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(DBhost+"get_member.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("group_id", group_id));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

                    String jsonString = builder.toString();
                    Log.i("result", jsonString);
                    try {
                        JSONArray jArray = new JSONArray(jsonString);
                        JSONObject json_data = jArray.getJSONObject(0);
                        JSONArray json_user_array =   json_data.getJSONArray("U");
                        Log.i("fridne", json_user_array.toString());
                        JSONObject json_user_array_data;
                        for (int i = 0; i < json_user_array.length(); i++) {
                            json_user_array_data = json_user_array.getJSONObject(i);
                            Log.i("fridne", json_user_array_data.getString("FB_ID"));
                            String id = json_user_array_data.getString("FB_ID");
                            if(!id.equals(user_id)) {
                                String name = json_user_array_data.getString("name");
                                String email = json_user_array_data.getString("email");
                                String coordinate = json_user_array_data.getString("coordinate");
                                String date = json_user_array_data.getString("date");
                                Log.d(TAG, name + "\n" + email + "\n" + coordinate + "\n" + date);
                                friend.add(name);
                                friend_id.add(id);
                                friend_coordinate.add(coordinate);
                                try {
                                    URL url = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                                    friend_pic.add(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Log.i("friend", email);
                                Log.d("friend", id);
                                Log.d("friend", "https://graph.facebook.com/" + id + "/picture?type=large");
                                Log.d("friend", ""+friend.size());
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }
                isgetMember = false;
                Looper.myLooper().quit();
            }
        });
    }
    public void delBlackListGroup(final String group_id, final String user_id){
        isDeleteGroup = true;
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(DBhost+"del_blacklist_group.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    Log.d("del group", "" + group_id + user_id);
                    nameValuePairs.add(new BasicNameValuePair("group_id", group_id));
                    nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                    httpClient.execute(post);
                } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }
                isDeleteGroup = false;
                Looper.myLooper().quit();
            }
        });
    }

    public void searchGroup(final String user_id, Context context){
        final SQLiteDatabase sqLiteDatabase = LocalDataBase.getDatabase(context);
        isSearchGroup = true;
        group_name_list = new ArrayList<>();
        group_id_list = new ArrayList<>();
        group_user_list = new ArrayList<>();
        group_user_id_list = new ArrayList<>();
        group_in_list = new ArrayList<>();
        group_type_list = new ArrayList<>();
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(DBhost+"search_group_test.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

                    String jsonString = builder.toString();
                    Log.i("result", jsonString);
                    try {
                        JSONArray jArray = new JSONArray(jsonString);
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject json_data = jArray.getJSONObject(i);
                            group_name_list.add(json_data.getString("G_NAME"));
                            group_id_list.add(json_data.getString("G_ID"));
                            group_user_list.add(json_data.getString("member_id"));
                            group_user_id_list.add(json_data.getString("member_NAME").substring(1));
                            group_in_list.add(json_data.getString("in"));
                            group_type_list.add(json_data.getString("G_TY"));
                            //putGroup(sqLiteDatabase,
                            //        Integer.parseInt(json_data.getString("G_ID")),
                            //        json_data.getString("G_NAME"),
                            //        json_data.getString("member_id"),
                            //        json_data.getString("member_NAME").substring(1),
                            //        Integer.parseInt(json_data.getString("in")));
                            Log.i("result", "" + group_in_list.size());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }
                isSearchGroup = false;
                Log.i("result", "search finish");
                Looper.myLooper().quit();
            }
        });
    }

    public void delGroup(final String group_id, final String user_id){
        isDeleteGroup = true;
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(DBhost+"del_group.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    Log.d("del group", "" + group_id + user_id);
                    nameValuePairs.add(new BasicNameValuePair("group_id", group_id));
                    nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                    httpClient.execute(post);
                } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }
                isDeleteGroup = false;
                Looper.myLooper().quit();
            }
        });
    }

    public void joinGroup(final String group_id, final String user_id, final String is_in){
        isJoinGroup = true;
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(DBhost+"join_group.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    Log.d("join group", "" + group_id + user_id);
                    nameValuePairs.add(new BasicNameValuePair("group_id", group_id));
                    nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
                    nameValuePairs.add(new BasicNameValuePair("is_in", is_in));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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
                    JoinGroupResult = builder.toString();
                    Log.i("result", JoinGroupResult);
                } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }
                isJoinGroup = false;
                Looper.myLooper().quit();
            }
        });
    }
    public void getGroupMember(final String group_id, final String user_id, Context context){
        final SQLiteDatabase sqLiteDatabase = LocalDataBase.getDatabase(context);
        Log.d(TAG, "group_id" + group_id);
        isgetMember = true;
        friend = new ArrayList<>();
        friend_id = new ArrayList<>();
        friend_pic = new ArrayList<>();
        friend_coordinate = new ArrayList<>();
        area = new String[5];
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(DBhost+"get_member.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("group_id", group_id));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

                    String jsonString = builder.toString();
                    Log.i("result", jsonString);
                    try {
                        JSONArray jArray = new JSONArray(jsonString);
                        JSONObject json_data = jArray.getJSONObject(0);
                        JSONArray json_area_array =  json_data.getJSONArray("A");
                        JSONObject json_area_array_data;
                        Log.i("area", json_area_array.toString());
                        for (int i = 0; i < json_area_array.length(); i++) {
                            json_area_array_data = json_area_array.getJSONObject(i);
                            Log.i("area", json_area_array_data.getString("A_ID"));
                            area[0]=json_area_array_data.getString("A_name");
                            area[1]=json_area_array_data.getString("A_remark");
                            area[2]=json_area_array_data.getString("A_coordinate");
                            area[3]=json_area_array_data.getString("A_meter");
                            area[4]=json_area_array_data.getString("A_deadline");
                            //putArea(sqLiteDatabase,
                            //        Integer.parseInt(json_area_array_data.getString("A_ID")),
                            //        Integer.parseInt(group_id),
                            //        Double.valueOf(json_area_array_data.getString("A_coordinate").substring(0, json_area_array_data.getString("A_coordinate").indexOf('s'))),
                            //        Double.valueOf(json_area_array_data.getString("A_coordinate").substring(json_area_array_data.getString("A_coordinate").indexOf('s') + 1)),
                            //        Integer.parseInt(json_area_array_data.getString("A_meter")),
                            //        json_area_array_data.getString("A_name"),
                            //        json_area_array_data.getString("A_remark"),
                            //        json_area_array_data.getString("A_deadline"));
                        }
                        JSONArray json_user_array =   json_data.getJSONArray("U");
                        Log.i("fridne", json_user_array.toString());
                        JSONObject json_user_array_data;



                        //String area_coordinate = json_data.getString("area_coordinate");
                        //String area_meter = json_data.getString("area_meter");
                        //String area_name = json_data.getString("area_name");
                        //String area_remark = json_data.getString("area_remark");
                        //String area_deadline = json_data.getString("area_deadline");
                        //Log.d(TAG,  area_coordinate + "\n" + area_meter + "\n" + area_name + "\n" + area_remark + "\n" + area_deadline);
                        for (int i = 0; i < json_user_array.length(); i++) {
                            json_user_array_data = json_user_array.getJSONObject(i);
                            Log.i("fridne", json_user_array_data.getString("FB_ID"));
                            String id = json_user_array_data.getString("FB_ID");
                            if(!id.equals(user_id)) {
                                String name = json_user_array_data.getString("name");
                                String email = json_user_array_data.getString("email");
                                String coordinate = json_user_array_data.getString("coordinate");
                                String date = json_user_array_data.getString("date");
                                Log.d(TAG, name + "\n" + email + "\n" + coordinate + "\n" + date);
                                friend.add(name);
                                friend_id.add(id);
                                friend_coordinate.add(coordinate);
                                try {
                                    URL url = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                                    friend_pic.add(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Log.i("friend", email);
                                Log.d("friend", id);
                                Log.d("friend", "https://graph.facebook.com/" + id + "/picture?type=large");
                                Log.d("friend", ""+friend.size());
                                //putMember(sqLiteDatabase,
                                //        id,
                                //        json_user_array_data.getString("name"),
                                //        json_user_array_data.getString("email"),
                                //        Double.valueOf(json_user_array_data.getString("coordinate").substring(0, json_user_array_data.getString("coordinate").indexOf('s'))),
                                //        Double.valueOf(json_user_array_data.getString("coordinate").substring(json_user_array_data.getString("coordinate").indexOf('s') + 1)),
                                //        json_user_array_data.getString("date"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }
                isgetMember = false;
                Looper.myLooper().quit();
            }
        });
    }
    public void searchfriend(final String friend_keyword){
        isSearchfriend = true;
        group_name_list = new ArrayList<>();
        group_id_list = new ArrayList<>();
        group_user_list = new ArrayList<>();
        group_user_id_list = new ArrayList<>();
        group_in_list = new ArrayList<>();
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(DBhost+"search_friend.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("friend_keyword", friend_keyword));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

                    String jsonString = builder.toString();
                    Log.i("result", jsonString);

                    try {
                        JSONArray jArray = new JSONArray(jsonString);
                        Log.i("result", jArray.toString());
                        JSONObject json_data = jArray.getJSONObject(0);


                        if (json_data.isNull("email")) {
                            search_friend_email = friend_keyword;
                            search_friend_name = json_data.getString("name");
                        } else {
                            search_friend_email = json_data.getString("email");
                            search_friend_name = friend_keyword;
                        }


                        search_friend_id = json_data.getString("FB_ID");
                        try {
                            URL url = new URL("https://graph.facebook.com/" + search_friend_id + "/picture?type=large");
                            search_fridnd_pic = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.i("friend", search_friend_name);
                        Log.d("friend", search_friend_id);
                        Log.d("friend", "https://graph.facebook.com/" + search_friend_id + "/picture?type=large");

                    } catch (JSONException e) {
                        search_friend_email = null;
                        search_friend_name = null;
                        Log.i("result", "friend null");
                        e.printStackTrace();
                        }
                    } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }
                isSearchfriend = false;
                Looper.myLooper().quit();
            }
        });
    }
    public void updateArea(final String area_id, final String user_id, final String is_in){
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(DBhost+"area_user.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    Log.d("update Area", "" + area_id + user_id);
                    nameValuePairs.add(new BasicNameValuePair("area_id", area_id));
                    nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
                    nameValuePairs.add(new BasicNameValuePair("user_is_in", is_in));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                    httpClient.execute(post);
                } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }
            }
        });
    }
    public void getMemberIsIn(final String user_id){
        isgetMemberIsIn = true;
        mThread = new HandlerThread("net");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自動產生的方法 Stub
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(DBhost+"is_in_test.php");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    Log.d("user_id", "" + user_id);
                    nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

                    String jsonString = builder.toString();
                    Log.i("result", jsonString);
                    Notification_output.clear();//清空通知
                    try {
                        JSONArray jArray = new JSONArray(jsonString);
                        String desdline;
                        JSONArray member_jArray;
                        for (int i = 0; i <= jArray.length(); i++) {
                            JSONObject json_data = jArray.getJSONObject(i);
                            //Log.d("area", json_data.getString("area"));
                            String group_type = json_data.getString("area_type");

                            switch (group_type){
                                case "0":
                                    //Notification_output.add("家人");
                                    break;
                                case "1":
                                    //Notification_output.add("朋友");
                                    member_jArray = new JSONArray(json_data.getString("user"));
                                    desdline = json_data.getString("group_deadline");
                                    int getMon = Integer.valueOf(desdline.substring(0, desdline.indexOf("/")));
                                    int getDay = Integer.valueOf(desdline.substring(desdline.indexOf("/") + 1, desdline.indexOf(",")));
                                    int getHou = Integer.valueOf(desdline.substring(desdline.indexOf(",") + 1, desdline.indexOf(":")));
                                    int getMin = Integer.valueOf(desdline.substring(desdline.indexOf(":") + 1));
                                    Log.d("group_deadline", json_data.getString("group_deadline"));
                                    Calendar c = Calendar.getInstance();
                                    int m = c.get(Calendar.MONTH);
                                    int d = c.get(Calendar.DAY_OF_MONTH);
                                    int h = c.get(Calendar.HOUR_OF_DAY);
                                    int mi = c.get(Calendar.MINUTE);
                                    m++;
                                    boolean changeday = false;
                                    if(getMon == m && getDay == d)
                                        Notification_output.add("今天"+getHou+"點"+getMin+"分您要"+ json_data.getString("area_name"));
                                    break;
                                case "2":
                                    member_jArray = new JSONArray(json_data.getString("user"));
                                    desdline = json_data.getString("group_deadline");
                                    String member_array = "";
                                    int member_count=0;
                                    for (int j = 0; j < member_jArray.length(); j++) {
                                        JSONObject member_json_data = member_jArray.getJSONObject(j);
                                        Log.d("is_in", member_json_data.getString("is_in"));
                                        if(member_json_data.getString("is_in").equals("1")) {
                                            member_array = member_array + member_json_data.getString("name") + ",";
                                            Log.d("member", member_json_data.getString("name") + "is in the" + json_data.getString("area"));
                                            member_count++;
                                        } else {
                                            Log.d("member", member_json_data.getString("name") + "is not in the" + json_data.getString("area"));
                                        }
                                    }
                                    if(member_count>0) {
                                        Notification_output.add(member_array + "在" + json_data.getString("area_name") + "的區域內。");
                                    }
                                    char week[] = new char[7];
                                    for (int k=0; k<7; k++) {
                                        week[k] = desdline.charAt(k);
                                    }
                                    String hour = desdline.substring(desdline.indexOf(',')+1, desdline.lastIndexOf(','));
                                    String min = desdline.substring(desdline.lastIndexOf(',')+1);
                                    String output = "\n每周";
                                    if(week[0]=='1')
                                        output+="一,";
                                    if(week[1]=='1')
                                        output+="二,";
                                    if(week[2]=='1')
                                        output+="三,";
                                    if(week[3]=='1')
                                        output+="四,";
                                    if(week[4]=='1')
                                        output+="五,";
                                    if(week[5]=='1')
                                        output+="六,";
                                    if(week[6]=='1')
                                        output+="日,";
                                    output.substring(0,output.lastIndexOf(',')-1);
                                    output+="的";
                                    output+=hour;
                                    output+="點";
                                    output+=min;
                                    output+="分\n";
                                    Notification_output.add(output+json_data.getString("area_name")+"要上課");
                                    break;
                                case "3":
                                    //Notification_output.add("黑單");
                                    break;
                                case "4":
                                    //Notification_output.add("寵物");
                                    break;
                            }
                            //String desdline = json_data.getString("group_deadline");
                            //String member_array = "";
                            //int member_count=0;
                            //for (int j = 0; j < member_jArray.length(); j++) {
                            //    JSONObject member_json_data = member_jArray.getJSONObject(j);
                            //    Log.d("is_in", member_json_data.getString("is_in"));
                            //    if(member_json_data.getString("is_in").equals("1")){
                            //        member_array = member_array + member_json_data.getString("name") + ",";
                            //        Log.d("member", member_json_data.getString("name") + "is in the" + json_data.getString("area"));
                            //        member_count++;
                            //    } else {
                            //        Log.d("member", member_json_data.getString("name") + "is not in the" + json_data.getString("area"));
                            //    }
                            //}
                            //if(member_count>0) {
                            //    Notification_output.add(member_array + "在" + json_data.getString("area_name") + "的區域內。");
                            //}
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }
                Log.i("result", "test");
                isgetMemberIsIn = false;
                Looper.myLooper().quit();
            }
        });
    }
    public void putGroup(SQLiteDatabase sqLiteDatabase, int id, String name, String member_id, String member_NAME, int in) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("g_id", id);
        contentValues.put("g_name", name);
        contentValues.put("member_id", member_id);
        contentValues.put("member_NAME", member_NAME);
        contentValues.put("member_in", in);
        Cursor cursor = sqLiteDatabase.query(true,
                "group_table",//資料表名稱
                new String[]{"g_id", "g_name"},//欄位名稱
                "g_id=" + id,//WHERE
                null, // WHERE 的參數
                null, // GROUP BY
                null, // HAVING
                null, // ORDOR BY
                null  // 限制回傳的rows數量
        );
        String output = "";
        if(cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                int g_id = cursor.getInt(0);
                String g_name = cursor.getString(1);
                output = output + g_id + g_name;
                cursor.moveToNext();
            }
        }
        if(output==""){
            sqLiteDatabase.insert("group_table", null, contentValues);
            Log.d(TAG, "Group_insert");
        }else {
            sqLiteDatabase.update("group_table",
                    contentValues,
                    "g_id=" + id,
                    null);
            Log.d(TAG, "Group_update");
        }
    }
    public void putMember(SQLiteDatabase sqLiteDatabase, String id, String name, String email, double lat, double lng, String update_time) {
        Log.d(TAG, "putMember");
        ContentValues contentValues = new ContentValues();
        contentValues.put("u_id", id);
        contentValues.put("u_name", name);
        contentValues.put("u_email", email);
        contentValues.put("u_lat", lat);
        contentValues.put("u_lng", lng);
        contentValues.put("update_time", update_time);
        Cursor cursor = sqLiteDatabase.query(true,
                "user_table",//資料表名稱
                new String[]{"u_id", "u_name"},//欄位名稱
                "u_id=" + id,//WHERE
                null, // WHERE 的參數
                null, // GROUP BY
                null, // HAVING
                null, // ORDOR BY
                null  // 限制回傳的rows數量
        );
        String output = "";
        if(cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                int u_id = cursor.getInt(0);
                String g_name = cursor.getString(1);
                output = output + u_id + g_name;
                cursor.moveToNext();
            }
        }
        if(output==""){
            sqLiteDatabase.insert("user_table", null, contentValues);
            Log.d(TAG, "Member_insert");
        }else {
            sqLiteDatabase.update("user_table",
                    contentValues,
                    "u_id=" + id,
                    null);
            Log.d(TAG, "Member_update");
        }
    }
    public void putArea(SQLiteDatabase sqLiteDatabase, int a_id, int g_id, double area_lat, double area_long, int area_meter, String area_name, String area_remark, String area_deadline) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("a_id", a_id);
        contentValues.put("g_id", g_id);
        contentValues.put("area_lat", area_lat);
        contentValues.put("area_long", area_long);
        contentValues.put("area_meter", area_meter);
        contentValues.put("area_name", area_name);
        contentValues.put("area_remark", area_remark);
        contentValues.put("area_deadline", area_deadline);
        Cursor cursor = sqLiteDatabase.query(true,
                "area_table",//資料表名稱
                new String[]{"g_id", "area_name"},//欄位名稱
                "g_id=" + g_id,//WHERE
                null, // WHERE 的參數
                null, // GROUP BY
                null, // HAVING
                null, // ORDOR BY
                null  // 限制回傳的rows數量
        );
        String output = "";
        if(cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                int u_id = cursor.getInt(0);
                String g_name = cursor.getString(1);
                output = output + u_id + g_name;
                cursor.moveToNext();
            }
        }
        if(output==""){
            sqLiteDatabase.insert("area_table", null, contentValues);
            Log.d(TAG, "Area_insert");
        }else {
            sqLiteDatabase.update("area_table",
                    contentValues,
                    "g_id=" + g_id,
                    null);
            Log.d(TAG, "Area_update");
        }
    }
    public String getText(SQLiteDatabase sqLiteDatabase) {

        Cursor cursor = sqLiteDatabase.query(true,
                "user_table",//資料表名稱
                new String[]{"u_id", "u_name"},//欄位名稱
                null,//WHERE
                null, // WHERE 的參數
                null, // GROUP BY
                null, // HAVING
                null, // ORDOR BY
                null  // 限制回傳的rows數量
        );
        String output = "";
        if(cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                int u_id = cursor.getInt(0);
                String u_name = cursor.getString(1);
                output = output + u_id + u_name;
                cursor.moveToNext();
            }
        }
        return  output;
    }
}
