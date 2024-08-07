package notice.impl

import com.google.gson.Gson
import config.MyConfig.WE_WORK_ROBOT_WEBHOOK
import config.getDotEnv
import notice.INotice
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory
import java.net.URLDecoder


class WeWorkNoticeImpl : INotice {

    private val client = OkHttpClient()

    override fun sendMSg(msg: String, seqId: String) {
        logger.info("sendMSg seqId:$seqId msg:$msg")
        val request = Request.Builder()
                .url(WE_WORK_ROBOT_WEBHOOK.getDotEnv())
                .post(Gson().toJson(WechatRobotMarkdownReq.ofMarkDown(msg)).toRequestBody(jsonMediaType))
                .build()
        client.newCall(request).execute().use { response ->
            val respBodyStr = response.body?.string()
            logger.info("sendMSg seqId:$seqId respBodyStr:${respBodyStr}")
        }
    }

    override fun genMsgLine(msg: String) = "$msg\n"

    override fun genMsgWarning(msg: String) = "<font color=\"info\">${msg}</font>"

    override fun genMsgComment(msg: String) = "<font color=\"comment\">${msg}</font>"

    override fun genMsgInfo(msg: String) = "<font color=\"warning\">${msg}</font>"

    override fun genMsgLink(msg: String, url: String): String {
        val urlDecode = URLDecoder.decode(url, "UTF-8")
        return "[$msg]($urlDecode)"
    }

    data class WechatRobotMarkdownReq(
        var chatid: String = "@all_group",
        var markdown: Markdown = Markdown(),
        var msgtype: String = "markdown",
        var post_id: String = "",
        var mentioned_list: String = "@all",
        var visible_to_user: String = ""
    ) {
        data class Markdown(
            var at_short_name: Boolean = false,
            var attachments: List<Attachment> = listOf(),
            var content: String = ""
        )

        data class Attachment(
            var actions: List<Action> = listOf(),
            var callback_id: String = ""
        )

        data class Action(
            var border_color: String = "",
            var name: String = "",
            var replace_text: String = "",
            var text: String = "",
            var text_color: String = "",
            var type: String = "",
            var value: String = ""
        )

        companion object {
            fun ofMarkDown(content: String) = WechatRobotMarkdownReq(
                markdown = Markdown(
                    content = content
                )
            )
        }

    }

    companion object {
        private val logger = LoggerFactory.getLogger(WeWorkNoticeImpl::class.java)

        val jsonMediaType: MediaType = "application/json".toMediaType()
    }
}