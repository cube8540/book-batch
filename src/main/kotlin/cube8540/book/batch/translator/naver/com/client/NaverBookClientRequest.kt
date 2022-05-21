package cube8540.book.batch.translator.naver.com.client

import feign.Param

data class NaverBookClientRequest(
    val start: Int = 1,

    val display: Int = 10,

    @get:Param("d_publ")
    val publisher: String? = null,

    @get:Param("d_dafr")
    val from: String? = null,

    @get:Param("d_dato")
    val to: String? = null,

    @get:Param("d_isbn")
    val isbn: String? = null
)