package atomixsoft.dev;

import atomixsoft.dev.input.Input;
import atomixsoft.dev.platform.Window;
import atomixsoft.dev.util.Timer;
import atomixsoft.dev.util.WinProps;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.Locale;

import static org.lwjgl.glfw.GLFW.*;

public final class Application {

    private static final int TARGET_UPS = 60;
    private static final double FIXED_TIMESTEP = 1.0 / TARGET_UPS;
    private static final double MAX_FRAME_TIME = 0.25;

    private final VoxelGame m_Game;

    private GLFWErrorCallback m_ErrorCallback;
    private Window m_Window;

    private boolean m_GlfwInitialized;
    private boolean m_Running;

    public Application(VoxelGame game) {
        if(game == null)
            throw new IllegalArgumentException("Game cannot be null!");

        m_Game = game;
    }

    public void run() {
        try {
            initialize();
            gameLoop();
        } catch (RuntimeException e) {
            e.printStackTrace(System.err);
        } finally {
            dispose();
        }
    }

    private void initialize() {
        m_ErrorCallback = GLFWErrorCallback.createPrint(System.err);
        m_ErrorCallback.set();

        if(glfwPlatformSupported(GLFW_PLATFORM_WAYLAND))
            glfwInitHint(GLFW_PLATFORM, GLFW_PLATFORM_X11);

        if(!glfwInit())
            throw new IllegalStateException("Failed to initialize GLFW.");

        m_GlfwInitialized = true;
        Input.initialize();

        WinProps props = new WinProps("Voxel-Rama", 1280, 720, true);
        m_Window = new Window(props);

        if(!m_Window.create())
            throw new IllegalStateException("Window was not created properly!");

        m_Game.initialize(m_Window);
        m_Running = true;

        IO.println(String.format(Locale.ROOT, "Window created: %d x %d", props.width(), props.height()));
    }

    private void gameLoop() {
        Timer timer = new Timer();
        double accumulator = 0.0;

        while(m_Running && !m_Window.shouldClose()) {
            double frameTime = timer.getElapsedSeconds();
            if(frameTime > MAX_FRAME_TIME)
                frameTime = MAX_FRAME_TIME;

            accumulator += frameTime;

            Input.beginFrame();
            m_Window.pollEvents();

            m_Game.processInput(frameTime);

            while(accumulator >= FIXED_TIMESTEP) {
                m_Game.update(FIXED_TIMESTEP);
                accumulator -= FIXED_TIMESTEP;
            }

            double interpolationAlpha = accumulator / FIXED_TIMESTEP;
            m_Game.render(interpolationAlpha);

            m_Window.swapBuffers();
        }
    }

    private void dispose() {
        m_Running = false;

        m_Game.dispose();
        Input.clear();

        if (m_Window != null) {
            m_Window.dispose();
            m_Window = null;
        }

        if(m_GlfwInitialized) {
            glfwTerminate();
            m_GlfwInitialized = false;
        }

        if (m_ErrorCallback != null) {
            m_ErrorCallback.free();
            m_ErrorCallback = null;
        }

        IO.println("Voxel-Rama shut down!");
        System.exit(0);
    }

}
