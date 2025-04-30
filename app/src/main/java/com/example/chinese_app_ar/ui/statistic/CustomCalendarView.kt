package com.example.chinese_app_ar.ui.statistic

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.CalendarView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CustomCalendarView : CalendarView {

    private var datesToHighlight: List<String> = listOf("2024-05-01", "2024-05-06", "2024-05-11")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val highlightedDates = mutableSetOf<Calendar>()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        for (date in datesToHighlight) {
            val calendar = Calendar.getInstance()
            calendar.time = dateFormat.parse(date)
            highlightedDates.add(calendar)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val paint = Paint()
        paint.color = Color.RED
        paint.style = Paint.Style.FILL

        // Draw highlights on specified dates
        for (highlightedDate in highlightedDates) {
            val dateRect = rectForDate(highlightedDate)
            if (dateRect != null) {
                canvas.drawRect(dateRect, paint)
            }
        }
    }

    private fun rectForDate(calendar: Calendar): Rect? {
        // Get the current month and year being displayed by the CalendarView
        val displayedMonth = calendar.get(Calendar.MONTH)
        val displayedYear = calendar.get(Calendar.YEAR)

        // Check if the date to be highlighted is in the displayed month and year
        if (calendar.get(Calendar.MONTH) != displayedMonth || calendar.get(Calendar.YEAR) != displayedYear) {
            return null
        }

        // Calculate the position of the date in the calendar view
        val firstDayOfMonth = calendar.clone() as Calendar
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1)
        val dayOfWeekOffset = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - firstDayOfMonth.firstDayOfWeek

        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH) + dayOfWeekOffset - 1
        val row = dayOfMonth / 7
        val column = dayOfMonth % 7

        // Define the size of each day cell (assuming equal width and height)
        val daySize = width / 7

        // Calculate the rectangle for the specific date
        val left = column * daySize
        val right = left + daySize
        val top = row * daySize
        val bottom = top + daySize

        return Rect(left, top, right, bottom)
    }


    private val daySize: Int
        get() = width / 7
}
