package com.ecosdovazio.client;

import com.ecosdovazio.EcosDoVazio;
import com.ecosdovazio.entity.HorrorMob;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;

/**
 * Renderizador compartilhado: usa o esqueleto humanoide (só o formato do modelo),
 * mas cada criatura tem a SUA textura e a SUA escala.
 */
public class HorrorRenderer extends MobRenderer<HorrorMob, HumanoidRenderState, HumanoidModel<HumanoidRenderState>> {
    private final Identifier textura;
    private final float escala;

    public HorrorRenderer(EntityRendererProvider.Context ctx, String nome, float escala) {
        super(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER)), 0.4f * escala);
        this.textura = Identifier.fromNamespaceAndPath(EcosDoVazio.ID, "textures/entity/" + nome + ".png");
        this.escala = escala;
    }

    @Override
    public HumanoidRenderState createRenderState() {
        return new HumanoidRenderState();
    }

    @Override
    public Identifier getTextureLocation(HumanoidRenderState estado) {
        return textura;
    }

    @Override
    protected void scale(HumanoidRenderState estado, PoseStack pose) {
        pose.scale(escala, escala, escala);
    }
}
