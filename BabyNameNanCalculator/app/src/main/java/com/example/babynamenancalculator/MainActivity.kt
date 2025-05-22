package com.example.babynamenancalculator

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val boyNames = mapOf(
        "Ayaan" to "Gift of God", "Vihaan" to "Dawn", "Rudra" to "Lord Shiva", "Neil" to "Champion",
        "Arjun" to "Bright", "Kunal" to "Lotus", "Liam" to "Warrior", "Rayan" to "Gates of Heaven",
        "Aryan" to "Noble", "Kabir" to "Great", "Omar" to "Long life", "Sahil" to "Guide",
        "Zayn" to "Beauty", "Tanish" to "Ambition", "Dev" to "God", "Yuvan" to "Youthful",
        "Ansh" to "Portion", "Nirav" to "Calm", "Ishaan" to "Sun", "Rahul" to "Efficient",
        "Advik" to "Unique", "Reyan" to "Fame", "Shaurya" to "Bravery", "Pranav" to "Sacred",
        "Darsh" to "Sight"
    )

    private val girlNames = mapOf(
        "Anaya" to "Care", "Riya" to "Singer", "Kiara" to "Bright", "Emma" to "Universal",
        "Zoya" to "Alive", "Myra" to "Beloved", "Isha" to "Goddess", "Tara" to "Star",
        "Nia" to "Purpose", "Aaradhya" to "Worshipped", "Siya" to "Goddess Sita", "Lara" to "Cheerful",
        "Nikita" to "Victorious", "Pia" to "Pious", "Meera" to "Saintly", "Avni" to "Earth",
        "Navya" to "New", "Saanvi" to "Goddess Lakshmi", "Kriti" to "Creation", "Disha" to "Direction",
        "Ira" to "Earth", "Amaira" to "Beautiful", "Trisha" to "Wish", "Bhavya" to "Magnificent",
        "Ishita" to "Desire"
    )

    private val unisexNames = mapOf(
        "Ariel" to "Lion of God", "Jordan" to "Flowing down", "Rey" to "Royal", "Taylor" to "Cutter",
        "Sky" to "Limitless", "Robin" to "Fame", "Alex" to "Protector", "Dakota" to "Ally",
        "Sam" to "Listener", "Jamie" to "Supplanter", "Riley" to "Valiant", "Casey" to "Alert",
        "Devan" to "Godlike", "Arya" to "Noble", "Sasha" to "Defender", "Rowan" to "Little redhead",
        "Morgan" to "Sea-born", "Bailey" to "Steward", "Cameron" to "Crooked nose",
        "Harley" to "Meadow", "Drew" to "Strong", "Jesse" to "Gift", "Kiran" to "Ray of light",
        "Milan" to "Union", "Noel" to "Christmas"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editParent1 = findViewById<EditText>(R.id.editParent1)
        val editParent2 = findViewById<EditText>(R.id.editParent2)
        val editStartLetter = findViewById<EditText>(R.id.editStartLetter)
        val genderGroup = findViewById<RadioGroup>(R.id.genderGroup)
        val btnGenerate = findViewById<Button>(R.id.btnGenerate)
        val textResult = findViewById<TextView>(R.id.textResult)
        val btnPopup = findViewById<Button>(R.id.btnPopup)

        btnPopup.setOnClickListener {
            showInstructionsPopup()
        }

        btnGenerate.setOnClickListener {
            val parent1 = editParent1.text.toString().trim()
            val parent2 = editParent2.text.toString().trim()
            val startLetter = editStartLetter.text.toString().trim().lowercase()
            val selectedGenderId = genderGroup.checkedRadioButtonId

            if (parent1.isNotEmpty() && parent2.isNotEmpty()) {
                val part1 = parent1.take(3).replaceFirstChar { it.uppercase() }
                val part2 = parent2.takeLast(3).replaceFirstChar { it.lowercase() }
                val babyName = (part1 + part2).replace(" ", "")
                textResult.text = "Custom Baby Name: $babyName\nMeaning: Combination of parent names"
                return@setOnClickListener
            }

            val nameMap = when (selectedGenderId) {
                R.id.radioBoy -> boyNames
                R.id.radioGirl -> girlNames
                R.id.radioUnisex -> unisexNames
                else -> emptyMap()
            }

            val filteredNames = if (startLetter.isNotEmpty()) {
                nameMap.filterKeys { it.lowercase().startsWith(startLetter) }
            } else {
                nameMap
            }

            if (filteredNames.isNotEmpty()) {
                val selected = filteredNames.entries.random()
                val name = selected.key
                val meaning = selected.value
                textResult.text = "Baby Name: $name\nMeaning: $meaning"
            } else {
                textResult.text = "No matching name found!"
            }
        }
    }


    private fun showInstructionsPopup() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("How to Use")
        dialogBuilder.setMessage(
            "Enter parent names to create a unique name combination.\n\n" +
                    "Or choose a gender and starting letter to get name suggestions with meanings."
        )
        dialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        dialogBuilder.show()
    }
}
