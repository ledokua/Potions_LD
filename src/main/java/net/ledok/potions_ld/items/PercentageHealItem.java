package net.ledok.potions_ld.items;

import net.ledok.potions_ld.registry.EffectRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Locale;

public class PercentageHealItem extends Item {

    private final float healPercentage;
    private final int useTimeTicks;
    private final int sicknessTicks;
    private final int additionalTicks;
    private final boolean givesSickness;

    public PercentageHealItem(Properties settings, float healPercentage, int useTimeTicks, int sicknessTicks, int additionalTicks, boolean givesSickness) {
        super(settings);
        this.healPercentage = Math.max(0.0f, Math.min(1.0f, healPercentage));
        this.useTimeTicks = useTimeTicks;
        this.sicknessTicks = sicknessTicks;
        this.additionalTicks = additionalTicks;
        this.givesSickness = givesSickness;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        if (user instanceof Player player) {
            if (player.getHealth() < player.getMaxHealth()) {
                if (!world.isClientSide) {
                    float effectiveHealPercentage = this.healPercentage;
                    
                    if (player.hasEffect(EffectRegistry.POTION_SICKNESS)) {
                        MobEffectInstance effectInstance = player.getEffect(EffectRegistry.POTION_SICKNESS);
                        if (effectInstance != null) {
                            int amplifier = effectInstance.getAmplifier();
                            // Reduce healing by 5% per level (amplifier + 1)
                            // Level 1 (amplifier 0) = 5% reduction
                            // Level 2 (amplifier 1) = 10% reduction
                            float reduction = 0.05f * (amplifier + 1);
                            effectiveHealPercentage = Math.max(0.0f, effectiveHealPercentage - reduction);
                        }
                    }

                    float healAmount = player.getMaxHealth() * effectiveHealPercentage;
                    player.heal(healAmount);
                    
                    // Apply Potion Sickness logic
                    if (this.givesSickness) {
                        if (player.hasEffect(EffectRegistry.POTION_SICKNESS)) {
                            MobEffectInstance currentEffect = player.getEffect(EffectRegistry.POTION_SICKNESS);
                            if (currentEffect != null) {
                                int currentDuration = currentEffect.getDuration();
                                int currentAmplifier = currentEffect.getAmplifier();
                                
                                // Increase duration by additionalTicks
                                int newDuration = currentDuration + this.additionalTicks;
                                
                                // Increase amplifier by 1
                                int newAmplifier = currentAmplifier + 1;
                                
                                player.addEffect(new MobEffectInstance(EffectRegistry.POTION_SICKNESS, newDuration, newAmplifier));
                            }
                        } else {
                            player.addEffect(new MobEffectInstance(EffectRegistry.POTION_SICKNESS, this.sicknessTicks, 0));
                        }
                    }

                    player.displayClientMessage(Component.translatable("message.potions_ld.healing_potion_used").withStyle(ChatFormatting.GREEN), true);
                    player.playSound(SoundEvents.GENERIC_DRINK, 1.0f, 1.0f);
                }
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
        }
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return this.useTimeTicks;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (user.getHealth() >= user.getMaxHealth()) {
            return InteractionResultHolder.fail(itemStack);
        }
        user.startUsingItem(hand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        tooltip.add(Component.literal(""));

        // Prepare the placeholder values
        String healAmount = (int)(this.healPercentage * 100) + "%";
        // Use Locale.ROOT to ensure dot is used as decimal separator
        String useTime = String.format(Locale.ROOT, "%.1f", this.useTimeTicks / 20.0f);
        String sicknessTime = String.format(Locale.ROOT, "%.1f", this.sicknessTicks / 20.0f);

        tooltip.add(Component.translatable("item.potions_ld.healing_potion.tooltip.heal", healAmount)
                .withStyle(ChatFormatting.BLUE));

        tooltip.add(Component.translatable("item.potions_ld.healing_potion.tooltip.use_time", useTime)
                .withStyle(ChatFormatting.GRAY));

        if (this.givesSickness) {
             tooltip.add(Component.translatable("item.potions_ld.healing_potion.tooltip.sickness", sicknessTime)
                .withStyle(ChatFormatting.RED));
        }

        super.appendHoverText(stack, context, tooltip, type);
    }
}
