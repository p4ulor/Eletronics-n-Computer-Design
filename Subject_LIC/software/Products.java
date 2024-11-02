import java.io.FileReader;
import java.util.Scanner;


public class Products {
    public static final int MAX_PRODUCTS = 16, MAX_QUANTITY = 20;
    private static final String FILENAME = "Products.txt";
    //Represents the stock of products
    private static Product[] items = new Product[MAX_PRODUCTS];

    /**
     * Returns the product located in the location passed as param
     *
     * @param index position of the product
     * @return the product located in the position index
     */
    public static Product getItem(int index) {
        return items[index];
    }


    /**
     * Adds one product to the system if possible
     *
     * @param name     name of the product
     * @param quantity how many products of this type exist
     * @param position code that represents where is located this product
     * @param price    price of the product
     * @return if the product was added
     */
    public static void addProduct(int position, String name, int quantity, int price) {
        if (items.length <= MAX_PRODUCTS)
            if (quantity <= MAX_QUANTITY)
                if (position < items.length)
                    items[position] = new Product(position, name, quantity, price);
    }

    /**
     * Removes one product to the system if possible
     *
     * @param product that will be removed
     * @return if the product was removed
     */
    public static boolean removeProduct(Product product) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == product) {
                items[i] = null;
                return true;
            }
        }
        return false;
    }

    /**
     * Removes on item from the product of the indicated position
     *
     * @param position code where the product is located in the machine
     * @return if the item was removed or not
     */
    public static boolean removeItem(int position) {
        Product p = items[position];
        if (p != null)
            if (p.getQuantity() > 0) {
                p.removeQuantity(1);
                return true;
            }
        return false;
    }

    /**
     * Writes in the file FILENAME all the products
     */
    public static void writeProducts() {
        StringBuilder s = new StringBuilder();
        for (Product p : items)
            if (p != null)
                s.append(p.toString()).append("\n");
        FileAccess.writeFile(FILENAME, s.toString());

    }

    /**
     * Reads all products of the file FILENAME
     */
    public static void readProducts() {
        try (Scanner in = new Scanner(new FileReader(FILENAME))) {
            String line;
            while (in.hasNextLine()) {
                String[] info;
                line = in.nextLine();
                info = line.split(";");
                Product.addProduct(Integer.parseInt(info[0]), info[1],
                        Integer.parseInt(info[2]), Integer.parseInt(info[3]));
            }

        } catch (Exception e) {
            System.out.println("Load Error");
            e.printStackTrace();
        }
    }
}


