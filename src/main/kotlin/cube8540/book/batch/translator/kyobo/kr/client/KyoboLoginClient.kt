package cube8540.book.batch.translator.kyobo.kr.client

import feign.Response
import feign.form.FormProperty
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping

data class KyoboLoginRequest(

    @FormProperty("memid")
    var username: String,

    @FormProperty("pw")
    var password: String
)

interface KyoboCookieResolver {
    fun resolveCookie(setCookies: Collection<String>): Map<String, String>
}

class DefaultKyoboCookieResolver: KyoboCookieResolver {

    override fun resolveCookie(setCookies: Collection<String>): Map<String, String> {
        val map = HashMap<String, String>()
        setCookies.forEach {setCookie ->
            val keyValueToken = setCookie.split(";")[0]
            val keyValue = keyValueToken.split("=")

            map[keyValue[0]] = keyValue[1]
        }
        return map
    }
}

@FeignClient(name = "kyoboLoginClient", url = "https://www.kyobobook.co.kr", configuration = [KyoboLoginClientConfiguration::class])
interface KyoboLoginClient {

    @PostMapping(path = ["/login/login.laf"], consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun login(request: KyoboLoginRequest): Response

}