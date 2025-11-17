package JavaIO;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class TextFileAnalyzer {
    
    public static class AnalysisResult {
        private final long lineCount; // количество строк в файле
        private final long wordCount; // количество слов в файле
        private final long charCount; // количество символов в файле
        private final Map<Character, Integer> charFrequency; // частота использования символов
        
        public AnalysisResult(long lineCount, long wordCount, long charCount, Map<Character, Integer> charFrequency) {
            this.lineCount = lineCount;
            this.wordCount = wordCount;
            this.charCount = charCount;
            this.charFrequency = charFrequency;
        }
        
        public long getLineCount() {
            return lineCount;
        }
        
        public long getWordCount() {
            return wordCount;
        }
        
        public long getCharCount() {
            return charCount;
        }
        
        public Map<Character, Integer> getCharFrequency() {
            return charFrequency;
        }
        
        @Override
        public String toString() {
            return "AnalysisResult{" +
                    "lineCount=" + lineCount +
                    ", wordCount=" + wordCount +
                    ", charCount=" + charCount +
                    ", charFrequency=" + charFrequency +
                    '}';
        }
    }
    
    /**
     * Анализирует текстовый файл и возвращает статистику
     * @param filePath путь к файлу для анализа
     * @return результат анализа
     * @throws IOException если произошла ошибка при чтении файла
     */
    public AnalysisResult analyzeFile(String filePath) throws IOException {
        // TODO: Реализовать анализ файла по переданному пути `filePath`, используя BufferedReader
        // Подсчитать в файле: lineCount, wordCount, charCount
        // Использовать try-with-resources для автоматического закрытия потоков
        
        long lineCount = 0;
        long wordCount = 0;
        long charCount = 0;
        Map<Character, Integer> charFrequency = new HashMap<>();
        
        try (FileReader fileReader = new FileReader(filePath);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lineCount++;
                charCount += line.length();

                String[] words = line.trim().split("\\s+");
                if (!line.trim().isEmpty()) {
                    wordCount += words.length;
                }
                for (char c : line.toCharArray()) {
                    charFrequency.put(c, charFrequency.getOrDefault(c, 0) + 1);
                }
            }
        }
        
        return new AnalysisResult(lineCount, wordCount, charCount, charFrequency);
    }
    
    /**
     * Сохраняет результаты анализа в файл
     * @param result результат анализа
     * @param outputPath путь к файлу для сохранения результатов
     * @throws IOException если произошла ошибка при записи файла
     */
    public void saveAnalysisResult(AnalysisResult result, String outputPath) throws IOException {
        // TODO: Сохранить результаты в файл по указанному пути `outputPath` используя BufferedWriter
        
        try (FileWriter fileWriter = new FileWriter(outputPath);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            
            bufferedWriter.write("Результаты анализа файла");
            bufferedWriter.newLine();
            bufferedWriter.write("========================");
            bufferedWriter.newLine();
            bufferedWriter.write("Количество строк: " + result.getLineCount());
            bufferedWriter.newLine();
            bufferedWriter.write("Количество слов: " + result.getWordCount());
            bufferedWriter.newLine();
            bufferedWriter.write("Количество символов: " + result.getCharCount());
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            bufferedWriter.write("Частота использования символов:");
            bufferedWriter.newLine();
            
            for (Map.Entry<Character, Integer> entry : result.getCharFrequency().entrySet()) {
                char ch = entry.getKey();
                int count = entry.getValue();
                String charDisplay = (ch == '\n') ? "\\n" : 
                                    (ch == '\r') ? "\\r" : 
                                    (ch == '\t') ? "\\t" : 
                                    String.valueOf(ch);
                bufferedWriter.write("'" + charDisplay + "': " + count);
                bufferedWriter.newLine();
            }
        }
    }
}

