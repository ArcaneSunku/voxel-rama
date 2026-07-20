package atomixsoft.dev.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class Files {

    public static String ReadFromFile(String path) {
        if(path == null || path.isEmpty())
            throw new IllegalArgumentException("File path is null or empty!");

        String normalize = path.startsWith("/") ? path : "/" + path;
        try(InputStream is = Files.class.getResourceAsStream(normalize)) {
            if(is == null)
                throw new IllegalArgumentException("File not found: " + normalize);

            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e ) {
            throw new IllegalStateException("Cannot read file: " + normalize, e);
        }
    }

}
