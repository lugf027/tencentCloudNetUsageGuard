package config

import io.github.cdimascio.dotenv.Dotenv

/**
 * 获取配置项，从 .env 文件里读
 */
object MyConfig {
    const val TENCENT_CLOUD_SDK_SECRET_ID = "TENCENT_CLOUD_SDK_SECRET_ID"
    const val TENCENT_CLOUD_SDK_SECRET_KEY = "TENCENT_CLOUD_SDK_SECRET_KEY"
    const val TENCENT_CLOUD_NET_USAGE_PERCENT = "TENCENT_CLOUD_NET_USAGE_PERCENT"
    private val mDotenv by lazy { Dotenv.load() }

    fun getConfig(key: String): String = mDotenv.get(key, "").trim()
}

fun String.getDotEnv() = MyConfig.getConfig(this)
