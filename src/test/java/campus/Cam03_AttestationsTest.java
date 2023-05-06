package campus;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class Cam03_AttestationsTest {

    Faker faker = new Faker();
    String attestationID;
    String attestationName;
    Map<String, String> attestation;

    RequestSpecification requestSpecification;

    @BeforeClass
    public void login() {

        baseURI = "https://test.mersys.io";

        Map<String, String> userCredential = new HashMap<>();
        userCredential.put("username", "turkeyts");
        userCredential.put("password", "TechnoStudy123");
        userCredential.put("rememberMe", "true");

        Cookies cookies =

                given()

                        .contentType(ContentType.JSON)
                        .body(userCredential)

                        .when()
                        .post("/auth/login")

                        .then()
//                       .log().all()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();

        requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }

    @Test
    public void createAttestation() {

        attestation = new HashMap<>();

        attestationName = "Degree Certificates Attestation - " + faker.number().digits(5);
        attestation.put("name", attestationName);
        

        attestationID =

                given()

                        .spec(requestSpecification)
                        .body(attestation)
                        .log().body()

                        .when()
                        .post("/school-service/api/attestation")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;

        System.out.println("attestationID = " + attestationID);
    }

    @Test(dependsOnMethods = "createAttestation")
    public void createAttestationNegative() {

        given()

                .spec(requestSpecification)
                .body(attestation)
                .log().body()

                .when()
                .post("/school-service/api/attestation")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;
    }

    @Test(dependsOnMethods = "createAttestation")
    public void updateAttestation() {
        attestationName = "Post Graduation Certificates Attestation - " + faker.number().digits(5);

        attestation.put("id", attestationID);
        attestation.put("name", attestationName);

        given()

                .spec(requestSpecification)
                .body(attestation)
                .log().body()

                .when()
                .put("/school-service/api/attestation")

                .then()
                .log().body() // show incoming body as log
                .statusCode(200)
                .body("name", equalTo(attestationName))
        ;
    }


}
