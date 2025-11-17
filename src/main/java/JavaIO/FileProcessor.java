package JavaIO;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileProcessor {
    
    /**
     * Разбивает файл на части указанного размера
     * @param sourcePath путь к исходному файлу
     * @param outputDir директория для сохранения частей
     * @param partSize размер каждой части в байтах
     * @return список путей к созданным частям
     * @throws IOException если произошла ошибка при работе с файлами
     */
    public List<Path> splitFile(String sourcePath, String outputDir, int partSize) throws IOException {
        // TODO: Реализовать разбиение файла используя FileChannel и ByteBuffer
        // - Создать части в указанной директории
        // - Имена файлов: originalName.part1, originalName.part2, etc.
        // - Вернуть список путей к созданным частям
        
        Path source = Paths.get(sourcePath);
        Path outputDirectory = Paths.get(outputDir);

        if (!Files.exists(outputDirectory)) {
            Files.createDirectories(outputDirectory);
        }

        String fileName = source.getFileName().toString();
        String baseName = fileName.contains(".") ? 
            fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
        String extension = fileName.contains(".") ? 
            fileName.substring(fileName.lastIndexOf('.')) : "";
        
        List<Path> partPaths = new ArrayList<>();
        ByteBuffer buffer = ByteBuffer.allocate(partSize);
        int partNumber = 1;
        
        try (FileChannel sourceChannel = FileChannel.open(source, StandardOpenOption.READ)) {
            long fileSize = sourceChannel.size();
            long totalBytesRead = 0;
            
            while (totalBytesRead < fileSize) {
                String partFileName = baseName + ".part" + partNumber + extension;
                Path partPath = outputDirectory.resolve(partFileName);
                
                try (FileChannel partChannel = FileChannel.open(
                        partPath, 
                        StandardOpenOption.CREATE, 
                        StandardOpenOption.WRITE, 
                        StandardOpenOption.TRUNCATE_EXISTING)) {
                    
                    long bytesToRead = Math.min(partSize, fileSize - totalBytesRead);
                    long bytesWritten = 0;
                    
                    while (bytesWritten < bytesToRead) {
                        buffer.clear();
                        // Установить лимит буфера на оставшиеся байты
                        int limit = (int) Math.min(buffer.capacity(), bytesToRead - bytesWritten);
                        buffer.limit(limit);
                        
                        int bytesRead = sourceChannel.read(buffer);
                        
                        if (bytesRead == -1) {
                            break;
                        }
                        
                        buffer.flip();
                        while (buffer.hasRemaining()) {
                            partChannel.write(buffer);
                        }
                        
                        bytesWritten += bytesRead;
                        totalBytesRead += bytesRead;
                    }
                }
                
                partPaths.add(partPath);
                partNumber++;
            }
        }
        
        return partPaths;
    }
    
    /**
     * Объединяет части файла обратно в один файл
     * @param partPaths список путей к частям файла (в правильном порядке)
     * @param outputPath путь для результирующего файла
     * @throws IOException если произошла ошибка при работе с файлами
     */
    public void mergeFiles(List<Path> partPaths, String outputPath) throws IOException {
        // TODO: Реализовать объединение частей используя FileChannel
        // - Проверить что все части существуют
        // - Объединить в правильном порядке
        
        Path output = Paths.get(outputPath);

        for (Path partPath : partPaths) {
            if (!Files.exists(partPath)) {
                throw new IOException("Часть файла не найдена: " + partPath);
            }
        }

        try (FileChannel outputChannel = FileChannel.open(
                output, 
                StandardOpenOption.CREATE, 
                StandardOpenOption.WRITE, 
                StandardOpenOption.TRUNCATE_EXISTING)) {
            
            ByteBuffer buffer = ByteBuffer.allocate(8192); // 8KB буфер
            
            for (Path partPath : partPaths) {
                try (FileChannel partChannel = FileChannel.open(partPath, StandardOpenOption.READ)) {
                    long position = 0;
                    long partSize = partChannel.size();
                    
                    while (position < partSize) {
                        buffer.clear();
                        int bytesRead = partChannel.read(buffer, position);
                        
                        if (bytesRead == -1) {
                            break;
                        }
                        
                        buffer.flip();
                        while (buffer.hasRemaining()) {
                            outputChannel.write(buffer);
                        }
                        
                        position += bytesRead;
                    }
                }
            }
        }
    }
}

