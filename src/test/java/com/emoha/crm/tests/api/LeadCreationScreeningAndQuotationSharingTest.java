package com.emoha.crm.tests.api;

import com.emoha.crm.api.CflowStageStatusLogClient;
import com.emoha.crm.api.CflowStageStatusLogClient.NursingAssessmentEvent;
import com.emoha.crm.api.LeadApiClient;
import com.emoha.crm.api.LeadApiClient.LeadCreationResult;
import com.emoha.crm.testdata.LeadTestData;
import com.emoha.crm.testdata.QuotationSharingTestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LeadCreationScreeningAndQuotationSharingTest {

    private static final Logger logger =
            LoggerFactory.getLogger(LeadCreationScreeningAndQuotationSharingTest.class);

    @Test
    public void quotationSharingCanBeCompletedAfterNursingAssessment() {

        LeadApiClient leadApiClient = new LeadApiClient();
        CflowStageStatusLogClient cflowStageStatusLogClient =
                new CflowStageStatusLogClient();
        LeadTestData leadTestData = LeadTestData.defaultLead();
        QuotationSharingTestData quotationSharingTestData =
                QuotationSharingTestData.defaultQuotation();

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

        leadApiClient.submitLeadScreening(leadTestData, leadCreationResult);
        logger.info(
                "Lead screening submitted for record_id={}. Complete Nursing Assessment in Cflow.",
                leadCreationResult.recordId()
        );

        NursingAssessmentEvent nursingAssessmentEvent =
                cflowStageStatusLogClient.waitForNursingAssessmentCompleted(
                        leadCreationResult.recordId()
                );
        String serviceStartDate = nursingAssessmentEvent.serviceStartDateForApi(
                quotationSharingTestData.getServiceStartDate()
        );

        logger.info(
                "Nursing Assessment completed for record_id={} wms_lead_id={}",
                nursingAssessmentEvent.recordId(),
                nursingAssessmentEvent.wmsLeadId()
        );

        leadApiClient.submitQuotationSharingNew(
                quotationSharingTestData,
                leadCreationResult,
                nursingAssessmentEvent.recordId(),
                serviceStartDate
        );
        leadApiClient.submitQuotationSharingCompleted(
                quotationSharingTestData,
                leadCreationResult,
                nursingAssessmentEvent.recordId(),
                serviceStartDate
        );
    }
}
