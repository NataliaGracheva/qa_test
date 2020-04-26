package test;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.Card;
import data.DbUtils;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import page.CreditPage;
import page.PaymentPage;
import page.StartPage;

import java.sql.SQLException;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;

public class BuyingTripTest {
    Card validCard = new Card("4444 4444 4444 4441", "12", "22", "Card Holder", "123");
    Card declinedCard = new Card("4444 4444 4444 4442", "12", "22", "Card Holder", "123");
    Card fakeCard = new Card("4444 4444 4444 4449", "12", "22", "Card Holder", "123");




    @BeforeEach
    public void openPage() throws SQLException {
        // очистить таблицы
//        DbUtils.clearTables();
        open("http://localhost:8080/");
    }

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Должен подтверждать покупку по карте со статусом APPROVED")
    void shouldConfirmPaymentWithValidCard() throws SQLException {
        DbUtils.clearTables();
        StartPage startPage = new StartPage();
        PaymentPage paymentPage = startPage.goToPaymentPage();
        paymentPage.fillData(validCard);
        paymentPage.notificationOkIsVisible();
        assertEquals("APPROVED", DbUtils.findPaymentStatus());
    }

    @Test
    @DisplayName("Должен подтверждать кредит по карте со статусом APPROVED")
    void shouldConfirmCreditWithValidCard() throws SQLException {
        DbUtils.clearTables();
        StartPage startPage = new StartPage();
        CreditPage creditPage = startPage.goToCreditPage();
        creditPage.fillData(validCard);
        creditPage.notificationOkIsVisible();
        assertEquals("APPROVED", DbUtils.findCreditStatus());
    }

    @Test
    @DisplayName("Не должен подтверждать покупку по карте со статусом DECLINED")
    void shouldNotConfirmPaymentWithDeclinedCard() throws SQLException {
        DbUtils.clearTables();
        StartPage startPage = new StartPage();
        PaymentPage paymentPage = startPage.goToPaymentPage();
        paymentPage.fillData(declinedCard);
        paymentPage.notificationErrorIsVisible();
        assertEquals("DECLINED", DbUtils.findPaymentStatus());
    }

    @Test
    @DisplayName("Не должен подтверждать кредит по карте со статусом DECLINED")
    void shouldNotConfirmCreditWithDeclinedCard() throws SQLException {
        DbUtils.clearTables();
        StartPage startPage = new StartPage();
        CreditPage creditPage = startPage.goToCreditPage();
        creditPage.fillData(declinedCard);
        creditPage.notificationErrorIsVisible();
        assertEquals("DECLINED", DbUtils.findCreditStatus());
    }

    @Test
    @DisplayName("Не должен подтверждать покупку по несуществующей карте")
    void shouldNotConfirmPaymentWithFakeCard() throws SQLException {
        DbUtils.clearTables();
        StartPage startPage = new StartPage();
        PaymentPage paymentPage = startPage.goToPaymentPage();
        paymentPage.fillData(fakeCard);
        paymentPage.notificationErrorIsVisible();
        assertEquals(0, DbUtils.countRecords());
    }

    @Test
    @DisplayName("Не должен подтверждать кредит по несуществующей карте")
    void shouldNotConfirmCreditWithFakeCard() throws SQLException {
        DbUtils.clearTables();
        StartPage startPage = new StartPage();
        CreditPage creditPage = startPage.goToCreditPage();
        creditPage.fillData(fakeCard);
        creditPage.notificationErrorIsVisible();
        assertEquals(0, DbUtils.countRecords());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/incorrectValues.cvs", numLinesToSkip = 1)
    @DisplayName("Должен показывать сообщение об ошибке при заполнении полей невалидными значениями")
    void shouldShowWarningIfValueIsIncorrectForPayment(String number, String month, String year, String owner, String cvc, String warning, String message) {
        Card incorrectValues = new Card(number, month, year, owner, cvc);
        StartPage startPage = new StartPage();
        PaymentPage paymentPage = startPage.goToPaymentPage();
        paymentPage.fillData(incorrectValues);
//        paymentPage.inputInvalidIsVisible();
        assertTrue(paymentPage.inputInvalidIsVisible(), message);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/incorrectValues.cvs", numLinesToSkip = 1)
    @DisplayName("Должен показывать сообщение об ошибке при заполнении полей невалидными значениями")
    void shouldShowWarningIfValueIsIncorrectForCredit(String number, String month, String year, String owner, String cvc, String warning, String message) {
        Card incorrectValues = new Card(number, month, year, owner, cvc);
        StartPage startPage = new StartPage();
        CreditPage creditPage = startPage.goToCreditPage();
        creditPage.fillData(incorrectValues);
//        creditPage.inputInvalidIsVisible();
        assertTrue(creditPage.inputInvalidIsVisible(), message);
    }
}
