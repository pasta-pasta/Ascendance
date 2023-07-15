package com.pasta.ascendance.items;

import com.pasta.ascendance.blocks.entities.NanoInjectorEntity;
import com.pasta.ascendance.containers.NanoInjectorScreen;
import com.pasta.ascendance.core.ASCFunctions;
import com.pasta.ascendance.core.reggers.BlockRegger;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

import static com.pasta.ascendance.core.ASCFunctions.rayTrace;

public class NanoInjectorItem extends Item {
    public NanoInjectorItem(Properties properties){

        super(properties);
    }



    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        return super.use(world, player, hand);
    }

}
