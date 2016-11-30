package com.liupeng.shuttleBusComing.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liupeng.shuttleBusComing.R;
import com.liupeng.shuttleBusComing.activity.PublicActivity;
import com.liupeng.shuttleBusComing.activity.SettingActivity;

public class MeFragment extends Fragment implements View.OnClickListener {

    private View layout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_me, container, false);
        setOnListener();
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public static MeFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString("ARGS", content);
        MeFragment fragment = new MeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void setOnListener() {
        layout.findViewById(R.id.view_user).setOnClickListener(this);
        layout.findViewById(R.id.txt_line).setOnClickListener(this);
        layout.findViewById(R.id.txt_alarm).setOnClickListener(this);
        layout.findViewById(R.id.txt_setting).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_line:// 线路
                startActivity(new Intent(getActivity(), PublicActivity.class));
                break;
            case R.id.txt_alarm:// 提醒
                startActivity(new Intent(getActivity(), PublicActivity.class));
                break;

            case R.id.txt_setting:// 设置
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            default:
                break;
        }
    }
}
