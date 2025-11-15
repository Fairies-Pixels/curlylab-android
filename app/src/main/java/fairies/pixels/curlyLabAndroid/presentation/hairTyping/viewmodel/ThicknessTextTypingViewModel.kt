package fairies.pixels.curlyLabAndroid.presentation.hairTyping.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fairies.pixels.curlyLabAndroid.data.remote.model.request.profile.HairTypeRequest
import fairies.pixels.curlyLabAndroid.domain.repository.profile.HairTypesRepository
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.Answer
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.Question
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.ThicknessTypes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThicknessTextTypingViewModel @Inject constructor(
    private val hairTypesRepository: HairTypesRepository
) : ViewModel() {

    private val _questions = MutableStateFlow<List<Question>>(porosityQuestions)
    val questions: StateFlow<List<Question>> = _questions.asStateFlow()

    private val _currentQuestionId = MutableStateFlow<Int>(0)
    val currentQuestionId: StateFlow<Int> = _currentQuestionId.asStateFlow()

    private val _result = MutableStateFlow<ThicknessTypes?>(null)
    val result: StateFlow<ThicknessTypes?> = _result.asStateFlow()

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
        val thin = resultString.count { it.toString() == ThicknessTypes.THIN.code }
        val medium = resultString.count { it.toString() == ThicknessTypes.MEDIUM.code }
        val bold = resultString.count { it.toString() == ThicknessTypes.BOLD.code }

        val max = maxOf(thin, medium, bold)

        _result.value = when (max) {
            bold -> ThicknessTypes.BOLD
            thin -> ThicknessTypes.THIN
            else -> ThicknessTypes.MEDIUM
        }
    }

    fun saveResult() {
        viewModelScope.launch {
            try {
                val userId = "07eeb4d3-9c17-4aa6-89bd-5e080385520b"
                try {
                    hairTypesRepository.updateHairType(
                        userId,
                        HairTypeRequest(
                            userId = userId,
                            thickness = _result.value?.dbCode
                        )
                    )
                    _saved.value = true
                } catch (e: Exception) {
                    _saved.value = false
                }
            } catch (e: Exception) {
                _saved.value = false
            }
        }
    }

    companion object {
        val porosityQuestions = listOf(
            Question(
                topic = "Возьмите карандаш/ручку, намотайте волос виток к витку. Ширину всех витков поделите на число витков",
                answers = listOf(
                    Answer(
                        ThicknessTypes.BOLD.code,
                        "Более 0,07 мм",
                        true
                    ),
                    Answer(
                        ThicknessTypes.MEDIUM.code,
                        "0,05-0,07 мм",
                        false
                    ),
                    Answer(
                        ThicknessTypes.THIN.code,
                        "Менее 0,05 мм",
                        false
                    ),
                    Answer(
                        "",
                        "Я не умею/не хочу считать",
                        false
                    ),
                )
            ),
            Question(
                topic = "Поднесите волос к окну и рассмотри на свет",
                answers = listOf(
                    Answer(
                        ThicknessTypes.BOLD.code,
                        "Волосок широкий, крупный, его отчётливо видно",
                        true
                    ),
                    Answer(
                        ThicknessTypes.THIN.code,
                        "Волос настолько тонкий, что его еле заметно на свету",
                        false
                    ),
                    Answer(
                        ThicknessTypes.MEDIUM.code,
                        "Что-то среднее",
                        false
                    )
                )
            )
        )
    }
}