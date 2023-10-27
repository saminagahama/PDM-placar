package ufc.smd.esqueleto_placar

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Switch
import data.Placar

class ConfigActivity : AppCompatActivity() {
    var placar: Placar = Placar("Jogo sem Config", "0 - 0", "20/05/20 10h", false) // Adjusted score format

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        openConfig()
        initInterface()
    }

    fun saveConfig() {
        val sharedFilename = "configPlacar"
        val sp: SharedPreferences = getSharedPreferences(sharedFilename, Context.MODE_PRIVATE)
        var edShared = sp.edit()

        edShared.putString("matchname", placar.nome_partida)
        edShared.putBoolean("has_timer", placar.has_timer)
        edShared.commit()
    }

    fun openConfig() {
        val sharedFilename = "configPlacar"
        val sp: SharedPreferences = getSharedPreferences(sharedFilename, Context.MODE_PRIVATE)
        placar.nome_partida = sp.getString("matchname", "Jogo Padrão").toString()
        placar.has_timer = sp.getBoolean("has_timer", false)
    }

    fun initInterface() {
        val tv = findViewById<EditText>(R.id.editTextGameName)
        tv.setText(placar.nome_partida)
        val sw = findViewById<Switch>(R.id.swTimer)
        sw.isChecked = placar.has_timer
    }

    fun updatePlacarConfig() {
        val tv = findViewById<EditText>(R.id.editTextGameName)
        val sw = findViewById<Switch>(R.id.swTimer)
        placar.nome_partida = tv.text.toString()
        placar.has_timer = sw.isChecked
    }

    fun openPlacar(v: View) {
        updatePlacarConfig() // Get data from the interface and update the placar
        saveConfig() // Save to shared preferences
        val intent = Intent(this, PlacarActivity::class.java).apply {
            putExtra("placar", placar)
        }
        startActivity(intent)
    }
}
