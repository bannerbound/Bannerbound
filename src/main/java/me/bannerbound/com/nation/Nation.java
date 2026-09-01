package me.bannerbound.com.nation;

import me.bannerbound.com.api.research.ResearchDefinition;
import me.bannerbound.com.api.settlement.Era;
import me.bannerbound.com.api.settlement.GovernmentType;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Nation {
    public static class PoliticalStatus {
        public GovernmentType governmentType;
    }

    public UUID id;
    public Map<UUID, Settlement> settlements;

    public Map<String, ResearchDefinition> unlockedResearches;
    public Map<String, ResearchDefinition> unlockedFaithResearches;
    public Map<String, ResearchDefinition> unlockedCultureResearches;
    public Map<String, ResearchDefinition> unlockedWarResearches;

    public Set<String> unlockedItems;
    public Set<String> unlockedFeatures;
    public Set<String> unlockedFlags;

    public List<UUID> members; // players affiliated with the nation
    public PoliticalStatus politicalStatus;

    public Era era;
}
