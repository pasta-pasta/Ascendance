package com.pasta.ascendance.core.reggers;

import com.pasta.ascendance.Ascendance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;

import static net.minecraft.tags.TagEntry.tag;

public class TagRegger {

    public static final TagKey<Block> coloniesTag = BlockTags.create(new ResourceLocation(Ascendance.MOD_ID, "colonies"));

}
