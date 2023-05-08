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

public class Cam12_DiscountsTests {
    Faker faker = new Faker();
    String discountID;
    String discountDescription;
    String discountCode;
    RequestSpecification recSpec;
    Map<String, String> discount;

    @BeforeClass
    public void Login() {

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
    public void createDiscounts() {
        discount = new HashMap<>();
        discountDescription = faker.nation().nationality() + faker.number().digits(5);
        discountCode = faker.code().asin() + faker.number().digits(5);

        discount.put("description", discountDescription);
        discount.put("code", discountCode);

        discountID =
                given()

                        .spec(recSpec)
                        .body(discount)
                        .log().body()

                        .when()
                        .post("/school-service/api/discounts")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");


    }

    @Test(dependsOnMethods = "createDiscounts")
    public void createDiscountsNegative() {
        discount.put("description", discountDescription);
        discount.put("code", discountCode);


        given()

                .spec(recSpec)
                .body(discount)
                .log().body()

                .when()
                .post("/school-service/api/discounts")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"));


    }

    @Test(dependsOnMethods = "createDiscountsNegative")
    public void updateDiscounts() {
        discount.put("id", discountID);

        discountDescription = ("TechnoStudy-" + faker.number().digits(5));
        discount.put("description", discountDescription);
        discount.put("code", discountCode);

        given()

                .spec(recSpec)
                .body(discount)
                // .log().body()

                .when()
                .put("/school-service/api/discounts")

                .then()
                .log().body()
                .statusCode(200)
                .body("description", equalTo(discountDescription))
        ;

    }

    @Test(dependsOnMethods = "updateDiscounts")
    public void deleteDiscounts() {
        given()

                .spec(recSpec)
                .pathParam("discountID", discountID)
                .log().uri()

                .when()
                .delete("/school-service/api/discounts/{discountID}")

                .then()
                .log().body()
                .statusCode(200)
        ;


    }

    @Test(dependsOnMethods = "deleteDiscounts")
    public void deleteDiscountsNegative() {
        given()

                .spec(recSpec)
                .pathParam("discountID", discountID)
                .log().uri()

                .when()
                .delete("/school-service/api/discounts/{discountID}")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("Discount not found"))
        ;

    }
}
