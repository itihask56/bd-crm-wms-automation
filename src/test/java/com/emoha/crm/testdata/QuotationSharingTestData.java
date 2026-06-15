package com.emoha.crm.testdata;

import com.emoha.crm.utils.ConfigReader;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class QuotationSharingTestData {

    private static final DateTimeFormatter API_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final String stageName;
    private final String serviceStartDate;
    private final Map<String, Object> newValues;
    private final Map<String, Object> completedValues;

    private QuotationSharingTestData() {

        this.stageName = configuredOrDefault("quotationSharingStageName", "Quotation Sharing");
        this.serviceStartDate = configuredOrDefault(
                "quotationServiceStartDate",
                LocalDate.now().plusDays(1).format(API_DATE_FORMAT)
        );
        this.newValues = buildValues("", "", "Yes");
        this.completedValues = buildValues("Accepted", "Accepted", "No");
    }

    public static QuotationSharingTestData defaultQuotation() {

        return new QuotationSharingTestData();
    }

    public String getStageName() {

        return stageName;
    }

    public String getServiceStartDate() {

        return serviceStartDate;
    }

    public Map<String, Object> newValues() {

        return newValues;
    }

    public Map<String, Object> completedValues() {

        return completedValues;
    }

    private Map<String, Object> buildValues(
            String carer1Approval,
            String carer2Approval,
            String shareQuotation
    ) {

        int carer1PricePerDay = intConfiguredOrDefault("quotationCarer1PricePerDay", 100);
        int carer1Days = intConfiguredOrDefault("quotationCarer1NoOfDays", 25);
        int carer1FinalPrice = intConfiguredOrDefault(
                "quotationCarer1FinalPrice",
                carer1PricePerDay * carer1Days
        );
        int carer2PricePerDay = intConfiguredOrDefault("quotationCarer2PricePerDay", 200);
        int carer2Days = intConfiguredOrDefault("quotationCarer2NoOfDays", 25);
        int carer2FinalPrice = intConfiguredOrDefault(
                "quotationCarer2FinalPrice",
                carer2PricePerDay * carer2Days
        );

        Map<String, Object> values = new LinkedHashMap<>();
        values.put(
                "Carer1 Type - Quote",
                configuredOrDefault("quotationCarer1Type", "Attendant")
        );
        values.put(
                "Carer1 Shift - Q",
                configuredOrDefault("quotationCarer1Shift", "Visiting (12 Hrs)")
        );
        values.put("Carer1 Price (Per Day)", carer1PricePerDay);
        values.put("Carer1 No of Days", carer1Days);
        values.put("Carer1 Final Price", carer1FinalPrice);
        values.put("Carer1 Approval", carer1Approval);
        values.put(
                "Carer2 Type - Quote",
                configuredOrDefault("quotationCarer2Type", "Nurse")
        );
        values.put(
                "Carer2 Shift - Q",
                configuredOrDefault("quotationCarer2Shift", "At Home (24 Hrs)")
        );
        values.put("Carer2 Price (Per Day)", carer2PricePerDay);
        values.put("Carer2 No of Days", carer2Days);
        values.put("Carer2 Final Price", carer2FinalPrice);
        values.put("Carer2 Approval", carer2Approval);
        values.put(
                "Total Price (Per Day)",
                intConfiguredOrDefault(
                        "quotationTotalPricePerDay",
                        carer1PricePerDay + carer2PricePerDay
                )
        );
        values.put(
                "Total Final Price",
                intConfiguredOrDefault(
                        "quotationTotalFinalPrice",
                        carer1FinalPrice + carer2FinalPrice
                )
        );
        values.put("Advance Amount", intConfiguredOrDefault("quotationAdvanceAmount", 1000));
        values.put("Share Quotation ", shareQuotation);

        return values;
    }

    private int intConfiguredOrDefault(String key, int defaultValue) {

        String value = ConfigReader.getOptionalProperty(key);

        if (isBlank(value)) {
            return defaultValue;
        }

        return Integer.parseInt(value.trim());
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
}
