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

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class Cam13_NationalitiesTests {

    Faker faker = new Faker();
    String NationalitiesID;
    String NationalitiesName;
    Map<String, String> Nationalities;

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
    public void createNationalities() {

        Nationalities = new HashMap<>();

        NationalitiesName = "serdar" + faker.number().digits(3);
        Nationalities.put("name", NationalitiesName);


        NationalitiesID =

                given()

                        .spec(requestSpecification)
                        .body(Nationalities)
                        .log().body()

                        .when()
                        .post("/school-service/api/nationality")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;
        System.out.println("NationalitiesID = " + NationalitiesID);
    }

    @Test(dependsOnMethods = "createNationalities")
    public void createNationalitiesNegative() {

        Nationalities.put("name", NationalitiesName);

        given()

                .spec(requestSpecification)
                .body(Nationalities)
                .log().body()

                .when()
                .post("/school-service/api/nationality")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;
    }

    @Test(dependsOnMethods = "createNationalitiesNegative")
    public void updateNationalities() {

        NationalitiesName = "Tazekan - " + faker.number().digits(5);

        Nationalities.put("id", NationalitiesID);
        Nationalities.put("name", NationalitiesName);

        given()

                .spec(requestSpecification)
                .body(Nationalities)
                // .log().body()

                .when()
                .put("/school-service/api/nationality")

                .then()
                .log().body() // show incoming body as log
                .statusCode(200)
                .body("name", equalTo(NationalitiesName))
        ;
    }

    @Test(dependsOnMethods = "updateNationalities")
    public void deleteNationalities() {

        given()

                .spec(requestSpecification)
                .log().uri()

                .when()
                .delete("/school-service/api/nationality/" + NationalitiesID)

                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    @Test(dependsOnMethods = "deleteNationalities")
    public void deleteNationalitiesNegative() {

        given()

                .spec(requestSpecification)
                .log().uri()

                .when()
                .delete("/school-service/api/nationality/" + NationalitiesID)

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("Nationality not  found"))
        ;
    }
}
