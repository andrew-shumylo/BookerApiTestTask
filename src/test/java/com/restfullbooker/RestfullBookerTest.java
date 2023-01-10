package com.restfullbooker;

import static data.restfullbooker.BookingDataBuilder.getBookingData;
import static data.restfullbooker.BookingDataBuilder.getPartialBookingData;
import static data.restfullbooker.TokenBuilder.getToken;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import data.restfullbooker.BookingData;
import data.restfullbooker.PartialBookingData;
import data.restfullbooker.TokenCreds;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class RestfullBookerTest extends BaseSetup
{
    private BookingData newBooking;
    private BookingData updatedBooking;
    private PartialBookingData partialUpdateBooking;
    private TokenCreds tokenCreds;
    private int bookingId;

    @BeforeTest
    public void testSetup () {
        newBooking = getBookingData ();
        updatedBooking = getBookingData ();
        partialUpdateBooking = getPartialBookingData ();
        tokenCreds = getToken ();
    }
    private String generateToken ()
    {
        return given ().body (tokenCreds)
                .when ()
                .post ("/auth")
                .then ()
                .assertThat ()
                .body ("token", is (notNullValue ()))
                .extract ()
                .path ("token");
    }
    @Test
    @Description ("Create New Booking")
    public void createBookingTest () {
        bookingId = given ().body (newBooking)
                .when ()
                .post ("/booking")
                .then ()
                .statusCode (200)
                .and ()
                .assertThat ()
                .body ("bookingid", notNullValue ())
                .body ("booking.firstname",
                        equalTo (newBooking.getFirstname ()),
                        "booking.lastname",
                        equalTo (newBooking.getLastname ()),
                        "booking.totalprice",
                        equalTo (newBooking.getTotalprice ()),
                        "booking.depositpaid",
                        equalTo (newBooking.isDepositpaid ()),
                        "booking.bookingdates.checkin",
                        equalTo (newBooking.getBookingdates ().getCheckin ()),
                        "booking.bookingdates.checkout",
                        equalTo (newBooking.getBookingdates ().getCheckout ()),
                        "booking.additionalneeds",
                        equalTo (newBooking.getAdditionalneeds ()))
                .extract ()
                .path ("bookingid");
    }

    @Test
    @Description ("Get details of newly created booking, and ensure it has all details as were specified during its creation.")
    public void getBookingTest () {
        given ().get ("/booking/" + bookingId)
                .then ()
                .statusCode (200)
                .and ()
                .assertThat ()
                .body ("firstname",
                        equalTo (newBooking.getFirstname ()),
                        "lastname",
                        equalTo (newBooking.getLastname ()),
                        "totalprice",
                        equalTo (newBooking.getTotalprice ()),
                        "depositpaid",
                        equalTo (newBooking.isDepositpaid ()),
                        "bookingdates.checkin",
                        equalTo (newBooking.getBookingdates ().getCheckin ()),
                        "bookingdates.checkout",
                        equalTo (newBooking.getBookingdates ().getCheckout ()),
                        "additionalneeds",
                        equalTo (newBooking.getAdditionalneeds ()));

    }
    @Test
    @Description ("Update the booking details")

    public void updatePartialBookingTest () {
        given ().body (partialUpdateBooking)
                .when ()
                .header ("Cookie", "token=" + generateToken())
                .patch ("/booking/" + bookingId)
                .then ()
                .statusCode (200)
                .and ()
                .assertThat ()
                .body ("firstname",
                        equalTo (partialUpdateBooking.getFirstname ()),
                        "lastname",
                        equalTo (updatedBooking.getLastname ()),
                        "totalprice",
                        equalTo (partialUpdateBooking.getTotalprice ()),
                        "depositpaid",
                        equalTo (updatedBooking.isDepositpaid ()),
                        "bookingdates.checkin",
                        equalTo (updatedBooking.getBookingdates ().getCheckin ()),
                        "bookingdates.checkout",
                        equalTo (updatedBooking.getBookingdates ().getCheckout ()),
                        "additionalneeds",
                        equalTo (updatedBooking.getAdditionalneeds ()));

    }

    @Test
    @Description ("Delete the booking")
    public void deleteBookingTest () {
        given ().header ("Cookie", "token=" + generateToken())
                .when ()
                .delete ("/booking/" + bookingId)
                .then ()
                .statusCode (201);
    }

    @Test
    @Description ("Delete booking (Validation)")
    @Severity (SeverityLevel.NORMAL)
    @Story ("End to End tests using rest-assured")
    @Step ("Check by retrieving deleted booking")
    public void checkBookingIsDeleted () {
        given ().get ("/booking/" + bookingId)
                .then ()
                .statusCode (404);
    }
}
