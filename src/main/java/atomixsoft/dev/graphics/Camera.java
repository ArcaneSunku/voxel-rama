package atomixsoft.dev.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    private final Matrix4f m_ViewMatrix;
    private final Matrix4f m_ProjectionMatrix;

    private final Vector3f m_Position;

    private float m_Pitch;
    private float m_Yaw;

    private float m_FOV;
    private float m_Near, m_Far;

    public Camera(float fov, float near, float far) {
        m_ViewMatrix = new Matrix4f();
        m_ProjectionMatrix = new Matrix4f();

        m_Position = new Vector3f();

        m_FOV = fov;
        m_Near = near;
        m_Far = far;

        m_Yaw = -90.0f;
        m_Pitch = 0.0f;
    }

    public void updateProjection(int width, int height) {
        float aspect = (float) width / (float) height;
        m_ProjectionMatrix.identity()
                .perspective((float) Math.toRadians(m_FOV), aspect, m_Near, m_Far);
    }

    public void updateView() {
        Vector3f forward = new Vector3f((float) Math.cos(Math.toRadians(m_Yaw)) * (float) Math.cos(Math.toRadians(m_Pitch)),
                (float) Math.sin(Math.toRadians(m_Pitch)),
                (float) Math.sin(Math.toRadians(m_Yaw)) * (float) Math.cos(Math.toRadians(m_Pitch))).normalize();

        m_ViewMatrix.identity()
                .lookAt(m_Position, new Vector3f(m_Position).add(forward), new Vector3f(0.0f, 1.0f, 0.0f));
    }

    public void setPosition(float x, float y, float z) {
        m_Position.set(x, y, z);
    }

    public void setPitch(float pitch) {
        m_Pitch = pitch;
    }

    public void setYaw(float yaw) {
        m_Yaw = yaw;
    }

    public Matrix4f getViewMatrix() {
        return m_ViewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return m_ProjectionMatrix;
    }

    public Vector3f getPosition() {
        return m_Position;
    }

    public float getPitch() {
        return m_Pitch;
    }

    public float getYaw() {
        return m_Yaw;
    }

}
