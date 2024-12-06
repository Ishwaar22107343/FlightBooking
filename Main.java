import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

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

            if(input.equals("1")){
                FlightTicketBooking.searchFlight();
            }else if(input.equals("2")){
                continue;
            }else if(input.equals("3")){
                FlightTicketBooking.editTicket();
                continue;
            }else if(input.equals("4")){
                continue;
            }else if(input.equals("5")){
                continue;
            }else if(input.equals("6")){
                System.out.println("Thank you for choosing Cholaz Flight Booking Services");
                System.out.println();
                System.out.println("==============================");
                break;
            }else{
                System.out.println("Please enter a valid input");
                continue;
            }

        }while(true);
    }
}

class FlightTicketBooking {
    //creating queue waiting list 
    private static Queue<String> waitingList = new LinkedList<>();
    
    public static void waitingEnqueue(String passengerDetails) {
        waitingList.add(passengerDetails);
        System.out.println("Passenger added to the waiting list");
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
    public static void searchFlight(){
         String inputFilePath = "C://Users//ISHWAAR//Documents//SEM3//DS//FlightBooking.csv";
         SimpleDateFormat dateformat = new SimpleDateFormat("d/M/yyyy");
         Scanner scanner = new Scanner(System.in):
         System.out.println("Enter start date (d/M/yyyy): ");
         String startDate = scanner.nextLine();
         System.out.println("Enter end date (d/M/yyyy): ");
         String endDate = scanner.nextLine();
         boolean flightFound = false;
         try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
            String line;
            System.out.println("Flights available between " + startDate + " and " + endDate + ":");
            System.out.println("Flight\tCapacity\tDate");
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                String flightDate = details[2].trim();
                Date flightDateObj = dateFormat.parse(flightDate);
                Date startDateObj = dateFormat.parse(startDate);
                Date endDateObj = dateFormat.parse(endDate);

                if (!flightDateObj.before(startDateObj) && !flightDateObj.after(endDateObj)) {
                    System.out.println(details[0] + "\t" + details[1] + "\t\t" + flightDate);
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

        }

    //method to edit ticket information
    public static void editTicket(){

        String inputFilePath = "C://Users//ISHWAAR//Documents//SEM3//DS//FlightBooking.csv";
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
    public  static void viewTicketStatus(String passengerName, String passportNumber, String flightName, String ticketNumber){
        String inputFilePath = "C://Users//ISHWAAR//Documents//SEM3//DS//testing.csv";
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
            System.err.println("Error writing to the file: ");
        }
    }
}
