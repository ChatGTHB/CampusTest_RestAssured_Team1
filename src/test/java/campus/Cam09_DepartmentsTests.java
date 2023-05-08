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

public class Cam09_DepartmentsTests {
    RequestSpecification recSpec;
    String departmentsId;
    String departmentsName;
    Faker faker = new Faker();
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
                        .log().all()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();


        recSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }

    @Test
    public void createDepartments() {

        Map<String, String> departments = new HashMap<>();

        departmentsName = faker.country().countryCode2() + faker.number().digits(3);
        departments.put("name", departmentsName);
        departments.put("code", faker.number().digits(4));
        departments.put("school", "6390f3207a3bcb6a7ac977f9");

        departmentsId =

                given()

                        .spec(recSpec)
                        .body(departments)
                        .log().body()

                        .when()
                        .post("/school-service/api/department")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;
    }

    @Test(dependsOnMethods = "createDepartments")
    public void createDepartmentsNegative() {

        Map<String, String> departments = new HashMap<>();

        departments.put("name", departmentsName);
        departments.put("code", faker.number().digits(4));
        departments.put("school", "6390f3207a3bcb6a7ac977f9");

        given()

                .spec(recSpec)
                .body(departments)
                .log().body()

                .when()
                .post("/school-service/api/department")

                .then()
                .log().body()
                .statusCode(400)
                .extract().path("id")
        ;
    }

    @Test(dependsOnMethods = "createDepartmentsNegative")
    public void updateParameters() {

        Map<String, String> departments = new HashMap<>();
        departmentsName = "departName" + faker.number().digits(3);
        departments.put("name", departmentsName);
        departments.put("id", departmentsId);
        departments.put("code", faker.number().digits(4));
        departments.put("school", "6390f3207a3bcb6a7ac977f9");


        given()
                .spec(recSpec)
                .body(departments)
                .log().body()

                .when()
                .put("/school-service/api/department")

                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    @Test(dependsOnMethods = "updateParameters")
    public void deleteParameters() {

        given()
                .spec(recSpec)
                .log().uri()

                .when()
                .delete("/school-service/api/department/" + departmentsId)

                .then()
                .log().body()
                .statusCode(204);
    }

    @Test(dependsOnMethods = "deleteParameters")
    public void deleteDepartmentsNegative() {
        given()

                .spec(recSpec)
                .log().uri()

                .when()
                .delete("/school-service/api/department/" + departmentsId)

                .then()
                .log().body()
                .statusCode(204)
        ;
    }
}