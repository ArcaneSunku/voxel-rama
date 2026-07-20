package atomixsoft.dev.util;

public final class Timer {

    private static final double NANOSECONDS_TO_SECONDS = 1.0 / 1e9;

    private long m_PreviousTime;

    public Timer() {
        reset();
    }

    public void reset() {
        m_PreviousTime = System.nanoTime();
    }

    public double getElapsedSeconds() {
        long currentTime = System.nanoTime();

        double elapsedSeconds = (currentTime - m_PreviousTime) * NANOSECONDS_TO_SECONDS;
        m_PreviousTime = currentTime;

        return elapsedSeconds;
    }

}
