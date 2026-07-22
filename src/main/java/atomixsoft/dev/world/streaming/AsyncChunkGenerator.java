package atomixsoft.dev.world.streaming;

import atomixsoft.dev.world.chunk.Chunk;
import atomixsoft.dev.world.chunk.ChunkPosition;
import atomixsoft.dev.world.generation.WorldGenerator;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class AsyncChunkGenerator implements AutoCloseable {

    private static final long SHUTDOWN_TIMEOUT_SECONDS = 5L;

    private final WorldGenerator m_WorldGenerator;
    private final ExecutorService m_Executor;

    private final ConcurrentLinkedQueue<ChunkGenerationResult> m_CompletedResults;
    private final AtomicBoolean m_Closed;

    public AsyncChunkGenerator(WorldGenerator worldGenerator, int workerCount) {
        if (worldGenerator == null)
            throw new IllegalArgumentException("World generator cannot be null.");

        if (workerCount < 1)
            throw new IllegalArgumentException("Worker count must be at least one.");

        m_WorldGenerator = worldGenerator;
        m_Executor = Executors.newFixedThreadPool(workerCount, new ChunkGenerationThreadFactory());

        m_CompletedResults = new ConcurrentLinkedQueue<>();
        m_Closed = new AtomicBoolean();
    }

    public void submit(ChunkPosition position) {
        if (position == null)
            throw new IllegalArgumentException("Chunk position cannot be null.");

        if (m_Closed.get())
            throw new IllegalStateException("Async chunk generator is closed.");

        m_Executor.execute(() -> generate(position));
    }

    public ChunkGenerationResult pollCompleted() {
        return m_CompletedResults.poll();
    }

    public boolean hasCompletedResults() {
        return !m_CompletedResults.isEmpty();
    }

    @Override
    public void close() {
        if (!m_Closed.compareAndSet(false, true))
            return;

        m_Executor.shutdownNow();

        try {
            if (!m_Executor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS))
                System.err.println("Chunk generation workers did not terminate cleanly.");
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }

        m_CompletedResults.clear();
    }

    private void generate(ChunkPosition position) {
        if (m_Closed.get())
            return;

        try {
            Chunk chunk = m_WorldGenerator.generateChunk(position);

            if (m_Closed.get())
                return;

            m_CompletedResults.add(ChunkGenerationResult.succeeded(position, chunk));
        } catch (Throwable throwable) {
            if (m_Closed.get())
                return;

            m_CompletedResults.add(ChunkGenerationResult.failed(position, throwable));
        }
    }

    private static final class ChunkGenerationThreadFactory implements ThreadFactory {

        private final AtomicInteger m_ThreadCounter = new AtomicInteger();

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable, "Chunk Generator-" + m_ThreadCounter.incrementAndGet());
            thread.setDaemon(true);

            return thread;
        }
    }

}