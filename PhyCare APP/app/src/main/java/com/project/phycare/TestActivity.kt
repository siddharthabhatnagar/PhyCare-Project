package com.project.phycare

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

private lateinit var database: DatabaseReference
private var currentQuestionIndex = 0
private var totalScore = 0

val questions = listOf(
    Question(
        id = 1,
        text = "1. How often do you feel sad or down without any clear reason?\n1. बिना किसी स्पष्ट कारण के आप कितनी बार उदास महसूस करते हैं?",
        options = listOf("Never / कभी नहीं", "Sometimes / कभी-कभी", "Often / अक्सर", "Almost Always / लगभग हमेशा")
    ),
    Question(
        id = 2,
        text = "2. Do you feel anxious or nervous even in normal situations?\n2. क्या आप सामान्य स्थितियों में भी बेचैनी या घबराहट महसूस करते हैं?",
        options = listOf("Never / कभी नहीं", "Sometimes / कभी-कभी", "Often / अक्सर", "Almost Always / लगभग हमेशा")
    ),
    Question(
        id = 3,
        text = "3. Do you struggle to focus or concentrate on tasks?\n3. क्या आप कार्यों पर ध्यान केंद्रित करने में कठिनाई महसूस करते हैं?",
        options = listOf("Never / कभी नहीं", "Sometimes / कभी-कभी", "Often / अक्सर", "Almost Always / लगभग हमेशा")
    ),
    Question(
        id = 4,
        text = "4. How often do you feel tired or low on energy?\n4. आप कितनी बार थकान या ऊर्जा की कमी महसूस करते हैं?",
        options = listOf("Never / कभी नहीं", "Sometimes / कभी-कभी", "Often / अक्सर", "Almost Always / लगभग हमेशा")
    ),
    Question(
        id = 5,
        text = "5. Do you have trouble sleeping or staying asleep?\n5. क्या आपको सोने या नींद बनाए रखने में परेशानी होती है?",
        options = listOf("Never / कभी नहीं", "Sometimes / कभी-कभी", "Often / अक्सर", "Almost Always / लगभग हमेशा")
    ),
    Question(
        id = 6,
        text = "6. Do you feel hopeless or that things won't improve?\n6. क्या आपको लगता है कि चीजें कभी नहीं सुधरेंगी या आशा नहीं है?",
        options = listOf("Never / कभी नहीं", "Sometimes / कभी-कभी", "Often / अक्सर", "Almost Always / लगभग हमेशा")
    ),
    Question(
        id = 7,
        text = "7. Do you feel a lack of interest in things you used to enjoy?\n7. क्या आपको उन चीजों में रुचि नहीं रहती जो पहले आपको पसंद थीं?",
        options = listOf("Never / कभी नहीं", "Sometimes / कभी-कभी", "Often / अक्सर", "Almost Always / लगभग हमेशा")
    ),
    Question(
        id = 8,
        text = "8. Do you feel irritable or angry without clear reason?\n8. क्या आप बिना किसी कारण के चिड़चिड़े या गुस्से में महसूस करते हैं?",
        options = listOf("Never / कभी नहीं", "Sometimes / कभी-कभी", "Often / अक्सर", "Almost Always / लगभग हमेशा")
    ),
    Question(
        id = 9,
        text = "9. How often do you feel overwhelmed by your emotions?\n9. क्या आप अपनी भावनाओं से अभिभूत महसूस करते हैं?",
        options = listOf("Never / कभी नहीं", "Sometimes / कभी-कभी", "Often / अक्सर", "Almost Always / लगभग हमेशा")
    ),
    Question(
        id = 10,
        text = "10. Do you avoid social interaction or talking to people?\n10. क्या आप सामाजिक मेलजोल या लोगों से बात करने से बचते हैं?",
        options = listOf("Never / कभी नहीं", "Sometimes / कभी-कभी", "Often / अक्सर", "Almost Always / लगभग हमेशा")
    )
)

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_test)

        database = FirebaseDatabase.getInstance().reference

        val questionText: TextView = findViewById(R.id.questionText)
        val optionsGroup: RadioGroup = findViewById(R.id.optionsGroup)
        val nextButton: Button = findViewById(R.id.nextButton)

        nextButton.setOnClickListener {
            val selectedOptionId = optionsGroup.checkedRadioButtonId
            if (selectedOptionId != -1) {
                val selectedOption = findViewById<RadioButton>(selectedOptionId)
                val score = optionsGroup.indexOfChild(selectedOption)
                totalScore += score

                currentQuestionIndex++
                if (currentQuestionIndex < questions.size) {
                    val nextQuestion = questions[currentQuestionIndex]
                    questionText.text = nextQuestion.text
                    optionsGroup.clearCheck()
                    nextButton.text = if (currentQuestionIndex == questions.size - 1) "Submit" else "Next"
                } else {
                    saveResultToFirebase()
                    showResult()
                }
            } else {
                Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show()
            }
        }

        // Display the first question
        val firstQuestion = questions[currentQuestionIndex]
        questionText.text = firstQuestion.text
    }

    private fun saveResultToFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val result = mapOf(
            "score" to totalScore,
            "timestamp" to ServerValue.TIMESTAMP
        )

        userId?.let {
            database.child("users").child(it).child("test_results").push().setValue(result)
            database.child("users").child(it).child("istestdone").setValue(true)
        }
    }

    private fun showResult() {
        val status = when {
            totalScore <= 9 -> "Mild"
            totalScore in 10..19 -> "Moderate"
            else -> "Severe"
        }

        val suggestions = when (status) {
            "Mild" -> "Try journaling and light exercise."
            "Moderate" -> "Consider meditation and limiting screen time."
            "Severe" -> "Seek professional help and engage in daily meditation."
            else -> ""
        }

        Toast.makeText(this, "Status: $status\nSuggestions: $suggestions", Toast.LENGTH_LONG).show()
        startActivity(Intent(this,MainActivity::class.java))
    }
}
data class Question(
    val id: Int,
    val text: String,
    val options: List<String>
)
