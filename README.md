# TencentCloudNetUsageGuard

轮询腾讯云轻量服务器的流量包使用情况，借助企业微信机器人推送通知，并在流量使用达到阙值时强制关机。

## Build

`clone`项目后，使用`gradle`可以打包`.jar`文件以部署:

```shell
git clone git@github.com:lugf027/tencentCloudNetUsageGuard.git
cd tencentCloudNetUsageGuard
./gradlew clean build
```

注意运行时，需要参考`.env.example`进行环境配置。

网络不佳时，修改gradle https://mirrors.cloud.tencent.com/gradle/gradle-7.4.2-bin.zip

## KeyPoint

### TencentCloud SDK API

* [https://mvnrepository.com/artifact/com.tencentcloudapi/tencentcloud-sdk-java](https://mvnrepository.com/artifact/com.tencentcloudapi/tencentcloud-sdk-java)
* [https://console.cloud.tencent.com/api/explorer?Product=lighthouse&Version=2020-03-24&Action=StopInstances](https://console.cloud.tencent.com/api/explorer?Product=lighthouse&Version=2020-03-24&Action=StopInstances)

```kotlin
// 查询各个实例的网络使用情况
val netConfigResp = client.DescribeInstancesTrafficPackages(DescribeInstancesTrafficPackagesRequest())
netConfigResp.instanceTrafficPackageSet.forEach { instanceNetPackage ->
    instanceNetPackage.trafficPackageSet.forEach { netPackage ->
        val usedPercent = netPackage.trafficUsed / netPackage.trafficPackageTotal.toFloat()
        logger.info(
            "currentInstance id:${instanceNetPackage.instanceId} usedPercent:${usedPercent}" +
                "total:${netPackage.trafficPackageTotal / SIZE_1_GB}GB " +
                "used:${netPackage.trafficUsed / SIZE_1_MB}MB " +
                "remain:${netPackage.trafficPackageRemaining / SIZE_1_MB}MB " +
                "overUse:${netPackage.trafficOverflow / SIZE_1_GB}GB "
        )
        // 实例的流量包使用百分比大于阙值，直接关机
        if (usedPercent > TCUtils.netUsageMaxPercent) {
            val stopReq = StopInstancesRequest()
            stopReq.instanceIds = listOf(instanceNetPackage.instanceId).toTypedArray()
            val stopResp = client.StopInstances(stopReq)
            logger.info("StopInstances stopResp:${stopResp.toJsonStr()}")
        }
    }
}
```

### Scheduler

* [https://mvnrepository.com/artifact/org.quartz-scheduler/quartz](https://mvnrepository.com/artifact/org.quartz-scheduler/quartz)
* [https://github.com/quartz-scheduler/quartz](https://github.com/quartz-scheduler/quartz)

定义拓展方法：

```kotlin
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
```

使用如下，其中`TCLightHouseJob`是具体要执行的任务类。

```kotlin
val scheduler = StdSchedulerFactory.getDefaultScheduler()
scheduler.start()
scheduler.scheduleJob(
    TCLightHouseJob::class.get("LightHouseJob", "TC"),
    getTrigger("LightHouseJobTrigger", "TC") { it.withIntervalInMinutes(30).repeatForever() },
)
```

### EnverimentParams

* [https://github.com/cdimascio/dotenv-java](https://github.com/cdimascio/dotenv-java)

```kotlin
/**
 * 获取配置项，从 .env 文件里读
 */
object MyConfig {
    const val TENCENT_CLOUD_SDK_SECRET_ID = "TENCENT_CLOUD_SDK_SECRET_ID"
    const val TENCENT_CLOUD_SDK_SECRET_KEY = "TENCENT_CLOUD_SDK_SECRET_KEY"
    const val TENCENT_CLOUD_NET_USAGE_PERCENT = "TENCENT_CLOUD_NET_USAGE_PERCENT"
    const val WE_WORK_ROBOT_WEBHOOK = "WE_WORK_ROBOT_WEBHOOK"

    private val mDotenv by lazy { Dotenv.load() }

    fun getConfig(key: String): String = mDotenv.get(key, "").trim()
}

fun String.getDotEnv() = MyConfig.getConfig(this)
```

### Notify

* 发起`Post`请求即可，略。