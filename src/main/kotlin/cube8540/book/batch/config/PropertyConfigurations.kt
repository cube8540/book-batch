package cube8540.book.batch.config

import cube8540.book.batch.external.kyobo.kr.KyoboAuthenticationInfo
import cube8540.book.batch.external.naver.com.NaverBookAPIKey
import cube8540.book.batch.external.nl.go.NationalLibraryAPIKey
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "api.authentication")
class AuthenticationProperty(
    val nationalLibrary: NationalLibraryAPIKey,
    val naverBook: NaverBookAPIKey,
    val kyobo: KyoboAuthenticationInfo
)

@ConstructorBinding
@ConfigurationProperties(prefix = "api.connection")
class APIConnectionProperty(
    val maxWaitSecond: Int? = 5,
    val retryCount: Int? = 1,
    val retryDelaySecond: Int? = 5
)