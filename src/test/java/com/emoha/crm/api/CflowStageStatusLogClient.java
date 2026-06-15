package com.emoha.crm.api;

import com.emoha.crm.utils.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CflowStageStatusLogClient {

    private static final DateTimeFormatter LOG_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter API_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final String DEFAULT_USER_AGENT =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) "
                    + "AppleWebKit/537.36 (KHTML, like Gecko) "
                    + "Chrome/125.0 Safari/537.36";

    private final String stageStatusLogUrl;
    private final String userAgent;
    private final String rangeHeader;
    private final Duration pollTimeout;
    private final Duration pollInterval;

    public CflowStageStatusLogClient() {

        this.stageStatusLogUrl = configuredOrDefault(
                "cflowStageStatusLogUrl",
                "https://api.emoha.com/public/reporting/stage-status-logs.txt"
        );
        this.userAgent = configuredOrDefault("cflowStageStatusLogUserAgent", DEFAULT_USER_AGENT);
        this.rangeHeader = "bytes=-" + configuredOrDefault("cflowStageStatusLogTailBytes", "2000000");
        this.pollTimeout = Duration.ofSeconds(
                Integer.parseInt(configuredOrDefault("cflowStageStatusPollTimeoutSeconds", "3600"))
        );
        this.pollInterval = Duration.ofSeconds(
                Integer.parseInt(configuredOrDefault("cflowStageStatusPollIntervalSeconds", "10"))
        );
    }

    public NursingAssessmentEvent waitForNursingAssessmentCompleted(int recordId) {

        long deadline = System.currentTimeMillis() + pollTimeout.toMillis();
        AssertionError lastFailure = null;

        while (System.currentTimeMillis() <= deadline) {
            try {
                NursingAssessmentEvent event = findNursingAssessmentCompleted(recordId);

                if (event != null) {
                    return event;
                }
            } catch (AssertionError e) {
                lastFailure = e;
            }

            sleepUntilNextPoll();
        }

        String message = "Timed out after "
                + pollTimeout.toSeconds()
                + " seconds waiting for Nursing Assessment completion for record_id="
                + recordId
                + ". The client polls the latest log tail using Range: "
                + rangeHeader;

        if (lastFailure != null) {
            message += ". Last failure: " + lastFailure.getMessage();
        }

        throw new AssertionError(message);
    }

    private NursingAssessmentEvent findNursingAssessmentCompleted(int recordId) {

        Response response = RestAssured
                .given()
                .accept("*/*")
                .header("User-Agent", userAgent)
                .header("Range", rangeHeader)
                .get(stageStatusLogUrl)
                .then()
                .extract()
                .response();

        if (response.statusCode() != 200 && response.statusCode() != 206) {
            throw new AssertionError(
                    "Cflow stage status log returned HTTP status "
                            + response.statusCode()
                            + " with body: "
                            + response.asString()
            );
        }

        String responseBody = response.asString();

        if (responseBody.contains("Bots Not Allowed")) {
            throw new AssertionError(
                    "Cflow stage status log blocked the request with 'Bots Not Allowed'. "
                            + "Check cflowStageStatusLogUserAgent configuration."
            );
        }

        for (JSONObject payload : extractStageStatusPayloads(responseBody)) {
            NursingAssessmentEvent event = NursingAssessmentEvent.from(payload);

            if (event.matchesCompletedAssessment(recordId)) {
                return event;
            }
        }

        return null;
    }

    private List<JSONObject> extractStageStatusPayloads(String logText) {

        List<JSONObject> payloads = new ArrayList<>();
        String marker = "staging :";
        int searchFrom = 0;

        while (searchFrom < logText.length()) {
            int markerIndex = logText.indexOf(marker, searchFrom);

            if (markerIndex < 0) {
                break;
            }

            int jsonStart = logText.indexOf('{', markerIndex + marker.length());

            if (jsonStart < 0) {
                break;
            }

            int jsonEnd = findJsonObjectEnd(logText, jsonStart);

            if (jsonEnd < 0) {
                break;
            }

            try {
                payloads.add(new JSONObject(logText.substring(jsonStart, jsonEnd + 1)));
            } catch (JSONException ignored) {
                // Ignore malformed historical log entries and keep scanning.
            }

            searchFrom = jsonEnd + 1;
        }

        return payloads;
    }

    private int findJsonObjectEnd(String text, int jsonStart) {

        int depth = 0;
        boolean inString = false;
        boolean escaping = false;

        for (int i = jsonStart; i < text.length(); i++) {
            char current = text.charAt(i);

            if (escaping) {
                escaping = false;
                continue;
            }

            if (current == '\\') {
                escaping = inString;
                continue;
            }

            if (current == '"') {
                inString = !inString;
                continue;
            }

            if (inString) {
                continue;
            }

            if (current == '{') {
                depth++;
            } else if (current == '}') {
                depth--;

                if (depth == 0) {
                    return i;
                }
            }
        }

        return -1;
    }

    private void sleepUntilNextPoll() {

        try {
            Thread.sleep(pollInterval.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Interrupted while waiting for Cflow status log", e);
        }
    }

    private String configuredOrDefault(String key, String defaultValue) {

        String value = ConfigReader.getOptionalProperty(key);

        if (isBlank(value)) {
            return defaultValue;
        }

        return value;
    }

    private boolean isBlank(String value) {

        return value == null || value.trim().isEmpty();
    }

    public record NursingAssessmentEvent(
            int recordId,
            String wmsLeadId,
            String status,
            String workflow,
            String endpoint,
            String serviceStartDate,
            JSONObject stageData
    ) {

        private static NursingAssessmentEvent from(JSONObject payload) {

            return new NursingAssessmentEvent(
                    parseRecordId(payload.optString("RecordID", "")),
                    payload.optString("WMS LeadID"),
                    payload.optString("Nursing Assessment Status"),
                    payload.optString("workflow"),
                    payload.optString("endpoint"),
                    payload.optString("Service Start Date"),
                    payload.optJSONObject("Stage Data")
            );
        }

        private boolean matchesCompletedAssessment(int expectedRecordId) {

            return recordId > 0
                    && recordId == expectedRecordId
                    && "Completed".equalsIgnoreCase(status)
                    && "Home Care Process".equalsIgnoreCase(workflow)
                    && "/webhook/c-flow/update-status".equals(endpoint);
        }

        public String serviceStartDateForApi(String fallbackDate) {

            if (serviceStartDate == null || serviceStartDate.trim().isEmpty()) {
                return fallbackDate;
            }

            try {
                LocalDate date = LocalDateTime
                        .parse(serviceStartDate, LOG_DATE_TIME_FORMAT)
                        .toLocalDate();

                return date.format(API_DATE_FORMAT);
            } catch (RuntimeException ignored) {
                return fallbackDate;
            }
        }

        private static int parseRecordId(String recordId) {

            if (recordId == null || recordId.trim().isEmpty()) {
                return -1;
            }

            try {
                return Integer.parseInt(recordId.trim());
            } catch (NumberFormatException ignored) {
                return -1;
            }
        }
    }
}
