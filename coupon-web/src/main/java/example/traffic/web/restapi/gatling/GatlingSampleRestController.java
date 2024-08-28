package example.traffic.web.restapi.gatling;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gatling")
public class GatlingSampleRestController {

    @GetMapping()
    public String sample() throws InterruptedException {
//        TimeUnit.SECONDS.sleep(1);
        return "Hello World";
    }
}
