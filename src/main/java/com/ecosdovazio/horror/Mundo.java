package com.ecosdovazio.horror;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class Mundo {
    private Mundo() {}

    public static ServerLevel nivel(ServerPlayer p) {
        return (ServerLevel) p.level();
    }

    /** Vetor horizontal unitário para onde o jogador está virado. */
    public static Vec3 frente(ServerPlayer p) {
        double yaw = Math.toRadians(p.getYRot());
        return new Vec3(-Math.sin(yaw), 0.0, Math.cos(yaw));
    }

    /** Gira um vetor horizontal ao redor do eixo Y (radianos). */
    public static Vec3 girar(Vec3 v, double rad) {
        double c = Math.cos(rad), s = Math.sin(rad);
        return new Vec3(v.x * c - v.z * s, 0.0, v.x * s + v.z * c);
    }

    /**
     * Procura um chão livre (2 blocos de ar em cima, bloco sólido embaixo) perto da altura
     * de referência. Retorna null se não achar ou se o chunk não estiver carregado.
     */
    public static Vec3 acharChao(ServerLevel n, double x, double z, int yRef) {
        for (int y = yRef + 4; y >= yRef - 8; y--) {
            BlockPos pos = new BlockPos((int) Math.floor(x), y, (int) Math.floor(z));
            if (!n.hasChunkAt(pos)) return null;
            BlockPos abaixo = pos.below();
            BlockState sPe = n.getBlockState(pos);
            BlockState sCabeca = n.getBlockState(pos.above());
            BlockState sChao = n.getBlockState(abaixo);
            boolean livre = sPe.getCollisionShape(n, pos).isEmpty() && sPe.getFluidState().isEmpty()
                    && sCabeca.getCollisionShape(n, pos.above()).isEmpty() && sCabeca.getFluidState().isEmpty();
            boolean firme = !sChao.getCollisionShape(n, abaixo).isEmpty() && !sChao.is(BlockTags.LEAVES)
                    && sChao.getFluidState().isEmpty();
            if (livre && firme) return new Vec3(Math.floor(x) + 0.5, y, Math.floor(z) + 0.5);
        }
        return null;
    }
}
