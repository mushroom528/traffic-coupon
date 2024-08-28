package example.gatling;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class SampleApiSimulator extends Simulation {
    private static final String BASE_URL = "http://localhost:8080";

    ScenarioBuilder scn = scenario("Gatling Sample Test")
            .exec(http("GET /api/gatling")
                    .get("/api/gatling")
                    .check(status().is(200))
                    .check(bodyString().is("Hello World"))
            );

    {
        setUp(
                scn.injectOpen(rampUsersPerSec(10).to(300).during(120))
        ).protocols(
                http.baseUrl(BASE_URL)
        );
    }

}
