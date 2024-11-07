package net.etylop.immersivefarming.client.renderer.entity.model;

import net.etylop.immersivefarming.entity.SowerEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;

public final class SowerModel extends CartModel<SowerEntity> {
    private final ModelPart plowShaftUpper;
    private final ModelPart plowShaftLower;

    public SowerModel(final ModelPart root) {
        super(root);
        ModelPart parts = root.getChild("body").getChild("parts");
        this.plowShaftUpper = parts.getChild("plow_shaft_upper");
        this.plowShaftLower = this.plowShaftUpper.getChild("plow_shaft_lower" );
    }

    public ModelPart getShaft(final int original) {
        return this.plowShaftLower;
    }

    @Override
    public void setupAnim(final SowerEntity entity, final float delta, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float pitch) {
        super.setupAnim(entity, delta, limbSwingAmount, ageInTicks, netHeadYaw, pitch);
        this.plowShaftUpper.xRot = (float) (entity.getPlowing() ? Math.PI / 4.0D - Math.toRadians(pitch) : Math.PI / 3D);
    }

    public static LayerDefinition createLayer() {
        final MeshDefinition def = CartModel.createDefinition();

        final EasyMeshBuilder axis = new EasyMeshBuilder("axis", 0, 0);
        axis.addBox(-12.5F, -1.0F, -1.0F, 25, 2, 2);

        final EasyMeshBuilder[] triangle = new EasyMeshBuilder[3];
        triangle[0] = new EasyMeshBuilder("triangle_0", 0, 4);
        triangle[0].addBox(-7.5F, -9.0F, 0.0F, 15, 2, 2);

        triangle[1] = new EasyMeshBuilder("triangle_1", 0, 11);
        triangle[1].addBox(-5.0F, -9.0F, 0.5F, 2, 14, 2);
        triangle[1].zRot = -0.175F;

        triangle[2] = new EasyMeshBuilder("triangle_2", 0, 11);
        triangle[2].mirror(true);
        triangle[2].addBox(3.0F, -9.0F, 0.5F, 2, 14, 2);
        triangle[2].zRot = 0.175F;

        final EasyMeshBuilder shaft = new EasyMeshBuilder("shaft", 0, 8);
        shaft.zRot = -0.07F;
        shaft.addBox(0.0F, 0.0F, -8.0F, 20, 2, 1);
        shaft.addBox(0.0F, 0.0F, 7.0F, 20, 2, 1);

        final EasyMeshBuilder shaftConnector = new EasyMeshBuilder("shaftConnector", 0, 27);
        shaftConnector.zRot = -0.26F;
        shaftConnector.addBox(-16.0F, 0.0F, -8.0F, 16, 2, 1);
        shaftConnector.addBox(-16.0F, 0.0F, 7.0F, 16, 2, 1);

        final EasyMeshBuilder shafts = new EasyMeshBuilder("shafts");
        shafts.setRotationPoint(0.0F, 0.0F, -14.0F);
        shafts.yRot = (float) Math.PI / 2.0F;
        shafts.addChild(shaft);
        shafts.addChild(shaftConnector);

        final EasyMeshBuilder plowShaftUpper;
        final EasyMeshBuilder plowShaftLower;

        plowShaftUpper = new EasyMeshBuilder("plow_shaft_upper", 56, 0);
        plowShaftUpper.addBox(-1.0F, -2.0F, -2.0F, 2, 30, 2);
        plowShaftUpper.setRotationPoint(-3.0F + 3, -7.0F, 0.0F);
        plowShaftUpper.yRot = -0.523599F + (float) Math.PI / 6.0F;

        plowShaftLower = new EasyMeshBuilder("plow_shaft_lower", 42, 4);
        plowShaftLower.addBox(-15.0F, -0.7F, -0.7F, 30, 8, 8);
        plowShaftLower.setRotationPoint(0.0F, 28.0F, -1.0F);
        plowShaftLower.xRot = (float) Math.PI / 4.0F;
        plowShaftUpper.addChild(plowShaftLower);

        final EasyMeshBuilder plowHandle = new EasyMeshBuilder("plow_handle", 50, 4);
        plowHandle.addBox(-0.5F, 0.0F, -0.5F, 1, 18, 1);
        plowHandle.setRotationPoint(0.0F, 33.0F, 5.0F);
        plowHandle.xRot = (float) Math.PI / 2.0F;
        plowShaftUpper.addChild(plowHandle);

        final EasyMeshBuilder plowHandleGrip = new EasyMeshBuilder("plow_handle_grip", 50, 23);
        plowHandleGrip.addBox(-0.5F, 0.0F, -1.0F, 1, 5, 1);
        plowHandleGrip.setRotationPoint(0.0F, 32.8F, 21.0F);
        plowHandleGrip.xRot = (float) Math.PI / 4.0F;
        plowShaftUpper.addChild(plowHandleGrip);

        final EasyMeshBuilder parts = new EasyMeshBuilder("parts");
        parts.setRotationPoint(0.0F, -5.0F, -1.0F);
        parts.addChild(shafts);
        parts.addChild(triangle[0]);
        parts.addChild(triangle[1]);
        parts.addChild(triangle[2]);
        parts.addChild(plowShaftUpper);

        final EasyMeshBuilder body = CartModel.createBody();
        body.addChild(axis);
        body.addChild(parts);
        body.build(def.getRoot());

        return LayerDefinition.create(def, 64, 64);
    }
}
