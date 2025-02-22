package net.etylop.immersivefarming.client.renderer;

import net.etylop.immersivefarming.ImmersiveFarming;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class IFModelLayers {

    public static final ModelLayerLocation PLOW = main("plow");
    public static final ModelLayerLocation SOWER = main("sower");

    private static ModelLayerLocation main(String name) {
        return layer(name, "main");
    }

    private static ModelLayerLocation layer(String name, String layer) {
        return new ModelLayerLocation(new ResourceLocation(ImmersiveFarming.MOD_ID, name), layer);
    }
}
