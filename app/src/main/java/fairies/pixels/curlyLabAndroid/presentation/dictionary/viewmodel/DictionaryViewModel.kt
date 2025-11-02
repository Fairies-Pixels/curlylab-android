package fairies.pixels.curlyLabAndroid.presentation.dictionary.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DictionaryViewModel : ViewModel() {
    private val _words = MutableStateFlow<List<Word>>(emptyList())
    val words: StateFlow<List<Word>> get() = _words
    private var searchQuery = ""
    private val constWords = listOf(
        Word("Афропик", "Гребень для кудрявых волос, имеющий длинные, но редкие зубчики, предназначенный для бережного распутывания волос"),
        Word("Диффузор", "Насадка для фена в виде чаши с зубчиками, обеспечивающая равномерное распределение волос. Такая форма помогает сделать завиток более упругим"),
        Word("Капор", "Головной убор в виде съемного капюшона. В версии кудрявого метода приветствуются капоры с шелковой подкладкой, которые помогут уменьшить трение и сохранить влагу, не повреждая волосы"),
        Word("Ливин", "Несмываемый уход, который обычно используют перед стайлинговыми средствами для сохранения влаги"),
        Word("Стайлинг", "Процесс, позволяющий придать волосам необходимую текстуру и сохранить завиток. Для кудрявых волос стайлинг включает использование различных средств (например, гели, кремы, муссы, пенки в различных комбинациях) и техник"),
        Word("LOC", "Стайлинг, состоящий из 3 средств: ливина (leave-in), масла (oil) и крема (cream). Возможна вариативность в порядке нанесения"),
        Word("LOG", "Стайлинг, состоящий из 3 средств: ливина (leave-in), масла (oil) и геля (gel). Возможна вариативность в порядке нанесения"),
        Word("Smasters", "Техника разделения стайлинга на 2 этапа: нанесение одной части средств на мокрые и другой — на сухие волосы"),
        Word("Squish to condish", "Метод смывания кондиционера, согласно которому необходимо набрать в ладони немного воды, уложить в них локоны и начать жамкать")
    )

    init {
        loadWords(constWords)
    }

    private fun loadWords(newWords: List<Word>) {
        _words.value = newWords
    }

    fun filterWords(query: String) {
        searchQuery = query
        if (query.isEmpty()) {
            loadWords(constWords)
        } else {
            loadWords(constWords.filter {
                it.name.contains(query, ignoreCase = true)
            })
        }
    }
}

data class Word(
    val name: String,
    val description: String
)