import java.util.*;
import java.io.*;

public class BusReservationSystem {
    static final int MAX_SEATS = 40;
    static final String ADMIN_PASSWORD = "admin123";
    static final String DATA_FILE = "bus_data.txt";
    static Scanner scanner = new Scanner(System.in);
    static Seat[] seats = new Seat[MAX_SEATS];

    public static void main(String[] args) {
        loadDataFromFile();
        while (true) {
            clearScreen();
            System.out.println("=====================================");
            System.out.println("     Bus Ticket Reservation System");
            System.out.println("=====================================");
            System.out.println("1. Admin Login");
            System.out.println("2. Traveler Interface");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1: adminLogin(); break;
                case 2: travelerInterface(); break;
                case 3:
                    saveDataToFile();
                    System.out.println("Exiting... Data saved successfully.");
                    return;
                default: System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    static void adminLogin() {
        scanner.nextLine(); // consume newline
        clearScreen();
        System.out.print("Enter Admin Password: ");
        String password = scanner.nextLine();
        if (password.equals(ADMIN_PASSWORD)) {
            System.out.println("Login successful!");
            adminPanel();
        } else {
            System.out.println("Invalid password. Returning to main menu.");
        }
    }

    static void adminPanel() {
        while (true) {
            clearScreen();
            System.out.println("=====================================");
            System.out.println("          Admin Panel");
            System.out.println("=====================================");
            System.out.println("1. View Available Seats");
            System.out.println("2. Update Seat Prices");
            System.out.println("3. Return to Main Menu");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1: viewAvailableSeats(); break;
                case 2: updateSeatPrices(); break;
                case 3: return;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
    }

    static void travelerInterface() {
        while (true) {
            clearScreen();
            System.out.println("=====================================");
            System.out.println("         Traveler Interface");
            System.out.println("=====================================");
            System.out.println("1. View Available Seats");
            System.out.println("2. Reserve a Seat");
            System.out.println("3. Reserve Seats for Group");
            System.out.println("4. Cancel Reservation");
            System.out.println("5. Return to Main Menu");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1: viewAvailableSeats(); break;
                case 2: reserveSeat(); break;
                case 3: reserveSeatsForGroup(); break;
                case 4: cancelReservation(); break;
                case 5: return;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
    }

    static void viewAvailableSeats() {
        clearScreen();
        for (Seat seat : seats) {
            if (!seat.isReserved) {
                System.out.println("Seat " + seat.seatNumber + ": Available");
            } else {
                System.out.println("Seat " + seat.seatNumber + ": Reserved by " + seat.passengerName + " (" + seat.seatType + ")");
            }
        }
        pause();
    }

    static void reserveSeat() {
        clearScreen();
        System.out.print("Enter seat number (1-" + MAX_SEATS + "): ");
        int seatNumber = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (seatNumber < 1 || seatNumber > MAX_SEATS || seats[seatNumber - 1].isReserved) {
            System.out.println("Invalid or already reserved seat.");
            pause();
            return;
        }

        System.out.print("Enter origin distance: ");
        int origin = scanner.nextInt();
        System.out.print("Enter destination distance: ");
        int destination = scanner.nextInt();
        scanner.nextLine(); // consume newline

        System.out.print("Enter seat type (economy/premium): ");
        String seatType = scanner.nextLine();

        if (!seatType.equals("economy") && !seatType.equals("premium")) {
            System.out.println("Invalid seat type.");
            pause();
            return;
        }

        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        float fare = calculateFare(origin, destination, seatType);
        Seat seat = seats[seatNumber - 1];
        seat.isReserved = true;
        seat.passengerName = name;
        seat.seatType = seatType;
        seat.originDistance = origin;
        seat.destinationDistance = destination;

        System.out.printf("Seat %d reserved for %s. Fare: $%.2f\n", seatNumber, name, fare);
        System.out.println("SMS: Your seat has been successfully booked.");
        pause();
    }

    static void reserveSeatsForGroup() {
        clearScreen();
        System.out.print("Enter number of seats to reserve: ");
        int numSeats = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter seat type (economy/premium): ");
        String seatType = scanner.nextLine();
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter origin distance: ");
        int origin = scanner.nextInt();
        System.out.print("Enter destination distance: ");
        int destination = scanner.nextInt();

        int count = 0;
        for (Seat seat : seats) {
            if (!seat.isReserved && count < numSeats) {
                seat.isReserved = true;
                seat.passengerName = name;
                seat.seatType = seatType;
                seat.originDistance = origin;
                seat.destinationDistance = destination;
                count++;
            }
        }

        if (count == numSeats) {
            System.out.println("Group of " + numSeats + " seats reserved successfully.");
        } else {
            System.out.println("Not enough seats available.");
        }

        float total = 0;
        System.out.print("Seats reserved: ");
        for (Seat seat : seats) {
            if (seat.isReserved && seat.passengerName.equals(name)) {
                System.out.print(seat.seatNumber + " ");
                float cost = calculateFare(seat.originDistance, seat.destinationDistance, seat.seatType);
                total += cost;
            }
        }
        System.out.printf("\nTotal fare: $%.2f\n", total);
        System.out.println("SMS: Your seats have been successfully booked.");
        pause();
    }

    static void cancelReservation() {
        clearScreen();
        System.out.print("Enter seat number to cancel (1-" + MAX_SEATS + "): ");
        int seatNumber = scanner.nextInt();

        if (seatNumber < 1 || seatNumber > MAX_SEATS || !seats[seatNumber - 1].isReserved) {
            System.out.println("Invalid or unreserved seat.");
            pause();
            return;
        }

        Seat seat = seats[seatNumber - 1];
        seat.isReserved = false;
        seat.passengerName = "";
        seat.seatType = "economy";
        seat.originDistance = 0;
        seat.destinationDistance = 0;
        System.out.println("Reservation cancelled successfully.");
        System.out.println("SMS: Your reservation has been successfully cancelled.");
        pause();
    }

    static void updateSeatPrices() {
        clearScreen();
        System.out.print("Enter seat number to update (1-" + MAX_SEATS + "): ");
        int seatNumber = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter seat type (economy/premium): ");
        String seatType = scanner.nextLine();

        if (seatNumber < 1 || seatNumber > MAX_SEATS) {
            System.out.println("Invalid seat number.");
            pause();
            return;
        }

        seats[seatNumber - 1].seatType = seatType;
        System.out.println("Seat " + seatNumber + " type updated to " + seatType + ".");
        pause();
    }

    static float calculateFare(int origin, int destination, String seatType) {
        int distance = Math.abs(destination - origin);
        float base = 0.1f * distance;
        return seatType.equals("premium") ? base * 1.5f : base;
    }

    static void saveDataToFile() {
        try (PrintWriter writer = new PrintWriter(DATA_FILE)) {
            for (Seat seat : seats) {
                writer.printf("%d %b %s %s %d %d\n", seat.seatNumber, seat.isReserved, seat.passengerName,
                        seat.seatType, seat.originDistance, seat.destinationDistance);
            }
        } catch (IOException e) {
            System.out.println("Error saving data.");
        }
    }

    static void loadDataFromFile() {
        for (int i = 0; i < MAX_SEATS; i++) {
            seats[i] = new Seat(i + 1);
        }

        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (Scanner fileScanner = new Scanner(file)) {
            for (int i = 0; i < MAX_SEATS; i++) {
                int num = fileScanner.nextInt();
                boolean reserved = fileScanner.nextBoolean();
                String name = fileScanner.next();
                String type = fileScanner.next();
                int origin = fileScanner.nextInt();
                int dest = fileScanner.nextInt();
                seats[i] = new Seat(num, reserved, name, type, origin, dest);
            }
        } catch (Exception e) {
            System.out.println("Error loading data. Initializing fresh data.");
        }
    }

    static void pause() {
        System.out.println("Press Enter to continue...");
        scanner.nextLine(); // consume newline
        scanner.nextLine();
    }
}

// Seat class
class Seat {
    int seatNumber;
    boolean isReserved;
    String passengerName;
    String seatType;
    int originDistance;
    int destinationDistance;

    public Seat(int seatNumber) {
        this.seatNumber = seatNumber;
        this.isReserved = false;
        this.passengerName = "";
        this.seatType = "economy";
        this.originDistance = 0;
        this.destinationDistance = 0;
    }

    public Seat(int seatNumber, boolean isReserved, String passengerName, String seatType, int originDistance, int destinationDistance) {
        this.seatNumber = seatNumber;
        this.isReserved = isReserved;
        this.passengerName = passengerName;
        this.seatType = seatType;
        this.originDistance = originDistance;
        this.destinationDistance = destinationDistance;
    }
}
