package course.concurrency.m3_shared.homework1;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public final class GridThreadSerialNumber {
  /** The next serial number to be assigned. */
  private static final AtomicInteger nextSerialNum = new AtomicInteger();

  private GridThreadSerialNumber() {
  }

  /**
   * @return Serial number value.
   */
  public static int get() {
    return nextSerialNum.incrementAndGet();
  }

  public static void main(String[] args){
    final Set<Integer> result=new HashSet<>(1_000_000);

    for(int i = 0; i < 1_000_000; i++) {
      CompletableFuture.runAsync(()->{
      final int random = GridThreadSerialNumber.get();
      if (result.contains(random)){
        throw new RuntimeException();
      }
      result.add(random);
      }).exceptionally(throwable -> {
        System.out.println("found double!");
        return null;
      }).join();

    }
  }
}
