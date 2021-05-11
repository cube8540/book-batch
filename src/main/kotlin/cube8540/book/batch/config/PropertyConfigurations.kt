package cube8540.book.batch.config

import cube8540.book.batch.infra.kyobo.kr.KyoboAuthenticationInfo
import cube8540.book.batch.infra.naver.com.NaverBookAPIKey
import cube8540.book.batch.infra.nl.go.NationalLibraryAPIKey
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "api-authentication")
class AuthenticationProperty(
    val nationalLibrary: NationalLibraryAPIKey,
    val naverBook: NaverBookAPIKey,
    val kyobo: KyoboAuthenticationInfo
)