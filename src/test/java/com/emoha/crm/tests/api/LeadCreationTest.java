package com.emoha.crm.tests.api;

import com.emoha.crm.api.LeadApiClient;
import com.emoha.crm.api.LeadApiClient.LeadCreationResult;
import com.emoha.crm.testdata.LeadTestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LeadCreationTest {

    private static final Logger logger =
            LoggerFactory.getLogger(LeadCreationTest.class);

    @Test
    public void leadCanBeCreated() {

        LeadApiClient leadApiClient = new LeadApiClient();
        LeadTestData leadTestData = LeadTestData.defaultLead();

        LeadCreationResult leadCreationResult = leadApiClient.createLead(leadTestData);

        logger.info(
                "Created lead_uuid={} record_id={}",
                leadCreationResult.leadUuid(),
                leadCreationResult.recordId()
        );

        Assert.assertNotNull(
                leadCreationResult.leadUuid(),
                "Create lead response should include lead_uuid"
        );
        Assert.assertTrue(
                leadCreationResult.recordId() > 0,
                "Create lead response should include valid record_id"
        );
    }
}
