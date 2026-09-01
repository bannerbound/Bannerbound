package me.bannerbound.com.nation;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import me.bannerbound.com.api.houses.House;

import java.util.Map;
import java.util.UUID;

public class Settlement {
    public UUID id;
    public Nation nation;

    public Long2ObjectMap<Boolean> claimedChunks;
    public Map<String, House> houses;

    // status
    public double foodGeneration;
    public double researchGeneration;
    public double cultureGeneration;
}
