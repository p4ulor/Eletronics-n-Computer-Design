public class Product extends Products {
    private final String PRODUCT;
    private int quantity;
    private int position;
    //prince in cents
    private int price;

    Product(int position, String name, int quantity, int price) {
        this.quantity = quantity;
        PRODUCT = name;
        this.position = position;
        this.price = price;
    }

    @Override
    public String toString() {
        return Integer.valueOf(position).toString() + ';' +
                PRODUCT + ';' +
                Integer.valueOf(quantity).toString() + ';' +
                Integer.valueOf(price).toString();
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int value) {
        quantity = value;
    }

    public String getPRODUCT() {
        return PRODUCT;
    }

    public void removeQuantity(int number) {
        quantity -= number;
    }
}
