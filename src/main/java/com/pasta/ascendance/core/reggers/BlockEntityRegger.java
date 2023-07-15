package com.pasta.ascendance.core.reggers;

import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.blocks.entities.NanoInjectorEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import com.pasta.ascendance.core.reggers.*;

import static com.pasta.ascendance.core.reggers.BlockRegger.NANOINJECTOR;

public class BlockEntityRegger {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES
            = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Ascendance.MOD_ID);

    public static final RegistryObject<BlockEntityType<NanoInjectorEntity>> NANOINJECTORENTITY = BLOCK_ENTITIES.register("nanoinjector", ()
            -> BlockEntityType.Builder.of(NanoInjectorEntity::new, NANOINJECTOR.get()).build(null));


}
