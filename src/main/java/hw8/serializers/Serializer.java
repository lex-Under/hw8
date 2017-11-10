package hw8.serializers;

import hw8.model.Publisher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * Интерфейс, декларирующий файловую сериализацию/десериализацию издательств
 */
public interface Serializer {

    public void serialize(Publisher p, File writeTo) throws IOException;

    public Publisher deserialize(File readFrom) throws IOException;
}
