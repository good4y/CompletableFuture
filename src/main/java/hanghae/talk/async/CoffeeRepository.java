package hanghae.talk.async;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

@Repository
public class CoffeeRepository {
  private Map<String, Coffee> coffeeMap = new HashMap<>();

  @PostConstruct
  public void init(){
    coffeeMap.put("latte", Coffee.builder().name("latte").price(1100).build());
    coffeeMap.put("mocha", Coffee.builder().name("latte").price(1300).build());
    coffeeMap.put("americano", Coffee.builder().name("latte").price(900).build());
  }

  public int getPriceByName(String name){
    try{
      Thread.sleep(1000);
    }catch (InterruptedException e){
      e.printStackTrace();
    }

    return coffeeMap.get(name).getPrice();
  }
}
