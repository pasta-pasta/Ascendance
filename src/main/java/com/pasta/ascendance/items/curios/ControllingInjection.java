package com.pasta.ascendance.items.curios;

import com.pasta.ascendance.core.server.ASCServerSideHandler;
import com.pasta.ascendance.core.server.packets.InfectionCapabilityC2SPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
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

public class ControllingInjection extends Item implements ICurioItem {

    private final Random rand = new Random();

    public ControllingInjection(Properties properties) {
        super(properties);
    }



    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.getWearer();

    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.literal("This extremely intricate group of nanites will prevent you from dying of nanite infestation. §mWith a chance."));

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
