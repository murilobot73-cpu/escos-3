package com.ecosdovazio.horror;

import com.ecosdovazio.Config;
import com.ecosdovazio.entity.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/** Todos os eventos de terror psicológico. Cada um devolve true se realmente aconteceu. */
public final class Eventos {
    private Eventos() {}

    @FunctionalInterface
    public interface Acao {
        boolean rodar(ServerPlayer p, ServerLevel n, RandomSource r);
    }

    /** sanidadeMax: só pode ocorrer com sanidade <= isso. peso: chance relativa. */
    public record Evento(String id, int sanidadeMax, int peso, int recargaSeg, Acao acao) {}

    public static final List<Evento> TODOS = List.of(
            new Evento("passos", 92, 10, 45, Eventos::passos),
            new Evento("sons", 90, 12, 30, Eventos::sons),
            new Evento("batidas", 82, 8, 60, Eventos::batidas),
            new Evento("porta", 80, 8, 90, Eventos::porta),
            new Evento("chat", 75, 9, 80, Eventos::chat),
            new Evento("sino", 70, 5, 180, Eventos::sino),
            new Evento("tochas", 68, 7, 120, Eventos::tochas),
            new Evento("observador", 62, 8, 150, Eventos::observador),
            new Evento("crianca", 58, 6, 240, Eventos::crianca),
            new Evento("apagao", 50, 6, 150, Eventos::apagao),
            new Evento("eco", 46, 5, 300, Eventos::eco),
            new Evento("sombra", 40, 6, 240, Eventos::sombra),
            new Evento("saiu", 32, 4, 420, Eventos::saiu),
            new Evento("visita", 12, 3, 600, Eventos::visita)
    );

    private static final String[] FRASES = {
            "não olhe para trás.",
            "você ouviu isso?",
            "ele está na janela.",
            "por que você parou?",
            "tem alguém aí com você.",
            "eu estou vendo você.",
            "você não devia ter vindo aqui.",
            "não durma.",
            "ele sabe o seu nome.",
            "ainda está aí?",
            "acorda.",
            "você já esteve aqui antes.",
            "pare de olhar para ele.",
            "a luz está acabando."
    };

    // ------------------------------------------------------------------ sons e pequenos sustos

    /** Passos atrás de você, cada vez mais perto, no tipo de chão em que você pisa. */
    static boolean passos(ServerPlayer p, ServerLevel n, RandomSource r) {
        SoundEvent passo = n.getBlockState(p.blockPosition().below()).getSoundType().getStepSound();
        if (passo == null) passo = Sons.PASSO_PEDRA;
        final SoundEvent som = passo;
        Vec3 atras = Mundo.frente(p).scale(-1.0);
        int quantos = 6 + r.nextInt(5);
        for (int i = 0; i < quantos; i++) {
            final int k = i;
            Agenda.depois(i * 9L + r.nextInt(3), () -> {
                double dist = Math.max(2.0, 9.0 - k * 0.9);
                Sons.privado(p, som, p.position().add(atras.scale(dist)), 1.0f, 0.85f);
            });
        }
        Sanidade.reduzir(p, 1);
        return true;
    }

    /** Barulhos de longe, de direções aleatórias. */
    static boolean sons(ServerPlayer p, ServerLevel n, RandomSource r) {
        SoundEvent[] lista = {Sons.CAVERNA, Sons.FANTASMA, Sons.CREEPER, Sons.VIDRO, Sons.ESCUTANDO, Sons.RESPIRO, Sons.ENCARADA};
        SoundEvent s = lista[r.nextInt(lista.length)];
        double ang = r.nextDouble() * Math.PI * 2.0;
        double d = 6.0 + r.nextDouble() * 10.0;
        Vec3 pos = p.position().add(Math.cos(ang) * d, 0.0, Math.sin(ang) * d);
        Sons.privado(p, s, pos, 0.8f, 0.6f + r.nextFloat() * 0.5f);
        Sanidade.reduzir(p, 0.5);
        return true;
    }

    /** Batidas de algo que quer entrar, vindas de trás de você. */
    static boolean batidas(ServerPlayer p, ServerLevel n, RandomSource r) {
        Vec3 pos = p.position().add(Mundo.frente(p).scale(-3.5)).add(0.0, 1.0, 0.0);
        int quantas = 3 + r.nextInt(3);
        for (int i = 0; i < quantas; i++) {
            Agenda.depois(i * 11L, () -> Sons.privado(p, Sons.BATIDA, pos, 1.2f, 0.7f));
        }
        Sanidade.reduzir(p, 1.5);
        return true;
    }

    /** Sino distante tocando três vezes. */
    static boolean sino(ServerPlayer p, ServerLevel n, RandomSource r) {
        double ang = r.nextDouble() * Math.PI * 2.0;
        Vec3 pos = p.position().add(Math.cos(ang) * 40.0, 0.0, Math.sin(ang) * 40.0);
        for (int i = 0; i < 3; i++) {
            Agenda.depois(i * 50L, () -> Sons.privado(p, Sons.SINO, pos, 3.0f, 0.5f));
        }
        Sanidade.reduzir(p, 2);
        return true;
    }

    // ------------------------------------------------------------------ mexendo no mundo

    /** Uma porta de madeira por perto se abre sozinha. */
    static boolean porta(ServerPlayer p, ServerLevel n, RandomSource r) {
        if (!Config.MEXER_EM_PORTAS) return false;
        BlockPos base = p.blockPosition();
        List<BlockPos> portas = new ArrayList<>();
        for (BlockPos pos : BlockPos.betweenClosed(base.offset(-14, -4, -14), base.offset(14, 4, 14))) {
            BlockState s = n.getBlockState(pos);
            if (s.is(BlockTags.WOODEN_DOORS)
                    && s.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER
                    && !s.getValue(BlockStateProperties.OPEN)) {
                portas.add(pos.immutable());
            }
        }
        if (portas.isEmpty()) return false;
        BlockPos alvo = portas.get(r.nextInt(portas.size()));
        BlockState s = n.getBlockState(alvo);
        if (s.getBlock() instanceof DoorBlock porta) {
            porta.setOpen(null, n, s, alvo, true);
        }
        Sanidade.reduzir(p, 2);
        return true;
    }

    /** Tochas perto de você se apagam (somem) uma a uma. */
    static boolean tochas(ServerPlayer p, ServerLevel n, RandomSource r) {
        if (!Config.APAGAR_TOCHAS) return false;
        BlockPos base = p.blockPosition();
        List<BlockPos> tochas = new ArrayList<>();
        for (BlockPos pos : BlockPos.betweenClosed(base.offset(-10, -5, -10), base.offset(10, 5, 10))) {
            Block b = n.getBlockState(pos).getBlock();
            if (b == Blocks.TORCH || b == Blocks.WALL_TORCH || b == Blocks.SOUL_TORCH || b == Blocks.SOUL_WALL_TORCH) {
                tochas.add(pos.immutable());
            }
        }
        if (tochas.isEmpty()) return false;
        int quantas = Math.min(tochas.size(), 1 + r.nextInt(3));
        for (int i = 0; i < quantas; i++) {
            BlockPos pos = tochas.remove(r.nextInt(tochas.size()));
            Agenda.depois(i * 25L, () -> {
                n.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                Sons.privado(p, Sons.FIZZ, Vec3.atCenterOf(pos), 0.6f, 0.8f);
            });
        }
        Sanidade.reduzir(p, 3);
        return true;
    }

    /** A visão escurece em pulsos, com o coração batendo. */
    static boolean apagao(ServerPlayer p, ServerLevel n, RandomSource r) {
        Efeitos.escuridao(p, 220);
        for (int i = 0; i < 4; i++) {
            Agenda.depois(i * 30L, () -> Sons.privado(p, Sons.CORACAO, 1.1f, 0.9f));
        }
        Efeitos.barra(p, Component.literal("...").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        Sanidade.reduzir(p, 3);
        return true;
    }

    // ------------------------------------------------------------------ jogo mental

    /** Mensagens no chat com o SEU nome. */
    static boolean chat(ServerPlayer p, ServerLevel n, RandomSource r) {
        Efeitos.falaFalsa(p, FRASES[r.nextInt(FRASES.length)]);
        if (r.nextInt(3) == 0) {
            Agenda.depois(60L, () -> Efeitos.falaFalsa(p, FRASES[r.nextInt(FRASES.length)]));
        }
        Sanidade.reduzir(p, 2);
        return true;
    }

    /** O jogo diz que você saiu, depois que entrou, e então "você" diz que não era você. */
    static boolean saiu(ServerPlayer p, ServerLevel n, RandomSource r) {
        Efeitos.sistema(p, Component.translatable("multiplayer.player.left", p.getDisplayName()));
        Agenda.depois(20L * 7, () -> Efeitos.sistema(p, Component.translatable("multiplayer.player.joined", p.getDisplayName())));
        Agenda.depois(20L * 10, () -> Efeitos.falaFalsa(p, "não era eu."));
        Sanidade.reduzir(p, 5);
        return true;
    }

    // ------------------------------------------------------------------ aparições

    /** O Observador surge ao longe, no seu campo de visão. Cada vez mais perto. */
    static boolean observador(ServerPlayer p, ServerLevel n, RandomSource r) {
        int vez = Diretor.contar(p, "observador");
        double dist = Math.max(13.0, 46.0 - vez * 6.0);
        for (int i = 0; i < 14; i++) {
            Vec3 dir = Mundo.girar(Mundo.frente(p), (r.nextDouble() - 0.5) * 2.2);
            Vec3 pos = Mundo.acharChao(n, p.getX() + dir.x * dist, p.getZ() + dir.z * dist, p.getBlockY());
            if (pos == null) continue;
            ObservadorEntity e = new ObservadorEntity(ModEntities.OBSERVADOR, n);
            e.setPos(pos.x, pos.y, pos.z);
            if (!p.hasLineOfSight(e)) continue;
            n.addFreshEntity(e);
            Diretor.registrar(p, "observador");
            return true;
        }
        return false;
    }

    /** A Criança de costas, no escuro. */
    static boolean crianca(ServerPlayer p, ServerLevel n, RandomSource r) {
        for (int i = 0; i < 20; i++) {
            Vec3 dir = Mundo.girar(Mundo.frente(p), (r.nextDouble() - 0.5) * 1.8);
            double dist = 8.0 + r.nextDouble() * 6.0;
            Vec3 pos = Mundo.acharChao(n, p.getX() + dir.x * dist, p.getZ() + dir.z * dist, p.getBlockY());
            if (pos == null) continue;
            if (n.getMaxLocalRawBrightness(BlockPos.containing(pos)) > 7) continue;
            CriancaEntity e = new CriancaEntity(ModEntities.CRIANCA, n);
            e.setPos(pos.x, pos.y, pos.z);
            n.addFreshEntity(e);
            Diretor.registrar(p, "crianca");
            return true;
        }
        return false;
    }

    /** A Sombra nasce atrás de você e vem andando quando você não olha. */
    static boolean sombra(ServerPlayer p, ServerLevel n, RandomSource r) {
        for (int i = 0; i < 14; i++) {
            Vec3 dir = Mundo.girar(Mundo.frente(p).scale(-1.0), (r.nextDouble() - 0.5) * 1.2);
            double dist = 22.0 + r.nextDouble() * 8.0;
            Vec3 pos = Mundo.acharChao(n, p.getX() + dir.x * dist, p.getZ() + dir.z * dist, p.getBlockY());
            if (pos == null) continue;
            SombraEntity e = new SombraEntity(ModEntities.SOMBRA, n);
            e.setPos(pos.x, pos.y, pos.z);
            n.addFreshEntity(e);
            Diretor.registrar(p, "sombra");
            Sons.privado(p, Sons.CORACAO, 0.5f, 0.9f);
            return true;
        }
        return false;
    }

    /** O Eco começa a refazer o seu caminho de 8 segundos atrás. */
    static boolean eco(ServerPlayer p, ServerLevel n, RandomSource r) {
        EcoEntity e = new EcoEntity(ModEntities.ECO, n);
        e.setPos(p.getX(), p.getY(), p.getZ());
        n.addFreshEntity(e);
        Diretor.registrar(p, "eco");
        return true;
    }

    /** O final: O Hospedeiro vem andando, e não importa se você olha. */
    static boolean visita(ServerPlayer p, ServerLevel n, RandomSource r) {
        for (int i = 0; i < 16; i++) {
            Vec3 dir = Mundo.girar(Mundo.frente(p), (r.nextDouble() - 0.5) * 0.9);
            Vec3 pos = Mundo.acharChao(n, p.getX() + dir.x * 30.0, p.getZ() + dir.z * 30.0, p.getBlockY());
            if (pos == null) continue;
            HospedeiroEntity e = new HospedeiroEntity(ModEntities.HOSPEDEIRO, n);
            e.setPos(pos.x, pos.y, pos.z);
            n.addFreshEntity(e);
            Diretor.registrar(p, "visita");
            Efeitos.escuridao(p, 100);
            Sons.privado(p, Sons.GRITO_SCULK, 0.6f, 0.5f);
            Efeitos.barra(p, Component.literal("Ele está aqui.").withStyle(ChatFormatting.DARK_RED));
            return true;
        }
        return false;
    }
}
