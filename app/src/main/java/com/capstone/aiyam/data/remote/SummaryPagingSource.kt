package com.capstone.aiyam.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.capstone.aiyam.domain.model.Alerts
import com.capstone.aiyam.domain.model.WeeklySummary
import com.capstone.aiyam.domain.repository.UserRepository
import com.capstone.aiyam.utils.withToken
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

class SummaryPagingSource(
    private val alertService: AlertService,
    private val userRepository: UserRepository
) : PagingSource<Int, WeeklySummary>() {
    private val weeksPerPage = 4

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WeeklySummary> {
        val alerts = withToken(userRepository.getFirebaseUser(), userRepository::getFirebaseToken) {
            alertService.getAlerts(it)
        }

        val page = params.key ?: 0
        val summaries = aggregateAlertsByWeek(alerts.data)

        val start = page * weeksPerPage
        val end = (page + 1) * weeksPerPage

        val pageData = summaries.subList(start, end.coerceAtMost(summaries.size))

        val nextKey = if (end < summaries.size) page + 1 else null
        val prevKey = if (page > 0) page - 1 else null

        return LoadResult.Page(
            data = pageData,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }

    private fun aggregateAlertsByWeek(alerts: List<Alerts>): List<WeeklySummary> {
        val weekFields = WeekFields.of(Locale.getDefault())
        return alerts.groupBy { alert ->
            val dateTime = LocalDateTime.parse(alert.createdAt, DateTimeFormatter.ISO_DATE_TIME)
            val weekNumber = dateTime.get(weekFields.weekOfWeekBasedYear())
            val year = dateTime.year
            "$year-W$weekNumber"
        }.map { (week, alerts) ->
            WeeklySummary(date = LocalDate.now(), alertCount = alerts.size)
        }.sortedBy { it.date }
    }

    override fun getRefreshKey(state: PagingState<Int, WeeklySummary>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
