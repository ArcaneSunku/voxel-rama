package atomixsoft.dev.graphics;

import atomixsoft.dev.util.Files;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.opengl.GL20.*;

public final class Shader {

    private int m_ProgramId;

    public Shader(String vertexShaderResource, String fragmentShaderResource) {
        String vertexSource = Files.ReadFromFile(vertexShaderResource);
        String fragmentSource = Files.ReadFromFile(fragmentShaderResource);

        int vertexShaderId = compileShader(GL_VERTEX_SHADER, vertexSource, vertexShaderResource);
        int fragmentShaderId = compileShader(GL_FRAGMENT_SHADER, fragmentSource, fragmentShaderResource);

        try {
            m_ProgramId = linkProgram(vertexShaderId, fragmentShaderId);
        } finally {
            glDeleteShader(vertexShaderId);
            glDeleteShader(fragmentShaderId);
        }
    }

    private static int compileShader(int shaderType, String source, String resourcePath) {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0)
            throw new IllegalStateException("Failed to create OpenGL shader object for: " + resourcePath);

        glShaderSource(shaderId, source);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == MemoryUtil.NULL) {
            String infoLog = glGetShaderInfoLog(shaderId);

            glDeleteShader(shaderId);

            throw new IllegalStateException("Failed to compile shader: " + resourcePath + System.lineSeparator() + infoLog);
        }

        return shaderId;
    }

    private static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        int programId = glCreateProgram();
        if (programId == 0)
            throw new IllegalStateException("Failed to create OpenGL shader program.");

        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);

        glLinkProgram(programId);

        if (glGetProgrami(programId, GL_LINK_STATUS) == MemoryUtil.NULL) {
            String infoLog = glGetProgramInfoLog(programId);

            glDeleteProgram(programId);

            throw new IllegalStateException("Failed to link OpenGL shader program." + System.lineSeparator() + infoLog);
        }

        glDetachShader(programId, vertexShaderId);
        glDetachShader(programId, fragmentShaderId);

        return programId;
    }

    public void bind() {
        ensureCreated();
        glUseProgram(m_ProgramId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void dispose() {
        if (m_ProgramId == 0)
            return;

        glDeleteProgram(m_ProgramId);
        m_ProgramId = 0;
    }

    public void setUniform(String name, Matrix4f value) {
        ensureCreated();

        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            value.get(buffer);

            glUniformMatrix4fv(getLocation(name), false, buffer);
        }
    }

    private void ensureCreated() {
        if (m_ProgramId == 0)
            throw new IllegalStateException("The shader program has been disposed.");
    }

    private int getLocation(String name) {
        ensureCreated();

        if(name == null || name.isBlank())
            throw new IllegalArgumentException("Uniform name cannot be null or blank!");

        int location = glGetUniformLocation(m_ProgramId, name);
        if(location < 0)
            throw new IllegalStateException("Uniform not found: " + name);

        return location;
    }

    public int getProgramId() {
        ensureCreated();
        return m_ProgramId;
    }
}