package cube8540.book.batch.interlock.naver.com.client

import java.beans.ConstructorProperties

data class NaverBookClientError
@ConstructorProperties(value = ["errorMessage", "errorCode"])
constructor(var errorMessage: String, var errorCode: String)