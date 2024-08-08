package tencentcloud.lighthouse

import com.tencentcloudapi.common.profile.ClientProfile
import com.tencentcloudapi.common.profile.HttpProfile
import com.tencentcloudapi.lighthouse.v20200324.models.*
import com.tencentcloudapi.lighthouse.v20200324.LighthouseClient
import notice.NoticeManager
import notice.impl.INoticeService
import org.slf4j.LoggerFactory
import tencentcloud.utils.TCConstants
import tencentcloud.utils.TCConstants.SIZE_1_GB
import tencentcloud.utils.TCConstants.SIZE_1_MB
import tencentcloud.utils.TCUtils
import tencentcloud.utils.toJsonStr
import utils.MyUtils

/**
 * 轻量云服务器
 * see@ https://console.cloud.tencent.com/api/explorer?Product=lighthouse&Version=2020-03-24&Action=StopInstances
 */
object TCLightHouseHelper {
    private const val ENDPOINT = "lighthouse.tencentcloudapi.com"

    private val logger = LoggerFactory.getLogger(TCLightHouseHelper::class.java)

    private val clientProfile by lazy { createClientProfile() }

    /**
     * 查询轻量云服务器流量使用情况，必要时关机
     */
    fun stopInstanceIfNeed(seqId: String) {
        logger.info("queryNetUsage seqId:$seqId netUsageMaxPercent:${TCUtils.netUsageMaxPercent}")
        val client = LighthouseClient(TCUtils.credential, TCConstants.REGION_GUANGZHOU, clientProfile)
        // 简单查询一下所有的实例
        val allInstanceResp: DescribeInstancesResponse = client.DescribeInstances(DescribeInstancesRequest())
        allInstanceResp.instanceSet.forEach { instance ->
            logger.info("currentInstance id:${instance.instanceId} name:${instance.instanceName} os:${instance.osName}")
        }

        // 查询各个实例的网络使用情况
        val netConfigResp = client.DescribeInstancesTrafficPackages(DescribeInstancesTrafficPackagesRequest())
        netConfigResp.instanceTrafficPackageSet.forEach { instanceNetPackage ->
            instanceNetPackage.trafficPackageSet.forEach { netPackage ->
                allInstanceResp.instanceSet.find { it.instanceId == instanceNetPackage.instanceId }?.let { instance ->
                    notifyNetUsage(instance, netPackage, seqId)
                }
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
    }

    /**
     * 发送通知
     */
    private fun notifyNetUsage(instance: Instance, netPackage: TrafficPackage, seqId: String) {
        NoticeManager.getNoticeList().forEach { noticeService ->
            kotlin.runCatching {
                notifyNetUsageImpl(instance, netPackage, noticeService, seqId)
            }.onFailure {
                logger.error("notifyNetUsageToAll error ${noticeService.getName()} ${it}\n${it.stackTraceToString()}")
            }
        }
    }

    private fun notifyNetUsageImpl(
        instance: Instance,
        netPackage: TrafficPackage,
        noticeService: INoticeService,
        seqId: String,
    ) {
        val msgSB = StringBuilder()
        msgSB.append(noticeService.genMsgLine(noticeService.getMsgBold("流量包使用通知")))
        msgSB.append(noticeService.genMsgLine("实例ID: ${noticeService.genMsgInfo(instance.instanceId)}"))
        msgSB.append(noticeService.genMsgLine("实例名: ${noticeService.genMsgInfo(instance.instanceName)}"))
        msgSB.append(noticeService.genMsgLine("实例OS: ${noticeService.genMsgInfo(instance.osName)}"))
        val netTotal = "${netPackage.trafficPackageTotal / SIZE_1_GB} GB"
        val netUsed = "${netPackage.trafficUsed / SIZE_1_MB} MB"
        val usedPercent = MyUtils.floatToPercentage(netPackage.trafficUsed / netPackage.trafficPackageTotal.toFloat())
        msgSB.append(noticeService.genMsgLine("流量总量: ${noticeService.genMsgInfo(netTotal)}"))
        msgSB.append(noticeService.genMsgLine("流量使用: ${noticeService.genMsgInfo(netUsed)}"))
        msgSB.append(noticeService.genMsgLine("流量比例: ${noticeService.genMsgWarning(usedPercent)}"))

        noticeService.sendMSg(msgSB.toString(), seqId)
    }


    private fun createClientProfile() = ClientProfile().apply {
        val httpProfile = HttpProfile()
        httpProfile.endpoint = ENDPOINT
        this.httpProfile = httpProfile
    }
}
