package schedule

import org.quartz.Job
import org.quartz.JobBuilder
import org.quartz.JobDetail
import org.quartz.SimpleScheduleBuilder
import org.quartz.Trigger
import org.quartz.TriggerBuilder
import kotlin.reflect.KClass

inline fun <reified T> KClass<T>.get(name: String, group: String): JobDetail where T : Job {
    return JobBuilder.newJob(this.java)
        .withIdentity(name, group)
        .build()
}

fun getTrigger(name: String, group: String, scheduleTypeCallback: (SimpleScheduleBuilder) -> Unit): Trigger {
    val scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
    scheduleTypeCallback.invoke(scheduleBuilder)
    return TriggerBuilder.newTrigger()
        .withIdentity(name, group)
        .startNow()
        .withSchedule(scheduleBuilder)
        .build()
}
