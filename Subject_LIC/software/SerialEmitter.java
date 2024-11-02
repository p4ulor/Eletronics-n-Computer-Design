public class SerialEmitter {
    private static final int SDX_PIN = 0b1, SCLK_PIN = 0b10, BUSY_SIGNAL = 0b1000000;
    private static int parity;

    // Inicia a classe
    public static void init() {
        HAL.init();
        parity = 0;
    }

    // Envia uma trama para o SerialReceiver identificado o destino em addr e os bits de dados em ‘data’.
    public static void send(Destination addr, int data) {
        int lnd = addr.ordinal();
        while (isBusy()) ;

        startSignal();
        sendLnD(lnd);

        int nDataBits = lnd * 5 + 4;//algoritmo para calcular o numero de bits a enviar dependendo do destinatario
        sendData(nDataBits, data);

        sendParity();

    }

    // Envia o sinal de iniciação de envio de trama
    private static void startSignal() {
        HAL.clrBits(SCLK_PIN);
        HAL.setBits(SDX_PIN);
        HAL.clrBits(SDX_PIN);
        parity = 0;
    }

    //Envia o bit LnD
    private static void sendLnD(int lnd) {
        HAL.writeBits(SDX_PIN, lnd);
        HAL.setBits(SCLK_PIN);

        parity += lnd;
    }

    private static void sendData(int nBits, int data) {
        for (int i = 0; i < nBits; i++) {
            int val = (data >> i) & 1;
            HAL.writeBits(SDX_PIN, val);
            HAL.clrBits(SCLK_PIN);
            HAL.setBits(SCLK_PIN);

            parity += val;
        }
    }

    private static void sendParity() {
        HAL.writeBits(SDX_PIN, ~parity);
        HAL.clrBits(SCLK_PIN);

        HAL.setBits(SCLK_PIN);
        HAL.setBits(SDX_PIN);
        HAL.clrBits(SCLK_PIN);
    }

    // Retorna true se o canal série estiver ocupado
    public static boolean isBusy() {
        return HAL.isBit(BUSY_SIGNAL);
    }

    // Envia tramas para os diferentes módulos Serial Receiver.
    public enum Destination {
        Dispenser, LCD
    }
}
