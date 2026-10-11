package com.ecosdovazio.horror;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Sanidade de 0 a 100 por jogador. Fica na memória e é gravada como "tag" do jogador
 * (ecos_sanidade_NN), então sobrevive a fechar e abrir o mundo.
 */
public final class Sanidade {
    private Sanidade() {}

    private static final String PREFIXO = "ecos_sanidade_";
    private static final Map<UUID, Double> VALORES = new HashMap<>();
    private static final Map<UUID, Integer> GRAVADOS = new HashMap<>();

    public static double obter(ServerPlayer p) {
        return VALORES.computeIfAbsent(p.getUUID(), id -> lerTag(p));
    }

    public static void definir(ServerPlayer p, double valor) {
        double v = Mth.clamp(valor, 0.0, 100.0);
        VALORES.put(p.getUUID(), v);
        int inteiro = (int) Math.round(v);
        Integer gravado = GRAVADOS.get(p.getUUID());
        if (gravado == null || gravado != inteiro) {
            for (String t : new ArrayList<>(p.getTags())) {
                if (t.startsWith(PREFIXO)) p.removeTag(t);
            }
            p.addTag(PREFIXO + inteiro);
            GRAVADOS.put(p.getUUID(), inteiro);
        }
    }

    public static void reduzir(ServerPlayer p, double quanto) {
        definir(p, obter(p) - quanto);
    }

    public static void aumentar(ServerPlayer p, double quanto) {
        definir(p, obter(p) + quanto);
    }

    private static double lerTag(ServerPlayer p) {
        List<String> tags = new ArrayList<>(p.getTags());
        for (String t : tags) {
            if (t.startsWith(PREFIXO)) {
                try {
                    return Integer.parseInt(t.substring(PREFIXO.length()));
                } catch (NumberFormatException ignorado) {
                    // tag corrompida: ignora
                }
            }
        }
        return 100.0;
    }

    static void limpar() {
        VALORES.clear();
        GRAVADOS.clear();
    }
}
