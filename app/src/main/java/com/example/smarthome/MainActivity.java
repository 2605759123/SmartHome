package com.example.smarthome;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthome.Data.user_data;
import com.example.smarthome.chenjin.StatusBarUtil;
import com.example.smarthome.fragment.HomeFragment;
import com.example.smarthome.fragment.MeFragment;
import com.example.smarthome.utils.Config;
import com.example.smarthome.utils.DataManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends FragmentActivity {

    @Bind(R.id.content)
    FrameLayout content;
    @Bind(R.id.iv_home)
    ImageView ivHome;
    @Bind(R.id.tv_home)
    TextView tvHome;
    @Bind(R.id.ll_home)
    LinearLayout llHome;
    @Bind(R.id.iv_power)
    ImageView ivPower;
    @Bind(R.id.ll_power)
    LinearLayout llPower;
    @Bind(R.id.iv_me)
    ImageView ivMe;
    @Bind(R.id.tv_me)
    TextView tvMe;
    @Bind(R.id.ll_me)
    LinearLayout llMe;
    private HomeFragment homeFragment;
    private MeFragment meFragment;
    private FragmentTransaction ft;
    private String login_appid = "";
    private String json=null;
    private String token = "";
    private String tuatus="201";
    private String deletetuatus="204";
    private String login_account;//用户的账号
    private String login_password;//用户的密码
    private String gatewayId;
    SharedPreferences sp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
//        StatusBarUtil.setRootViewFitsSystemWindows(this,true);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this,0x55000000);
        }
        ButterKnife.bind(this);
        initData();//进入的时候 显示首页
        initsp();//初始化sp内容 token等
        initUserData();//初始化用户的id和pwd
        try {
            SearchRequest();//获取数据库中存的gatewayid
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initUserData() {
        login_account= user_data.account;
        login_password=user_data.pwd;
        token=user_data.token;
    }

    private void initsp() {
        sp=PreferenceManager.getDefaultSharedPreferences(this);//init sp
        login_appid = sp.getString("appId","");
//        token = sp.getString("token", "");
    }

    private void initData() {
        setSelect(0);
    }

    @OnClick({R.id.ll_home,R.id.ll_power,R.id.ll_me})
    public void changeTab(View view){
        switch (view.getId()){
            case R.id.ll_home:
                setSelect(0);
                break;
            case R.id.ll_power:
                setSelect(1);
                break;
            case R.id.ll_me:
                setSelect(2);
                break;

        }
    }

    private void setSelect(int i) {
        FragmentManager fm=getSupportFragmentManager();
        ft = fm.beginTransaction();
//        hideFragment();
        resetTab();
        switch (i){
            case 0:
                //首页
                hideFragment();
                if (homeFragment==null){
                    homeFragment = new HomeFragment();
                    ft.add(R.id.content,homeFragment);
                }
                ft.show(homeFragment);
                ivHome.setImageResource(R.drawable.ic_home_selected);
                tvHome.setTextColor(getResources().getColor(R.color.home_selected));
                break;
            case 1:
                //PowerManager powerManager = new PowerManager();
                //power
                if (user_data.power.equals("off")){
                    ivPower.setImageResource(R.drawable.ic_power_on);
                    user_data.power="on";
                    poweron();
                }else{
                    ivPower.setImageResource(R.drawable.ic_power_off);
                    user_data.power="off";
                    poweroff();
                }


                break;
            case 2:
                //me
                hideFragment();
                if (meFragment==null){
                    meFragment = new MeFragment();
                    ft.add(R.id.content,meFragment);
                }
                ft.show(meFragment);
                ivMe.setImageResource(R.drawable.ic_person_selected);
                tvMe.setTextColor(getResources().getColor(R.color.home_selected));
                break;
        }
        ft.commit();//提交
    }

    private void poweron() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String add_url = Config.all_url + "/iocm/app/cmd/v1.4.0/deviceCommands?appId=" + login_appid;
                Log.i("add_url2",add_url);
                try {
                    Log.i("okli",login_account+" "+token+" "+gatewayId+" ");

                    json = DataManager.Comened_DEVICEID(MainActivity.this, add_url, login_appid, token,gatewayId,"ON","control","power");
                    if (json.equals(tuatus))
                    {
                        //Looper.prepare();//打开子程序修改UI权限
                        toast("下发成功");
                        //Looper.loop();
                    }
                    else
                    {
                        //Looper.prepare();
                        toast("下发失败");
                        //Looper.loop();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void poweroff() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String add_url = Config.all_url + "/iocm/app/cmd/v1.4.0/deviceCommands?appId=" + login_appid;
                Log.i("add_url2",add_url);
                try {
                    Log.i("okli",login_account+" "+token+" "+gatewayId+" ");

                    json = DataManager.Comened_DEVICEID(MainActivity.this, add_url, login_appid, token,gatewayId,"OFF","control","power");
                    if (json.equals(tuatus))
                    {
                        //Looper.prepare();//打开子程序修改UI权限
                        toast("下发成功");
                        //Looper.loop();
                    }
                    else
                    {
                        //Looper.prepare();
                        toast("下发失败");
                        //Looper.loop();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void resetTab() {
        ivHome.setImageResource(R.drawable.ic_home_unselected);
        ivMe.setImageResource(R.drawable.ic_person_unselected);
        tvHome.setTextColor(getResources().getColor(R.color.home_unselected));
        tvMe.setTextColor(getResources().getColor(R.color.home_unselected));

    }

    private void hideFragment() {
        if (homeFragment!=null){
            ft.hide(homeFragment);
        }
        if (meFragment!=null){
            ft.hide(meFragment);
        }
    }


    //------------以下为数据库登陆验证的地方

    public void SearchRequest() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("poi2","123");
                HttpURLConnection connection=null;
                BufferedReader reader=null;
                Log.i("poi5","123");
                try{
                    String url1 = "http://47.102.201.183:8080/test2/SearchServlet?account="+login_account+"&password="+login_password;
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

                    //gatewayId.clear();
                    gatewayId = IntentionJSON(response.toString());
                    user_data.gatewayId=gatewayId;


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
                    if (gatewayId.equals("")){
                        toast("抱歉，当前账户下没有设备，请先进行添加");
                    }else {

                    }
                }


            }
        }).start();
    }
    private void toast(final String toast){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,toast,Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String IntentionJSON(String jsondata){
        try{
            Log.i("jsondata",jsondata);
            JSONObject js=new JSONObject(jsondata);
            String jsonObject=js.getJSONObject("1").toString();
            Log.i("3232:",jsonObject);
            JSONObject object=new JSONObject(jsonObject);
            String ob=object.optString("Intention");
            Log.i("ob",ob);
            return ob;

        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }





}
