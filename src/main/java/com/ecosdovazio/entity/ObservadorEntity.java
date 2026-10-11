package com.ecosdovazio.entity;

import com.ecosdovazio.horror.Efeitos;
import com.ecosdovazio.horror.Sanidade;
import com.ecosdovazio.horror.Sons;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

/**
 * O Observador: figura alta e imóvel, sempre longe, sempre virada para você.
 * Não faz nada. Some se você encará-lo por muito tempo ou chegar perto demais.
 * A cada aparição o Diretor o coloca um pouco mais perto.
 */
public class ObservadorEntity extends HorrorMob {
    private int olhadas = 0;

    public ObservadorEntity(EntityType<? extends PathfinderMob> tipo, Level nivel) {
        super(tipo, nivel);
    }

    @Override
    protected int vidaMaxima() {
        return 20 * 75;
    }

    @Override
    protected void comportamento(ServerLevel nivel, ServerPlayer alvo) {
        parar();
        encarar(alvo);
        double d = this.distanceTo(alvo);

        if (observadoPor(alvo, 0.985)) {
            olhadas++;
            if (olhadas == 1) somPrivado(alvo, Sons.CORACAO, 0.7f, 0.9f);
        } else {
            olhadas = Math.max(0, olhadas - 1);
        }

        if (olhadas > 45 || d < 8.0) {
            somPrivado(alvo, Sons.CAVERNA, 1.0f, 0.7f);
            Sanidade.reduzir(alvo, 3);
            if (nivel.getRandom().nextFloat() < 0.3f) Efeitos.escuridao(alvo, 80);
            this.discard();
        }
    }
}
