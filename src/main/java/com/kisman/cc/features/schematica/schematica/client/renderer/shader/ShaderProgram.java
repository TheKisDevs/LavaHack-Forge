package com.kisman.cc.features.schematica.schematica.client.renderer.shader;

import com.kisman.cc.features.schematica.schematica.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ShaderProgram {
    private static final Minecraft MINECRAFT = Minecraft.getMinecraft();

    private int program;

    public ShaderProgram(String domain, String vertShaderFilename, String fragShaderFilename) {
        try {
            init(domain, vertShaderFilename, fragShaderFilename);
            if (program > 0) {
                GL20.glUseProgram(this.program);
                GL20.glUniform1i(GL20.glGetUniformLocation(this.program, "texture"), 0);
                GL20.glUseProgram(0);
            }
        } catch (Exception e) {
            Reference.logger.error("Could not initialize shader program!", e);
            program = 0;
        }
    }

    private void init(String domain, String vertShaderFilename, String fragShaderFilename) {
        if (!OpenGlHelper.shadersSupported) {
            program = 0;
            return;
        }

        program = GL20.glCreateProgram();

        int vertShader = loadAndCompileShader(domain, vertShaderFilename, GL20.GL_VERTEX_SHADER);
        int fragShader = loadAndCompileShader(domain, fragShaderFilename, GL20.GL_FRAGMENT_SHADER);

        if (vertShader != 0) {
            GL20.glAttachShader(program, vertShader);
        }

        if (fragShader != 0) {
            GL20.glAttachShader(program, fragShader);
        }

        GL20.glLinkProgram(program);

        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            Reference.logger.error("Could not link shader: {}", GL20.glGetProgramInfoLog(program, 1024));
            GL20.glDeleteProgram(program);
            program = 0;
            return;
        }

        GL20.glValidateProgram(program);

        if (GL20.glGetProgrami(program, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
            Reference.logger.error("Could not validate shader: {}", GL20.glGetProgramInfoLog(program, 1024));
            GL20.glDeleteProgram(program);
            program = 0;
        }
    }

    private int loadAndCompileShader(String domain, String filename, int shaderType) {
        if (filename == null) {
            return 0;
        }

        int handle = GL20.glCreateShader(shaderType);

        if (handle == 0) {
            Reference.logger.error("Could not create shader of type {} for {}: {}", shaderType, filename, GL20.glGetProgramInfoLog(program, 1024));
            return 0;
        }

        String code = loadFile(new ResourceLocation(domain, filename));
        if (code == null) {
            GL20.glDeleteShader(handle);
            return 0;
        }

        GL20.glShaderSource(handle, code);
        GL20.glCompileShader(handle);

        if (GL20.glGetShaderi(handle, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            Reference.logger.error("Could not compile shader {}: {}", filename, GL20.glGetShaderInfoLog(program, 1024));
            GL20.glDeleteShader(handle);
            return 0;
        }

        return handle;
    }

    private String loadFile(ResourceLocation resourceLocation) {
        try {
            StringBuilder code = new StringBuilder();
            InputStream inputStream = MINECRAFT.getResourceManager().getResource(resourceLocation).getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                code.append(line);
                code.append('\n');
            }
            reader.close();

            return code.toString();
        } catch (Exception e) {
            Reference.logger.error("Could not load shader file!", e);
        }

        return null;
    }

    public int getProgram() {
        return program;
    }
}
