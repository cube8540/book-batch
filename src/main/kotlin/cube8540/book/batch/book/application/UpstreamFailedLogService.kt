package cube8540.book.batch.book.application

interface UpstreamFailedLogService {
    fun registerFailedLogs(requests: List<UpstreamFailedLogRegisterRequest>)
}