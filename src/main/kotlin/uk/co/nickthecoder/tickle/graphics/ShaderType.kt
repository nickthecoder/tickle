package uk.co.nickthecoder.tickle.graphics

import org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER
import org.lwjgl.opengl.GL20.GL_VERTEX_SHADER
import org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER
import org.lwjgl.opengl.GL40.GL_TESS_CONTROL_SHADER
import org.lwjgl.opengl.GL40.GL_TESS_EVALUATION_SHADER

enum class ShaderType(val value: Int) {
    VERTEX_SHADER(GL_VERTEX_SHADER),
    FRAGMENT_SHADER(GL_FRAGMENT_SHADER),
    GEOMETRY_SHADER(GL_GEOMETRY_SHADER),
    TESS_CONTROL_SHADER(GL_TESS_CONTROL_SHADER),
    TESS_EVALUATION_SHADER(GL_TESS_EVALUATION_SHADER)
}
