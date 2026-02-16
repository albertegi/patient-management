import groovyjarjarantlr4.v4.runtime.atn.StarLoopEntryState;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.notNullValue;

public class AuthIntegrationTest {
    @BeforeAll
    static void setUp(){
        RestAssured.baseURI = "http://localhost:8333";
    }

    @Test
    public void shouldReturnOkWithValidToken(){
        /** 3 -steps for testing for unit test, integration test etc.:
         * (1) Arrange -> You do any set up that the test may need to
         * work 100% of the time. this includes setting up any data that the test might need
         * (2) act: act on that test after setting it up. the code we write
         * that actually triggers the thing that we are testing. calling the login endpoint and get a response
         * (3) assert: assert the result from stage. assert that the response has a valid token and also has an Ok status
         *
          */

        String longinPayload = """
                {
                    "email": "testuser@test.com",
                    "password": "password123"
                }

                """;

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(longinPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract().response();

        System.out.println("Generated Token: " + response.jsonPath().getString("token"));
    }


    @Test
    public void shouldReturnUnauthorizedOnValidLogin(){

        String longinPayload = """
                {
                    "email": "invalid_user@test.com",
                    "password": "wrong_password"
                }

                """;

                RestAssured.given()
                .contentType("application/json")
                .body(longinPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401);
    }

}
