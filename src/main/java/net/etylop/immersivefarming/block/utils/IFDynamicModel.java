package net.etylop.immersivefarming.block.utils;

import blusunrize.immersiveengineering.common.util.Utils;
import net.etylop.immersivefarming.ImmersiveFarming;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

import java.util.List;

public class IFDynamicModel {
    private final ResourceLocation name;

    public IFDynamicModel(String desc)
    {
        this.name = new ResourceLocation(ImmersiveFarming.MOD_ID, "dynamic/"+desc);
        ForgeModelBakery.addSpecialModel(this.name);
    }

    public BakedModel get()
    {
        final BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        return blockRenderer.getBlockModelShaper().getModelManager().getModel(name);
    }

    public List<BakedQuad> getNullQuads()
    {
        return getNullQuads(EmptyModelData.INSTANCE);
    }

    public List<BakedQuad> getNullQuads(IModelData data)
    {
        return get().getQuads(null, null, Utils.RAND, data);
    }

    public ResourceLocation getName()
    {
        return name;
    }
}
