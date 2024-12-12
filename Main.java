import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        //Main Page
        Scanner scanner = new Scanner(System.in);
        System.out.println("===Welcome to Cholaz Flight Booking Services===");

        do{
            System.out.println("======Please choose your choice of action======");
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
    private static final String inputFilePath = "C://Users//ISHWAAR//Documents//SEM3//DS//testing.csv"; // Change according to ur file
    private static final String waitingListFilePath = "C://Users//ISHWAAR//Documents//SEM3//DS//testing_waiting_list.csv";




    //method to search flight
    public static void searchFlight() {

        String start;
        String end;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter start date (dd mm yyyy): ");
        start = scanner.nextLine();

        System.out.println("Enter end date (dd mm yyyy): ");
        end = scanner.nextLine();

        boolean flightFound = false;
        ArrayList <String> availableFlight = new ArrayList<>();
        ArrayList <String> availableFlightName = new ArrayList<>();
        ArrayList <String> availableDate = new ArrayList<>();
        ArrayList <String> capacity = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
            String line;

            System.out.println("Flights available between " + start+ " and " + end+" :");
            System.out.println("Flight\tDate\t\t\tCapacity");

            while ((line = reader.readLine()) != null) {
                // Skip empty or malformed lines
                if (line.trim().isEmpty())
                    continue;

                String[] details = line.split(",");
                String[] dateS = start.split("/");
                int[] startDate = new int [3];
                startDate[0] = Integer.parseInt(dateS[0]);
                startDate[1] = Integer.parseInt(dateS[1]);
                startDate[2] = Integer.parseInt(dateS[2]);
                String[] dateE = end.split("/");
                int[] endDate = new int [3];
                endDate[0] = Integer.parseInt(dateE[0]);
                endDate[1] = Integer.parseInt(dateE[1]);
                endDate[2] = Integer.parseInt(dateE[2]);

                // Ensure the line has at least 3 columns (Flight, Capacity, Date)
                if (details.length < 3) {
                    continue;
                }

                String[] parts = details[2].split("/");
                if (parts.length != 3) {
                    continue;
                }

                int[] flightDate = new int[3];
                try {
                    // Parse the flight date
                    for (int i = 0; i < 3; i++) {
                        flightDate[i] = Integer.parseInt(parts[i]);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid date format: " + details[2]);
                    continue;
                }

                // Check if the flight date is within the specified range
                if (startDate[2] <= flightDate[2] && endDate[2] >= flightDate[2]) {
                    if(startDate[1] >= flightDate[1] && endDate[1] <= flightDate[1]){
                        if(startDate[0] <= flightDate[0] && endDate[0] >= flightDate[0]){
                            if(availableFlight.contains(details[0]+","+details[2]+","+details[1])) {
                                continue;
                            }else{
                                availableFlight.add(details[0]+","+details[2]+","+details[1]);
                                availableFlightName.addFirst(details[0]);
                                availableDate.add(details[2]);
                                capacity.add(details[1]);
                                flightFound = true;
                            }
                        }
                    }

                    flightFound = true;
                }
            }

            reader.close();
            for (int i = 0; i < availableFlight.size(); i++) {
                System.out.println(availableFlightName.get(i) + "\t\t" + availableDate.get(i) + "\t\t" + capacity.get(i));
            }
            System.out.println();
            System.out.println("==============================");
            System.out.println();


            if (!flightFound) {
                System.out.println("No flights available during this duration.");
                System.out.println("==============================");
                System.out.println();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error reading the file.");
        }
    }

    public static void bookTicket(){

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Flight Name: ");
        String flightName = scanner.nextLine();
        System.out.println("Enter Flight Date (d/m/yyyy): ");
        String flightDate = scanner.nextLine();

        Queue<String> waitingList = loadWaitingList(flightName, flightDate);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
            List<String[]> flightData = new ArrayList<>();
            String line;
            boolean flightFound = false;
            boolean seatAvailable = false;

            while ((line = reader.readLine()) != null) {

                if(line.trim().isEmpty()){
                    continue;
                }

                String[] details = line.split(",");

                if(details.length < 6){
                    continue;
                }
                flightData.add(details);

                if (details[0].trim().equalsIgnoreCase(flightName.trim()) && details[2].trim().equalsIgnoreCase(flightDate.trim())) {
                    flightFound = true;

                    // Check seat availability
                    if(details[3].trim().isEmpty() && details[4].trim().isEmpty()){
                        seatAvailable = true;
                    }
                }
            }
            reader.close();

            if (!flightFound) {
                System.out.println("No flight found for the given name and date.");
                System.out.println("==============================");
                System.out.println();
                return;
            }

            if (seatAvailable) {
                // Get Passenger Details
                System.out.println("Seats available! Enter passenger details:");
                System.out.print("Passenger Name: ");
                String passengerName = scanner.nextLine();
                System.out.print("Passport Number: ");
                String passportNumber = scanner.nextLine();

                for (String[] record : flightData){
                    if(record[0].trim().equalsIgnoreCase(flightName.trim()) && record[2].trim().equalsIgnoreCase(flightDate.trim()) && record[3].trim().isEmpty() && record[4].trim().isEmpty()){
                        record[3] = passengerName;
                        record[4] = passportNumber;
                        break;
                    }
                }

                //Write updated data back to CSV file
                BufferedWriter writer = new BufferedWriter(new FileWriter(inputFilePath));
                for (String[] record : flightData){
                    writer.write(String.join(",", record));
                    writer.newLine();
                }
                writer.close();
                System.out.println("Ticket booked successfully for "+passengerName+".");
                System.out.println("==============================");
                System.out.println();
            } else {

                System.out.println("No seats available. Adding passenger to the waiting list.");
                System.out.print("Passenger Name: ");
                String passengerName = scanner.nextLine();
                System.out.print("Passport Number: ");
                String passportNumber = scanner.nextLine();

                String waitingListEntry = flightName + "," + flightDate + "," + passengerName + "," +passportNumber;
                waitingList.add(waitingListEntry);
                saveWaitingList(waitingList);

                System.out.println("You have been added to the waiting list.");
                System.out.println("==============================");
                System.out.println();
            }
        } catch (IOException e) {
            System.out.println("Error accessing the flight data.");
        }
    }
    private static Queue<String> loadWaitingList(String flightName, String flightDate) {
        Queue<String> waitingList = new LinkedList<>();
        Queue<String> otherWaitingList = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(waitingListFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (!line.trim().isEmpty() && details.length == 4) {
                    if (details[0].trim().equalsIgnoreCase(flightName.trim()) &&
                            details[1].trim().equalsIgnoreCase(flightDate.trim())) {
                        waitingList.add(line);
                    } else {
                        otherWaitingList.add(line);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading the waiting list.");
        }

        // Write back only `otherWaitingList` to preserve entries not related to the current flight
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(waitingListFilePath))) {
            while (!otherWaitingList.isEmpty()) {
                writer.write(otherWaitingList.poll());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating the waiting list file.");
        }

        return waitingList;
    }

    private static void saveWaitingList(Queue<String> waitingList) {
        Queue<String> tempQueue = new LinkedList<>(waitingList); // Copy to preserve original
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(waitingListFilePath, true))) {
            while (!tempQueue.isEmpty()) {
                writer.write(tempQueue.poll());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving the waiting list.");
        }
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
        System.out.println("Enter Flight Name: ");
        String flightName = scanner.nextLine();
        System.out.println("Enter Passenger Name: ");
        String passengerName = scanner.nextLine();
        System.out.println("Enter Passport Number: ");
        String passportNumber = scanner.nextLine();
        System.out.println("Enter date: ");
        String date = scanner.nextLine();

        try{
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
            BufferedReader waitingReader = new BufferedReader(new FileReader(waitingListFilePath));
            String line;
            boolean confirmed = false;

            while((line = reader.readLine()) != null){
                String [] details = new String [6];
                details =line.split(",");
                //Finding ticket under passenger name and passport number
                if(details[0].trim().equalsIgnoreCase(flightName.trim()) && details[2].trim().equalsIgnoreCase(date.trim()) && details [3].trim().equalsIgnoreCase(passengerName.trim()) && details [4].trim().equalsIgnoreCase(passportNumber.trim())){

                    System.out.println("Ticket found in flight "+details[0]+" under passenger "+details[3]+" passport number "+details[4]);
                    System.out.println("Ticket status: CONFIRMED");
                    System.out.println();
                    System.out.println("==============================");
                    System.out.println();
                    confirmed = true;
                    break;
                }

            }
            if(!confirmed){
                String waitingLine;
                while((waitingLine = waitingReader.readLine()) != null){
                    String[] waitingDetails = new String [4];
                    waitingDetails = waitingLine.split(",");

                    if(waitingDetails[0].trim().equalsIgnoreCase(flightName.trim()) && waitingDetails[1].trim().equalsIgnoreCase(date.trim()) && waitingDetails [2].trim().equalsIgnoreCase(passengerName.trim()) && waitingDetails [3].trim().equalsIgnoreCase(passportNumber.trim())){

                        System.out.println("Ticket found in flight "+waitingDetails[0]+" under passenger "+waitingDetails[2]+" passport number "+waitingDetails[3]);
                        System.out.println("Ticket status: WAITING");
                        System.out.println("==============================");
                        System.out.println();
                        confirmed = true;
                        break;
                    }
                }
                waitingReader.close();
                if(!confirmed){
                    System.out.println("No ticket found under this passenger. ");
                    System.out.println("==============================");
                    System.out.println();
                }
            }
        }catch(FileNotFoundException e){
            System.out.println("File Not Found");
        }catch(IOException e){
            System.out.println("IO Exception thrown");
        }
    }

    public static void cancelTicket() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Flight Name: ");
        String flightName = scanner.nextLine();
        System.out.println("Enter Flight Date: ");
        String flightDate = scanner.nextLine();
        System.out.print("Enter Passport Number: ");
        String passportNumber = scanner.nextLine();
        System.out.print("Enter Ticket Number: ");
        String ticketNumber = scanner.nextLine();

        Queue<String> waitingList = loadWaitingList(flightName, flightDate);
        List<String> updatedLines = new ArrayList<>();
        boolean rowUpdated = false;
        boolean passengerAdded = false;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] text = line.split(",");
                if (text.length >= 6 &&
                        text[4].trim().equalsIgnoreCase(passportNumber.trim()) &&
                        text[5].trim().equalsIgnoreCase(ticketNumber.trim())) {
                    rowUpdated = true;
                    if (waitingList.isEmpty()) {
                        updatedLines.add(text[0] + "," + text[1] + "," + text[2] + ",,," + text[5]);
                    } else {
                        String[] newPassenger = waitingList.poll().split(",");
                        updatedLines.add(text[0] + "," + text[1] + "," + text[2] + "," + newPassenger[2] + "," + newPassenger[3] + "," + text[5]);
                        passengerAdded = true;
                    }
                } else {
                    updatedLines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the file.");
        }

        if (!rowUpdated) {
            System.out.println("Booking information not found in the database!");
            System.out.println("==============================");
            System.out.println();
        } else {
            System.out.println("Ticket cancellation successful.");
            if (passengerAdded) {
                System.out.println("Passenger from the waiting list has been added to the confirmed list.");
                System.out.println("==============================");
                System.out.println();
            } else {
                System.out.println("Waiting list is empty.");
                System.out.println("==============================");
                System.out.println();
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(inputFilePath))) {
            for (String updatedLine : updatedLines) {
                bw.write(updatedLine);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to the file.");
        }

        saveWaitingList(waitingList); // Save remaining waiting list entries back to the file
    }

}
