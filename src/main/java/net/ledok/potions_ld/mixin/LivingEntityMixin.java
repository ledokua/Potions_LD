package net.ledok.potions_ld.mixin;

import net.ledok.potions_ld.registry.EffectRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    // In 1.21, the key is likely Holder<MobEffect>, but we only iterate values so generic type erasure saves us here.
    // However, to be safe and correct, we should treat it as Map<?, MobEffectInstance> or just use the raw type if we are unsure of the key.
    // But since we only use .values(), Map<MobEffect, MobEffectInstance> is fine for the shadow definition as long as we don't try to put/get with MobEffect keys.
    @Shadow @Final private Map<MobEffect, MobEffectInstance> activeEffects;

    @Shadow protected abstract void onEffectRemoved(MobEffectInstance effect);

    @Inject(method = "removeAllEffects", at = @At("HEAD"), cancellable = true)
    private void onRemoveAllEffects(CallbackInfoReturnable<Boolean> cir) {
        if (this.level().isClientSide) {
            cir.setReturnValue(false);
            return;
        }

        boolean changed = false;
        Iterator<MobEffectInstance> iterator = this.activeEffects.values().iterator();
        while (iterator.hasNext()) {
            MobEffectInstance effect = iterator.next();
            
            // Check if it is Potion Sickness
            // In 1.21, getEffect() returns Holder<MobEffect>.
            // EffectRegistry.POTION_SICKNESS is also a Holder<MobEffect>.
            // We should compare them directly.
            if (effect.getEffect().equals(EffectRegistry.POTION_SICKNESS)) {
                continue; // Skip removing this effect
            }
            
            this.onEffectRemoved(effect);
            iterator.remove();
            changed = true;
        }
        cir.setReturnValue(changed);
    }
}
