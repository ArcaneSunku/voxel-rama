package atomixsoft.dev;

import atomixsoft.dev.platform.Window;

import static org.lwjgl.opengl.GL11.*;

public final class VoxelGame {

    private Window m_Window;
    private boolean m_Initialized;

    public void initialize(Window window) {
        if(m_Initialized)
            throw new IllegalStateException("Voxel-Rama already initialized!");

        if(window == null)
            throw new IllegalArgumentException("Window cannot be null!");

        m_Window = window;
        m_Initialized = true;

        IO.println("Voxel-Rama initialized!");
    }

    public void update(double delta) {
        validate();

        /*
         * Game simulation will be updated here.
         *
         * delta is currently 1.0 / 60.0, meaning this method
         * runs using a consistent simulation step of approximately
         * 0.016666 seconds.
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
    }

    public void dispose() {
        if(!m_Initialized)
            return;

        /*
         * Game-owned OpenGL resources will be destroyed here before
         * Application destroys the window and OpenGL context.
         */

        m_Window = null;
        m_Initialized = false;

        IO.println("Voxel-Rama disposed.");
    }

    private void validate() {
        if(!m_Initialized)
            throw new IllegalStateException("Voxel-Rama has not been initialized!");
    }

}
