package com.pasta.ascendance.core.reggers;

import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.containers.NanoInjectorMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuRegger {

    public static final DeferredRegister<MenuType<?>> MENUS
            = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Ascendance.MOD_ID);

    public static final RegistryObject<MenuType<NanoInjectorMenu>> NANOINJECTOR_MENU =
            registerMenuType(NanoInjectorMenu::new, "nanoinjector_menu");

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

}
