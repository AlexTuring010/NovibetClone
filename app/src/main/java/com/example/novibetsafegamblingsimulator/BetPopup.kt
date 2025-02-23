import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.novibetsafegamblingsimulator.R

class BetPopup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val depositButton = findViewById<Button>(R.id.depositButton) // Use standard Button
        depositButton.setOnClickListener {
            showBetAmountDialog()
        }
    }

    private fun showBetAmountDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter Bet Amount")

        // Set up the input
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("Deposit") { dialog, which ->
            val betAmountString = input.text.toString()
            if (betAmountString.isNotEmpty()) {
                val betAmount = betAmountString.toDoubleOrNull()
                if (betAmount != null && betAmount > 0) {
                    // Process the bet amount
                    Toast.makeText(this, "Bet amount: $betAmount", Toast.LENGTH_SHORT).show()
                    // Handle the actual deposit logic here
                } else {
                    Toast.makeText(this, "Invalid bet amount", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a bet amount", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        builder.show()
    }
}
