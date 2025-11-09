package com.example.playlistmaker // Объявление пакета, к которому принадлежит файл

import android.content.Context // Импорт класса Context для доступа к системным сервисам
import android.os.Bundle // Импорт класса Bundle для сохранения и восстановления состояния
import android.text.Editable // Импорт интерфейса Editable для работы с изменяемым текстом
import android.text.TextWatcher // Импорт интерфейса TextWatcher для отслеживания изменений в EditText
import android.view.View // Импорт базового класса для всех View-компонентов
import android.view.inputmethod.EditorInfo // Импорт констант для действий с клавиатурой (например, кнопка "Done")
import android.view.inputmethod.InputMethodManager // Импорт класса для управления экранной клавиатурой
import android.widget.Button // Импорт класса Button
import android.widget.EditText // Импорт класса EditText для ввода текста
import android.widget.ImageView // Импорт класса ImageView для отображения изображений
import android.widget.LinearLayout // Импорт класса LinearLayout для расположения элементов друг за другом
import android.widget.ProgressBar // Импорт класса ProgressBar для индикатора загрузки
import android.widget.TextView // Импорт класса TextView для отображения текста
import androidx.appcompat.app.AppCompatActivity // Импорт базового класса для активити с поддержкой AppCompat
import androidx.core.view.isVisible // Импорт extension-функции isVisible для удобного управления видимостью
import androidx.recyclerview.widget.RecyclerView // Импорт класса RecyclerView для отображения списков
import com.google.android.material.appbar.MaterialToolbar // Импорт класса Toolbar из библиотеки Material Design
import retrofit2.Call // Импорт класса Call для выполнения сетевых запросов
import retrofit2.Callback // Импорт интерфейса Callback для получения ответов от сервера
import retrofit2.Response // Импорт класса Response для представления ответа от сервера

// Объявление класса SearchActivity, который наследуется от AppCompatActivity
class SearchActivity : AppCompatActivity() {

    // --- ОБЪЯВЛЕНИЕ ПЕРЕМЕННЫХ КЛАССА ---

    // Объявление переменных для View-элементов экрана
    private lateinit var searchEditText: EditText // Поле для ввода поискового запроса
    private lateinit var clearButton: ImageView // Кнопка "крестик" для очистки поля ввода
    private lateinit var recyclerView: RecyclerView // Список для отображения найденных треков
    private lateinit var errorLayout: LinearLayout // Layout, который показывается при ошибке
    private lateinit var noResultsLayout: LinearLayout // Layout, который показывается, если ничего не найдено
    private lateinit var progressBar: ProgressBar // Индикатор загрузки (крутится во время запроса)
    private lateinit var errorImage: ImageView // Картинка для плейсхолдера ошибки
    private lateinit var errorText: TextView // Текст для плейсхолдера ошибки
    private lateinit var retryButton: Button // Кнопка "Обновить" на экране ошибки

    // Переменные для данных и адаптера
    private val tracks = mutableListOf<Track>() // Список, в котором будут храниться данные о треках
    private val trackAdapter = TrackAdapter(tracks) // Адаптер для RecyclerView, связывает данные и их отображение
    private val iTunesService = RetrofitClient.api // Объект для отправки запросов к iTunes API

    // Объект-компаньон для хранения констант
    companion object {
        private const val SEARCH_TEXT = "TEXT" // Ключ для сохранения текста поиска при повороте экрана
    }

    // --- ЖИЗНЕННЫЙ ЦИКЛ ACTIVITY ---

    // Метод, который вызывается при создании Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Вызов родительского метода
        setContentView(R.layout.activity_search) // Установка файла разметки для этого экрана

        // Настройка Toolbar (верхняя панель)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar) // Находим Toolbar по его ID
        toolbar.setNavigationOnClickListener { finish() } // Устанавливаем обработчик нажатия на кнопку "назад"

        // Инициализация всех View-элементов
        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_button)
        recyclerView = findViewById(R.id.recycler_view_tracks)
        errorLayout = findViewById(R.id.errorLayout)
        noResultsLayout = findViewById(R.id.noResultsLayout)
        progressBar = findViewById(R.id.progressBar)
        errorImage = findViewById(R.id.errorImage)
        errorText = findViewById(R.id.errorText)
        retryButton = findViewById(R.id.retryButton)

        // Устанавливаем адаптер для RecyclerView
        recyclerView.adapter = trackAdapter

        // Восстановление состояния после поворота экрана или пересоздания Activity
        if (savedInstanceState != null) {
            // Если есть сохраненное состояние, восстанавливаем текст в поле ввода
            searchEditText.setText(savedInstanceState.getString(SEARCH_TEXT, ""))
        }

        // --- УСТАНОВКА СЛУШАТЕЛЕЙ ---

        // Слушатель нажатия на кнопку очистки текста
        clearButton.setOnClickListener {
            searchEditText.setText("") // Очищаем поле ввода
            hideKeyboard() // Прячем клавиатуру
            tracks.clear() // Очищаем список треков
            trackAdapter.notifyDataSetChanged() // Уведомляем адаптер, что данные изменились
            showPlaceholder(PlaceholderType.EMPTY) // Показываем пустой экран (скрываем все плейсхолдеры)
        }

        // Слушатель нажатия на кнопку "Обновить"
        retryButton.setOnClickListener {
            performSearch() // Повторно выполняем поиск
        }

        // Слушатель изменений в поле ввода текста
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { } // Метод до изменения текста

            // Метод во время изменения текста
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Показываем или скрываем кнопку очистки в зависимости от того, пустое ли поле
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
            override fun afterTextChanged(s: Editable?) { } // Метод после изменения текста
        })

        // Слушатель нажатия на кнопку действия на клавиатуре (например, "Done" или "Search")
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            // Если нажата кнопка "Done"
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch() // Выполняем поиск
                true // Возвращаем true, чтобы система поняла, что событие обработано
            } else {
                false // В остальных случаях возвращаем false
            }
        }
    }

    // Метод для сохранения состояния перед уничтожением Activity
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState) // Вызов родительского метода
        // Сохраняем текущий текст из поля ввода в Bundle
        outState.putString(SEARCH_TEXT, searchEditText.text.toString())
    }

    // --- ОСНОВНАЯ ЛОГИКА ЭКРАНА ---

    // Приватная функция для выполнения поискового запроса
    private fun performSearch() {
        val query = searchEditText.text.toString() // Получаем текст из поля ввода
        if (query.isEmpty()) return // Если запрос пустой, ничего не делаем

        hideKeyboard() // Прячем клавиатуру
        showPlaceholder(PlaceholderType.LOADING) // Показываем индикатор загрузки

        // Выполняем асинхронный сетевой запрос
        iTunesService.searchTracks(query).enqueue(object : Callback<TrackResponse> {
            // Этот код выполнится, когда придет ответ от сервера
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                // Если запрос прошел успешно (код ответа 2xx)
                if (response.isSuccessful) {
                    val foundTracks = response.body()?.results // Получаем список треков из тела ответа
                    // Если список не пустой
                    if (!foundTracks.isNullOrEmpty()) {
                        tracks.clear() // Очищаем старый список
                        tracks.addAll(foundTracks) // Добавляем новые треки
                        trackAdapter.notifyDataSetChanged() // Обновляем отображение списка
                        showPlaceholder(PlaceholderType.EMPTY) // Показываем RecyclerView
                    } else {
                        // Если список пустой, показываем плейсхолдер "Ничего не нашлось"
                        showPlaceholder(PlaceholderType.NO_RESULTS)
                    }
                } else {
                    // Если сервер вернул ошибку (например, 404, 500), показываем плейсхолдер ошибки
                    showPlaceholder(PlaceholderType.ERROR)
                }
            }

            // Этот код выполнится, если произошла ошибка сети (например, нет интернета)
            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                showPlaceholder(PlaceholderType.ERROR) // Показываем плейсхолдер ошибки
            }
        })
    }

    // Enum (перечисление) для управления состоянием UI (что именно показывать пользователю)
    private enum class PlaceholderType {
        EMPTY, // Показать пустой экран (только список, который может быть пустым)
        LOADING, // Показать загрузку
        ERROR, // Показать ошибку
        NO_RESULTS // Показать "Ничего не найдено"
    }

    // Функция для управления видимостью плейсхолдеров
    private fun showPlaceholder(type: PlaceholderType) {
        // Устанавливаем видимость каждого элемента в зависимости от переданного типа
        recyclerView.isVisible = type == PlaceholderType.EMPTY
        progressBar.isVisible = type == PlaceholderType.LOADING
        errorLayout.isVisible = type == PlaceholderType.ERROR
        noResultsLayout.isVisible = type == PlaceholderType.NO_RESULTS

        // Если тип - ОШИБКА, дополнительно настраиваем экран ошибки
        if (type == PlaceholderType.ERROR) {
            errorText.text = getString(R.string.connection_error) // Устанавливаем текст ошибки
            // Устанавливаем картинку, которая будет выбрана автоматически (светлая/темная) благодаря папке drawable-night
            errorImage.setImageResource(R.drawable.placeholder_internet_error)
        }
    }

    // Вспомогательная функция, чтобы спрятать клавиатуру
    private fun hideKeyboard() {
        // Получаем системный сервис для управления вводом
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // Прячем клавиатуру, привязанную к окну нашего поля ввода
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }
}
