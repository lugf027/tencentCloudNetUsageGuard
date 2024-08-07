import org.quartz.impl.StdSchedulerFactory
import schedule.biz.TCLightHouseJob
import schedule.get
import schedule.getTrigger

fun main() {
    val scheduler = StdSchedulerFactory.getDefaultScheduler()
    scheduler.start()
    scheduler.scheduleJob(
        TCLightHouseJob::class.get("LightHouseJob", "TC"),
        getTrigger("LightHouseJobTrigger", "TC") { it.withIntervalInMinutes(30).repeatForever() },
    )
}
