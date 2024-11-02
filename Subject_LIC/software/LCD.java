import isel.leic.utils.Time;


public class LCD { // Escreve no LCD usando a interface a 4 bits.
    public static final int LINES = 2, COLS = 16; // Dimensão do display.

    // Escreve um byte de comando/dados no LCD
    private static void writeByte(boolean rs, int data) {
        Time.sleep(4);
        SerialEmitter.send(SerialEmitter.Destination.LCD, (data << 1) | (rs ? 1 : 0));
    }

    // Escreve um comando no LCD
    private static void writeCMD(int data) {
        writeByte(false, data);
    }

    // Escreve um dado no LCD
    private static void writeDATA(int data) {
        writeByte(true, data);
    }

    // Envia a sequência de iniciação para comunicação a 4 bits.
    public static void init() {
        Time.sleep(50);
        writeCMD(0x30);
        Time.sleep(5);
        writeCMD(0x30);
        Time.sleep(1);
        writeCMD(0x30);
        writeCMD(0x38);
        writeCMD(0x8);
        writeCMD(0x1);
        writeCMD(0x6);
        writeCMD(0xF);

    }

    // Escreve um caráter na posição corrente.
    public static void write(char c) {
        writeDATA((int) c);
    }

    // Escreve uma string na posição corrente.
    public static void write(String txt) {
        for (int i = 0; i < txt.length(); i++)
            write(txt.charAt(i));
    }

    // Envia comando para posicionar cursor (‘lin’:0..LINES-1 , ‘col’:0..COLS-1)
    public static void cursor(int lin, int col) {
        writeCMD(((0x40 * lin) + col) | 0x80);

    }

    // Envia comando para limpar o ecrã e posicionar o cursor em (0,0)
    public static void clear() {
        writeCMD(0x1);

    }
}
