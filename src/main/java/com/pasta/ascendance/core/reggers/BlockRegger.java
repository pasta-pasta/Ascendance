package com.pasta.ascendance.core.reggers;

import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.blocks.NaniteDamager;
import com.pasta.ascendance.blocks.NaniteDoor;
import com.pasta.ascendance.blocks.colonies.AggressiveColony;
import com.pasta.ascendance.blocks.colonies.ControllingColony;
import com.pasta.ascendance.blocks.colonies.GuardColony;
import com.pasta.ascendance.blocks.colonies.NanoColony;
import com.pasta.ascendance.blocks.NanoInjector;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockRegger {
    public static final DeferredRegister<Block> BLOCKS
            = DeferredRegister.create(ForgeRegistries.BLOCKS, Ascendance.MOD_ID);

    public static final RegistryObject<Block> NANOCOLONY = BLOCKS.register("nanocolony", () ->
            new NanoColony(Block.Properties.of(Material.STONE)
                    .strength(1.5f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.SCULK))
    );

    public static final RegistryObject<Block> NANOINJECTOR = BLOCKS.register("nanoinjector", () ->
            new NanoInjector(Block.Properties.of(Material.FROGLIGHT)
                    .strength(1.5f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.SCULK))
    );

    public static final RegistryObject<Block> GUARD_COLONY_BLOCK = BLOCKS.register("guardcolonyblock", () ->
            new GuardColony(Block.Properties.of(Material.STONE)
                    .strength(3.5f, 60.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> AGGRESSIVE_COLONY_BLOCK = BLOCKS.register("aggressivecolonyblock", () ->
            new AggressiveColony(Block.Properties.of(Material.STONE)
                    .strength(0.5f, 60000.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> CONTROLLING_COLONY_BLOCK = BLOCKS.register("controllingcolonyblock", () ->
            new ControllingColony(Block.Properties.of(Material.STONE)
                    .strength(5f, 60000.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> NANITE_GLASS = BLOCKS.register("nanite_glass", () ->
            new GlassBlock(Block.Properties.of(Material.GLASS)
                    .strength(5f, 60.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.GLASS).noOcclusion())
    );

    public static final RegistryObject<Block> NANITE_DOOR = BLOCKS.register("nanite_door", () ->
            new NaniteDoor(Block.Properties.of(Material.STONE)
                    .strength(0.5f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> NANITE_DAMAGER = BLOCKS.register("nanite_damager", () ->
            new NaniteDamager(Block.Properties.of(Material.STONE)
                    .strength(0.5f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE))
    );


}
