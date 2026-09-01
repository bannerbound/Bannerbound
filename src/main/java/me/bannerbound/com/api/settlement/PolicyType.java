package me.bannerbound.com.api.settlement;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum PolicyType {
    ECONOMIC(0xFFE0A040),
    CULTURAL(0xFFD060C0),
    SCIENTIFIC(0xFF40A0E0),
    MILITARISTIC(0xFFE05050),
    DIPLOMATIC(0xFF50C080),
    FAITH(0xFFE0D060);

    private final int color;

    PolicyType(int color) {
        this.color = color;
    }

    public int color() {
        return color;
    }

    public String langKey() {
        return "bannerbound.policy.type." + name().toLowerCase(Locale.ROOT);
    }

    @Nullable
    public static PolicyType byName(String raw) {
        if (raw == null) return null;
        try {
            return valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}