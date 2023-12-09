package grade.tradeback.miscellaneous;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/misc")
public class MiscController {
    @GetMapping("/get-ip")
    public String getClientIp(HttpServletRequest request) {
        System.out.println(request.getRemoteAddr());
        return request.getRemoteAddr();
    }
    @GetMapping("/get-date")
    public String getCurrentTimestamp() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now); // Returns the current timestamp in seconds
    }
}
