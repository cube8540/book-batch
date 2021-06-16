package cube8540.book.batch.book.repository

import cube8540.book.batch.book.domain.UpstreamFailedLog
import org.springframework.data.jpa.repository.JpaRepository

interface UpstreamFailedLogRepository: JpaRepository<UpstreamFailedLog, Long>