import isel.leic.UsbPort;

public class HAL {
    private static int out;

    public static void init() {
        out = 0;
        out();

    }

    public static boolean isBit(int mask) { // Retorna true se o bit tiver o valor lógico ‘1’
        return (in() & mask) != 0;
    } // Retorna true se o bit tiver o valor lógico ‘1’

    public static int readBits(int mask) { // Retorna os valores dos bits representados por mask presentes no UsbPort
        return in() & mask;
    }

    public static void writeBits(int mask, int value) { // Escreve nos bits representados por mask o valor de value
        out = (out & ~mask) | (value & mask);
        out();
    }

    public static void setBits(int mask) { // Coloca os bits representados por mask no valor lógico ‘1’
        out |= mask;
        out();
    }

    public static void clrBits(int mask) { // Coloca os bits representados por mask no valor lógico ‘0’
        out &= ~mask;
        out();
    }

    private static int in() {
        return ~UsbPort.in();
    }

    private static void out() {
        UsbPort.out(~out);
    }
}