package le1779.whereareyou;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity {

    private TextView error;
    private String user_email = null;
    private String user_password = null;
    private String user_checkpassword = null;
    private String user_data = "";
    Data data = new Data();
    final DataBase dataBase = new DataBase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final EditText email = (EditText)findViewById(R.id.email_editText);
        final EditText password = (EditText)findViewById(R.id.password_editText);
        final EditText check_password = (EditText)findViewById(R.id.checkpassword_editText);
        Button Sign_In = (Button)findViewById(R.id.SignIn_button);
        Button Sign_Up = (Button)findViewById(R.id.SignUp_button);
        error = (TextView)findViewById(R.id.error_textView);



        if(data.readfile(SignUpActivity.this, "UserData")) {
            user_email = data.getEmail();
            user_password = data.getPassword();
            email.setText(user_email);
            password.setText(user_password);
        }
        Sign_In.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                error.setText("");
                user_email = email.getText().toString();
                user_password = password.getText().toString();
                //dataBase.checkDB(user_email, user_password);
                new Thread(runnable1).start();
            }
        });
        Sign_Up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                error.setText("");
                user_email = email.getText().toString();
                user_password = password.getText().toString();
                user_checkpassword = check_password.getText().toString();
                if(user_password.equals(user_checkpassword)){
                    dataBase.creatData(user_email, null, null, null, null, user_password);
                    new Thread(runnable2).start();
                }
                else{
                    error.setText("密碼驗證錯誤");
                }
            }
        });
    }

    Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            while(!dataBase.checkDone){//檢查完成離開
                try{
                    Thread.sleep(500);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            Message msg = new Message();
            if(dataBase.isDataExist) {//檢查用戶是否存在
                if(dataBase.isCorrect){
                    user_data += user_email + "\n";
                    user_data += user_password + "\n";
                    //data.saveData(user_data, SignUpActivity.this, "UserData");
                    msg.what = 2;
                    handler.sendMessage(msg);
                } else{
                    msg.what = 3;
                    handler.sendMessage(msg);
                }
            } else{//新用戶
                msg.what = 4;
                handler.sendMessage(msg);
            }
        }
    };
    Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            while(!dataBase.checkDone){
                try{
                    Thread.sleep(500);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            Message msg = new Message();
            if(!dataBase.isCreatData) {//檢查用戶是否存在
                msg.what = 0;
                handler.sendMessage(msg);
            } else{//新用戶
                user_data += user_email + "\n";
                user_data += user_password + "\n";
                //data.saveData(user_data, SignUpActivity.this, "UserData");
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }
    };
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    error.setText("用戶已存在。");
                    break;
                case 1:
                    error.setText("創建成功，請登入。");
                    break;
                case 2:
                    error.setText("登入成功。");
                    Intent intent = new Intent();
                    intent.setClass(SignUpActivity.this, MapsActivity.class);
                    startActivity(intent);
                    SignUpActivity.this.finish();
                    break;
                case 3:
                    error.setText("密碼錯誤。");
                    break;
                case 4:
                    error.setText("查無帳號。");
                    break;
            }
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

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
}
