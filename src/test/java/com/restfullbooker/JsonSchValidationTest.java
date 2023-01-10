package com.restfullbooker;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import data.restfullbooker.BookingData;
import data.restfullbooker.PartialBookingData;
import data.restfullbooker.TokenCreds;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.io.InputStream;

import static data.restfullbooker.BookingDataBuilder.getBookingData;
import static data.restfullbooker.BookingDataBuilder.getPartialBookingData;
import static data.restfullbooker.TokenBuilder.getToken;
import static io.restassured.RestAssured.given;

@Epic ("Rest Assured POC - Example Tests")
@Feature ("JSON Schema Validation using rest-assured")
public class JsonSchValidationTest extends BaseSetup
{
    private BookingData newBooking;
    private BookingData updatedBooking;
    private PartialBookingData partialUpdateBooking;
    private TokenCreds tokenCreds;
    private int bookingId;

    @BeforeTest
    public void testSetup ()
    {
        newBooking = getBookingData ();
        updatedBooking = getBookingData ();
        partialUpdateBooking = getPartialBookingData ();
        tokenCreds = getToken ();
    }

    @Test
    @Description ("Example test for checking json schema for new booking - Post request")
    public void testCreateBookingJsonSchema ()
    {

        InputStream createBookingJsonSchema = getClass ().getClassLoader ()
                .getResourceAsStream ("createbookingjsonschema.json");
        bookingId = given ().body (newBooking)
                .when ()
                .post ("/booking")
                .then ()
                .statusCode (200)
                .and ()
                .assertThat ()
                .body (JsonSchemaValidator.matchesJsonSchema (createBookingJsonSchema))
                .and ()
                .extract ()
                .path ("bookingid");
    }

    @Test
    @Description ("Example test for checking json schema after getting a booking - get request")
    public void testGetBookingJsonSchema ()
    {

        InputStream getBookingJsonSchema = getClass ().getClassLoader ()
                .getResourceAsStream ("getbookingjsonschema.json");

        given ().when ()
                .get ("/booking/" + bookingId)
                .then ()
                .statusCode (200)
                .assertThat ()
                .body (JsonSchemaValidator.matchesJsonSchema (getBookingJsonSchema));
    }

    @Test
    @Description ("Example test for checking json schema after updating a booking - Put request")
    public void testUpdateBookingJsonSchema ()
    {
        InputStream updateBookingJsonSchema = getClass ().getClassLoader ()
                .getResourceAsStream ("updatebookingjsonschema.json");

        given ().when ()
                .body (updatedBooking)
                .get ("/booking/" + bookingId)
                .then ()
                .statusCode (200)
                .assertThat ()
                .body (JsonSchemaValidator.matchesJsonSchema (updateBookingJsonSchema));
    }

    @Test
    @Description ("Example test for checking json schema after updating a booking partially - Patch request")
    public void testUpdatePartialBookingJsonSchema ()
    {
        InputStream updatePartialBookingJsonSchema = getClass ().getClassLoader ()
                .getResourceAsStream ("updatepartialbookingjsonschema.json");

        given ().when ()
                .body (partialUpdateBooking)
                .get ("/booking/" + bookingId)
                .then ()
                .statusCode (200)
                .assertThat ()
                .body (JsonSchemaValidator.matchesJsonSchema (updatePartialBookingJsonSchema));
    }

    @Test
    @Description ("Example test for checking json schema for token authentication - Post request")
    public void testCreateJsonSchema ()
    {
        InputStream createTokenJsonSchema = getClass ().getClassLoader ()
                .getResourceAsStream ("createtokenjsonschema.json");

        given ().body (tokenCreds)
                .when ()
                .post ("/auth")
                .then ()
                .statusCode (200)
                .and ()
                .assertThat ()
                .body (JsonSchemaValidator.matchesJsonSchema (createTokenJsonSchema));
    }
}
