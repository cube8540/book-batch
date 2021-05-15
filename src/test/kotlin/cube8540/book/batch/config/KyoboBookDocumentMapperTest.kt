package cube8540.book.batch.config

import cube8540.book.batch.domain.DivisionRawMapper
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.net.URI
import java.util.stream.Stream

class KyoboBookDocumentMapperTest {

    companion object {
        const val exampleHtmlFile0 = "kyobo-book-page-example.0.html"
        const val exampleHtmlFile1 = "kyobo-book-page-example.1.html"
    }

    private val divisionRawMapper: DivisionRawMapper = mockk(relaxed = true) {
        every { mapping(listOf("47", "4717")) } returns listOf("division0000", "division0001")
        every { mapping(listOf("47", "4722")) } returns listOf("division0000", "division0002")
    }

    private val documentMapper = DefaultMapperConfiguration()
        .kyoboBookDocumentMapper(divisionRawMapper)

    @ParameterizedTest
    @MethodSource(value = ["kyoboBookDetailsProvider"])
    fun deserialization(document: Document, expectedBookDetails: ExpectedBookDetails) {
        val result = documentMapper.convertValue(document)

        assertThat(result.resolveIsbn()).isEqualTo(expectedBookDetails.isbn)
        assertThat(result.resolveAuthors()).isEqualTo(expectedBookDetails.authors)
        assertThat(result.resolveTitle()).isEqualTo(expectedBookDetails.title)
        assertThat(result.resolveThumbnail()?.largeThumbnail).isEqualTo(expectedBookDetails.largeThumbnail)
        assertThat(result.resolveThumbnail()?.mediumThumbnail).isEqualTo(expectedBookDetails.mediumThumbnail)
        assertThat(result.resolvePrice()).isEqualTo(expectedBookDetails.price)
        assertThat(result.resolveSeriesCode()).isEqualTo(expectedBookDetails.seriesCode)
        assertThat(result.resolveDivisions()).isEqualTo(expectedBookDetails.divisions)
        assertThat(result.resolveDescription()).isEqualTo(expectedBookDetails.description)
    }

    private fun kyoboBookDetailsProvider() = Stream.of(
        Arguments.of(getDocument(exampleHtmlFile0), ExpectedBookDetails(
            isbn = "9791127859251",
            authors = setOf("카규 쿠모 (원작)"),
            title = "고블린 슬레이어 외전: 이어 원. 6",
            largeThumbnail = URI.create("http://image.kyobobook.co.kr/images/book/xlarge/251/x9791127859251.jpg"),
            mediumThumbnail = URI.create("http://image.kyobobook.co.kr/images/book/large/251/l9791127859251.jpg"),
            price = 5500.0,
            seriesCode = "5800076231538",
            divisions = setOf("division0000", "division0001"),
            description = """소치기 소녀는 고전(孤電)의 술사와 행동을 함께하는 고블린 슬레이어에게 불안감을 느끼는 나날을 보낸다. 모험가 길드에서 접수원에게 마녀를 소개받은 창잡이는 함께 요술사 토벌에 나서지만, 그곳에서는……?! 그리고 고블린을 조사하는 고전의 술사와 고블린 슬레이어는 세계의 끝─ 암흑의 탑에 다다른다. 다만 그곳은 고블린의 소굴이었다……!! 어딘가 먼 곳에서 주사위 구르는 소리가 들린다. 원작자 카규 쿠모의 신규 집필 단편 소설도 수록!!"""
        )),
        Arguments.of(getDocument(exampleHtmlFile1), ExpectedBookDetails(
            isbn = "9791133447831",
            authors = setOf("Norimitsu Kaihou (원작)"),
            title = "학교생활!. 9",
            largeThumbnail = URI.create("http://image.kyobobook.co.kr/images/book/xlarge/831/x9791133447831.jpg"),
            mediumThumbnail = URI.create("http://image.kyobobook.co.kr/images/book/large/831/l9791133447831.jpg"),
            price = 5000.0,
            seriesCode = "5800068780426",
            divisions = setOf("division0000", "division0002"),
            description = """‘학교에서 살자!’ 삽에 대한 애정이 각별한(?) 쿠루미. 모두를 아우르는 리. 서글서글한 고문 메구 언니. 천진난만한 무드메이커 유키. 이들 학교생활부의 행복한 일상. 그러나ㅡ……?! 평온한 일상 X 압도적 절망이 교차하는 스쿨 서바이벌 호러!!"""
        ))
    )

    private fun getDocument(resourcePath: String): Document {
        val htmlFile = File(javaClass.classLoader.getResource(resourcePath)!!.file)
        val reader = BufferedReader(InputStreamReader(FileInputStream(htmlFile), "EUC-KR"))

        return Jsoup.parse(reader.readText())
    }
}

data class ExpectedBookDetails(
    val isbn: String,
    val authors: Set<String>,
    val title: String,
    val largeThumbnail: URI,
    val mediumThumbnail: URI,
    val price: Double,
    val seriesCode: String,
    val divisions: Set<String>,
    val description: String
)