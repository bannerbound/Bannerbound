package me.bannerbound.com.api.research.treetypes;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import me.bannerbound.com.api.research.ResearchDefinition;
import me.bannerbound.com.api.research.ResearchDefinitionReader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FaithResearchTreeResource extends SimpleJsonResourceReloadListener {
    public static final String FOLDER = "research_faith";
    private static final Gson GSON = new Gson();
    private static volatile Map<String, ResearchDefinition> TREE = Map.of();

    public FaithResearchTreeResource() {
        super(GSON, FOLDER);
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> resources, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        Map<String, ResearchDefinition> map = new HashMap<>();

        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            String id = entry.getKey().toString();
            map.put(id, ResearchDefinitionReader.readResearchDefinitionFromJson(id, entry.getValue()));
        }

        TREE = Collections.unmodifiableMap(map);
    }

    public static Map<String, ResearchDefinition> getAll() {
        return TREE;
    }

    public static ResearchDefinition get(String id) {
        return TREE.get(id);
    }
}
