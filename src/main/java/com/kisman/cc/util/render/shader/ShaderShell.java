package com.kisman.cc.util.render.shader;

import com.kisman.cc.Kisman;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class ShaderShell {
    public static ShaderShell ROUNDED_RECT_ALPHA;
    public static ShaderShell ROUNDED_RECT;
    public static ShaderShell BLUR;
    private int shaderID;

    public static String defaultPath = "assets/kismancc/shader/fragment/";
    public static String defaultSuffix = ".shader";

    public ShaderShell(String shaderName) {
        try {
            createShaderFromFile(shaderName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void init() {
        ROUNDED_RECT_ALPHA = new ShaderShell("roundedrect_alpha");
        ROUNDED_RECT = new ShaderShell("roundedrect");
        BLUR = new ShaderShell("blur");
    }

    public void attach() {
        ARBShaderObjects.glUseProgramObjectARB(shaderID);
    }

    public void set1I(String name, int value0) {
        ARBShaderObjects.glUniform1iARB(ARBShaderObjects.glGetUniformLocationARB(shaderID, name), value0);
    }

    public void set1F(String name, float value0) {
        ARBShaderObjects.glUniform1fARB(ARBShaderObjects.glGetUniformLocationARB(shaderID, name), value0);
    }

    public void set2F(String name, float value0, float value1) {
        ARBShaderObjects.glUniform2fARB(ARBShaderObjects.glGetUniformLocationARB(shaderID, name), value0, value1);
    }

    public void set3F(String name, float value0, float value1, float value2) {
        ARBShaderObjects.glUniform3fARB(ARBShaderObjects.glGetUniformLocationARB(shaderID, name), value0, value1,
                value2);
    }

    public void set4F(String name, float value0, float value1, float value2, float value3) {
        ARBShaderObjects.glUniform4fARB(ARBShaderObjects.glGetUniformLocationARB(shaderID, name), value0, value1,
                value2, value3);
    }

    public void detach() {
        ARBShaderObjects.glUseProgramObjectARB(0);
    }

    private void createShaderFromFile(String shaderName) throws IOException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(defaultPath + shaderName + defaultSuffix);

        if(stream == null) {
            Kisman.LOGGER.error("Error while initializing " + shaderName + " shader!!!");
            return;
        }

        createShader(IOUtils.toString(stream, Charset.defaultCharset()));

        /*if (shaderName.equalsIgnoreCase("roundedrect")) {

            createShader("uniform vec4 color;\n" +
                    "uniform vec2 resolution;\n" +
                    "uniform vec2 center;\n" +
                    "uniform vec2 dst;\n" +
                    "uniform float radius;\n" +
                    "\n" +
                    "float rect(vec2 pos, vec2 center, vec2 size) {  \n" +
                    "    return length(max(abs(center - pos) - (size / 2), 0)) - radius;\n" +
                    "}\n" +
                    "\n" +
                    "void main() {\n" +
                    "    vec2 pos = gl_FragCoord.xy;\n" +
                    "\tpos.y = resolution.y - pos.y;\n" +
                    "\tgl_FragColor = vec4(vec3(color), (-rect(pos, center, dst) / radius) * color.a);\n" +
                    "}");
        }*/

    }

    void createShader(String str) {
        int shaderProgram = ARBShaderObjects.glCreateProgramObjectARB();
        if (shaderProgram == 0) {
            System.out.println("PC Issued");
            Minecraft.getMinecraft().shutdown();
            return;
        }
        int shader = ARBShaderObjects.glCreateShaderObjectARB(ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
        ARBShaderObjects.glShaderSourceARB(shader, str);
        ARBShaderObjects.glCompileShaderARB(shader);
        ARBShaderObjects.glAttachObjectARB(shaderProgram, shader);
        ARBShaderObjects.glLinkProgramARB(shaderProgram);
        this.shaderID = shaderProgram;
    }
}