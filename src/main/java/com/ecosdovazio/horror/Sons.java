package com.ecosdovazio.horror;

import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

/**
 * Sons vanilla reaproveitados, tocados SÓ para o jogador-alvo (via pacote).
 * Em multiplayer, os outros não ouvem nada: só você sabe que algo está errado.
 */
public final class Sons {
    private Sons() {}

    public static SoundEvent som(String id) {
        return SoundEvent.createVariableRangeEvent(Identifier.withDefaultNamespace(id));
    }

    public static final SoundEvent CAVERNA = som("ambient.cave");
    public static final SoundEvent CORACAO = som("entity.warden.heartbeat");
    public static final SoundEvent SUSSURRO = som("entity.warden.nearby_close");
    public static final SoundEvent FANTASMA = som("entity.phantom.ambient");
    public static final SoundEvent RISADA = som("entity.vex.ambient");
    public static final SoundEvent GRITO = som("entity.enderman.scream");
    public static final SoundEvent ENCARADA = som("entity.enderman.stare");
    public static final SoundEvent CREEPER = som("entity.creeper.primed");
    public static final SoundEvent VIDRO = som("block.glass.break");
    public static final SoundEvent BATIDA = som("entity.zombie.attack_wooden_door");
    public static final SoundEvent SINO = som("block.bell.use");
    public static final SoundEvent ESCUTANDO = som("entity.warden.listening");
    public static final SoundEvent FIZZ = som("block.fire.extinguish");
    public static final SoundEvent PASSO_PEDRA = som("block.stone.step");
    public static final SoundEvent RESPIRO = som("entity.player.breath");
    public static final SoundEvent GRITO_SCULK = som("block.sculk_shrieker.shriek");

    public static void privado(ServerPlayer p, SoundEvent som, Vec3 pos, float volume, float pitch) {
        if (p == null || p.isRemoved() || p.connection == null) return;
        p.connection.send(new ClientboundSoundPacket(Holder.direct(som), SoundSource.AMBIENT,
                pos.x, pos.y, pos.z, volume, pitch, p.getRandom().nextLong()));
    }

    /** Dentro da cabeça do jogador (sem direção). */
    public static void privado(ServerPlayer p, SoundEvent som, float volume, float pitch) {
        privado(p, som, p.position(), volume, pitch);
    }
}
