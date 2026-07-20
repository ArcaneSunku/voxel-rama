package atomixsoft.dev.platform;

import atomixsoft.dev.input.Input;
import atomixsoft.dev.util.WinProps;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

public class Window {

    private final WinProps m_Properties;

    private long m_Handle = MemoryUtil.NULL;

    private int m_FramebufferWidth;
    private int m_FramebufferHeight;

    private  boolean m_CursorCaptured;

    public Window(WinProps properties) {
        if (properties == null)
            throw new IllegalArgumentException("Window properties cannot be null.");

        m_Properties = properties;
    }

    public boolean create() {
        if (m_Handle != MemoryUtil.NULL) {
            IO.println("The GLFW window has already been created.");
            return false;
        }

        glfwDefaultWindowHints();

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        m_Handle = glfwCreateWindow(m_Properties.width(), m_Properties.height(), m_Properties.title(), MemoryUtil.NULL, MemoryUtil.NULL);
        if (m_Handle == MemoryUtil.NULL) {
            IO.println("Failed to create GLFW Window!");
            return false;
        }

        centerOnPrimaryMonitor();

        glfwMakeContextCurrent(m_Handle);
        glfwSwapInterval(m_Properties.vSync() ? 1 : 0);

        GL.createCapabilities();

        initializeFramebufferSize();
        installCallbacks();

        glfwShowWindow(m_Handle);
        return true;
    }

    private void centerOnPrimaryMonitor() {
        if(glfwGetPlatform() == GLFW_PLATFORM_WAYLAND) {
            IO.println("Window centering skipped because Wayland handles top-level Window placement!");
            return;
        }

        long primaryMonitor = glfwGetPrimaryMonitor();

        if(primaryMonitor == MemoryUtil.NULL) {
            IO.println("Could not retrieve the primary monitor!");
            IO.println("The window will not be centered!");

            return;
        }

        GLFWVidMode vid_mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if(vid_mode == null) {
            IO.println("Failed to retrieve a proper Video Mode!");
            IO.println("The window will not be centered!");

            return;
        }

        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);

            glfwGetWindowSize(m_Handle, width, height);

            int windowX = (vid_mode.width() - width.get(0)) / 2;
            int windowY = (vid_mode.height() - height.get(0)) / 2;

            glfwSetWindowPos(m_Handle, windowX, windowY);
        }
    }

    private void initializeFramebufferSize() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);

            glfwGetFramebufferSize(m_Handle, width, height);

            m_FramebufferWidth = width.get(0);
            m_FramebufferHeight = height.get(0);
        }

        glViewport(0, 0, m_FramebufferWidth, m_FramebufferHeight);
    }

    private void installCallbacks() {
        glfwSetFramebufferSizeCallback(m_Handle, (window, width, height) -> {
            m_FramebufferWidth = width;
            m_FramebufferHeight = height;

            glViewport(0, 0, width, height);
        });

        glfwSetKeyCallback(m_Handle, (window, key, scancode, action, mods) -> {
            Input.handleKey(key, action);

            if(key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)
                requestClose();

            if(key == GLFW_KEY_F1 && action == GLFW_PRESS)
                setCursorCaptured(!m_CursorCaptured);
        });

        glfwSetCursorPosCallback(m_Handle, (window, xPos, yPos) -> Input.handleMouseMove(xPos, yPos));
        glfwSetMouseButtonCallback(m_Handle, (window, button, action, mods) -> Input.handleMouseButton(button, action));
    }

    private void validate() {
        if(m_Handle == MemoryUtil.NULL)
            throw new IllegalStateException("The GLFW Window has not been created!");
    }

    public void pollEvents() {
        validate();
        glfwPollEvents();
    }

    public void swapBuffers() {
        validate();
        glfwSwapBuffers(m_Handle);
    }

    public void dispose() {
        if(m_Handle == MemoryUtil.NULL)
            return;

        glfwFreeCallbacks(m_Handle);
        glfwDestroyWindow(m_Handle);
        m_CursorCaptured = false;

        m_Handle = MemoryUtil.NULL;
        m_FramebufferWidth = 0;
        m_FramebufferHeight = 0;

        IO.println("GLFW window destroyed.");
    }

    public void requestClose() {
        if(m_Handle !=  MemoryUtil.NULL)
            glfwSetWindowShouldClose(m_Handle, true);
    }

    public boolean shouldClose() {
        if (m_Handle == MemoryUtil.NULL)
            return true;

        return glfwWindowShouldClose(m_Handle);
    }

    public void setCursorCaptured(boolean captured) {
        validate();

        if(m_CursorCaptured == captured)
            return;

        m_CursorCaptured = captured;
        glfwSetInputMode(m_Handle, GLFW_CURSOR, captured ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);

        if(glfwRawMouseMotionSupported())
            glfwSetInputMode(m_Handle, GLFW_RAW_MOUSE_MOTION, captured ? GLFW_TRUE : GLFW_FALSE);

        Input.resetMousePosition();
    }

    public WinProps getProperties() {
        return m_Properties;
    }

    public int getFramebufferWidth() {
        return m_FramebufferWidth;
    }

    public int getFramebufferHeight() {
        return m_FramebufferHeight;
    }

    public boolean isCursorCaptured() {
        return m_CursorCaptured;
    }

}
