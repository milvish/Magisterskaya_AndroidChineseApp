package com.example.chinese_app_ar.ui.statistic

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.chinese_app_ar.databinding.FragmentStatisticBinding
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class StatisticsFragment : Fragment() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    lateinit var binding: FragmentStatisticBinding
    lateinit var json: JSONArray

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatisticBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Загрузка данных из Firebase Storage
        loadLanguageStudyDates()

        // Слушатель для календаря
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$year-${(month + 1).toString().padStart(2, '0')}-" +
                    "${dayOfMonth.toString().padStart(2, '0')}"
            // Здесь можно обработать выбранную дату
            highlightStudyDates(json, selectedDate)
        }
        binding.myCalendarView.visibility = View.GONE
    }

    private fun loadLanguageStudyDates() {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("language_study_dates.json")

        storageRef.getBytes(1024 * 1024) // 1 MB max
            .addOnSuccessListener { bytes ->
                val jsonString = String(bytes)
                json = JSONArray(jsonString)
                Log.d("JSON", json.toString())
                highlightStudyDates(json)
                displayDatesInTextView(json)
                displayNearestDateAndDifference(json)
                displayLongestConsecutiveStreakAndCurrentStreak(json)
            }
            .addOnFailureListener {
                // Обработка ошибки загрузки данных из Firebase Storage
            }
    }


    private fun highlightStudyDates(jsonArray: JSONArray) {
        val calendar = Calendar.getInstance()
        val selectedYear = calendar.get(Calendar.YEAR)
        val selectedMonth = calendar.get(Calendar.MONTH)
        val selectedDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        var foundMatch = false

        for (i in 0 until jsonArray.length()) {
            val dateString = jsonArray.getString(i)
            val date = dateFormat.parse(dateString)
            if (date != null) {
                calendar.time = date
                val jsonYear = calendar.get(Calendar.YEAR)
                val jsonMonth = calendar.get(Calendar.MONTH)
                val jsonDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                if (selectedYear == jsonYear && selectedMonth == jsonMonth && selectedDayOfMonth == jsonDayOfMonth) {

                    binding.calendarView.setBackgroundColor(Color.RED)
                    foundMatch = true
                    break
                }
            }
        }

        if (!foundMatch) {
            binding.calendarView.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    private fun highlightStudyDates(jsonArray: JSONArray, selectedDate: String) {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(selectedDate)
        if (date != null) {
            calendar.time = date
            val selectedYear = calendar.get(Calendar.YEAR)
            val selectedMonth = calendar.get(Calendar.MONTH)
            val selectedDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            var foundMatch = false

            for (i in 0 until jsonArray.length()) {
                val dateString = jsonArray.getString(i)
                val date = dateFormat.parse(dateString)
                if (date != null) {
                    calendar.time = date
                    val jsonYear = calendar.get(Calendar.YEAR)
                    val jsonMonth = calendar.get(Calendar.MONTH)
                    val jsonDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                    if (selectedYear == jsonYear && selectedMonth == jsonMonth && selectedDayOfMonth == jsonDayOfMonth) {
                        binding.calendarView.setBackgroundColor(Color.rgb(255, 200, 200))
                        foundMatch = true
                        break
                    }
                }
            }

            if (!foundMatch) {
                binding.calendarView.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    private fun displayDatesInTextView(jsonArray: JSONArray) {
        val datesStringBuilder = StringBuilder()
        for (i in 0 until jsonArray.length()) {
            val dateString = jsonArray.getString(i)
            datesStringBuilder.append(dateString).append("\n")
        }
        binding.datesList.text = datesStringBuilder.toString()
    }

    private fun displayNearestDateAndDifference(jsonArray: JSONArray) {
        val currentDate = Calendar.getInstance().time
        var nearestDate: Calendar? = null
        var minDiff: Long = Long.MAX_VALUE

        for (i in 0 until jsonArray.length()) {
            val dateString = jsonArray.getString(i)
            val date = dateFormat.parse(dateString)
            if (date != null) {
                val calendarDate = Calendar.getInstance()
                calendarDate.time = date
                val diff = abs(currentDate.time - date.time)
                if (diff < minDiff) {
                    minDiff = diff
                    nearestDate = calendarDate
                }
            }
        }

        if (nearestDate != null) {
            val diffInDays = TimeUnit.MILLISECONDS.toDays(minDiff)
            binding.fromLastDate.text = "Latest date of studying: ${dateFormat.format(nearestDate.time)} \n (in $diffInDays days)"
            binding.statisticLatestDay.statisticText.text = "Последняя дата обучения "
            binding.statisticLatestDay.singleDate.visibility = View.VISIBLE
            binding.statisticLatestDay.date.text = "${dateFormat.format(nearestDate.time)}"
            binding.statisticLatestDay.countDays.text = "$diffInDays"
            binding.statisticLatestDay.countDaysText.text = "дней \nназад"
        } else {
            binding.fromLastDate.text = "Нет дат"
        }
    }

    private fun abs(value: Long): Long {
        return if (value < 0) -value else value
    }

    private fun displayLongestConsecutiveStreakAndCurrentStreak(jsonArray: JSONArray) {
        val dates = mutableListOf<Date>()
        for (i in 0 until jsonArray.length()) {
            val dateString = jsonArray.getString(i)
            val date = dateFormat.parse(dateString)
            if (date != null) {
                dates.add(date)
            }
        }

        if (dates.isEmpty()) {
            binding.longestStreak.text = "Нет дат"
            return
        } else {

            dates.sort()

            var longestStreak = 0
            var currentStreak = 0
            var maxStreak = 0

            var longestStartDate: Date? = null
            var longestEndDate: Date? = null
            var currentStartDate: Date? = dates[0]
            var currentEndDate: Date? = dates[0]

            for (i in 1 until dates.size) {
                val diffInMillis = dates[i].time - dates[i - 1].time
                val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)
                if (diffInDays == 1L) {
                    currentStreak++
                    currentEndDate = dates[i]
                    if (currentStreak > longestStreak) {
                        longestStreak = currentStreak
                        longestStartDate = currentStartDate
                        longestEndDate = currentEndDate
                    }
                } else {
                    currentStreak = 1
                    currentStartDate = dates[i]
                    currentEndDate = dates[i]
                }
            }

            val today = Calendar.getInstance().time
            if (today.time != dates.last().time ) {
                maxStreak = longestStreak
            }

            val longestStartString = dateFormat.format(longestStartDate!!)
            val longestEndString = dateFormat.format(longestEndDate!!)
            val currentStartString = dateFormat.format(currentStartDate!!)
            val currentEndString = dateFormat.format(currentEndDate!!)

            binding.longestStreak.text =
                "Longest streak of consecutive study days: $longestStreak days\n" +
                        "From: $longestStartString To: $longestEndString"

            binding.statisticLongestStreak.statisticText.text = "Самая длинная последовательность дней обучения"
            binding.statisticLongestStreak.fromTo.visibility = View.VISIBLE
            binding.statisticLongestStreak.dateStart.text = "$longestStartString"
            binding.statisticLongestStreak.dateEnd.text = "$longestEndString"
            binding.statisticLongestStreak.countDays.text = "$longestStreak"
            if (longestStreak == 4) {
                binding.statisticLongestStreak.countDaysText.text = "дня"
            } else {
                binding.statisticLongestStreak.countDaysText.text = "дней"
            }

            if (today.time != dates.last().time) {
                maxStreak = longestStreak
                binding.currentStreak.text =
                    "Current streak of consecutive study days: 0 days\n" +
                            "From: ${dateFormat.format(today.time)} To: ${dateFormat.format(today.time)}"
                binding.statisticCurrentStreak.statisticText.text = "Текущая последовательность дней"
                binding.statisticCurrentStreak.singleDate.visibility = View.VISIBLE
                binding.statisticCurrentStreak.date.text = "Today"
                binding.statisticCurrentStreak.countDays.text = "0"
                binding.statisticCurrentStreak.countDaysText.text = "дней"
            } else {
                binding.currentStreak.text =
                    "Current streak of consecutive study days: $maxStreak days\n" +
                            "From: $currentStartString To: $currentEndString"
                binding.statisticCurrentStreak.statisticText.text = "Текущая последовательность дней"
                binding.statisticCurrentStreak.fromTo.visibility = View.VISIBLE
                binding.statisticCurrentStreak.dateStart.text = "$currentStartString"
                binding.statisticCurrentStreak.dateEnd.text = "$currentEndString"
                binding.statisticCurrentStreak.countDays.text = "$maxStreak"
                binding.statisticCurrentStreak.countDaysText.text = "дней"
            }

        }
    }
}
