package com.example.quizapp

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class QuizActivity : AppCompatActivity() {

    data class Question(
        val question: String,
        val options: List<String>,
        val answer: String
    )

    data class TriviaResponse(
        val response_code: Int,
        val results: List<QuestionApi>
    )

    data class QuestionApi(
        val question: String,
        val correct_answer: String,
        val incorrect_answers: List<String>
    )

    interface TriviaApiService {
        @GET("api.php")
        fun getQuestions(
            @Query("amount") amount: Int,
            @Query("type") type: String = "multiple"
        ): Call<TriviaResponse>
    }

    private lateinit var questions: List<Question>
    private var currentIndex = 0
    private var score = 0

    private lateinit var questionText: TextView
    private lateinit var optionsGroup: RadioGroup
    private lateinit var optionA: RadioButton
    private lateinit var optionB: RadioButton
    private lateinit var optionC: RadioButton
    private lateinit var optionD: RadioButton
    private lateinit var nextButton: Button
    private lateinit var timerText: TextView
    private lateinit var questionNumberText: TextView
    private lateinit var timerProgressBar: ProgressBar
    private lateinit var questionCard: androidx.cardview.widget.CardView



    private var countDownTimer: CountDownTimer? = null
    private val questionTimeInMillis: Long = 15000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        val helpButton: ImageButton = findViewById(R.id.helpButton)
        helpButton.setOnClickListener {
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("How to Play")
            builder.setMessage(
                "1. Read the question carefully.\n" +
                        "2. Choose the correct answer from the options.\n" +
                        "3. Tap 'Next' to proceed.\n" +
                        "4. Final score is shown at the end."
            )
            builder.setPositiveButton("OK", null)
            builder.show()
        }




        questionNumberText = findViewById(R.id.questionNumberText)
        timerProgressBar = findViewById(R.id.timerProgressBar)
        questionCard = findViewById(R.id.questionCard)




        questionText = findViewById(R.id.questionText)
        optionsGroup = findViewById(R.id.optionsGroup)
        optionA = findViewById(R.id.optionA)
        optionB = findViewById(R.id.optionB)
        optionC = findViewById(R.id.optionC)
        optionD = findViewById(R.id.optionD)
        nextButton = findViewById(R.id.nextButton)
        timerText = findViewById(R.id.timerText)

        fetchQuestionsFromApi()

        nextButton.setOnClickListener {
            val selectedId = optionsGroup.checkedRadioButtonId
            if (selectedId != -1) {
                countDownTimer?.cancel()

                val selectedOption = findViewById<RadioButton>(selectedId)
                val isCorrect = selectedOption.text.toString() == questions[currentIndex].answer

                showAnswerFeedback(selectedOption, isCorrect)
            } else {
                Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchQuestionsFromApi() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://opentdb.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(TriviaApiService::class.java)
        val call = service.getQuestions(amount = 10)

        call.enqueue(object : Callback<TriviaResponse> {
            override fun onResponse(call: Call<TriviaResponse>, response: Response<TriviaResponse>) {
                if (response.isSuccessful) {
                    val triviaResponse = response.body()
                    triviaResponse?.let {
                        questions = it.results.map { q ->
                            val options = q.incorrect_answers.toMutableList()
                            options.add(q.correct_answer)
                            options.shuffle()
                            Question(
                                q.question.decodeHtml(),
                                options.map { it.decodeHtml() },
                                q.correct_answer.decodeHtml()
                            )
                        }
                        loadQuestion(0)
                    }
                } else {
                    Toast.makeText(this@QuizActivity, "Failed to load questions", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TriviaResponse>, t: Throwable) {
                Toast.makeText(this@QuizActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadQuestion(index: Int) {
        val q = questions[index]
        questionNumberText.text = "Question ${index + 1} of ${questions.size}"
        questionText.text = q.question
        optionA.text = q.options[0]
        optionB.text = q.options[1]
        optionC.text = q.options[2]
        optionD.text = q.options[3]
        optionsGroup.clearCheck()

        resetOptionsColor()
        startTimer()

        questionCard.alpha = 0f
        questionCard.animate().alpha(1f).setDuration(500).start()
    }



    private fun startTimer() {
        countDownTimer?.cancel()

        timerProgressBar.max = questionTimeInMillis.toInt()
        timerProgressBar.progress = questionTimeInMillis.toInt()

        countDownTimer = object : CountDownTimer(questionTimeInMillis, 50) {
            override fun onTick(millisUntilFinished: Long) {
                timerText.text = (millisUntilFinished / 1000 + 1).toString()
                timerProgressBar.progress = millisUntilFinished.toInt()
            }

            override fun onFinish() {
                timerText.text = "0"
                timerProgressBar.progress = 0
                Toast.makeText(this@QuizActivity, "Time's up!", Toast.LENGTH_SHORT).show()
                moveToNextQuestion(false)
            }
        }.start()
    }


    private fun showAnswerFeedback(selectedOption: RadioButton, isCorrect: Boolean) {
        val correctColor = ContextCompat.getColor(this, android.R.color.holo_green_light)
        val wrongColor = ContextCompat.getColor(this, android.R.color.holo_red_light)

        val colorFrom = Color.WHITE
        val colorTo = if (isCorrect) correctColor else wrongColor

        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        colorAnimation.duration = 500
        colorAnimation.addUpdateListener { animator ->
            selectedOption.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimation.start()

        if (isCorrect) score++


        setOptionsEnabled(false)

        selectedOption.postDelayed({
            moveToNextQuestion(isCorrect)
            setOptionsEnabled(true)
        }, 1000)
    }

    private fun setOptionsEnabled(enabled: Boolean) {
        optionA.isEnabled = enabled
        optionB.isEnabled = enabled
        optionC.isEnabled = enabled
        optionD.isEnabled = enabled
    }

    private fun resetOptionsColor() {
        optionA.setBackgroundColor(Color.WHITE)
        optionB.setBackgroundColor(Color.WHITE)
        optionC.setBackgroundColor(Color.WHITE)
        optionD.setBackgroundColor(Color.WHITE)
    }

    private fun moveToNextQuestion(answeredCorrectly: Boolean) {
        currentIndex++
        if (currentIndex < questions.size) {
            loadQuestion(currentIndex)
        } else {
            countDownTimer?.cancel()
            val intent = android.content.Intent(this, ResultActivity::class.java)
            intent.putExtra("SCORE", score)
            intent.putExtra("TOTAL", questions.size)
            startActivity(intent)
            finish()
        }
    }

    private fun String.decodeHtml(): String =
        android.text.Html.fromHtml(this, android.text.Html.FROM_HTML_MODE_LEGACY).toString()
}
