package hanghae.talk.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoffeeComponent implements CoffeeUseCase{

  private final CoffeeRepository coffeeRepository;
  Executor executor = Executors.newFixedThreadPool(10);
//  private final ThreadPoolTaskExecutor taskExecutor;
  @Override
  public int getPrice(String name) {

    log.info("동기 호출 방식으로 가격 조회 시작");

    return coffeeRepository.getPriceByName(name);
  }

  // Non-Blocking 과 Blocking이 섞임 -> 메서드를 호출하는 순간 블록킹 현상
//  @Override
//  public CompletableFuture<Integer> getPriceAsync(String name) {
//
//    log.info("비동기 호출 방식으로 가격 조회 시작");
//
//    CompletableFuture<Integer> future = new CompletableFuture<>();
//
//    new Thread(() -> {
//      log.info("새로운 쓰레드로 작업 시작");
//      Integer price = coffeeRepository.getPriceByName(name);
//      future.complete(price);
//    }).start();
//
//    return future;
//  }

  @Override
  public CompletableFuture<Integer> getPriceAsync(String name){
    log.info("비동기 호출 방식으로 가격 조회 시작");

    return  CompletableFuture.supplyAsync(() -> {
      log.info("supplyAsync");
      return coffeeRepository.getPriceByName(name);
    },
        executor);
  }

  @Override
  public Future<Integer> getDiscountPriceAsync(Integer price) {
    return null;
  }
}
