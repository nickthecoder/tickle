package uk.co.nickthecoder.tickle.graphics

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.math.Matrix4
import java.io.File


/**
 * Based upon the following tutorial :
 * https://github.com/SilverTiger/lwjgl3-tutorial/blob/master/src/silvertiger/tutorial/lwjgl/graphic/Renderer.java
 */
class Renderer(val window: Window) {

    private val program = ShaderProgram()
    private var vertexBuffer = VertexBuffer()

    private var vertices = MemoryUtil.memAllocFloat(4096)
    private var numVertices: Int = 0
    private var drawing: Boolean = false

    private var currentTexture: Texture? = null

    private var uniColor: Int = 0

    init {
        println("Creating renderer")

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        /* Generate Vertex Buffer Object */
        vertexBuffer = VertexBuffer()
        vertexBuffer.bind(Target.ARRAY_BUFFER)

        /* Create FloatBuffer */
        vertices = MemoryUtil.memAllocFloat(4096)

        /* Upload null data to allocate storage for the VBO */
        val size = (vertices.capacity() * java.lang.Float.BYTES).toLong()
        vertexBuffer.uploadData(Target.ARRAY_BUFFER, size, Usage.DYNAMIC_DRAW)

        /* Initialize variables */
        numVertices = 0
        drawing = false

        /* Load shaders */
        val vertexShader: Shader
        val fragmentShader: Shader
        println("Loading shaders")
        val shadersDir = File(Game.resourceDirectory, "shaders")
        vertexShader = Shader.load(ShaderType.VERTEX_SHADER, File(shadersDir, "legacy.vert"))
        fragmentShader = Shader.load(ShaderType.FRAGMENT_SHADER, File(shadersDir, "legacy.frag"))

        program.attachShaders(vertexShader, fragmentShader)
        program.link()
        program.use()

        vertexShader.delete()
        fragmentShader.delete()

        /* Specify Vertex Pointers */
        val posAttrib = program.getAttributeLocation("position")
        program.enableVertexAttribute(posAttrib)
        program.pointVertexAttribute(posAttrib, 2, 4 * java.lang.Float.BYTES, 0)

        /* Specify Texture Pointer */
        val texAttrib = program.getAttributeLocation("texcoord")
        program.enableVertexAttribute(texAttrib)
        program.pointVertexAttribute(texAttrib, 2, 4 * java.lang.Float.BYTES, 2L * java.lang.Float.BYTES)

        /* Set texture uniform */
        val uniTex = program.getUniformLocation("texImage")
        program.setUniform(uniTex, 0)

        /* Set model matrix to identity matrix */
        val model = Matrix4()
        val uniModel = program.getUniformLocation("model")
        program.setUniform(uniModel, model)

        /* Set view matrix to identity matrix */
        val view = Matrix4()
        val uniView = program.getUniformLocation("view")
        program.setUniform(uniView, view)

        /* Set color uniform */
        uniColor = program.getUniformLocation("color")
        program.setUniform(uniColor, Color.SEMI_TRANSPARENT)

        centerView(0f, 0f)
    }

    /**
     * Move the view, so that the bottom left is at position (x,y).
     */
    fun centerView(centerX: Float, centerY: Float) {
        changeView(orthographicProjection(centerX, centerY))
    }

    fun rotateView(centerX: Float, centerY: Float, radians: Double) {
        changeView(orthographicProjection(centerX, centerY) * Matrix4.zRotation(centerX, centerY, radians))
    }

    fun orthographicProjection(centerX: Float, centerY: Float): Matrix4 {
        val w = window.width.toFloat()
        val h = window.height.toFloat()
        return Matrix4.orthographic(
                left = centerX - w / 2, right = centerX + w / 2,
                bottom = centerY - h / 2, top = centerY + h / 2,
                near = -1f, far = 1f)
    }

    fun changeView(projection: Matrix4) {
        val uniProjection = program.getUniformLocation("projection")
        program.setUniform(uniProjection, projection)
    }

    fun clearColor(color: Color) {
        GL11.glClearColor(color.red, color.green, color.blue, color.alpha)
    }

    fun clear() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }

    fun beginFrame() {
        currentColor = null
        currentTexture = null
    }

    fun endFrame() {
        if (drawing) {
            end()
        }
        currentTexture?.unbind()
        currentTexture = null
        currentColor = null
    }

    fun begin() {
        if (drawing) {
            throw IllegalStateException("Renderer is already drawing!")
        }
        drawing = true
        numVertices = 0
    }

    fun end() {
        if (!drawing) {
            throw IllegalStateException("Renderer isn't drawing!")
        }
        drawing = false
        flush()
    }

    fun flush() {
        if (numVertices > 0) {
            vertices.flip()

            /* Upload the new vertex data */
            vertexBuffer.bind(Target.ARRAY_BUFFER)
            vertexBuffer.uploadSubData(Target.ARRAY_BUFFER, 0, vertices)

            /* Draw batch */
            glDrawArrays(GL_TRIANGLES, 0, numVertices)

            /* Clear vertex data for next batch */
            vertices.clear()
            numVertices = 0
        }
    }

    fun drawTexture(texture: Texture, x: Float, y: Float, color: Color = Color.WHITE) {
        /* Vertex positions */
        val x1 = x
        val y1 = y
        val x2 = x1 + texture.width
        val y2 = y1 + texture.height

        /* Texture coordinates */
        val s1 = 0f
        val t1 = 0f
        val s2 = 1f
        val t2 = 1f

        drawTextureRegion(texture, x1, y1, x2, y2, s1, t1, s2, t2, color)
    }

    var currentColor: Color? = null

    fun drawTextureRegion(
            texture: Texture,
            x1: Float, y1: Float, x2: Float, y2: Float,
            s1: Float, t1: Float, s2: Float, t2: Float,
            color: Color = Color.WHITE) {

        if (currentColor != color) {
            if (drawing) {
                end()
            }
            currentColor = color
            program.setUniform(uniColor, color)
        }

        if (currentTexture != texture) {
            if (drawing) {
                end()
            }
            texture.bind()
            currentTexture = texture
        }
        if (vertices.remaining() < 8 * 6) {
            /* We need more space in the buffer, so flush it */
            flush()
        }

        if (!drawing) {
            begin()
        }
        // Add the two triangles which make up our rectangle
        vertices.put(x1).put(y1).put(s1).put(t1)
        vertices.put(x1).put(y2).put(s1).put(t2)
        vertices.put(x2).put(y2).put(s2).put(t2)

        vertices.put(x1).put(y1).put(s1).put(t1)
        vertices.put(x2).put(y2).put(s2).put(t2)
        vertices.put(x2).put(y1).put(s2).put(t1)

        numVertices += 6
    }

    fun delete() {
        MemoryUtil.memFree(vertices)

        vertexBuffer.delete()
        program.delete()
    }

}
