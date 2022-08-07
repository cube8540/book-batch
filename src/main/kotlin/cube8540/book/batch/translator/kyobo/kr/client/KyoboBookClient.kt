package cube8540.book.batch.translator.kyobo.kr.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

const val KYOBO_HOST = "https://www.kyobobook.co.kr"
const val DETAILS_PATH = "/product/detailViewKor.laf"

const val ISBN_QUERY_PARAM_NAME = "barcode"

@FeignClient(name = "kyoboBookClient", url = KYOBO_HOST, configuration = [KyoboBookClientConfiguration::class])
interface KyoboBookClient {

    @GetMapping(path = [DETAILS_PATH], consumes = [MediaType.TEXT_HTML_VALUE])
    fun search(@RequestParam(ISBN_QUERY_PARAM_NAME) isbn: String): String

}