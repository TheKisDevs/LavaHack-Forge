package com.kisman.cc.util.render.shader.framebuffer

import org.apache.commons.io.IOUtils
import org.lwjgl.opengl.ARBShaderObjects
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import java.nio.charset.Charset

/**
 * @author _kisman_
 * @since 20:36 of 17.08.2022
 */
abstract class Shader(
    fragmentShader : String
) {
    val uniformsRaw = HashMap<String, Int>()
    var program = 0


    init {
        run {
            var vertexShaderID = 0
            var fragmentShaderID = 0

            try {
                /*final InputStream vertexStream = this.getClass().getResourceAsStream("/assets/kismancc/shader/vertex.vert");
            vertexShaderID = this.createShader(IOUtils.toString(vertexStream, Charset.defaultCharset()), 35633);
            IOUtils.closeQuietly(vertexStream);
            final InputStream fragmentStream = this.getClass().getResourceAsStream("/assets/kismancc/shader/fragment/" + fragmentShader);
            fragmentShaderID = this.createShader(IOUtils.toString(fragmentStream, Charset.defaultCharset()), 35632);
            IOUtils.closeQuietly(fragmentStream);*/
                val vertexStream = javaClass.getResourceAsStream(vertex())
                vertexShaderID = createShader(IOUtils.toString(vertexStream, Charset.defaultCharset()), 35633)
                IOUtils.closeQuietly(vertexStream)

                val fragmentStream = javaClass.getResourceAsStream(fragment(fragmentShader))
                fragmentShaderID = createShader(IOUtils.toString(fragmentStream, Charset.defaultCharset()), 35632)
                IOUtils.closeQuietly(fragmentStream)
            } catch(e : Exception) {
                e.printStackTrace()

                return@run
            }

            if(vertexShaderID == 0 || fragmentShaderID == 0) {
                return@run
            }

            program = ARBShaderObjects.glCreateProgramObjectARB()

            if(program == 0) {
                return@run
            }

            ARBShaderObjects.glAttachObjectARB(
                program,
                vertexShaderID
            )

            ARBShaderObjects.glAttachObjectARB(
                program,
                fragmentShaderID
            )

            ARBShaderObjects.glLinkProgramARB(program)
            ARBShaderObjects.glValidateProgramARB(program)
        }
    }

    fun startShader() {
        GL11.glPushMatrix()
        GL20.glUseProgram(program)

        uniformsRaw.clear()

        setupUniforms()
        updateUniforms()
    }

    fun stopShader() {
        GL20.glUseProgram(0)
        GL11.glPopMatrix()
    }

    abstract fun setupUniforms()
    abstract fun updateUniforms()

    private fun createShader(
        source : String,
        type : Int
    ) : Int {
        var shader = 0

        try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(type)

            if(shader == 0) {
                return 0
            }

            ARBShaderObjects.glShaderSourceARB(
                shader,
                source
            )

            ARBShaderObjects.glCompileShaderARB(shader)

            if(ARBShaderObjects.glGetObjectParameteriARB(shader, 35713) == 0) {
                throw RuntimeException(
                    "Error creating shader: ${getLogInfo(shader)}"
                )
            }

            return shader
        } catch(e : Exception) {
            ARBShaderObjects.glDeleteObjectARB(shader)

            throw e
        }
    }

    private fun getLogInfo(i : Int) : String = ARBShaderObjects.glGetInfoLogARB(i, ARBShaderObjects.glGetObjectParameteriARB(i, 35716))

    fun setupUniform(
        name : String
    ) {
        uniformsRaw[name] = GL20.glGetUniformLocation(
            program,
            name
        )
    }
}