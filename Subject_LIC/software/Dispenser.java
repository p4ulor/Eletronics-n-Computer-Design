import isel.leic.utils.Time;

public class Dispenser { // Controla o estado do mecanismo de dispensa.

    private static final int EJECT = 0b10000;
    private static final int FINISH = 0b1000;

    // Inicia a classe, estabelecendo os valores iniciais.
    public static void init() {
        HAL.clrBits(EJECT);
    }

    // Envia comando para dispensar uma unidade de um produto
    public static void dispense(int productId) {
        SerialEmitter.send(SerialEmitter.Destination.Dispenser, productId);
        while (!HAL.isBit(FINISH))
            HAL.setBits(EJECT);
        Time.sleep(1000);
        HAL.clrBits(EJECT);
    }
}
