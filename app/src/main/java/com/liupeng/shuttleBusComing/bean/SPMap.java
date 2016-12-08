package com.liupeng.shuttleBusComing.bean;

import java.util.List;

import static com.liupeng.shuttleBusComing.utils.Initialize.FAVORITELINE_KEY;

/**
 * Created by liupeng on 2016/12/8.
 * E-mail: liupeng826@hotmail.com
 */

public class SPMap implements Comparable{

	private String key;
	private String line;
	private List stationName;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
		this.line = key.replace(FAVORITELINE_KEY, "");
	}

	public String getLine() {
		return line;
	}

	public List getStationName() {
		return stationName;
	}

	public void setStationName(List stationName) {
		this.stationName = stationName;
	}

	@Override
	public int compareTo(Object o)
	{
		SPMap data = (SPMap)o;

		String otherKey = data.getKey();

		return this.getKey().compareTo(otherKey);
	}
}
