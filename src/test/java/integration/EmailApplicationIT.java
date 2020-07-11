package integration;

import com.demo.email.EmailApplication;
import com.demo.email.model.Email;
import com.demo.email.model.EmailStatus;
import com.demo.email.repository.EmailRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = EmailApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(locations="classpath:application-integrationTest.properties")
public class EmailApplicationIT {

    @LocalServerPort
    private int port;

    @Value("${spring.mail.username}")
    private String defaultTestSenderAddress;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmailRepository emailRepository;

    @MockBean
    private JavaMailSender javaMailSender;

    private long id1 = 1l;
    private long id2 = 2l;
    private long id3 = 3l;
    private long id4 = 4l;
    private String sender1 = "sender1@test.com";
    private String sender2 = defaultTestSenderAddress;
    private String recipient11 = "recipient11@test.com";
    private String recipient12 = "recipient12@test.com";
    private String recipient21 = "recipient21@test.com";
    private String subject1 = "test subject";
    private String subject2 = "test subject 2";
    private String message1 = "test email message";
    private String message2 = "test email message 2";


    @Test
    public void createEmailIT() throws Exception {
        HttpEntity<String> request = new HttpEntity<>(buildCreateEmail1Rq(), createHeaders());

        ResponseEntity<String> result = restTemplate.postForEntity(createURL("/emails"), request, String.class);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        String expectedResult = buildCreateEmailExpectedResult();
        JSONAssert.assertEquals(expectedResult, result.getBody(), false);
    }

    @Test
    public void getEmailIT() throws Exception {
        setUpTestData();

        ResponseEntity<String> result = restTemplate.getForEntity(createURL("/emails/1"), String.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        JSONObject expectedEmail = buildGetEmail1ExpectedResult(id1, EmailStatus.PENDING);
        JSONAssert.assertEquals(expectedEmail.toString(), result.getBody(), false);
    }

    @Test
    public void getEmailsIT() throws Exception {
        setUpTestData();

        ResponseEntity<String> result = restTemplate.getForEntity(createURL("/emails"), String.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        JSONArray expectedResult = new JSONArray();
        expectedResult.put(buildGetEmail1ExpectedResult(id1, EmailStatus.PENDING));
        expectedResult.put(buildGetEmail2ExpectedResult(id2, EmailStatus.SENT));
        JSONAssert.assertEquals(expectedResult.toString(), result.getBody(), false);
    }

    @Test
    public void sendPendingEmailsIT() throws Exception {
        setUpTestData();
        HttpEntity<String> request = new HttpEntity<>(createHeaders());

        ResponseEntity<String> result = restTemplate.postForEntity(createURL("/emails/send"), request, String.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        JSONAssert.assertEquals(buildSentIdsExpectedResult(id1), result.getBody(), false);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    public void endToEndScenario() throws Exception {
        HttpEntity<String> request1 = new HttpEntity<>(buildCreateEmail1Rq(), createHeaders());
        ResponseEntity<String> result1 = restTemplate.postForEntity(createURL("/emails"), request1, String.class);
        assertEquals(HttpStatus.CREATED, result1.getStatusCode());

        HttpEntity<String> request2 = new HttpEntity<>(buildCreateEmail2Rq(), createHeaders());
        ResponseEntity<String> result2 = restTemplate.postForEntity(createURL("/emails"), request2, String.class);
        assertEquals(HttpStatus.CREATED, result2.getStatusCode());

        HttpEntity<String> request3 = new HttpEntity<>(buildCreateEmail2Rq(), createHeaders());
        ResponseEntity<String> result3 = restTemplate.postForEntity(createURL("/emails"), request3, String.class);
        assertEquals(HttpStatus.CREATED, result3.getStatusCode());

        ResponseEntity<String> result4 = restTemplate.getForEntity(createURL("/emails"), String.class);
        assertEquals(HttpStatus.OK, result4.getStatusCode());
        JSONArray expectedResult4 = new JSONArray();
        expectedResult4.put(buildGetEmail1ExpectedResult(id1, EmailStatus.PENDING));
        expectedResult4.put(buildGetEmail2ExpectedResult(id2, EmailStatus.PENDING));
        expectedResult4.put(buildGetEmail2ExpectedResult(id3, EmailStatus.PENDING));
        JSONAssert.assertEquals(expectedResult4.toString(), result4.getBody(), false);

        HttpEntity<String> request5 = new HttpEntity<>(createHeaders());
        ResponseEntity<String> result5 = restTemplate.postForEntity(createURL("/emails/send"), request5, String.class);
        assertEquals(HttpStatus.OK, result5.getStatusCode());
        JSONAssert.assertEquals(buildSentIdsExpectedResult(id1, id2, id3), result5.getBody(), false);

        HttpEntity<String> request6 = new HttpEntity<>(buildCreateEmail2Rq(), createHeaders());
        ResponseEntity<String> result6 = restTemplate.postForEntity(createURL("/emails"), request6, String.class);
        assertEquals(HttpStatus.CREATED, result6.getStatusCode());

        ResponseEntity<String> result7 = restTemplate.getForEntity(createURL("/emails"), String.class);
        assertEquals(HttpStatus.OK, result7.getStatusCode());
        JSONArray expectedResult7 = new JSONArray();
        expectedResult7.put(buildGetEmail1ExpectedResult(id1, EmailStatus.SENT));
        expectedResult7.put(buildGetEmail2ExpectedResult(id2, EmailStatus.SENT));
        expectedResult7.put(buildGetEmail2ExpectedResult(id3, EmailStatus.SENT));
        expectedResult7.put(buildGetEmail2ExpectedResult(id4, EmailStatus.PENDING));
        JSONAssert.assertEquals(expectedResult7.toString(), result7.getBody(), false);

        HttpEntity<String> request8 = new HttpEntity<>(createHeaders());
        ResponseEntity<String> result8 = restTemplate.postForEntity(createURL("/emails/send"), request8, String.class);
        assertEquals(HttpStatus.OK, result8.getStatusCode());
        JSONAssert.assertEquals(buildSentIdsExpectedResult(id4), result8.getBody(), false);

        ResponseEntity<String> result9 = restTemplate.getForEntity(createURL("/emails"), String.class);
        assertEquals(HttpStatus.OK, result9.getStatusCode());
        JSONArray expectedResult9 = new JSONArray();
        expectedResult9.put(buildGetEmail1ExpectedResult(id1, EmailStatus.SENT));
        expectedResult9.put(buildGetEmail2ExpectedResult(id2, EmailStatus.SENT));
        expectedResult9.put(buildGetEmail2ExpectedResult(id3, EmailStatus.SENT));
        expectedResult9.put(buildGetEmail2ExpectedResult(id4, EmailStatus.SENT));
        JSONAssert.assertEquals(expectedResult9.toString(), result9.getBody(), false);

        HttpEntity<String> request10 = new HttpEntity<>(createHeaders());
        ResponseEntity<String> result10 = restTemplate.postForEntity(createURL("/emails/send"), request10, String.class);
        assertEquals(HttpStatus.NOT_FOUND, result10.getStatusCode());
    }

    private void setUpTestData() {
        Email email1 = Email.builder()
                .id(id1)
                .sender(sender1)
                .recipients(Set.of(recipient11, recipient12))
                .subject(subject1)
                .message(message1)
                .build();
        Email email2 = Email.builder()
                .id(id2)
                .sender(sender2)
                .recipients(Set.of(recipient21))
                .subject(subject2)
                .message(message2)
                .status(EmailStatus.SENT)
                .build();
        emailRepository.save(email1);
        emailRepository.save(email2);
    }

    private String createURL(String url) {
        return "http://localhost:" + port + url;
    }

    private String buildCreateEmail1Rq() throws JSONException {
        JSONObject emailRqJsonObject = new JSONObject();
        emailRqJsonObject.put("sender", sender1);
        JSONArray recipientsArray = new JSONArray();
        recipientsArray.put(recipient11);
        recipientsArray.put(recipient12);
        emailRqJsonObject.put("recipients", recipientsArray);
        emailRqJsonObject.put("subject", subject1);
        emailRqJsonObject.put("message", message1);
        return emailRqJsonObject.toString();
    }

    private String buildCreateEmail2Rq() throws JSONException {
        JSONObject emailRqJsonObject = new JSONObject();
        JSONArray recipientsArray = new JSONArray();
        recipientsArray.put(recipient21);
        emailRqJsonObject.put("recipients", recipientsArray);
        emailRqJsonObject.put("subject", subject2);
        emailRqJsonObject.put("message", message2);
        return emailRqJsonObject.toString();
    }

    private String buildCreateEmailExpectedResult() throws JSONException {
        JSONObject expectedResult = new JSONObject();
        expectedResult.put("id", id1);
        expectedResult.put("sender", sender1);
        JSONArray expectedRecipientsArray = new JSONArray();
        expectedRecipientsArray.put(recipient11);
        expectedRecipientsArray.put(recipient12);
        expectedResult.put("recipients", expectedRecipientsArray);
        expectedResult.put("subject", subject1);
        expectedResult.put("message", message1);
        expectedResult.put("status", EmailStatus.PENDING.toString());
        return expectedResult.toString();
    }

    private JSONObject buildGetEmail1ExpectedResult(long id, EmailStatus expectedStatus) throws JSONException {
        JSONObject expectedEmail = new JSONObject();
        expectedEmail.put("id", id);
        expectedEmail.put("sender", sender1);
        JSONArray expectedRecipientsArray = new JSONArray();
        expectedRecipientsArray.put(recipient11);
        expectedRecipientsArray.put(recipient12);
        expectedEmail.put("recipients", expectedRecipientsArray);
        expectedEmail.put("subject", subject1);
        expectedEmail.put("message", message1);
        expectedEmail.put("status", expectedStatus.toString());
        return expectedEmail;
    }

    private JSONObject buildGetEmail2ExpectedResult(long id, EmailStatus expectedStatus) throws JSONException {
        JSONObject expectedEmail2 = new JSONObject();
        expectedEmail2.put("id", id);
        expectedEmail2.put("sender", sender2);
        JSONArray expectedRecipients2 = new JSONArray();
        expectedRecipients2.put(recipient21);
        expectedEmail2.put("recipients", expectedRecipients2);
        expectedEmail2.put("subject", subject2);
        expectedEmail2.put("message", message2);
        expectedEmail2.put("status", expectedStatus.toString());
        return expectedEmail2;
    }

    private String buildSentIdsExpectedResult(long ... ids) throws JSONException {
        JSONArray expectedIds = new JSONArray();
        for (long id : ids) {
            expectedIds.put(id);
        }
        JSONObject expectedResult = new JSONObject();
        expectedResult.put("sentEmailIds", expectedIds);
        return expectedResult.toString();
    }
}
