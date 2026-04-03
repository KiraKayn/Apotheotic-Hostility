package net.kayn.apotheotic_hostility.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kayn.apotheotic_hostility.ApotheoticHostility;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class UniversalBossLevelConfig extends SimpleJsonResourceReloadListener {

    public static final UniversalBossLevelConfig INSTANCE = new UniversalBossLevelConfig();

    private static Map<String, Integer> RARITY_MIN_LEVELS = Collections.emptyMap();

    public UniversalBossLevelConfig() {
        super(new Gson(), "universal_boss");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager manager, ProfilerFiller profiler) {
        Map<String, Integer> loaded = new LinkedHashMap<>();

        for (Map.Entry<ResourceLocation, JsonElement> file : objects.entrySet()) {
            JsonObject json = file.getValue().getAsJsonObject();
            if (!json.has("tier_min_levels")) continue;

            JsonObject tiers = json.getAsJsonObject("tier_min_levels");
            for (Map.Entry<String, JsonElement> entry : tiers.entrySet()) {
                loaded.put(entry.getKey(), entry.getValue().getAsInt());
            }
        }

        RARITY_MIN_LEVELS = Collections.unmodifiableMap(loaded);

        if (RARITY_MIN_LEVELS.isEmpty()) {
            ApotheoticHostility.LOGGER.info("universal_boss: no tier_min_levels found — level filtering disabled.");
        } else {
            ApotheoticHostility.LOGGER.info("universal_boss: tier_min_levels loaded: {}", RARITY_MIN_LEVELS);
        }
    }

    public static Map<String, Integer> getMinLevels() {
        return RARITY_MIN_LEVELS;
    }
}