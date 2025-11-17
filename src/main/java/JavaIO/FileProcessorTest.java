package JavaIO;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

class FileProcessorTest {
    
    @Test
    void testSplitAndMergeFile() throws IOException {
        FileProcessor processor = new FileProcessor();

        Path testFile = Files.createTempFile("test", ".dat");
        byte[] testData = new byte[1500]; // 1.5КВ данных
        new Random().nextBytes(testData);
        Files.write(testFile, testData);

        String outputDir = Files.createTempDirectory("parts").toString();
        List<Path> parts = processor.splitFile(testFile.toString(), outputDir, 500);

        assertNotNull(parts, "Список частей не должен быть null");
        assertEquals(3, parts.size(), "Должно быть создано 3 части файла");

        for (Path part : parts) {
            assertTrue(Files.exists(part), "Часть файла должна существовать: " + part);
            assertTrue(Files.size(part) > 0, "Размер части должен быть больше 0: " + part);
        }

        assertEquals(500, Files.size(parts.get(0)), "Первая часть должна быть 500 байт");
        assertEquals(500, Files.size(parts.get(1)), "Вторая часть должна быть 500 байт");
        assertEquals(500, Files.size(parts.get(2)), "Третья часть должна быть 500 байт");

        Path mergedFile = Files.createTempFile("merged", ".dat");
        processor.mergeFiles(parts, mergedFile.toString());
        assertArrayEquals(Files.readAllBytes(testFile), Files.readAllBytes(mergedFile));
    }
}

