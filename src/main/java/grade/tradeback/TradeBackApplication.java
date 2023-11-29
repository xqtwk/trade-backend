package grade.tradeback;

import grade.tradeback.auth.AuthenticationService;
import grade.tradeback.auth.RegisterRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static grade.tradeback.user.Role.*;

@SpringBootApplication
public class TradeBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeBackApplication.class, args);
    }
}
