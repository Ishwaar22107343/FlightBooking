import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Main Page
        Scanner scanner = new Scanner(System.in);
        System.out.println("===Welcome to Cholaz Flight Booking Services===");
        System.out.println("======Please choose your choice of action======");
        do {
            System.out.println("1. Search for flight availability");
            System.out.println("2. Book a flight ticket");
            System.out.println("3. Edit flight ticket information");
            System.out.println("4. View flight ticket Status");
            System.out.println("5. Cancel a flight ticket");
            System.out.println("6. Exit System");

            System.out.print("Input your choice of action: ");
            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    FlightTicketBooking.searchFlight();
                    break;
                case "2":
                    FlightTicketBooking.bookTicket();
                    break;
                case "3":
                    FlightTicketBooking.editTicket();
                    break;
                case "4":
                    FlightTicketBooking.viewTicketStatus();
                    break;
                case "5":
                    FlightTicketBooking.cancelTicket();
                    break;
                case "6":
                    System.out.println("Thank you for choosing Cholaz Flight Booking Services");
                    System.out.println("==============================");
                    return;
                default:
                    System.out.println("Please enter a valid input.");
            }
        } while (true);
    }
}

class FlightTicketBooking {
    // creating queue waiting list
    private static final String inputFilePath = "FlightBooking.csv"; // Change according to ur file
    private static final SimpleDateFormat dateformat = new SimpleDateFormat("d/M/yyyy");

    // method to search flight
    public static void searchFlight() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter start date (d/M/yyyy): ");
        String startDate = scanner.nextLine();
        System.out.println("Enter end date (d/M/yyyy): ");
        String endDate = scanner.nextLine();

        boolean flightFound = false;
        List<String> displayedFlights = new ArrayList<>(); // List to track displayed flights per date

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));

            // Skip the header row
            reader.readLine();

            String line;
            System.out.println("Flights available between " + startDate + " and " + endDate + ":");
            System.out.println("Flight\tDate\tUnoccupied Seats");

            Date startDateObj = dateformat.parse(startDate);
            Date endDateObj = dateformat.parse(endDate);

            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                String flightName = details[0].trim();
                String flightDate = details[2].trim();
                String flightIdentifier = flightName + "-" + flightDate; // Unique identifier for a flight per date
                Date flightDateObj = dateformat.parse(flightDate);

                // Process only flights within the date range and not already displayed for this
                // date
                if (!flightDateObj.before(startDateObj) && !flightDateObj.after(endDateObj)
                        && !displayedFlights.contains(flightIdentifier)) {
                    int unoccupiedSeats = 0;

                    // Reopen the file to count unoccupied seats for this specific flight and date
                    BufferedReader seatReader = new BufferedReader(new FileReader(inputFilePath));
                    seatReader.readLine(); // Skip the header row again

                    String seatLine;
                    while ((seatLine = seatReader.readLine()) != null) {
                        String[] seatDetails = seatLine.split(",");
                        if (seatDetails[0].trim().equalsIgnoreCase(flightName)
                                && seatDetails[2].trim().equalsIgnoreCase(flightDate)) {
                            if (seatDetails[3].trim().isEmpty()) { // Check if passenger name is empty
                                unoccupiedSeats++;
                            }
                        }
                    }
                    seatReader.close();

                    // Display flight information
                    System.out.println(flightName + "\t" + flightDate + "\t" + unoccupiedSeats);
                    displayedFlights.add(flightIdentifier); // Add flight per date to displayed list
                    flightFound = true;
                }
            }

            reader.close();

            if (!flightFound) {
                System.out.println("No flights available during this duration.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error reading the file.");
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please enter in d/M/yyyy format.");
        }
    }

    public static void bookTicket() {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Flight Name: ");
        String flightName = scanner.nextLine();
        System.out.println("Enter Flight Date (d/M/yyyy): ");
        String flightDate = scanner.nextLine();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
            List<String[]> flightData = new ArrayList<>();
            String line;
            boolean flightFound = false;
            boolean seatAvailable = false;
            String assignedTicketNumber = "";

            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                flightData.add(details);

                if (details[0].trim().equalsIgnoreCase(flightName.trim())
                        && details[2].trim().equalsIgnoreCase(flightDate.trim())) {
                    flightFound = true;

                    // Check for unassigned seats
                    if (details[3].trim().isEmpty()) { // Check if passenger name is empty
                        seatAvailable = true;
                        assignedTicketNumber = details[5].trim(); // Get the ticket number
                        break; // Exit loop as we found an unassigned seat
                    }
                }
            }
            reader.close();

            if (!flightFound) {
                System.out.println("No flight found for the given name and date.");
                return;
            }

            if (seatAvailable) {
                // Get Passenger Details
                System.out.println("Seats available! Enter passenger details:");
                System.out.print("Passenger Name: ");
                String passengerName = scanner.nextLine();
                System.out.print("Passport Number: ");
                String passportNumber = scanner.nextLine();

                // Update the flight data with passenger details
                for (int i = 0; i < flightData.size(); i++) {
                    String[] details = flightData.get(i);
                    if (details[0].trim().equalsIgnoreCase(flightName.trim())
                            && details[2].trim().equalsIgnoreCase(flightDate.trim())
                            && details[5].trim().equals(assignedTicketNumber)) {
                        details[3] = passengerName; // Assign passenger name
                        details[4] = passportNumber; // Assign passport number
                        flightData.set(i, details); // Update the list with new details
                        break;
                    }
                }

                // Write updated flight data back to the file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(inputFilePath))) {
                    for (String[] flightDetail : flightData) {
                        writer.write(String.join(",", flightDetail));
                        writer.newLine();
                    }
                }

                System.out.println("Ticket booked successfully! Your Ticket ID is: " + assignedTicketNumber);

            } else {

                System.out.println("No seats available. Adding passenger to the waiting list.");
                System.out.print("Passenger Name: ");
                String passengerName = scanner.nextLine();
                System.out.print("Passport Number: ");
                String passportNumber = scanner.nextLine();

                // Determine the week number from the flight date
                Date flightDateObj = new SimpleDateFormat("d/M/yyyy").parse(flightDate);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(flightDateObj);
                int week = calendar.get(Calendar.WEEK_OF_YEAR);

                // Add to waiting list using WaitingListManager
                boolean addedToWaitingList = WaitingListManager.addToWaitingList(flightName, week, passengerName,
                        passportNumber, "WL-001", "1"); // Assuming capacity is 1 for waiting list
                if (addedToWaitingList) {
                    System.out.println("You have been added to the waiting list.");
                    // Save the waiting list to file
                    WaitingListManager.saveWaitingListsToFile();
                } else {
                    System.out.println("Failed to add to the waiting list.");
                }
            }
            System.out.println("You have been added to the waiting list.");

        } catch (IOException | ParseException exception) {
            System.out.println("Error accessing the flight data.");
        }
    }

    // method to edit ticket information
    public static void editTicket() {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Flight Name: ");
        String flightName = scanner.nextLine();
        System.out.println("Enter ticket number: ");
        String ticketNumber = scanner.nextLine();
        System.out.println("Enter date: ");
        String date = scanner.nextLine();

        boolean isEdited = false;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
            StringBuilder updatedContent = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                String[] details = new String[6];
                details = line.split(",");
                // Finding ticket that is intended to be edited
                if (details[0].trim().equalsIgnoreCase(flightName.trim())
                        && details[2].trim().equalsIgnoreCase(date.trim())
                        && details[5].trim().equalsIgnoreCase(ticketNumber.trim())) {

                    System.out.println("Ticket found in flight " + details[0] + " under passenger " + details[3]
                            + " passport number " + details[4]);
                    System.out.println("Enter new passenger name: ");
                    details[3] = scanner.nextLine();
                    System.out.println("Enter new Passport number: ");
                    details[4] = scanner.nextLine();
                    System.out.println("Editing passenger information...");
                    isEdited = true;
                }
                // Append current or updated line to updated content
                updatedContent.append(String.join(",", details)).append(System.lineSeparator());
            }
            reader.close();

            // Overwrite the file with updated content
            BufferedWriter writer = new BufferedWriter(new FileWriter(inputFilePath));
            writer.write(updatedContent.toString());
            writer.close();

            if (isEdited) {
                System.out.println("Ticket updated successfully...");
                System.out.println();
                System.out.println("==============================");
                System.out.println();
            } else {
                System.out.println("No matching ticket found. No changes made...");
                System.out.println();
                System.out.println("==============================");
                System.out.println();
            }

        } catch (FileNotFoundException e) {
            System.out.println("File Not Found");
        } catch (IOException e) {
            System.out.println("IO Exception thrown");
        }
    }

    // method to view ticket status
    public static void viewTicketStatus() {

        Scanner scanner = new Scanner(System.in);
        System.out.printf("Enter Flight Name: ");
        String flightName = scanner.nextLine();
        System.out.println("Enter Passenger Name: ");
        String passengerName = scanner.nextLine();
        System.out.println("Enter Passport Number: ");
        String passportNumber = scanner.nextLine();
        System.out.println("Enter date: ");
        String date = scanner.nextLine();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] details = new String[6];
                details = line.split(",");
                // Finding ticket under passenger name and passport number
                if (details[0].trim().equalsIgnoreCase(flightName.trim())
                        && details[2].trim().equalsIgnoreCase(date.trim())
                        && details[3].trim().equalsIgnoreCase(passengerName.trim())
                        && details[4].trim().equalsIgnoreCase(passportNumber.trim())) {
                    System.out.println("Ticket found in flight " + details[0] + " under passenger " + details[3]
                            + " passport number " + details[4]);
                    System.out.println("Ticket status: CONFIRMED");
                    break;
                }

            }

        } catch (FileNotFoundException e) {
            System.out.println("File Not Found");
        } catch (IOException e) {
            System.out.println("IO Exception thrown");
        }
    }

    public static void cancelTicket() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Passport Number: ");
        String passportNumber = scanner.nextLine();
        System.out.print("Enter Ticket Number: ");
        String ticketNumber = scanner.nextLine();

        StringBuilder updatedContent = new StringBuilder();
        boolean ticketFound = false;
        String canceledFlight = "";
        int canceledWeek = 0;

        try {
            // Read confirmed bookings file
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
            String line;

            // Process each line and find the ticket to cancel
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (details.length > 5 && details[4].trim().equals(passportNumber.trim()) &&
                        details[5].trim().equals(ticketNumber.trim())) {
                    ticketFound = true;
                    canceledFlight = details[0];
                    canceledWeek = extractWeekFromDate(details[2]); // Extract week from date

                    // Clear the ticket details from the file
                    updatedContent.append(details[0]).append(",").append(details[1]).append(",")
                            .append(details[2]).append(",").append("").append(",")
                            .append("").append(",").append("").append("\n");
                } else {
                    updatedContent.append(line).append("\n");
                }
            }
            reader.close();

            if (ticketFound) {
                // Update the confirmed tickets file with cleared details
                BufferedWriter writer = new BufferedWriter(new FileWriter("FlightBooking.csv"));
                writer.write(updatedContent.toString());
                writer.close();

                System.out.println("Ticket canceled successfully.");

                // Fetch the next passenger from the waiting list
                WaitingListManager.WaitingListPassenger nextPassenger = WaitingListManager
                        .removeFromWaitingList(canceledFlight, canceledWeek);
                if (nextPassenger != null) {
                    // Add the next passenger to the confirmed list
                    String newBooking = String.format("%s,%s,01/01/2024,%s,%s,%s\n",
                            canceledFlight,
                            nextPassenger.flightCapacity,
                            nextPassenger.passengerName,
                            nextPassenger.passportNumber,
                            nextPassenger.ticketNumber);

                    try (BufferedWriter writer2 = new BufferedWriter(new FileWriter("FlightBooking.csv", true))) {
                        writer2.write(newBooking);
                        System.out.println(
                                "Passenger from waiting list moved to confirmed list: " + nextPassenger.passengerName);
                    } catch (IOException e) {
                        System.err.println("Error saving the new booking to the file: " + e.getMessage());
                    }
                    // Save updated waiting lists to file
                    WaitingListManager.saveWaitingListsToFile();
                } else {
                    System.out.println("No passengers in the waiting list for this flight and week.");
                }
            } else {
                System.out.println("Ticket not found. No changes made.");
            }
        } catch (IOException e) {
            System.err.println("Error reading or writing the file: " + e.getMessage());
        }
    }

    // Helper method to extract week number from a date string
    private static int extractWeekFromDate(String date) {
        // Assuming the date format is DD/MM/YYYY
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            LocalDate localDate = LocalDate.parse(date, formatter);
            // Get the week number of the year
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            return localDate.get(weekFields.weekOfWeekBasedYear());
        } catch (Exception e) {
            System.err.println("Error parsing date: " + e.getMessage());
            return 1; // Default to week 1 if parsing fails
        }
    }

    class WaitingListManager {
        private static final String WAITING_LIST_FILE = "WaitingLists.csv";

        // Waiting List Passenger Class
        public static class WaitingListPassenger {
            String passengerName;
            String passportNumber;
            String ticketNumber;
            String flightCapacity;

            public WaitingListPassenger(String passengerName, String passportNumber, String ticketNumber,
                    String flightCapacity) {
                this.passengerName = passengerName;
                this.passportNumber = passportNumber;
                this.ticketNumber = ticketNumber;
                this.flightCapacity = flightCapacity;
            }
        }

        // Waiting Lists Storage
        private static final Map<String, Queue<WaitingListPassenger>> waitingLists = new HashMap<>();

        // Initialize waiting lists
        static {
            for (int flight = 1; flight <= 10; flight++) {
                for (int week = 1; week <= 52; week++) {
                    String key = "A" + flight + "_Week" + week;
                    waitingLists.put(key, new LinkedList<>());
                }
            }
        }

        // Add passenger to waiting list
        public static boolean addToWaitingList(String flight, int week, String passengerName, String passportNumber,
                String ticketNumber, String flightCapacity) {
            String key = flight + "_Week" + week;
            Queue<WaitingListPassenger> queue = waitingLists.get(key);

            if (queue == null) {
                System.out.println("Invalid flight or week number.");
                return false;
            }

            // Check if passenger is already in waiting list
            boolean alreadyExists = queue.stream()
                    .anyMatch(p -> p.passportNumber.equals(passportNumber));

            if (alreadyExists) {
                System.out.println("Passenger is already in the waiting list.");
                return false;
            }

            WaitingListPassenger passenger = new WaitingListPassenger(passengerName, passportNumber, ticketNumber,
                    flightCapacity);
            queue.add(passenger);
            System.out.println("Passenger added to waiting list for " + key);
            return true;
        }

        // Save waiting lists to file in exact CSV format
        // Save waiting lists to file in the exact confirmed ticket format
        public static void saveWaitingListsToFile() {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(WAITING_LIST_FILE))) {
                // Write header to match confirmed tickets format
                writer.write("Flight,Capacity,Date,Passenger Name,Passport Number,Ticket Number\n");

                for (Map.Entry<String, Queue<WaitingListPassenger>> entry : waitingLists.entrySet()) {
                    String key = entry.getKey();
                    String flight = key.split("_")[0];

                    int counter = 1; // Start counter for waiting list ticket numbers
                    for (WaitingListPassenger passenger : entry.getValue()) {
                        String ticketNumber = flight + "-WL" + String.format("%03d", counter++);

                        // Write each waiting list entry in the exact format of confirmed tickets
                        writer.write(String.format("%s,%s,01/01/2024,%s,%s,%s\n",
                                flight,
                                passenger.flightCapacity,
                                passenger.passengerName,
                                passenger.passportNumber,
                                ticketNumber));
                    }
                }
                System.out.println("Waiting lists saved successfully.");
            } catch (IOException e) {
                System.err.println("Error writing waiting lists to file: " + e.getMessage());
            }
        }

        // Load waiting lists from file
        public static void loadWaitingListsFromFile() {
            try (BufferedReader reader = new BufferedReader(new FileReader(WAITING_LIST_FILE))) {
                String line;
                reader.readLine(); // Skip header

                while ((line = reader.readLine()) != null) {
                    String[] details = line.split(",");
                    if (details.length < 6) {
                        System.err.println("Invalid waiting list entry: " + line);
                        continue;
                    }

                    // Extract details
                    String flight = details[0];
                    String flightCapacity = details[1];
                    String passengerName = details[3];
                    String passportNumber = details[4];
                    String ticketNumber = details[5];

                    // Dynamically determine week from ticket number
                    String weekString = ticketNumber.split("-")[1].substring(2); // Extract WL### (e.g., WL001)
                    int week = Integer.parseInt(weekString); // Convert to integer

                    // Add to waiting list
                    boolean added = addToWaitingList(flight, week, passengerName, passportNumber, ticketNumber,
                            flightCapacity);
                    if (!added) {
                        System.err.println("Failed to add passenger to waiting list: " + line);
                    }
                }
                System.out.println("Waiting lists loaded successfully.");
            } catch (FileNotFoundException e) {
                System.out.println("No existing waiting list file found.");
            } catch (IOException | NumberFormatException e) {
                System.err.println("Error loading waiting lists: " + e.getMessage());
            }
        }

        // Method to remove passenger from waiting list
        public static WaitingListPassenger removeFromWaitingList(String flight, int week) {
            String key = flight + "_Week" + week;
            Queue<WaitingListPassenger> queue = waitingLists.get(key);

            if (queue == null || queue.isEmpty()) {
                System.out.println("Waiting list is empty for " + key);
                return null;
            }

            return queue.poll();
        }
    }
}
