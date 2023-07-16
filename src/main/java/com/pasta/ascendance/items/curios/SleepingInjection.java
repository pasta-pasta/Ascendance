package com.pasta.ascendance.items.curios;

import com.pasta.ascendance.core.server.ASCServerSideHandler;
import com.pasta.ascendance.core.server.packets.InfectionCapabilityC2SPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
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

public class SleepingInjection extends Item implements ICurioItem {

    private Random rand = new Random();

    public SleepingInjection(Properties properties) {
        super(properties);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        ICurioItem.super.onEquip(slotContext, prevStack, stack);
        AttributeInstance maxHealth = slotContext.getWearer().getAttribute(Attributes.MAX_HEALTH);
        assert maxHealth != null;
        maxHealth.setBaseValue(maxHealth.getBaseValue()-4.0D);
        if (maxHealth.getValue() > maxHealth.getAttribute().getDefaultValue()){
            slotContext.getWearer().setHealth((float) maxHealth.getValue());
        }
        if (prevStack.getDamageValue() > 0) {
            stack.setDamageValue(prevStack.getDamageValue());
        }

    }


    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        ICurioItem.super.onUnequip(slotContext, newStack, stack);
        AttributeInstance maxHealth = slotContext.getWearer().getAttribute(Attributes.MAX_HEALTH);
        assert maxHealth != null;
        maxHealth.setBaseValue(maxHealth.getBaseValue()+4.0D);
        if (maxHealth.getValue() > maxHealth.getAttribute().getDefaultValue()){
            slotContext.getWearer().setHealth((float) maxHealth.getValue());
        }
        if (newStack.getDamageValue() > 0) {
            stack.setDamageValue(newStack.getDamageValue());
        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.getWearer();
        if (entity instanceof Player && entity.getLevel().isClientSide){
            if(rand.nextFloat()>0.85){
                ASCServerSideHandler.sendToServer(new InfectionCapabilityC2SPacket(-2));
                stack.setDamageValue(stack.getDamageValue()+1);
                if (stack.getDamageValue() >= stack.getMaxDamage()) stack.setCount(0);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.literal("A sleeping group of nanites will provide some reduction of nanite infection, at cost of your health and it's durability."));

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
