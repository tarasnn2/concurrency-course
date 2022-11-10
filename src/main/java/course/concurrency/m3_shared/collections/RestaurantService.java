package course.concurrency.m3_shared.collections;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public class RestaurantService {

    private Map<String, Restaurant> restaurantMap = new ConcurrentHashMap<>() {{
        put("A", new Restaurant("A"));
        put("B", new Restaurant("B"));
        put("C", new Restaurant("C"));
    }};

    private Map<String, LongAdder> stat = Map.of("A", new LongAdder(), "B", new LongAdder(), "C", new LongAdder());

    public Restaurant getByName(String restaurantName) {
        addToStat(restaurantName);
        return restaurantMap.get(restaurantName);
    }

    public void addToStat(String restaurantName) {
        stat.get(restaurantName).increment();
    }

    public Set<String> printStat() {
        Set<String> result = new HashSet<>();
        stat.forEach((s, longAdder) -> result.add(s+" - "+longAdder.sum()));
        return result;
    }
}
