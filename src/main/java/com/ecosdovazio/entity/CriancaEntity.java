package com.ecosdovazio.entity;

import com.ecosdovazio.horror.Sanidade;
import com.ecosdovazio.horror.Sons;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

/**
 * A Criança: pequena, parada de costas para você, no escuro. Quando você desvia o olhar,
 * ela gira bem devagar na sua direção. Se você a flagra virada para você, ela some rindo.
 */
public class CriancaEntity extends HorrorMob {
    public CriancaEntity(EntityType<? extends PathfinderMob> tipo, Level nivel) {
        super(tipo, nivel);
    }

    @Override
    protected int vidaMaxima() {
        return 20 * 90;
    }

    @Override
    protected void comportamento(ServerLevel nivel, ServerPlayer alvo) {
        parar();
        float paraVoce = anguloPara(alvo);
        if (idade == 1) definirRotacao(paraVoce + 180.0f);

        boolean vista = observadoPor(alvo, 0.6);
        float erro = Math.abs(Mth.wrapDegrees(paraVoce - this.getYRot()));

        if (!vista) {
            girarSuave(paraVoce, 1.4f);
            if (idade % 180 == 0) somPrivado(alvo, Sons.FANTASMA, 0.35f, 0.5f);
        } else if (erro < 25.0f) {
            somPrivado(alvo, Sons.RISADA, 0.9f, 1.7f);
            Sanidade.reduzir(alvo, 4);
            this.discard();
            return;
        }

        if (this.distanceTo(alvo) < 3.0) {
            somPrivado(alvo, Sons.RISADA, 0.9f, 1.4f);
            this.discard();
        }
    }
}
