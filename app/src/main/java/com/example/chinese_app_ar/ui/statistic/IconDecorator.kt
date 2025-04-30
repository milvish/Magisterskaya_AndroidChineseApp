package com.example.chinese_app_ar.ui.statistic

import android.graphics.drawable.Drawable
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan


class IconDecorator(private val drawable: Drawable, private val dates: Set<CalendarDay>) : DayViewDecorator {
    private var color = 0

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    /*
    override fun decorate(view: DayViewFacade) {
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        val span = IconSpan(drawable)
        view.addSpan(span)
    }

     */
    public override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(5f, color))
    }
}