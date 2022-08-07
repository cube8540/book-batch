package cube8540.book.batch.translator.store

import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager

interface OAuth2Provider {

    fun getAuthenticationToken(serviceName: String): String?

}

class DefaultOAuth2Provider(private val manager: OAuth2AuthorizedClientManager): OAuth2Provider {

    companion object {
        val ANONYMOUS_USER_AUTHENTICATION = AnonymousAuthenticationToken("key", "anonymous", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"))
    }

    override fun getAuthenticationToken(serviceName: String): String? {
        val request = OAuth2AuthorizeRequest
            .withClientRegistrationId(serviceName)
            .principal(ANONYMOUS_USER_AUTHENTICATION).build()

        return manager.authorize(request)?.accessToken?.tokenValue
    }
}