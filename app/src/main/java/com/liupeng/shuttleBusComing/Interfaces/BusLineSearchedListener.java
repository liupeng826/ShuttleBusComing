package com.liupeng.shuttleBusComing.Interfaces;

import com.liupeng.shuttleBusComing.bean.ErrorStatus;
import com.amap.api.services.busline.BusLineItem;

import java.util.List;

/**
 * Created by liupeng on 2016/5/26.
 */
public interface BusLineSearchedListener {
    public void getBusLineMessage(List<BusLineItem> busLineItems, ErrorStatus errorStatus);
    public void getManyBusLineMessage(List<List<BusLineItem>> busLineItemsList);
}
