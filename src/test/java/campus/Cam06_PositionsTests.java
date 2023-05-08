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

public class Cam06_PositionsTests {

    Faker faker = new Faker();
    String positionsID;
    String positionsName;
    String positionsShort;
    RequestSpecification recSpec;
    Map<String, String> positions = new HashMap<>();

    @BeforeClass
    public void Setup() {

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
                        // .log().all()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();

        recSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }

    @Test
    public void createPositions() {

        positionsName = "seyma" + faker.number().digits(5);
        positionsShort = "seyma" + faker.number().digits(5);

        positions.put("name", positionsName);
        positions.put("shortName", positionsShort);
        positions.put("tenantId", "6390ef53f697997914ec20c2");
        positions.put("active", "true");

        positionsID =

                given()

                        .spec(recSpec)
                        .body(positions)
                        .log().body()

                        .when()
                        .post("/school-service/api/employee-position")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");

        System.out.println("positionsID = " + positionsID);
    }

    @Test(dependsOnMethods = "createPositions")
    public void createPositionsNegative() {

        given()

                .spec(recSpec)
                .body(positions)
                .log().body()

                .when()
                .post("/school-service/api/employee-position")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;
    }

    @Test(dependsOnMethods = "createPositionsNegative")
    public void updatePositions() {

        positions.put("id", positionsID);

        positionsName = ("TechnoStudy" + faker.number().digits(5));
        positions.put("name", positionsName);
        positions.put("shortName", positionsShort);

        given()

                .spec(recSpec)
                .body(positions)
                // .log().body()

                .when()
                .put("/school-service/api/employee-position/")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(positionsName))
        ;
    }

    @Test(dependsOnMethods = "updatePositions")
    public void deletePositions() {

        given()

                .spec(recSpec)
                .pathParam("positionsID", positionsID)
                .log().uri()

                .when()
                .delete("/school-service/api/employee-position/{positionsID}")

                .then()
                .log().body()
                .statusCode(204)
        ;
    }

    @Test(dependsOnMethods = "deletePositions")
    public void deletePositionsNegative() {

        given()

                .spec(recSpec)
                .pathParam("positionsID", positionsID)
                .log().uri()

                .when()
                .delete("/school-service/api/employee-position/{positionsID}")

                .then()
                .log().body()
                .statusCode(204)
        ;
    }
}
