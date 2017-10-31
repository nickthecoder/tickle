package uk.co.nickthecoder.tickle.graphics

import org.joml.Matrix4f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.util.Rectd


/**
 * Based upon the following tutorial :
 * https://github.com/SilverTiger/lwjgl3-tutorial/blob/master/src/silvertiger/tutorial/lwjgl/graphic/Renderer.java
 */
class Renderer(val window: Window) {

    private val program = ShaderProgram()
    private var vertexBuffer = VertexBuffer()

    private val projection = Matrix4f()

    private var vertices = MemoryUtil.memAllocFloat(4096)
    private var numVertices: Int = 0
    private var drawing: Boolean = false

    val identityMatrix = Matrix4f()

    private var currentTexture: Texture? = null
    private val currentColor = Color(-1f, -1f, -1f, -1f)

    var currentModelMatrix = identityMatrix

    private var uniColor: Int = -1
    private var uniModel: Int = -1


    init {
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
        val vertexShader = Shader.load(ShaderType.VERTEX_SHADER, Game::class.java.getResourceAsStream("shaders/renderer.vert"))
        val fragmentShader = Shader.load(ShaderType.FRAGMENT_SHADER, Game::class.java.getResourceAsStream("shaders/renderer.frag"))

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

        /* Set model matrix to identity matrix */
        val model = Matrix4f()
        uniModel = program.getUniformLocation("model")
        program.setUniform(uniModel, model)

        /* Set color uniform */
        uniColor = program.getUniformLocation("color")
        program.setUniform(uniColor, Color.white())

        // centerView(0f, 0f)
    }

    fun changeProjection(projection: Matrix4f) {
        val uniProjection = program.getUniformLocation("projection")
        program.setUniform(uniProjection, projection)
    }

    fun clearColor(color: Color) {
        GL11.glClearColor(color.red, color.green, color.blue, color.alpha)
    }

    fun clear() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }

    fun beginView() {
        program.use()
        currentColor.red = -1.12345f // An invalid value, therefore equals tests will fail
        currentTexture = null
    }

    fun endView() {
        if (drawing) {
            end()
        }
        currentTexture?.unbind()
        currentTexture = null
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

    private val WHITE = Color.white()

    fun drawTexture(texture: Texture, worldRect: Rectd, textureRect: Rectd, color: Color = WHITE) {
        drawTexture(texture, worldRect.left, worldRect.bottom, worldRect.right, worldRect.top, textureRect, color, identityMatrix)
    }

    fun drawTexture(texture: Texture, left: Double, bottom: Double, right: Double, top: Double, textureRect: Rectd, color: Color = WHITE) {
        drawTexture(texture, left, bottom, right, top, textureRect, color, identityMatrix)
    }

    fun drawTexture(texture: Texture, left: Double, bottom: Double, right: Double, top: Double, textureRect: Rectd, color: Color = WHITE, modelMatrix: Matrix4f?) {
        val mm = modelMatrix ?: identityMatrix
        if (mm !== currentModelMatrix) {
            flush()
            program.setUniform(uniModel, mm)
            currentModelMatrix = mm
        }

        drawTextureRegion(
                texture,
                left.toFloat(), bottom.toFloat(), right.toFloat(), top.toFloat(),
                textureRect.left.toFloat(), textureRect.bottom.toFloat(), textureRect.right.toFloat(), textureRect.top.toFloat(), color)
    }

    fun drawTextureRegion(
            texture: Texture,
            x1: Float, y1: Float, x2: Float, y2: Float,
            s1: Float, t1: Float, s2: Float, t2: Float,
            color: Color = WHITE) {

        if (currentColor != color) {
            if (drawing) {
                end()
            }
            currentColor.set(color)
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

        // While tinkering, I'm ALWAYS flushing!
        // flush()
    }

    fun delete() {
        MemoryUtil.memFree(vertices)

        vertexBuffer.delete()
        program.delete()
    }

}
