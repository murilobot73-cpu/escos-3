package com.ecosdovazio;

/** Ajustes rápidos. Mexa aqui para deixar mais leve ou mais cruel. */
public final class Config {
    private Config() {}

    /** Tempo sem eventos depois de entrar no mundo (ticks). 3 min. */
    public static final int GRACA_TICKS = 20 * 60 * 3;
    /** Chance base por segundo (multiplicada por (1 - sanidade/100)^2). */
    public static final double CHANCE_BASE = 0.06;
    /** Intervalo mínimo entre eventos para o mesmo jogador. */
    public static final int INTERVALO_MIN = 20 * 25;
    public static final int INTERVALO_VAR = 20 * 35;
    /** Multiplicador geral de frequência. 0 = desliga os eventos. */
    public static final double INTENSIDADE = 1.0;
    /** Multiplicador da queda de sanidade no escuro. */
    public static final double VELOCIDADE_DRENAGEM = 1.0;
    /** Evento "tochas" remove tochas perto do jogador. */
    public static final boolean APAGAR_TOCHAS = true;
    /** Evento "porta" abre portas de madeira sozinhas. */
    public static final boolean MEXER_EM_PORTAS = true;
}
