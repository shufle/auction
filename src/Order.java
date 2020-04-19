import java.math.BigDecimal;
import java.math.RoundingMode;

class Order {
    /**
     * Направление S- продажа, B - покупка
     */
    private String direction;

    /**
     * Количество бумаг
     */
    private Long amount;

    /**
     * Цена заявки
     */
    private BigDecimal price;

    public Order(String direction, Long amount, Double price) {
        this.direction = direction;
        this.amount = amount;
        this.price = new BigDecimal(price).setScale(2, RoundingMode.UP);
    }

    public String getDirection() {
        return direction;
    }

    public Long getAmount() {
        return amount;
    }

    public BigDecimal getPrice() {
        return price;
    }
}