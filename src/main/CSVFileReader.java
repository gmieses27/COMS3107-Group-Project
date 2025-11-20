package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class CSVFileReader {

    public static List<TextbookListing> readListings(String filePath) throws IOException {
        List<TextbookListing> listings = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 8) continue;

                try {
                    String isbn = parts[0].trim();
                    String title = parts[1].trim();
                    int edition = Integer.parseInt(parts[2].trim());
                    String condition = parts[3].trim();
                    double price = Double.parseDouble(parts[4].trim());
                    String courseNumber = parts[5].trim();
                    String sellerId = parts[6].trim();
                    boolean acceptsBarter = Boolean.parseBoolean(parts[7].trim());

                    listings.add(new TextbookListing(isbn, title, edition, condition, price, courseNumber, sellerId, acceptsBarter));
                } catch (NumberFormatException e) {
                    System.err.println("Skipping malformed row: " + line);
                }
            }
        }

        return listings;
    }

    private static List<String> parseCsvLine(String line) {
        List<String> cols = new ArrayList<>();
        if (line == null || line.isEmpty()) return cols;

        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    cur.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                cols.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        cols.add(cur.toString());
        return cols;
    }

    public static Map<String, String[]> readBookMetadata(String filePath) throws IOException {
        Map<String, String[]> metadata = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String header = reader.readLine();
            if (header == null) return metadata;

            List<String> headerCols = parseCsvLine(header);
            int isbnIdx = -1, titleIdx = -1, authorIdx = -1;
            for (int i = 0; i < headerCols.size(); i++) {
                String h = headerCols.get(i).trim().toLowerCase();
                if (h.equals("isbn")) isbnIdx = i;
                if (h.equals("title") || h.equals("name")) titleIdx = i;
                if (h.equals("author") || h.equals("authors")) authorIdx = i;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> cols = parseCsvLine(line);
                if (isbnIdx < 0 || isbnIdx >= cols.size()) continue;
                String isbn = cols.get(isbnIdx).trim();
                String title = titleIdx >= 0 && titleIdx < cols.size() ? cols.get(titleIdx).trim() : "";
                String author = authorIdx >= 0 && authorIdx < cols.size() ? cols.get(authorIdx).trim() : "";
                if (!isbn.isEmpty()) {
                    metadata.put(isbn, new String[] { title, author });
                }
            }
        }

        return metadata;
    }

    public static Map<String, Double> readMarketPrices(String filePath) throws IOException {
        Map<String, Double> sumMap = new HashMap<>();
        Map<String, Integer> countMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String header = reader.readLine();
            if (header == null) return Map.of();

            List<String> headerCols = parseCsvLine(header);
            int isbnIdx = -1, priceIdx = -1;
            for (int i = 0; i < headerCols.size(); i++) {
                String h = headerCols.get(i).trim().toLowerCase();
                if (h.contains("isbn")) isbnIdx = i;
                if (h.contains("price") || h.contains("market")) priceIdx = i;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> cols = parseCsvLine(line);
                if (isbnIdx < 0 || isbnIdx >= cols.size()) continue;
                String rawIsbn = cols.get(isbnIdx).trim();
                if (rawIsbn.isEmpty()) continue;
                String isbn = rawIsbn.replaceAll("[^0-9Xx]", "").toUpperCase();

                String priceStr = (priceIdx >= 0 && priceIdx < cols.size()) ? cols.get(priceIdx).trim() : "";
                if (priceStr.isEmpty()) continue;
                // remove currency symbols and commas
                priceStr = priceStr.replaceAll("[^0-9.\\-]", "");
                double price;
                try {
                    price = Double.parseDouble(priceStr);
                } catch (NumberFormatException e) {
                    continue;
                }

                sumMap.put(isbn, sumMap.getOrDefault(isbn, 0.0) + price);
                countMap.put(isbn, countMap.getOrDefault(isbn, 0) + 1);
            }
        }

        Map<String, Double> averages = new HashMap<>();
        for (String k : sumMap.keySet()) {
            double sum = sumMap.get(k);
            int cnt = countMap.getOrDefault(k, 1);
            averages.put(k, sum / cnt);
        }

        return averages;
    }
}
