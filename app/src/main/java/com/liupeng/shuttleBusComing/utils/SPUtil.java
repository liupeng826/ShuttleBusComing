package com.liupeng.shuttleBusComing.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.liupeng.shuttleBusComing.bean.SPMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.liupeng.shuttleBusComing.utils.Initialize.FAVORITELINE_KEY;
import static com.liupeng.shuttleBusComing.utils.Initialize.FILENAME;

/**
 * Created by liupeng on 2016/12/8.
 * E-mail: liupeng826@hotmail.com
 */

public class SPUtil {

	private static final String regularEx = "#";

	public static String[] getSharedPreference(String key, Context context ) {
		String[] str = null;
		SharedPreferences sp = context.getSharedPreferences(FILENAME, MODE_PRIVATE);
		String values;
		values = sp.getString(key, "");
		if (!values.equals("")) {
			str = values.split(regularEx);
		}

		return str;
	}

	public static void setSharedPreference(String key, String[] values, Context context) {
		String str = "";
		SharedPreferences sp = context.getSharedPreferences(FILENAME, MODE_PRIVATE);
		if (values != null && values.length > 0) {
			for (String value : values) {
				str += value;
				str += regularEx;
			}
			SharedPreferences.Editor et = sp.edit();
			et.putString(key, str);
			et.apply();
		}
	}

	public static List<SPMap> getAllFavoriteKey(Context context){
		List<SPMap> sPMapList = new ArrayList<SPMap>();

		SharedPreferences sp = context.getSharedPreferences(FILENAME, MODE_PRIVATE);
		Map<String, ?> allContent = sp.getAll();
		//注意遍历map的方法
		for(Map.Entry<String, ?>  entry : allContent.entrySet()){
			if(entry.getKey().contains(FAVORITELINE_KEY)){
				SPMap sPMap = new SPMap();
				sPMap.setKey(entry.getKey());
				sPMap.setStationName(new ArrayList<>(Arrays.asList(entry.getValue().toString().split(regularEx))));
				sPMapList.add(sPMap);
			}
		}

		Collections.sort(sPMapList);

		return sPMapList;
	}
}
