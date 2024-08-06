package tencentcloud.utils

import com.tencentcloudapi.common.AbstractModel
import com.tencentcloudapi.common.Credential
import config.MyConfig
import config.getDotEnv

object TCUtils {
    private val secretId by lazy { MyConfig.TENCENT_CLOUD_SDK_SECRET_ID.getDotEnv() }
    private val secretKey by lazy { MyConfig.TENCENT_CLOUD_SDK_SECRET_KEY.getDotEnv() }
    val credential by lazy { Credential(secretId, secretKey) }
    val netUsageMaxPercent by lazy { MyConfig.TENCENT_CLOUD_NET_USAGE_PERCENT.getDotEnv().toFloat() }
}

fun AbstractModel.toJsonStr(): String {
    return AbstractModel.toJsonString(this)
}
