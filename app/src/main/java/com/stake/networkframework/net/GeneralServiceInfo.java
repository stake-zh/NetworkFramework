package com.stake.networkframework.net;

public class GeneralServiceInfo extends BaseServiceInfo {
    @Override
    public String getBaseUrl(int serverEnvType) {
        switch (serverEnvType) {
            case RetroAdapter.ServerEnvType.TEST:
            case RetroAdapter.ServerEnvType.FORMAL:
            default:
                return " http://baid.com/";
        }

    }
}
