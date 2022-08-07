package cube8540.book.batch.translator.store.client

import cube8540.book.batch.translator.BookUpstreamAPIRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.beans.ConstructorProperties


data class StoreUpstreamResponse
@ConstructorProperties(value = ["successBooks", "failedBooks"])
constructor(
    val successBooks: List<String>?,
    val failedBooks: List<StoreUpstreamFailedBooks>?
)

data class StoreUpstreamFailedBooks
@ConstructorProperties(value = ["isbn", "errors"])
constructor(
    val isbn: String,
    val errors: List<StoreUpstreamFailedReason>
)

data class StoreUpstreamFailedReason
@ConstructorProperties(value = ["property", "message"])
constructor(
    val property: String,
    val message: String
)

@FeignClient(name = "storeClient", url = "\${api.endpoint.application.host}", configuration = [StoreClientConfiguration::class])
interface StoreClient {

    @PostMapping("/api/v1/books")
    fun upstream(@RequestBody request: BookUpstreamAPIRequest): StoreUpstreamResponse
}