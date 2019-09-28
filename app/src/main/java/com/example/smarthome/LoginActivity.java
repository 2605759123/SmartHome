package com.example.smarthome;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthome.Data.user_data;
import com.example.smarthome.chenjin.StatusBarUtil;
import com.example.smarthome.utils.Config;
import com.example.smarthome.utils.DataManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    SharedPreferences sp;
    String user = "", pwd = "", address = "",port="";//ocean平台处
    EditText textusername;
    EditText textPassword;//数据库端
    TextView Register;
    private CheckBox rem_pw, auto_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this,true);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
//        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
//            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
//            //这样半透明+白=灰, 状态栏的文字能看得清
//            StatusBarUtil.setStatusBarColor(this,0x55000000);
//        }

        Button button1=(Button)findViewById(R.id.mylogin);
        textusername=(EditText)findViewById(R.id.username);
        textPassword=(EditText)findViewById(R.id.password);
        Register=(TextView) findViewById(R.id.SignIn);
        rem_pw = (CheckBox) findViewById(R.id.cb_mima);
        auto_login = (CheckBox) findViewById(R.id.cb_auto);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        //Toast.makeText(MyActivity1.this,"警告：目前只允许存在一个设备，如果有多个，只能获取最后添加设备的数据",Toast.LENGTH_LONG).show();
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                try {
                    LoginRequest();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        textusername.setText(sp.getString("account",""));
        init();
        //sp = PreferenceManager.getDefaultSharedPreferences(this);
        //init();

        //判断记住密码多选框的状态
        if(sp.getBoolean("ISCHECK", false))
        {
            //设置默认是记录密码状态
            rem_pw.setChecked(true);
            textusername.setText(sp.getString("USER_NAME",""));
            textPassword.setText(sp.getString("PASSWORD", ""));
            //判断自动登陆多选框状态
            if(sp.getBoolean("AUTO_ISCHECK", false))
            {
                //设置默认是自动登录状态
                auto_login.setChecked(true);
                //跳转界面
                try {
                    LoginRequest();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        //监听记住密码多选框按钮事件
        rem_pw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (rem_pw.isChecked()) {
                    Log.i("xuanzhong","记住密码已选中");
                    sp.edit().putBoolean("ISCHECK", true).commit();

                }else {
                    Log.i("xuanzhong","记住密码没有选中");
                    sp.edit().putBoolean("ISCHECK", false).commit();

                }

            }
        });

        //监听自动登录多选框事件
        auto_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (auto_login.isChecked()) {
                    Log.i("xuanzhong","自动登陆已选中");
                    sp.edit().putBoolean("AUTO_ISCHECK",true).commit();

                } else {
                    Log.i("xuanzhong","自动登陆没有选中");
                    sp.edit().putBoolean("AUTO_ISCHECK", false).commit();
                }
            }
        });
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
//                    //如果没有权限
//
//                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.CALL_PHONE},10111);//请求电话权限
//                }
//            }
//        }).start();

    }
    // private void init(){
    //    login_appid = sp.getString("appId",""); //账号
    //     token = sp.getString("token", "");  //密码
    // }









    private void init(){
        user = sp.getString("appId", "2WihY1xyy5IvM9m9r4zEzpin6IQa");//华为应用服务器登陆用户名
        user_data.user=user;
        pwd = sp.getString("userpwd", "ziO5jsuCw4BeFvp2imHiwMc3Ccoa");//华为应用服务器登陆用户名
        address = sp.getString("seraddress", "139.159.133.59");//华为云登陆地址
        port = sp.getString("post", "8743");//华为云登陆端口
    }

    private void Login_into() {
        address=address.toString().trim();
        port=port.toString().trim();
        user=user.toString().trim();
        pwd=pwd.toString().trim();

        Config.all_url="https://"+address+":"+port;
        Init_Demand_HTTPS();
    }
    private void Init_Demand_HTTPS() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Message msg = new Message();
                Bundle data = new Bundle();
                try {
                    String url="https://"+address+":"+port+"/iocm/app/sec/v1.1.0/login";
//                    https://218.4.33.71:8743/iocm/app/sec/v1.1.0/login
                    String json= DataManager.Login_Request(LoginActivity.this,url,user,pwd); //获取token
                    data.putString("json", json);
                    Log.i("bbbb", "josn1" + json);
                } catch ( Exception e ) {
                    Log.i("debugg",Log.getStackTraceString(e));
                    data.putString("errmsg", e.getMessage());
                }
                msg.setData(data);
                loginhandler.sendMessage(msg);
            }
        }).start();
    }
    private Handler loginhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String json = data.getString("json");
            Log.i("aaa", "josn1" + json);
//            {
//                "accessToken": "dc4d1368-2e43-4185-b22b-13714e22eb92",
//                    "tokenType": "bearer",
//                    "refreshToken": "a769565a-7d0a-4079-9fe3-1a97b84309a8",
//                    "expiresIn": 43200,
//                    "scope": "[read, write]"
//            }
            String token="";
            try {
                JSONObject jsono = new JSONObject(json);
                token=jsono.getString("accessToken");
//                sp.edit().putString("token",token).commit();//不建议
                user_data.token=token;
                sp.edit().putString("appId",user.trim()).commit();
                user_data.appId=user.trim();
                sp.edit().putString("userpwd",pwd.trim()).commit();
                sp.edit().putString("seraddress",address.trim()).commit();
                sp.edit().putString("post",port.trim()).commit();
//                sp.edit().putString("account",textusername.getText().toString().trim()).commit();//用户名写入配置  不建议
//                sp.edit().putString("LoginPassword",textPassword.getText().toString().trim()).commit();//密码写入配置
                user_data.account=textusername.getText().toString().trim();
                user_data.pwd=textPassword.getText().toString().trim();
                //此处进行了修改
//                获取到的accessToken
            }catch (Exception e ){
                e.printStackTrace();
                Log.i("debugg",Log.getStackTraceString(e));
                Toast.makeText(LoginActivity.this, "请求失败,请检查参数设置！", Toast.LENGTH_SHORT).show();
                return;
            }
            //登录成功和记住密码框为选中状态才保存用户信息
            if(rem_pw.isChecked())
            {
                //记住用户名、密码、
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("USER_NAME", String.valueOf(textusername.getText()));
                editor.putString("PASSWORD", String.valueOf(textPassword.getText()));
                editor.commit();
            }

            GetConfigfromhuawei();
        }
    };

    private void GetConfigfromhuawei(){
//        returndata=0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("pppppppppppppppppp",user_data.token);
                    String add_url = Config.all_url + "/iocm/app/dm/v1.3.0/devices/"+user_data.Intention+"?appId=" + user;
                    Log.i("bbbbbbbbb",add_url);
                    String json = DataManager.Txt_REQUSET(LoginActivity.this, add_url, user, user_data.token);
                    Log.i("json1111:",json);
//                    mlist = new ArrayList<DataInfo>();

                    JSONObject jo = new JSONObject(json);
                    JSONArray jsonArray = jo.getJSONArray("services");
                    Log.i("jo999",jsonArray.getJSONObject(0).toString());
                    JSONObject jo2=new JSONObject(jsonArray.getJSONObject(0).toString());
                    String data_s=jo2.getString("data");
                    JSONObject data_ob=new JSONObject(data_s);
                    Log.i("xinqing",data_ob.getString("xinqing"));
                    user_data.xinqing=data_ob.getString("xinqing");
                    user_data.wendu=data_ob.getString("wendu");
                    user_data.shidu=data_ob.getString("shidu");

                    Intent intent=new Intent(getApplication(), MainActivity.class);
                    startActivity(intent);
                    finish();

                } catch ( Exception e ) {
                    Log.i("debug",Log.getStackTraceString(e));
                    toast("Config获取失败");
                    e.printStackTrace();
                }


            }
        }).start();

    }



    //------------以下为数据库登陆验证的地方

    public void LoginRequest() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("poi2","123");
                HttpURLConnection connection=null;
                BufferedReader reader=null;
                String account=textusername.getText().toString();
                String password=textPassword.getText().toString();
                Log.i("1user",account);
                if (account.equals("")){
                    Log.i("poi3","123");
                    toast("请输入用户名");
                }else if (password.equals("")){
                    Log.i("poi4","123");
                    toast("请输入密码");
                }else{
                    Log.i("poi5","123");
                    try{
                        String url1 = "http://47.102.201.183:8080/test2/LoginServlet?account="+account+"&password="+password;
                        Log.i("url111",url1);
                        //String tag = "Login";
                        URL url=new URL(url1);
                        connection=(HttpURLConnection)url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(8000);
                        connection.setReadTimeout(8000);
                        InputStream in=connection.getInputStream();
                        //下面对获取到的输入流进行读取
                        reader=new BufferedReader(new InputStreamReader(in));
                        StringBuilder response=new StringBuilder();
                        String line;
                        while ((line=reader.readLine())!=null){
                            response.append(line);
                        }
                        String result="";
                        result=LoginJSON(response.toString());
                        Log.i("result",result);
                        if (result.equals("")){
                            //数据库连接不上
                            toast("验证账号密码失败，请检查网络或联系我们");
                        }else if (result.equals("success")){
                            //数据库验证登陆成功
                            Log.i("poi6","123");
                            SearchRequest();//开始获取config
                        }else if(result.equals("failed")){
                            //数据库验证账号或密码错误
                            toast("账号或密码错误，请检查");
                        }
                    }catch (Exception e){
                        toast("验证账号密码失败，请检查网络或联系我们");
                        Log.i("debugg",Log.getStackTraceString(e));
                        e.printStackTrace();
                    }finally {
                        if(reader!=null){
                            try {
                                reader.close();
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                        if (connection!=null){
                            connection.disconnect();
                        }
                    }
                }

            }
        }).start();
    }
    private void toast(final String toast){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this,toast,Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String LoginJSON(String jsondata){
        try{
            Log.i("jsondata",jsondata);
            JSONObject js=new JSONObject(jsondata);
            String jsonObject=js.getJSONObject("params").toString();
            Log.i("3232:",jsonObject);
            JSONObject object=new JSONObject(jsonObject);
            String ob=object.optString("Result");
            Log.i("ob",ob);
            return ob;

        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }



    //以下是获取服务器配置数据
    public void SearchRequest() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("poi2","123");
                HttpURLConnection connection=null;
                BufferedReader reader=null;
                String account=textusername.getText().toString();
                String password=textPassword.getText().toString();
                Log.i("1user",account);
                if (account.equals("")){
                    Log.i("poi3","123");
                    toast("请输入用户名");
                }else if (password.equals("")){
                    Log.i("poi4","123");
                    toast("请输入密码");
                }else{
                    Log.i("poi5","123");
                    try{
                        String url1 = "http://47.102.201.183:8080/test2/SearchServlet?account="+account+"&password="+password;
                        //String tag = "Login";
                        URL url=new URL(url1);
                        connection=(HttpURLConnection)url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(8000);
                        connection.setReadTimeout(8000);
                        InputStream in=connection.getInputStream();
                        //下面对获取到的输入流进行读取
                        reader=new BufferedReader(new InputStreamReader(in));
                        StringBuilder response=new StringBuilder();
                        String line;
                        while ((line=reader.readLine())!=null){
                            response.append(line);
                        }
                        String result="";
                        result=IntentionJSON(response.toString());
                        Log.i("result",result);
                        Login_into();//开始连接ocean


                    }catch (Exception e){
                        toast("数据库连接失败，请联系我们");
                        Log.i("debugg",Log.getStackTraceString(e));
                        e.printStackTrace();
                    }finally {
                        if(reader!=null){
                            try {
                                reader.close();
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                        if (connection!=null){
                            connection.disconnect();
                        }
                    }
                }

            }
        }).start();
    }

    private String IntentionJSON(String jsondata){
        try{
            Log.i("jsondata",jsondata);
            JSONObject js=new JSONObject(jsondata);
            String jsonObject=js.getJSONObject("1").toString();
            Log.i("3232:",jsonObject);
            JSONObject object=new JSONObject(jsonObject);
            String ob=object.optString("Intention");
            user_data.Intention=object.optString("Intention");
            user_data.qichuangtime=object.optString("qichuangtime");
            Log.i("qichuang",user_data.qichuangtime);
            user_data.shuijiaotime=object.optString("shuijiaotime");
            user_data.qichuangpower= object.optString("qichuangpower");
            user_data.shuijiaopower=object.optString("shuijiaopower");
//            user_data.power=object.optString("power");
            user_data.chuanglianpower=object.optString("chuanglianpower");
            user_data.qichuangyanshi=object.optString("qichuangyanshi");
            user_data.shuijiaoyanshi=object.getString("shuijiaoyanshi");
            Log.i("user_data",user_data.qichuangtime+"   "+user_data.shuijiaotime+"    "+user_data.qichuangpower+"    "+user_data.shuijiaopower+"     "+user_data.power+"     "+user_data.chuanglianpower+"\n"+user_data.qichuangyanshi+"      "+user_data.shuijiaoyanshi);
            Log.i("ob",ob);
            return ob;

        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }



}
