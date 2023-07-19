package com.pasta.ascendance.core.reggers;

import com.pasta.ascendance.Ascendance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class TagRegger {

    public static final TagKey<Block> coloniesTag = BlockTags.create(new ResourceLocation(Ascendance.MOD_ID, "colonies"));

    public static final TagKey<Item> compostables = ItemTags.create(new ResourceLocation(Ascendance.MOD_ID, "compostables"));

    public static final TagKey<Block> naniteBlockers = BlockTags.create(new ResourceLocation(Ascendance.MOD_ID, "nanite_blockers"));

}
