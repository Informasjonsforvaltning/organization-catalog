package no.brreg.informasjonsforvaltning.organizationcatalogue.utils

import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.TestPlan

class TestPlanExecutionListener : TestExecutionListener {

    override fun testPlanExecutionFinished(testPlan: TestPlan?) {

        if (System.getProperty("test.type").contains("contract")) {
            ApiTestContainer.stopGracefully()
        }
    }
}
