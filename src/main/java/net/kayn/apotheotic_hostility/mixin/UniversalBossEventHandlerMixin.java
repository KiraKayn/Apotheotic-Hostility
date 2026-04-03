package net.kayn.apotheotic_hostility.mixin;

import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import net.kayn.apotheotic_hostility.data.MobLevelContext;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.kayn.fallen_gems_affixes.adventure.boss.UniversalBossEventHandler", remap = false)
public class UniversalBossEventHandlerMixin {

    @Inject(
            method = "onEntityJoin",
            at = @At("HEAD"),
            remap = false
    )
    private static void storeLevel(MobSpawnEvent.FinalizeSpawn event, CallbackInfo ci) {
        if (!ModList.get().isLoaded("fallen_gems_affixes")) return;
        if (!(event.getEntity() instanceof Mob)) return;
        Mob mob = event.getEntity();

        if (!MobTraitCap.HOLDER.isProper(mob)) return;

        int level;
        try {
            level = MobTraitCap.HOLDER.get(mob).getLevel();
        } catch (Exception e) {
            return;
        }

        MobLevelContext.set(level);
    }

    @Inject(
            method = "onEntityJoin",
            at = @At("RETURN"),
            remap = false
    )
    private static void clearLevel(MobSpawnEvent.FinalizeSpawn event, CallbackInfo ci) {
        MobLevelContext.clear();
    }
}