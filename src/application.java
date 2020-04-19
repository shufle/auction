import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
/**
 * Программа, которая вычисляет оптимальную цену дискретного аукциона некоторой (единственной) ценной бумаги
 */
public class application {

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        List<Order> orders = new ArrayList<>();
        System.out.print("Please input order list.\nExample: S 100 10.00\n");
        while (true) {
            System.out.print("order: ");
            String line = in.nextLine();
            if (line.isEmpty()) {
                in.close();
                break;
            }
            Order newOrder = parseOrderLine(line);
            if (newOrder == null) {
                System.out.print("Bad order format, try again.\n");
                continue;
            }
            orders.add(newOrder);
            if (orders.size() == 1000000) break;
        }
        System.out.println(findAuctionPrice(orders));
    }

    private static Order parseOrderLine(String line) {
        if (line == null || line.isEmpty()) return null;
        String[] split = line.split(" ");
        if (split.length != 3) return null;
        try {
            String direction = split[0].trim();
            if (!direction.equals("S") && !direction.equals("B")) return null;
            Long amount = Long.parseLong(split[1]);
            if (amount < 1 || amount > 1000) return null;
            Double price = Double.parseDouble(split[2]);
            if (price <= 0 || price > 100) return null;
            return new Order(direction, amount, price);
        } catch (Exception e) {
            System.out.println("parseOrderLine error " + line + " " + e.getMessage());
            return null;
        }
    }

    /**
     * Алгоритм оптимальной цены дискретного аукциона
     *
     * @param orderList список заявок
     * @return строка с ответом "объем сделок + цена" или "0 n/a"
     */
    public static String findAuctionPrice(List<Order> orderList) {
        String NOT_FOUND = "0 n/a";
        try {
            Comparator<Order> priceComparator = Comparator.comparing(Order::getPrice);
            TreeMap<BigDecimal, Long> sellPool = new TreeMap<>();
            TreeMap<BigDecimal, Long> buyPool = new TreeMap<>();
            AtomicLong sellAmount = new AtomicLong();
            AtomicLong buyAmount = new AtomicLong();
            orderList.stream().filter(o -> o.getDirection().equals("S")).sorted(priceComparator).
                    forEachOrdered(o -> {
                        if (sellPool.containsKey(o.getPrice())) {
                            sellPool.put(o.getPrice(), sellPool.get(o.getPrice()) + o.getAmount());
                            sellAmount.addAndGet(o.getAmount());
                        } else {
                            sellPool.put(o.getPrice(), sellAmount.addAndGet(o.getAmount()));
                        }
                    });
            orderList.stream().filter(o -> o.getDirection().equals("B")).sorted(priceComparator.reversed()).
                    forEachOrdered(o -> {
                        if (buyPool.containsKey(o.getPrice())) {
                            buyPool.put(o.getPrice(), buyPool.get(o.getPrice()) + o.getAmount());
                            buyAmount.addAndGet(o.getAmount());
                        } else {
                            buyPool.put(o.getPrice(), buyAmount.addAndGet(o.getAmount()));
                        }
                    });
            //ищем пересечения
            BigDecimal buyPrice = buyPool.lastKey();
            BigDecimal sellPrice = sellPool.firstKey();
            if (buyPrice.compareTo(sellPrice) < 0) return NOT_FOUND;
            if (buyPrice.compareTo(sellPrice) == 0) {
                long amount = Math.min(sellPool.get(sellPrice), buyPool.get(buyPrice));
                return amount + " " + sellPrice.toString();
            }
            List<BigDecimal> buySet = buyPool.keySet().stream().filter(bd -> bd.compareTo(sellPrice) <= 0).sorted().collect(Collectors.toList());
            List<BigDecimal> sellSet = sellPool.keySet().stream().filter(bd -> bd.compareTo(buyPrice) >= 0).sorted().collect(Collectors.toList());
            List<BigDecimal> list = new ArrayList<>();
            list.addAll(buySet);
            list.addAll(sellSet);
            list = list.stream().distinct().sorted().collect(Collectors.toList());


            TreeMap<BigDecimal, Long> prices = new TreeMap<>();
            Long currentBuyAmount = buyPool.get(buySet.get(0));
            Long currentSellAmount = sellPool.get(sellSet.get(0));
            for (BigDecimal price : list) {
                currentBuyAmount = buyPool.get(price) == null ? currentBuyAmount : buyPool.get(price);
                currentSellAmount = sellPool.get(price) == null ? currentSellAmount : sellPool.get(price);
                prices.put(price, Math.min(currentBuyAmount, currentSellAmount));
            }
            Long maxAmount = prices.values().stream().max(Long::compare).get();
            List<BigDecimal> result = new ArrayList<>();
            prices.forEach((k,v) -> {
                if (v.equals(maxAmount)) {
                    result.add(k);
                }
            });
            if (result.size() == 1) {
                return maxAmount + " " + result.get(0).toString();
            }
            return maxAmount + " " + result.get(0).add(result.get(1)).divide(new BigDecimal(2), RoundingMode.UP).toString();
        } catch (Exception e) {
            return NOT_FOUND;
        }
    }
}
