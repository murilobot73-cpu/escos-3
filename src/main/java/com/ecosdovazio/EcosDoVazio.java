package com.ecosdovazio;

import com.ecosdovazio.entity.ModEntities;
import com.ecosdovazio.horror.Comandos;
import com.ecosdovazio.horror.Diretor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EcosDoVazio implements ModInitializer {
    public static final String ID = "ecos_do_vazio";
    public static final Logger LOG = LoggerFactory.getLogger(ID);

    @Override
    public void onInitialize() {
        ModEntities.registrar();
        Diretor.iniciar();
        CommandRegistrationCallback.EVENT.register((dispatcher, registro, ambiente) -> Comandos.registrar(dispatcher));
        LOG.info("Ecos do Vazio carregado. Não olhe para trás.");
    }
}
