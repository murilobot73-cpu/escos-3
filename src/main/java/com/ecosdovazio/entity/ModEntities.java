package com.ecosdovazio.entity;

import com.ecosdovazio.EcosDoVazio;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class ModEntities {
    private ModEntities() {}

    public static final EntityType<ObservadorEntity> OBSERVADOR = criar("observador", ObservadorEntity::new, 0.6f, 2.7f);
    public static final EntityType<SombraEntity> SOMBRA = criar("sombra", SombraEntity::new, 0.6f, 1.95f);
    public static final EntityType<CriancaEntity> CRIANCA = criar("crianca", CriancaEntity::new, 0.4f, 1.1f);
    public static final EntityType<EcoEntity> ECO = criar("eco", EcoEntity::new, 0.6f, 1.8f);
    public static final EntityType<HospedeiroEntity> HOSPEDEIRO = criar("hospedeiro", HospedeiroEntity::new, 1.2f, 3.6f);

    private static <T extends HorrorMob> EntityType<T> criar(String nome, EntityType.EntityFactory<T> fabrica, float largura, float altura) {
        ResourceKey<EntityType<?>> chave = ResourceKey.create(Registries.ENTITY_TYPE,
                Identifier.fromNamespaceAndPath(EcosDoVazio.ID, nome));
        EntityType<T> tipo = EntityType.Builder.of(fabrica, MobCategory.MISC)
                .sized(largura, altura)
                .clientTrackingRange(12)
                .updateInterval(1)
                .build(chave);
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, chave, tipo);
    }

    /** Chamar no onInitialize: força o carregamento da classe (registro) e define atributos. */
    public static void registrar() {
        FabricDefaultAttributeRegistry.register(OBSERVADOR, HorrorMob.atributos(0.2));
        FabricDefaultAttributeRegistry.register(SOMBRA, HorrorMob.atributos(0.36));
        FabricDefaultAttributeRegistry.register(CRIANCA, HorrorMob.atributos(0.2));
        FabricDefaultAttributeRegistry.register(ECO, HorrorMob.atributos(0.2));
        FabricDefaultAttributeRegistry.register(HOSPEDEIRO, HorrorMob.atributos(0.16));
    }
}
