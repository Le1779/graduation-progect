package le1779.whereareyou;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    //GPSTracker gps;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Marker marker;
    private List<Marker> friend_mark = new ArrayList<Marker>();
    private List<Circle> area_circle = new ArrayList<Circle>();
    private List<Marker> area_mark = new ArrayList<Marker>();
    private List<String> area_mark_time = new ArrayList<String>();
    private double longitude;//經
    private double latitude;//緯
    private int windowWidth;
    private int add_destination=-1;
    private int now_destination_meter=0;
    private int now_destination_marker=0;

    boolean IS_PUT_MEMBER=false;
    int IS_FIRST=0;

    private TableRow member_list_tableRow;


    ArrayList<String>  group_name_list;
    ArrayList<String>  group_id_list;
    ArrayList<String>  group_user_list;
    ArrayList<String>  group_user_id_list;
    ArrayList<String> group_in_list;
    ArrayList<String> group_type_list;

    ArrayList<String>  member_name_list;
    ArrayList<String>  member_coordinate_list;
    ArrayList<Bitmap>  member_pic_list;
    ArrayList<String>  member_date_list;

    ArrayList<String> blacklist_id = new ArrayList<>();
    ArrayList<String> blacklist_name = new ArrayList<>();
    ArrayList<String> blacklist_address = new ArrayList<>();
    ArrayList<LatLng> blacklist_latlng = new ArrayList<>();

    ArrayList<String> class_deadline = new ArrayList<>();
    ArrayList<String> class_meter = new ArrayList<>();
    ArrayList<LatLng> class_latlng = new ArrayList<>();

    ArrayList<String> date_deadline = new ArrayList<>();
    ArrayList<String> date_meter = new ArrayList<>();
    ArrayList<LatLng> date_latlng = new ArrayList<>();
    ArrayList<String> date_name = new ArrayList<>();
    ArrayList<String> date_address = new ArrayList<>();

    ArrayList<String> activity_meter = new ArrayList<>();
    ArrayList<LatLng> activity_latlng = new ArrayList<>();
    ArrayList<String> activity_address = new ArrayList<>();
    ArrayList<String> pet_update_time = new ArrayList<>();
    ArrayList<LatLng> pet_latlng = new ArrayList<>();
    ArrayList<LatLng> pet_history_latlng = new ArrayList<>();

    ArrayList<String> home_deadline = new ArrayList<>();
    ArrayList<String> home_meter = new ArrayList<>();
    ArrayList<LatLng> home_latlng = new ArrayList<>();

    private Toolbar toolbar;
    private String now_group = null;
    private String coordinate;
    private String TAG = "MapsActivity";
    private String user_name, user_email, user_id ,user_password;
    private Bitmap user_picture;
    private TextView Output;
    private Button testbtn;
    private EditText group_editText;
    private SeekBar seekBar;
    DrawerLayout drawerLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private View search_dialog_view;
    private View rollcall_dialog_view;
    Button rollcall_btn;
    Button creat_group_btn;
    ImageButton left_drawer_open_imgBtn;
    private TextView search_dialog_view_editText;
    private TextView search_dialog_view_textView;
    private LinearLayout search_friend_found_user;
    private ImageView search_friend_pic;
    private TextView search_friend_name, search_friend_email,  group_name_textView, area_time_textView;
    private ListView search_friend_list;
    private ExpandableListView group_expandableListView;
    private ExpandableListViewAdapter adapter;
    ArrayList<String> friends_name = new ArrayList<String>();
    ArrayList<String> friends_id = new ArrayList<String>();
    ArrayList<Bitmap> fb_friend_pic = new ArrayList<Bitmap>();

    AccessToken accessToken;
    DataBase dataBase = new DataBase();
    Data data = new Data();
    ProgressDialog search_group_dialog;

    boolean is_put_member_first_time=false;
    int style_color = Color.rgb(255, 194, 204);
    Bitmap icon = null;

    //(229, 80, 84) #E55257 橘色
    //(3, 18, 119) #031377 藍色
    //(255, 210, 73) #FFD147 黃色
    //(41, 136, 102) #298968 綠色
    //(211, 27, 91) #D41C5C 紅色
    //(92, 92, 92) #5B5B5B

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_maps);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        windowWidth = displaymetrics.widthPixels;

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        if (data.readfile(MapsActivity.this, "UserData")) {
            user_email = data.getEmail();
            user_name = data.getName();
            user_id = data.getId();
            user_picture = data.getPicture();
            initView();
            user_picture = getRoundedBitmap(user_picture, windowWidth/7);
            //dataBase.searchDB("1");
            //new Thread(runnable).start();//將好友放置在地圖上
        }

        creat_group_btn = (Button)findViewById(R.id.creat_group_button);
        creat_group_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("creat group button", "on click");
                LayoutInflater inflater = LayoutInflater.from(MapsActivity.this);
                final View v = inflater.inflate(R.layout.alertdialog_edit, null);
                final AlertDialog creat_group_dialog = new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("輸入群組名稱")
                        .setView(v)
                        .show();
                final EditText editText = (EditText) (v.findViewById(R.id.Group_editText));
                final TextView textView = (TextView) (v.findViewById(R.id.creat_error_textView));
                String[] itemsText = {
                        "家人群組", "朋友群組", "老師群組", "黑單群組", "寵物群組"
                };
                ArrayAdapter adapter = new ArrayAdapter<String>(MapsActivity.this, R.layout.grid_item, itemsText);

                adapter.setDropDownViewResource(R.layout.grid_item);
                final Spinner spinner = (Spinner) (v.findViewById(R.id.creat_group_spinner));

                spinner.setAdapter(adapter);

                Button button = (Button) (v.findViewById(R.id.creat_group_button));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String creat_group_name = editText.getText().toString();
                        if(creat_group_name.equals(null)||creat_group_name.equals("")){
                            textView.setText("名稱不能是空白!");
                        }else {
                            creat_group_dialog.dismiss();
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            switch (spinner.getSelectedItemPosition()){
                                case 0:
                                    intent.setClass(MapsActivity.this, FamilyGroupActivity.class);
                                    bundle.putString("group_name", editText.getText().toString());
                                    bundle.putString("user_id", user_id);
                                    break;
                                case 1:
                                    intent.setClass(MapsActivity.this, FriendGroupActivity.class);
                                    bundle.putString("group_name", editText.getText().toString());
                                    bundle.putString("user_id", user_id);
                                    break;
                                case 2:
                                    intent.setClass(MapsActivity.this, TeacherGroupActivity.class);
                                    bundle.putString("group_name", editText.getText().toString());
                                    bundle.putString("user_id", user_id);
                                    break;
                                case 3:
                                    intent.setClass(MapsActivity.this, BlackListActivity.class);
                                    bundle.putString("group_name", editText.getText().toString());
                                    bundle.putString("user_id", user_id);
                                    break;
                                case 4:
                                    intent.setClass(MapsActivity.this, PetGroupActivity.class);
                                    bundle.putString("group_name", editText.getText().toString());
                                    bundle.putString("user_id", user_id);
                                    break;
                            }
                            intent.putExtras(bundle);
                            startActivity(intent);
                            //dataBase.creatGroup(editText.getText().toString(), user_id);
                            //new Thread(initgroupListView_runnable).start();
                        }
                    }
                });

                //int height=a.getWindow().getDecorView().getHeight();
                //int width=a.getWindow().getDecorView().getWidth();
                //fastblur(MapsActivity.this, getScreenShot(height, width), 10, v);
            }
        });
        Button right_test = (Button)findViewById(R.id.right_button);
        right_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MapsActivity.this, "test", Toast.LENGTH_LONG).show();
                Log.d("Right button", "on click");
            }
        });

        rollcall_btn = (Button)findViewById(R.id.rollcall_button);


        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                search_group_dialog = ProgressDialog.show(MapsActivity.this, " 更新中", "請等待...", true);
                new Thread(initgroupListView_runnable).start();
            }
        });


        search_group_dialog = ProgressDialog.show(MapsActivity.this, " 更新中", "請等待...",true);
        new Thread(initGroup_runnable).start();

        ListView config_listview = (ListView)findViewById(R.id.config_listView);
        config_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    accessToken = AccessToken.getCurrentAccessToken();
                    if (accessToken != null) {
                        LoginManager.getInstance().logOut();
                        data.saveData("null", MapsActivity.this, "UserData", user_picture);
                        Intent intentMap = new Intent();
                        intentMap.setClass(MapsActivity.this, UserActivity.class);
                        startActivity(intentMap);
                        MapsActivity.this.finish();
                    }
                }
                if(position == 2){
                    Intent intentFriend = new Intent();
                    intentFriend.setClass(MapsActivity.this, FriendGroupActivity.class);
                    Bundle bundleFriend = new Bundle();
                    bundleFriend.putString("group_name", "測試群組測試群組測試群組");
                    bundleFriend.putString("user_id", user_id);
                    intentFriend.putExtras(bundleFriend);
                    startActivity(intentFriend);
                }
            }
        });
        String[] iconText = {
                "登出臉書", "裝置配對", "設定", "關於"
        };
        int[] icon = {
                R.drawable.friend, R.drawable.match, R.drawable.config, R.drawable.us
        };
        List<Map<String, Object>> citems = new ArrayList<>();
        Map<String, Object> citem = new HashMap<String, Object>();
        for (int i = 0; i < iconText.length; i++) {
            citem = new HashMap<>();
            citem.put("image", icon[i]);
            citem.put("text", iconText[i]);
            citems.add(citem);
        }
        config_listview.setAdapter(new SimpleAdapter(MapsActivity.this,
                citems, R.layout.config_list, new String[]{"image", "text"},
                new int[]{R.id.config_imageView, R.id.config_textView}));


        group_expandableListView = (ExpandableListView)findViewById(R.id.group_expandableListView);

        group_expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                final int position = group_id_list.indexOf(adapter.group_id_list.get(groupPosition).get(childPosition));
                String in = group_in_list.get(position);
                Log.d(TAG, in);
                search_group_dialog = new ProgressDialog(MapsActivity.this);
                search_group_dialog.setTitle("請稍等");
                search_group_dialog.setMessage("正在下載成員");
                if (Integer.parseInt(in) == 1) {
                    now_group = group_id_list.get(position);
                    group_name_textView.setText(group_name_list.get(position));

                    switch (groupPosition){
                        case 0:
                            new Thread(putMember_runnable).start();
                            search_group_dialog.setIcon(R.drawable.home_group_icon);
                            break;
                        case 1:
                            new Thread(putFriend_runnable).start();
                            search_group_dialog.setIcon(R.drawable.friend_group_icon);
                            break;
                        case 2:
                            new Thread(putClass_runnable).start();
                            search_group_dialog.setIcon(R.drawable.teacher_group_icon);
                            break;
                        case 3:
                            new Thread(putBlackList_runnable).start();
                            search_group_dialog.setIcon(R.drawable.black_group_icon1);
                            break;
                        case 4:
                            new Thread(putPet_runnable).start();
                            search_group_dialog.setIcon(R.drawable.pokeball);
                            break;
                    }
                    search_group_dialog.show();
                    adapter = new ExpandableListViewAdapter(MapsActivity.this, group_name_list, group_user_id_list, group_type_list, group_id_list,style_color);
                    group_expandableListView.setAdapter(adapter);
                    for (int i = 0; i < 5; i++) {
                        group_expandableListView.expandGroup(i);
                    }

                } else {
                    new AlertDialog.Builder(MapsActivity.this)
                            .setTitle("你還不在這群組中")
                            .setMessage("確定要進入 \"" + group_name_list.get(position) + "\"嗎?")
                            .setPositiveButton("我要加入", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dataBase.joinGroup(group_id_list.get(position), user_id, "1");
                                    search_group_dialog = ProgressDialog.show(MapsActivity.this, " 更新中", "請等待...",true);
                                    new Thread(joinGroup_runnable).start();
                                }
                            })
                            .setNeutralButton("不要", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
                return false;
            }
        });
        group_expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (ExpandableListView.getPackedPositionType(l) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    final int groupPosition = ExpandableListView.getPackedPositionGroup(l);
                    final int childPosition = ExpandableListView.getPackedPositionChild(l);
                    //Toast.makeText(MapsActivity.this, adapter.group_id_list.get(groupPosition).get(childPosition), Toast.LENGTH_SHORT).show();

                    final int it = group_id_list.indexOf(adapter.group_id_list.get(groupPosition).get(childPosition));

                    new AlertDialog.Builder(MapsActivity.this)
                            .setTitle("確定刪除?")
                            .setMessage("你將刪除 \"" + group_name_list.get(it) + "\"")
                            .setPositiveButton("刪除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (Integer.valueOf(group_type_list.get(it))){
                                        case 0:
                                            dataBase.delGroup(group_id_list.get(it), user_id);
                                            new Thread(delGroup_runnable).start();
                                            break;
                                        case 1:
                                            dataBase.delFriendGroup(group_id_list.get(it), user_id);
                                            new Thread(delGroup_runnable).start();
                                            break;
                                        case 2:
                                            dataBase.delTeacherGroup(group_id_list.get(it), user_id);
                                            new Thread(delGroup_runnable).start();
                                            break;
                                        case 3:
                                            dataBase.delBlackListGroup(group_id_list.get(it), user_id);
                                            new Thread(delGroup_runnable).start();
                                            break;
                                    }
                                }
                            })
                            .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                    return true;
                }
                return false;
            }
        });
    }

    Runnable getFriendPic_runnable = new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < friends_id.size(); i++) {
                try {
                    URL url = new URL("https://graph.facebook.com/" + friends_id.get(i) + "/picture?type=large");
                    fb_friend_pic.add(FriendtoRoundBitmap(BitmapFactory.decodeStream(url.openConnection().getInputStream()), windowWidth / 5));
                    Log.d(TAG, "https://graph.facebook.com/" + friends_id.get(i) + "/picture?type=large");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            getFriendPic_handler.sendEmptyMessage(0);
        }
    };
    Handler getFriendPic_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();
            Map<String, Object> item = new HashMap<String, Object>();
            for (int i = 0; i < friends_name.size(); i++) {
                item = new HashMap<String, Object>();
                item.put("pic", fb_friend_pic.get(i));
                item.put("name", friends_name.get(i));
                //item.put("email", friends_id.get(i));
                items.add(item);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(MapsActivity.this,
                    items, R.layout.fb_friend_list, new String[]{"pic", "name"},
                    new int[]{R.id.fb_name_pic_imageView, R.id.fb_name_lis_textView});
            simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data,
                                            String textRepresentation) {
                    if (view instanceof ImageView && data instanceof Bitmap) {
                        ImageView i = (ImageView) view;
                        i.setImageBitmap((Bitmap) data);
                        return true;
                    }
                    return false;
                }
            });
            search_friend_list.setAdapter(simpleAdapter);
        }
    };
    Runnable initgroupListView_runnable = new Runnable() {
        @Override
        public void run() {
            //while (dataBase.isDeleteGroup || dataBase.isCreatGroup) {}
            dataBase.searchGroup(user_id, MapsActivity.this);
            while(dataBase.isSearchGroup){
                try {
                    Log.d(TAG, "wait");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            group_name_list = new ArrayList<String>(dataBase.group_name_list);
            group_id_list = new ArrayList<String>(dataBase.group_id_list);
            group_user_list = new ArrayList<String>(dataBase.group_user_list);
            group_user_id_list = new ArrayList<String>(dataBase.group_user_id_list);
            group_in_list = new ArrayList<String>(dataBase.group_in_list);
            group_type_list = new ArrayList<String>(dataBase.group_type_list);
            Log.d(TAG, "get array" + group_in_list.size());
            initgroupListView_handler.sendEmptyMessage(0);
        }
    };
    Handler initgroupListView_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            adapter = new ExpandableListViewAdapter(MapsActivity.this, group_name_list, group_user_id_list, group_type_list, group_id_list,style_color);
            group_expandableListView.setAdapter(adapter);
            for (int i = 0; i < 5; i++) {
                group_expandableListView.expandGroup(i);
            }
            mSwipeRefreshLayout.setRefreshing(false);
            search_group_dialog.dismiss();
        }
    };
    Runnable putMember_runnable = new Runnable() {
        @Override
        public void run() {
            IS_PUT_MEMBER=false;
            dataBase.getFamilyGroup(now_group, user_id);
            while(dataBase.isgetFamily){
                try {
                    Log.d(TAG, now_group + "wait get member");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            home_deadline = new ArrayList<>(dataBase.home_deadline);
            home_meter = new ArrayList<>(dataBase.home_meter);
            home_latlng = new ArrayList<>(dataBase.home_latlng);

            member_name_list = new ArrayList<String>(dataBase.friend);
            member_coordinate_list = new ArrayList<String>(dataBase.friend_coordinate);
            member_pic_list = new ArrayList<Bitmap>(dataBase.friend_pic);
            member_date_list = new ArrayList<String>(dataBase.friend_date);

            Log.d(TAG, "get member");
            putMember_handler.sendEmptyMessage(0);
        }
    };
    Handler putMember_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            icon = BitmapFactory.decodeResource(MapsActivity.this.getResources(), R.drawable.home_group_icon);
            style_color = Color.rgb(252, 60, 159);
            rollcall_btn.setVisibility(View.INVISIBLE);
            left_drawer_open_imgBtn.setImageBitmap(getSmallImage(icon));
            area_time_textView.setTextColor(style_color);
            toolbar.setBackgroundColor(style_color);
            area_time_textView.setText("");
            area_time_textView.setTextColor(style_color);
            creat_group_btn.setBackgroundColor(style_color);

            is_put_member_first_time=true;
            member_list_tableRow.removeAllViews();
            //add friend
            LinearLayout member_info = new LinearLayout(MapsActivity.this);
            member_info.setOrientation(LinearLayout.VERTICAL);

            ImageButton member_pic_imagebtn = new ImageButton(MapsActivity.this);
            member_pic_imagebtn.setImageBitmap(FriendtoRoundBitmap(changeColor(BitmapFactory.decodeResource(getResources(), R.drawable.add_friend_background), Color.red(style_color), Color.green(style_color), Color.blue(style_color)), windowWidth / 5));
            member_pic_imagebtn.setBackground(null);
            member_pic_imagebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new GraphRequest(
                            AccessToken.getCurrentAccessToken(),
                            "/me/friends",
                            null,
                            HttpMethod.GET,
                            new GraphRequest.Callback() {
                                public void onCompleted(GraphResponse response) {
                                    Log.d("Facebook", "getfriend");
                                    JSONArray friendslist;
                                    friends_name = new ArrayList<>();
                                    friends_id = new ArrayList<>();
                                    fb_friend_pic = new ArrayList<>();
                                    try {
                                        JSONArray rawName = response.getJSONObject().getJSONArray("data");
                                        friendslist = new JSONArray(rawName.toString());
                                        for (int l = 0; l < friendslist.length(); l++) {
                                            friends_name.add(friendslist.getJSONObject(l).getString("name"));
                                            friends_id.add(friendslist.getJSONObject(l).getString("id"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    initSearchFriend_dialog();
                                    new Thread(getFriendPic_runnable).start();
                                    new AlertDialog.Builder(MapsActivity.this)
                                            .setTitle("朋友列表")
                                            .setView(search_dialog_view)
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            }).show();

                                }
                            }
                    ).executeAsync();
                }
            });
            member_info.addView(member_pic_imagebtn);

            TextView member_name_text = new TextView(MapsActivity.this);
            member_name_text.setText("新增好友");
            //member_name_text.setTextColor(Color.rgb(255,255,255));
            member_name_text.setGravity(Gravity.CENTER_HORIZONTAL);
            member_name_text.setTypeface(null, Typeface.BOLD);
            member_info.addView(member_name_text);
            member_list_tableRow.addView(member_info);



            LinearLayout left_linearLayout = (LinearLayout)findViewById(R.id.left_drawer);
            drawerLayout.closeDrawer(left_linearLayout);
            mMap.clear();
            setUpMap();
            LatLng place;
            BitmapDescriptor icon;
            try {
                mMap.addCircle(new CircleOptions()
                        .center(home_latlng.get(0))
                        .fillColor(Color.argb(200, 255, 255, 255))
                        .strokeColor(style_color)
                        .strokeWidth(5)
                        .radius(Integer.valueOf(home_meter.get(0)) / 2));//Integer.getInteger(dataBase.area[3])/2)
                mMap.addMarker(new MarkerOptions()
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromBitmap(FriendtoRoundBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.home_group_icon), windowWidth / 10)))
                        .title("家")
                                //.snippet(dataBase.area[1])
                        .position(home_latlng.get(0)));
                String time = home_deadline.get(0);
                String hStr = time.substring(0,time.indexOf(":"));
                String minStr = time.substring(time.indexOf(":") + 1);
                area_time_textView.setText("要在"+hStr+"點"+minStr+"分 之前到家");
                area_time_textView.setTextSize(20);
                area_time_textView.setTypeface(null, Typeface.BOLD);
                //class area button
                member_info = new LinearLayout(MapsActivity.this);
                member_info.setOrientation(LinearLayout.VERTICAL);

                member_pic_imagebtn = new ImageButton(MapsActivity.this);
                member_pic_imagebtn.setImageBitmap(FriendtoRoundBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.home_group_icon), windowWidth / 7));
                member_pic_imagebtn.setBackground(null);
                member_pic_imagebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        moveMap(home_latlng.get(0));
                    }
                });
                member_info.addView(member_pic_imagebtn);

                member_name_text = new TextView(MapsActivity.this);
                member_name_text.setText("家");
                member_name_text.setGravity(Gravity.CENTER_HORIZONTAL);
                member_name_text.setTypeface(null, Typeface.BOLD);
                member_info.addView(member_name_text);
                member_list_tableRow.addView(member_info);
            } catch (Exception e) {
                Log.e("friend_size", e.toString());
            }
            //user button
            member_info = new LinearLayout(MapsActivity.this);
            member_info.setOrientation(LinearLayout.VERTICAL);

            member_pic_imagebtn = new ImageButton(MapsActivity.this);
            member_pic_imagebtn.setImageBitmap(user_picture);
            member_pic_imagebtn.setBackground(null);
            member_pic_imagebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moveMap(new LatLng(latitude, longitude));
                }
            });
            member_info.addView(member_pic_imagebtn);

            member_name_text = new TextView(MapsActivity.this);
            member_name_text.setText(user_name);
            //member_name_text.setTextColor(Color.rgb(255, 255, 255));
            member_name_text.setGravity(Gravity.CENTER_HORIZONTAL);
            member_name_text.setTypeface(null, Typeface.BOLD);
            member_info.addView(member_name_text);
            member_list_tableRow.addView(member_info);

            if(!dataBase.friend_coordinate.isEmpty()) {

                int friend_size = dataBase.friend_coordinate.size();
                Log.i("friend_size", String.valueOf(friend_size));
                Bitmap firendpic = user_picture;
                try {
                    for (int i = 0; i < friend_size; i++) {
                        String friend_name = dataBase.friend.get(i);
                        String friend_coordinate = dataBase.friend_coordinate.get(i);
                        String friend_date = dataBase.friend_date.get(i);
                        String friend_lat = friend_coordinate.substring(0, friend_coordinate.indexOf('s'));
                        String friend_long = friend_coordinate.substring(friend_coordinate.indexOf('s') + 1);
                        place = new LatLng(Double.parseDouble(friend_lat), Double.parseDouble(friend_long));
                        firendpic = dataBase.friend_pic.get(i);
                        firendpic = FriendtoRoundBitmap(firendpic, windowWidth / 7);
                        Log.d(TAG, friend_date);

                        icon = BitmapDescriptorFactory.fromBitmap(firendpic);
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .anchor(0.5f, 0.5f)
                                .icon(icon)
                                .title(friend_name)
                                .snippet(friend_date)
                                .position(place));
                        friend_mark.add(marker);

                        //建立下方使用者按鈕
                        member_info = new LinearLayout(MapsActivity.this);
                        member_info.setOrientation(LinearLayout.VERTICAL);

                        member_pic_imagebtn = new ImageButton(MapsActivity.this);
                        member_pic_imagebtn.setImageBitmap(firendpic);
                        member_pic_imagebtn.setBackground(null);
                        final LatLng finalPlace = place;
                        member_pic_imagebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                moveMap(finalPlace);
                            }
                        });
                        member_info.addView(member_pic_imagebtn);

                        member_name_text = new TextView(MapsActivity.this);
                        if(isDataExpired(friend_date))
                            member_name_text.setText(friend_name+"\n離線中");
                        else
                            member_name_text.setText(friend_name);
                        //member_name_text.setTextColor(Color.rgb(255, 255, 255));
                        member_name_text.setGravity(Gravity.CENTER_HORIZONTAL);
                        member_name_text.setTypeface(null, Typeface.BOLD);
                        member_info.addView(member_name_text);
                        member_list_tableRow.addView(member_info);

                    }
                } catch (Exception e) {
                    Log.e("friend_size", e.toString());
                }
            }else {
                Log.d(TAG, "member is null");

            }
            IS_PUT_MEMBER=true;

            search_group_dialog.dismiss();
        }
    };
    Runnable putFriend_runnable = new Runnable() {
        @Override
        public void run() {
            IS_PUT_MEMBER=false;
            dataBase.getFriendGroup(now_group, user_id);
            while(dataBase.isgetFriend){
                try {
                    Log.d(TAG, "wait get Teacher");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            member_name_list = new ArrayList<>(dataBase.friend);
            member_coordinate_list = new ArrayList<>(dataBase.friend_coordinate);
            member_pic_list = new ArrayList<>(dataBase.friend_pic);

            date_deadline = new ArrayList<>(dataBase.date_deadline);
            date_meter = new ArrayList<>(dataBase.date_meter);
            date_latlng = new ArrayList<>(dataBase.date_latlng);
            date_name = new ArrayList<>(dataBase.date_name);
            date_address = new ArrayList<>(dataBase.date_address);

            Log.d(TAG, "get member");
            putFriend_handler.sendEmptyMessage(0);
        }
    };
    Handler putFriend_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            icon = BitmapFactory.decodeResource(MapsActivity.this.getResources(), R.drawable.friend_group_icon);
            style_color = Color.rgb(255, 174, 39);
            rollcall_btn.setVisibility(View.VISIBLE);
            rollcall_btn.setBackgroundColor(style_color);
            rollcall_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    initRollcall_dialog(date_latlng.get(0), Integer.parseInt(date_meter.get(0)));
                    new AlertDialog.Builder(MapsActivity.this)
                            .setTitle("點名列表")
                            .setView(rollcall_dialog_view)
                            .setNegativeButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                }
            });
            left_drawer_open_imgBtn.setImageBitmap(getSmallImage(icon));
            area_time_textView.setTextColor(style_color);
            toolbar.setBackgroundColor(style_color);
            area_time_textView.setText("");
            area_time_textView.setTextColor(style_color);
            creat_group_btn.setBackgroundColor(style_color);

            is_put_member_first_time=true;
            member_list_tableRow.removeAllViews();
            //add friend
            LinearLayout member_info = new LinearLayout(MapsActivity.this);
            member_info.setOrientation(LinearLayout.VERTICAL);

            ImageButton member_pic_imagebtn = new ImageButton(MapsActivity.this);
            member_pic_imagebtn.setImageBitmap(FriendtoRoundBitmap(changeColor(BitmapFactory.decodeResource(getResources(), R.drawable.add_friend_background), Color.red(style_color), Color.green(style_color), Color.blue(style_color)), windowWidth / 5));
            member_pic_imagebtn.setBackground(null);
            member_pic_imagebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new GraphRequest(
                            AccessToken.getCurrentAccessToken(),
                            "/me/friends",
                            null,
                            HttpMethod.GET,
                            new GraphRequest.Callback() {
                                public void onCompleted(GraphResponse response) {
                                    Log.d("Facebook", "getfriend");
                                    JSONArray friendslist;
                                    friends_name = new ArrayList<>();
                                    friends_id = new ArrayList<>();
                                    fb_friend_pic = new ArrayList<>();
                                    try {
                                        JSONArray rawName = response.getJSONObject().getJSONArray("data");
                                        friendslist = new JSONArray(rawName.toString());
                                        for (int l = 0; l < friendslist.length(); l++) {
                                            friends_name.add(friendslist.getJSONObject(l).getString("name"));
                                            friends_id.add(friendslist.getJSONObject(l).getString("id"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    initSearchFriend_dialog();
                                    new Thread(getFriendPic_runnable).start();
                                    new AlertDialog.Builder(MapsActivity.this)
                                            .setTitle("朋友列表")
                                            .setView(search_dialog_view)
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            }).show();

                                }
                            }
                    ).executeAsync();
                }
            });
            member_info.addView(member_pic_imagebtn);

            TextView member_name_text = new TextView(MapsActivity.this);
            member_name_text.setText("新增好友");
            //member_name_text.setTextColor(Color.rgb(255,255,255));
            member_name_text.setGravity(Gravity.CENTER_HORIZONTAL);
            member_name_text.setTypeface(null, Typeface.BOLD);
            member_info.addView(member_name_text);
            member_list_tableRow.addView(member_info);

            LinearLayout left_linearLayout = (LinearLayout)findViewById(R.id.left_drawer);
            drawerLayout.closeDrawer(left_linearLayout);
            mMap.clear();
            setUpMap();
            LatLng place;
            BitmapDescriptor icon;
            //class
            try {
                mMap.addCircle(new CircleOptions()
                        .center(date_latlng.get(0))
                        .fillColor(Color.argb(200, 255, 255, 255))
                        .strokeColor(style_color)
                        .strokeWidth(5)
                        .radius(Integer.valueOf(date_meter.get(0)) / 2));
                mMap.addMarker(new MarkerOptions()
                        .anchor(0.5f, 0.5f)
                        .title(date_name.get(0))
                        .snippet(date_address.get(0))
                        .icon(BitmapDescriptorFactory.fromBitmap(FriendtoRoundBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.friend_group_icon), windowWidth / 10)))
                        .position(date_latlng.get(0)));
                String time = date_deadline.get(0);
                String mStr = time.substring(0,time.indexOf("/"));
                String dStr = time.substring(time.indexOf("/")+1,time.indexOf(","));
                String hStr = time.substring(time.indexOf(",")+1,time.indexOf(":"));
                String minStr = time.substring(time.indexOf(":") + 1);
                area_time_textView.setText(mStr+"月"+dStr+"日"+hStr+"點"+minStr+"分");
                area_time_textView.setTextSize(20);
                area_time_textView.setTypeface(null, Typeface.BOLD);
                //class area button
                member_info = new LinearLayout(MapsActivity.this);
                member_info.setOrientation(LinearLayout.VERTICAL);

                member_pic_imagebtn = new ImageButton(MapsActivity.this);
                member_pic_imagebtn.setImageBitmap(FriendtoRoundBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.friend_group_icon), windowWidth / 7));
                member_pic_imagebtn.setBackground(null);
                member_pic_imagebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        moveMap(date_latlng.get(0));
                    }
                });
                member_info.addView(member_pic_imagebtn);

                member_name_text = new TextView(MapsActivity.this);
                member_name_text.setText("約會地點");
                member_name_text.setGravity(Gravity.CENTER_HORIZONTAL);
                member_name_text.setTypeface(null, Typeface.BOLD);
                member_info.addView(member_name_text);
                member_list_tableRow.addView(member_info);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

            //user button
            member_info = new LinearLayout(MapsActivity.this);
            member_info.setOrientation(LinearLayout.VERTICAL);

            member_pic_imagebtn = new ImageButton(MapsActivity.this);
            member_pic_imagebtn.setImageBitmap(user_picture);
            member_pic_imagebtn.setBackground(null);
            member_pic_imagebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moveMap(new LatLng(latitude, longitude));
                }
            });
            member_info.addView(member_pic_imagebtn);

            member_name_text = new TextView(MapsActivity.this);
            member_name_text.setText(user_name);
            //member_name_text.setTextColor(Color.rgb(255, 255, 255));
            member_name_text.setGravity(Gravity.CENTER_HORIZONTAL);
            member_name_text.setTypeface(null, Typeface.BOLD);
            member_info.addView(member_name_text);
            member_list_tableRow.addView(member_info);

            if(!dataBase.friend_coordinate.isEmpty()) {
                int friend_size = dataBase.friend_coordinate.size();
                Log.i("friend_size", String.valueOf(friend_size));
                Bitmap firendpic;
                try {
                    for (int i = 0; i < friend_size; i++) {
                        String friend_name = dataBase.friend.get(i);
                        String friend_coordinate = dataBase.friend_coordinate.get(i);
                        String friend_lat = friend_coordinate.substring(0, friend_coordinate.indexOf('s'));
                        String friend_long = friend_coordinate.substring(friend_coordinate.indexOf('s') + 1);
                        place = new LatLng(Double.parseDouble(friend_lat), Double.parseDouble(friend_long));
                        firendpic = dataBase.friend_pic.get(i);
                        firendpic = FriendtoRoundBitmap(firendpic, windowWidth / 7);

                        icon = BitmapDescriptorFactory.fromBitmap(firendpic);
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .anchor(0.5f, 0.5f)
                                .icon(icon)
                                .title(friend_name)
                                .snippet("")
                                .position(place));
                        friend_mark.add(marker);

                        //建立下方使用者按鈕
                        member_info = new LinearLayout(MapsActivity.this);
                        member_info.setOrientation(LinearLayout.VERTICAL);

                        member_pic_imagebtn = new ImageButton(MapsActivity.this);
                        member_pic_imagebtn.setImageBitmap(firendpic);
                        member_pic_imagebtn.setBackground(null);
                        final LatLng finalPlace = place;
                        member_pic_imagebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                moveMap(finalPlace);
                            }
                        });
                        member_info.addView(member_pic_imagebtn);

                        member_name_text = new TextView(MapsActivity.this);
                        member_name_text.setText(friend_name);
                        //member_name_text.setTextColor(Color.rgb(255, 255, 255));
                        member_name_text.setGravity(Gravity.CENTER_HORIZONTAL);
                        member_name_text.setTypeface(null, Typeface.BOLD);
                        member_info.addView(member_name_text);
                        member_list_tableRow.addView(member_info);

                    }
                } catch (Exception e) {
                    Log.e("friend_size", e.toString());
                }
            }else {
                Log.d(TAG, "member is null");

            }
            IS_PUT_MEMBER=true;

            search_group_dialog.dismiss();
        }
    };
    Runnable putClass_runnable = new Runnable() {
        @Override
        public void run() {
            IS_PUT_MEMBER=false;
            dataBase.getTeacherGroup(now_group, user_id);
            while(dataBase.isgetClass){
                try {
                    Log.d(TAG, "wait get Teacher");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            member_name_list = new ArrayList<>(dataBase.friend);
            member_coordinate_list = new ArrayList<>(dataBase.friend_coordinate);
            member_pic_list = new ArrayList<>(dataBase.friend_pic);

            class_deadline = new ArrayList<>(dataBase.class_deadline);
            class_meter = new ArrayList<>(dataBase.class_meter);
            class_latlng = new ArrayList<>(dataBase.class_latlng);

            Log.d(TAG, "get member");
            putClass_handler.sendEmptyMessage(0);
        }
    };
    Handler putClass_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            icon = BitmapFactory.decodeResource(MapsActivity.this.getResources(), R.drawable.teacher_group_icon);
            style_color = Color.rgb(123, 84, 235);
            rollcall_btn.setVisibility(View.VISIBLE);
            rollcall_btn.setBackgroundColor(style_color);
            rollcall_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    initRollcall_dialog(class_latlng.get(0), Integer.parseInt(class_meter.get(0)));
                    new AlertDialog.Builder(MapsActivity.this)
                            .setTitle("點名列表")
                            .setView(rollcall_dialog_view)
                            .setNegativeButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                }
            });
            left_drawer_open_imgBtn.setImageBitmap(getSmallImage(icon));
            area_time_textView.setTextColor(style_color);
            toolbar.setBackgroundColor(style_color);
            area_time_textView.setText("");
            area_time_textView.setTextColor(style_color);
            creat_group_btn.setBackgroundColor(style_color);

            is_put_member_first_time=true;
            member_list_tableRow.removeAllViews();
            //add friend
            LinearLayout member_info = new LinearLayout(MapsActivity.this);
            member_info.setOrientation(LinearLayout.VERTICAL);

            ImageButton member_pic_imagebtn = new ImageButton(MapsActivity.this);
            member_pic_imagebtn.setImageBitmap(FriendtoRoundBitmap(changeColor(BitmapFactory.decodeResource(getResources(), R.drawable.add_friend_background), Color.red(style_color), Color.green(style_color), Color.blue(style_color)), windowWidth / 5));
            member_pic_imagebtn.setBackground(null);
            member_pic_imagebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new GraphRequest(
                            AccessToken.getCurrentAccessToken(),
                            "/me/friends",
                            null,
                            HttpMethod.GET,
                            new GraphRequest.Callback() {
                                public void onCompleted(GraphResponse response) {
                                    Log.d("Facebook", "getfriend");
                                    JSONArray friendslist;
                                    friends_name = new ArrayList<>();
                                    friends_id = new ArrayList<>();
                                    fb_friend_pic = new ArrayList<>();
                                    try {
                                        JSONArray rawName = response.getJSONObject().getJSONArray("data");
                                        friendslist = new JSONArray(rawName.toString());
                                        for (int l = 0; l < friendslist.length(); l++) {
                                            friends_name.add(friendslist.getJSONObject(l).getString("name"));
                                            friends_id.add(friendslist.getJSONObject(l).getString("id"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    initSearchFriend_dialog();
                                    new Thread(getFriendPic_runnable).start();
                                    new AlertDialog.Builder(MapsActivity.this)
                                            .setTitle("朋友列表")
                                            .setView(search_dialog_view)
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            }).show();

                                }
                            }
                    ).executeAsync();
                }
            });
            member_info.addView(member_pic_imagebtn);

            TextView member_name_text = new TextView(MapsActivity.this);
            member_name_text.setText("新增好友");
            //member_name_text.setTextColor(Color.rgb(255,255,255));
            member_name_text.setGravity(Gravity.CENTER_HORIZONTAL);
            member_name_text.setTypeface(null, Typeface.BOLD);
            member_info.addView(member_name_text);
            member_list_tableRow.addView(member_info);



            LinearLayout left_linearLayout = (LinearLayout)findViewById(R.id.left_drawer);
            drawerLayout.closeDrawer(left_linearLayout);
            mMap.clear();
            setUpMap();
            LatLng place;
            BitmapDescriptor icon;
            //class
            try {
                mMap.addCircle(new CircleOptions()
                        .center(class_latlng.get(0))
                        .fillColor(Color.argb(200, 255, 255, 255))
                        .strokeColor(style_color)
                        .strokeWidth(5)
                        .radius(Integer.valueOf(class_meter.get(0)) / 2));
                mMap.addMarker(new MarkerOptions()
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromBitmap(FriendtoRoundBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.school), windowWidth / 10)))
                        .position(class_latlng.get(0)));
                String time = class_deadline.get(0);
                char week[] = new char[7];
                for (int i=0; i<7; i++) {
                    week[i] = time.charAt(i);
                }
                String hour = time.substring(time.indexOf(',')+1, time.lastIndexOf(','));
                String min = time.substring(time.lastIndexOf(',')+1);
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
                output.substring(0,output.lastIndexOf(','));
                output+="的";
                output+=hour;
                output+="點";
                output+=min;
                output+="分\n";
                area_time_textView.setText(output);
                area_time_textView.setTextSize(20);
                area_time_textView.setTypeface(null, Typeface.BOLD);
                //class area button
                member_info = new LinearLayout(MapsActivity.this);
                member_info.setOrientation(LinearLayout.VERTICAL);

                member_pic_imagebtn = new ImageButton(MapsActivity.this);
                member_pic_imagebtn.setImageBitmap(FriendtoRoundBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.school), windowWidth / 7));
                member_pic_imagebtn.setBackground(null);
                member_pic_imagebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        moveMap(class_latlng.get(0));
                    }
                });
                member_info.addView(member_pic_imagebtn);

                member_name_text = new TextView(MapsActivity.this);
                member_name_text.setText("教室");
                member_name_text.setGravity(Gravity.CENTER_HORIZONTAL);
                member_name_text.setTypeface(null, Typeface.BOLD);
                member_info.addView(member_name_text);
                member_list_tableRow.addView(member_info);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

            //user button
            member_info = new LinearLayout(MapsActivity.this);
            member_info.setOrientation(LinearLayout.VERTICAL);

            member_pic_imagebtn = new ImageButton(MapsActivity.this);
            member_pic_imagebtn.setImageBitmap(user_picture);
            member_pic_imagebtn.setBackground(null);
            member_pic_imagebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moveMap(new LatLng(latitude, longitude));
                }
            });
            member_info.addView(member_pic_imagebtn);

            member_name_text = new TextView(MapsActivity.this);
            member_name_text.setText(user_name);
            //member_name_text.setTextColor(Color.rgb(255, 255, 255));
            member_name_text.setGravity(Gravity.CENTER_HORIZONTAL);
            member_name_text.setTypeface(null, Typeface.BOLD);
            member_info.addView(member_name_text);
            member_list_tableRow.addView(member_info);

            //if(!dataBase.friend_coordinate.isEmpty()) {
            //    int friend_size = dataBase.friend_coordinate.size();
            //    Log.i("friend_size", String.valueOf(friend_size));
            //    Bitmap firendpic = user_picture;
            //    try {
            //        for (int i = 0; i < friend_size; i++) {
            //            String friend_name = dataBase.friend.get(i);
            //            String friend_coordinate = dataBase.friend_coordinate.get(i);
            //            String friend_lat = friend_coordinate.substring(0, friend_coordinate.indexOf('s'));
            //            String friend_long = friend_coordinate.substring(friend_coordinate.indexOf('s') + 1);
            //            place = new LatLng(Double.parseDouble(friend_lat), Double.parseDouble(friend_long));
            //            firendpic = dataBase.friend_pic.get(i);
            //            firendpic = FriendtoRoundBitmap(firendpic, windowWidth / 7);
//
            //            icon = BitmapDescriptorFactory.fromBitmap(firendpic);
            //            Marker marker = mMap.addMarker(new MarkerOptions()
            //                    .anchor(0.5f, 0.5f)
            //                    .icon(icon)
            //                    .title(friend_name)
            //                    .snippet("")
            //                    .position(place));
            //            friend_mark.add(marker);
//
            //            //建立下方使用者按鈕
            //            member_info = new LinearLayout(MapsActivity.this);
            //            member_info.setOrientation(LinearLayout.VERTICAL);
//
            //            member_pic_imagebtn = new ImageButton(MapsActivity.this);
            //            member_pic_imagebtn.setImageBitmap(firendpic);
            //            member_pic_imagebtn.setBackground(null);
            //            final LatLng finalPlace = place;
            //            member_pic_imagebtn.setOnClickListener(new View.OnClickListener() {
            //                @Override
            //                public void onClick(View view) {
            //                    moveMap(finalPlace);
            //                }
            //            });
            //            member_info.addView(member_pic_imagebtn);
//
            //            member_name_text = new TextView(MapsActivity.this);
            //            member_name_text.setText(friend_name);
            //            //member_name_text.setTextColor(Color.rgb(255, 255, 255));
            //            member_name_text.setGravity(Gravity.CENTER_HORIZONTAL);
            //            member_name_text.setTypeface(null, Typeface.BOLD);
            //            member_info.addView(member_name_text);
            //            member_list_tableRow.addView(member_info);
//
            //        }
            //    } catch (Exception e) {
            //        Log.e("friend_size", e.toString());
            //    }
            //}else {
            //    Log.d(TAG, "member is null");
//
            //}
            IS_PUT_MEMBER=true;

            search_group_dialog.dismiss();
        }
    };
    Runnable putBlackList_runnable = new Runnable() {
        @Override
        public void run() {
            dataBase.getBlackListMember(now_group, user_id);
            dataBase.getBlackListGroup(now_group, MapsActivity.this);
            while(dataBase.isgetBlackList||dataBase.isgetMember){
                try {
                    Log.d(TAG, "wait get BlackList");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            member_name_list = new ArrayList<String>(dataBase.friend);
            member_coordinate_list = new ArrayList<String>(dataBase.friend_coordinate);
            member_pic_list = new ArrayList<Bitmap>(dataBase.friend_pic);

            blacklist_id = new ArrayList<>(dataBase.blacklist_id);
            blacklist_name = new ArrayList<>(dataBase.blacklist_name);
            blacklist_address = new ArrayList<>(dataBase.blacklist_address);
            blacklist_latlng = new ArrayList<>(dataBase.blacklist_latlng);
            Log.d(TAG, "get BlackList");
            putBlackList_handler.sendEmptyMessage(0);
        }
    };
    Handler putBlackList_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            icon = BitmapFactory.decodeResource(MapsActivity.this.getResources(), R.drawable.black_group_icon);
            style_color = Color.rgb(0, 0, 0);
            rollcall_btn.setVisibility(View.INVISIBLE);
            left_drawer_open_imgBtn.setImageBitmap(getSmallImage(icon));
            area_time_textView.setTextColor(style_color);
            toolbar.setBackgroundColor(style_color);
            area_time_textView.setText("");
            area_time_textView.setTextColor(style_color);
            creat_group_btn.setBackgroundColor(style_color);


            is_put_member_first_time=true;
            member_list_tableRow.removeAllViews();
            //add friend
            LinearLayout member_info = new LinearLayout(MapsActivity.this);
            member_info.setOrientation(LinearLayout.VERTICAL);

            ImageButton member_pic_imagebtn = new ImageButton(MapsActivity.this);
            member_pic_imagebtn.setImageBitmap(FriendtoRoundBitmap(changeColor(BitmapFactory.decodeResource(getResources(), R.drawable.add_friend_background), Color.red(style_color), Color.green(style_color), Color.blue(style_color)), windowWidth / 5));
            member_pic_imagebtn.setBackground(null);
            member_pic_imagebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new GraphRequest(
                            AccessToken.getCurrentAccessToken(),
                            "/me/friends",
                            null,
                            HttpMethod.GET,
                            new GraphRequest.Callback() {
                                public void onCompleted(GraphResponse response) {
                                    Log.d("Facebook", "getfriend");
                                    JSONArray friendslist;
                                    friends_name = new ArrayList<String>();
                                    friends_id = new ArrayList<String>();
                                    fb_friend_pic = new ArrayList<Bitmap>();
                                    try {
                                        JSONArray rawName = response.getJSONObject().getJSONArray("data");
                                        friendslist = new JSONArray(rawName.toString());
                                        for (int l = 0; l < friendslist.length(); l++) {
                                            friends_name.add(friendslist.getJSONObject(l).getString("name"));
                                            friends_id.add(friendslist.getJSONObject(l).getString("id"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    initSearchFriend_dialog();
                                    new Thread(getFriendPic_runnable).start();
                                    new AlertDialog.Builder(MapsActivity.this)
                                            .setTitle("朋友列表")
                                            .setView(search_dialog_view)
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            }).show();

                                }
                            }
                    ).executeAsync();
                }
            });
            member_info.addView(member_pic_imagebtn);

            TextView member_name_text = new TextView(MapsActivity.this);
            member_name_text.setText("新增好友");
            //member_name_text.setTextColor(Color.rgb(255,255,255));
            member_name_text.setGravity(Gravity.CENTER_HORIZONTAL);
            member_name_text.setTypeface(null, Typeface.BOLD);
            member_info.addView(member_name_text);
            member_list_tableRow.addView(member_info);

            //user button
            member_info = new LinearLayout(MapsActivity.this);
            member_info.setOrientation(LinearLayout.VERTICAL);

            member_pic_imagebtn = new ImageButton(MapsActivity.this);
            member_pic_imagebtn.setImageBitmap(user_picture);
            member_pic_imagebtn.setBackground(null);
            member_pic_imagebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moveMap(new LatLng(latitude, longitude));
                }
            });
            member_info.addView(member_pic_imagebtn);

            member_name_text = new TextView(MapsActivity.this);
            member_name_text.setText(user_name);
            //member_name_text.setTextColor(Color.rgb(255, 255, 255));
            member_name_text.setGravity(Gravity.CENTER_HORIZONTAL);
            member_name_text.setTypeface(null, Typeface.BOLD);
            member_info.addView(member_name_text);
            member_list_tableRow.addView(member_info);

            LinearLayout left_linearLayout = (LinearLayout)findViewById(R.id.left_drawer);
            drawerLayout.closeDrawer(left_linearLayout);
            mMap.clear();
            try {
                for(int i = 0; i<blacklist_name.size(); i++) {
                    mMap.addMarker(new MarkerOptions()
                            .anchor(0.5f, 0.5f)
                            .title(blacklist_name.get(i))
                            .snippet(blacklist_address.get(i))
                            .icon(BitmapDescriptorFactory.fromBitmap(FriendtoRoundBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.disabled), windowWidth / 10)))
                            .position(blacklist_latlng.get(i)));
                }
            } catch (Exception e){
                Log.e("friend_size", e.toString());
            }
            setUpMap();
            if(!dataBase.friend_coordinate.isEmpty()) {
                LatLng place;
                BitmapDescriptor icon;
                int friend_size = dataBase.friend_coordinate.size();
                Log.i("friend_size", String.valueOf(friend_size));
                Bitmap firendpic;
                try {
                    for (int i = 0; i < friend_size; i++) {
                        String friend_name = dataBase.friend.get(i);
                        String friend_coordinate = dataBase.friend_coordinate.get(i);
                        String friend_lat = friend_coordinate.substring(0, friend_coordinate.indexOf('s'));
                        String friend_long = friend_coordinate.substring(friend_coordinate.indexOf('s') + 1);
                        place = new LatLng(Double.parseDouble(friend_lat), Double.parseDouble(friend_long));
                        firendpic = dataBase.friend_pic.get(i);
                        firendpic = FriendtoRoundBitmap(firendpic, windowWidth / 7);

                        icon = BitmapDescriptorFactory.fromBitmap(firendpic);
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .anchor(0.5f, 0.5f)
                                .icon(icon)
                                .title(friend_name)
                                .snippet("")
                                .position(place));
                        friend_mark.add(marker);

                        //建立下方使用者按鈕
                        member_info = new LinearLayout(MapsActivity.this);
                        member_info.setOrientation(LinearLayout.VERTICAL);

                        member_pic_imagebtn = new ImageButton(MapsActivity.this);
                        member_pic_imagebtn.setImageBitmap(firendpic);
                        member_pic_imagebtn.setBackground(null);
                        final LatLng finalPlace = place;
                        member_pic_imagebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                moveMap(finalPlace);
                            }
                        });
                        member_info.addView(member_pic_imagebtn);

                        member_name_text = new TextView(MapsActivity.this);
                        member_name_text.setText(friend_name);
                        //member_name_text.setTextColor(Color.rgb(255, 255, 255));
                        member_name_text.setGravity(Gravity.CENTER_HORIZONTAL);
                        member_name_text.setTypeface(null, Typeface.BOLD);
                        member_info.addView(member_name_text);
                        member_list_tableRow.addView(member_info);

                    }
                } catch (Exception e) {
                    Log.e("friend_size", e.toString());
                }
            }else {
                Log.d(TAG, "member is null");

            }

            search_group_dialog.dismiss();
        }
    };
    Runnable putPet_runnable = new Runnable() {
        @Override
        public void run() {
            dataBase.getPetGroup(now_group, user_id);
            while(dataBase.isgetPet){
                try {
                    Log.d(TAG, "wait get BlackList");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            pet_update_time = new ArrayList<>(dataBase.pet_update_time);
            pet_latlng = new ArrayList<>(dataBase.pet_latlng);
            pet_history_latlng = new ArrayList<>(dataBase.pet_history_latlng);

            activity_address = new ArrayList<>(dataBase.activity_address);
            activity_meter = new ArrayList<>(dataBase.activity_meter);
            activity_latlng = new ArrayList<>(dataBase.activity_latlng);
            Log.d(TAG, "get BlackList");
            putPet_handler.sendEmptyMessage(0);
        }
    };
    Handler putPet_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            icon = BitmapFactory.decodeResource(MapsActivity.this.getResources(), R.drawable.pokeball);
            style_color = Color.rgb(47, 219, 107);
            rollcall_btn.setVisibility(View.INVISIBLE);
            left_drawer_open_imgBtn.setImageBitmap(getSmallImage(icon));
            area_time_textView.setTextColor(style_color);
            toolbar.setBackgroundColor(style_color);
            area_time_textView.setText("");
            area_time_textView.setTextColor(style_color);
            creat_group_btn.setBackgroundColor(style_color);

            is_put_member_first_time=true;
            member_list_tableRow.removeAllViews();
            //add friend
            LinearLayout member_info = new LinearLayout(MapsActivity.this);
            member_info.setOrientation(LinearLayout.VERTICAL);

            ImageButton member_pic_imagebtn = new ImageButton(MapsActivity.this);
            member_pic_imagebtn.setImageBitmap(FriendtoRoundBitmap(changeColor(BitmapFactory.decodeResource(getResources(), R.drawable.add_friend_background), Color.red(style_color), Color.green(style_color), Color.blue(style_color)), windowWidth / 5));
            member_pic_imagebtn.setBackground(null);
            member_pic_imagebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new GraphRequest(
                            AccessToken.getCurrentAccessToken(),
                            "/me/friends",
                            null,
                            HttpMethod.GET,
                            new GraphRequest.Callback() {
                                public void onCompleted(GraphResponse response) {
                                    Log.d("Facebook", "getfriend");
                                    JSONArray friendslist;
                                    friends_name = new ArrayList<>();
                                    friends_id = new ArrayList<>();
                                    fb_friend_pic = new ArrayList<>();
                                    try {
                                        JSONArray rawName = response.getJSONObject().getJSONArray("data");
                                        friendslist = new JSONArray(rawName.toString());
                                        for (int l = 0; l < friendslist.length(); l++) {
                                            friends_name.add(friendslist.getJSONObject(l).getString("name"));
                                            friends_id.add(friendslist.getJSONObject(l).getString("id"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    initSearchFriend_dialog();
                                    new Thread(getFriendPic_runnable).start();
                                    new AlertDialog.Builder(MapsActivity.this)
                                            .setTitle("朋友列表")
                                            .setView(search_dialog_view)
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            }).show();

                                }
                            }
                    ).executeAsync();
                }
            });
            member_info.addView(member_pic_imagebtn);

            TextView member_name_text = new TextView(MapsActivity.this);
            member_name_text.setText("新增朋友");
            //member_name_text.setTextColor(Color.rgb(255,255,255));
            member_name_text.setGravity(Gravity.CENTER_HORIZONTAL);
            member_name_text.setTypeface(null, Typeface.BOLD);
            member_info.addView(member_name_text);
            member_list_tableRow.addView(member_info);

            //user button
            member_info = new LinearLayout(MapsActivity.this);
            member_info.setOrientation(LinearLayout.VERTICAL);

            member_pic_imagebtn = new ImageButton(MapsActivity.this);
            member_pic_imagebtn.setImageBitmap(user_picture);
            member_pic_imagebtn.setBackground(null);
            member_pic_imagebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moveMap(new LatLng(latitude, longitude));
                }
            });
            member_info.addView(member_pic_imagebtn);

            member_name_text = new TextView(MapsActivity.this);
            member_name_text.setText(user_name);
            //member_name_text.setTextColor(Color.rgb(255, 255, 255));
            member_name_text.setGravity(Gravity.CENTER_HORIZONTAL);
            member_name_text.setTypeface(null, Typeface.BOLD);
            member_info.addView(member_name_text);
            member_list_tableRow.addView(member_info);

            LinearLayout left_linearLayout = (LinearLayout)findViewById(R.id.left_drawer);
            drawerLayout.closeDrawer(left_linearLayout);
            mMap.clear();
            setUpMap();
            if(!dataBase.pet_latlng.isEmpty()) {
                int friend_size = dataBase.pet_latlng.size();
                Log.i("friend_size", String.valueOf(friend_size));
                try {
                    mMap.addCircle(new CircleOptions()
                            .center(activity_latlng.get(0))
                            .fillColor(Color.argb(200, 255, 255, 255))
                            .strokeColor(style_color)
                            .strokeWidth(5)
                            .radius(Integer.valueOf(activity_meter.get(0)) / 2));
                    mMap.addMarker(new MarkerOptions()
                            .anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory.fromBitmap(FriendtoRoundBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pokeball), windowWidth / 7)))
                            .position(activity_latlng.get(0)));
                    member_info = new LinearLayout(MapsActivity.this);
                    member_info.setOrientation(LinearLayout.VERTICAL);

                    member_pic_imagebtn = new ImageButton(MapsActivity.this);
                    member_pic_imagebtn.setImageBitmap(FriendtoRoundBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pokeball), windowWidth / 7));
                    member_pic_imagebtn.setBackground(null);
                    member_pic_imagebtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            moveMap(activity_latlng.get(0));
                        }
                    });
                    member_info.addView(member_pic_imagebtn);

                    member_name_text = new TextView(MapsActivity.this);
                    member_name_text.setText("寵物活動範圍");
                    member_name_text.setGravity(Gravity.CENTER_HORIZONTAL);
                    member_name_text.setTypeface(null, Typeface.BOLD);
                    member_info.addView(member_name_text);
                    member_list_tableRow.addView(member_info);
                    for (int i = 0; i < friend_size; i++) {
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory.fromBitmap(FriendtoRoundBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pikachu), windowWidth / 7)))
                                .title(pet_update_time.get(i))
                                .snippet("")
                                .position(pet_latlng.get(i)));
                        friend_mark.add(marker);
                        PolygonOptions rectOptions = new PolygonOptions();
                        for(int j = 0; j<pet_history_latlng.size(); j++) {
                            rectOptions.add(pet_history_latlng.get(j));
                        }

                        Polygon polygon = mMap.addPolygon(rectOptions.strokeColor(style_color));
                        //建立下方使用者按鈕
                        member_info = new LinearLayout(MapsActivity.this);
                        member_info.setOrientation(LinearLayout.VERTICAL);

                        member_pic_imagebtn = new ImageButton(MapsActivity.this);
                        member_pic_imagebtn.setImageBitmap(FriendtoRoundBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pikachu), windowWidth / 7));
                        member_pic_imagebtn.setBackground(null);
                        final LatLng finalPlace = pet_latlng.get(i);
                        member_pic_imagebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                moveMap(finalPlace);
                            }
                        });
                        member_info.addView(member_pic_imagebtn);
                        member_name_text = new TextView(MapsActivity.this);
                        member_name_text.setText("寵物");
                        //member_name_text.setTextColor(Color.rgb(255, 255, 255));
                        member_name_text.setGravity(Gravity.CENTER_HORIZONTAL);
                        member_name_text.setTypeface(null, Typeface.BOLD);
                        member_info.addView(member_name_text);
                        member_list_tableRow.addView(member_info);

                    }
                } catch (Exception e) {
                    Log.e("friend_size", e.toString());
                }
            }else {
                Log.d(TAG, "member is null");

            }

            search_group_dialog.dismiss();
        }
    };
    Runnable joinGroup_runnable = new Runnable() {
        @Override
        public void run() {
            while(dataBase.isJoinGroup){
                try {
                    Log.d(TAG, "wait join");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "is Join");
            joinGroup_handler.sendEmptyMessage(0);
        }
    };
    Handler joinGroup_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if (dataBase.JoinGroupResult.substring(0, 1).equals("1")) {
                    Toast.makeText(getApplicationContext(), "對方等待邀請中", Toast.LENGTH_SHORT).show();
                } else if (dataBase.JoinGroupResult.substring(0, 1).equals("2")) {
                    Toast.makeText(getApplicationContext(), "已送出邀請，等待對方確認", Toast.LENGTH_SHORT).show();
                } else if (dataBase.JoinGroupResult.substring(0, 1).equals("3")) {
                    Toast.makeText(getApplicationContext(), "成功加入!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "已在群組內", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Log.e(TAG, e.toString());
            }
            new Thread(initgroupListView_runnable).start();
        }
    };
    Runnable delGroup_runnable = new Runnable() {
        @Override
        public void run() {
            while(dataBase.isDeleteGroup){
                try {
                    Log.d(TAG, "wait del");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            delGroup_handler.sendEmptyMessage(0);
        }
    };
    Handler delGroup_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            search_group_dialog = ProgressDialog.show(MapsActivity.this, " 更新中", "請等待...",true);
            new Thread(initGroup_runnable).start();
        }
    };
    Runnable initGroup_runnable = new Runnable() {
        @Override
        public void run() {
            dataBase.searchGroup(user_id, MapsActivity.this);
            while(dataBase.isSearchGroup){
                try {
                    Log.d(TAG, "wait");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            group_name_list = new ArrayList<String>(dataBase.group_name_list);
            group_id_list = new ArrayList<String>(dataBase.group_id_list);
            group_user_list = new ArrayList<String>(dataBase.group_user_list);
            group_user_id_list = new ArrayList<String>(dataBase.group_user_id_list);
            group_in_list = new ArrayList<String>(dataBase.group_in_list);
            group_type_list = new ArrayList<String>(dataBase.group_type_list);
            Log.d(TAG, "get array" + group_in_list.size());
            initGroup_handler.sendEmptyMessage(0);
        }
    };
    Handler initGroup_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            adapter = new ExpandableListViewAdapter(MapsActivity.this, group_name_list, group_user_id_list, group_type_list, group_id_list,style_color);
            group_expandableListView.setAdapter(adapter);
            if(!group_name_list.isEmpty()) {
                for (int i = 0; i < 5; i++) {
                    group_expandableListView.expandGroup(i);
                }
            }
            mSwipeRefreshLayout.setRefreshing(false);
            String in="0";
            int i=0;
            while (Integer.parseInt(in) == 0 && i<group_in_list.size()){
                i++;
                in = group_in_list.get(i-1);
                Log.d(TAG, in);

            }
            if(Integer.parseInt(in) == 1) {
                now_group = group_id_list.get(i);
                group_name_textView.setText(group_name_list.get(i));
                switch (Integer.valueOf(group_type_list.get(i))){
                    case 0:
                        new Thread(putMember_runnable).start();
                        break;
                    case 1:
                        new Thread(putFriend_runnable).start();
                        break;
                    case 2:
                        new Thread(putClass_runnable).start();
                        break;
                    case 3:
                        new Thread(putBlackList_runnable).start();
                        break;
                    case 4:
                        new Thread(putPet_runnable).start();
                        break;
                }
            } else{
                group_name_textView.setText("你還沒有加入任何群組");
            }
            //search_group_dialog.dismiss();
            //new Thread(initMember_runnable).start();
        }
    };
    Runnable initMember_runnable = new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < group_id_list.size(); i++) {
                dataBase.getGroupMember(group_id_list.get(i), user_id, MapsActivity.this);
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
            String in="0";
            int i=0;
            while (Integer.parseInt(in) == 0 && i<group_in_list.size()){
                i++;
                in = group_in_list.get(i-1);
                Log.d(TAG, in);

            }
            if(Integer.parseInt(in) == 1) {
                now_group = group_id_list.get(i);
                group_name_textView.setText(group_name_list.get(i));
                switch (Integer.valueOf(group_type_list.get(i))){
                    case 0:
                        new Thread(putMember_runnable).start();
                        break;
                    case 1:
                        new Thread(putFriend_runnable).start();
                        break;
                    case 2:
                        new Thread(putClass_runnable).start();
                        break;
                    case 3:
                        new Thread(putBlackList_runnable).start();
                        break;
                    case 4:
                        new Thread(putPet_runnable).start();
                        break;
                }
            } else{
                group_name_textView.setText("你還沒有加入任何群組");
            }
            search_group_dialog.dismiss();
        }
    };
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        View mActionBarView = getLayoutInflater().inflate(R.layout.map_action_bar, null);
        actionBar.setCustomView(mActionBarView);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //actionBar.setTitle("test");
        //actionBar.setIcon(R.drawable.friend);
        group_name_textView = (TextView) mActionBarView.findViewById(R.id.group_name_action_bar);
        left_drawer_open_imgBtn = (ImageButton) mActionBarView.findViewById(R.id.left_drawer_open);
        left_drawer_open_imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout left_linearLayout = (LinearLayout) findViewById(R.id.left_drawer);
                drawerLayout.openDrawer(left_linearLayout);
            }
        });
        ImageButton right_drawer_open_imgBtn = (ImageButton) mActionBarView.findViewById(R.id.right_drawer_open);
        right_drawer_open_imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout right_linearLayout = (LinearLayout)findViewById(R.id.right_drawer);
                drawerLayout.openDrawer(right_linearLayout);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            //getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            //return true;
        //}
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.left_drawer_open:

                return true;
            //case android.R.id.home:
            //    LinearLayout left_linearLayout = (LinearLayout)findViewById(R.id.left_drawer);
            //    drawerLayout.openDrawer(left_linearLayout);
            //    return true;
            //case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                //return true;

            case R.id.creat_group:
                //Intent intentArea = new Intent();
                //intentArea.setClass(MapsActivity.this, AreaActivity.class);
                //Bundle bundle = new Bundle();
                //bundle.putDouble("latitude",latitude );
                //bundle.putDouble("longitude", longitude);
                //intentArea.putExtras(bundle);
                //startActivity(intentArea);
                //Calendar c = Calendar.getInstance();
                //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                //c.add(Calendar.HOUR_OF_DAY, 1);
                //Toast.makeText(getApplicationContext(), simpleDateFormat.format(c.getTime()), Toast.LENGTH_SHORT).show();
                dataBase.getMemberIsIn(user_id);
                //Toast.makeText(getApplicationContext(), dataBase.Notification_output, Toast.LENGTH_SHORT).show();
                //SQLiteDatabase sqLiteDatabase = LocalDataBase.getDatabase(this);
                //Toast.makeText(getApplicationContext(), dataBase.getText(sqLiteDatabase), Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_fblogout:
                accessToken = AccessToken.getCurrentAccessToken();
                if (accessToken != null) {
                    LoginManager.getInstance().logOut();
                    data.saveData("null", this, "UserData", user_picture);
                    Intent intentMap = new Intent();
                    intentMap.setClass(MapsActivity.this, UserActivity.class);
                    startActivity(intentMap);
                    MapsActivity.this.finish();
                }
                return true;

            case R.id.action_dev:
                if(Output.getVisibility() == View.INVISIBLE)
                    Output.setVisibility(View.VISIBLE);
                else
                    Output.setVisibility(View.INVISIBLE);
                return true;
            case R.id.right_drawer_open:
                LinearLayout right_linearLayout = (LinearLayout)findViewById(R.id.right_drawer);
                drawerLayout.openDrawer(right_linearLayout);
                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onStart() {//open background service
        super.onStart();

        if(!isServiceRunning(this, "GpsService")) {
            final String Action = "FilterString";
            IntentFilter filter = new IntentFilter(Action);
            // 將 BroadcastReceiver 在 Activity 掛起來。
            registerReceiver(receiver, filter);
            // 啟動 Service。
            Intent intent = new Intent(this, GpsService.class);
            startService(intent);
            Log.d("Maps", "startService");
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        //unregisterReceiver(receiver);
        //Intent intent = new Intent(this, GpsService.class);
        //stopService(new Intent(intent));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null) {
            setUpMap();
            final Button area_del_btn = (Button)findViewById(R.id.this_area_delete_button);
            final TextView area_time_text = (TextView)findViewById(R.id.this_area_time_textView);
            //下面這個改成下載
            Bundle bundle = this.getIntent().getExtras();
            if(bundle!=null) {
                double center_latitude = bundle.getDouble("center_latitude");
                double center_longitude = bundle.getDouble("center_longitude");
                area_mark_time.add(bundle.getString("area_time"));
                Log.d("Maps", "onActivityResult" + center_latitude + center_longitude);
                LatLng area_latLng = new LatLng(center_latitude, center_longitude);
                mMap.addCircle(new CircleOptions()
                        .center(area_latLng)
                        .fillColor(Color.argb(200, 255, 255, 255))
                        .strokeColor(Color.rgb(255, 192, 203))
                        .strokeWidth(5)
                        .radius(bundle.getInt("area_distance") / 2));
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(FriendtoRoundBitmap(textAsBitmap("  ", windowWidth / 10, Color.rgb(255, 192, 203)), windowWidth / 15));
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .anchor(0.5f, 0.5f)
                        .icon(icon)
                        .title(bundle.getString("area_name"))
                        .snippet(bundle.getString("area_address"))
                        .position(area_latLng));
                area_mark.add(marker);
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (area_mark.contains(marker)) {
                            Log.d(TAG, "area mark");
                            area_del_btn.setVisibility(View.VISIBLE);
                            area_time_text.setVisibility(View.VISIBLE);
                            area_time_text.setText(area_mark_time.get(area_mark.indexOf(marker)));
                        }
                        return false;
                    }
                });
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        area_del_btn.setVisibility(View.INVISIBLE);
                        area_time_text.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        ImageButton pic = (ImageButton)findViewById(R.id.user_pic_imageButton);
        TextView name = (TextView)findViewById(R.id.user_name_textView);
        Output = (TextView)findViewById(R.id.txtOutput);
        area_time_textView = (TextView)findViewById(R.id.time_textView);
        pic.setImageBitmap(user_picture);
        name.setText(user_name);
        name.setTextSize(windowWidth / 25);

        member_list_tableRow =  (TableRow)findViewById(R.id.member_list_tableRow);

        //Bitmap originBitmap = user_picture;//getScreenShot(0, 0);
        //int scaleRatio = 10;
        //int blurRadius = 8;
        //Bitmap scaledBitmap = Bitmap.createScaledBitmap(originBitmap,
        //        originBitmap.getWidth() / scaleRatio,
        //        originBitmap.getHeight() / scaleRatio,
        //        false);
        //Bitmap blurBitmap = FastBlurUtil.doBlur(scaledBitmap, blurRadius, true);
        //BitmapDrawable bdrawable = new BitmapDrawable(getResources(),blurBitmap);
        //member_list_tableRow.setBackground(bdrawable);
    }

    private Bitmap getRoundedBitmap(Bitmap bitmap, int w) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scale = (float)w/width;//螢幕寬度/圖片寬度
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);// 得到新的icon

        return bitmap;
    }
    private Bitmap FriendtoRoundBitmap(Bitmap bitmap, int w) {
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

        output = getRoundedBitmap(output, w);
        return output;
    }
    private void setUpMap() {
        //del.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        new AlertDialog.Builder(MapsActivity.this)
        //                .setTitle("確定要刪除這區域?")
        //                .setMessage("")
        //                .setPositiveButton("刪除", new DialogInterface.OnClickListener() {
        //                    @Override
        //                    public void onClick(DialogInterface dialog, int which) {
        //                        //if(is area del){
        //                        //上傳刪除區域的請求
        //                        destination.get(destination.size() - 1).remove();
        //                        destination.remove(destination.size() - 1);
        //                        destination_mark.get(destination_mark.size() - 1).remove();
        //                        destination_mark.remove(destination_mark.size() - 1);
        //                        add_destination = destination.size();
        //                        linearLayout.setVisibility(View.GONE);
        //                        //}
        //                    }
        //                })
        //                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
        //                    @Override
        //                    public void onClick(DialogInterface dialog, int which) {
        //                    }
        //                })
        //                .show();
        //    }
        //});

        // 建立位置的座標物件
        LatLng place = new LatLng(latitude, longitude);
        // 移動地圖
        if(IS_FIRST <= 1)
            moveMap(place);
        IS_FIRST++;
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        addMarker(place, user_name, user_email + "\n" + user_id, user_picture);
    }
    // 移動地圖到參數指定的位置
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
    // 在地圖加入指定位置與標題的標記
    private void addMarker(LatLng place, String title, String snippet, Bitmap bitmap) {
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
        if(marker != null) {
            marker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(place).title(title).snippet(snippet).icon(icon);
        marker = mMap.addMarker(markerOptions);

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 處理 Service 傳來的訊息。
            double Lat = intent.getDoubleExtra("Lat", 0.0);
            double Long = intent.getDoubleExtra("Long", 0.0);
            float Accuracy = intent.getFloatExtra("Accuracy", 0.0f);
            float Bearing = intent.getFloatExtra("Bearing", 0.0f);
            float Speed = intent.getFloatExtra("Speed", 0.0f);
            String Time = intent.getStringExtra("Time");
            int Count = intent.getIntExtra("Count", 0);
            Output.setText("Lat: " + Lat + "\n" +
                    "Long" + Long + "\n" +
                    "Accuracy" + Accuracy + "\n" +
                    "Bearing" + Bearing + "\n" +
                    "Speed" + Speed + "\n" +
                    "Time" + Time + "\n" +
                    "Count" + Count + "\n");
            latitude = Lat;
            longitude = Long;
            //String coordinate = String.valueOf(Lat) + String.valueOf(Long);
            //dataBase.updateDB(user_name, user_id, coordinate, user_email, Time);
            if(now_group!=null && is_put_member_first_time) {
                switch (Integer.valueOf(group_type_list.get(group_id_list.indexOf(now_group)))){
                    case 0:
                        new Thread(putMember_runnable).start();
                        break;
                    case 1:
                        new Thread(putFriend_runnable).start();
                        break;
                    case 2:
                        new Thread(putClass_runnable).start();
                        break;
                    case 3:
                        new Thread(putBlackList_runnable).start();
                        break;
                    case 4:
                        new Thread(putPet_runnable).start();
                        break;
                }
                Log.d(TAG, "更新位置");
            }
        }
    };
    public static boolean isServiceRunning(Context context, String serviceClassName){
        final ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
                return true;
            }
        }
        return false;
    }

    public Bitmap textAsBitmap(String text, float textSize, int textColor) {
        if(text.equals(null))
            text=" ";
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawColor(textColor);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }
    private Bitmap getScreenShot(int w, int h)
    {
        //藉由View來Cache全螢幕畫面後放入Bitmap
        View mView = getWindow().getDecorView();
        mView.setDrawingCacheEnabled(true);
        mView.buildDrawingCache();
        Bitmap mFullBitmap = mView.getDrawingCache();

        //取得系統狀態列高度
        Rect mRect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(mRect);
        int mStatusBarHeight = mRect.top;

        //取得手機螢幕長寬尺寸
        int mPhoneWidth = getWindowManager().getDefaultDisplay().getWidth();
        int mPhoneHeight = getWindowManager().getDefaultDisplay().getHeight();

        int ww = (mPhoneWidth-w)/2;
        int hh = (mPhoneHeight-h)/2;

        //將狀態列的部分移除並建立新的Bitmap
        //Bitmap mBitmap = Bitmap.createBitmap(mFullBitmap, ww, hh, mPhoneWidth-ww, mPhoneHeight - hh);
        Bitmap mBitmap = Bitmap.createBitmap(mFullBitmap, 0, mStatusBarHeight, mPhoneWidth, mPhoneHeight - mStatusBarHeight);
        //將Cache的畫面清除
        mView.destroyDrawingCache();

        return mBitmap;
    }
    public static Bitmap fastblur(Context context, Bitmap sentBitmap, int radius, View view)  {
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        final RenderScript rs = RenderScript.create(context);
        final Allocation input = Allocation.createFromBitmap(rs, sentBitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(rs,input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(radius);/* e.g. 3.f */
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap);
        view.setBackground(new BitmapDrawable(view.getResources(), bitmap));
        return bitmap;
    }
    private static Bitmap getSmallImage(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.5f, 0.5f); //長寬縮放比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
    }
    private Bitmap changeColor(Bitmap src,int setr, int setg, int setb){
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

                if(alpha != 0) {
                    r = setr;
                    g = setg;
                    b = setb;
                }

                newBitmap[index] = alpha | (r << 16) | (g << 8) | b;
            }
        }

        Bitmap bm = Bitmap.createBitmap(bmWidth, bmHeight, Bitmap.Config.ARGB_8888);
        bm.setPixels(newBitmap, 0, bmWidth, 0, 0, bmWidth, bmHeight);

        return bm;
    }
    private void initSearchFriend_dialog(){
        LayoutInflater inflater = LayoutInflater.from(MapsActivity.this);
        search_dialog_view = inflater.inflate(R.layout.alertdialog_wait, null);
        search_dialog_view_editText = (EditText) (search_dialog_view.findViewById(R.id.search_friend_editText));
        search_dialog_view_textView = (TextView) (search_dialog_view.findViewById(R.id.search_friend_textView));
        Button button = (Button) (search_dialog_view.findViewById(R.id.search_friend_button));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataBase.searchfriend(search_dialog_view_editText.getText().toString());
                new Thread(search_friend_runnable).start();
                search_dialog_view_textView.setText("尋找中...");
            }
        });
        search_friend_found_user = (LinearLayout)(search_dialog_view.findViewById(R.id.search_friend_found_user));
        search_friend_pic = (ImageView)(search_dialog_view.findViewById(R.id.search_friend_pic_imageView));
        search_friend_name = (TextView)(search_dialog_view.findViewById(R.id.search_friend_name_textView));
        search_friend_email = (TextView)(search_dialog_view.findViewById(R.id.search_friend_email_textView));
        search_friend_list = (ListView)(search_dialog_view.findViewById(R.id.search_friend_listView));
        search_friend_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (now_group != null) {
                    String id = friends_id.get(i);
                    dataBase.joinGroup(now_group, id, "0");
                    search_group_dialog = ProgressDialog.show(MapsActivity.this, " 更新中", "請等待...",true);
                    new Thread(joinGroup_runnable).start();
                    //Toast.makeText(getApplicationContext(), friends_name.get(i), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "請選擇群組", Toast.LENGTH_SHORT).show();
                }
            }
        });
        search_friend_found_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(now_group!=null) {
                    dataBase.joinGroup(now_group, dataBase.search_friend_id, "0");
                    search_group_dialog = ProgressDialog.show(MapsActivity.this, " 更新中", "請等待...",true);
                    new Thread(joinGroup_runnable).start();
                    //Toast.makeText(getApplicationContext(), dataBase.search_friend_id, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "請選擇群組", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    Runnable search_friend_runnable = new Runnable() {
        @Override
        public void run() {
            while(dataBase.isSearchfriend){
                try {
                    Log.d(TAG, "wait");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            search_friend_handler.sendEmptyMessage(0);
        }
    };
    Handler search_friend_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(dataBase.search_friend_name==null) {
                search_dialog_view_textView.setText("未找到");
                search_friend_found_user.setVisibility(View.GONE);
            }
            else {
                search_dialog_view_textView.setText("找到");
                search_friend_found_user.setVisibility(View.VISIBLE);
                search_friend_pic.setImageBitmap(FriendtoRoundBitmap(dataBase.search_fridnd_pic, windowWidth/5));
                search_friend_name.setText(dataBase.search_friend_name);
                search_friend_email.setText(dataBase.search_friend_email);
            }
        }
    };

    private void initRollcall_dialog(LatLng areaLatLng, int areameter){
        LayoutInflater inflater = LayoutInflater.from(MapsActivity.this);
        rollcall_dialog_view = inflater.inflate(R.layout.alertdialog_rollcall, null);
        TableRow on_time_row = (TableRow) (rollcall_dialog_view.findViewById(R.id.on_time_list_tableRow));
        TableRow be_late_row = (TableRow) (rollcall_dialog_view.findViewById(R.id.late_list_tableRow));


        ImageButton member_pic_imagebtn;
        TextView member_name_text;
        TextView late_distance;
        LatLng member_place;
        Bitmap member_pic;

        int on_time_count = 0;
        int be_late_count = 0;

        try {
            LinearLayout member_info;
            for(int i = 0; i<member_name_list.size(); i++) {
                member_place = new LatLng(Double.parseDouble(member_coordinate_list.get(i).substring(0, member_coordinate_list.get(i).indexOf('s')))
                        , Double.parseDouble(member_coordinate_list.get(i).substring(member_coordinate_list.get(i).indexOf('s')+1)));
                member_pic = member_pic_list.get(i);
                member_pic = FriendtoRoundBitmap(member_pic, windowWidth / 7);

                float results[] = new float[1];
                Location.distanceBetween(member_place.latitude, member_place.longitude, areaLatLng.latitude, areaLatLng.longitude, results);
                int distance = (int)results[0];
                int area_meter = areameter/2;

                //建立下方使用者按鈕
                member_info = new LinearLayout(MapsActivity.this);
                member_info.setOrientation(LinearLayout.VERTICAL);

                member_pic_imagebtn = new ImageButton(MapsActivity.this);
                member_pic_imagebtn.setImageBitmap(member_pic);
                member_pic_imagebtn.setBackground(null);
                member_info.addView(member_pic_imagebtn);

                member_name_text = new TextView(MapsActivity.this);
                member_name_text.setText(member_name_list.get(i));
                member_name_text.setGravity(Gravity.CENTER_HORIZONTAL);
                member_name_text.setTypeface(null, Typeface.BOLD);
                member_info.addView(member_name_text);
                if(distance>area_meter){
                    late_distance = new TextView(MapsActivity.this);
                    if(distance>999){
                        distance=distance/1000;
                        late_distance.setText(distance+"公里");
                    }else {
                        late_distance.setText(distance+"公尺");
                    }



                    late_distance.setGravity(Gravity.CENTER_HORIZONTAL);
                    member_info.addView(late_distance);
                    be_late_row.addView(member_info);
                    be_late_count++;
                }else {
                    on_time_row.addView(member_info);
                    on_time_count++;
                }
            }
            TextView member_is_null = new TextView(MapsActivity.this);
            member_is_null.setText("無");
            member_is_null.setGravity(Gravity.CENTER_HORIZONTAL);
            member_is_null.setTypeface(null, Typeface.BOLD);
            if(be_late_count==0){
                be_late_row.addView(member_is_null);
            }else if(on_time_count==0){
                on_time_row.addView(member_is_null);
            }
        } catch (Exception e){
            Log.e("friend_size", e.toString());
        }
    }
    private boolean isDataExpired(String date){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date systemTime = new Date(System.currentTimeMillis()) ; // 獲取當前時間
        String systemTimeStr = formatter.format(systemTime);
        Log.d(TAG, date+"\n"+systemTimeStr);
        //取的兩個時間
        Date nowTime = null;
        Date oldTime = null;
        try {
            nowTime = formatter.parse(systemTimeStr);
            oldTime = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//取得兩個時間的Unix時間
        Long ut1=nowTime.getTime();
        Long ut2=oldTime.getTime();
//相減獲得兩個時間差距的毫秒
        Long timeP=ut1-ut2;//毫秒差
        Long min=timeP/60000;//分差
        Log.d(TAG, timeP+" "+min);
        if(min>10)
            return true;
        else
            return false;
    }
}
