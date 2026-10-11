package com.ecosdovazio.horror;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public final class Efeitos {
    private Efeitos() {}

    public static void escuridao(ServerPlayer p, int ticks) {
        p.addEffect(new MobEffectInstance(MobEffects.DARKNESS, ticks, 0, false, false, true));
    }

    public static void cegueira(ServerPlayer p, int ticks) {
        p.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, ticks, 0, false, false, true));
    }

    public static void lentidao(ServerPlayer p, int ticks) {
        p.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, ticks, 1, false, false, true));
    }

    public static void nausea(ServerPlayer p, int ticks) {
        p.addEffect(new MobEffectInstance(MobEffects.NAUSEA, ticks, 0, false, false, true));
    }

    public static void barra(ServerPlayer p, Component texto) {
        if (p.isRemoved()) return;
        p.displayClientMessage(texto, true);
    }

    public static void titulo(ServerPlayer p, Component titulo, Component subtitulo) {
        if (p.isRemoved() || p.connection == null) return;
        p.connection.send(new ClientboundSetTitlesAnimationPacket(5, 50, 40));
        p.connection.send(new ClientboundSetSubtitleTextPacket(subtitulo));
        p.connection.send(new ClientboundSetTitleTextPacket(titulo));
    }

    /** Mensagem no chat que parece vir do próprio jogador. */
    public static void falaFalsa(ServerPlayer p, String frase) {
        if (p.isRemoved()) return;
        String nome = p.getName().getString();
        p.sendSystemMessage(Component.literal("<" + nome + "> " + frase));
    }

    public static void sistema(ServerPlayer p, Component c) {
        if (p.isRemoved()) return;
        p.sendSystemMessage(c.copy().withStyle(ChatFormatting.YELLOW));
    }
}
