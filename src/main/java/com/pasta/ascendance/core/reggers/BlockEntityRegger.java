package com.pasta.ascendance.core.reggers;

import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.blocks.entities.NaniteDamagerEntity;
import com.pasta.ascendance.blocks.entities.NanoInjectorEntity;
import com.pasta.ascendance.compacted.blocks.entities.CompactedBlockEntity;
import com.pasta.ascendance.compacted.blocks.entities.CompactedDimBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.pasta.ascendance.core.reggers.BlockRegger.*;

public class BlockEntityRegger {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES
            = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Ascendance.MOD_ID);

    public static final RegistryObject<BlockEntityType<NanoInjectorEntity>> NANOINJECTORENTITY = BLOCK_ENTITIES.register("nanoinjector", ()
            -> BlockEntityType.Builder.of(NanoInjectorEntity::new, NANOINJECTOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<NaniteDamagerEntity>> NANITEDAMAGERENTITY = BLOCK_ENTITIES.register("nanitedamager", ()
            -> BlockEntityType.Builder.of(NaniteDamagerEntity::new, NANITE_DAMAGER.get()).build(null));

    public static final RegistryObject<BlockEntityType<CompactedBlockEntity>> COMPACTEDBLOCKENTITY = BLOCK_ENTITIES.register("compactedblock", ()
            -> BlockEntityType.Builder.of(CompactedBlockEntity::new, COMPACTED_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<CompactedDimBlockEntity>> COMPACTEDDIMBLOCKENTITY = BLOCK_ENTITIES.register("compacteddimblock", ()
            -> BlockEntityType.Builder.of(CompactedDimBlockEntity::new, COMPACTED_DIMBLOCK.get()).build(null));


}
