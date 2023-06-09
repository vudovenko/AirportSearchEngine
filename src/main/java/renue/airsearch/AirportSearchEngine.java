package renue.airsearch;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class AirportSearchEngine {

    private static Map<String, Long> namesAndBytes;
    private static List<String> names;

    public static void main(String[] args) {
        namesAndBytes = new HashMap<>();
        names = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        getAirportNamesAndByteNumbers();
        Collections.sort(names);
        startRequestingAirportNames(scanner);
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

    public static void startRequestingAirportNames(Scanner scanner) {
        String airportName = getBeginningAirportName(scanner);
        while (!airportName.equals("!quit")) {
            long startTime = System.nanoTime();
            List<String> desiredAirports = getDesiredAirports(airportName);
            if (desiredAirports.size() == 0) {
                System.out.println("Искомые аэропорты не найдены!");
                airportName = getBeginningAirportName(scanner);
                continue;
            }
            getRequiredInformationAboutAirports(desiredAirports);
            long endTime = System.nanoTime();
            long milliseconds = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
            System.out.println("Количество найденных строк: " + desiredAirports.size()
                    + "\nВремя, затраченное на поиск: " + milliseconds + " мс");
            airportName = getBeginningAirportName(scanner);
        }
    }

    public static String getBeginningAirportName(Scanner scanner) {
        System.out.println("Введите начало имени аэропорта: ");
        String airName = scanner.nextLine();
        if (airName.equals("")) {
            System.out.println("Вы ввели пустую строку - введите название аэропорта");
            airName = getBeginningAirportName(scanner);
        }

        return airName;
    }

    public static List<String> getDesiredAirports(String subname) {
        int startIndex = binarySearchFirstString(names, subname);
        int endIndex = binarySearchLastString(names, subname);
        List<String> airports = new ArrayList<>();

        for (int i = startIndex; i <= endIndex; i++) {
            if (i == -1) {
                continue;
            }
            airports.add(names.get(i));
        }

        return airports;
    }

    public static void getRequiredInformationAboutAirports(List<String> airportNames) {
        for (String airName : airportNames) {
            Long airIndex = namesAndBytes.get(airName);
            try (RandomAccessFile randomAccessFile
                         = new RandomAccessFile("airports.csv", "r")) {
                randomAccessFile.seek(airIndex);
                String airportInformation = randomAccessFile.readLine();
                System.out.printf("\"%s\"[%s]\n", airName, airportInformation);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Бинарный поиск самой первой строки, содержащей подстроку
    private static int binarySearchFirstString(List<String> names, String subname) {
        int low = 0;
        int high = names.size() - 1;
        int result = -1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            String currentString = names.get(mid);

            if (currentString.toLowerCase().startsWith(subname.toLowerCase())) {
                // Нашли строку, начинающуюся с подстроки
                result = mid;
                high = mid - 1; // Переходим к поиску в левой половине
            } else if (currentString.toLowerCase().compareTo(subname.toLowerCase()) < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return result;
    }

    // Бинарный поиск самой последней строки, содержащей подстроку
    private static int binarySearchLastString(List<String> names, String subnames) {
        int low = 0;
        int high = names.size() - 1;
        int result = -1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            String currentString = names.get(mid);

            if (currentString.toLowerCase().startsWith(subnames.toLowerCase())) {
                // Нашли строку, начинающуюся с подстроки
                result = mid;
                low = mid + 1; // Переходим к поиску в правой половине
            } else if (currentString.toLowerCase().compareTo(subnames.toLowerCase()) < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return result;
    }
}
