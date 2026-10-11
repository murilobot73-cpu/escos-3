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

/**
 * O Hospedeiro: o final do medo. Enorme, lento, e NÃO para quando você olha.
 * Cada passo dele faz seu coração bater mais alto. Quando te alcança, tudo apaga
 * e você "acorda" com a sanidade parcialmente restaurada.
 */
public class HospedeiroEntity extends HorrorMob {
    public HospedeiroEntity(EntityType<? extends PathfinderMob> tipo, Level nivel) {
        super(tipo, nivel);
    }

    @Override
    protected int vidaMaxima() {
        return 20 * 150;
    }

    @Override
    protected void comportamento(ServerLevel nivel, ServerPlayer alvo) {
        double d = this.distanceTo(alvo);

        if (idade % 10 == 0 || this.getNavigation().isDone()) {
            this.getNavigation().moveTo(alvo, 1.0);
        }

        if (idade % 20 == 0) {
            float vol = (float) Math.max(0.3, 1.6 - d / 25.0);
            float pitch = (float) Math.max(0.6, 1.1 - d / 60.0);
            somPrivado(alvo, Sons.CORACAO, vol, pitch);
        }
        if (d < 22 && idade % 100 == 0) Efeitos.escuridao(alvo, 120);
        if (d < 14 && idade % 160 == 0) somPrivado(alvo, Sons.SUSSURRO, 1.0f, 0.5f);

        if (d < 2.8) {
            Efeitos.cegueira(alvo, 100);
            Efeitos.escuridao(alvo, 400);
            Efeitos.nausea(alvo, 200);
            Efeitos.lentidao(alvo, 100);
            somPrivado(alvo, Sons.GRITO, 1.0f, 0.4f);
            Efeitos.titulo(alvo,
                    Component.literal("ACORDE").withStyle(ChatFormatting.DARK_RED),
                    Component.literal("isso nunca foi um sonho").withStyle(ChatFormatting.DARK_GRAY));
            Sanidade.definir(alvo, 45);
            this.discard();
        }
    }
}
