package test;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.SQLHelper;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import page.HomePages;

import static com.codeborne.selenide.Selenide.open;
import static data.SQLHelper.cleanDatabase;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentDebitCardTest {
    HomePages homePages;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("Allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setUp() {
        homePages = open("http://localhost:8080/", HomePages.class);
    }

    @AfterEach
    void tearDownAllDatabase() {
        cleanDatabase();
    }

    @Test
    @DisplayName("Дебетовая карта. Успешная оплата с подтвержденной карты (со значением “APPROVED")
    void shouldSuccessfulFromApprovedDebitCard() {
        homePages.chooseBy("Оплата по карте");
        homePages.enteringApprovedCard();
        homePages.enteringValidCardValidityPeriod();
        homePages.enteringValidOwner();
        homePages.enteringValidCVC();
        homePages.verifySuccessfulNotification("Операция одобрена Банком.");
        var actualStatusLastLinePaymentRequestEntity = SQLHelper.getStatusLastLinePaymentRequestEntity();
        var expectedStatus = "APPROVED";
        assertEquals(actualStatusLastLinePaymentRequestEntity, expectedStatus);
    }

    @Test
    @DisplayName("Дебетовая карта. Неудачная оплата с отклоненной карты (со значением “DECLINED.")
    void shouldFailedPayFromApprovedDebitCard() {
        homePages.chooseBy("Оплата по карте");
        homePages.enteringDeclinedCard();
        homePages.enteringValidCardValidityPeriod();
        homePages.enteringValidOwner();
        homePages.enteringValidCVC();
        homePages.verifyErrorNotification("Ошибка! Банк отказал в проведении операции.");
        var actualStatusLastLinePaymentRequestEntity = SQLHelper.getStatusLastLinePaymentRequestEntity();
        var expectedStatus = "DECLINED";
        assertEquals(actualStatusLastLinePaymentRequestEntity, expectedStatus);
    }

    @Test
    @DisplayName("Дебетовая карта. Неудачная оплата картой, которой нет в базе")
    void shouldUnsuccessfulPayFromNonexistenceDebitCard() {
        homePages.chooseBy("Оплата по карте");
        homePages.enteringRandomCard();
        homePages.enteringValidCardValidityPeriod();
        homePages.enteringValidOwner();
        homePages.enteringValidCVC();
        homePages.verifyErrorNotification("Ошибка! Банк отказал в проведении операции.");
    }

    @Test
    @DisplayName("Получение ошибки после ввода букв в поле номер карты.")
    void shouldReturnErrorWhenWriteLettersInCardNumber() {
        homePages.chooseBy("Оплата по карте");
        homePages.enteringInvalidCard();
        homePages.enteringValidCardValidityPeriod();
        homePages.enteringValidOwner();
        homePages.enteringValidCVC();
        homePages.verifySuccessfulNotificationIsNotVisible();
        homePages.verifyErrorCardNumberField("Неверный формат");
    }

    @Test
    @DisplayName("Получение ошибки после ввода карты с истекшим сроком.")
    void shouldReturnErrorWhenCardWithExpiredCardData() {
        homePages.chooseBy("Оплата по карте");
        homePages.enteringApprovedCard();
        homePages.enteringInvalidCardValidityPeriod();
        homePages.enteringValidOwner();
        homePages.enteringValidCVC();
        homePages.verifySuccessfulNotificationIsNotVisible();
        homePages.verifyPeriodErrorYearField("Истёк срок действия карты");
    }

    @Test
    @DisplayName("Получение ошибки после ввода неверных данных карты.")
    void shouldReturnErrorWhenCardWithInvalidCardholder() {
        homePages.chooseBy("Оплата по карте");
        homePages.enteringApprovedCard();
        homePages.enteringValidCardValidityPeriod();
        homePages.enteringInValidOwner();
        homePages.enteringValidCVC();
        homePages.verifySuccessfulNotificationIsNotVisible();
        homePages.verifyErrorOwnerField("Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("Получение ошибки после неверного ввода CVC/CVV.")
    void shouldReturnErrorWhenCardWithInvalidCVC() {
        homePages.chooseBy("Оплата по карте");
        homePages.enteringApprovedCard();
        homePages.enteringValidCardValidityPeriod();
        homePages.enteringValidOwner();
        homePages.enteringInValidCVC();
        homePages.verifySuccessfulNotificationIsNotVisible();
        homePages.verifyErrorCVCField("Неверный формат");
    }

    @Test
    @DisplayName("Получение ошибки после отправки пустой формы.")
    void shouldReturnErrorWhenEmptyForm() {
        homePages.chooseBy("Оплата по карте");
        homePages.verifySuccessfulNotificationIsNotVisible();
        homePages.verifyErrorCardNumberField("Неверный формат");
        homePages.verifyErrorMonthField("Неверный формат");
        homePages.verifyErrorYearField("Неверный формат");
        homePages.verifyErrorOwnerField("Поле обязательно для заполнения");
        homePages.verifyErrorCVCField("Неверный формат");
    }
}
