package com.liupeng.shuttleBusComing.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.liupeng.shuttleBusComing.R;
import com.liupeng.shuttleBusComing.activities.BusLineShowActivity;
import com.liupeng.shuttleBusComing.activities.MainActivity;
import com.liupeng.shuttleBusComing.activities.MapActivity;
import com.liupeng.shuttleBusComing.adapter.LineAdapter;
import com.liupeng.shuttleBusComing.utils.ApiService;
import com.liupeng.shuttleBusComing.utils.BusLineGson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.liupeng.shuttleBusComing.utils.Initialize.WebApiURL;

public class LineFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private MainActivity mainActivity;
//    private LinearLayout searchBtn;
    private LinearLayout mainMessage;
    private RelativeLayout hideBg;
    private LineAdapter lineAdapter;
    private ArrayList<String> data = new ArrayList<>();

    @BindView(R.id.imgBtn_map)
    ImageButton imgBtn_map;
    @BindView(R.id.lines_list)
    ListView listView;
    @BindView(R.id.lineSearchBtn)
    LinearLayout searchBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_line, container, false);
        ButterKnife.bind(this, view);
        initView(view);
        getBusLines();
        return view;
    }

    public void initView(View view) {
        mainMessage = $(view, R.id.main_message);
        hideBg = $(view, R.id.hide_bg);
//        searchBtn = $(view, R.id.searchBtn);
        //listView = $(view, R.id.lines_list);
        mainActivity = (MainActivity) getActivity();
        imgBtn_map.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        listView.setOnItemClickListener(this);
    }

    private <T extends View> T $(View view, int resId) {
        return (T) view.findViewById(resId);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBtn_map:
                startActivity(new Intent(getActivity(), MapActivity.class));
                break;
            case R.id.lineSearchBtn:
                Log.i("search:","");
                break;
            default:
                break;
        }
    }

    public void showLine(int i) {
//        if (null != busLineMessage) {
        Intent intentShowLine = new Intent(getActivity(), BusLineShowActivity.class);

//            intentShowLine.putParcelableArrayListExtra("GoLine",
//                    (ArrayList<? extends Parcelable>) busLineMessage.get(0)
//                            .get("GoBusLineMessage")
//                            .getBusStations());
//
//            intentShowLine.putParcelableArrayListExtra("BackLine",
//                    (ArrayList<? extends Parcelable>) busLineMessage.get(0)
//                            .get("BackBusLineMessage")
//                            .getBusStations());
//
            intentShowLine.putExtra("LineNumber", i);
//
//            intentShowLine.putExtra("FirstBus", busLineMessage.get(0)
//                    .get("GoBusLineMessage").getFirstBusTime());
//
//            intentShowLine.putExtra("LastBus", busLineMessage.get(0)
//                    .get("GoBusLineMessage").getLastBusTime());
        startActivity(intentShowLine);
//        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public static LineFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString("ARGS", content);
        LineFragment fragment = new LineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        showLine(((Integer) lineAdapter.getItem(i)));
    }

    private void showMessage() {
        mainMessage.setVisibility(View.VISIBLE);
        hideBg.setVisibility(View.GONE);
    }

    public void getBusLines() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WebApiURL)
                //增加返回值为String的支持
                .addConverterFactory(ScalarsConverterFactory.create())
                //增加返回值为Gson的支持(以实体类返回)
                .addConverterFactory(GsonConverterFactory.create())
                //增加返回值为Oservable<T>的支持
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        ApiService apiManager = retrofit.create(ApiService.class);//这里采用的是Java的动态代理模式

        Call<BusLineGson> call = apiManager.getBusLines();
        call.enqueue(new Callback<BusLineGson>() {
            @Override
            public void onResponse(Call<BusLineGson> call, Response<BusLineGson> response) {
                //处理请求成功

                if (response.body().getData() != null) {
                    ArrayList<Integer> lineNames = new ArrayList<>();
                    List<Integer> lineNumbers = response.body().getData();
                    for(Integer a: lineNumbers){
                        lineNames.add(a);
                    }

                    lineAdapter = new LineAdapter(mainActivity, lineNames);
                    listView.setAdapter(lineAdapter);

                    showMessage();
                }
            }

            @Override
            public void onFailure(Call<BusLineGson> call, Throwable t) {
                //处理请求失败
                Toast.makeText(mainActivity, "获取班车线路出现错误", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
