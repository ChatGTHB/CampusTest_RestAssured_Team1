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

public class Cam10_BankAccountsTests {
    Faker faker = new Faker();
    String bankAccountID;
    String bankAccountUserName;
    Map<String, String> bankAccount;
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
    public void createBankAccount() {

        bankAccount = new HashMap<>();

        bankAccountUserName = faker.address().firstName() + " " + faker.address().lastName();
        bankAccount.put("name", bankAccountUserName);

        bankAccount.put("iban", "DE" + faker.number().digits(12));
        bankAccount.put("integrationCode", faker.number().digits(4));

        bankAccount.put("currency", "EUR");
        bankAccount.put("schoolId", "6390f3207a3bcb6a7ac977f9");

        bankAccountID =

                given()

                        .spec(requestSpecification)
                        .body(bankAccount)
                        .log().body()

                        .when()
                        .post("/school-service/api/bank-accounts")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;
    }

    @Test(dependsOnMethods = "createBankAccount")
    public void createBankAccountNegative() {

        given()

                .spec(requestSpecification)
                .body(bankAccount)
                .log().body()

                .when()
                .post("/school-service/api/bank-accounts")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;
    }

    @Test(dependsOnMethods = "createBankAccount")
    public void updateBankAccount() {

        bankAccountUserName = faker.address().firstName() + " " + faker.address().lastName() + " " + faker.address().lastName();
        bankAccount.put("name", bankAccountUserName);

        bankAccount.put("iban", "DE" + faker.number().digits(16));
        bankAccount.put("integrationCode", faker.number().digits(8));

        bankAccount.put("currency", "USD");
        bankAccount.put("schoolId", "6390f3207a3bcb6a7ac977f9");
        bankAccount.put("id", bankAccountID);

        given()

                .spec(requestSpecification)
                .body(bankAccount)
                // .log().body()

                .when()
                .put("/school-service/api/bank-accounts")

                .then()
                .log().body() // show incoming body as log
                .statusCode(200)
                .body("name", equalTo(bankAccountUserName))
        ;
    }

    @Test(dependsOnMethods = "updateBankAccount")
    public void deleteBankAccount() {

        given()

                .spec(requestSpecification)
                .log().uri()

                .when()
                .delete("/school-service/api/bank-accounts/" + bankAccountID)

                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    @Test(dependsOnMethods = "deleteBankAccount")
    public void deleteAttestationNegative() {

        given()

                .spec(requestSpecification)
                .log().uri()

                .when()
                .delete("/school-service/api/bank-accounts/" + bankAccountID)

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("must be exist"))
        ;
    }
}
