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

public class Cam02_PositionCategoriesTests {

    Faker faker = new Faker();
    String positionCategoriesID;
    String positionCategoriesName;
    Map<String, String> positionCategories;

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
    public void createPositionCategories() {

        positionCategories = new HashMap<>();

        positionCategoriesName = "Scrum Master - " + faker.number().digits(5);
        positionCategories.put("name", positionCategoriesName);


        positionCategoriesID =

                given()

                        .spec(requestSpecification)
                        .body(positionCategories)
                        .log().body()

                        .when()
                        .post("/school-service/api/position-category")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;

        System.out.println("positionCategoriesID = " + positionCategoriesID);
    }

    @Test(dependsOnMethods = "createPositionCategories")
    public void createPositionCategoriesNegative(){

        given()

                .spec(requestSpecification)
                .body(positionCategories)
                .log().body()

                .when()
                .post("/school-service/api/position-category")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))

                ;

    }

    @Test(dependsOnMethods = "createPositionCategories")
    public void updatePositionCategories(){

        positionCategoriesName = "ProductOwner - " + faker.number().digits(5);

        positionCategories.put("id", positionCategoriesID);
        positionCategories.put("name", positionCategoriesName);

        given()

                .spec(requestSpecification)
                .body(positionCategories)
                // .log().body()

                .when()
                .put("/school-service/api/position-category")

                .then()
                .log().body() // show incoming body as log
                .statusCode(200)
                .body("name", equalTo(positionCategoriesName))
        ;
    }

    @Test(dependsOnMethods = "updatePositionCategories")
    public void deletePositionCategories(){

        given()

                .spec(requestSpecification)
                .log().uri()

                .when()
                .delete("/school-service/api/position-category/" + positionCategoriesID)

                .then()
                .log().body()
                .statusCode(204)
        ;


    }

    @Test(dependsOnMethods = "deletePositionCategories")
    public void deletePositionCategoriesNegative(){

        given()

                .spec(requestSpecification)
                .pathParam("positionCategoriesID", positionCategoriesID)
                .log().uri()

                .when()
                .delete("/school-service/api/position-category/{positionCategoriesID}")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("PositionCategory not  found"))
        ;

    }

}
