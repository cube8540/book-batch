package cube8540.book.batch.config

import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import org.apache.ibatis.type.MappedTypes
import java.net.URI
import java.sql.*
import java.time.LocalDate
import java.time.LocalDateTime

@MappedTypes(LocalDateTime::class)
class MybatisLocalDateTimeTypeHandler: BaseTypeHandler<LocalDateTime>() {
    override fun setNonNullParameter(ps: PreparedStatement?, i: Int, parameter: LocalDateTime?, jdbcType: JdbcType?) {
        if (parameter == null) {
            ps?.setTimestamp(i, null)
        } else {
            ps?.setTimestamp(i, Timestamp.valueOf(parameter))
        }
    }

    override fun getNullableResult(rs: ResultSet?, columnName: String): LocalDateTime? {
        return rs?.getTimestamp(columnName)?.toLocalDateTime()
    }

    override fun getNullableResult(rs: ResultSet?, columnIndex: Int): LocalDateTime? {
        return rs?.getTimestamp(columnIndex)?.toLocalDateTime()
    }

    override fun getNullableResult(cs: CallableStatement?, columnIndex: Int): LocalDateTime? {
        return cs?.getTimestamp(columnIndex)?.toLocalDateTime()
    }
}

@MappedTypes
class MybatisLocalDateTypeHandler: BaseTypeHandler<LocalDate>() {
    override fun setNonNullParameter(ps: PreparedStatement?, i: Int, parameter: LocalDate?, jdbcType: JdbcType?) {
        if (parameter == null) {
            ps?.setDate(i, null)
        } else {
            ps?.setDate(i, Date.valueOf(parameter))
        }
    }

    override fun getNullableResult(rs: ResultSet?, columnName: String?): LocalDate? {
        return rs?.getDate(columnName)?.toLocalDate()
    }

    override fun getNullableResult(rs: ResultSet?, columnIndex: Int): LocalDate? {
        return rs?.getDate(columnIndex)?.toLocalDate()
    }

    override fun getNullableResult(cs: CallableStatement?, columnIndex: Int): LocalDate? {
        return cs?.getDate(columnIndex)?.toLocalDate()
    }

}

@MappedTypes
class MybatisUriTypeHandler: BaseTypeHandler<URI>() {
    override fun setNonNullParameter(ps: PreparedStatement?, i: Int, parameter: URI?, jdbcType: JdbcType?) {
        if (parameter == null) {
            ps?.setString(i, null)
        } else {
            ps?.setString(i, parameter.toString())
        }
    }

    override fun getNullableResult(rs: ResultSet?, columnName: String?): URI? {
        return rs?.getString(columnName)?.let { URI.create(it) }
    }

    override fun getNullableResult(rs: ResultSet?, columnIndex: Int): URI? {
        return rs?.getString(columnIndex)?.let { URI.create(it) }
    }

    override fun getNullableResult(cs: CallableStatement?, columnIndex: Int): URI? {
        return cs?.getString(columnIndex)?.let { URI.create(it) }
    }
}