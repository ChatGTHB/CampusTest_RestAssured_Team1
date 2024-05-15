package mersys;

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

public class MersysStudentGroupsTests {
    Faker randomGenerator = new Faker();
    String studentGroupID;
    String studentGroupSchoolID = "646cbb07acf2ee0d37c6d984";
    String studentGroupName = randomGenerator.company().name();
    String studentGroupDescription = randomGenerator.company().catchPhrase();
    Map<String, String> studentGroup;
    RequestSpecification requestSpecification;

    @BeforeClass
    public void login() {

        baseURI = "https://test.mersys.io/school-service/api/student-group";

//        Map<String, String> userCredential = new HashMap<>();
//        userCredential.put("username", "turkeyts");
//        userCredential.put("password", "TechnoStudy123");
//        userCredential.put("rememberMe", "true");

//        String userCredential="{\n" +
//                "  \"username\": \"turkeyts\",\n" +
//                "  \"password\": \"TechnoStudy123\",\n" +
//                "  \"rememberMe\": \"true\"\n" +
//                "}";

        Login userCredential = new Login();
        userCredential.setUsername("turkeyts");
        userCredential.setPassword("TechnoStudy123");
        userCredential.setRememberMe("true");

        Cookies cookies =

                given()

                        .contentType(ContentType.JSON).body(userCredential)

                        .when().post("https://test.mersys.io/auth/login")

                        .then()
//                       .log().all()
                        .statusCode(200).extract().response().getDetailedCookies();

        requestSpecification = new RequestSpecBuilder().setContentType(ContentType.JSON).addCookies(cookies).build();
    }

    @Test
    public void createStudentGroup() {

        studentGroup = new HashMap<>();
        studentGroup.put("name", studentGroupName);
        studentGroup.put("description", studentGroupDescription);
        studentGroup.put("schoolId", studentGroupSchoolID);

        studentGroupID =

                given().spec(requestSpecification).body(studentGroup).log().body()

                        .when().post("")

                        .then().log().body().statusCode(201).extract().path("id");

        System.out.println("studentGroupID = " + studentGroupID);
    }

    @Test(dependsOnMethods = "createStudentGroup")
    public void createStudentGroupNegative() {

        given().spec(requestSpecification).body(studentGroup).log().body()

                .when().post("")

                .then().log().body().statusCode(400).body("message", containsString("already"));
    }

    @Test(dependsOnMethods = "createStudentGroup")
    public void editStudentGroup() {

        studentGroup.put("id", studentGroupID);
        studentGroup.put("name", "New " + studentGroupName);
        studentGroup.put("description", studentGroupDescription + randomGenerator.shakespeare());

        given().spec(requestSpecification).body(studentGroup)
                // .log().body()

                .when().put("")

                .then().log().body() // show incoming body as log
                .statusCode(200).body("name", equalTo("New " + studentGroupName));
    }

    @Test(dependsOnMethods = "editStudentGroup")
    public void deleteStudentGroup() {

        given().spec(requestSpecification).log().uri()

                .when().delete(studentGroupID)

                .then().log().body().statusCode(200);
    }

    @Test(dependsOnMethods = "deleteStudentGroup")
    public void deleteStudentGroupNegative() {

        given().spec(requestSpecification).pathParam("studentGroupID", studentGroupID).log().uri()

                .when().delete("{studentGroupID}")

                .then().log().body().statusCode(400).body("message", equalTo("Group with given id does not exist!"));
    }
}
