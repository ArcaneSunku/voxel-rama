package atomixsoft.dev;

import atomixsoft.dev.graphics.Camera;
import atomixsoft.dev.graphics.CameraController;
import atomixsoft.dev.graphics.Shader;

import atomixsoft.dev.input.Input;
import atomixsoft.dev.platform.Window;

import atomixsoft.dev.world.World;
import atomixsoft.dev.world.block.Blocks;
import atomixsoft.dev.world.chunk.*;
import atomixsoft.dev.world.chunk.mesh.ChunkMeshData;
import atomixsoft.dev.world.chunk.mesh.ChunkMesher;
import atomixsoft.dev.world.render.ChunkModel;
import atomixsoft.dev.world.render.WorldRenderer;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;
import static org.lwjgl.opengl.GL11.*;

public final class VoxelGame {

    private Window m_Window;
    private Shader m_Shader;

    private Camera m_Camera;
    private CameraController m_CamController;

    private World m_World;
    private WorldRenderer m_Renderer;

    private boolean m_Initialized;

    public void initialize(Window window) {
        if(m_Initialized)
            throw new IllegalStateException("Voxel-Rama already initialized!");

        if(window == null)
            throw new IllegalArgumentException("Window cannot be null!");

        m_Window = window;
        m_Window.setCursorCaptured(true);

        Blocks.Initialize();

        m_Camera = new Camera(65.0f, 0.01f, 1000.0f);
        m_Camera.setPosition(8.0f, 8.0f, 24.0f);

        m_Camera.updateProjection(window.getFramebufferWidth(), window.getFramebufferHeight());
        m_Camera.updateView();

        m_CamController = new CameraController(m_Camera);

        m_Shader = new Shader("shaders/triangle.vert", "shaders/triangle.frag");

        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        glEnable(GL_DEPTH_TEST);

        initializeWorld();

        m_Initialized = true;
        IO.println("Voxel-Rama initialized!");
    }

    private void initializeWorld() {
        m_World = new World();

        createTestChunk(new ChunkPosition(1, 0, 0));
        createTestChunk(new ChunkPosition(0, 0, 1));

        createTestChunk(new ChunkPosition(0, 0, 0));

        createTestChunk(new ChunkPosition(-1, 0, 0));
        createTestChunk(new ChunkPosition(0, 0, -1));

        m_Renderer = new WorldRenderer(m_World);
        m_Renderer.update();
    }

    private void createTestChunk(ChunkPosition position) {
        m_World.createChunk(position);

        int worldOriginX = position.getWorldBlockOriginX();
        int worldOriginY = position.getWorldBlockOriginY();
        int worldOriginZ = position.getWorldBlockOriginZ();

        for (int z = 0; z < Chunk.SIZE; z++) {
            for (int x = 0; x < Chunk.SIZE; x++) {
                m_World.setBlock(worldOriginX + x, worldOriginY, worldOriginZ + z, Blocks.STONE);
                m_World.setBlock(worldOriginX + x, worldOriginY + 1, worldOriginZ + z, Blocks.DIRT);
                m_World.setBlock(worldOriginX + x, worldOriginY + 2, worldOriginZ + z, Blocks.GRASS);
            }
        }
    }

    public void processInput(double delta) {
        validate();

        if(!m_Window.isCursorCaptured())
            return;

        m_CamController.update(delta);
    }

    public void update(double delta) {
        validate();
        m_Renderer.update();
    }

    public void render(double alpha) {
        validate();

        glClearColor(0.08f, 0.11f, 0.16f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        m_Renderer.render(m_Shader, m_Camera);
    }

    public void dispose() {
        if(!m_Initialized)
            return;

        if(m_Shader != null)
            m_Shader.dispose();

        m_Window = null;
        m_Initialized = false;
        m_CamController = null;

        disposeWorld();
        Blocks.Dispose();

        IO.println("Voxel-Rama disposed!");
    }

    private void disposeWorld() {
        m_Renderer.dispose();
        m_Renderer = null;

        m_World.clear();
        m_World = null;
    }

    private void validate() {
        if(!m_Initialized)
            throw new IllegalStateException("Voxel-Rama has not been initialized!");
    }

}
