import isel.leic.utils.Time;

import java.util.Calendar;
import java.util.TimeZone;

public class MyApp {
    //is the wait time until gets back to the start screen
    private static final int TIMEOUT = 10 * 1000;
    //information wich search mode is being used
    private static boolean isKeyBoardMode;

    //Initialization of all the classes needed in this project
    private static void init() {
        HAL.init();
        KBD.init();
        LCD.init();
        TUI.init();
        CoinDeposit.init();
        CoinAcceptor.init();
        Dispenser.init();
        isKeyBoardMode = true;
    }

    /**
     * updates the classes with the information of their files
     */
    private static void load() {
        Product.readProducts();
        CoinDeposit.updateCoins();
    }

    public static void main(String[] args) {
        init();
        load();
        while (!TUI.isOff()) {//verifies if the machine is still on
            start();
            if (M.isPressed()) {
                maintenance();
                continue;
            }
            run();
        }
        save();
    }

    /**
     * Responsible for the search of the product and display that is selected in the moment
     *
     * @return they last key pressed
     */
    private static char searchProduct() {
        char key;
        key = isKeyBoardMode ? keyBoardSearch() : scrollSearch();
        switch (key) {
            case '#':
            case KBD.NONE:
                return key;
            case '*':
                isKeyBoardMode = !isKeyBoardMode;
                if (isKeyBoardMode)
                    TUI.productDisplay(TUI.indexProduct, TUI.KEYBOARD_MODE);
                else
                    TUI.productDisplay(TUI.indexProduct, TUI.KEYBOARD_MODE);
        }
        return key;
    }

    /**
     * Executes the search for a product in scroll mode as well the display of the product selected
     *
     * @return they las key pressed
     */
    private static char scrollSearch() {
        char key = '0';
        while (TUI.scrollMode(key)) key = KBD.waitKey(TIMEOUT);
        return key;
    }

    /**
     * Executes the search for a product in keyboard mode as well the display of the product selected
     *
     * @return the last key pressed
     */
    private static char keyBoardSearch() {
        char key = (char) (TUI.indexProduct % 10 + 48);//%10 is in case of number with 2 digits gets only the unity
        while (TUI.keyBoardMode(key)) key = KBD.waitKey(TIMEOUT);
        return key;
    }

    /**
     * routine that executes when maintenance signal is active
     */
    private static void maintenance() {
        TUI.maintStartScreen();
        while (M.isPressed())
            switch (KBD.getKey()) {
                case '1':
                    maintAdd();
                    TUI.maintStartScreen();
                    break;
                case '2':
                    maintRem();
                    TUI.maintStartScreen();
                    break;
                case '3':
                    if (confChoice("ShutDown")) {
                        TUI.turnOff();
                        return;
                    } else TUI.maintStartScreen();
            }
    }

    /**
     * Routine used to remove an item from the machine
     */
    private static void maintRem() {
        char key;
        firstDisplay();
        //search for the product
        //Case the input takes more then TIMEOUT milliseconds machines goes back to the start mode
        while (!TUI.isConfirmation(key = searchProduct())) {
            if (key == KBD.NONE) return;
        }
        //p holds the information of the product that is on display
        Product p = TUI.getProductOnDisplay();
        if (confChoice("Remove " + p.getPRODUCT()))
            if (Product.removeProduct(p)) {
                TUI.clearAllScreen();
                TUI.showCenter("Product", 0);
                TUI.showCenter("Removed", 1);
                Time.sleep(1000);
                TUI.clearAllScreen();
            }
    }

    /**
     * Routine used to add a new quantity to an item from the machine
     */
    private static void maintAdd() {
        char key;
        //search for the product
        //Case the input takes more then TIMEOUT milliseconds machines goes back to the start mode
        firstDisplay();
        while (!TUI.isConfirmation(key = searchProduct())) {
            if (key == KBD.NONE)
                return;
        }
        int value = getQuantity();
        String s = value + " " + TUI.getProductOnDisplay().getPRODUCT();
        if (confChoice(s))
            TUI.getProductOnDisplay().setQuantity(value);//sets the new quantity of the product that is in display
    }

    /**
     * Waits until a valid quantity is inserted
     *
     * @return the quantity inserted
     */
    private static int getQuantity() {
        int value;
        do {
            TUI.showCenter("#--", 1);
            int q1 = getNumInput();
            TUI.showCenter("#-" + q1, 1);
            int q0 = getNumInput();
            TUI.showCenter(String.format("#%d%d", q1, q0), 1);
            value = q1 * 10 + q0;
        } while (value >= Product.MAX_QUANTITY);
        return value;
    }

    /**
     * Waits until it get a numerical input
     *
     * @return the number of the input
     */
    private static int getNumInput() {
        int q;
        do {
            q = KBD.getKey();
        }
        while (!Character.isDigit(q));
        return Character.digit(q, 10);
    }

    /**
     * Prompts the message of confirmation of the action and waits for the choice
     *
     * @param choice is the message to be displayed on the prompt
     * @return the choice of the user
     */
    private static boolean confChoice(String choice) {
        TUI.clearAllScreen();
        TUI.showCenter(choice, 0);
        TUI.showCenter("Yes-5   other-No", 1);

        char c;
        for (c = KBD.getKey(); c == KBD.NONE; c = KBD.getKey()) ;
        return c == '5';
    }

    private static void start() {
        TUI.startScreen();
        boolean wait = false;//variable used so the screen doesn't refresh more then one time per second
        Calendar time = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        showCurrTime(time);
        //cicle that will get the current time while is the start screen
        for (; !TUI.isConfirmation(KBD.getKey()) && !M.isPressed(); time = Calendar.getInstance(TimeZone.getTimeZone("UTC"))) {
            //when passes one second the hours in the screen will be updated with actual time
            if (time.get(Calendar.SECOND) == 0) {//passa quando os segundos terem valor 0
                if (!wait) {
                    //when passes one day the date in the screen will be updated with actual day
                    if (time.get(Calendar.HOUR) == 0)
                        showCurrDate(time);
                    showCurrHours(time);
                    wait = true;
                }
            } else wait = false;
        }
    }

    /**
     * Show on the display the date and the hours
     *
     * @param time holds the information of the day selected
     */
    private static void showCurrTime(Calendar time) {
        showCurrDate(time);
        showCurrHours(time);
    }

    //Shows on display the hours
    private static void showCurrHours(Calendar time) {
        TUI.showRight(String.format("%02d:%02d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE)), 1);
    }

    //Shows on display the date
    private static void showCurrDate(Calendar time) {
        TUI.showLeft(String.format("%02d/%02d/%02d", time.get(Calendar.DAY_OF_MONTH), time.get(Calendar.MONTH) + 1, time.get(Calendar.YEAR) % 100), 1);
    }

    /**
     * Saves all the information in the respective files
     */
    private static void save() {
        Products.writeProducts();
        System.out.println("coins Stored" + CoinAcceptor.coinsStored);
        CoinDeposit.insertedCoins(CoinAcceptor.coinsStored);
        CoinDeposit.updateFile();
    }

    private static void run() {
        char key;//holds the information of the key pressed
        firstDisplay();
        while ((key = searchProduct()) != KBD.NONE)
            //checks if is to execute the purchase of the product
            if (TUI.isConfirmation(key)) {
                if (purchase(TUI.getProductOnDisplay())) {
                    productCollection();
                    updateFiles();
                } else if (CoinAcceptor.coins > 0) cancel();
                return;
            }
        System.out.println("end");
    }

    /**
     * this method guaranties that indexProduct variable holds a valid information and displays it
     */
    private static void firstDisplay() {
        if (Product.getItem(TUI.indexProduct) == null)
            TUI.indexProduct = TUI.searchNextProduct(TUI.indexProduct);
        TUI.productDisplay(TUI.indexProduct, TUI.KEYBOARD_MODE);
    }

    /**
     * Is called when the purchased was canceled
     */
    private static void cancel() {
        TUI.clearAllScreen();
        TUI.showCenter("Returning", 0);
        TUI.showCenter(CoinAcceptor.coins + " coins", 1);
        CoinAcceptor.ejectCoins();
    }

    /**
     * gets all the information from the text files to the respective classes
     */
    private static void updateFiles() {
        CoinDeposit.insertedCoins(CoinAcceptor.coinsStored);
        CoinDeposit.updateFile();
        Products.writeProducts();
    }

    /**
     * Shows to the costumer when to get the product and waits until the collection of the same
     * showing the greetings in the end.
     */
    private static void productCollection() {
        TUI.clearAllScreen();
        TUI.showCenter("Collect", 0);
        TUI.showCenter("Product", 1);
        Dispenser.dispense(TUI.indexProduct);
        Products.removeItem(TUI.indexProduct);
        TUI.clearAllScreen();
        TUI.showCenter("Thank you", 0);
        TUI.showCenter("for buying", 1);
    }

    /**
     * Executes the purchase routine if the product given is valid
     *
     * @param p is the product that will be bought
     * @return whether the purchase was successful or not
     */
    private static boolean purchase(Product p) {
        if (p != null && p.getQuantity() > 0) {
            TUI.clearAllScreen();
            TUI.showCenter(p.getPRODUCT(), 0);
            TUI.showLeft("Insert", 1);
            int price = p.getPrice();
            TUI.showCenter(String.format("%02d", price), 1);
            if (checkBuy(price)) {
                CoinAcceptor.collectCoin();
                return true;
            }
        }
        return false;
    }

    /**
     * Verifies if the coins inserted are enough to buy the product
     *
     * @param price of the product
     * @return true if all coins where inserted
     */
    private static boolean checkBuy(int price) {
        do {
            if (TUI.isConfirmation(KBD.getKey())) return false;
            if (CoinAcceptor.hasCoin()) {
                CoinAcceptor.acceptCoin();
                TUI.showCenter(String.format("%02d", price - CoinAcceptor.coins), 1);
            }
        } while (CoinAcceptor.coins < price);
        return true;
    }
}