package com.liupeng.shuttleBusComing.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.liupeng.shuttleBusComing.R;
import com.liupeng.shuttleBusComing.activities.PublicActivity;
import com.liupeng.shuttleBusComing.activities.SettingActivity;
import com.liupeng.shuttleBusComing.views.WaveView;

public class MeFragment extends Fragment implements View.OnClickListener {

    private View layout;
    private ImageView imageView;
    private WaveView waveView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_me, container, false);

        imageView = (ImageView) layout.findViewById(R.id.image);
        waveView = (WaveView) layout.findViewById(R.id.wave_view);

        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-2,-2);
        lp.gravity = Gravity.BOTTOM|Gravity.CENTER;
        waveView.setOnWaveAnimationListener(new WaveView.OnWaveAnimationListener() {
            @Override
            public void OnWaveAnimation(float y) {
                lp.setMargins(0,0,0,(int)y+2);
                imageView.setLayoutParams(lp);
            }
        });

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
