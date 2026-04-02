package net.kayn.apotheotic_hostility.mixin;

import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import net.kayn.apotheotic_hostility.data.MobLevelContext;
import net.kayn.apotheotic_hostility.data.UniversalBossLevelConfig;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Set;

@Mixin(targets = "net.kayn.fallen_gems_affixes.adventure.boss.UniversalBossConfig", remap = false)
public class UniversalBossConfigMixin {

    @Inject(
            method = "rollRarity(Lnet/minecraft/util/RandomSource;Ljava/util/Set;)Ldev/shadowsoffire/apotheosis/adventure/loot/LootRarity;",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void filterRarityByLevel(RandomSource rand, Set<LootRarity> allowed,
                                     CallbackInfoReturnable<LootRarity> cir) {
        int mobLevel = MobLevelContext.get();
        if (mobLevel < 0) return;

        Map<String, Integer> minLevels = UniversalBossLevelConfig.getAllMinLevels();
        if (minLevels.isEmpty()) return;

        String eligibleRarityKey = null;
        int highestMinLevel = -1;
        for (Map.Entry<String, Integer> entry : minLevels.entrySet()) {
            if (mobLevel < entry.getValue()) continue;
            if (entry.getValue() <= highestMinLevel) continue;

            if (allowed != null) {
                try {
                    LootRarity candidate = RarityRegistry.byLegacyId(entry.getKey()).get();
                    if (!allowed.contains(candidate)) continue;
                } catch (Exception e) {
                    continue;
                }
            }

            highestMinLevel = entry.getValue();
            eligibleRarityKey = entry.getKey();
        }

        if (eligibleRarityKey == null) {
            cir.setReturnValue(null);
            return;
        }

        try {
            LootRarity eligibleRarity = RarityRegistry.byLegacyId(eligibleRarityKey).get();

            net.kayn.fallen_gems_affixes.adventure.boss.UniversalBossConfig config =
                    net.kayn.fallen_gems_affixes.adventure.boss.UniversalBossLoader.getConfig();
            if (config == null) {
                cir.setReturnValue(null);
                return;
            }

            Float chance = null;
            for (Map.Entry<LootRarity, Float> entry : config.tierChances().entrySet()) {
                if (eligibleRarityKey.equals(config.getRarityKey(entry.getKey()))) {
                    chance = entry.getValue();
                    break;
                }
            }

            if (chance == null || rand.nextFloat() >= chance) {
                cir.setReturnValue(null);
                return;
            }

            cir.setReturnValue(eligibleRarity);

        } catch (Exception e) {
            cir.setReturnValue(null);
        }
    }
}