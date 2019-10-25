package com.example.smarthome;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthome.Data.user_data;
import com.example.smarthome.utils.Config;
import com.example.smarthome.utils.DataManager;
import com.suke.widget.SwitchButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Adapter extends ArrayAdapter<MySettingdata> {
    private int resourceId;
    private Context context;
    private String nowzhuangtai;

    public Adapter(@NonNull Context context, int resource, @NonNull List<MySettingdata> objects) {
        super(context, resource, objects);
        resourceId=resource;
        this.context=context;
    }


    String power;
    String Servlet;
    String method;
    String powername;
    private String tuatus="201";//状态
    private String deletetuatus="204";

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final MySettingdata mySettingdata=getItem(position);
        View view=LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        ImageView imageView=(ImageView)view.findViewById(R.id.whatimage);
        TextView textView=(TextView)view.findViewById(R.id.whatname);
        SwitchButton switchButton=(SwitchButton)view.findViewById(R.id.switchbutton);
        imageView.setImageResource(mySettingdata.getImageId());
        textView.setText(mySettingdata.getName());
        if (mySettingdata.getName().equals("退出登陆")){
            switchButton.setVisibility(view.INVISIBLE);
        }


        if (mySettingdata.getSwitch1().equals("ON")){
            switchButton.setChecked(true);
        }else {
            switchButton.setChecked(false);
        }
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                //TODO do your job
                if (isChecked==true){
                    nowzhuangtai="ON";
                }else{
                    nowzhuangtai="OFF";
                }
                try {
                    powerRequest(mySettingdata);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("switchbutton", mySettingdata.getName()+String.valueOf(isChecked));
            }
        });
        return  view;
    }

    public void powerRequest(final MySettingdata mySettingdata) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("poi2","123");
                HttpURLConnection connection=null;
                BufferedReader reader=null;
                String account=user_data.account;
                String password=user_data.pwd;

                if (mySettingdata.getName().equals("修改起床时间")){
                    power=user_data.qichuangpower;
                    Servlet="QichuangpowerServlet";
                    method="qichuangVoice";
                    powername="qichuangpower";
                }else if (mySettingdata.getName().equals("修改睡觉时间")){
                    power=user_data.shuijiaopower;
                    Servlet="ShuijiaopowerServlet";
                    method="shuijiaoVoice";
                    powername="shuijiaopower";
                }else if (mySettingdata.getName().equals("窗帘开关")){
                    power=user_data.chuanglianpower;
                    Servlet="ChuanglianPowerServlet";
                    method="chuanglianPower";
                    powername="chuanglianpower";
                }
                try{
                    String url1 = "http://47.102.201.183:8080/test2/"+Servlet+"?account="+account+"&password="+password+"&"+powername+"="+nowzhuangtai;
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
                        Toast.makeText(context,"配置失败，请重试",Toast.LENGTH_SHORT).show();//一般不会这样
                        Looper.loop();
//                        toast("数据库连接失败，请联系我们");//一般不会这样
                    }else if (result.equals("成功")){
                        //成功写入
                        Looper.prepare();
                        Toast.makeText(context,"配置成功",Toast.LENGTH_SHORT).show();
                        qichuangtimechange();
                        Looper.loop();
                    }else if(result.equals("失败")){
                        //数据库写入失败(这里最好加上把加上的设备删除)
                        Looper.prepare();
                        Toast.makeText(context,"未知原因：配置失败，请重试",Toast.LENGTH_SHORT).show();//一般不会这样
                        Looper.loop();
//                        toast("未知原因：数据库写入失败，请联系我们");
                    }
                }catch (Exception e){
                    Log.i("debugg",Log.getStackTraceString(e));
                    Looper.prepare();
                    Toast.makeText(context,"配置失败，请重试",Toast.LENGTH_SHORT).show();//一般不会这样
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
//                qichuangtime4=user_data.qichuangtime.substring(0,2)+user_data.qichuangtime.substring(3,5);
                try {
                    Log.i("mmethod",method);
                    String json = DataManager.Comened_DEVICEID(context, add_url, user_data.appId, user_data.token,user_data.gatewayId,nowzhuangtai,method,"power");
                    if (json.equals(tuatus))
                    {
                        Looper.prepare();//打开子程序修改UI权限
                        Toast.makeText(context,"下发成功",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                    else
                    {
                        Looper.prepare();
                        Toast.makeText(context,"下发失败",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    private String qichuangtimeJSON(String jsondata){//设置睡觉时间也可以用
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
