import java.io.InputStream;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AverageSentence {
    public static void main(String[] args) {
        String fileName = "test2.json";

        try (InputStream inputStream = AverageSentence.class.getClassLoader().getResourceAsStream(fileName)) {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Map<String, Double>> data = objectMapper.readValue(inputStream, Map.class);

            double result = averagePath(data, "START", "END", 0, 1.0, new HashSet<>());
//            double result = new Calculator(data, "START", "END").calculateAverage();

            System.out.println(String.format("%.2f", result));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double averagePath(Map<String, Map<String, Double>> data, String start, String end, int length, double probability, Set<String> visited) {
        if (start.equals(end)) {
            int path = length - 1;
            return probability * path;
        }

        if (visited.contains(start)) {
            return 0;
        }

//        System.out.println(start);

        visited.add(start);
        Map<String, Double> neighbors = data.getOrDefault(start, Map.of());

        double result = neighbors.entrySet().stream()
                .mapToDouble(entry -> {
                    String nextNode = entry.getKey();
                    double nextProbability = entry.getValue();
                    return averagePath(data, nextNode, end, length + 1, probability * nextProbability, visited);
                })
                .sum();

        visited.remove(start);

        return result;
    }

    private static class Path {
        private final double probability;
        private final int length;
        public Path(int length, double probability) {
            this.length = length;
            this.probability = probability;
        }
    }

    private static class Calculator {
        private final Map<String, Map<String, Double>> data;
        private final Set<String> visited = new HashSet<>();
        private final List<List<Double>> paths = new LinkedList<>();
        private final Stack<Double> path = new Stack<>();
        private final String start;
        private final String end;

        public Calculator(Map<String, Map<String, Double>> data, String start, String end) {
            this.data = data;
            this.start = start;
            this.end = end;
        }

        public double calculateAverage() {
            collectPaths(start, 1.0);

            return paths.stream()
                    .mapToDouble(path -> path.stream().reduce(1.0, (a, b) -> a * b) * path.size())
                    .sum();
        }

        private void collectPaths(String current, double probability) {
            System.out.println(paths.size() + " " + current + " " + visited.size());

            path.add(probability);
            visited.add(current);

            if (current.equals(end)) {
                paths.add(path);
            } else {
                data.get(current)
                        .entrySet()
                        .stream()
                        .filter(it -> !visited.contains(it.getKey()))
                        .forEach(it -> collectPaths(it.getKey(), it.getValue()));
            }

            visited.remove(current);
            path.pop();
        }
    }

    private static class Calculator2 {
        private final Map<String, Map<String, Double>> data;
        private final String start;
        private final String end;

        public Calculator2(Map<String, Map<String, Double>> data, String start, String end) {
            this.data = data;
            this.start = start;
            this.end = end;
        }

        public double calculateAverage() {
            List<List<Double>> paths = new LinkedList<>();
            Stack<String> nodes = new Stack<>();
            Stack<Double> probabilities = new Stack<>();
            Stack<List<Double>> currentPathStack = new Stack<>();

            nodes.push(start);
            probabilities.push(1.0);
            currentPathStack.push(new LinkedList<>());

            while (!nodes.isEmpty()) {
                System.out.println(nodes.size());
                String current = nodes.pop();
                double probability = probabilities.pop();
                List<Double> currentPath = currentPathStack.pop();

                currentPath.add(probability);

                if (current.equals(end)) {
                    paths.add(new LinkedList<>(currentPath));
                    continue;
                }

                if (data.containsKey(current)) {
                    for (Map.Entry<String, Double> neighbor : data.get(current).entrySet()) {
                        nodes.push(neighbor.getKey());
                        probabilities.push(neighbor.getValue());
                        currentPathStack.push(new LinkedList<>(currentPath));
                    }
                }
            }

            return paths.stream()
                    .mapToDouble(path -> path.stream().reduce(1.0, (a, b) -> a * b) * path.size())
                    .sum();
        }
    }
}
