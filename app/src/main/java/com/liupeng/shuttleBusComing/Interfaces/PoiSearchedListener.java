package com.liupeng.shuttleBusComing.Interfaces;

import com.liupeng.shuttleBusComing.bean.ErrorStatus;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;

import java.util.List;

/**
 * Created by liupeng on 2016/5/25.
 */
public interface PoiSearchedListener {
    public void onGetPoiMessage(List<PoiItem> poiItems, String poiTitle, LatLonPoint addressPoint, ErrorStatus status);
}
