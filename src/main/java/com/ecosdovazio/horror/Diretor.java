package com.ecosdovazio.horror;

import com.ecosdovazio.Config;
import com.ecosdovazio.EcosDoVazio;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * O "Diretor de Horror": a cada segundo atualiza a sanidade de cada jogador e,
 * quanto mais baixa ela estiver, mais provável (e mais pesado) é o próximo evento.
 */
public final class Diretor {
    private Diretor() {}

    private static long tickGlobal = 0;
    private static final Map<UUID, Long> GRACA = new HashMap<>();
    private static final Map<UUID, Long> PROXIMO = new HashMap<>();
    private static final Map<UUID, Map<String, Long>> RECARGAS = new HashMap<>();
    private static final Map<UUID, Map<String, Integer>> CONTAGENS = new HashMap<>();

    public static long agora() {
        return tickGlobal;
    }

    public static void iniciar() {
        ServerTickEvents.END_SERVER_TICK.register(Diretor::aoTick);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                GRACA.put(handler.getPlayer().getUUID(), tickGlobal + Config.GRACA_TICKS));
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            tickGlobal = 0;
            GRACA.clear();
            PROXIMO.clear();
            RECARGAS.clear();
            CONTAGENS.clear();
            Agenda.limpar();
            Sanidade.limpar();
        });
    }

    public static int contar(ServerPlayer p, String id) {
        return CONTAGENS.getOrDefault(p.getUUID(), Map.of()).getOrDefault(id, 0);
    }

    public static void registrar(ServerPlayer p, String id) {
        CONTAGENS.computeIfAbsent(p.getUUID(), k -> new HashMap<>()).merge(id, 1, Integer::sum);
    }

    public static List<String> nomes() {
        List<String> l = new ArrayList<>();
        for (Eventos.Evento e : Eventos.TODOS) l.add(e.id());
        return l;
    }

    /** Dispara um evento específico ignorando sanidade e recarga (usado pelo comando). */
    public static boolean forcar(ServerPlayer p, String id) {
        for (Eventos.Evento e : Eventos.TODOS) {
            if (e.id().equals(id)) return e.acao().rodar(p, Mundo.nivel(p), p.getRandom());
        }
        return false;
    }

    private static void aoTick(MinecraftServer server) {
        tickGlobal++;
        Agenda.processar(tickGlobal);
        if (tickGlobal % 20 != 0) return;
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            if (p.isSpectator() || p.isCreative()) continue;
            atualizarSanidade(p);
            considerarEvento(p);
        }
    }

    private static void atualizarSanidade(ServerPlayer p) {
        ServerLevel n = Mundo.nivel(p);
        BlockPos pos = p.blockPosition();
        int luz = n.getMaxLocalRawBrightness(pos);
        boolean ceu = n.canSeeSky(pos);
        double d = 0.0;

        if (luz <= 4) d -= 0.35 * Config.VELOCIDADE_DRENAGEM;
        else if (luz >= 10) d += 0.15;
        if (ceu && luz >= 12) d += 0.10;
        if (p.getY() < 40 && !ceu) d -= 0.12 * Config.VELOCIDADE_DRENAGEM;
        if (n.dimension() == Level.NETHER) d -= 0.15;
        if (n.dimension() == Level.END) d -= 0.25;
        if (p.isSleeping()) d += 1.0;

        for (ServerPlayer outro : n.players()) {
            if (outro != p && outro.distanceToSqr(p) < 144.0) {
                d += 0.3; // companhia acalma
                break;
            }
        }
        if (d != 0.0) Sanidade.aumentar(p, d);
    }

    private static void considerarEvento(ServerPlayer p) {
        UUID id = p.getUUID();
        if (Config.INTENSIDADE <= 0.0) return;
        if (tickGlobal < GRACA.getOrDefault(id, 0L)) return;
        if (tickGlobal < PROXIMO.getOrDefault(id, 0L)) return;

        RandomSource r = p.getRandom();
        double sanidade = Sanidade.obter(p);
        double falta = 1.0 - sanidade / 100.0;
        double chance = falta * falta * Config.CHANCE_BASE * Config.INTENSIDADE;
        if (r.nextDouble() > chance) return;

        Map<String, Long> recargas = RECARGAS.computeIfAbsent(id, k -> new HashMap<>());
        List<Eventos.Evento> candidatos = new ArrayList<>();
        for (Eventos.Evento e : Eventos.TODOS) {
            if (sanidade <= e.sanidadeMax() && tickGlobal >= recargas.getOrDefault(e.id(), 0L)) {
                candidatos.add(e);
            }
        }

        for (int tentativa = 0; tentativa < 3 && !candidatos.isEmpty(); tentativa++) {
            Eventos.Evento escolhido = sortear(candidatos, r);
            candidatos.remove(escolhido);
            boolean ok;
            try {
                ok = escolhido.acao().rodar(p, Mundo.nivel(p), r);
            } catch (Exception ex) {
                EcosDoVazio.LOG.warn("Evento {} falhou", escolhido.id(), ex);
                ok = false;
            }
            if (ok) {
                recargas.put(escolhido.id(), tickGlobal + escolhido.recargaSeg() * 20L);
                PROXIMO.put(id, tickGlobal + Config.INTERVALO_MIN + r.nextInt(Config.INTERVALO_VAR));
                return;
            }
        }
    }

    private static Eventos.Evento sortear(List<Eventos.Evento> lista, RandomSource r) {
        int total = 0;
        for (Eventos.Evento e : lista) total += e.peso();
        int sorteio = r.nextInt(total);
        for (Eventos.Evento e : lista) {
            sorteio -= e.peso();
            if (sorteio < 0) return e;
        }
        return lista.get(0);
    }
}
