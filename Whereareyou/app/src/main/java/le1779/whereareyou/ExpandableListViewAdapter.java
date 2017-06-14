package le1779.whereareyou;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevin on 2016/9/26.
 */
public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

    private Context context;
    private String[] group_type_text = new String[]{
            "家人群組","朋友群組","老師群組","黑單群組","寵物群組"
    };
    ArrayList<String> group_name;
    ArrayList<String> group_id;
    ArrayList<String> group_type;
    ArrayList<String> member_name;

    public List<List<String>> group_id_list;
    List<List<String>> member_name_list;

    ArrayList<String> family_group;
    ArrayList<String> teacher_group;
    ArrayList<String> friend_group;
    ArrayList<String> pet_group;
    ArrayList<String> blacklist_group;
    ArrayList<String> family_member;
    ArrayList<String> teacher_member;
    ArrayList<String> friend_member;
    ArrayList<String> blacklist_member;
    ArrayList<String> pet_member;

    int style_color;


    public ExpandableListViewAdapter(Context context, ArrayList<String> group_name, ArrayList<String> member_name, ArrayList<String> group_type, ArrayList<String> group_id, int color){
        this.context = context;
        this.group_name = group_name;
        this.group_id = group_id;
        this.member_name = member_name;
        this.group_type = group_type;
        style_color = color;
        family_group = new ArrayList<String>();
        teacher_group = new ArrayList<String>();
        friend_group = new ArrayList<String>();
        blacklist_group = new ArrayList<String>();
        pet_group = new ArrayList<String>();
        family_member = new ArrayList<String>();
        teacher_member = new ArrayList<String>();
        friend_member = new ArrayList<String>();
        blacklist_member = new ArrayList<String>();
        pet_member = new ArrayList<String>();
        if(!group_name.isEmpty()) {
            for (int i = 0; i < group_name.size(); i++) {
                Log.d("EX", group_type.get(i));
                switch (group_type.get(i)) {
                    case "0":
                        family_group.add(group_id.get(i));
                        family_member.add(member_name.get(i));
                        break;
                    case "1":
                        teacher_group.add(group_id.get(i));
                        teacher_member.add(member_name.get(i));
                        break;
                    case "2":
                        friend_group.add(group_id.get(i));
                        friend_member.add(member_name.get(i));
                        break;
                    case "3":
                        blacklist_group.add(group_id.get(i));
                        blacklist_member.add(member_name.get(i));
                        break;
                    case "4":
                        pet_group.add(group_id.get(i));
                        pet_member.add(member_name.get(i));
                        break;
                }
            }
            group_id_list = new ArrayList<List<String>>();
            member_name_list = new ArrayList<List<String>>();
            try {
                group_id_list.add(family_group);
                group_id_list.add(teacher_group);
                group_id_list.add(friend_group);
                group_id_list.add(blacklist_group);
                group_id_list.add(pet_group);

                member_name_list.add(family_member);
                member_name_list.add(teacher_member);
                member_name_list.add(friend_member);
                member_name_list.add(blacklist_member);
                member_name_list.add(pet_member);
            }catch (Exception e){
                Log.e("expand", e.toString());
            }
        }
        //for(int i=0; i<4; i++){
        //    if(group_name_list.get(i).isEmpty()){
        //        group_name_list.get(i).add("無");
        //    }
        //}
    }

    @Override
    public int getGroupCount() {
        return group_type_text.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return group_id_list.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return group_type_text[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return group_id_list.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean b, View view, ViewGroup viewGroup) {
        RelativeLayout relativeLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        relativeLayout.setLayoutParams(layoutParams);
        TextView group_type = new TextView(context);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        group_type.setLayoutParams(layoutParams);
        group_type.setText(group_type_text[groupPosition]);
        group_type.setGravity(Gravity.CENTER_HORIZONTAL);
        switch (groupPosition){
            case 0:
                group_type.setTextColor(Color.rgb(252, 60, 159));
                break;
            case 1:
                group_type.setTextColor(Color.rgb(255, 174, 39));
                break;
            case 2:
                group_type.setTextColor(Color.rgb(123, 84, 235));
                break;
            case 3:
                group_type.setTextColor(Color.rgb(0, 0, 0));
                break;
            case 4:
                group_type.setTextColor(Color.rgb(41, 136, 102));
                break;
        }
        group_type.setTextSize(20);
        relativeLayout.addView(group_type);
        return group_type;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.expandable_listview_group, null);
        ImageView iconImgView = (ImageView)view.findViewById(R.id.group_icon_imageView);
        TextView group_name_textView = (TextView)view.findViewById(R.id.group_name_textView);
        TextView member_name_textView = (TextView)view.findViewById(R.id.member_name_textView);
        Bitmap icon = null;
        switch (groupPosition){
            case 0:
                group_name_textView.setTextColor(Color.rgb(252, 60, 159));
                icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.home_group_icon);
                break;
            case 1:
                group_name_textView.setTextColor(Color.rgb(255, 174, 39));
                icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.friend_group_icon);
                break;
            case 2:
                group_name_textView.setTextColor(Color.rgb(123, 84, 235));
                icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.teacher_group_icon);
                break;
            case 3:
                group_name_textView.setTextColor(Color.rgb(0, 0, 0));
                icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.black_group_icon);
                break;
            case 4:
                group_name_textView.setTextColor(Color.rgb(47, 219, 107));
                icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.pokeball);
                break;
        }
        icon = getSmallImage(icon);
        iconImgView.setImageBitmap(icon);
        Log.d(group_type_text[groupPosition] + "size", String.valueOf(group_id_list.get(groupPosition).size()));
        group_name_textView.setText(group_name.get(group_id.indexOf(group_id_list.get(groupPosition).get(childPosition))));
        group_name_textView.setTextSize(30);
        //group_name.setWidth(1);
        //linearLayout.addView(group_name);
        //TextView member_name = new TextView(context);
        member_name_textView.setText(member_name_list.get(groupPosition).get(childPosition));
        //member_name.setTextSize(20);
        //member_name.setWidth(1);
        //linearLayout.addView(member_name);
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private static Bitmap getSmallImage(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.5f,0.5f); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
    }
}
