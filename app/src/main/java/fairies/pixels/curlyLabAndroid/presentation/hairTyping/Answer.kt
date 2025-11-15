package fairies.pixels.curlyLabAndroid.presentation.hairTyping

data class Answer (
    val result: String,
    val text: String,
    var isSelected: Boolean
)

enum class PorosityTypes(val code: String, val dbCode: String, val resultName: String) {
    POROUS("p", "HIGH", "пористые"),
    SEMI_POROUS("s", "MEDIUM", "среднепористые"),
    NON_POROUS("n", "LOW", "низкопористые")
}

enum class ThicknessTypes(val code: String, val dbCode: String, val resultName: String) {
    THIN("t", "THIN", "тонкие"),
    MEDIUM("m", "MEDIUM","средние"),
    BOLD("b", "THICK","толстые")
}


enum class ColoredTypes(val code: String, val result: String) {
    COLORED("c", "окрашенные"),
    NOT_COLORED("n", "неокрашенные"),
}