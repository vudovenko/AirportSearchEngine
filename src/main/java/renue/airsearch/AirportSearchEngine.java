package renue.airsearch;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class AirportSearchEngine {

    private static Map<String, Long> namesAndBytes;
    private static List<String> names;

    public static void main(String[] args) {
        namesAndBytes = new HashMap<>();
        names = new ArrayList<>();
        getAirportNamesAndByteNumbers();
        Collections.sort(names);
    }

    public static void getAirportNamesAndByteNumbers() {
        try (RandomAccessFile randomAccessFile
                     = new RandomAccessFile("airports.csv", "r")) {
            String line;
            long index = randomAccessFile.getFilePointer();
            while ((line = randomAccessFile.readLine()) != null) {
                String airName = getName(line);
                names.add(airName);
                namesAndBytes.put(airName, index);
                index = randomAccessFile.getFilePointer();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getName(String str) {
        String newStr = str.replace("\"", "");
        String[] strs = newStr.split(",");
        return strs[1];
    }
}