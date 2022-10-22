package course.concurrency.m2_async.cf.min_price;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        final List<CompletableFuture<Double>> futures =
            shopIds.stream()
            .map(shopId -> CompletableFuture.supplyAsync(getPrice(itemId, shopId))
                .orTimeout(2899,TimeUnit.MILLISECONDS)
                .exceptionally(throwable -> Double.NaN))
            .collect(Collectors.toList());

        return CompletableFuture
                .allOf(futures.toArray(CompletableFuture<?>[]::new))
                .thenApply(processResult().apply(futures))
                .join();
    }
    private Function<List<CompletableFuture<Double>>, Function<Void, Double>> processResult(){
        return  futures -> t -> futures
            .stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList())
            .stream()
            .filter(price -> !price.equals(Double.NaN))
            .min(Double::compareTo)
            .orElse(Double.NaN);
    }
    private Supplier<Double> getPrice(long itemId, long shopId){
        return () -> priceRetriever.getPrice(itemId,shopId);
    }
}
