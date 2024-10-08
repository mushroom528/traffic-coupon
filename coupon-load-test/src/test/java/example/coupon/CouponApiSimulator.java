package example.coupon;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class CouponApiSimulator extends Simulation {


    ChainBuilder setupRequest = exec(
            http("coupon-setup")
                    .post("")
                    .body(StringBody("{ \"name\": \"기본쿠폰\", \"code\": \"CODE-1\", \"total\": \"50\" }"))
                    .check(status().is(200)) // 상태 코드 200 확인
    );
    ScenarioBuilder setupScenario = scenario("setup").exec(setupRequest);

    ChainBuilder coupon = exec(
            http("Coupon-test")
                    .post("/issue")
                    .body(StringBody("{ \"code\": \"CODE-1\", \"username\": \"hyokwon\" }"))
                    .check(status().is(200)) // 상태 코드 200 확인
    );

    ScenarioBuilder couponScenario = scenario("coupon issue test").exec(coupon);
    HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8080/api/coupon").acceptHeader("application/json").contentTypeHeader("application/json");


    {
        setUp(

                /**
                 * constantConcurrentUsers: x
                 * during: y
                 * x 명의 유저가 y초 동안 동시에 요청
                 */
//                couponScenario.injectClosed(constantConcurrentUsers(100).during(5))
//                        .protocols(httpProtocol),
                setupScenario.injectOpen(atOnceUsers(1)).protocols(httpProtocol)
                        .andThen(
                                couponScenario.injectClosed(constantConcurrentUsers(10).during(10))
                                        .protocols(httpProtocol)
                        ));
    }
}
