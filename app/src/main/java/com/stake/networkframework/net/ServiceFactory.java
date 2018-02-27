package com.stake.networkframework.net;

public class ServiceFactory {

    public static CommonService getCommonService() {
        return RetroAdapter.getService(CommonService.class);
    }

}
