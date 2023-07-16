package com.pasta.ascendance.items.curios;

import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.capabilities.nanites.infection.PlayerNaniteInfectionProvider;
import com.pasta.ascendance.core.reggers.ItemRegger;
import com.pasta.ascendance.core.server.ASCServerSideHandler;
import com.pasta.ascendance.core.server.packets.InfectionCapabilityC2SPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class AgressiveSmallColony extends Item implements ICurioItem {

    public AgressiveSmallColony(Properties properties) {
        super(properties);
    }
    private Random rand = new Random();

    public boolean hasCurioItem(Player player) {
        return CuriosApi.getCuriosHelper().getCuriosHandler(player)
                .map(handler -> {
                    ICurioStacksHandler curioHandler = handler.getStacksHandler("curio").orElse(null);

                    if (curioHandler == null) return false; // No 'curio' type found

                    for (int i = 0; i < curioHandler.getSlots(); i++) {
                        ItemStack stack = curioHandler.getStacks().getStackInSlot(i);
                        if (stack.getItem() == ItemRegger.SLEEPING_INJECTION.get()) {
                            return true;
                        }
                    }
                    return false;
                }).orElse(false);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.getWearer();
        if (hasCurioItem((Player) entity)){
            entity.hurt(new DamageSource("nanites"), 0.5F);
        }
        else{
            entity.hurt(new DamageSource("nanites").bypassArmor(), 1.5F);
        }

        if (entity.getLevel().isClientSide){
            if(rand.nextFloat()>0.8){
                ASCServerSideHandler.sendToServer(new InfectionCapabilityC2SPacket(1));
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        Style boldStyle = Style.EMPTY.withBold(true);
        Style normalStyle = Style.EMPTY;
        MutableComponent tooltipText = Component.literal("I would ").setStyle(normalStyle)
                .append( Component.literal("not").setStyle(boldStyle))
                .append( Component.literal(" recommend to put this on")
                .setStyle(normalStyle));

        tooltip.add(tooltipText);

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
