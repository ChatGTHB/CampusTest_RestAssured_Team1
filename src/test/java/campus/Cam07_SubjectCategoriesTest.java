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

public class Cam07_SubjectCategoriesTest {
    Faker faker = new Faker();
    String subjectID;
    String subjectName;
    String subjectCode;
    RequestSpecification recSpec;
    Map<String, String> subject;

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
    public void createSubject() {
        subject = new HashMap<>();
        subjectName = faker.country().name() + faker.number().digits(5);
        subjectCode = faker.country().countryCode2() + faker.number().digits(5);

        subject.put("name", subjectName);
        subject.put("code", subjectCode);

        subjectID =
                given()
                        .spec(recSpec)
                        .body(subject)
                        .log().body()

                        .when()
                        .post("/school-service/api/subject-categories")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");
        System.out.println("subjectID = " + subjectID);
    }

    @Test(dependsOnMethods = "createSubject")
    public void createSubjectNegative() {
        subject.put("name", subjectName);
        subject.put("code", subjectCode);


        given()
                .spec(recSpec)
                .body(subject)
                .log().body()

                .when()
                .post("/school-service/api/subject-categories")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))

        ;

        ;

    }

    @Test(dependsOnMethods = "createSubjectNegative")
    public void updateSubject() {
        subject.put("id", subjectID);

        subjectName = ("TechnoStudy" + faker.number().digits(5));
        subject.put("name", subjectName);
        subject.put("code", subjectCode);


        given()
                .spec(recSpec)
                .body(subject)
                // .log().body()

                .when()
                .put("/school-service/api/subject-categories")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(subjectName))

        ;


    }

    @Test(dependsOnMethods = "updateSubject")
    public void deleteSubject() {
        given()
                .spec(recSpec)
                .pathParam("subjectID", subjectID)
                .log().uri()

                .when()
                .delete("/school-service/api/subject-categories/{subjectID}")

                .then()
                .log().body()
                .statusCode(200)


        ;


    }

    @Test(dependsOnMethods = "deleteSubject")
    public void deleteSubjectNegative() {
        given()
                .spec(recSpec)
                .pathParam("subjectID", subjectID)
                .log().uri()

                .when()
                .delete("/school-service/api/subject-categories/{subjectID}")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("SubjectCategory not  found"))


        ;
    }


}
