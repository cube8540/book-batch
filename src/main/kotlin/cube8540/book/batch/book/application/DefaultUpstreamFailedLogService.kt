package cube8540.book.batch.book.application

import cube8540.book.batch.book.domain.UpstreamFailedLog
import cube8540.book.batch.book.repository.BookDetailsRepository
import cube8540.book.batch.book.repository.UpstreamFailedLogRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultUpstreamFailedLogService
@Autowired
constructor(
    private val upstreamFailedLogRepository: UpstreamFailedLogRepository,

    private val bookDetailsRepository: BookDetailsRepository
): UpstreamFailedLogService {

    @Transactional
    override fun registerFailedLogs(requests: List<UpstreamFailedLogRegisterRequest>) {
        val failedLogs = requests.map {
            UpstreamFailedLog(bookDetailsRepository.getOne(it.isbn), it.reasons)
        }

        upstreamFailedLogRepository.saveAll(failedLogs)
    }
}