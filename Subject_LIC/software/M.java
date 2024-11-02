public class M {

    /**
     * Checks if the maintenance signal is on
     *
     * @return state of the maintenance signal
     */
    public static boolean isPressed() {
        return HAL.isBit(0b10000);
    }
}
