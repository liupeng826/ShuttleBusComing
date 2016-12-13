package com.liupeng.shuttleBusComing.utils;

import android.content.Context;
import android.widget.TextView;

import com.liupeng.shuttleBusComing.R;

/**
 * Created by liupeng on 2016/11/30.
 */
public class StyleUtil {


    public static Context context;


    public static void init(Context ctx){
        context = ctx;
    }


    public static void setColor(TextView textView,String status){
        switch (status){
            case "click":
                textView.setTextColor(context.getResources().getColor(R.color.click));
                break;
            case "default":
                textView.setTextColor(context.getResources().getColor(R.color.text_default));
                break;
        }

    }
}
