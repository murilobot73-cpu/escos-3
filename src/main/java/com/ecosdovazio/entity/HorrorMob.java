package com.ecosdovazio.entity;

import com.ecosdovazio.horror.Sons;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Base de todas as criaturas do mod. Elas são invulneráveis, não têm IA vanilla
 * e não causam dano: o medo vem do comportamento (olhar, seguir, sumir).
 */
public abstract class HorrorMob extends PathfinderMob {
    protected int idade = 0;

    protected HorrorMob(EntityType<? extends PathfinderMob> tipo, Level nivel) {
        super(tipo, nivel);
        this.setInvulnerable(true);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder atributos(double velocidade) {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, velocidade)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }

    @Override
    protected void registerGoals() {
        // Sem goals vanilla: todo o comportamento é programado em comportamento().
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distancia) {
        return false;
    }

    protected abstract int vidaMaxima();

    protected abstract void comportamento(ServerLevel nivel, ServerPlayer alvo);

    @Override
    public void tick() {
        super.tick();
        if (!(this.level() instanceof ServerLevel nivel)) return;
        idade++;
        Player p = nivel.getNearestPlayer(this, 96.0);
        if (!(p instanceof ServerPlayer alvo) || p.isSpectator()) {
            if (idade > 100) this.discard();
            return;
        }
        if (idade > vidaMaxima()) {
            this.discard();
            return;
        }
        comportamento(nivel, alvo);
    }

    /** O jogador está olhando para esta criatura? limiar ~0.985 = olhando direto; ~0.5 = vê pelo canto do olho. */
    protected boolean observadoPor(ServerPlayer p, double limiar) {
        Vec3 olhar = p.getLookAngle();
        Vec3 para = this.getEyePosition().subtract(p.getEyePosition());
        double dist = para.length();
        if (dist < 0.01) return true;
        double dot = olhar.dot(para.scale(1.0 / dist));
        return dot > limiar && p.hasLineOfSight(this);
    }

    protected float anguloPara(Entity alvo) {
        double dx = alvo.getX() - this.getX();
        double dz = alvo.getZ() - this.getZ();
        return (float) (Mth.atan2(dz, dx) * 57.29578) - 90.0f;
    }

    protected void definirRotacao(float yaw) {
        this.setYRot(yaw);
        this.setYHeadRot(yaw);
        this.setYBodyRot(yaw);
    }

    protected void encarar(Entity alvo) {
        definirRotacao(anguloPara(alvo));
    }

    protected void girarSuave(float yawAlvo, float passo) {
        float atual = this.getYRot();
        float diff = Mth.wrapDegrees(yawAlvo - atual);
        definirRotacao(atual + Mth.clamp(diff, -passo, passo));
    }

    protected void parar() {
        this.getNavigation().stop();
        Vec3 v = this.getDeltaMovement();
        this.setDeltaMovement(0.0, v.y, 0.0);
    }

    protected void somPrivado(ServerPlayer p, SoundEvent som, float volume, float pitch) {
        Sons.privado(p, som, this.position(), volume, pitch);
    }
}
