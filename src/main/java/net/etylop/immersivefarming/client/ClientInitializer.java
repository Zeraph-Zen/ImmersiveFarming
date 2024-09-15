package net.etylop.immersivefarming.client;


import net.etylop.immersivefarming.CommonInitializer;
import net.etylop.immersivefarming.ImmersiveFarming;
import net.etylop.immersivefarming.gui.screen.PlowScreen;
import net.etylop.immersivefarming.client.renderer.AstikorCartsModelLayers;
import net.etylop.immersivefarming.client.renderer.entity.PlowRenderer;
import net.etylop.immersivefarming.client.renderer.entity.model.PlowModel;
import net.etylop.immersivefarming.client.renderer.texture.AssembledTexture;
import net.etylop.immersivefarming.client.renderer.texture.AssembledTextureFactory;
import net.etylop.immersivefarming.client.renderer.texture.Material;
import net.etylop.immersivefarming.entity.IFEntities;
import net.etylop.immersivefarming.gui.IFMenuTypes;
import net.etylop.immersivefarming.network.serverbound.ActionKeyMessage;
import net.etylop.immersivefarming.network.serverbound.ToggleSlowMessage;
import net.etylop.immersivefarming.world.IFWorld;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenOpenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

public final class ClientInitializer extends CommonInitializer {
    private final KeyMapping action = new KeyMapping("key.astikorcarts.desc", GLFW.GLFW_KEY_R, "key.categories.astikorcarts");

    @Override
    public void init(final Context mod) {
        super.init(mod);
        mod.bus().<TickEvent.ClientTickEvent>addListener(e -> {
            if (e.phase == TickEvent.Phase.END) {
                final Minecraft mc = Minecraft.getInstance();
                final Level world = mc.level;
                if (world != null) {
                    while (this.action.consumeClick()) {
                        ImmersiveFarming.CHANNEL.sendToServer(new ActionKeyMessage());
                    }
                    if (!mc.isPaused()) {
                        IFWorld.get(world).ifPresent(IFWorld::tick);
                    }
                }
            }
        });
        mod.bus().<InputEvent.KeyInputEvent>addListener(e -> {
            final Minecraft mc = Minecraft.getInstance();
            final Player player = mc.player;
            if (player != null) {
                if (ToggleSlowMessage.getCart(player).isPresent()) {
                    final KeyMapping binding = mc.options.keySprint;
                    while (binding.consumeClick()) {
                        ImmersiveFarming.CHANNEL.sendToServer(new ToggleSlowMessage());
                        KeyMapping.set(binding.getKey(), false);
                    }
                }
            }
        });
        mod.bus().<ScreenOpenEvent>addListener(e -> {
            if (e.getScreen() instanceof InventoryScreen) {
                final LocalPlayer player = Minecraft.getInstance().player;
            }
        });
        mod.modBus().<FMLClientSetupEvent>addListener(e -> {
            MenuScreens.register(IFMenuTypes.PLOW_CART.get(), PlowScreen::new);
            ClientRegistry.registerKeyBinding(this.action);
        });
        mod.modBus().<EntityRenderersEvent.RegisterRenderers>addListener(e -> {
            e.registerEntityRenderer(IFEntities.PLOW.get(), PlowRenderer::new);
        });
        mod.modBus().<EntityRenderersEvent.RegisterLayerDefinitions>addListener(e -> {
            ForgeHooksClient.registerLayerDefinition(AstikorCartsModelLayers.PLOW, PlowModel::createLayer);
        });
        new AssembledTextureFactory()
            .add(new ResourceLocation(ImmersiveFarming.MOD_ID, "textures/entity/plow.png"), new AssembledTexture(64, 64)
                .add(new Material(new ResourceLocation("block/oak_planks"), 16)
                    .fill(0, 0, 64, 32, Material.R90)
                    .fill(0, 8, 42, 3, Material.R0, 0, 1)
                    .fill(0, 27, 34, 3, Material.R0, 0, 2)
                )
                .add(new Material(new ResourceLocation("block/stripped_spruce_log"), 16)
                    .fill(54, 54, 10, 10, Material.R0, 2, 0)
                )
                .add(new Material(new ResourceLocation("block/oak_log"), 16)
                    .fill(0, 0, 54, 4, Material.R90)
                    .fill(46, 60, 8, 4, Material.R90)
                )
                .add(new Material(new ResourceLocation("block/stone"), 16)
                    .fill(62, 55, 2, 9)
                )
            )
            .register(mod.modBus());
    }
}
