package atomixsoft.dev;

import atomixsoft.dev.graphics.Camera;
import atomixsoft.dev.graphics.CameraController;
import atomixsoft.dev.graphics.Shader;
import atomixsoft.dev.input.Input;
import atomixsoft.dev.platform.Window;
import org.joml.Matrix4f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public final class VoxelGame {

    private Window m_Window;
    private Shader m_Shader;

    private Camera m_Camera;
    private CameraController m_CamController;

    private int m_VAO, m_VBO, m_EBO;

    private boolean m_Initialized;

    public void initialize(Window window) {
        if(m_Initialized)
            throw new IllegalStateException("Voxel-Rama already initialized!");

        if(window == null)
            throw new IllegalArgumentException("Window cannot be null!");

        m_Window = window;
        m_Shader = new Shader("shaders/triangle.vert", "shaders/triangle.frag");

        initTriangle();

        m_Camera = new Camera(65.0f, 0.01f, 1000.0f);
        m_Camera.setPosition(0.0f, 0.0f, 3.0f);

        m_Camera.updateProjection(window.getFramebufferWidth(), window.getFramebufferHeight());
        m_Camera.updateView();

        m_CamController = new CameraController(m_Camera);
        m_Window.setCursorCaptured(true);

        m_Initialized = true;
        IO.println("Voxel-Rama initialized!");
    }

    private void initTriangle() {
        m_VAO = glGenVertexArrays();
        glBindVertexArray(m_VAO);

        float[] vertices = new float[] {
                // Position(x,y,z)  Color(r,g,b)
                -0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
                 0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f,
                 0.5f,  0.5f, 0.0f, 0.0f, 0.0f, 1.0f,
                -0.5f,  0.5f, 0.0f, 0.2f, 0.5f, 1.0f
        };

        m_VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, m_VBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0L);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3L * Float.BYTES);
        glEnableVertexAttribArray(1);

        int[] indices = new int[] {
                0, 1, 2,
                2, 3, 0
        };

        m_EBO = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }

    public void processInput(double delta) {
        validate();

        if(!m_Window.isCursorCaptured())
            return;

        m_CamController.update(delta);
    }

    public void update(double delta) {
        validate();

        /*
         * Game simulation will be updated here.
         *
         * delta is currently 1.0 / 60.0, meaning this method
         * runs using a consistent simulation step of approximately
         * 0.01666 seconds.
         */
    }

    public void render(double alpha) {
        validate();

        /*
         * alpha will later be used to smoothly render
         * objects between their previous and current simulation states.
         */

        glClearColor(0.08f, 0.11f, 0.16f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        m_Shader.bind();

        m_Shader.setUniform("u_Model", new Matrix4f());
        m_Shader.setUniform("u_View", m_Camera.getViewMatrix());
        m_Shader.setUniform("u_Projection", m_Camera.getProjectionMatrix());

        glBindVertexArray(m_VAO);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0L);
        glBindVertexArray(0);

        m_Shader.unbind();
    }

    public void dispose() {
        if(!m_Initialized)
            return;

        /*
         * Game-owned OpenGL resources will be destroyed here before
         * Application destroys the window and OpenGL context.
         */

        glDeleteBuffers(m_EBO);
        glDeleteBuffers(m_VBO);
        glDeleteVertexArrays(m_VAO);

        if(m_Shader != null)
            m_Shader.dispose();

        m_Window = null;
        m_Initialized = false;

        IO.println("Voxel-Rama disposed!");
    }

    private void validate() {
        if(!m_Initialized)
            throw new IllegalStateException("Voxel-Rama has not been initialized!");
    }

}
