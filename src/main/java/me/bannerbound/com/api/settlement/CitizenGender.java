package me.bannerbound.com.api.settlement;

public enum CitizenGender {
    MALE,
    FEMALE;

    public static CitizenGender fromOrdinalOrMale(int ord) {
        CitizenGender[] vals = values();
        if (ord < 0 || ord >= vals.length) return MALE;
        return vals[ord];
    }

    public String key() {
        return name().toLowerCase();
    }

    public String texturePrefix() {
        return this == MALE ? "man" : "woman";
    }
}
