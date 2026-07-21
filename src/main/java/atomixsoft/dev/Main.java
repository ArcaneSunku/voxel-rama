package atomixsoft.dev;

public final class Main {
    static void main(String[] args) {
        final VoxelGame game = new VoxelGame();
        final Application app = new Application(game);

        app.run();
    }
}
