package com.example.chinese_app_ar.ui.quizlet

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.chinese_app_ar.R
import com.example.chinese_app_ar.databinding.FragmentQuizletQuestionBinding
import com.example.chinese_app_ar.databinding.ScoreDialogBinding

class QuizletQuestionFragment : Fragment(), OnClickListener {

    var time: String = ""
    var questionModelList: List<QuestionModel>? = emptyList()

    private var quizModel: QuizModel? = null

    private lateinit var dialog: AlertDialog
    companion object {
        fun newInstance() = QuizletQuestionFragment()

/*
        fun newInstance(questionModelList: List<QuestionModel>, time: String): QuizletQuestionFragment{
            /*
            val fragment = QuizletQuestionFragment()
            fragment.questionModelList = questionModelList
            fragment.time = time
            return fragment
             */
            val fragment = QuizletQuestionFragment()
            /*
            fragment.arguments = Bundle().apply {
                putSerializable("quizModel", qu)
                putString("time", time)
            }

             */
            fragment.questionModelList = questionModelList
            fragment.time = time
            return fragment
        }

 */
        fun newInstance(quizModel: QuizModel, time: String): QuizletQuestionFragment {
            val fragment = QuizletQuestionFragment()
            fragment.quizModel = quizModel
            fragment.time = time
            return fragment
        }
    }

    lateinit var binding: FragmentQuizletQuestionBinding


    private var currentQuestionIndex = 0
    private var selectedAnswer = ""
    private var score = 0

    //private val viewModel: QuizletQuestionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            quizModel = it.getSerializable("quizModel") as? QuizModel
            score = 0
            questionModelList = quizModel?.questionList
            time = it.getString("time").toString()
        }
        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizletQuestionBinding.inflate(inflater, container, false)

        binding.apply {
            btn0.setOnClickListener(this@QuizletQuestionFragment)
            btn1.setOnClickListener(this@QuizletQuestionFragment)
            btn2.setOnClickListener(this@QuizletQuestionFragment)
            btn3.setOnClickListener(this@QuizletQuestionFragment)
            nextBtn.setOnClickListener(this@QuizletQuestionFragment)
        }

        binding.time.text = time


        loadQuestions()
        startTimer()

        return binding.root
    }

    /*
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

     */

    private fun startTimer(){
        val totalTimeInMillis = time.toInt() * 60 * 1000L
        object : CountDownTimer(totalTimeInMillis,1000L){
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished /1000
                val minutes = seconds/60
                val remainingSeconds = seconds % 60
                binding.timerIndicatorTextview.text = String.format("%02d:%02d", minutes,remainingSeconds)

            }

            override fun onFinish() {
                //Finish the quiz

            }

        }.start()
    }

    private fun loadQuestions(){

        selectedAnswer = ""
        if(currentQuestionIndex == questionModelList?.size){
            finishQuiz()
            return
        }

        binding.apply {
            questionIndicatorTextview.text = "Вопрос ${currentQuestionIndex + 1}/${quizModel?.questionList?.size ?: 0} "
            questionProgressIndicator.progress =
                (currentQuestionIndex.toFloat() / (quizModel?.questionList?.size ?: 0).toFloat() * 100).toInt()
            questionTextview.text = quizModel?.questionList?.getOrNull(currentQuestionIndex)?.question
            btn0.text = quizModel?.questionList?.getOrNull(currentQuestionIndex)?.options?.getOrNull(0)
            btn1.text = quizModel?.questionList?.getOrNull(currentQuestionIndex)?.options?.getOrNull(1)
            btn2.text = quizModel?.questionList?.getOrNull(currentQuestionIndex)?.options?.getOrNull(2)
            btn3.text = quizModel?.questionList?.getOrNull(currentQuestionIndex)?.options?.getOrNull(3)
        }
    }

    override fun onClick(view: View?) {
        binding.apply {
            btn0.setBackgroundColor(requireContext().getColor(R.color.gray))
            btn1.setBackgroundColor(requireContext().getColor(R.color.gray))
            btn2.setBackgroundColor(requireContext().getColor(R.color.gray))
            btn3.setBackgroundColor(requireContext().getColor(R.color.gray))
        }

        val clickedBtn = view as Button
        if(clickedBtn.id==R.id.next_btn){
            //next button is clicked
            if(selectedAnswer.isEmpty()){
                Toast.makeText(requireContext(),"Пожалуйста, выберите ответ, чтобы продолжить",Toast.LENGTH_SHORT).show()
                return
            }
            if(selectedAnswer == questionModelList?.get(currentQuestionIndex)?.correct){
                score++
                Log.i("Score of quiz",score.toString())
            }
            currentQuestionIndex++
            loadQuestions()
        }else{
            //options button is clicked
            selectedAnswer = clickedBtn.text.toString()
            clickedBtn.setBackgroundColor(requireContext().getColor(R.color.orange))
        }
    }

    private fun finishQuiz(){
        val totalQuestions = questionModelList?.size
        val percentage = ((score.toFloat() / (totalQuestions?.toFloat()!!)) *100 ).toInt()

        val dialogBinding  = ScoreDialogBinding.inflate(layoutInflater)
        dialogBinding.apply {
            scoreProgressIndicator.progress = percentage
            scoreProgressText.text = "$percentage %"
            if(percentage>60){
                scoreTitle.text = "Поздравляем! Вы прошли тест"
                context?.let { scoreTitle.setTextColor(it.getColor(R.color.lesson_hsk_1)) }
            }else{
                scoreTitle.text = "Тест не пройден"
                context?.let { scoreTitle.setTextColor(it.getColor(R.color.lesson_hsk_2)) }
            }
            scoreSubtitle.text = "$score вопросов из $totalQuestions отвечены правильно"
            finishBtn.setOnClickListener {
                dialog.dismiss()
                val navController = Navigation.findNavController(requireView())
                navController.navigate(R.id.action_quizletQuestionFragment_to_nav_quizlet)

            }
        }

        dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .show()

    }




}