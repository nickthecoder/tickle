package uk.co.nickthecoder.tickle.graphics

import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.math.Matrix4
import uk.co.nickthecoder.tickle.math.toRadians
import java.io.File


class Renderer(val window: Window) {

    private val program = ShaderProgram()
    private var vao: VertexArray? = null
    private var vertexBuffer = VertexBuffer()

    private var vertices = MemoryUtil.memAllocFloat(4096)
    private var numVertices: Int = 0
    private var drawing: Boolean = false

    private var currentTexture: Texture? = null

    init {
        println("Creating renderer")

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        if (isLegacy()) {
            println("Is legacy Context")
            vao = null
        } else {
            println("Is default Context")
            /* Generate Vertex Array Object */
            vao = VertexArray()
            vao!!.bind()
        }

        /* Generate Vertex Buffer Object */
        vertexBuffer = VertexBuffer()
        vertexBuffer.bind(Target.ARRAY_BUFFER)

        /* Create FloatBuffer */
        vertices = MemoryUtil.memAllocFloat(4096)

        println("Created vertices size ${vertices.capacity()}")

        /* Upload null data to allocate storage for the VBO */
        val size = (vertices.capacity() * java.lang.Float.BYTES).toLong()
        vertexBuffer.uploadData(Target.ARRAY_BUFFER, size, Usage.DYNAMIC_DRAW)

        /* Initialize variables */
        numVertices = 0
        drawing = false

        println("Created vertex buffer ${vertexBuffer.handle}")

        /* Load shaders */
        val vertexShader: Shader
        val fragmentShader: Shader
        if (isLegacy()) {
            println("Loading legacy shaders")
            vertexShader = Shader.load(ShaderType.VERTEX_SHADER, File(Game.resourceDirectory, "legacy.vert"))
            fragmentShader = Shader.load(ShaderType.FRAGMENT_SHADER, File(Game.resourceDirectory, "legacy.frag"))
        } else {
            println("Loading default shaders")
            vertexShader = Shader.load(ShaderType.VERTEX_SHADER, File(Game.resourceDirectory, "default.vert"))
            fragmentShader = Shader.load(ShaderType.FRAGMENT_SHADER, File(Game.resourceDirectory, "default.frag"))
        }

        program.attachShaders(vertexShader, fragmentShader)
        if (!isLegacy()) {
            program.bindFragmentDataLocation(0, "fragColor")
        }
        program.link()
        program.use()

        println("Used the ShaderProgram ${program.handle}")

        vertexShader.delete()
        fragmentShader.delete()

        /* Specify Vertex Pointers */
        val posAttrib = program.getAttributeLocation("position")
        program.enableVertexAttribute(posAttrib)
        program.pointVertexAttribute(posAttrib, 2, 8 * java.lang.Float.BYTES, 0)

        /* Specify Color Pointer */
        val colAttrib = program.getAttributeLocation("color")
        program.enableVertexAttribute(colAttrib)
        program.pointVertexAttribute(colAttrib, 4, 8 * java.lang.Float.BYTES, 2L * java.lang.Float.BYTES)

        /* Specify Texture Pointer */
        val texAttrib = program.getAttributeLocation("texcoord")
        program.enableVertexAttribute(texAttrib)
        program.pointVertexAttribute(texAttrib, 2, 8 * java.lang.Float.BYTES, 6L * java.lang.Float.BYTES)

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

        centerView(0f, 0f)
    }

    /**
     * Move the view, so that the bottom left is at position (x,y).
     */
    fun centerView(centerX: Float, centerY: Float) {
        changeView(orthographicProjection(centerX, centerY))
    }

    fun rotateViewDegrees(centerX: Float, centerY: Float, degrees: Double) {
        rotateViewRadians(centerX, centerY, toRadians(degrees))
    }

    fun rotateViewRadians(centerX: Float, centerY: Float, radians: Double) {
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

    fun isLegacy(): Boolean {
        return !GL.getCapabilities().OpenGL32
    }

    fun clearColor(color: Color) {
        GL11.glClearColor(color.red, color.green, color.blue, color.alpha)
    }

    fun clear() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }

    fun frameStart() {
        currentTexture = null
    }

    fun frameEnd() {
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

            if (vao != null) {
                vao!!.bind()
            } else {
                vertexBuffer.bind(Target.ARRAY_BUFFER)
            }

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

    fun drawTexture(texture: Texture, x: Float, y: Float, c: Color = Color.WHITE) {
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

        drawTextureRegion(texture, x1, y1, x2, y2, s1, t1, s2, t2, c)
    }

    fun drawTextureRegion(texture: Texture, x: Float, y: Float, regX: Float, regY: Float, regWidth: Float, regHeight: Float) {
        drawTextureRegion(texture, x, y, regX, regY, regWidth, regHeight, Color.WHITE)
    }

    fun drawTextureRegion(texture: Texture, x: Float, y: Float, regX: Float, regY: Float, regWidth: Float, regHeight: Float, c: Color) {
        /* Vertex positions */
        val x1 = x
        val y1 = y
        val x2 = x + regWidth
        val y2 = y + regHeight

        /* Texture coordinates */
        val s1 = regX / texture.width
        val t1 = regY / texture.height
        val s2 = (regX + regWidth) / texture.width
        val t2 = (regY + regHeight) / texture.height

        drawTextureRegion(texture, x1, y1, x2, y2, s1, t1, s2, t2, c)
    }

    fun drawTextureRegion(texture: Texture, x1: Float, y1: Float, x2: Float, y2: Float, s1: Float, t1: Float, s2: Float, t2: Float, color: Color = Color.WHITE) {
        if (currentTexture != texture) {
            if (drawing) {
                end()
            }
            texture.bind()
            begin()
            currentTexture = texture
        }
        if (vertices.remaining() < 8 * 6) {
            /* We need more space in the buffer, so flush it */
            flush()
        }

        val r = color.red
        val g = color.green
        val b = color.blue
        val a = color.alpha

        vertices.put(x1).put(y1).put(r).put(g).put(b).put(a).put(s1).put(t1)
        vertices.put(x1).put(y2).put(r).put(g).put(b).put(a).put(s1).put(t2)
        vertices.put(x2).put(y2).put(r).put(g).put(b).put(a).put(s2).put(t2)

        vertices.put(x1).put(y1).put(r).put(g).put(b).put(a).put(s1).put(t1)
        vertices.put(x2).put(y2).put(r).put(g).put(b).put(a).put(s2).put(t2)
        vertices.put(x2).put(y1).put(r).put(g).put(b).put(a).put(s2).put(t1)

        numVertices += 6
    }

    fun delete() {
        MemoryUtil.memFree(vertices)

        vao?.delete()
        vertexBuffer.delete()
        program.delete()
    }

}
