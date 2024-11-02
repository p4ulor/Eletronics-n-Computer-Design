public class TUI {
    public static final int SCROLL_MODE = 1;
    public static final int KEYBOARD_MODE = 0;
    public static int indexProduct;
    public static int searchMode;
    private static boolean off;
    private static Product onDisplay;

    /**
     * Method responsible for the initiation of the classes needed.
     * This method must be used before using any of the other methods of this class
     */
    public static void init() {
        indexProduct = 0;
        off = false;
        onDisplay = null;
        searchMode = KEYBOARD_MODE;
    }

    /**
     * Display in the LCD the initial screen information
     */
    public static void startScreen() {
        onDisplay = null;
        searchMode = KEYBOARD_MODE;
        clearAllScreen();
        showLeft("Vending Machine", 0);
        indexProduct = 0;
    }


    /**
     * Verifies if the key pressed is the confirmation
     *
     * @param key code pressed in the keyboard
     * @return {@code boolean} that indicates if the key pressed is #
     */
    public static boolean isConfirmation(char key) {
        return key == '#';
    }

    /**
     * Changes the value of indexProduct for the next of previous product depending of code putted in the keyboard, if this one is valid.
     * input 2 -gives the next product
     * input 8 -gives the previous product
     * input * our # gets out of scroll mode
     *
     * @param key key pressed in the keyboard
     * @return {@code boolean} that inform if still inside the scroll mode
     */
    public static boolean scrollMode(char key) {
        if (Character.isDigit(key)) {
            switch (key) {
                case '2':
                    indexProduct = searchNextProduct(indexProduct);
                    break;
                case '8':
                    indexProduct = searchPreviousProduct(indexProduct);
                    break;
            }
            productDisplay(indexProduct, SCROLL_MODE);
            return true;
        }
        return false;
    }

    /**
     * Changes the value of indexProduct for the code putted in the keyboard, if this one is valid.
     *
     * @param key key pressed in the keyboard
     * @return {@code boolean} that inform if still inside the keyboard mode
     */
    public static boolean keyBoardMode(char key) {
        if (Character.isDigit(key)) {
            int value = Character.digit(key, 10);
            int tenValue = 10 * indexProduct + value;
           /* System.out.println("value: "+Integer.toString(value));
            System.out.println("tenValue: "+Integer.toString(tenValue));*/
            if (verifyIndex(value)) {
                indexProduct = value;
            }
            if (verifyIndex(tenValue)) {
                indexProduct = tenValue;
            }
            if (!verifyIndex(indexProduct)) {
                indexProduct = searchNextProduct(indexProduct);
            }
            productDisplay(indexProduct, KEYBOARD_MODE);
            return true;
        }
        return false;
    }

    public static boolean verifyIndex(int value) {
        if (value < Product.MAX_PRODUCTS) {

            if (value == 0)
                return true;//case product 0 is unavailable is need to reach values with number 0
            else
                for (int i = value; i != 0; i /= 10)
                    if (i % 10 == 1)
                        return true;//case product 1 is unavailable is need to reach values with number 1

            Product p = Product.getItem(value);
            return p != null && p.getQuantity() > 0 && p.getQuantity() < Product.MAX_QUANTITY;
        }
        return false;
    }

    /**
     * Displays the product information in LCD.
     *
     * @param index of the product to be displayed
     * @param mode
     */
    public static void productDisplay(int index, int mode) {
        Product product = Product.getItem(index);
        productDisplay(product, mode);
    }

    public static void productDisplay(Product product, int mode) {
        if (product != null)
            if (product != onDisplay) {
                refreshScreen(product, mode);
                onDisplay = product;
            } else if (searchMode != mode) {
                showProductId(mode);
                searchMode = mode;
            }
    }

    /**
     * Responsible for the update of the information on the screen
     *
     * @param product product that should be displaying on the screen
     * @param mode    of search of the product (KEYBOARD_MODE or SCROLL_MODE)
     */
    public static void refreshScreen(Product product, int mode) {
        clearAllScreen();
        showCenter(product.getPRODUCT(), 0);
        int quantity = product.getQuantity();
        if (quantity >= 1 && quantity <= Product.MAX_QUANTITY)
            showCenter("#" + String.format("%02d", quantity), 1);
        else showCenter("#--", 1);
        int price = product.getPrice();
        showRight(String.format("%.1f", price / 10f), 1);
        showProductId(mode);
    }

    /**
     * Show the product location in the mode passed as param
     *
     * @param mode of apresentation of the location of the prodiuct
     */
    public static void showProductId(int mode) {
        showLeft(String.format("%02d", indexProduct) + (mode == TUI.KEYBOARD_MODE ? ":" : "*"), 1);
    }

    /**
     * Display a message on LCD with the alignment on the center.
     *
     * @param txt  Text to show on LCD. User has to make sure the text can fit in the LCD line.
     * @param line Which line the text is supposed to appear.Lines start at 0.
     *             User has to make sure that the number passed is coherent with the LCD.
     */
    public static void showCenter(String txt, int line) {
        int center = (LCD.COLS - txt.length()) / 2;
        if (center >= 0 && line < LCD.LINES) {
            LCD.cursor(line, center);
            LCD.write(txt);
        }
    }

    /**
     * Display a message on LCD with the alignment on the right.
     *
     * @param txt  Text to show on LCD. User has to make sure the text can fit in the LCD line.
     * @param line Which line the text is supposed to appear.Lines start at 0.
     *             User has to make sure that the number passed is coherent with the LCD.
     */
    public static void showRight(String txt, int line) {
        int left = LCD.COLS - txt.length();
        if (left >= 0 && line < LCD.LINES) {
            LCD.cursor(line, left);
            LCD.write(txt);
        }
    }

    /**
     * Display a message on LCD with the alignment on the left
     *
     * @param txt  Text to show on LCD. User has to make sure the text can fit in the LCD line.
     * @param line Which line the text is supposed to appear.Lines start at 0.
     *             User has to make sure that the number passed is coherent with the LCD.
     */
    public static void showLeft(String txt, int line) {
        LCD.cursor(line, 0);
        LCD.write(txt);
    }

    /**
     * Search for a new product starting the searched in product above the actual one
     *
     * @param index the number where the product is located in the machine.
     * @return the product that is before the actual one.
     * Case doesn't exist returns the product with the code passed as param.
     */
    public static int searchNextProduct(int index) {
        Product p;
        int start = index;
        do {
            index = Math.floorMod(index + 1, Product.MAX_PRODUCTS);
            p = Product.getItem(index);
        } while (p == null || p.getQuantity() <= 0 || p.getQuantity() > Product.MAX_QUANTITY || start == index);

        return index;
    }

    /**
     * Search for a new product starting the searched in product bellow the actual one
     *
     * @param indexProduct the code where the product is located in the machine.
     * @return the product that is before the actual one.
     * Case doesn't exist returns the product with the code passed as param.
     */
    public static int searchPreviousProduct(int indexProduct) {
        Product p;
        int start = indexProduct;
        do {
            indexProduct = Math.floorMod(indexProduct - 1, Product.MAX_PRODUCTS);
            p = Product.getItem(indexProduct);
        } while (p == null || p.getQuantity() <= 0 || p.getQuantity() > Product.MAX_QUANTITY || start == indexProduct);

        return indexProduct;
    }

    /**
     * Clears all information displayed in LCD screen
     */
    public static void clearAllScreen() {
        LCD.clear();
    }


    /**
     * Indicates system status(on or off)
     *
     * @return boolean off
     **/
    public static boolean isOff() {
        return off;
    }

    public static void collectScreen() {
        clearAllScreen();
        showCenter("Collect Product", 1);
    }

    /**
     * Display of when the machines turns off
     */
    public static void turnOff() {
        off = true;
        clearAllScreen();
        showCenter("IS", 0);
        showCenter("OFF", 1);
    }


    public static Product getProductOnDisplay() {
        return onDisplay;
    }

    /**
     * Start screen display when maintenance mode is on
     */
    public static void maintStartScreen() {
        clearAllScreen();
        showLeft("Maintenance Mode", 0);
        showLeft("1-Ld 2-Rm 3-Off", 1);
        onDisplay = null;
    }
}

