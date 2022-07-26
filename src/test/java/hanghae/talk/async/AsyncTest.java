package hanghae.talk.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = {
    CoffeeComponent.class,
    CoffeeRepository.class
})
@Slf4j
public class AsyncTest {

  @Autowired
  private CoffeeComponent coffeeComponent;

  @Test
  public void 가격_조회_동기_블록킹_호출_테스트() {
    int expectedPrice = 1100;

    int resultPrice = coffeeComponent.getPrice("latte");
    log.info("최종 가격 전달 받음");

    Assertions.assertEquals(expectedPrice, resultPrice);
  }

  @Test
  public void 가격_조회_비동기_블록킹_호출_테스트() {
    int expectedPrice = 1100;

    CompletableFuture<Integer> future = coffeeComponent.getPriceAsync("latte");
    log.info("아직 최종 데이터를 전달 받지 않았지만, 다른 작업 수행 가능");
    int resultPrice = future.join();
    // CompletableFuture의 get 또는 join 메서드를 이용하여 Blocking을 구현하게 됨.(완벽한 Non-blocking이 아님)
    // 다른 작업 수행 중에 자동으로 메서드를 제공하는 쪽에서 결과값을 보내야 완전한 Non-Blocking
    log.info("최종 가격 전달 받음");
    Assertions.assertEquals(expectedPrice, resultPrice);
  }

  @Test
  public void 가격_조회_비동기_호출_콜백_반환없음_테스트() {
    Integer expectedPrice = 1100;

    CompletableFuture<Void> future = coffeeComponent
        .getPriceAsync("latte")
        .thenAccept(p -> {
          log.info("콜백, 가격은 " + p + "원, 하지만 데이터를 반환하지 않음");
          Assertions.assertEquals(expectedPrice, p);
        });

    log.info("아직 최종 데이터를 전달 받지 않았지만, 다른 작업 수행 가능 -> Non-Blocking");

//    아래 구문이 없으면, main Thread가 종료되기 때문에, thenAccept 확인하기 전에 끝난다.
//    따라서, 테스트를 위해 메인쓰레드가 종료되지 않도록 블록킹으로 대기하기 위한 코드.
//    future가 complete가 되면 위에 작성한 thenAccept 코드가 실행이 됨.
//    깔끔하지 않음 -> 개선의 여지가 있다.
    Assertions.assertNull(future.join());
  }

  @Test
  public void 가격_조회_비동기_호출_콜백_반환_테스트() {
    Integer expectedPrice = 1100 + 100;

    CompletableFuture<Void> future = coffeeComponent
        .getPriceAsync("latte")
        .thenApply(p -> {
          log.info("같은 스레드로 동작");
          return p + 100;
        })
        .thenAccept(p -> {
          log.info("콜백, 가격은 " + p + "원, 하지만 데이터 반환 안함.");
          Assertions.assertEquals(expectedPrice, p);
        });

    log.info("아직 최종 데이터를 전달 받지는 않았지만, 다른 작업 수행 가능.");

    Assertions.assertNull(future.join());
  }

  @Test
  public void 가격_조회_비동기_호출_콜백_반환_테스트_다른스레드() {
    Integer expectedPrice = 1100 + 100;
    Executor executor = Executors.newFixedThreadPool(5);  //해당 구문 추가

    CompletableFuture<Void> future = coffeeComponent
        .getPriceAsync("latte")
        .thenApplyAsync(p -> {
          log.info("다른 스레드로 동작");
          return p + 100;
        }, executor)
        .thenAcceptAsync(p -> {
          log.info("콜백, 가격은 " + p + "원, 하지만 데이터 반환 안함.");
          Assertions.assertEquals(expectedPrice, p);
        }, executor);

    log.info("아직 최종 데이터를 전달 받지는 않았지만, 다른 작업 수행 가능.");

    Assertions.assertNull(future.join());
  }

}
