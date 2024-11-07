package com.google.steps;

import com.google.pages.GoogleFlightsPage;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GoogleFlightsSteps {

    private final WebDriver webDriver;
    private final WebDriverWait wait;
    private final GoogleFlightsPage googleFlightsPage;

    public GoogleFlightsSteps(WebDriver webDriver) {
        this.webDriver = webDriver;
        this.wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        googleFlightsPage = new GoogleFlightsPage(webDriver);
    }

    @Step("Google flight page open")
    public void open() {
        webDriver.navigate().to(GoogleFlightsPage.GOOGLE_FLIGHT_URL);
        webDriver.manage().window().fullscreen();
    }

    @Step("I waiting for the loading bar to disappear")
    public void waitLoadingEnds() {
        try {
            WebElement loadingBarElement = wait.until(ExpectedConditions.visibilityOfElementLocated(googleFlightsPage.getLoadingBar()));
            wait.until(ExpectedConditions.invisibilityOf(loadingBarElement));
        } catch (TimeoutException E) {
            System.out.println("Loading bar was very fast");
        }
    }

    @Step("I enter departure airport {textForSearch}")
    public void enterDepartureAirport(String textForSearch) {
        WebElement fieldFromElement = wait.until(ExpectedConditions.visibilityOfElementLocated(googleFlightsPage.getFieldFrom()));
        fieldFromElement.clear();
        fieldFromElement.sendKeys(textForSearch);
    }


    @Step("I enter arrival airport {textForSearch}")
    public void enterArrivalAirport(String textForSearch) {
        WebElement fieldToElement = wait.until(ExpectedConditions.visibilityOfElementLocated(googleFlightsPage.getFieldTo()));
        fieldToElement.clear();
        fieldToElement.sendKeys(textForSearch);
    }

    @Step("I expand airports lists of cities")
    public void expandCitiesInAirportLists() {
        try {
            List<WebElement> listOfCountriesTogglesElements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(googleFlightsPage.getListOfCountriesToggles()));
            listOfCountriesTogglesElements.forEach(WebElement::click);
        } catch (TimeoutException e) {
            System.out.println("No expandable city lists with airports");
        }
    }

    @Step("I select departure airport by index {number}")
    public void selectDepartureAirportByIndex(int number) {
        expandCitiesInAirportLists();

        List<WebElement> listOfAirportsElements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(googleFlightsPage.getListOfDepartureAirports()));

        listOfAirportsElements.get(number).click();
    }

    @Step("I select arrival airport by index {number}")
    public void setArrivalAirportFromListByNumber(int number) {
        expandCitiesInAirportLists();

        List<WebElement> listOfAirportsElements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(googleFlightsPage.getListOfArrivalAirports()));

        listOfAirportsElements.get(number).click();
    }

    @Step("I open departure calendar")
    public void clickToDepartureDateField() {
        try {
            WebElement fieldDepartureDateElement = wait.until(ExpectedConditions.elementToBeClickable(googleFlightsPage.getFieldDepartureDate()));
            fieldDepartureDateElement.click();
        } catch (ElementClickInterceptedException e) {
            System.out.println("Departure date selected but again throw exception");
        }
    }

    /// Current day index = 0
    @Step("I select departure date in calendar by number of available day = {dayNumber}")
    public void chooseAvailableDepartureDateByIndex(int dayNumber) {

        clickToDepartureDateField();

        List<WebElement> webElements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(googleFlightsPage.getButtonsOfCalendarDates()));

        wait.until(ExpectedConditions.elementToBeClickable(webElements.get(dayNumber)));

        webElements.get(dayNumber).click();

    }

    public enum NumberOfTrips {
        ROUND_TRIP("Round trip"),
        ONE_WAY("One way"),
        MULTI_CITY("Multi-city");

        private final String text;

        NumberOfTrips(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    @Step("I click calendar button Done")
    public void clickDoneInCalendar() {
        webDriver.findElement(googleFlightsPage.getButtonDoneForCalendar()).click();
    }

    @Step("I click button Search")
    public void clickButtonSearch() {
        webDriver.findElement(googleFlightsPage.getButtonSearch()).click();
    }

    @Step("I open stops filter")
    public void openStopsFilter() {
        wait.until(ExpectedConditions.elementToBeClickable(googleFlightsPage.getButtonOfStopsNumberList())).click();
    }

    @Step("I select the trip type {numberOfTrips}")
    public void changeNumberOfTrips(NumberOfTrips numberOfTrips) {
        WebElement tripTypeDropDownListButtonElement = wait.until(ExpectedConditions.elementToBeClickable(googleFlightsPage.getDropDownListNumberOfTripsButton()));

        moveToElement(tripTypeDropDownListButtonElement);

        tripTypeDropDownListButtonElement.click();

        WebElement numberOfTripsElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[contains(@class, 'VfPpkd-OkbHre-SfQLQb-M1Soyc-bN97Pc') and .//span[text()='" + numberOfTrips.toString() + "']]")));

        WebElement oneWayButtonElement = wait.until(ExpectedConditions.visibilityOf(numberOfTripsElement));

        oneWayButtonElement.click();

        wait.until(ExpectedConditions.invisibilityOf(numberOfTripsElement));

    }

    public void moveToElement(WebElement element) {
        Actions actions = new Actions(webDriver);
        actions.moveToElement(element).perform();
    }

    public enum StopNumbers {
        ANU_NUMBER_OF_STOPS("Any number of stops"),
        NONSTOP_ONLY("Nonstop only"),
        ONE_STOP_OR_FEWER("1 stop or fewer"),
        TWO_STOPS_OR_FEWER("2 stops or fewer");

        private final String filterText;

        StopNumbers(String filterText) {
            this.filterText = filterText;
        }

        @Override
        public String toString() {
            return filterText;
        }
    }

    @Step("I select the stops filter")
    public void changeStopsFilter(StopNumbers stopNumbers) {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class, 'm76nmf') and .//*[text()='" + stopNumbers.toString() + "']]"))).click();
    }


    @Step("I close filter list")
    public void closeFilter() {
        wait.until(ExpectedConditions.elementToBeClickable(googleFlightsPage.getButtonCloseForStopsList())).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(googleFlightsPage.getButtonCloseForStopsList()));
        waitLoadingEnds();
    }

    @Step("I get list of flights")
    public List<WebElement> getListOfFlights() {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(googleFlightsPage.getListOfFlights())).stream().filter(WebElement::isDisplayed).toList();
    }

    public String getDepartureAirportIATA(WebElement flight) {
        return flight.findElement(googleFlightsPage.getDepartureAirportIATA()).getText();
    }

    public void assertDepartureAirportIATAofFlight(String departureAirportIATA, WebElement flight) {
        assertEquals(departureAirportIATA, getDepartureAirportIATA(flight));
    }

    @Step("I check all departure airports IATA of flights equals {departureAirportIATA}")
    public void assertDepartureAirportIATAOfFlights(String departureAirportIATA, List<WebElement> flights) {
        assertAll("Departure IATA codes should match",
                flights.stream()
                        .map(flight -> () -> assertDepartureAirportIATAofFlight(departureAirportIATA, flight))
        );
    }

    public String getArrivalAirportIATA(WebElement flight) {
        return flight.findElement(googleFlightsPage.getArriveAirportIATA()).getText();
    }


    public void assertArrivalAirportIATAofFlight(String arrivalAirportIATA, WebElement flight) {
        assertEquals(arrivalAirportIATA, getArrivalAirportIATA(flight));
    }

    @Step("I check all arrival airports IATA of flights equals {arrivalAirportIATA}")
    public void assertArrivalAirportIATAOfFlights(String arrivalAirportIATA, List<WebElement> flights) {
        assertAll("Arrival IATA codes should match",
                flights.stream()
                        .map(flight -> () -> assertArrivalAirportIATAofFlight(arrivalAirportIATA, flight))
        );
    }
}
