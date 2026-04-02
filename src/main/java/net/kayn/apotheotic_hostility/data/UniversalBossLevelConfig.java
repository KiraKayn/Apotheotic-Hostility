package net.kayn.apotheotic_hostility.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kayn.apotheotic_hostility.ApotheoticHostility;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

public class UniversalBossLevelConfig extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new Gson();
    public static final UniversalBossLevelConfig INSTANCE = new UniversalBossLevelConfig();
    private static final Map<String, Integer> RARITY_MIN_LEVELS = new HashMap<>();

    public UniversalBossLevelConfig() {
        super(GSON, "universal_boss");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager manager, ProfilerFiller profiler) {
        RARITY_MIN_LEVELS.clear();
        System.out.println("UNIVERSAL BOSS APPLY CALLED, files: " + objects.keySet());
        for (JsonElement element : objects.values()) {
            JsonObject json = element.getAsJsonObject();
            System.out.println("JSON KEYS IN FILE: " + json.keySet());
            if (!json.has("tier_min_levels")) {
                System.out.println("NO tier_min_levels FOUND");
                continue;
            }
            JsonObject tiers = json.getAsJsonObject("tier_min_levels");
            for (Map.Entry<String, JsonElement> entry : tiers.entrySet()) {
                RARITY_MIN_LEVELS.put(entry.getKey(), entry.getValue().getAsInt());
            }
            break;
        }
        ApotheoticHostility.LOGGER.info("universal_boss rarity min levels loaded: {}", RARITY_MIN_LEVELS);
    }

    public static int getMinLevelForRarity(String rarity) {
        return RARITY_MIN_LEVELS.getOrDefault(rarity, 0);
    }

    public static Map<String, Integer> getAllMinLevels() {
        return RARITY_MIN_LEVELS;
    }
}