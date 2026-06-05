package com.emoha.crm.testdata;

import com.emoha.crm.utils.ConfigReader;
import com.github.javafaker.Faker;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class LeadTestData {

    private static final Faker faker = new Faker();
    private static final String DEFAULT_ASSIGN_TO_NO =
            "ebb54e49-2444-11f1-ae02-0aebc5f0b63b";
    private static final DateTimeFormatter API_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final String elderName;
    private final String elderCountryCode;
    private final String elderContactNumber;
    private final String principalSale;
    private final String elderGender;
    private final String nokName;
    private final String nokCountryCode;
    private final String nokContactNumber;
    private final String leadSource;
    private final String age;
    private final String nameOfReferrer;
    private final String elderRelationshipWithNok;
    private final boolean pseudoElder;
    private final boolean vasEntry;
    private final String stageName;
    private final String screeningStatus;
    private final Map<String, String> screeningValues;

    private LeadTestData() {

        this.elderName = generatedOrConfigured("leadElderName", this::generateElderName);
        this.elderCountryCode = configuredOrDefault("leadElderCountryCode", "+91");
        this.elderContactNumber =
                generatedOrConfigured("leadElderContactNumber", this::generateContactNumber);
        this.principalSale = configuredOrDefault("leadPrincipalSale", "Elder");
        this.elderGender = configuredOrDefault("leadElderGender", "Male");
        this.nokName = ConfigReader.getOptionalProperty("leadNokName");
        this.nokCountryCode = configuredOrDefault("leadNokCountryCode", "+91");
        this.nokContactNumber = ConfigReader.getOptionalProperty("leadNokContactNumber");
        this.leadSource = configuredOrDefault("leadSource", "Walk-in");
        this.age = configuredOrDefault("leadAge", "67");
        this.nameOfReferrer = ConfigReader.getOptionalProperty("leadNameOfReferrer");
        this.elderRelationshipWithNok =
                ConfigReader.getOptionalProperty("leadElderRelationshipWithNok");
        this.pseudoElder = Boolean.parseBoolean(
                configuredOrDefault("leadIsPseudoElder", "false")
        );
        this.vasEntry = Boolean.parseBoolean(configuredOrDefault("leadIsVasEntry", "false"));
        this.stageName = configuredOrDefault("leadScreeningStageName", "Lead Screening");
        this.screeningStatus = configuredOrDefault("leadScreeningStatus", "Qualified");
        this.screeningValues = buildScreeningValues();
    }

    public static LeadTestData defaultLead() {

        return new LeadTestData();
    }

    public JSONObject createLeadPayload() {

        JSONObject payload = new JSONObject()
                .put("elder_name", elderName)
                .put("elder_country_code", elderCountryCode)
                .put("elder_contact_number", elderContactNumber)
                .put("principal_sale", principalSale)
                .put("elder_gender", elderGender)
                .put("nok_name", nokName)
                .put("nok_country_code", nokCountryCode)
                .put("nok_contact_number", nokContactNumber)
                .put("lead_source", leadSource)
                .put("age", age)
                .put("elder_relationship_with_nok", elderRelationshipWithNok)
                .put("is_pseudo_elder", pseudoElder)
                .put("is_vas_entry", vasEntry);

        if (isBlank(nameOfReferrer)) {
            payload.put("name_of_referrer", JSONObject.NULL);
        } else {
            payload.put("name_of_referrer", nameOfReferrer);
        }

        return payload;
    }

    public String getStageName() {

        return stageName;
    }

    public String getScreeningStatus() {

        return screeningStatus;
    }

    public Map<String, String> screeningValues() {

        return screeningValues;
    }

    private Map<String, String> buildScreeningValues() {

        Map<String, String> values = new LinkedHashMap<>();
        String defaultServiceDate = LocalDate.now().plusDays(1).format(API_DATE_FORMAT);

        values.put("Service Required", configuredOrDefault("screeningServiceRequired", "Carer"));
        values.put("Staff Required", configuredOrDefault("screeningStaffRequired", "1"));
        values.put(
                "Service Start Date",
                configuredOrDefault("screeningServiceStartDate", defaultServiceDate)
        );
        values.put(
                "For how long do you need our services?",
                configuredOrDefault("screeningServiceDurationType", "Days")
        );
        values.put("Number Of Days", configuredOrDefault("screeningNumberOfDays", "5"));
        values.put(
                "Any other specific requirement?",
                ConfigReader.getOptionalProperty("screeningSpecificRequirement")
        );
        values.put(
                "Nursing Assessment Type",
                configuredOrDefault(
                        "screeningNursingAssessmentType",
                        "Virtual Nursing Assessment"
                )
        );
        values.put(
                "Nursing Assessment Date",
                configuredOrDefault("screeningNursingAssessmentDate", defaultServiceDate)
        );
        values.put(
                "Nursing Assessment Time slot",
                configuredOrDefault("screeningNursingAssessmentTimeSlot", "01:55 PM")
        );
        values.put(
                "Home Address",
                configuredOrDefault(
                        "screeningHomeAddress",
                        "5V83+F3Q, Peru Baug, Jay Prakash Nagar, Goregaon East, "
                                + "Mumbai, Maharashtra 400063"
                )
        );
        values.put("City", configuredOrDefault("screeningCity", "Mumbai"));
        values.put("State", configuredOrDefault("screeningState", "Maharashtra"));
        values.put("Pincode", configuredOrDefault("screeningPincode", "400063"));
        values.put(
                "Nursing assessment at same adress?",
                configuredOrDefault("screeningNursingAssessmentSameAddress", "yes")
        );
        values.put("Remarks", ConfigReader.getOptionalProperty("screeningRemarks"));
        values.put("Assign to NO", configuredOrDefault("screeningAssignToNo", DEFAULT_ASSIGN_TO_NO));
        values.put("Carer1 type", configuredOrDefault("screeningCarer1Type", "Nurse"));
        values.put("Carer1 Gender", configuredOrDefault("screeningCarer1Gender", "Male"));
        values.put(
                "Service Hours (Carer 1)",
                configuredOrDefault("screeningCarer1ServiceHours", "At Home (24 Hrs)")
        );
        values.put("Carer1 shift", ConfigReader.getOptionalProperty("screeningCarer1Shift"));
        values.put(
                "Carer1 shift timings",
                ConfigReader.getOptionalProperty("screeningCarer1ShiftTimings")
        );

        return values;
    }

    private String configuredOrDefault(String key, String defaultValue) {

        String value = ConfigReader.getOptionalProperty(key);

        if (isBlank(value) || "generate".equalsIgnoreCase(value)) {
            return defaultValue;
        }

        return value;
    }

    private String generatedOrConfigured(String key, ValueGenerator generator) {

        String value = ConfigReader.getOptionalProperty(key);

        if (isBlank(value) || "generate".equalsIgnoreCase(value)) {
            return generator.generate();
        }

        return value;
    }

    private String generateContactNumber() {

        return "44" + ThreadLocalRandom.current().nextLong(100_000_00L, 999_999_99L);
    }

    private String generateElderName() {

        return "test " + faker.name().fullName();
    }

    private boolean isBlank(String value) {

        return value == null || value.trim().isEmpty();
    }

    private interface ValueGenerator {

        String generate();
    }
}
