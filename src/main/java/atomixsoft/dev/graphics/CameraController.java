package atomixsoft.dev.graphics;

import atomixsoft.dev.input.Input;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

public final class CameraController {

    private static final Vector3f WORLD_UP = new Vector3f(0.0f, 1.0f, 0.0f);

    private final Camera m_Camera;

    private float m_MovementSpeed;
    private float m_SprintMultiplier;
    private float m_MouseSensitivity;

    public CameraController(Camera camera) {
        if (camera == null)
            throw new IllegalArgumentException("Camera cannot be null.");

        m_Camera = camera;

        m_MovementSpeed = 5.0f;
        m_SprintMultiplier = 2.0f;
        m_MouseSensitivity = 0.1f;
    }

    public void update(double delta) {
        updateRotation();
        updateMovement((float) delta);

        m_Camera.updateView();
    }

    private void updateRotation() {
        float yaw = m_Camera.getYaw() + (float) Input.getMouseDeltaX() * m_MouseSensitivity;
        float pitch = m_Camera.getPitch() - (float) Input.getMouseDeltaY() * m_MouseSensitivity;

        pitch = Math.clamp(pitch, -89.0f, 89.0f);

        m_Camera.setYaw(yaw);
        m_Camera.setPitch(pitch);
    }

    private void updateMovement(float delta) {
        Vector3f forward = calculateForward();
        Vector3f right = new Vector3f(forward)
                .cross(WORLD_UP)
                .normalize();

        /*
         * Keep ordinary WASD movement parallel to the ground.
         * Looking upward should not make W move the camera upward.
         */
        Vector3f planarForward = new Vector3f(forward.x, 0.0f, forward.z)
                .normalize();

        Vector3f movement = new Vector3f();

        if (Input.isKeyDown(GLFW_KEY_W))
            movement.add(planarForward);

        if (Input.isKeyDown(GLFW_KEY_S))
            movement.sub(planarForward);

        if (Input.isKeyDown(GLFW_KEY_D))
            movement.add(right);

        if (Input.isKeyDown(GLFW_KEY_A))
            movement.sub(right);

        if (Input.isKeyDown(GLFW_KEY_SPACE))
            movement.add(WORLD_UP);

        if (Input.isKeyDown(GLFW_KEY_LEFT_CONTROL))
            movement.sub(WORLD_UP);

        if (movement.lengthSquared() == 0.0f)
            return;

        float speed = m_MovementSpeed;

        if (Input.isKeyDown(GLFW_KEY_LEFT_SHIFT))
            speed *= m_SprintMultiplier;

        movement.normalize()
                .mul(speed * delta);

        m_Camera.getPosition().add(movement);
    }

    private Vector3f calculateForward() {
        float yawRadians = (float) Math.toRadians(m_Camera.getYaw());

        float pitchRadians = (float) Math.toRadians(m_Camera.getPitch());

        return new Vector3f( (float) Math.cos(yawRadians) * (float) Math.cos(pitchRadians),
                (float) Math.sin(pitchRadians),
                (float) Math.sin(yawRadians) * (float) Math.cos(pitchRadians))
                .normalize();
    }

    public float getMovementSpeed() {
        return m_MovementSpeed;
    }

    public void setMovementSpeed(float movementSpeed) {
        if (movementSpeed < 0.0f)
            throw new IllegalArgumentException("Movement speed can't be a negative number!");

        m_MovementSpeed = movementSpeed;
    }

    public float getSprintMultiplier() {
        return m_SprintMultiplier;
    }

    public void setSprintMultiplier(float sprintMultiplier) {
        if (sprintMultiplier < 1.0f)
            throw new IllegalArgumentException("Sprint Multiplier can't be less than 1!");

        m_SprintMultiplier = sprintMultiplier;
    }

    public float getMouseSensitivity() {
        return m_MouseSensitivity;
    }

    public void setMouseSensitivity(float mouseSensitivity) {
        if (mouseSensitivity < 0.0f)
            throw new IllegalArgumentException("Movement sensitivity can't be a negative number!");

        m_MouseSensitivity = mouseSensitivity;
    }
}