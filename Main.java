import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        //Main Page
        Scanner scanner = new Scanner(System.in);
        System.out.println("===Welcome to Cholaz Flight Booking Services===");
        System.out.println("======Please choose your choice of action======");
        do{
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
                FlightTicketBooking.viewTicketStatus() ;                 
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
    //creating queue waiting list 
    private static Queue<String> waitingList = new LinkedList<>();
    private static final String inputFilePath = "FlightBooking.csv"; // Change according to ur file
    private static final SimpleDateFormat dateformat = new SimpleDateFormat("d/M/yyyy");
    
    public static void waitingEnqueue(String passengerDetails) {
        waitingList.add(passengerDetails);
        System.out.println("You have been added to the waiting list");
    }

    public static String waitingDequeue() {
        if (waitingList.isEmpty()){
            System.out.println("Waiting list is empty");
            return null;
        }else{
            return waitingList.poll();
        }
    }

    //method to search flight
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

            // Process only flights within the date range and not already displayed for this date
            if (!flightDateObj.before(startDateObj) && !flightDateObj.after(endDateObj) && !displayedFlights.contains(flightIdentifier)) {
                int unoccupiedSeats = 0;

                // Reopen the file to count unoccupied seats for this specific flight and date
                BufferedReader seatReader = new BufferedReader(new FileReader(inputFilePath));
                seatReader.readLine(); // Skip the header row again

                String seatLine;
                while ((seatLine = seatReader.readLine()) != null) {
                    String[] seatDetails = seatLine.split(",");
                    if (seatDetails[0].trim().equalsIgnoreCase(flightName) && seatDetails[2].trim().equalsIgnoreCase(flightDate)) {
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
        public static void bookTicket(){

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

            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                flightData.add(details);

                if (details[0].trim().equalsIgnoreCase(flightName.trim()) && details[2].trim().equalsIgnoreCase(flightDate.trim())) {
                    flightFound = true;

                    // Check seat availability
                    int capacity = Integer.parseInt(details[1].trim());
                    int confirmedBookings = countConfirmedBookings(flightData, flightName, flightDate);
                    if (confirmedBookings < capacity) {
                        seatAvailable = true;
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

                String ticketID = generateTicketID();
                String newBooking = flightName + "," + flightDate + "," + passengerName + "," + passportNumber + "," + ticketID;

                // Append new booking to file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(inputFilePath, true))) {
                    writer.write(newBooking);
                    writer.newLine();
                    writer.close();
                }
                System.out.println("Ticket booked successfully! Your Ticket ID is: " + ticketID);

            } else {

                System.out.println("No seats available. Adding passenger to the waiting list.");
                System.out.print("Passenger Name: ");
                String passengerName = scanner.nextLine();
                System.out.print("Passport Number: ");
                String passportNumber = scanner.nextLine();
                waitingEnqueue(flightName + "," + flightDate + "," + passengerName + "," + passportNumber);

                System.out.println("You have been added to the waiting list.");
            }
        } catch (IOException e) {
            System.out.println("Error accessing the flight data.");
        }
    }

    private static int countConfirmedBookings(List<String[]> flightData, String flightName, String flightDate) {
        int count = 0;
        for (String[] record : flightData) {
            if (record[0].trim().equalsIgnoreCase(flightName.trim()) &&
                    record[1].trim().equalsIgnoreCase(flightDate.trim())) {
                count++;
            }
        }
        return count;
    }

    private static String generateTicketID() {
        return "TID" + System.currentTimeMillis();
    }

    //method to edit ticket information
    public static void editTicket(){

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Flight Name: ");
        String flightName = scanner.nextLine();
        System.out.println("Enter ticket number: ");
        String ticketNumber = scanner.nextLine();
        System.out.println("Enter date: ");
        String date = scanner.nextLine();

        boolean isEdited = false;

        try{
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
            StringBuilder updatedContent = new StringBuilder();
            String line;

            while((line = reader.readLine()) != null){
                String [] details = new String [6];
                details =line.split(",");
                //Finding ticket that is intended to be edited
                if(details[0].trim().equalsIgnoreCase(flightName.trim()) && details[2].trim().equalsIgnoreCase(date.trim()) && details [5].trim().equalsIgnoreCase(ticketNumber.trim())){

                    System.out.println("Ticket found in flight "+details[0]+" under passenger "+details[3]+" passport number "+details[4]);
                    System.out.println("Enter new passenger name: ");
                    details[3] = scanner.nextLine();
                    System.out.println("Enter new Passport number: ");
                    details[4] = scanner.nextLine();
                    System.out.println("Editing passenger information...");
                    isEdited = true;
                }
                //Append current or updated line to updated content
                updatedContent.append(String.join(",",details)).append(System.lineSeparator());
            }
            reader.close();

            //Overwrite the file with updated content
            BufferedWriter writer = new BufferedWriter(new FileWriter(inputFilePath));
            writer.write(updatedContent.toString());
            writer.close();

            if(isEdited){
                System.out.println("Ticket updated successfully...");
                System.out.println();
                System.out.println("==============================");
                System.out.println();
            }else{
                System.out.println("No matching ticket found. No changes made...");
                System.out.println();
                System.out.println("==============================");
                System.out.println();
            }

        }catch(FileNotFoundException e){
            System.out.println("File Not Found");
        }catch(IOException e){
            System.out.println("IO Exception thrown");
        }
    }

    //method to view ticket status
    public  static void viewTicketStatus(){

        Scanner scanner = new Scanner(System.in);
        System.out.printf("Enter Flight Name: ");
        String flightName = scanner.nextLine();
        System.out.println("Enter Passenger Name: ");
        String passengerName = scanner.nextLine();
        System.out.println("Enter Passport Number: ");
        String passportNumber = scanner.nextLine();
        System.out.println("Enter date: ");
        String date = scanner.nextLine();

        try{
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
            String line;

            while((line = reader.readLine()) != null){
                String [] details = new String [6];
                details =line.split(",");
                //Finding ticket under passenger name and passport number
                if(details[0].trim().equalsIgnoreCase(flightName.trim()) && details[2].trim().equalsIgnoreCase(date.trim()) && details [3].trim().equalsIgnoreCase(passengerName.trim()) && details [4].trim().equalsIgnoreCase(passportNumber.trim())){
                    System.out.println("Ticket found in flight "+details[0]+" under passenger "+details[3]+" passport number "+details[4]);
                    System.out.println("Ticket status: CONFIRMED");
                    break;
                }

            }

        }catch(FileNotFoundException e){
            System.out.println("File Not Found");
        }catch(IOException e){
            System.out.println("IO Exception thrown");
        }
    }

public static void cancelTicket(){

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Passport Number ");
        String passportNumber = scanner.nextLine();
        System.out.print("Enter ticket number: ");
        String ticketNumber = scanner.nextLine();

        //to replace cancelled tickets with empty space
        String content = "";
        //reading csv file
        String fileName = "FlightBooking.csv";
        //Arraylist to store the updated lines
        List<String> updatedLines = new ArrayList<>();
        //variable to see if the row has been updated
        boolean rowUpdated = false;
        try{
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            //separating the header and adding it to arraylist first
            String header = br.readLine();
            updatedLines.add(header);
            while((line = br.readLine()) != null){
                String[] text = line.split(",");
                //checks if the current line being read is same to the parameterds passed as args
                if ((text[4].equalsIgnoreCase(passportNumber)) && (text[5].equalsIgnoreCase(ticketNumber))){
                    //if line found to have same details, adding row with empty data to arraylist
                    updatedLines.add(text[0] + "," + text[1] + "," + text[2] 
                    + "," + "" + "," + "" + "," + text[5]);
                    rowUpdated=true;
                } else {
                    updatedLines.add(line);
                }
              }
        }catch(FileNotFoundException e){
            System.out.println("File not Found");
        }catch(IOException e){
            System.out.println("Error reading the file");
        }

        //if row update didnt occur after exiting while loop
        if (!rowUpdated){
            System.out.println("Booking information not found in database!\nPlease enter your details correctly.");
        }else {
            //row update got happen, ticket has been cancelled
            //Adding the person first on queue to the updated lines
            System.out.println("Cancelling is successful");
            updatedLines.add(waitingDequeue());
            System.out.println("Passenger is added from waiting list to confirmed list");
        }

        //writing from the updateLines arraylist to the csv file
        //successfully cancelling ticket from the database
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (String updatedLine : updatedLines) {
                bw.write(updatedLine);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to the file: ");
        }
    }
}
