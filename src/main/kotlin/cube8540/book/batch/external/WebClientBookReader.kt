package cube8540.book.batch.external

import cube8540.book.batch.domain.BookDetails
import org.springframework.batch.item.database.AbstractPagingItemReader
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilderFactory

class WebClientBookReader(private val uriBuilderFactory: UriBuilderFactory, private val webClient: WebClient)
    : AbstractPagingItemReader<BookDetails>() {

    lateinit var requestPageParameterName: String
    lateinit var requestPageSizeParameterName: String

    override fun doReadPage() {
        val bookAPIResponse = webClient.get()
            .uri(
                uriBuilderFactory.builder()
                    .queryParam(requestPageParameterName, page + 1)
                    .queryParam(requestPageSizeParameterName, pageSize)
                    .build()
            )
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(BookAPIResponse::class.java)
            .block()

        if (results == null) {
            results = ArrayList<BookDetails>()
        }
        results.addAll(bookAPIResponse?.books?: emptyList())
    }

    override fun doJumpToPage(itemIndex: Int) {
    }
}