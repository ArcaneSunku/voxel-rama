package atomixsoft.dev.input;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public final class Input {

    private static final List<Boolean> s_KeysDown = new ArrayList<>(GLFW_KEY_LAST);
    private static final List<Boolean> s_KeysPressed = new ArrayList<>(GLFW_KEY_LAST);
    private static final List<Boolean> s_KeysReleased = new ArrayList<>(GLFW_KEY_LAST);

    private static final List<Boolean> s_MouseButtonDown = new ArrayList<>(GLFW_MOUSE_BUTTON_LAST);
    private static final List<Boolean> s_MouseButtonPressed = new ArrayList<>(GLFW_MOUSE_BUTTON_LAST);
    private static final List<Boolean> s_MouseButtonReleased = new ArrayList<>(GLFW_MOUSE_BUTTON_LAST);

    private static double s_MouseX, s_MouseY;
    private static double s_MouseDeltaX, s_MouseDeltaY;

    private static boolean s_MousePositionInitialized;

    public static void initialize() {
        for(var i = 0; i < GLFW_KEY_LAST; ++i) {
            s_KeysDown.add(i, false);
            s_KeysPressed.add(i, false);
            s_KeysReleased.add(i, false);
        }

        for(var i = 0; i < GLFW_MOUSE_BUTTON_LAST; ++i) {
            s_MouseButtonDown.add(i, false);
            s_MouseButtonPressed.add(i, false);
            s_MouseButtonReleased.add(i, false);
        }
    }

    public static void beginFrame() {
        for(var i = 0; i < GLFW_KEY_LAST; ++i) {
            s_KeysPressed.set(i, false);
            s_KeysReleased.set(i, false);
        }

        for(var i = 0; i < GLFW_MOUSE_BUTTON_LAST; ++i) {
            s_MouseButtonPressed.add(i, false);
            s_MouseButtonReleased.add(i, false);
        }

        s_MouseDeltaX = 0.0;
        s_MouseDeltaY = 0.0;
    }

    public static void resetMousePosition() {
        s_MouseDeltaX = 0.0;
        s_MouseDeltaY = 0.0;
        s_MousePositionInitialized = false;
    }

    public static void handleKey(int key, int action) {
        if(!isValidKey(key))
            return;

        switch (action) {
            case GLFW_PRESS -> {
                s_KeysDown.set(key, true);
                s_KeysPressed.set(key, true);
            }

            case GLFW_REPEAT -> s_KeysDown.set(key, true);

            case GLFW_RELEASE -> {
                s_KeysDown.set(key, false);
                s_KeysReleased.set(key, true);
            }
        }
    }

    public static void handleMouseButton(int button, int action) {
        if(!isValidMouseButton(button))
            return;

        switch (action) {
            case GLFW_PRESS -> {
                s_MouseButtonDown.set(button, true);
                s_MouseButtonPressed.set(button, true);
            }

            case GLFW_REPEAT -> s_MouseButtonDown.set(button, true);

            case GLFW_RELEASE -> {
                s_MouseButtonDown.set(button, false);
                s_MouseButtonReleased.set(button, true);
            }
        }
    }

    public static void handleMouseMove(double x, double y) {
        if(!s_MousePositionInitialized) {
            s_MouseX = x;
            s_MouseY = y;

            s_MousePositionInitialized = true;
            return;
        }

        s_MouseDeltaX += x - s_MouseX;
        s_MouseDeltaY += y - s_MouseY;

        s_MouseX = x;
        s_MouseY = y;
    }

    public static void clear() {
        for(var i = 0; i < GLFW_KEY_LAST; ++i) {
            s_KeysDown.set(i, false);
            s_KeysPressed.set(i, false);
            s_KeysReleased.set(i, false);
        }

        for(var i = 0; i < GLFW_MOUSE_BUTTON_LAST; ++i) {
            s_MouseButtonDown.set(i, false);
            s_MouseButtonPressed.set(i, false);
            s_MouseButtonReleased.set(i, false);
        }

        s_MouseX = 0.0;
        s_MouseY = 0.0;

        s_MouseDeltaX = 0.0;
        s_MouseDeltaY = 0.0;

        s_MousePositionInitialized = false;
    }

    public static boolean isKeyDown(int key) {
        return isValidKey(key) && s_KeysDown.get(key);
    }

    public static boolean isKeyPressed(int key) {
        return isValidKey(key) && s_KeysPressed.get(key);
    }

    public static boolean isKeyReleased(int key) {
        return isValidKey(key) && s_KeysReleased.get(key);
    }

    public static boolean isMouseButtonDown(int button) {
        return isValidMouseButton(button) && s_MouseButtonDown.get(button);
    }

    public static boolean isMouseButtonPressed(int button) {
        return isValidMouseButton(button) && s_MouseButtonPressed.get(button);
    }

    public static boolean isMouseButtonReleased(int button) {
        return isValidMouseButton(button) && s_MouseButtonReleased.get(button);
    }

    public static double getMouseX() {
        return s_MouseX;
    }

    public static double getMouseY() {
        return s_MouseY;
    }

    public static double getMouseDeltaX() {
        return s_MouseDeltaX;
    }

    public static double getMouseDeltaY() {
        return s_MouseDeltaY;
    }

    private static boolean isValidKey(int key) {
        return key >= 0 && key <= GLFW_KEY_LAST;
    }

    private static boolean isValidMouseButton(int button) {
        return button >= 0 && button <= GLFW_MOUSE_BUTTON_LAST;
    }

}
