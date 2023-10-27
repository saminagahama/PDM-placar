package ufc.smd.esqueleto_placar

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.getSystemService
import data.Placar
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.charset.StandardCharsets

class PlacarActivity : AppCompatActivity() {
    lateinit var placar: Placar
    lateinit var tvResultadoJogo: TextView

    var goalsTeamA = 0
    var goalsTeamB = 0

    private var elapsedTime = 0L
    private val handler = Handler()
    private lateinit var timerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_placar)

        placar = intent.extras?.getSerializable("placar") as Placar
        tvResultadoJogo = findViewById(R.id.tvPlacar)
        val tvNomePartida = findViewById<TextView>(R.id.tvNomePartida2)
        tvNomePartida.text = placar.nome_partida

        timerButton = findViewById(R.id.timerButton)
        timerButton.setOnClickListener {
            when {
                elapsedTime < 45 * 60 * 1000 -> startFirstHalfTimer()
                elapsedTime < (45 + 15) * 60 * 1000 -> startBreakTime()
                else -> startSecondHalfTimer()
            }
        }

        ultimoJogos()
        updateScoreDisplay()
    }

    fun addGoalTeamA(v: View) {
        goalsTeamA++
        updateScoreDisplay()
        vibrar(v)
    }

    fun addGoalTeamB(v: View) {
        goalsTeamB++
        updateScoreDisplay()
        vibrar(v)
    }

    private fun updateScoreDisplay() {
        placar.resultado = "$goalsTeamA - $goalsTeamB"
        tvResultadoJogo.text = placar.resultado
    }

    private fun startFirstHalfTimer() {
        handler.postDelayed(timerRunnable, 1000)
    }

    private fun startBreakTime() {
        handler.postDelayed({
            startSecondHalfTimer()
        }, 15 * 60 * 1000)
    }

    private fun startSecondHalfTimer() {
        handler.postDelayed(timerRunnable, 1000)
    }

    private val timerRunnable = object : Runnable {
        override fun run() {
            elapsedTime += 1000

            if (elapsedTime < 45 * 60 * 1000 || (elapsedTime > (45 + 15) * 60 * 1000 && elapsedTime < (45 + 15 + 45) * 60 * 1000)) {
                handler.postDelayed(this, 1000)
            } else {
                // Notify the end of a half or end of the match
            }
        }
    }

    fun vibrar(v: View) {
        val buzzer = getSystemService<Vibrator>()
        val pattern = longArrayOf(0, 200, 100, 300)
        buzzer?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                buzzer.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                buzzer.vibrate(pattern, -1)
            }
        }

    }


    fun saveGame(v: View) {

        val sharedFilename = "PreviousGames"
        val sp: SharedPreferences = getSharedPreferences(sharedFilename, Context.MODE_PRIVATE)
        var edShared = sp.edit()
        //Salvar o número de jogos já armazenados
        var numMatches= sp.getInt("numberMatch",0) + 1
        edShared.putInt("numberMatch", numMatches)

        //Escrita em Bytes de Um objeto Serializável
        var dt= ByteArrayOutputStream()
        var oos = ObjectOutputStream(dt);
        oos.writeObject(placar);

        //Salvar como "match"
        edShared.putString("match"+numMatches, dt.toString(StandardCharsets.ISO_8859_1.name()))
        edShared.commit() //Não esqueçam de comitar!!!

    }

    fun lerUltimosJogos(v: View){
        val sharedFilename = "PreviousGames"
        val sp: SharedPreferences = getSharedPreferences(sharedFilename, Context.MODE_PRIVATE)

        var meuObjString:String= sp.getString("match1","").toString()
        if (meuObjString.length >=1) {
            var dis = ByteArrayInputStream(meuObjString.toByteArray(Charsets.ISO_8859_1))
            var oos = ObjectInputStream(dis)
            var placarAntigo:Placar=oos.readObject() as Placar
            Log.v("SMD26",placar.resultado)
        }
    }




    fun ultimoJogos () {
        val sharedFilename = "PreviousGames"
        val sp:SharedPreferences = getSharedPreferences(sharedFilename,Context.MODE_PRIVATE)
        var matchStr:String=sp.getString("match1","").toString()
       // Log.v("PDM22", matchStr)
        if (matchStr.length >=1){
            var dis = ByteArrayInputStream(matchStr.toByteArray(Charsets.ISO_8859_1))
            var oos = ObjectInputStream(dis)
            var prevPlacar:Placar = oos.readObject() as Placar
            Log.v("PDM22", "Jogo Salvo:"+ prevPlacar.resultado)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(timerRunnable)
    }
}