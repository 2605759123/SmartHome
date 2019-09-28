package com.example.smarthome.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.smarthome.Adapter_main;
import com.example.smarthome.Data.user_data;
import com.example.smarthome.MyNewsdata;
import com.example.smarthome.R;
import com.example.smarthome.WebActivity;
import com.example.smarthome.get.getDateSx;
import com.example.smarthome.utils.Config;
import com.example.smarthome.utils.DataManager;
import com.example.smarthome.views.GifView;
import com.example.smarthome.views.SwitcherView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment {

    @Bind(R.id.main_background)
    ImageView mainBackground;
    @Bind(R.id.work_gif)
    GifView workGif;
    @Bind(R.id.timetext)
    TextView timetext;
    @Bind(R.id.xinqing)
    TextView xinqing;
    @Bind(R.id.switcherView)
    SwitcherView switcherView;
    @Bind(R.id.list_view2)
    ListView listView2;
    private GifView gifView;
    private Context mContext;
    ViewGroup container;
    View view;
    Adapter_main adapter;
    private List<MyNewsdata> myNewsdata = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = View.inflate(container.getContext(), R.layout.fragment_home, null);
        ButterKnife.bind(this, view);
        mContext = container.getContext();
        this.container = container;
        gifView = (GifView) view.findViewById(R.id.work_gif);
        gifView.startAnimation();
        makeSwitchview();
        initSetting();
        Log.i("datesx", getDateSx.get());
        if (getDateSx.get().equals("zaoshang")) {
            mainBackground.setImageResource(R.drawable.xml_zaoshang);
            timetext.setText("早上好");
            if (user_data.xinqing.equals("t")) {//疲惫
                xinqing.setText("每天都要充满活力哦");
            } else if (user_data.xinqing.equals("b")) {//忧虑
                xinqing.setText("笑一笑，更健康");
            } else {//开心和自然
                xinqing.setText("您的心情看起来不错哦");
            }
            workGif.setGifid(R.raw.zaoshang);
            workGif.initializeView();//刷新view视图
        }
        if (getDateSx.get().equals("shangwu")) {
            mainBackground.setImageResource(R.drawable.xml_zaoshang);
            timetext.setText("上午好");
            makexinqing(user_data.xinqing);//判断心情
            workGif.setGifid(R.raw.zaoshang);
            workGif.initializeView();//刷新view视图
        }

        if (getDateSx.get().equals("zhongwu")) {
            mainBackground.setImageResource(R.drawable.xml_zhongwu);
            timetext.setText("中午好");
            makexinqing(user_data.xinqing);//判断心情
            workGif.setGifid(R.raw.zhongwu2);
            workGif.initializeView();//刷新view视图
        }

        if (getDateSx.get().equals("xiawu")) {
            mainBackground.setImageResource(R.drawable.xml_xiawu);
            timetext.setText("下午好");
            makexinqing(user_data.xinqing);//判断心情
            workGif.setGifid(R.raw.xiawu2);
            workGif.initializeView();//刷新view视图
        }

        if (getDateSx.get().equals("wanshang")) {
            mainBackground.setImageResource(R.drawable.xml_wanshang);
            timetext.setText("晚上好");
            makexinqing(user_data.xinqing);//判断心情
            workGif.setGifid(R.raw.wanshang);
            workGif.initializeView();//刷新view视图
            Log.i("kkk", "2222");
        }
        Log.i("threadss","2");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("threadss","3");
                while (true) {
                    Log.i("threadss","1");
                    GetConfigfromhuawei();
                    try {
                        Thread.sleep(10000);//10s
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();

        return view;
    }

    private void initSetting() {
        initMySettingdata();//chu shi hua
        adapter = new Adapter_main(container.getContext(), R.layout.main_news_data, myNewsdata);

        listView2 = (ListView) view.findViewById(R.id.list_view2);

        listView2.setAdapter(adapter);
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final MyNewsdata z = myNewsdata.get(position);
//                Toast.makeText(MySettingActivity.this,z.getName(),Toast.LENGTH_SHORT).show();
                if (z.getName() == "心情·感悟") {
                    String url="https://mp.weixin.qq.com/s/K_ddarUO6nG6x_phnwZzHw";
                    Intent intent=new Intent(container.getContext(),WebActivity.class);
                    intent.putExtra("url",url);
                    startActivity(intent);
                }
                if (z.getName() == "别让人生，输给了心情") {
                    String url="https://mp.weixin.qq.com/s/u5lU65Jd2SigzaHMArbqrg";
                    Intent intent=new Intent(container.getContext(),WebActivity.class);
                    intent.putExtra("url",url);
                    startActivity(intent);
                }
                if (z.getName() == "别让人生，输给了心情") {
                    String url="https://mp.weixin.qq.com/s/PwyH4l9q-SaZop1DEk1bnQ";
                    Intent intent=new Intent(container.getContext(),WebActivity.class);
                    intent.putExtra("url",url);
                    startActivity(intent);
                }
                if (z.getName() == "如何消除疲惫感") {
                    String url="https://jingyan.baidu.com/article/d8072ac4b486c9ec95cefde2.html";
                    Intent intent=new Intent(container.getContext(),WebActivity.class);
                    intent.putExtra("url",url);
                    startActivity(intent);
                }
                if (z.getName() == "疲惫的时候，愿你能读读这六句话") {
                    String url="https://mp.weixin.qq.com/s/4R960zf3GGrXk7hU5UwwSw";
                    Intent intent=new Intent(container.getContext(),WebActivity.class);
                    intent.putExtra("url",url);
                    startActivity(intent);
                }
                if (z.getName() == "你为什么总是情绪疲惫?") {
                    String url="https://mp.weixin.qq.com/s/Wr7VopbX9U-PiNAqJLLumw";
                    Intent intent=new Intent(container.getContext(),WebActivity.class);
                    intent.putExtra("url",url);
                    startActivity(intent);
                }

                if (z.getName() == "带着好心情，过好每一天！") {
                    String url="https://mp.weixin.qq.com/s/49chKE6Y0MK5on64dNzm6Q";
                    Intent intent=new Intent(container.getContext(),WebActivity.class);
                    intent.putExtra("url",url);
                    startActivity(intent);
                }

                if (z.getName() == "一笑一淡好心情！") {
                    String url="https://mp.weixin.qq.com/s/R_bMG4gT2Z5DNYKQJu-ALQ";
                    Intent intent=new Intent(container.getContext(),WebActivity.class);
                    intent.putExtra("url",url);
                    startActivity(intent);
                }

                if (z.getName() == "保持好心情，坚持好生活") {
                    String url="https://mp.weixin.qq.com/s/4OqTc0Lmn8JGE8USq0AWdA";
                    Intent intent=new Intent(container.getContext(),WebActivity.class);
                    intent.putExtra("url",url);
                    startActivity(intent);
                }



            }
        });
    }

    private void initMySettingdata() {
        myNewsdata.clear();
        if (user_data.xinqing.equals("b")){
            myNewsdata.add(new MyNewsdata("心情·感悟",R.drawable.b_1,"当遇到不快乐的时候，总会表现的沉默，不语，那时候心情是低落的"));
            myNewsdata.add(new MyNewsdata("别让人生，输给了心情",R.drawable.b_2,"心情不是人生的全部，却能左右人生的全部 心情好，什么都好 心情不好，什么都乱了"));
            myNewsdata.add(new MyNewsdata("心情不好，你们是怎么克服的？",R.drawable.b_3,"身处这浮躁的世界，我们太容易心情不好了。事业，生活，恋爱，亲情，友情..."));
        }else if (user_data.xinqing.equals("t")){
            myNewsdata.add(new MyNewsdata("如何消除疲惫感",R.drawable.t_1,"疲惫感就是精神疲乏，做事情不集中，易瞌睡，那么如何消除疲惫感呢？"));
            myNewsdata.add(new MyNewsdata("疲惫的时候，愿你能读读这六句话",R.drawable.t_2,"如果我们在内心疲惫的时候打开这本书，所有烦恼都会烟消云散"));
            myNewsdata.add(new MyNewsdata("你为什么总是情绪疲惫?",R.drawable.t_3,"不只是我，像这样碌碌无为又十分焦虑的人在我询问朋友的比利中，占一半以上"));
        }else{
            myNewsdata.add(new MyNewsdata("带着好心情，过好每一天！",R.drawable.h_1,"快乐在于我们自己对生活的态度，好心情才是我们人生的财富。"));
            myNewsdata.add(new MyNewsdata("一笑一淡好心情！",R.drawable.h_2,"松意百愁随风去  放容一笑胜千金"));
            myNewsdata.add(new MyNewsdata("保持好心情，坚持好生活",R.drawable.h_3,"保持良好的心态，拥有美好的人生"));
        }
    }

    private void makeSwitchview() {
        ArrayList<String> strs = new ArrayList<>();
        strs.add("温度：" + user_data.wendu + "℃ " + " 湿度：" + user_data.shidu + "%");
        switcherView.setResource(strs);
        switcherView.startRolling();
    }

    private void makexinqing(String s_xinqing) {
        if (s_xinqing.equals("t")) {//疲惫
            xinqing.setText("每天都要充满活力哦");
        } else if (s_xinqing.equals("b")) {//忧虑
            xinqing.setText("笑一笑，更健康");
        } else {//开心和自然
            xinqing.setText("您的心情看起来不错哦");
        }
    }

    private void GetConfigfromhuawei() {
//        returndata=0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("pppppppppppppppppp", user_data.token);
                    String add_url = Config.all_url + "/iocm/app/dm/v1.3.0/devices/" + user_data.Intention + "?appId=" + user_data.user;
                    Log.i("bbbbbbbbb", add_url);
                    String json = DataManager.Txt_REQUSET(mContext, add_url, user_data.user, user_data.token);
                    Log.i("json1111:", json);
//                    mlist = new ArrayList<DataInfo>();

                    JSONObject jo = new JSONObject(json);
                    JSONArray jsonArray = jo.getJSONArray("services");
                    Log.i("jo999", jsonArray.getJSONObject(0).toString());
                    JSONObject jo2 = new JSONObject(jsonArray.getJSONObject(0).toString());
                    String data_s = jo2.getString("data");
                    JSONObject data_ob = new JSONObject(data_s);
                    Log.i("xinqing", data_ob.getString("xinqing"));
                    user_data.xinqing = data_ob.getString("xinqing");
                    user_data.wendu = data_ob.getString("wendu");
                    user_data.shidu = data_ob.getString("shidu");
                    makexinqing(user_data.xinqing);
                    makeSwitchview();

                } catch (Exception e) {
                    Log.i("debug", Log.getStackTraceString(e));
                    e.printStackTrace();
                }


            }
        }).start();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
