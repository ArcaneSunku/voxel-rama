package atomixsoft.dev;

import atomixsoft.dev.graphics.Camera;
import atomixsoft.dev.graphics.CameraController;
import atomixsoft.dev.graphics.Shader;

import atomixsoft.dev.platform.Window;

import atomixsoft.dev.world.World;
import atomixsoft.dev.world.WorldProperties;
import atomixsoft.dev.world.WorldSeed;
import atomixsoft.dev.world.block.Blocks;
import atomixsoft.dev.world.generation.NoiseTerrainGenerator;
import atomixsoft.dev.world.generation.TerrainPresetId;
import atomixsoft.dev.world.generation.WorldGenerator;
import atomixsoft.dev.world.render.WorldRenderer;
import atomixsoft.dev.world.streaming.ChunkStreamingController;
import atomixsoft.dev.world.streaming.ChunkStreamingPresets;

import static org.lwjgl.opengl.GL11.*;

public final class VoxelGame {

    private Window m_Window;
    private Shader m_Shader;

    private Camera m_Camera;
    private CameraController m_CamController;

    private World m_World;
    private ChunkStreamingController m_ChunkStream;

    private WorldGenerator m_WorldGen;
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
        m_Camera.setPosition(8.0f, 18.0f, 30.0f);
        m_Camera.setPitch(-25.0f);

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
        WorldProperties properties = new WorldProperties("Development World", WorldSeed.fromInput("Voxel-Rama Development"), TerrainPresetId.BLENDED, 1);

        m_World = new World(properties);
        m_WorldGen = new WorldGenerator(new NoiseTerrainGenerator(properties));

        m_ChunkStream = new ChunkStreamingController(m_World, m_WorldGen, ChunkStreamingPresets.DEVELOPMENT);
        m_ChunkStream.prepareInitialArea(m_Camera.getPosition().x, m_Camera.getPosition().z);

        m_Renderer = new WorldRenderer(m_World);
        m_Renderer.update();
    }

    public void processInput(double delta) {
        validate();

        if(!m_Window.isCursorCaptured())
            return;

        m_CamController.update(delta);
    }

    public void update(double delta) {
        validate();

        m_ChunkStream.update(m_Camera.getPosition().x, m_Camera.getPosition().z);
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
        try {
            m_ChunkStream.close();
            m_ChunkStream = null;
        } catch(Exception e) {
            System.err.println("Error closing chunk stream!\n" + e.getMessage());
            throw new RuntimeException();
        }

        m_Renderer.dispose();
        m_Renderer = null;

        m_WorldGen = null;

        m_World.clear();
        m_World = null;
    }

    private void validate() {
        if(!m_Initialized)
            throw new IllegalStateException("Voxel-Rama has not been initialized!");
    }

}
