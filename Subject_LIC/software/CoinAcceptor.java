import isel.leic.utils.Time;

public class CoinAcceptor { // Implementa a interface com o moedeiro.
    private static final int COIN_SIGNAL = 1 << 5;
    private static final int ACCEPT_SIGNAL = 0b10000;
    private static final int COLLECT_SIGNAL = 0b100000;
    private static final int EJECT_SIGNAL = 0b1000000;

    public static int coins;
    public static int coinsStored;

    public static void init() {
        coins = 0;
    }

    // Retorna true se foi introduzida uma nova moeda.
    public static boolean hasCoin() {
        return HAL.isBit(COIN_SIGNAL);
    }

    // Informa o moedeiro que a moeda foi aceite.
    public static void acceptCoin() {
        while (hasCoin()) HAL.setBits(ACCEPT_SIGNAL);
        coins += 1;
        HAL.clrBits(ACCEPT_SIGNAL);
    }

    // Devolve as moedas que estão no moedeiro.
    public static void ejectCoins() {
        HAL.setBits(EJECT_SIGNAL);
        coins = 0;
        Time.sleep(1000);
        HAL.clrBits(EJECT_SIGNAL);
    }

    // Recolhe as moedas que estão no moedeiro.
    public static void collectCoin() {
        HAL.setBits(COLLECT_SIGNAL);
        coinsStored = coins;
        Time.sleep(1000);
        HAL.clrBits(COLLECT_SIGNAL);
        coins = 0;
    }
}