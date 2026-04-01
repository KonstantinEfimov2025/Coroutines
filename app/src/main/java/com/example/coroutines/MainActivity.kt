package com.example.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private var count: Int = 1
    // Создаем область видимости для корутин на главном потоке
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        // Принудительно устанавливаем совместимую тему прямо в коде,
        // чтобы приложение точно не падало из-за стилей
        setTheme(androidx.appcompat.R.style.Theme_AppCompat_Light_DarkActionBar)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Находим элементы интерфейса старым добрым способом
        val countText = findViewById<TextView>(R.id.countText)
        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        val statusText = findViewById<TextView>(R.id.statusText)
        val button = findViewById<Button>(R.id.button)

        // Слушатель для ползунка
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                count = if (progress == 0) 1 else progress
                countText.text = "$count coroutines"
            }
            override fun onStartTrackingTouch(seek: SeekBar) {}
            override fun onStopTrackingTouch(seek: SeekBar) {}
        })

        // Слушатель для кнопки
        button.setOnClickListener {
            launchCoroutines(statusText)
        }
    }

    // Приостанавливаемая функция (задача), которая "спит" 5 секунд
    suspend fun performTask(tasknumber: Int): Deferred<String> =
        coroutineScope.async(Dispatchers.Main) {
            delay(5000) // Имитация долгой работы
            return@async "Finished Coroutine $tasknumber"
        }


    fun launchCoroutines(statusText: TextView) {
        (1..count).forEach {
            statusText.text = "Started Coroutine $it"

            android.util.Log.d("COROUTINE_DEBUG", "Запущена корутина №$it")

            coroutineScope.launch(Dispatchers.Main) {
                val result = performTask(it).await()
                statusText.text = result

                android.util.Log.d("COROUTINE_DEBUG", "--- ЗАВЕРШИЛАСЬ корутина №$it")
            }
        }
    }
}