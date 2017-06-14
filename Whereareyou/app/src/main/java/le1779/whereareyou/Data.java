package le1779.whereareyou;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by kevin on 2015/12/13.
 */
public class Data {
    //private String filename = "UserData";
    private String TAG = "data";
    private String id;
    private String name;
    private String email;
    private Bitmap picture;
    private String password;

    public Data(){

    }
    public boolean saveData(String data, Context context, String filename, Bitmap bitmap){
        try {
            FileOutputStream output = context.openFileOutput(filename, Context.MODE_PRIVATE);
            //BufferedReader BR = new BufferedReader(new StringReader(c));
            output.write(data.getBytes());//write date
            Log.i(TAG, "write" + data);
            //save picture
            output = context.openFileOutput("user_picture", Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            return false;

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            return false;
        }
    }
    public boolean readfile(Context context, String filename){
        try {
            FileInputStream fin = context.openFileInput(filename);
            DataInputStream in = new DataInputStream(fin);
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));
            email = br.readLine();
            if(email.equals("null"))
                return false;
            id = br.readLine();
            name = br.readLine();
            //read picture
            fin = context.openFileInput("user_picture");
            picture = BitmapFactory.decodeStream(fin);
            in.close();
            return true;
        }catch(Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            picture = null;
            return false;
        }
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getEmail(){
        return email;
    }
    public Bitmap getPicture(){
        return picture;
    }
    public String getPassword(){
        return password;
    }
}
