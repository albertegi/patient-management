import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.notNullValue;

public class PatientIntegrationTest {

    @BeforeAll
    static void setUp(){
        RestAssured.baseURI = "http://localhost:8333";
    }

    @Test
    public void shouldReturnPatientsWithValidToken(){

        String longinPayload = """
                {
                    "email": "testuser@test.com",
                    "password": "password123"
                }

                """;

        String token = RestAssured.given()
                .contentType("application/json")
                .body(longinPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("token");
        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/patients")
                .then()
                .statusCode(200)
                .body("patients", notNullValue());

    }
}
