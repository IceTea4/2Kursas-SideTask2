import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LongestSentence {
    public static void main(String[] args) {
        String fileName = "test1.json";

        try (InputStream inputStream = LongestSentence.class.getClassLoader().getResourceAsStream(fileName)) {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Map<String, Double>> data = objectMapper.readValue(inputStream, Map.class);

            int res = longestPath(data, "START", "END", new HashSet<>()) - 1;

            System.out.println(res);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int longestPath(Map<String, Map<String, Double>> data, String start, String end, Set<String> visited) {
        if (start.equals(end) || visited.contains(start)) {
            return 0;
        }

        visited.add(start);

        int res = data.getOrDefault(start, Collections.emptyMap())
                .keySet()
                .stream()
                .mapToInt(next -> 1 + longestPath(data, next, end, visited))
                .max()
                .orElse(0);

        visited.remove(start);

        return res;
    }
}
