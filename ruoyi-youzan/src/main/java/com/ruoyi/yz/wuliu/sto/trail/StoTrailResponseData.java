/**
 * Copyright 2019 bejson.com
 */
package com.ruoyi.yz.wuliu.sto.trail;

import com.ruoyi.yz.base.BaseCif;
import java.util.List;

/**
 * Auto-generated: 2019-06-18 23:13:12
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class StoTrailResponseData extends BaseCif {

    private String id;
    private List<ScanList> scanList;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setScanList(List<ScanList> scanList) {
        this.scanList = scanList;
    }

    public List<ScanList> getScanList() {
        return scanList;
    }

}
