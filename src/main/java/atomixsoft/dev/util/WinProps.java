package atomixsoft.dev.util;

public record WinProps(
        String title,
        int width,
        int height,
        boolean vSync,
        boolean resizable) {

    public WinProps(String title, int width, int height, boolean vSync) {
        this(title, width, height, vSync,true);
    }

    public WinProps {
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Window title cannot be null or blank.");

        if (width <= 0 || height <= 0)
            throw new IllegalArgumentException("Window width/height must be greater than zero.");
    }
}