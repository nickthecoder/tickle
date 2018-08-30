/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
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
