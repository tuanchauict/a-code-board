package com.gazlaws.codeboard.ime

import android.inputmethodservice.Keyboard

object Keycode {
    const val SELECT_ALL = 53737
    const val CUT = 53738
    const val COPY = 53739
    const val PASTE = 53740
    const val UNDO = 53741
    const val REDO = 53742

    const val SHIFT = -1

    const val DELETE = Keyboard.KEYCODE_DELETE
    const val DONE = Keyboard.KEYCODE_DONE
    const val ESCAPE = 27
    const val SYM_MODE = -15
    const val CONTROL = 17
    const val TAB = 9

    const val DPAD_LEFT = 5000
    const val DPAD_DOWN = 5001
    const val DPAD_UP = 5002
    const val DPAD_RIGHT = 5003

    const val SPACE = 32

    private const val SYMBOL_PLUS = 43
    private const val SYMBOL_MINUS = 45
    private const val SYMBOL_MULTIPLY = 42
    private const val SYMBOL_DIVIDE = 47
    private const val SYMBOL_QUOTE = 34
    private const val SYMBOL_COLON = 58
    private const val SYMBOL_BRACKET = 40
    private const val SYMBOL_SQUARE_BRACKET = 91
    private const val SYMBOL_ANGLE_BRACKET = 123

    private const val DIGIT_0 = 48
    private const val DIGIT_1 = 49
    private const val DIGIT_2 = 50
    private const val DIGIT_3 = 51
    private const val DIGIT_4 = 52
    private const val DIGIT_5 = 53
    private const val DIGIT_6 = 54
    private const val DIGIT_7 = 55
    private const val DIGIT_8 = 56
    private const val DIGIT_9 = 57

    private const val LETTER_A = 97
    private const val LETTER_B = 98
    private const val LETTER_C = 99
    private const val LETTER_D = 100
    private const val LETTER_E = 101
    private const val LETTER_F = 102
    private const val LETTER_G = 103
    private const val LETTER_H = 104
    private const val LETTER_I = 105
    private const val LETTER_J = 106
    private const val LETTER_K = 107
    private const val LETTER_L = 108
    private const val LETTER_M = 109
    private const val LETTER_N = 110
    private const val LETTER_O = 111
    private const val LETTER_P = 112
    private const val LETTER_Q = 113
    private const val LETTER_R = 114
    private const val LETTER_S = 115
    private const val LETTER_T = 116
    private const val LETTER_U = 117
    private const val LETTER_V = 118
    private const val LETTER_W = 119
    private const val LETTER_X = 120
    private const val LETTER_Y = 121
    private const val LETTER_Z = 122


    const val FUNCTION_SWITCH = -150

    val NO_PREVIEW_KEY_CODES: Set<Int> = setOf(
        DELETE,
        DONE,
        TAB,
        SHIFT,
        SPACE,
        FUNCTION_SWITCH,

        SYMBOL_PLUS,
        SYMBOL_MINUS,
        SYMBOL_MULTIPLY,
        SYMBOL_DIVIDE,
        SYMBOL_QUOTE,
        SYMBOL_COLON,
        SYMBOL_BRACKET,
        SYMBOL_SQUARE_BRACKET,
        SYMBOL_ANGLE_BRACKET,

        DIGIT_0,
        DIGIT_1,
        DIGIT_2,
        DIGIT_3,
        DIGIT_4,
        DIGIT_5,
        DIGIT_6,
        DIGIT_7,
        DIGIT_8,
        DIGIT_9
    )

    val LONG_PRESS_KEY_CODES: Set<Int> = setOf(
        SPACE,
        LETTER_W,
        LETTER_A,
        LETTER_S,
        LETTER_D
    )
}
