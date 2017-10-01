package uk.co.nickthecoder.tickle.events

import org.lwjgl.glfw.GLFW.*

private val labelMap = mutableMapOf<String, Key>()
private val codeMap = mutableMapOf<Int, Key>()

enum class Key(val label: String, val code: Int) {

    SPACE("Space", GLFW_KEY_SPACE),
    APOSTROPHE("Apostrophe", GLFW_KEY_APOSTROPHE),
    COMMA("Comma", GLFW_KEY_COMMA),
    MINUS("Minus", GLFW_KEY_MINUS),
    PERIOD("Period", GLFW_KEY_PERIOD),
    SLASH("Slash", GLFW_KEY_SLASH),

    KEY_0("0", GLFW_KEY_0),
    KEY_1("1", GLFW_KEY_1),
    KEY_2("2", GLFW_KEY_2),
    KEY_3("3", GLFW_KEY_3),
    KEY_4("4", GLFW_KEY_4),
    KEY_5("5", GLFW_KEY_5),
    KEY_6("6", GLFW_KEY_6),
    KEY_7("7", GLFW_KEY_7),
    KEY_8("8", GLFW_KEY_8),
    KEY_9("9", GLFW_KEY_9),

    SEMICOLON("Semicolon", GLFW_KEY_SEMICOLON),
    EQUAL("Equals", GLFW_KEY_EQUAL),

    A("A", GLFW_KEY_A),
    B("B", GLFW_KEY_B),
    C("C", GLFW_KEY_C),
    D("D", GLFW_KEY_D),
    E("E", GLFW_KEY_E),
    F("F", GLFW_KEY_F),
    G("G", GLFW_KEY_G),
    H("H", GLFW_KEY_H),
    I("I", GLFW_KEY_I),
    J("J", GLFW_KEY_J),
    K("K", GLFW_KEY_K),
    L("L", GLFW_KEY_L),
    M("M", GLFW_KEY_M),
    N("N", GLFW_KEY_N),
    O("O", GLFW_KEY_O),
    P("P", GLFW_KEY_P),
    Q("Q", GLFW_KEY_Q),
    R("R", GLFW_KEY_R),
    S("S", GLFW_KEY_S),
    T("T", GLFW_KEY_T),
    U("U", GLFW_KEY_U),
    V("V", GLFW_KEY_V),
    W("W", GLFW_KEY_W),
    X("X", GLFW_KEY_X),
    Y("Y", GLFW_KEY_Y),
    Z("Z", GLFW_KEY_Z),

    LEFT_BRACKET("Open Bracket", GLFW_KEY_LEFT_BRACKET),
    BACKSLASH("Back Slash", GLFW_KEY_BACKSLASH),
    RIGHT_BRACKET("Close Bracket", GLFW_KEY_RIGHT_BRACKET),
    GRAVE_ACCENT("Grave Accent", GLFW_KEY_GRAVE_ACCENT),
    WORLD_1("World 1", GLFW_KEY_WORLD_1),
    WORLD_2("World 2", GLFW_KEY_WORLD_2),

    ESCAPE("Escape", GLFW_KEY_ESCAPE),
    ENTER("Enter", GLFW_KEY_ENTER),
    TAB("Tab", GLFW_KEY_TAB),
    BACKSPACE("Backspace", GLFW_KEY_BACKSPACE),
    INSERT("Insert", GLFW_KEY_INSERT),
    DELETE("Delete", GLFW_KEY_DELETE),
    RIGHT("Right", GLFW_KEY_RIGHT),
    LEFT("Left", GLFW_KEY_LEFT),
    DOWN("Down", GLFW_KEY_DOWN),
    UP("Up", GLFW_KEY_UP),
    PAGE_UP("Page Up", GLFW_KEY_PAGE_UP),
    PAGE_DOWN("Page Down", GLFW_KEY_PAGE_DOWN),
    HOME("Home", GLFW_KEY_HOME),
    END("End", GLFW_KEY_END),
    CAPS_LOCK("Caps Lock", GLFW_KEY_CAPS_LOCK),
    SCROLL_LOCK("Scroll Lock", GLFW_KEY_SCROLL_LOCK),
    NUM_LOCK("Num Lock", GLFW_KEY_NUM_LOCK),
    PRINT_SCREEN("Print Screen", GLFW_KEY_PRINT_SCREEN),
    PAUSE("Pause", GLFW_KEY_PAUSE),

    F1("F1", GLFW_KEY_F1),
    F2("F2", GLFW_KEY_F2),
    F3("F3", GLFW_KEY_F3),
    F4("F4", GLFW_KEY_F4),
    F5("F5", GLFW_KEY_F5),
    F6("F6", GLFW_KEY_F6),
    F7("F7", GLFW_KEY_F7),
    F8("F8", GLFW_KEY_F8),
    F9("F9", GLFW_KEY_F9),
    F10("F10", GLFW_KEY_F10),
    F11("F11", GLFW_KEY_F11),
    F12("F12", GLFW_KEY_F12),
    F13("F13", GLFW_KEY_F13),
    F14("F14", GLFW_KEY_F14),
    F15("F15", GLFW_KEY_F15),
    F16("F16", GLFW_KEY_F16),
    F17("F17", GLFW_KEY_F17),
    F18("F18", GLFW_KEY_F18),
    F19("F19", GLFW_KEY_F19),
    F20("F20", GLFW_KEY_F20),
    F21("F21", GLFW_KEY_F21),
    F22("F22", GLFW_KEY_F22),
    F23("F23", GLFW_KEY_F23),
    F24("F24", GLFW_KEY_F24),
    F25("F25", GLFW_KEY_F25),

    KP_0("KP_0", GLFW_KEY_KP_0),
    KP_1("KP_1", GLFW_KEY_KP_1),
    KP_2("KP_2", GLFW_KEY_KP_2),
    KP_3("KP_3", GLFW_KEY_KP_3),
    KP_4("KP_4", GLFW_KEY_KP_4),
    KP_5("KP_5", GLFW_KEY_KP_5),
    KP_6("KP_6", GLFW_KEY_KP_6),
    KP_7("KP_7", GLFW_KEY_KP_7),
    KP_8("KP_8", GLFW_KEY_KP_8),
    KP_9("KP_9", GLFW_KEY_KP_9),

    KP_DECIMAL("KP Decimal", GLFW_KEY_KP_DECIMAL),
    KP_DIVIDE("KP Divide", GLFW_KEY_KP_DIVIDE),
    KP_MULTIPLY(",KP Multiple", GLFW_KEY_KP_MULTIPLY),
    KP_SUBTRACT("KP Subtract", GLFW_KEY_KP_SUBTRACT),
    KP_ADD("KP Add", GLFW_KEY_KP_ADD),
    KP_ENTER("KP Enter", GLFW_KEY_KP_ENTER),
    KP_EQUAL("KP Equals", GLFW_KEY_KP_EQUAL),
    LEFT_SHIFT("Left Shift", GLFW_KEY_LEFT_SHIFT),
    LEFT_CONTROL("Left Control", GLFW_KEY_LEFT_CONTROL),
    LEFT_ALT("Left Alt", GLFW_KEY_LEFT_ALT),
    LEFT_SUPER("Left Super", GLFW_KEY_LEFT_SUPER),
    RIGHT_SHIFT("Right Shift", GLFW_KEY_RIGHT_SHIFT),
    RIGHT_CONTROL("Right Control", GLFW_KEY_RIGHT_CONTROL),
    RIGHT_ALT("Right Alt", GLFW_KEY_RIGHT_ALT),
    RIGHT_SUPER("Right Super", GLFW_KEY_RIGHT_SUPER),
    MENU("Menu", GLFW_KEY_MENU),
    LAST("Last", GLFW_KEY_LAST),

    UNKNOWN("Unknown", -1);

    init {
        labelMap[label] = this
        codeMap[code] = this
    }

    companion object {
        fun forLabel(label: String) = labelMap[label] ?: UNKNOWN
        fun forCode(code: Int) = codeMap[code] ?: UNKNOWN
    }
}
