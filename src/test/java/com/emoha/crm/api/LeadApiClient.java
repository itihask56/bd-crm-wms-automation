package com.emoha.crm.api;

import com.emoha.crm.testdata.LeadTestData;
import com.emoha.crm.testdata.QuotationSharingTestData;
import com.emoha.crm.utils.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;

public class LeadApiClient {

    private static final String CREATE_LEAD_PATH =
            "/api/v2/responder/cflow-crm/create-lead-record-in-cflow";
    private static final String UPDATE_STAGE_PATH =
            "/api/v2/responder/cflow-crm/update-stage-details-in-workflow";

    private final String baseApiUrl;
    private final String createLeadToken;
    private final String deviceType;

    public LeadApiClient() {

        this.baseApiUrl = configuredOrDefault("apiBaseUrl", "https://api.emoha.com");
        this.createLeadToken = ConfigReader.getProperty("leadCreationAuthToken");
        this.deviceType = configuredOrDefault("apiDeviceType", "mobile");
    }

    public LeadCreationResult createLead(LeadTestData leadTestData) {

        Response response = RestAssured
                .given()
                .baseUri(baseApiUrl)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("authorization", bearerToken(createLeadToken))
                .header("devicetype", deviceType)
                .body(leadTestData.createLeadPayload().toString())
                .post(CREATE_LEAD_PATH)
                .then()
                .extract()
                .response();

        assertSuccessfulResponse(response, "Create lead API");
        int responseCode = response.jsonPath().getInt("code");

        if (responseCode != 200) {
            throw new AssertionError(
                    "Create lead API returned code "
                            + responseCode
                            + " with body: "
                            + response.asString()
            );
        }

        String leadUuid = response.jsonPath().getString("data.lead_uuid");
        String recordId = response.jsonPath().getString("data.record_id");

        if (isBlank(leadUuid) || isBlank(recordId)) {
            throw new AssertionError(
                    "Create lead API response must include lead_uuid and record_id"
            );
        }

        return new LeadCreationResult(leadUuid, Integer.parseInt(recordId));
    }

    public void submitLeadScreening(
            LeadTestData leadTestData,
            LeadCreationResult leadCreationResult
    ) {

        JSONObject payload = new JSONObject()
                .put("stage_name", leadTestData.getStageName())
                .put("record_id", leadCreationResult.recordId())
                .put("status", leadTestData.getScreeningStatus())
                .put("values", new JSONObject(leadTestData.screeningValues()))
                .put("lead_uuid", leadCreationResult.leadUuid());

        Response response = RestAssured
                .given()
                .baseUri(baseApiUrl)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("authorization", bearerToken(ConfigReader.getProperty("leadScreeningAuthToken")))
                .header("devicetype", deviceType)
                .body(payload.toString())
                .post(UPDATE_STAGE_PATH)
                .then()
                .extract()
                .response();

        assertSuccessfulResponse(response, "Lead screening API");
        int responseCode = response.jsonPath().getInt("code");

        if (responseCode != 200) {
            throw new AssertionError(
                    "Lead screening API returned code "
                            + responseCode
                            + " with body: "
                            + response.asString()
            );
        }
    }

    public void submitQuotationSharingNew(
            QuotationSharingTestData quotationSharingTestData,
            LeadCreationResult leadCreationResult,
            int cflowRecordId,
            String serviceStartDate
    ) {

        submitQuotationSharing(
                quotationSharingTestData,
                leadCreationResult,
                cflowRecordId,
                serviceStartDate,
                "New",
                new JSONObject(quotationSharingTestData.newValues())
        );
    }

    public void submitQuotationSharingCompleted(
            QuotationSharingTestData quotationSharingTestData,
            LeadCreationResult leadCreationResult,
            int cflowRecordId,
            String serviceStartDate
    ) {

        submitQuotationSharing(
                quotationSharingTestData,
                leadCreationResult,
                cflowRecordId,
                serviceStartDate,
                "Completed",
                new JSONObject(quotationSharingTestData.completedValues())
        );
    }

    private void submitQuotationSharing(
            QuotationSharingTestData quotationSharingTestData,
            LeadCreationResult leadCreationResult,
            int cflowRecordId,
            String serviceStartDate,
            String status,
            JSONObject values
    ) {

        JSONObject payload = new JSONObject()
                .put("stage_name", quotationSharingTestData.getStageName())
                .put("record_id", cflowRecordId)
                .put("status", status)
                .put("lead_uuid", leadCreationResult.leadUuid())
                .put("service_start_date", serviceStartDate)
                .put("values", values);

        Response response = RestAssured
                .given()
                .baseUri(baseApiUrl)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("authorization", bearerToken(quotationSharingToken()))
                .header("devicetype", deviceType)
                .body(payload.toString())
                .post(UPDATE_STAGE_PATH)
                .then()
                .extract()
                .response();

        assertSuccessfulResponse(response, "Quotation sharing " + status + " API");
        int responseCode = response.jsonPath().getInt("code");

        if (responseCode != 200) {
            throw new AssertionError(
                    "Quotation sharing "
                            + status
                            + " API returned code "
                            + responseCode
                            + " with body: "
                            + response.asString()
            );
        }
    }

    private void assertSuccessfulResponse(Response response, String apiName) {

        if (response.statusCode() != 200) {
            throw new AssertionError(
                    apiName
                            + " returned HTTP status "
                            + response.statusCode()
                            + " with body: "
                            + response.asString()
            );
        }
    }

    private String configuredOrDefault(String key, String defaultValue) {

        String value = ConfigReader.getOptionalProperty(key);

        if (isBlank(value)) {
            return defaultValue;
        }

        return value;
    }

    private String bearerToken(String token) {

        if (token.trim().regionMatches(true, 0, "Bearer ", 0, "Bearer ".length())) {
            return token.trim();
        }

        return "Bearer " + token.trim();
    }

    private String quotationSharingToken() {

        String token = ConfigReader.getOptionalProperty("quotationSharingAuthToken");

        if (isBlank(token)) {
            return ConfigReader.getProperty("leadScreeningAuthToken");
        }

        return token;
    }

    private boolean isBlank(String value) {

        return value == null || value.trim().isEmpty();
    }

    public record LeadCreationResult(String leadUuid, int recordId) {
    }
}
