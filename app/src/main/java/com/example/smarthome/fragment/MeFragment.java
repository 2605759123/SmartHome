package com.example.smarthome.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.aitangba.pickdatetime.DatePickDialog;
import com.aitangba.pickdatetime.OnSureListener;
import com.aitangba.pickdatetime.bean.DateParams;
import com.example.smarthome.Adapter;
import com.example.smarthome.Data.user_data;
import com.example.smarthome.LoginActivity;
import com.example.smarthome.MainActivity;
import com.example.smarthome.MySettingdata;
import com.example.smarthome.R;
import com.example.smarthome.utils.Config;
import com.example.smarthome.utils.DataManager;

import org.json.JSONObject;
import org.raphets.roundimageview.RoundImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MeFragment extends Fragment {
    @Bind(R.id.touxiang)
    RoundImageView touxiang;
    @Bind(R.id.accountname)
    TextView accountname;
    @Bind(R.id.list_view)
    ListView listView;


    private List<MySettingdata> settingList = new ArrayList<>();
    ViewGroup container;
    View view;
    Adapter adapter;
    private String tuatus="201";
    private String deletetuatus="204";
    private String gatewayId;
    private String qichuangtime4;
    private String shuijiaotime4;
    SharedPreferences sp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        if (view==null){
//            Log.i("frag222","1");
//            view = View.inflate(container.getContext(), R.layout.fragment_me, null);
//        }else if (view!=null){
//            Log.i("frag222","2");
//            ViewGroup parent=(ViewGroup)view.getParent();
//            if (parent!=null){
//                parent.removeView(view);
//            }
//        }
//        Log.i("frag222","3");
        if(view!=null){
            ViewGroup parent =(ViewGroup)view.getParent();
            parent.removeView(view);
        }
        view = View.inflate(container.getContext(), R.layout.fragment_me, null);
        ButterKnife.bind(this, view);
        sp=PreferenceManager.getDefaultSharedPreferences(container.getContext());//init sp
        accountname=(TextView)view.findViewById(R.id.accountname);
        accountname.setText(user_data.account);
        gatewayId=user_data.gatewayId;
        this.container = container;
        initSetting();


        return view;
    }

    private void initSetting() {
        initMySettingdata();//chu shi hua
        adapter = new Adapter(container.getContext(), R.layout.settingdata, settingList);

        listView = (ListView) view.findViewById(R.id.list_view);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final MySettingdata z = settingList.get(position);
//                Toast.makeText(MySettingActivity.this,z.getName(),Toast.LENGTH_SHORT).show();
                if (z.getName().equals("修改起床时间")) {
                    showDatePickDialogqichuang(DateParams.TYPE_HOUR, DateParams.TYPE_MINUTE);
                }
                if (z.getName().equals("修改睡觉时间")) {
                    showDatePickDialogshuijiao(DateParams.TYPE_HOUR, DateParams.TYPE_MINUTE);
                }
                if (z.getName().equals("退出登陆")){
                    sp.edit().putBoolean("AUTO_ISCHECK", false).commit();
                    Intent intent=new Intent(container.getContext(),LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
    }


    private void initMySettingdata() {
        settingList.clear();
        settingList.add(new MySettingdata("修改起床时间", user_data.qichuangpower, R.drawable.icqichuangtime));
        settingList.add(new MySettingdata("修改睡觉时间", user_data.shuijiaopower, R.drawable.icshuijiaotime));
        settingList.add(new MySettingdata("窗帘开关",user_data.chuanglianpower,R.drawable.ic_chuanglianpower));
        settingList.add(new MySettingdata("退出登陆",user_data.chuanglianpower,R.drawable.ic_reply));
    }

    private void showDatePickDialogqichuang(@DateParams.Type int... style) {//设置起床时间的Dialog
        Calendar todayCal = Calendar.getInstance();
        int hour;
        int minute;
        hour= Integer.parseInt(user_data.qichuangtime.substring(0,2));
        minute= Integer.parseInt(user_data.qichuangtime.substring(3,5));
        hour=hour-12;
        todayCal.set(Calendar.HOUR,hour);
        todayCal.set(Calendar.MINUTE,minute);
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        endCal.add(Calendar.YEAR, 6);

        new DatePickDialog.Builder()
                .setTypes(style)
                .setCurrentDate(todayCal.getTime())
                .setStartDate(startCal.getTime())
                .setEndDate(endCal.getTime())
                .setOnSureListener(new OnSureListener() {
                    @Override
                    public void onSure(Date date) {
                        String message = new SimpleDateFormat("HH:mm").format(date);
                        Toast.makeText(container.getContext(), message, Toast.LENGTH_SHORT).show();
                        user_data.qichuangtime = message;
                        try {
                            qichuangtimeRequest();//设置到服务器
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        //数值选择器Numberpicker
                        final NumberPicker numberPicker =new NumberPicker(container.getContext());
                        AlertDialog.Builder builder = new AlertDialog.Builder(container.getContext());
                        numberPicker.setMaxValue(60);
                        numberPicker.setMinValue(0);
                        numberPicker.setWrapSelectorWheel(true); //设置循环滚动
                        numberPicker.setValue(Integer.parseInt(user_data.qichuangyanshi));
                        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                        builder.setTitle("请设置起床延时时间").setIcon(R.drawable.ic_naozhong).setView(numberPicker).setNegativeButton("Cancel", null);
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
//                                sp3.edit().putString("sosphone",inputServer.getText().toString()).commit();
                                user_data.qichuangyanshi= String.valueOf(numberPicker.getValue());
                                try {
                                    qichuangyanshiRequest();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(container.getContext(),"修改成功"+numberPicker.getValue(),Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.show();

//                        initSetting();
                    }
                })
                .show(container.getContext());
    }

    private void showDatePickDialogshuijiao(@DateParams.Type int... style) {//设置睡觉时间
        Calendar todayCal = Calendar.getInstance();
        int hour;
        int minute;
        hour= Integer.parseInt(user_data.shuijiaotime.substring(0,2));
        minute= Integer.parseInt(user_data.shuijiaotime.substring(3,5));
        hour=hour-12;
        todayCal.set(Calendar.HOUR,hour);
        todayCal.set(Calendar.MINUTE,minute);

        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        endCal.add(Calendar.YEAR, 6);

        new DatePickDialog.Builder()
                .setTypes(style)
                .setCurrentDate(todayCal.getTime())
                .setStartDate(startCal.getTime())
                .setEndDate(endCal.getTime())
                .setOnSureListener(new OnSureListener() {
                    @Override
                    public void onSure(Date date) {
                        String message = new SimpleDateFormat("HH:mm").format(date);
                        Toast.makeText(container.getContext(), message, Toast.LENGTH_SHORT).show();
                        user_data.shuijiaotime = message;
                        try {
                            shuijiaotimeRequest();//设置到服务器
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        //数值选择器Numberpicker
                        final NumberPicker numberPicker =new NumberPicker(container.getContext());
                        AlertDialog.Builder builder = new AlertDialog.Builder(container.getContext());
                        numberPicker.setMaxValue(60);
                        numberPicker.setMinValue(0);
                        numberPicker.setWrapSelectorWheel(true); //设置循环滚动
                        numberPicker.setValue(Integer.parseInt(user_data.shuijiaoyanshi));
                        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                        builder.setTitle("请设置睡觉延时时间").setIcon(R.drawable.ic_naozhong).setView(numberPicker).setNegativeButton("Cancel", null);
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
//                                sp3.edit().putString("sosphone",inputServer.getText().toString()).commit();
                                user_data.shuijiaoyanshi= String.valueOf(numberPicker.getValue());
                                try {
                                    shuijiaoyanshiRequest();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(container.getContext(),"修改成功"+numberPicker.getValue(),Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.show();


//                        initSetting();
                    }
                })
                .show(container.getContext());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    public void qichuangtimeRequest() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("poi2","123");
                HttpURLConnection connection=null;
                BufferedReader reader=null;
                String account=user_data.account;
                String password=user_data.pwd;
                Log.i("poi5","123");
                try{
                    Log.i("dataiii",account+" "+password+" "+user_data.qichuangtime);
                    String url1 = "http://47.102.201.183:8080/test2/QichuangtimeServlet?account="+account+"&password="+password+"&qichuangtime="+user_data.qichuangtime;
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
                    result=qichuangtimeJSON(response.toString());
                    Log.i("result",result);
                    if (result.equals("")){
                        //数据库连接不上
                        Looper.prepare();
                        Toast.makeText(container.getContext(),"配置失败，请重试",Toast.LENGTH_SHORT).show();//一般不会这样
                        Looper.loop();
//                        toast("数据库连接失败，请联系我们");//一般不会这样
                    }else if (result.equals("成功")){
                        //成功写入
                        Looper.prepare();
                        Toast.makeText(container.getContext(),"配置成功",Toast.LENGTH_SHORT).show();
                        qichuangtimechange();
                        Looper.loop();
                    }else if(result.equals("失败")){
                        //数据库写入失败(这里最好加上把加上的设备删除)
                        Looper.prepare();
                        Toast.makeText(container.getContext(),"未知原因：配置失败，请重试",Toast.LENGTH_SHORT).show();//一般不会这样
                        Looper.loop();
//                        toast("未知原因：数据库写入失败，请联系我们");
                    }
                }catch (Exception e){
                    Log.i("debugg",Log.getStackTraceString(e));
                    Looper.prepare();
                    Toast.makeText(container.getContext(),"配置失败，请重试",Toast.LENGTH_SHORT).show();//一般不会这样
                    Looper.loop();
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
        }).start();
    }


    private void qichuangtimechange(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String add_url = Config.all_url + "/iocm/app/cmd/v1.4.0/deviceCommands?appId=" + user_data.appId;
                Log.i("jhurl","123");
                Log.i("jhurl",add_url);
                qichuangtime4=user_data.qichuangtime.substring(0,2)+user_data.qichuangtime.substring(3,5);
                try {
                    Log.i("huisqh",gatewayId);
                    String json = DataManager.Comened_DEVICEID(container.getContext(), add_url, user_data.appId, user_data.token,user_data.gatewayId,qichuangtime4,"qichuangTime","time");
                    if (json.equals(tuatus))
                    {
                        Looper.prepare();//打开子程序修改UI权限
                        Toast.makeText(container.getContext(),"下发成功",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                    else
                    {
                        Looper.prepare();
                        Toast.makeText(container.getContext(),"下发失败",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }





    public void shuijiaotimeRequest() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("poi2","123");
                HttpURLConnection connection=null;
                BufferedReader reader=null;
                String account=user_data.account;
                String password=user_data.pwd;
                Log.i("poi5","123");
                try{
                    Log.i("dataiii",account+" "+password+" "+user_data.shuijiaotime);
                    String url1 = "http://47.102.201.183:8080/test2/ShuijiaotimeServlet?account="+account+"&password="+password+"&shuijiaotime="+user_data.shuijiaotime;
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
                    result=qichuangtimeJSON(response.toString());
                    Log.i("result",result);

                    if (result.equals("")){
                        //数据库连接不上
                        Looper.prepare();
                        Toast.makeText(container.getContext(),"配置失败，请重试",Toast.LENGTH_SHORT).show();//一般不会这样
                        Looper.loop();
//                        toast("数据库连接失败，请联系我们");//一般不会这样
                    }else if (result.equals("成功")){
                        //成功写入
                        Looper.prepare();
                        Toast.makeText(container.getContext(),"配置成功",Toast.LENGTH_SHORT).show();
                        shuijiaotimechange();
                        Looper.loop();
                    }else if(result.equals("失败")){
                        //数据库写入失败(这里最好加上把加上的设备删除)
                        Looper.prepare();
                        Toast.makeText(container.getContext(),"未知原因：配置失败，请重试",Toast.LENGTH_SHORT).show();//一般不会这样
                        Looper.loop();
//                        toast("未知原因：数据库写入失败，请联系我们");
                    }
                }catch (Exception e){
                    Log.i("debugg",Log.getStackTraceString(e));
                    Looper.prepare();
                    Toast.makeText(container.getContext(),"配置失败，请重试",Toast.LENGTH_SHORT).show();//一般不会这样
                    Looper.loop();
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
        }).start();
    }

    private void shuijiaotimechange(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                String add_url = Config.all_url + "/iocm/app/cmd/v1.4.0/deviceCommands?appId=" + user_data.appId;
                Log.i("jhurl","123");
                Log.i("jhurl",add_url);
                shuijiaotime4=user_data.shuijiaotime.substring(0,2)+user_data.shuijiaotime.substring(3,5);
                Log.i("shuijiaotime4",shuijiaotime4);
                try {
                    Log.i("huisqh",gatewayId);
                    String json = DataManager.Comened_DEVICEID(container.getContext(), add_url, user_data.appId, user_data.token,user_data.gatewayId,shuijiaotime4,"shuijiaoTime","time");
                    if (json.equals(tuatus))
                    {
                        Looper.prepare();//打开子程序修改UI权限
                       Toast.makeText(container.getContext(),"下发成功",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                    else
                    {
                        Looper.prepare();
                        Toast.makeText(container.getContext(),"下发失败",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }






    public void qichuangyanshiRequest() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("poi2","123");
                HttpURLConnection connection=null;
                BufferedReader reader=null;
                String account=user_data.account;
                String password=user_data.pwd;
                Log.i("poi5","123");
                try{
                    Log.i("dataiii",account+" "+password+" "+user_data.qichuangyanshi);
                    String url1 = "http://47.102.201.183:8080/test2/QichuangyanshiTimeServlet?account="+account+"&password="+password+"&qichuangyanshi="+user_data.qichuangyanshi;
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
                    result=qichuangtimeJSON(response.toString());
                    Log.i("result",result);

                    if (result.equals("")){
                        //数据库连接不上
                        Looper.prepare();
                        Toast.makeText(container.getContext(),"配置失败，请重试",Toast.LENGTH_SHORT).show();//一般不会这样
                        Looper.loop();
//                        toast("数据库连接失败，请联系我们");//一般不会这样
                    }else if (result.equals("成功")){
                        //成功写入
                        Looper.prepare();
                        Toast.makeText(container.getContext(),"配置成功",Toast.LENGTH_SHORT).show();
                        qichuangyanshichange();
                        Looper.loop();
                    }else if(result.equals("失败")){
                        //数据库写入失败(这里最好加上把加上的设备删除)
                        Looper.prepare();
                        Toast.makeText(container.getContext(),"未知原因：配置失败，请重试",Toast.LENGTH_SHORT).show();//一般不会这样
                        Looper.loop();
//                        toast("未知原因：数据库写入失败，请联系我们");
                    }
                }catch (Exception e){
                    Log.i("debugg",Log.getStackTraceString(e));
                    Looper.prepare();
                    Toast.makeText(container.getContext(),"配置失败，请重试",Toast.LENGTH_SHORT).show();//一般不会这样
                    Looper.loop();
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
        }).start();
    }

    private void qichuangyanshichange(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                String add_url = Config.all_url + "/iocm/app/cmd/v1.4.0/deviceCommands?appId=" + user_data.appId;
                Log.i("jhurl",add_url);
                try {
                    Log.i("huisqh",gatewayId);
                    String json = DataManager.Comened_DEVICEID(container.getContext(), add_url, user_data.appId, user_data.token,user_data.gatewayId,user_data.qichuangyanshi,"qichuangYanshi","time");
                    if (json.equals(tuatus))
                    {
                        Looper.prepare();//打开子程序修改UI权限
                        Toast.makeText(container.getContext(),"下发成功",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                    else
                    {
                        Looper.prepare();
                        Toast.makeText(container.getContext(),"下发失败",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }







    public void shuijiaoyanshiRequest() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("poi2","123");
                HttpURLConnection connection=null;
                BufferedReader reader=null;
                String account=user_data.account;
                String password=user_data.pwd;
                Log.i("poi5","123");
                try{
                    Log.i("dataiii",account+" "+password+" "+user_data.shuijiaoyanshi);
                    String url1 = "http://47.102.201.183:8080/test2/ShuijiaoyanshiTimeServlet?account="+account+"&password="+password+"&shuijiaoyanshi="+user_data.shuijiaoyanshi;
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
                    result=qichuangtimeJSON(response.toString());
                    Log.i("result",result);

                    if (result.equals("")){
                        //数据库连接不上
                        Looper.prepare();
                        Toast.makeText(container.getContext(),"配置失败，请重试",Toast.LENGTH_SHORT).show();//一般不会这样
                        Looper.loop();
//                        toast("数据库连接失败，请联系我们");//一般不会这样
                    }else if (result.equals("成功")){
                        //成功写入
                        Looper.prepare();
                        Toast.makeText(container.getContext(),"配置成功",Toast.LENGTH_SHORT).show();
                        shuijiaoyanshichange();
                        Looper.loop();
                    }else if(result.equals("失败")){
                        //数据库写入失败(这里最好加上把加上的设备删除)
                        Looper.prepare();
                        Toast.makeText(container.getContext(),"未知原因：配置失败，请重试",Toast.LENGTH_SHORT).show();//一般不会这样
                        Looper.loop();
//                        toast("未知原因：数据库写入失败，请联系我们");
                    }
                }catch (Exception e){
                    Log.i("debugg",Log.getStackTraceString(e));
                    Looper.prepare();
                    Toast.makeText(container.getContext(),"配置失败，请重试",Toast.LENGTH_SHORT).show();//一般不会这样
                    Looper.loop();
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
        }).start();
    }

    private void shuijiaoyanshichange(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                String add_url = Config.all_url + "/iocm/app/cmd/v1.4.0/deviceCommands?appId=" + user_data.appId;
                Log.i("jhurl",add_url);
                try {
                    Log.i("huisqh",gatewayId);
                    String json = DataManager.Comened_DEVICEID(container.getContext(), add_url, user_data.appId, user_data.token,user_data.gatewayId,user_data.shuijiaoyanshi,"shuijiaoYanshi","time");
                    if (json.equals(tuatus))
                    {
                        Looper.prepare();//打开子程序修改UI权限
                        Toast.makeText(container.getContext(),"下发成功",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                    else
                    {
                        Looper.prepare();
                        Toast.makeText(container.getContext(),"下发失败",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }




    private String qichuangtimeJSON(String jsondata){//都可以用
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






}
