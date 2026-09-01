package me.bannerbound.com.api.research;

import me.bannerbound.com.api.settlement.GovernmentType;
import me.bannerbound.com.api.settlement.ResearchType;
import me.bannerbound.com.api.research.treetypes.CultureResearchTreeResource;
import me.bannerbound.com.api.research.treetypes.FaithResearchTreeResource;
import me.bannerbound.com.api.research.treetypes.NormalResearchTreeResource;
import me.bannerbound.com.api.research.treetypes.WarResearchTreeResource;
import me.bannerbound.com.api.nation.Nation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class ResearchManager {
    public static Map<String, ResearchDefinition> getTree(ResearchType type) {
        return switch (type) {
            case NORMAL -> NormalResearchTreeResource.getAll();
            case FAITH -> FaithResearchTreeResource.getAll();
            case CULTURE -> CultureResearchTreeResource.getAll();
            case WAR -> WarResearchTreeResource.getAll();
        };
    }

    public static Map<String, ResearchDefinition> getUnlockedResearchesForNation(Nation nation, ResearchType type) {
        return switch (type) {
            case NORMAL -> nation.unlockedResearches;
            case FAITH -> nation.unlockedFaithResearches;
            case CULTURE -> nation.unlockedCultureResearches;
            case WAR -> nation.unlockedWarResearches;
        };
    }

    public static ResearchDefinition getResearchDefinition(ResearchType type, String id) {
        return getTree(type).get(id);
    }

    public static boolean isItemUnlocked(Nation nation, String id) {
        return nation.unlockedItems.contains(id);
    }

    public static boolean isItemUnlocked(Nation nation, Item item) {
        return isItemUnlocked(nation, BuiltInRegistries.ITEM.getKey(item).toString());
    }

    public static boolean isItemUnlocked(Nation nation, ItemStack itemStack) {
        return isItemUnlocked(nation, BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString());
    }

    public static boolean isFeatureUnlocked(Nation nation, String feature) {
        return nation.unlockedFeatures.contains(feature);
    }

    public static boolean isFlagUnlocked(Nation nation, String flag) {
        return nation.unlockedFlags.contains(flag);
    }

    private static void __internal_addUnlocks(Nation nation, ResearchDefinition researchDefinition) {
        if (researchDefinition.unlocksItems() != null) nation.unlockedItems.addAll(researchDefinition.unlocksItems());
        if (researchDefinition.unlocksFeatures() != null) nation.unlockedFeatures.addAll(researchDefinition.unlocksFeatures());
        if (researchDefinition.unlocksFlags() != null) nation.unlockedFlags.addAll(researchDefinition.unlocksFlags());
    }

    public static void unlockResearch(Nation nation, ResearchDefinition researchDefinition, Map<String, ResearchDefinition> unlockedResearches) {
        if (unlockedResearches.containsKey(researchDefinition.id())) return;

        __internal_addUnlocks(nation, researchDefinition);

        unlockedResearches.put(researchDefinition.id(), researchDefinition);
    }

    public static void unlockResearch(Nation nation, ResearchDefinition researchDefinition, ResearchType type) {
        unlockResearch(nation, researchDefinition, getUnlockedResearchesForNation(nation, type));
    }

    private static void removeResearch(Nation nation, Map<String, ResearchDefinition> unlockedTargetResearches, ResearchDefinition researchDefinition) {
        unlockedTargetResearches.remove(researchDefinition.id());

        recomputeUnlocks(nation);
    }

    public static void removeResearchCascading(Nation nation, ResearchDefinition researchDefinition, ResearchType type) {
        Map<String, ResearchDefinition> unlockedResearches = getUnlockedResearchesForNation(nation, type);
        if (!unlockedResearches.containsKey(researchDefinition.id())) return;

        List<ResearchDefinition> dependents = new ArrayList<>();
        for (ResearchDefinition unlocked : unlockedResearches.values()) {
            if (unlocked.prerequisites() != null && unlocked.prerequisites().contains(researchDefinition.id())) {
                dependents.add(unlocked);
            }
        }

        for (ResearchDefinition child : dependents) {
            removeResearchCascading(nation, child, type);
        }

        unlockedResearches.remove(researchDefinition.id());

        recomputeUnlocks(nation);
    }

    public static void removeResearchCascading(Nation nation, String researchId, ResearchType type) {
        Map<String, ResearchDefinition> unlockedTargetResearches = getUnlockedResearchesForNation(nation, type);

        if (!unlockedTargetResearches.containsKey(researchId)) return;
        ResearchDefinition researchDefinition = unlockedTargetResearches.get(researchId);

        removeResearchCascading(nation, researchDefinition, type);
    }

    public static void removeResearch(Nation nation, String researchId, ResearchType type) {
        Map<String, ResearchDefinition> unlockedTargetResearches = getUnlockedResearchesForNation(nation, type);

        if (!unlockedTargetResearches.containsKey(researchId)) return;
        ResearchDefinition researchDefinition = unlockedTargetResearches.get(researchId);

        removeResearch(nation, unlockedTargetResearches, researchDefinition);
    }

    public static void removeResearch(Nation nation, ResearchDefinition researchDefinition, ResearchType type) {
        removeResearch(nation, getUnlockedResearchesForNation(nation, type), researchDefinition);
    }

    public static boolean canUnlock(Nation nation, ResearchDefinition researchDefinition, ResearchType type) {
        if (getUnlockedResearchesForNation(nation, type).containsKey(researchDefinition.id())) return false;
        if (isMissingAPrerequisiteResearch(nation, researchDefinition, type)) return false;
        return hasPrerequisites(nation, researchDefinition);
    }

    public static boolean hasPrerequisites(Nation nation, ResearchDefinition researchDefinition) {
        if (researchDefinition.requiresTribe() && nation.politicalStatus.governmentType == GovernmentType.NONE)
            return false;

        return nation.era.isAtLeast(researchDefinition.minAge());
    }

    public static List<String> getMissingPrerequisiteResearches(Nation nation, ResearchDefinition researchDefinition, ResearchType type) {
        Map<String, ResearchDefinition> unlockedResearches = getUnlockedResearchesForNation(nation, type);
        List<String> missing = new ArrayList<>();

        for (String prerequisite : researchDefinition.prerequisites()) {
            if (!unlockedResearches.containsKey(prerequisite)) missing.add(prerequisite);
        }

        return missing;
    }

    public static boolean isMissingAPrerequisiteResearch(Nation nation, ResearchDefinition researchDefinition, ResearchType type) {
        Map<String, ResearchDefinition> unlockedResearches = getUnlockedResearchesForNation(nation, type);

        for (String prerequisite : researchDefinition.prerequisites()) {
            if (!unlockedResearches.containsKey(prerequisite)) return true;
        }

        return false;
    }

    private static void recomputeUnlocks(Nation nation, Map<String, ResearchDefinition> unlockedResearches) {
        for (ResearchDefinition definition : unlockedResearches.values()) {
            __internal_addUnlocks(nation, definition);
        }
    }

    public static void recomputeUnlocks(Nation nation) {
        nation.unlockedItems.clear();
        nation.unlockedFeatures.clear();
        nation.unlockedFlags.clear();

        recomputeUnlocks(nation, nation.unlockedResearches);
        recomputeUnlocks(nation, nation.unlockedFaithResearches);
        recomputeUnlocks(nation, nation.unlockedCultureResearches);
        recomputeUnlocks(nation, nation.unlockedWarResearches);
    }
}
