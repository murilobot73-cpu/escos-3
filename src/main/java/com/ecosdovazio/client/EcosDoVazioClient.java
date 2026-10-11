package com.ecosdovazio.client;

import com.ecosdovazio.entity.HorrorMob;
import com.ecosdovazio.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;

public class EcosDoVazioClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        reg(ModEntities.OBSERVADOR, "observador", 1.35f);
        reg(ModEntities.SOMBRA, "sombra", 1.0f);
        reg(ModEntities.CRIANCA, "crianca", 0.6f);
        reg(ModEntities.ECO, "eco", 1.0f);
        reg(ModEntities.HOSPEDEIRO, "hospedeiro", 1.9f);
    }

    private static void reg(EntityType<? extends HorrorMob> tipo, String nome, float escala) {
        EntityRendererRegistry.register(tipo, (EntityRendererProvider<HorrorMob>) ctx -> new HorrorRenderer(ctx, nome, escala));
    }
}
