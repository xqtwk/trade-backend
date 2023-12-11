package grade.tradeback;

import grade.tradeback.payments.rapyd.RapydService;
import grade.tradeback.user.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class TradeBackApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TradeBackApplication.class, args);
    }
}
