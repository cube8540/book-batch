package cube8540.book.batch.translator.naver.com.application

import cube8540.book.batch.translator.PageDecision

class NaverBookPageDecision: PageDecision {
    override fun calculation(page: Int, pageSize: Int): Int = when (page == 1) {
        true -> page
        else -> (pageSize * (page - 1)) + 1
    }
}