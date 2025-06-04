import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class ParkingSimulator {


    public static abstract class Vehicle {
        private final String licensePlate;
        private LocalDateTime entryTime;


        public Vehicle(String licensePlate, LocalDateTime entryTime) {
            this.licensePlate = licensePlate;
            this.entryTime = entryTime;
        }

        public String getLicensePlate() {
            return licensePlate;
        }


        public LocalDateTime getEntryTime() {
            return entryTime;
        }


        public void setEntryTime(LocalDateTime entryTime) {
            this.entryTime = entryTime;
        }


        public abstract double getHourlyRate();


        public abstract String getType();


        @Override
        public String toString() {
            return String.format("%s [Numer: %s]", getType(), licensePlate);
        }
    }


    public static class Car extends Vehicle {
        private static final double HOURLY_RATE = 5.0;

        public Car(String licensePlate, LocalDateTime entryTime) {
            super(licensePlate, entryTime);
        }

        @Override
        public double getHourlyRate() {
            return HOURLY_RATE;
        }

        @Override
        public String getType() {
            return "Samochód";
        }
    }


    public static class DeliveryVan extends Vehicle {
        private static final double HOURLY_RATE = 8.0;

        public DeliveryVan(String licensePlate, LocalDateTime entryTime) {
            super(licensePlate, entryTime);
        }

        @Override
        public double getHourlyRate() {
            return HOURLY_RATE;
        }

        @Override
        public String getType() {
            return "Van dostawczy";
        }
    }


    public static class ParkingSpot {
        private final int spotNumber;
        private Vehicle parkedVehicle;


        public ParkingSpot(int spotNumber) {
            this.spotNumber = spotNumber;
            this.parkedVehicle = null;
        }


        public boolean isOccupied() {
            return parkedVehicle != null;
        }


        public void parkVehicle(Vehicle vehicle) {
            this.parkedVehicle = vehicle;
        }


        public void removeVehicle() {
            this.parkedVehicle = null;
        }


        public Vehicle getParkedVehicle() {
            return parkedVehicle;
        }


        public int getSpotNumber() {
            return spotNumber;
        }

        @Override
        public String toString() {
            if (isOccupied()) {
                return String.format("Miejsce #%d: %s od %s",
                        spotNumber,
                        parkedVehicle.toString(),
                        parkedVehicle.getEntryTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
            return String.format("Miejsce #%d: (Dostępne)", spotNumber);
        }
    }


    public static class ParkingLot {
        private final int capacity;
        private final List<ParkingSpot> spots;
        private final Map<String, ParkingRecord> parkingHistory;
        private final List<ParkingRecord> dailyRecords;
        private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        public ParkingLot(int capacity) {
            this.capacity = capacity;
            this.spots = new ArrayList<>(capacity);
            for (int i = 1; i <= capacity; i++) {
                spots.add(new ParkingSpot(i));
            }
            this.parkingHistory = new HashMap<>();
            this.dailyRecords = new ArrayList<>();
        }


        public int getAvailableSpots() {
            int count = 0;
            for (ParkingSpot spot : spots) {
                if (!spot.isOccupied()) {
                    count++;
                }
            }
            return count;
        }


        private int findFirstAvailableSpotIndex() {
            for (int i = 0; i < spots.size(); i++) {
                if (!spots.get(i).isOccupied()) {
                    return i;
                }
            }
            return -1;
        }


        public boolean enterVehicle(Vehicle vehicle) {
            int spotIndex = findFirstAvailableSpotIndex();
            if (spotIndex == -1) {
                return false;
            }
            spots.get(spotIndex).parkVehicle(vehicle);
            return true;
        }


        public ParkingPayment exitVehicle(String licensePlate, LocalDateTime exitTime) {
            ParkingSpot spot = findVehicleSpot(licensePlate);
            if (spot == null) {
                return null;
            }
            Vehicle vehicle = spot.getParkedVehicle();
            Vehicle clonedVehicle = cloneVehicle(vehicle);


            long minutesParked = Duration.between(vehicle.getEntryTime(), exitTime).toMinutes();
            if (minutesParked < 0) minutesParked = 0;
            double hoursParked = Math.ceil(minutesParked / 60.0);
            double fee = hoursParked * vehicle.getHourlyRate();

            spot.removeVehicle();


            ParkingRecord record = new ParkingRecord(vehicle.getLicensePlate(), vehicle.getType(),
                    vehicle.getEntryTime(), exitTime, fee);
            dailyRecords.add(record);
            parkingHistory.put(vehicle.getLicensePlate(), record);

            return new ParkingPayment(record, hoursParked, fee);
        }


        private ParkingSpot findVehicleSpot(String licensePlate) {
            for (ParkingSpot spot : spots) {
                if (spot.isOccupied() && spot.getParkedVehicle().getLicensePlate().equalsIgnoreCase(licensePlate)) {
                    return spot;
                }
            }
            return null;
        }


        public List<String> listParkedVehicles() {
            List<String> list = new ArrayList<>();
            for (ParkingSpot spot : spots) {
                if (spot.isOccupied()) {
                    Vehicle v = spot.getParkedVehicle();
                    String s = String.format("Miejsce #%d: %s, Wjazd: %s",
                            spot.getSpotNumber(),
                            v.toString(),
                            v.getEntryTime().format(dtf));
                    list.add(s);
                }
            }
            return list;
        }


        public String generateDailyReport() {
            StringBuilder sb = new StringBuilder();
            sb.append("Dzienny Raport Parkingowy\n");
            sb.append("====================\n");
            if (dailyRecords.isEmpty()) {
                sb.append("Dziś nie wyjechały żadne pojazdy.\n");
            } else {
                double totalIncome = 0.0;
                sb.append(String.format("%-15s %-12s %-20s %-20s %-10s\n",
                        "Numer rejestracyjny", "Typ", "Czas wjazdu", "Czas wyjazdu", "Opłata"));
                for (ParkingRecord record : dailyRecords) {
                    sb.append(String.format("%-15s %-12s %-20s %-20s %8.2f\n",
                            record.getLicensePlate(),
                            record.getVehicleType(),
                            record.getEntryTime().format(dtf),
                            record.getExitTime().format(dtf),
                            record.getFee()));
                    totalIncome += record.getFee();
                }
                sb.append("====================\n");
                sb.append(String.format("Łączna liczba wyjeżdżających pojazdów: %d\n", dailyRecords.size()));
                sb.append(String.format("Łączny dochód: %.2f\n", totalIncome));
            }
            return sb.toString();
        }


        public void clearDailyReport() {
            dailyRecords.clear();
        }


        public static class ParkingPayment {
            private final ParkingRecord record;
            private final double hoursParked;
            private final double fee;

            public ParkingPayment(ParkingRecord record, double hoursParked, double fee) {
                this.record = record;
                this.hoursParked = hoursParked;
                this.fee = fee;
            }

            public ParkingRecord getRecord() {
                return record;
            }

            public double getHoursParked() {
                return hoursParked;
            }

            public double getFee() {
                return fee;
            }
        }


        public static class ParkingRecord {
            private final String licensePlate;
            private final String vehicleType;
            private final LocalDateTime entryTime;
            private final LocalDateTime exitTime;
            private final double fee;

            public ParkingRecord(String licensePlate, String vehicleType,
                                 LocalDateTime entryTime, LocalDateTime exitTime, double fee) {
                this.licensePlate = licensePlate;
                this.vehicleType = vehicleType;
                this.entryTime = entryTime;
                this.exitTime = exitTime;
                this.fee = fee;
            }

            public String getLicensePlate() {
                return licensePlate;
            }

            public String getVehicleType() {
                return vehicleType;
            }

            public LocalDateTime getEntryTime() {
                return entryTime;
            }

            public LocalDateTime getExitTime() {
                return exitTime;
            }

            public double getFee() {
                return fee;
            }
        }


        private Vehicle cloneVehicle(Vehicle vehicle) {
            if (vehicle instanceof Car) {
                return new Car(vehicle.getLicensePlate(), vehicle.getEntryTime());
            } else if (vehicle instanceof DeliveryVan) {
                return new DeliveryVan(vehicle.getLicensePlate(), vehicle.getEntryTime());
            } else {
                throw new IllegalArgumentException("Nieznany typ pojazdu");
            }
        }
    }


    public static class Program {
        private static final Scanner scanner = new Scanner(System.in);
        private final ParkingLot parkingLot;
        private LocalDateTime currentSimTime;


        public Program(int capacity) {
            this.parkingLot = new ParkingLot(capacity);
            this.currentSimTime = LocalDateTime.now();
        }


        public void run() {
            printWelcome();
            boolean exitRequested = false;

            while (!exitRequested) {
                printMenu();
                int choice = readInt("Wybierz opcję: ");
                switch (choice) {
                    case 1:
                        handleVehicleEntry();
                        break;
                    case 2:
                        handleVehicleExit();
                        break;
                    case 3:
                        showAvailability();
                        break;
                    case 4:
                        listParkedVehicles();
                        break;
                    case 5:
                        generateDailyReport();
                        break;
                    case 6:
                        clearDailyReport();
                        break;
                    case 7:
                        advanceTime();
                        break;
                    case 8:
                        exitRequested = true;
                        System.out.println("Zamykam program. Do widzenia!");
                        break;
                    default:
                        System.out.println("Nieprawidłowy wybór, proszę wybrać prawidłową opcję.");
                }
            }
        }

        private void printWelcome() {
            System.out.println("====================================");
            System.out.println("    WITAMY W SYMULATORZE PARKINGU    ");
            System.out.println("====================================");
            System.out.println("Aktualny czas systemowy: " + currentSimTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            System.out.println();
        }

        private void printMenu() {
            System.out.println("\nMenu:");
            System.out.println("1. Rejestracja wjazdu pojazdu");
            System.out.println("2. Rejestracja wyjazdu pojazdu");
            System.out.println("3. Sprawdzenie dostępności miejsc parkingowych");
            System.out.println("4. Lista aktualnie zaparkowanych pojazdów");
            System.out.println("5. Generowanie raportu dziennego");
            System.out.println("6. Wyczyść raport dzienny (symuluj nowy dzień)");
            System.out.println("7. Przesuń czas symulacji");
            System.out.println("8. Zamknij program");
        }

        private int readInt(String prompt) {
            int result = -1;
            while (result < 0) {
                System.out.print(prompt);
                try {
                    result = Integer.parseInt(scanner.nextLine());
                    if (result < 0) {
                        System.out.println("Proszę wprowadzić liczbę całkowitą nieujemną.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Nieprawidłowe dane, proszę wprowadzić liczbę całkowitą.");
                }
            }
            return result;
        }

        private String readNonEmptyString(String prompt) {
            String result = "";
            while (result.isEmpty()) {
                System.out.print(prompt);
                result = scanner.nextLine().trim();
                if (result.isEmpty()) {
                    System.out.println("Wprowadzenie nie może być puste, proszę spróbować ponownie.");
                }
            }
            return result;
        }

        private void handleVehicleEntry() {
            System.out.println("\nRejestracja Wjazdu Pojazdu");
            System.out.println("---------------------");

            if (parkingLot.getAvailableSpots() == 0) {
                System.out.println("Przykro mi, parking jest pełny. Brak dostępnych miejsc.");
                return;
            }

            String licensePlate = readNonEmptyString("Wprowadź numer rejestracyjny: ").toUpperCase();
            if (isVehicleAlreadyParked(licensePlate)) {
                System.out.println("Ten pojazd jest już zaparkowany.");
                return;
            }

            System.out.println("Wybierz typ pojazdu:");
            System.out.println("1. Samochód (stawka: 5.00 za godzinę)");
            System.out.println("2. Van dostawczy (stawka: 8.00 za godzinę)");
            int typeChoice = readInt("Wybór: ");

            Vehicle vehicle = null;
            switch (typeChoice) {
                case 1:
                    vehicle = new Car(licensePlate, currentSimTime);
                    break;
                case 2:
                    vehicle = new DeliveryVan(licensePlate, currentSimTime);
                    break;
                default:
                    System.out.println("Nieprawidłowy wybór typu pojazdu.");
                    return;
            }

            if (parkingLot.enterVehicle(vehicle)) {
                System.out.println("Pojazd wjechał i został pomyślnie zaparkowany o " + currentSimTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } else {
                System.out.println("Nie udało się zaparkować pojazdu. Parking może być pełny.");
            }
        }

        private boolean isVehicleAlreadyParked(String licensePlate) {
            List<String> parked = parkingLot.listParkedVehicles();
            for (String s : parked) {
                if (s.toLowerCase().contains(licensePlate.toLowerCase())) {
                    return true;
                }
            }
            return false;
        }

        private void handleVehicleExit() {
            System.out.println("\nRejestracja Wyjazdu Pojazdu");
            System.out.println("---------------------");

            String licensePlate = readNonEmptyString("Wprowadź numer rejestracyjny: ").toUpperCase();

            ParkingLot.ParkingPayment payment = parkingLot.exitVehicle(licensePlate, currentSimTime);
            if (payment == null) {
                System.out.println("Pojazd o numerze rejestracyjnym '" + licensePlate + "' nie został znaleziony na parkingu.");
                return;
            }

            System.out.println("Pojazd wyjechał pomyślnie.");
            System.out.printf("Typ: %s\n", payment.getRecord().getVehicleType());
            System.out.printf("Czas wjazdu: %s\n", payment.getRecord().getEntryTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            System.out.printf("Czas wyjazdu: %s\n", payment.getRecord().getExitTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            System.out.printf("Czas trwania: %.1f godz.(y)\n", payment.getHoursParked());
            System.out.printf("Kwota do zapłaty: %.2f jednostek walutowych\n", payment.getFee());
        }

        private void showAvailability() {
            int available = parkingLot.getAvailableSpots();
            System.out.printf("Aktualnie dostępne miejsca parkingowe: %d z %d\n", available, parkingLot.capacity);
        }

        private void listParkedVehicles() {
            System.out.println("\nAktualnie Zaparkowane Pojazdy:");
            System.out.println("--------------------------");
            List<String> parkedVehicles = parkingLot.listParkedVehicles();
            if (parkedVehicles.isEmpty()) {
                System.out.println("Parking jest pusty.");
            } else {
                for (String s : parkedVehicles) {
                    System.out.println(s);
                }
            }
        }

        private void generateDailyReport() {
            System.out.println("\nDzienny Raport Parkingowy");
            System.out.println("--------------------");
            System.out.print(parkingLot.generateDailyReport());
        }

        private void clearDailyReport() {
            parkingLot.clearDailyReport();
            System.out.println("Raport dzienny wyczyszczony. Rozpoczęto nowy dzień.");
        }

        private void advanceTime() {
            System.out.println("\nPrzesunięcie Czasu Symulacji");
            System.out.println("-----------------------");
            System.out.println("Aktualny czas symulacji: " + currentSimTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            int hours = readInt("Wprowadź godziny do przesunięcia: ");
            int minutes = readInt("Wprowadź minuty do przesunięcia: ");
            currentSimTime = currentSimTime.plusHours(hours).plusMinutes(minutes);
            System.out.println("Czas symulacji przesunięty do: " + currentSimTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }


        public static void main(String[] args) {
            final int parkingCapacity = 20; // Stała liczba miejsc parkingowych do demonstracji; można dostosować
            Program program = new Program(parkingCapacity);
            program.run();
        }}}
