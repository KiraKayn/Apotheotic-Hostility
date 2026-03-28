package net.kayn.apotheotic_hostility;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class FGAMixinConnector implements IMixinConnector {

    @Override
    public void connect() {

        Mixins.addConfiguration("apotheotic_hostility.mixins.json");
        boolean isFGAExist = getClass().getClassLoader().getResource(
                "net/kayn/fallen_gems_affixes/FallenGemsAffixes.class") != null;
        System.out.println("FGAMixinConnector.connect() called, FGA exists: " + isFGAExist);
        if (isFGAExist) {
            Mixins.addConfiguration("apotheotic_hostility.fga.mixins.json");
        }
    }
}