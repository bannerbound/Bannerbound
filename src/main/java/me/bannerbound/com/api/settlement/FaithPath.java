package me.bannerbound.com.api.settlement;

public enum FaithPath {
    ASTROLOGY,
    TOTEMIC;

    public static FaithPath fromOrdinal(int ordinal) {
        FaithPath[] vals = values();
        return ordinal >= 0 && ordinal < vals.length ? vals[ordinal] : ASTROLOGY;
    }
}
