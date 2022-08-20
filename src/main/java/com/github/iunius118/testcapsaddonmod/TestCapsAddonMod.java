package com.github.iunius118.testcapsaddonmod;

import com.github.iunius118.testcapsapi.api.capabilities.ITestCounter;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(TestCapsAddonMod.MODID)
public class TestCapsAddonMod {
    public static final String MODID = "testcapsaddonmod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", ExampleItem::new);

    public static final Capability<ITestCounter> TEST_COUNTER = CapabilityManager.get(new CapabilityToken<>(){});

    public TestCapsAddonMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
    }

    public static class ExampleItem extends Item {
        public ExampleItem() {
            super(new Item.Properties().tab(CreativeModeTab.TAB_MISC));
        }

        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
            if (!level.isClientSide()) {
                ItemStack itemStack = player.getItemInHand(interactionHand);
                itemStack.getCapability(TEST_COUNTER)
                        .ifPresent(testCounter -> countUp(testCounter, player));
            }

            return super.use(level, player, interactionHand);
        }

        private void countUp(ITestCounter testCounter, Player player) {
            testCounter.increase();

            int count = testCounter.get();
            String message = String.format("You have used this %s.", count == 1 ? "once" : (count == 2 ? "twice" : String.format("%d times", count)));
            MutableComponent messageComponent = Component.literal(message).withStyle(ChatFormatting.YELLOW);
            player.sendSystemMessage(messageComponent);
        }
    }
}
