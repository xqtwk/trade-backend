package grade.tradeback;

import grade.tradeback.rapyd.RapydService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class TradeBackApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TradeBackApplication.class, args);
        RapydService rapydService = context.getBean(RapydService.class);
        try {
            String result = rapydService.createCheckout();

            System.out.println("Result of createCheckout: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
