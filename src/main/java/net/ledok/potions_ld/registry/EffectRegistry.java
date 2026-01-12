package net.ledok.potions_ld.registry;

import net.ledok.potions_ld.PotionsLdMod;
import net.ledok.potions_ld.effects.PotionSicknessEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class EffectRegistry {
    public static final Holder<MobEffect> POTION_SICKNESS = Registry.registerForHolder(
            BuiltInRegistries.MOB_EFFECT,
            ResourceLocation.fromNamespaceAndPath(PotionsLdMod.MOD_ID, "potion_sickness"),
            new PotionSicknessEffect(MobEffectCategory.HARMFUL, 0x4B5320) // Dark greenish color
    );

    public static void initialize() {
    }
}
