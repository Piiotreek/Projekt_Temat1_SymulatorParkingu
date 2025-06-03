import java.time.LocalDateTime;
import java.util.List;

/**
 * ParkingSimulator - Program symulacji parkingu.
 */
public class ParkingSimulator {

    public static abstract class Vehicle {
        public Vehicle(String licensePlate, LocalDateTime entryTime) {}
        public abstract double getHourlyRate();
        public abstract String getType();
        public String getLicensePlate() { return null; }
        public LocalDateTime getEntryTime() { return null; }
        public void setEntryTime(LocalDateTime entryTime) {}
    }

    public static class Car extends Vehicle {
        public Car(String licensePlate, LocalDateTime entryTime) { super(licensePlate, entryTime); }
        @Override
        public double getHourlyRate() { return 0; }
        @Override
        public String getType() { return null; }
    }

    public static class DeliveryVan extends Vehicle {
        public DeliveryVan(String licensePlate, LocalDateTime entryTime) { super(licensePlate, entryTime); }
        @Override
        public double getHourlyRate() { return 0; }
        @Override
        public String getType() { return null; }
    }

    public static class ParkingSpot {
        public ParkingSpot(int spotNumber) {}
        public boolean isOccupied() { return false; }
        public void parkVehicle(Vehicle vehicle) {}
        public void removeVehicle() {}
        public Vehicle getParkedVehicle() { return null; }
        public int getSpotNumber() { return 0; }
    }

    public static class ParkingLot {
        public ParkingLot(int capacity) {}
        public int getAvailableSpots() { return 0; }
        public boolean enterVehicle(Vehicle vehicle) { return false; }
        public ParkingPayment exitVehicle(String licensePlate, LocalDateTime exitTime) { return null; }
        public List<String> listParkedVehicles() { return null; }
        public String generateDailyReport() { return null; }
        public void clearDailyReport() {}
    }

    public static class ParkingPayment {
        public ParkingPayment(ParkingRecord record, double hoursParked, double fee) {}
        public ParkingRecord getRecord() { return null; }
        public double getHoursParked() { return 0; }
        public double getFee() { return 0; }
    }

    public static class ParkingRecord {
        public ParkingRecord(String licensePlate, String vehicleType, LocalDateTime entryTime, LocalDateTime exitTime, double fee) {}
        public String getLicensePlate() { return null; }
        public String getVehicleType() { return null; }
        public LocalDateTime getEntryTime() { return null; }
        public LocalDateTime getExitTime() { return null; }
        public double getFee() { return 0; }
    }

    public static class Program {
        public Program(int capacity) {}
        public void run() {}
        private void printWelcome() {}
        private void printMenu() {}
        private int readInt(String prompt) { return 0; }
        private String readNonEmptyString(String prompt) { return null; }
        private void handleVehicleEntry() {}
        private void handleVehicleExit() {}
        private void showAvailability() {}
        private void listParkedVehicles() {}
        private void generateDailyReport() {}
        private void clearDailyReport() {}
        private void advanceTime() {}
        public static void main(String[] args) {
            Program program = new Program(20);
            program.run();
        }
    }
}
