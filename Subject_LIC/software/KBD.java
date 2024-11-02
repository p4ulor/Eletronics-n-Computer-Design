import isel.leic.utils.Time;

public class KBD {
    // Ler teclas. Métodos retornam ‘0’..’9’,’#’,’*’ ou NONE.
    public static final char NONE = '.';
    private static final char[] KEYS = {'1', '4', '7', '*', '2', '5', '8', '0', '3', '6', '9', '#'};//hardware
    private static final int DVAL_PIN = 1 << 7;
    private static final int ACK_PIN = 1 << 7;
    private static final int DATA_PINS = 0xf;

    // Inicia a classe
    public static void init() {
        HAL.clrBits(ACK_PIN);

    }

    // Retorna de imediato a tecla premida ou NONE se não há tecla premida.
    public static char getKey() {
        char c = NONE;
        if (HAL.isBit(DVAL_PIN)) {
            int charPos = HAL.readBits(DATA_PINS);
            c = KEYS[charPos];
            HAL.setBits(ACK_PIN);
            while (HAL.isBit(DVAL_PIN)) ;
        }
        init();
        return c;

    }
    // Retorna quando a tecla for premida ou NONE após decorrido ‘timeout’ milisegundos.

    public static char waitKey(long timeout) {
        long i = Time.getTimeInMillis() + timeout;
        while (i > Time.getTimeInMillis()) {
            char c = getKey();
            if (c != NONE) return c;
        }
        return NONE;

    }


}
