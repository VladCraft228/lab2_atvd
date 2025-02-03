package org.example.lab3;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.apache.hc.core5.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class BookingTest {

    private static final String BASE_URL = "https://restful-booker.herokuapp.com";

    private static final int GROUP_NUMBER = 122-22;
    private static final int STUDENT_NUMBER = 4;

    private static final String FIRSTNAME = "Vladyslav";
    private static final String LASTNAME = "Havryliuk SK Group";

    private static final String BOOKING = "/booking";
    private static final String BOOKING_ID = BOOKING + "/{id}";
    private static final String AUTH = "/auth";

    private final Faker faker = new Faker();
    private int bookingId;
    private String token;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.requestSpecification = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        RestAssured.responseSpecification = new ResponseSpecBuilder().build();
    }

    @Test
    public void verifyGetAuthToken() {
        Map<String, String> credentials = Map.of(
                "username", "admin",
                "password", "password123"
        );

        Response response = given()
                .body(credentials)
                .post(AUTH);

        response.then()
                .statusCode(HttpStatus.SC_OK);

        token = response.jsonPath().getString("token");
    }

    @Test(dependsOnMethods = "verifyGetAuthToken")
    public void verifyCreateBooking() {
        Map<String, Object> bookingDates = Map.of(
                "checkin", "2025-02-10",
                "checkout", "2025-02-15"
        );

        Map<String, Object> body = Map.of(
                "firstname", FIRSTNAME,
                "lastname", LASTNAME,
                "totalprice", GROUP_NUMBER + STUDENT_NUMBER,
                "depositpaid", faker.bool().bool(),
                "bookingdates", bookingDates,
                "additionalneeds", faker.food().dish()
        );

        Response response = given()
                .body(body)
                .post(BOOKING);

        response.then()
                .statusCode(HttpStatus.SC_OK);

        bookingId = response.jsonPath().getInt("bookingid");
    }

    @Test(dependsOnMethods = "verifyCreateBooking")
    public void verifyGetBooking() {
        given()
                .pathParam("id", bookingId)
                .get(BOOKING_ID)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("firstname", equalTo(FIRSTNAME))
                .body("lastname", equalTo(LASTNAME))
                .body("totalprice", equalTo(GROUP_NUMBER + STUDENT_NUMBER));
    }

    @Test(dependsOnMethods = "verifyGetBooking")
    public void verifyUpdateBooking() {
        Map<String, Object> bookingDates = Map.of(
                "checkin", "2025-03-01",
                "checkout", "2025-03-10"
        );

        Map<String, Object> body = Map.of(
                "firstname", FIRSTNAME,
                "lastname", LASTNAME,
                "totalprice", GROUP_NUMBER + STUDENT_NUMBER + 100,
                "depositpaid", true,
                "bookingdates", bookingDates,
                "additionalneeds", "Breakfast"
        );

        given()
                .header("Cookie", "token=" + token)
                .pathParam("id", bookingId)
                .body(body)
                .put(BOOKING_ID)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("totalprice", equalTo(GROUP_NUMBER + STUDENT_NUMBER + 100));
    }

    @Test(dependsOnMethods = "verifyUpdateBooking")
    public void verifyDeleteBooking() {
        given()
                .header("Cookie", "token=" + token)
                .pathParam("id", bookingId)
                .delete(BOOKING_ID)
                .then()
                .statusCode(HttpStatus.SC_CREATED);
    }
}
