package cube8540.book.batch.translator.naver.com.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.cloud.openfeign.SpringQueryMap
import org.springframework.web.bind.annotation.GetMapping

@FeignClient(name = "naverBookClient", url = "https://openapi.naver.com", configuration = [NaverBookClientConfiguration::class])
interface NaverBookClient {

    @GetMapping("/v1/search/book_adv.json")
    fun search(@SpringQueryMap request: NaverBookClientRequest): NaverBookClientResponse

}