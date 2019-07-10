package com.tuanchauict.acb.ime

import android.inputmethodservice.Keyboard
import android.view.KeyEvent

@Suppress("unused")
object Keycode {
    const val SHIFT = -1

    const val ESCAPE = 27
    const val CONTROL = 17
    private const val ENTER = Keyboard.KEYCODE_DONE
    private const val TAB = 9

    const val SPACE = 32

    const val SYMBOL_PLUS = 43
    const val SYMBOL_MINUS = 45
    const val SYMBOL_MULTIPLY = 42
    const val SYMBOL_DIVIDE = 47
    const val SYMBOL_QUOTE = 34
    const val SYMBOL_COLON = 58
    const val SYMBOL_BRACKET = 40
    const val SYMBOL_SQUARE_BRACKET = 91
    const val SYMBOL_ANGLE_BRACKET = 123

    const val SYMBOL_COMMA = 44

    const val DIGIT_0 = 48
    const val DIGIT_1 = 49
    const val DIGIT_2 = 50
    const val DIGIT_3 = 51
    const val DIGIT_4 = 52
    const val DIGIT_5 = 53
    const val DIGIT_6 = 54
    const val DIGIT_7 = 55
    const val DIGIT_8 = 56
    const val DIGIT_9 = 57

    const val LETTER_A = 97
    const val LETTER_B = 98
    const val LETTER_C = 99
    const val LETTER_D = 100
    const val LETTER_E = 101
    const val LETTER_F = 102
    const val LETTER_G = 103
    const val LETTER_H = 104
    const val LETTER_I = 105
    const val LETTER_J = 106
    const val LETTER_K = 107
    const val LETTER_L = 108
    const val LETTER_M = 109
    const val LETTER_N = 110
    const val LETTER_O = 111
    const val LETTER_P = 112
    const val LETTER_Q = 113
    const val LETTER_R = 114
    const val LETTER_S = 115
    const val LETTER_T = 116
    const val LETTER_U = 117
    const val LETTER_V = 118
    const val LETTER_W = 119
    const val LETTER_X = 120
    const val LETTER_Y = 121
    const val LETTER_Z = 122

    const val FUNCTION_SWITCH = -150
    const val FUNCTION_ESC = -1500

    private const val FUNCTION_DELETE = Keyboard.KEYCODE_DELETE

    const val FUNCTION_MOVE_HOME = -1501
    const val FUNCTION_MOVE_END = -1502
    const val FUNCTION_MOVE_TO_FIRST = -1503
    const val FUNCTION_MOVE_TO_LAST = -1504

    const val FUNCTION_DPAD_LEFT = -1505
    const val FUNCTION_DPAD_RIGHT = -1506
    const val FUNCTION_DPAD_UP = -1507
    const val FUNCTION_DPAD_DOWN = -1508

    private const val FUNCTION_SELECT_ALL = -1509

    private const val FUNCTION_CUT = -1510
    private const val FUNCTION_COPY = -1511
    private const val FUNCTION_PASTE = -1512

    private const val FUNCTION_UNDO = -1513
    private const val FUNCTION_REDO = -1514

    val NO_PREVIEW_KEY_CODES: Set<Int> = setOf(
        ENTER,
        TAB,
        SHIFT,
        SPACE,

        SYMBOL_PLUS,
        SYMBOL_MINUS,
        SYMBOL_MULTIPLY,
        SYMBOL_DIVIDE,
        SYMBOL_QUOTE,
        SYMBOL_COLON,
        SYMBOL_BRACKET,
        SYMBOL_SQUARE_BRACKET,
        SYMBOL_ANGLE_BRACKET,

        FUNCTION_SWITCH,
        FUNCTION_ESC,
        FUNCTION_MOVE_HOME,
        FUNCTION_MOVE_END,
        FUNCTION_MOVE_TO_FIRST,
        FUNCTION_MOVE_TO_LAST,
        FUNCTION_DPAD_LEFT,
        FUNCTION_DPAD_RIGHT,
        FUNCTION_DPAD_UP,
        FUNCTION_DPAD_DOWN,
        FUNCTION_SELECT_ALL,
        FUNCTION_CUT,
        FUNCTION_COPY,
        FUNCTION_PASTE,
        FUNCTION_UNDO,
        FUNCTION_REDO,
        FUNCTION_DELETE,

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

    val FUNCTION_KEY_TO_MENU_ACTION_MAP: Map<Int, Int> = mapOf(
        FUNCTION_UNDO to android.R.id.undo,
        FUNCTION_REDO to android.R.id.redo,
        FUNCTION_CUT to android.R.id.cut,
        FUNCTION_COPY to android.R.id.copy,
        FUNCTION_PASTE to android.R.id.paste,
        FUNCTION_SELECT_ALL to android.R.id.selectAll
    )

    val KEY_TO_SIMPLE_DOWN_UP_KEY_EVENT_MAP = mapOf(
        FUNCTION_DELETE to KeyEvent.KEYCODE_DEL,
        ENTER to KeyEvent.KEYCODE_ENTER,
        TAB to KeyEvent.KEYCODE_TAB,
        FUNCTION_DPAD_LEFT to KeyEvent.KEYCODE_DPAD_LEFT,
        FUNCTION_DPAD_RIGHT to KeyEvent.KEYCODE_DPAD_RIGHT,
        FUNCTION_DPAD_DOWN to KeyEvent.KEYCODE_DPAD_DOWN,
        FUNCTION_DPAD_UP to KeyEvent.KEYCODE_DPAD_UP,
        FUNCTION_ESC to KeyEvent.KEYCODE_ESCAPE,
        FUNCTION_MOVE_HOME to KeyEvent.KEYCODE_MOVE_HOME,
        FUNCTION_MOVE_END to KeyEvent.KEYCODE_MOVE_END
    )
    val LONG_KEY_TO_KEY_EVENT_MAP: Map<Int, Int> = mapOf(
        LETTER_A to KeyEvent.KEYCODE_DPAD_LEFT,
        LETTER_D to KeyEvent.KEYCODE_DPAD_RIGHT,
        LETTER_S to KeyEvent.KEYCODE_DPAD_DOWN,
        LETTER_W to KeyEvent.KEYCODE_DPAD_UP,
        LETTER_H to KeyEvent.KEYCODE_MOVE_HOME,
        LETTER_E to KeyEvent.KEYCODE_MOVE_END
    )

    val LONG_KEY_TO_MENU_ACTION_MAP: Map<Int, Int> = mapOf(
        LETTER_X to android.R.id.cut,
        LETTER_C to android.R.id.copy,
        LETTER_V to android.R.id.paste,
        LETTER_Z to android.R.id.undo
    )

    val LONG_PRESS_KEY_CODES: Set<Int> = setOf(
        SPACE,

        SYMBOL_COMMA,
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
    ) + LONG_KEY_TO_MENU_ACTION_MAP.keys + LONG_KEY_TO_KEY_EVENT_MAP.keys
}
