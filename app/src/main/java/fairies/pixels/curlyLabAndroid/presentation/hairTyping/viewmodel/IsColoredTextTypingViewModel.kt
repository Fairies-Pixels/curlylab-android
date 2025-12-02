package fairies.pixels.curlyLabAndroid.presentation.hairTyping.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fairies.pixels.curlyLabAndroid.data.local.AuthDataStore
import fairies.pixels.curlyLabAndroid.data.remote.model.request.profile.HairTypeRequest
import fairies.pixels.curlyLabAndroid.domain.repository.profile.HairTypesRepository
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.Answer
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.ColoredTypes
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.Question
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IsColoredTextTypingViewModel @Inject constructor(
    private val hairTypesRepository: HairTypesRepository,
    private val authDataStore: AuthDataStore
) : ViewModel() {

    private val _questions = MutableStateFlow(porosityQuestions)
    val questions: StateFlow<List<Question>> = _questions.asStateFlow()

    private val _currentQuestionId = MutableStateFlow(0)
    val currentQuestionId: StateFlow<Int> = _currentQuestionId.asStateFlow()

    private val _result = MutableStateFlow<String?>(null)
    val result: StateFlow<String?> = _result.asStateFlow()

    private val _saved = MutableStateFlow<Boolean?>(null)
    val saved: StateFlow<Boolean?> = _saved.asStateFlow()

    fun answering(answerId: Int, questionId: Int) {
        _questions.value = _questions.value.mapIndexed { idQuestion, question ->
            if (idQuestion == questionId) {
                Question(
                    question.topic,
                    question.answers.mapIndexed { id, it ->
                        Answer(
                            text = it.text,
                            isSelected = id == answerId,
                            result = it.result
                        )
                    }
                )
            } else {
                question
            }
        }
    }

    fun nextQuestion() {
        _currentQuestionId.value += 1
    }

    fun previousQuestion() {
        _currentQuestionId.value -= 1
    }

    fun getResult() {
        var resultString = ""
        _questions.value.forEach {
            resultString += it.answers.first { it.isSelected }.result
        }
        val colored = resultString.count { it.toString() == ColoredTypes.COLORED.code }
        val notColored = resultString.count { it.toString() == ColoredTypes.NOT_COLORED.code }

        val max = maxOf(colored, notColored)

        _result.value = when (max) {
            colored -> ColoredTypes.COLORED.result
            else -> ColoredTypes.NOT_COLORED.result
        }
    }

    fun saveResult() {
        viewModelScope.launch {
            try {
                val userId = authDataStore.getUserId()
                if (userId != null) {
                    try {
                        hairTypesRepository.updateHairType(
                            userId,
                            HairTypeRequest(
                                userId = userId,
                                isColored = _result.value.toString() == ColoredTypes.COLORED.result
                            )
                        )
                        _saved.value = true
                    } catch (e: Exception) {
                        _saved.value = false
                    }
                }
            } catch (e: Exception) {
                _saved.value = false
            }
        }
    }

    companion object {
        val porosityQuestions = listOf(
            Question(
                topic = "У вас окрашенные волосы? :)",
                answers = listOf(
                    Answer(
                        ColoredTypes.COLORED.code,
                        "Да",
                        true
                    ),
                    Answer(
                        ColoredTypes.NOT_COLORED.code,
                        "Нет",
                        false
                    )
                )
            )
        )
    }
}