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

public class Cam11_GradeLevelsTests {
    Faker faker = new Faker();
    String gradelevelID;
    String gradelevelName;
    String gradelevelShortName;

    RequestSpecification recSpec;
    Map<String, String> gradeLevel;

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
    public void createGradeLevel() {
        gradeLevel = new HashMap<>();
        gradelevelName = faker.name().firstName() + faker.number().digits(5);
        gradelevelShortName = faker.name().lastName() + faker.number().digits(5);

        gradeLevel.put("name", gradelevelName);
        gradeLevel.put("shortName", gradelevelShortName);

        gradelevelID =
                given()

                        .spec(recSpec)
                        .body(gradeLevel)
                        .log().body()

                        .when()
                        .post("/school-service/api/grade-levels")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");

        System.out.println("levelID = " + gradelevelID);
    }


    @Test(dependsOnMethods = "createGradeLevel")
    public void createGradeLevelNegative() {
        gradeLevel.put("name", gradelevelName);
        gradeLevel.put("shortName", gradelevelShortName);

        given()

                .spec(recSpec)
                .body(gradeLevel)
                .log().body()

                .when()
                .post("/school-service/api/grade-levels")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;
    }


    @Test(dependsOnMethods = "createGradeLevelNegative")
    public void updateGradeLevel() {
        gradeLevel.put("id", gradelevelID);

        gradelevelName = ("TechnoStudy" + faker.number().digits(5));
        gradeLevel.put("name", gradelevelName);
        gradeLevel.put("shortName", gradelevelShortName);

        given()

                .spec(recSpec)
                .body(gradeLevel)
                // .log().body()

                .when()
                .put("/school-service/api/grade-levels")

                .then()
                .log().body()
                .statusCode(200)
                .body("id", equalTo(gradelevelID))
        ;

    }

    @Test(dependsOnMethods = "updateGradeLevel")
    public void deleteGradeLevel() {
        given()

                .spec(recSpec)
                .pathParam("levelID", gradelevelID)
                .log().uri()

                .when()
                .delete("/school-service/api/grade-levels/{levelID}")

                .then()
                .log().body()
                .statusCode(200)
        ;

    }

    @Test(dependsOnMethods = "deleteGradeLevel")
    public void deleteGradeLevelNegative() {
        given()

                .spec(recSpec)
                .pathParam("levelID", gradelevelID)
                .log().uri()

                .when()
                .delete("/school-service/api/grade-levels/{levelID}")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("Grade Level not found."))
        ;

    }


}
