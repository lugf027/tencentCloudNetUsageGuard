package schedule.biz

import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import tencentcloud.lighthouse.TCLightHouseHelper
import utils.MyDateTimeUtils
import java.util.UUID

class TCLightHouseJob : Job {
    override fun execute(context: JobExecutionContext?) {
        val seqId = UUID.randomUUID().toString()

        logger.info("execute seqId:${seqId} ${MyDateTimeUtils.currentDateTime()}")
        TCLightHouseHelper.stopInstanceIfNeed(seqId)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TCLightHouseJob::class.java)
    }
}