package com.emoha.crm.testdata;

import com.github.javafaker.Faker;
import com.emoha.crm.utils.ConfigReader;

import java.util.concurrent.ThreadLocalRandom;

public class CarerTestData {

    private static final Faker faker = new Faker();
    private static final String DEFAULT_AADHAAR_CARD_PATH =
            "src/test/resources/testdata/sample-aadhaar-card.pdf";

    private final String carerName;
    private final String carerNumber;
    private final boolean pseudoNumber;
    private final String carerType;
    private final String category;
    private final String gender;
    private final String age;
    private final String region;
    private final String city;
    private final String serviceExperience;
    private final String specialSkills;
    private final String aadhaarNumber;
    private final String source;
    private final String languagePreference;
    private final String cityPreference;
    private final String photoPath;
    private final String aadhaarCardPath;
    private final String experienceDocumentPath;
    private final String panCardPath;

    private CarerTestData() {

        this.carerName = generatedOrConfigured("carerName", this::generateCarerName);
        this.carerNumber = generatedOrConfigured("carerNumber", this::generateCarerNumber);
        this.pseudoNumber = Boolean.parseBoolean(configuredOrDefault("carerIsPseudoNumber", "false"));
        this.carerType = configuredOrDefault("carerType", "Nurse");
        this.category = configuredOrDefault("carerCategory", "GNM");
        this.gender = configuredOrDefault("carerGender", "Male");
        this.age = configuredOrDefault("carerAge", "34");
        this.region = configuredOrDefault("carerRegion", "South");
        this.city = configuredOrDefault("carerCity", "Mararikulam");
        this.serviceExperience = ConfigReader.getOptionalProperty("carerServiceExperience");
        this.specialSkills = ConfigReader.getOptionalProperty("carerSpecialSkills");
        this.aadhaarNumber = generatedOrConfigured("carerAadhaarNumber", this::generateAadhaarNumber);
        this.source = configuredOrDefault("carerSource", "Direct");
        this.languagePreference = ConfigReader.getOptionalProperty("carerLanguagePreference");
        this.cityPreference = ConfigReader.getOptionalProperty("carerCityPreference");
        this.photoPath = ConfigReader.getOptionalProperty("carerPhotoPath");
        this.aadhaarCardPath = configuredOrDefault("carerAadhaarCardPath", DEFAULT_AADHAAR_CARD_PATH);
        this.experienceDocumentPath = ConfigReader.getOptionalProperty("carerExperienceDocumentPath");
        this.panCardPath = ConfigReader.getOptionalProperty("carerPanCardPath");
    }

    public static CarerTestData defaultCarer() {

        return new CarerTestData();
    }

    public String getCarerName() {

        return carerName;
    }

    public String getCarerNumber() {

        return carerNumber;
    }

    public boolean isPseudoNumber() {

        return pseudoNumber;
    }

    public String getCarerType() {

        return carerType;
    }

    public String getCategory() {

        return category;
    }

    public String getGender() {

        return gender;
    }

    public String getAge() {

        return age;
    }

    public String getRegion() {

        return region;
    }

    public String getCity() {

        return city;
    }

    public String getServiceExperience() {

        return serviceExperience;
    }

    public String getSpecialSkills() {

        return specialSkills;
    }

    public String getAadhaarNumber() {

        return aadhaarNumber;
    }

    public String getSource() {

        return source;
    }

    public String getLanguagePreference() {

        return languagePreference;
    }

    public String getCityPreference() {

        return cityPreference;
    }

    public String getPhotoPath() {

        return photoPath;
    }

    public String getAadhaarCardPath() {

        return aadhaarCardPath;
    }

    public String getExperienceDocumentPath() {

        return experienceDocumentPath;
    }

    public String getPanCardPath() {

        return panCardPath;
    }

    private String configuredOrDefault(String key, String defaultValue) {

        String value = ConfigReader.getOptionalProperty(key);

        if (value == null || value.trim().isEmpty() || "generate".equalsIgnoreCase(value)) {
            return defaultValue;
        }

        return value;
    }

    private String generatedOrConfigured(String key, ValueGenerator generator) {

        String value = ConfigReader.getOptionalProperty(key);

        if (value == null || value.trim().isEmpty() || "generate".equalsIgnoreCase(value)) {
            return generator.generate();
        }

        return value;
    }

    private String generateCarerNumber() {

        return "44" + ThreadLocalRandom.current().nextLong(100_000_00L, 999_999_99L);
    }

    private String generateCarerName() {

        return "test " + faker.name().fullName();
    }

    private String generateAadhaarNumber() {

        return String.valueOf(
                ThreadLocalRandom.current().nextLong(100_000_000_000L, 999_999_999_999L)
        );
    }

    private interface ValueGenerator {

        String generate();
    }
}
