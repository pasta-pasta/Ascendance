package com.pasta.ascendance.items.curios;

import com.pasta.ascendance.core.server.ASCServerSideHandler;
import com.pasta.ascendance.core.server.packets.InfectionCapabilityC2SPacket;
import net.minecraft.network.chat.Component;
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

    private final Random rand = new Random();

    public SleepingInjection(Properties properties) {
        super(properties);
    }



    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.getWearer();
        if (entity instanceof Player && entity.getLevel().isClientSide){
            if(rand.nextFloat()>0.9){
                ASCServerSideHandler.sendToServer(new InfectionCapabilityC2SPacket(-2, entity.getUUID()));
                stack.setDamageValue(stack.getDamageValue()+1);
                if (stack.getDamageValue() >= stack.getMaxDamage()) stack.setCount(0);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.literal("A sleeping group of nanites will provide some reduction of nanite infection."));

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
