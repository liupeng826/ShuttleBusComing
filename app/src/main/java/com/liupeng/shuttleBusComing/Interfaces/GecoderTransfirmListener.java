package com.liupeng.shuttleBusComing.Interfaces;

import com.liupeng.shuttleBusComing.bean.ErrorStatus;
import com.amap.api.services.core.LatLonPoint;

/**
 * Created by liupeng on 2016/5/8.
 */
public interface GecoderTransfirmListener {
    public void onGetLat(LatLonPoint latLonPoint, ErrorStatus status);
    public void onGetAddr(String address, ErrorStatus status);
}
