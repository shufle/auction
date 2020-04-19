import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;


class applicationTest {


    @Test
    void findAuctionPrice() {
        ArrayList<Order> list = new ArrayList<>();
        list.add(new Order("B", 100L, 8.2));
        list.add(new Order("S", 100L, 10.2));
        list.add(new Order("B", 100L, 9.2));
        list.add(new Order("S", 100L, 10.2));
        list.add(new Order("S", 100L, 10.1));
        list.add(new Order("S", 100L, 10.0));
        list.add(new Order("B", 100L, 7.2));
        list.add(new Order("B", 100L, 10.0));
        String auctionPrice = application.findAuctionPrice(list);
        Assertions.assertEquals("100 10.00", auctionPrice);
    }

    @Test
    void findAuctionPriceOneIntersection() {
        ArrayList<Order> list = new ArrayList<>();
        list.add(new Order("B", 200L, 8.2));
        list.add(new Order("S", 100L, 8.2));
        list.add(new Order("B", 100L, 7.2));
        list.add(new Order("S", 100L, 10.2));
        String auctionPrice = application.findAuctionPrice(list);
        Assertions.assertEquals("100 8.20", auctionPrice);
    }

    @Test
    void findAuctionPriceAverage() {
        ArrayList<Order> list = new ArrayList<>();
        list.add(new Order("B", 300L, 8.25));
        list.add(new Order("S", 100L, 8.25));
        list.add(new Order("B", 100L, 10.2));
        list.add(new Order("S", 300L, 10.2));
        list.add(new Order("S", 300L, 19.2));
        String auctionPrice = application.findAuctionPrice(list);
        Assertions.assertEquals("100 9.23", auctionPrice);
    }

    @Test
    void findAuctionPriceNa() {
        ArrayList<Order> list = new ArrayList<>();
        list.add(new Order("B", 300L, 18.25));
        list.add(new Order("S", 300L, 8.25));
        list.add(new Order("B", 100L, 10.2));
        list.add(new Order("S", 300L, 10.15));
        String auctionPrice = application.findAuctionPrice(list);
        Assertions.assertEquals("0 n/a", auctionPrice);
    }

}