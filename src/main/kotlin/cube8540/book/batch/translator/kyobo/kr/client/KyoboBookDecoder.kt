package cube8540.book.batch.translator.kyobo.kr.client

import feign.Response
import feign.Util
import feign.codec.DecodeException
import feign.codec.Decoder
import java.lang.reflect.Type
import java.nio.charset.Charset

class KyoboBookDecoder: Decoder {
    override fun decode(response: Response?, type: Type?): Any? {
        if (response == null) {
            return null
        }
        if (String::class.java == type) {
            return Util.toString(response.body().asReader(Charset.forName("EUC-KR")))
        } else {
            throw DecodeException(response.status(), "$type is not a type supported by this decoder", response.request())
        }
    }
}