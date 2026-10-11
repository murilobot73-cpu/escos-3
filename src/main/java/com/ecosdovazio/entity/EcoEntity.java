package com.ecosdovazio.entity;

import com.ecosdovazio.horror.Efeitos;
import com.ecosdovazio.horror.Sanidade;
import com.ecosdovazio.horror.Sons;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

import java.util.ArrayDeque;

/**
 * O Eco: refaz, passo a passo, o caminho que VOCÊ fez 8 segundos atrás.
 * Fica invisível até a trilha encher. Se você o encara, ele vira a cabeça e te encara de volta.
 * Se te alcançar (você ficou parado), se funde em você.
 */
public class EcoEntity extends HorrorMob {
    private static final int ATRASO = 160;
    private final ArrayDeque<double[]> trilha = new ArrayDeque<>();
    private int ticksVisto = 0;

    public EcoEntity(EntityType<? extends PathfinderMob> tipo, Level nivel) {
        super(tipo, nivel);
        this.setNoGravity(true);
        this.setSilent(true);
        this.setInvisible(true);
    }

    @Override
    protected int vidaMaxima() {
        return 20 * 60;
    }

    @Override
    protected void comportamento(ServerLevel nivel, ServerPlayer alvo) {
        trilha.addLast(new double[]{alvo.getX(), alvo.getY(), alvo.getZ(), alvo.getYRot(), alvo.getXRot()});
        if (trilha.size() <= ATRASO) {
            return; // ainda invisível, parado onde você estava
        }
        this.setInvisible(false);
        double[] a = trilha.pollFirst();
        this.setPos(a[0], a[1], a[2]);
        this.setDeltaMovement(0, 0, 0);
        this.setXRot((float) a[4]);
        definirRotacao((float) a[3]);

        double d = this.distanceTo(alvo);

        if (d > 3.0 && observadoPor(alvo, 0.93)) {
            encarar(alvo); // ele para de copiar você e olha de volta
            ticksVisto++;
            if (ticksVisto == 1) somPrivado(alvo, Sons.CORACAO, 0.8f, 0.9f);
            if (ticksVisto > 100) {
                somPrivado(alvo, Sons.CAVERNA, 1.0f, 0.6f);
                Sanidade.reduzir(alvo, 4);
                this.discard();
                return;
            }
        } else {
            ticksVisto = Math.max(0, ticksVisto - 1);
        }

        if (d < 1.6) {
            Efeitos.escuridao(alvo, 80);
            somPrivado(alvo, Sons.CORACAO, 1.2f, 0.7f);
            somPrivado(alvo, Sons.SUSSURRO, 0.8f, 0.6f);
            Sanidade.reduzir(alvo, 6);
            Efeitos.barra(alvo, Component.literal("Você já esteve aqui.").withStyle(ChatFormatting.DARK_GRAY));
            this.discard();
        }
    }
}
