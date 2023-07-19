package com.pasta.ascendance.items.curios;

import com.pasta.ascendance.core.server.ASCServerSideHandler;
import com.pasta.ascendance.core.server.packets.InfectionCapabilityC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class GuardInjection extends Item implements ICurioItem {

    private final Random rand = new Random();

    public GuardInjection(Properties properties) {
        super(properties);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack oldStack) {
        LivingEntity entity = slotContext.getWearer();
        if (entity instanceof Player) {
            // Remove the effects
            entity.removeEffect(MobEffects.HEALTH_BOOST);
            entity.removeEffect(MobEffects.REGENERATION);
        }
    }


    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.getWearer();
        if (entity instanceof Player && entity.getLevel().isClientSide){
            if (!entity.hasEffect(MobEffects.HEALTH_BOOST)){
                entity.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 200000, 5));
            }
            if (!entity.hasEffect(MobEffects.REGENERATION)){
                entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 2));
            }
            if (rand.nextFloat()>=0.95){
                ASCServerSideHandler.sendToServer(new InfectionCapabilityC2SPacket(1, entity.getUUID()));
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.literal("This guarding group of nanites will defend you with its life at cost of some infection."));

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
