package com.ecosdovazio.entity;

import com.ecosdovazio.horror.Efeitos;
import com.ecosdovazio.horror.Sanidade;
import com.ecosdovazio.horror.Sons;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

/**
 * A Sombra: só anda quando você NÃO está vendo. Se você olha, congela, virada para você,
 * sorrindo. Se encostar em você, não machuca: apaga sua visão e arranca sua sanidade.
 */
public class SombraEntity extends HorrorMob {
    private int ticksVista = 0;
    private int ticksTravada = 0;

    public SombraEntity(EntityType<? extends PathfinderMob> tipo, Level nivel) {
        super(tipo, nivel);
    }

    @Override
    protected int vidaMaxima() {
        return 20 * 100;
    }

    @Override
    protected void comportamento(ServerLevel nivel, ServerPlayer alvo) {
        boolean vista = observadoPor(alvo, 0.5);
        double d = this.distanceTo(alvo);

        if (vista) {
            parar();
            encarar(alvo);
            ticksVista++;
            if (ticksVista == 1) somPrivado(alvo, Sons.CORACAO, 0.6f, 1.0f);
            if (ticksVista > 160) { // você a encarou demais: ela recua para o escuro
                somPrivado(alvo, Sons.FANTASMA, 0.8f, 0.6f);
                this.discard();
            }
            return;
        }

        if (d < 2.2) {
            Efeitos.escuridao(alvo, 160);
            Efeitos.lentidao(alvo, 80);
            somPrivado(alvo, Sons.CORACAO, 1.4f, 0.8f);
            somPrivado(alvo, Sons.SUSSURRO, 1.0f, 0.5f);
            Sanidade.reduzir(alvo, 12);
            Efeitos.barra(alvo, Component.literal("Algo roçou em você.").withStyle(ChatFormatting.DARK_GRAY));
            this.discard();
            return;
        }

        if (d > 70) {
            this.discard();
            return;
        }

        if (idade % 10 == 0 || this.getNavigation().isDone()) {
            this.getNavigation().moveTo(alvo, 1.0);
        }
        ticksTravada = this.getNavigation().isDone() ? ticksTravada + 1 : 0;
        if (ticksTravada > 200) this.discard();
    }
}
