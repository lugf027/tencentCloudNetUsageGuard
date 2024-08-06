
import tencentcloud.lighthouse.TCLightHouseHelper
import java.util.UUID

fun main() {
    val seqId = UUID.randomUUID().toString()
    TCLightHouseHelper.stopInstanceIfNeed(seqId)
}
