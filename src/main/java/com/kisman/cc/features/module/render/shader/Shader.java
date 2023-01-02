package com.kisman.cc.features.module.render.shader;

import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.*;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

public abstract class Shader {
    public String name;
    public int program;
    public Map<String, Integer> uniformsMap;

    public Shader(String fragmentShader) {
        this.name = fragmentShader;

        int vertexShaderID = 0;
        int fragmentShaderID = 0;

        try {
            InputStream vertexStream = this.getClass().getResourceAsStream("/assets/kismancc/shader/vertex.vert");
            if(vertexStream != null) vertexShaderID = this.createShader(IOUtils.toString(vertexStream, Charset.defaultCharset()), ARBVertexShader.GL_VERTEX_SHADER_ARB);
            IOUtils.closeQuietly(vertexStream);
            InputStream fragmentStream = this.getClass().getResourceAsStream("/assets/kismancc/shader/fragment/" + fragmentShader);
            if(fragmentStream != null) fragmentShaderID = this.createShader(IOUtils.toString(fragmentStream, Charset.defaultCharset()), ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
            IOUtils.closeQuietly(fragmentStream);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (vertexShaderID == 0 || fragmentShaderID == 0) return;

        program = ARBShaderObjects.glCreateProgramObjectARB();
        if (program == 0) return;

        ARBShaderObjects.glAttachObjectARB(program, vertexShaderID);
        ARBShaderObjects.glAttachObjectARB(program, fragmentShaderID);
        ARBShaderObjects.glLinkProgramARB(program);
        ARBShaderObjects.glValidateProgramARB(program);
    }

    public void startShader() {
        GL11.glPushMatrix();
        GL20.glUseProgram(program);
        if (uniformsMap == null) {
            uniformsMap = new HashMap<>();
            setupUniforms();
        }
        updateUniforms();
    }

    public void stopShader() {
        GL20.glUseProgram(0);
        GL11.glPopMatrix();
    }

    public void setupUniforms() {}
    public void updateUniforms() {}

    public int createShader(String shaderSource, int shaderType) {
        int shader = 0;
        try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
            if (shader == 0) return 0;
            ARBShaderObjects.glShaderSourceARB(shader, shaderSource);
            ARBShaderObjects.glCompileShaderARB(shader);
            if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE) throw new RuntimeException("Error creating shader: " + this.getLogInfo(shader));
            return shader;
        } catch (Exception e) {
            ARBShaderObjects.glDeleteObjectARB(shader);
            throw e;
        }
    }

    public String getLogInfo(int i) {
        return ARBShaderObjects.glGetInfoLogARB(i, ARBShaderObjects.glGetObjectParameteriARB(i, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
    }

    public void setUniform(String uniformName, int location) {
        uniformsMap.put(uniformName, location);
    }

    public void setupUniform(String uniformName) {
        setUniform(uniformName, GL20.glGetUniformLocation(this.program, uniformName));
    }

    public int getUniform(String uniformName) {
        return uniformsMap.get(uniformName);
    }

    public int getProgramId() {
        return program;
    }
}
