package me.bannerbound.com.civpm;

import me.bannerbound.com.civpm.managers.CPMClientRegionsManager;

public class CivPMClient {
    private static final CivPMClient instance = new CivPMClient();
    private static final CPMClientRegionsManager regions_manager = new CPMClientRegionsManager();

    public static CivPMClient getInstance() {return instance;}
    public static CPMClientRegionsManager getRegionsManager() {return regions_manager;}
}
