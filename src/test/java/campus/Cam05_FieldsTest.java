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

public class Cam05_FieldsTest {

    String fieldName;
    String fieldID;
    String fieldCode;
    String newfieldName;
    String newfieldCode;
    Faker faker=new Faker();

    RequestSpecification recSpec;
    Map<String,String> fields=new HashMap<>();

    @BeforeClass
    public void Setup()  {
        baseURI="https://test.mersys.io";

        Map<String,String> userCredential=new HashMap<>();
        userCredential.put("username","turkeyts");
        userCredential.put("password","TechnoStudy123");
        userCredential.put("rememberMe","true");

        Cookies cookies=
                given()
                        .contentType(ContentType.JSON)
                        .body(userCredential)

                        .when()
                        .post("/auth/login")

                        .then()

                        .statusCode(200)
                        .extract().response().getDetailedCookies()
                ;
        recSpec= new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }

    @Test
    public void createFields() {


     fieldName="field-"+faker.number().digits(3);
     fieldCode=faker.number().digits(5);
        fields.put("name",fieldName);
        fields.put("code",fieldCode);
        fields.put("type","STRING");
        fields.put("schoolId","6390f3207a3bcb6a7ac977f9");

        fieldID=
                given()
                        .spec(recSpec)
                        .body(fields)
                        .log().body()

                        .when()
                        .post("/school-service/api/entity-field")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");


    }

    @Test(dependsOnMethods = "createFields")
    public void createFieldsNegative() {

        given()

                .spec(recSpec)
                .body(fields)
                .log().body()

                .when()
                .post("/school-service/api/entity-field")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;
    }
    @Test(dependsOnMethods = "createFields")
    public void updateFields() {

        newfieldName="field-"+faker.number().digits(2);
        newfieldCode=faker.number().digits(3);
        fields.put("name",newfieldName);
        fields.put("code",newfieldCode);
        fields.put("id", fieldID);

        given()
                .spec(recSpec)
                .body(fields)
                .log().body()

                .when()
                .put("/school-service/api/entity-field")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(newfieldName))
        ;
    }

    @Test(dependsOnMethods = "updateFields")
    public void deleteFields()  {

        given()
                .spec(recSpec)
                .log().uri()

                .when()
                .delete("/school-service/api/entity-field/"+ fieldID)

                .then()
                .log().body()
                .statusCode(204)
        ;

    }
    @Test(dependsOnMethods = "deleteFields")
    public void deleteFieldsNegative() {

        given()

                .spec(recSpec)
                .pathParam("fieldID", fieldID)
                .log().uri()

                .when()
                .delete("/school-service/api/attestation/{fieldID}")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("attestation not found"))
        ;
    }
}
