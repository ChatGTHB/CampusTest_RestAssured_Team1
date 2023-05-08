package campus;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.*;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class Cam04_DocumentTypesTests {

    String documentTypesId;
    RequestSpecification requestSpecification;

    @BeforeClass
    public void login() {

        baseURI = "https://test.mersys.io";

        Map<String, Object> userCredential = new HashMap<>();
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
    public void addDocument() {

        documentTypesId =

                given()

                        .spec(requestSpecification)
                        .body("{\n" +
                                "    \"id\":null,\n" +
                                "    \"name\": \"graduate2\",\n" +
                                "    \"description\": \"\",\n" +
                                "    \"attachmentStages\": [\n" +
                                "        \"STUDENT_REGISTRATION\"\n" +
                                "    ],\n" +
                                "    \"schoolId\": \"6390f3207a3bcb6a7ac977f9\",\n" +
                                "    \"active\": true,\n" +
                                "    \"required\": true,\n" +
                                "    \"translateName\": [],\n" +
                                "    \"useCamera\": false\n" +
                                "}")
                        .log().body()

                        .when()
                        .post("/school-service/api/attachments/create")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;

        System.out.println("documentTypesId = " + documentTypesId);
    }



    @Test(dependsOnMethods = "addDocument")
    public void updateDocument() {


        given()

                .spec(requestSpecification)
                .body("{\n" +
                        "  \"id\": \""+documentTypesId+"\",\n" +
                        "  \"name\": \"entrance examination\",\n" +
                        "  \"description\": \"\",\n" +
                        "  \"attachmentStages\": [\n" +
                        "    \"EMPLOYMENT\"\n" +
                        "  ],\n" +
                        "  \"active\": true,\n" +
                        "  \"required\": true,\n" +
                        "  \"useCamera\": false,\n" +
                        "  \"translateName\": [],\n" +
                        "  \"schoolId\": \"6390f3207a3bcb6a7ac977f9\"\n" +
                        "}")
                // .log().body()

                .when()
                .put("/school-service/api/attachments")

                .then()
                .log().body()
                .statusCode(200)

        ;
    }

    @Test(dependsOnMethods = "updateDocument")
    public void deleteDocument() {

        given()

                .spec(requestSpecification)
                .log().uri()

                .when()
                .delete("/school-service/api/attachments/" + documentTypesId)

                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    @Test(dependsOnMethods = "deleteDocument")
    public void deleteDocumentNegative() {



        given()

                .spec(requestSpecification)
                .log().uri()

                .when()
                .delete("/school-service/api/attachments/" + documentTypesId)

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("Attachment Type not found"))
        ;
    }
}


