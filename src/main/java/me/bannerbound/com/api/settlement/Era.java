package me.bannerbound.com.api.settlement;

import net.minecraft.network.chat.Component;

public enum Era {
    ANCIENT,
    CLASSICAL,
    MEDIEVAL,
    RENAISSANCE,
    INDUSTRIAL,
    DIESEL,
    ATOMIC,
    MODERN,
    FUTURE;

    public Component displayName() {
        return Component.translatable("bannerbound.era." + name().toLowerCase());
    }

    public String key() {
        return name().toLowerCase();
    }

    public int immigrationFloor() {
        return switch (this) {
            case ANCIENT     -> 7;
            case CLASSICAL   -> 10;
            case MEDIEVAL    -> 14;
            case RENAISSANCE -> 23;
            case INDUSTRIAL  -> 34;
            case DIESEL      -> 57;
            case ATOMIC      -> 80;
            case MODERN      -> 100;
            case FUTURE      -> 150;
        };
    }

    public int activePolicySlots() {
        return ordinal() + 1;
    }

    public int activePaletteSlots() {
        return ordinal() + 1;
    }

    public int registrationDocumentSlots() {
        return ordinal() + 1;
    }

    public Era next() {
        Era[] vals = values();
        int idx = ordinal();
        return idx + 1 < vals.length ? vals[idx + 1] : this;
    }

    public static Era fromOrdinalOrDefault(int ord) {
        Era[] vals = values();
        if (ord < 0 || ord >= vals.length) {
            return ANCIENT;
        }
        return vals[ord];
    }

    public static Era fromName(String name) {
        if (name == null) {
            return null;
        }
        for (Era e : values()) {
            if (e.name().equalsIgnoreCase(name)) {
                return e;
            }
        }
        return null;
    }
}
