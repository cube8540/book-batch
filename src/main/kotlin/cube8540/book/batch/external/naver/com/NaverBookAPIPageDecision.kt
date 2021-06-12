package cube8540.book.batch.external.naver.com

import cube8540.book.batch.external.PageDecision

class NaverBookAPIPageDecision: PageDecision {
    override fun calculation(page: Int, pageSize: Int): Int = when (page == 1) {
        true -> page
        else -> (pageSize * (page - 1)) + 1
    }
}