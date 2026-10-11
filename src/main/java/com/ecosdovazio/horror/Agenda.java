package com.ecosdovazio.horror;

import com.ecosdovazio.EcosDoVazio;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Mini-agendador: "faça isso daqui a N ticks". Usado para sequências (passos, batidas, sino...). */
public final class Agenda {
    private Agenda() {}

    private record Tarefa(long quando, Runnable acao) {}

    private static final List<Tarefa> TAREFAS = new ArrayList<>();

    public static void depois(long ticks, Runnable acao) {
        TAREFAS.add(new Tarefa(Diretor.agora() + Math.max(0, ticks), acao));
    }

    static void processar(long agora) {
        if (TAREFAS.isEmpty()) return;
        List<Tarefa> prontas = new ArrayList<>();
        Iterator<Tarefa> it = TAREFAS.iterator();
        while (it.hasNext()) {
            Tarefa t = it.next();
            if (t.quando() <= agora) {
                prontas.add(t);
                it.remove();
            }
        }
        for (Tarefa t : prontas) {
            try {
                t.acao().run();
            } catch (Exception e) {
                EcosDoVazio.LOG.warn("Tarefa agendada falhou", e);
            }
        }
    }

    static void limpar() {
        TAREFAS.clear();
    }
}
