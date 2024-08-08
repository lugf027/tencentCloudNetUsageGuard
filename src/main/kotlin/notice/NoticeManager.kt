package notice

import config.MyConfig
import config.getDotEnv
import notice.impl.INoticeService
import notice.impl.WeWorkRobotNoticeImpl

object NoticeManager {
    private val noticePlatformList = mutableListOf<INoticeService>()

    init {
        noticePlatformList.add(WeWorkRobotNoticeImpl(MyConfig.WE_WORK_ROBOT_WEBHOOK.getDotEnv()))
    }

    fun getNoticeList() = noticePlatformList.toList()
}

