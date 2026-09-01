package me.bannerbound.com.api.research;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.bannerbound.com.Bannerbound;
import me.bannerbound.com.api.settlement.Era;
import me.bannerbound.com.api.settlement.GovernmentType;
import me.bannerbound.com.api.settlement.PolicyType;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;
import java.util.List;

public class ResearchDefinitionReader {
    public static ResearchDefinition readResearchDefinitionFromJson(String id, JsonElement element) {
        try {
            JsonObject obj = element.getAsJsonObject();
            String name = GsonHelper.getAsString(obj, "name");
            String desc = GsonHelper.getAsString(obj, "description", "");
            double cost = GsonHelper.getAsDouble(obj, "cost", 0.0);
            int x = GsonHelper.getAsInt(obj, "x", 0);
            int y = GsonHelper.getAsInt(obj, "y", 0);
            boolean autoUnlock = GsonHelper.getAsBoolean(obj, "auto_unlock", false);
            Era minAge = Era.ANCIENT;
            if (obj.has("min_age")) {
                Era parsed = Era.fromName(GsonHelper.getAsString(obj, "min_age"));
                if (parsed != null) {
                    minAge = parsed;
                } else {
                    Bannerbound.LOGGER.warn("Bad min_age in {}: {}", id,
                            GsonHelper.getAsString(obj, "min_age"));
                }
            }
            List<String> prereqs = readStringArray(obj, "prerequisites");
            List<String> unlocksItems = new ArrayList<>();
            List<String> unlocksFeatures = new ArrayList<>();
            List<String> unlocksFlags = new ArrayList<>();
            if (obj.has("unlocks")) {
                JsonObject unlocks = GsonHelper.getAsJsonObject(obj, "unlocks");
                unlocksItems = readStringArray(unlocks, "items");
                unlocksFeatures = readStringArray(unlocks, "features");
                unlocksFlags = readStringArray(unlocks, "flags");
                for (String policyId : readStringArray(unlocks, "policy")) {
                    unlocksFlags.add("unlock.policy." + policyId);
                }
                for (String paletteId : readStringArray(unlocks, "palette")) {
                    unlocksFlags.add("unlock.palette." + paletteId);
                }
                for (String slotType : readStringArray(unlocks, "policy_slot")) {
                    PolicyType t =
                            PolicyType.byName(slotType);
                    if (t == null) {
                        Bannerbound.LOGGER.warn("Bad unlocks.policy_slot type in {}: {}",
                                id, slotType);
                    } else {
                        unlocksFlags.add("unlock.policy_slot." + t.name());
                    }
                }
            }

            //String ponderScene = GsonHelper.getAsString(obj, "ponder", "");

            GovernmentType govType =
                    parseGovernmentType(obj, id);
            boolean requiresTribe = GsonHelper.getAsBoolean(obj, "requires_tribe", false);
            int heraldryPoints = GsonHelper.getAsInt(obj, "heraldry_points", 0);
            boolean important = GsonHelper.getAsBoolean(obj, "important", false);

            return new ResearchDefinition(id, name, desc, cost, x, y, autoUnlock, minAge,
                    prereqs, unlocksItems, unlocksFeatures, unlocksFlags, govType,
                    requiresTribe, heraldryPoints, important, null);
        } catch (Exception ex) {
            Bannerbound.LOGGER.error("Failed to parse research {}", id, ex);
        }

        return null;
    }

    @org.jetbrains.annotations.Nullable
    private static GovernmentType parseGovernmentType(
            JsonObject obj, String key) {
        if (!obj.has("government_type")) return null;
        String raw = GsonHelper.getAsString(obj, "government_type");
        try {
            return GovernmentType.valueOf(raw.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            Bannerbound.LOGGER.warn("Bad government_type in {}: {}", key, raw);
            return null;
        }
    }

    private static List<String> readStringArray(JsonObject obj, String key) {
        List<String> out = new ArrayList<>();
        if (!obj.has(key)) {
            return out;
        }
        JsonArray arr = GsonHelper.getAsJsonArray(obj, key);
        for (JsonElement el : arr) {
            out.add(el.getAsString());
        }
        return out;
    }
}
