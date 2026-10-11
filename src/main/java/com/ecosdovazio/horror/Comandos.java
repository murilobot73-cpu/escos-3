package com.ecosdovazio.horror;

import com.ecosdovazio.entity.HorrorMob;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * /horror sanidade [0-100]   -> ver ou definir a SUA sanidade
 * /horror evento <nome>      -> dispara um evento em você (teste)
 * /horror limpar             -> remove as criaturas por perto
 * Os comandos só afetam quem os executa.
 */
public final class Comandos {
    private Comandos() {}

    public static void registrar(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("horror")
                .then(Commands.literal("sanidade")
                        .executes(c -> {
                            ServerPlayer p = c.getSource().getPlayerOrException();
                            long v = Math.round(Sanidade.obter(p));
                            c.getSource().sendSuccess(() -> Component.literal("Sanidade: " + v + "/100"), false);
                            return 1;
                        })
                        .then(Commands.argument("valor", IntegerArgumentType.integer(0, 100))
                                .executes(c -> {
                                    ServerPlayer p = c.getSource().getPlayerOrException();
                                    int v = IntegerArgumentType.getInteger(c, "valor");
                                    Sanidade.definir(p, v);
                                    c.getSource().sendSuccess(() -> Component.literal("Sanidade definida para " + v), false);
                                    return 1;
                                })))
                .then(Commands.literal("evento")
                        .then(Commands.argument("nome", StringArgumentType.word())
                                .suggests((c, b) -> SharedSuggestionProvider.suggest(Diretor.nomes(), b))
                                .executes(c -> {
                                    ServerPlayer p = c.getSource().getPlayerOrException();
                                    String nome = StringArgumentType.getString(c, "nome");
                                    if (!Diretor.nomes().contains(nome)) {
                                        c.getSource().sendFailure(Component.literal("Evento desconhecido. Opções: " + String.join(", ", Diretor.nomes())));
                                        return 0;
                                    }
                                    boolean ok = Diretor.forcar(p, nome);
                                    if (!ok) c.getSource().sendFailure(Component.literal("Não deu para disparar '" + nome + "' aqui (sem espaço/alvo)."));
                                    return ok ? 1 : 0;
                                })))
                .then(Commands.literal("limpar")
                        .executes(c -> {
                            ServerPlayer p = c.getSource().getPlayerOrException();
                            ServerLevel n = Mundo.nivel(p);
                            List<HorrorMob> lista = n.getEntitiesOfClass(HorrorMob.class, p.getBoundingBox().inflate(256.0));
                            lista.forEach(HorrorMob::discard);
                            int total = lista.size();
                            c.getSource().sendSuccess(() -> Component.literal("Removidas " + total + " criaturas."), false);
                            return total;
                        })));
    }
}
