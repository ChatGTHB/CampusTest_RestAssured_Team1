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

public class Cam08_SchoolLocationsTests {

    Faker faker = new Faker();
    String SchoolLocationID;
    String SchoolLocationName;
    String SchoolLocationShortName;
    String SchoolLocationCapacity;

    RequestSpecification recSpec;
    Map<String, String> SchoolLocation;

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
                        //.log().all()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();

        recSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }
    @Test
    public void CreateSchoolLocations() {

        SchoolLocation = new HashMap<>();

        SchoolLocationName = faker.name().firstName() + faker.number().digits(5);
        SchoolLocationShortName = faker.name().lastName() + faker.number().digits(3);
        SchoolLocationCapacity = faker.number().digits(5);

        SchoolLocation.put("name", SchoolLocationName);
        SchoolLocation.put("shortName", SchoolLocationShortName);
        SchoolLocation.put("capacity", SchoolLocationCapacity);
        SchoolLocation.put("type", "LABORATORY");
        SchoolLocation.put("school", "6390f3207a3bcb6a7ac977f9");

        SchoolLocationID =

                given()

                        .spec(recSpec)
                        .body(SchoolLocation)
                        .log().body()

                        .when()
                        .post("/school-service/api/location")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");

        System.out.println("LocationID = " + SchoolLocationID);
    }

    @Test(dependsOnMethods = "CreateSchoolLocations")
    public void createSchoolLocationsNegative() {

        SchoolLocation.put("name", SchoolLocationName);
        SchoolLocation.put("shortName", SchoolLocationShortName);

        given()

                .spec(recSpec)
                .body(SchoolLocation)
                .log().body()

                .when()
                .post("/school-service/api/location")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;
    }

    @Test(dependsOnMethods = "createSchoolLocationsNegative")
    public void updateSchoolLocations() {

        SchoolLocation.put("id", SchoolLocationID);
        SchoolLocationName = ("TechnoStudy" + faker.number().digits(5));
        SchoolLocation.put("name", SchoolLocationName);
        SchoolLocation.put("shortName", SchoolLocationShortName);

        given()

                .spec(recSpec)
                .body(SchoolLocation)
                // .log().body()

                .when()
                .put("/school-service/api/location")

                .then()
                .log().body()
                .statusCode(200)
                .body("id", equalTo(SchoolLocationID))
        ;
    }

    @Test(dependsOnMethods = "updateSchoolLocations")
    public void deleteSchoolLocations() {

        given()

                .spec(recSpec)
                .pathParam("SchoolLocationID", SchoolLocationID)
                .log().uri()

                .when()
                .delete("/school-service/api/location/{SchoolLocationID}")

                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    @Test(dependsOnMethods = "deleteSchoolLocations")
    public void deleteSchoolLocationNegative() {
        given()

                .spec(recSpec)
                .pathParam("SchoolLocationID", SchoolLocationID)
                .log().uri()

                .when()
                .delete("/school-service/api/location/{SchoolLocationID}")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("School Location not found"))
        ;
    }
}
