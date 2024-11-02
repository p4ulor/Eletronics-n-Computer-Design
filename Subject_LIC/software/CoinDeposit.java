public class CoinDeposit {
    private static final String FILENAME = "CoinDeposit.txt";
    private static int coinsAvailable;

    /**
     * Method responsible for initiation of the classes needed.
     * This method must be used before using any of the other methods of this class
     */
    public static void init() {
        coinsAvailable = 0;
    }

    /**
     * Inserts a coin in the coin deposit
     */
    public static void insertedCoins(int coins) {
        coinsAvailable += coins;
    }


    /**
     * Writes on the file FILENAME the number of coins
     */
    public static void updateFile() {
        FileAccess.writeFile(FILENAME, Integer.toString(coinsAvailable));
    }

    /**
     * Reads from the file FILENAME the number os coins and stores in coinsAvailable
     */
    public static void updateCoins() {
        String coins = FileAccess.readFile(FILENAME);
        if (coins != null)
            if (coins.endsWith(";"))
                coinsAvailable = Integer.valueOf(coins.split(";")[0]);
    }
}

