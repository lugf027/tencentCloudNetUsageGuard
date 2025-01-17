package notice.impl

interface INoticeService {
    fun getName(): String

    /**
     * 发送文本消息
     */
    fun sendMSg(msg: String, seqId: String)

    fun genMsgLine(msg: String): String

    fun genMsgWarning(msg: String): String

    fun genMsgComment(msg: String): String

    fun genMsgInfo(msg: String): String

    fun genMsgLink(msg: String, url: String): String

    fun getMsgBold(msg: String): String
}
