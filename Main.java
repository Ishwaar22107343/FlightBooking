import java.util.ArrayList;
public class Main {
    public static void main(String[] args) {

    }
}

class Queue <E> {
    private int head;
    private int tail;
    private E passenger[];
    private int maxSize;
    private ArrayList <E> waiting;
    private int waitHead;
    private int waitTail;

    public Queue(int maxSize){
        this.maxSize = maxSize;
        this.head = 0;
        this.tail = 0;
        this.passenger = (E[]) new Object[maxSize];
        this.waiting = new ArrayList<>();
        this.waitHead = 0;
        this.waitTail =0;
    }

    public boolean isEmpty(){
        return tail == 0;
    }

    public boolean isFull(){
        return tail == maxSize;
    }

    public void enqueue(E passengerName){
        if(!isFull()){
            passenger[tail] = passengerName;
            System.out.println("Ticket booked for passenger "+passengerName);
            tail++;
        }else{
            System.out.println("Flight is full...");
            System.out.println("You'll be added to the waiting list...");
            waitingEnqueue(passengerName);
        }
    }

    public E dequeue(){
        if (isEmpty()) {
            System.out.println("Flight is empty");
            return null;
        }else if(!isEmpty() && !isFull()){
            E temp = passenger[head];
            for (int i = 0; i < tail; i++) {
                passenger[i] = passenger[i+1];
            }
            passenger[tail] = null;
            System.out.println("Cancelling ticket for passenger "+temp);
            tail--;
            return temp;
        }else{
            E temp = passenger[head];
            for (int i = 0; i < tail; i++) {
                passenger[i] = passenger[i+1];
            }
            passenger[tail] = null;
            System.out.println("Cancelling ticket for "+temp);
            tail--;
            waitingDequeue();
            return temp;
        }
    }

    public void display() {
        if (!isEmpty()) {
            System.out.println("\nThere are " + tail + " passengers in the flight...\nDisplaying passsenger details");
            System.out.println();
            for (int i = 0; i < tail; i++) {
                System.out.print((i+1) + ". ");
                System.out.print(passenger[i]);
                System.out.println();
            }
            System.out.println();
        } else {
            System.out.println("Flight is empty");
        }
    }

    //method to add passengers to waiting list if flight is full
    public void waitingEnqueue(E s){
        waiting.add(s);
        waitTail++;
    }
    //method that adds passenger from waiting list into the flight once vacancy available
    public E waitingDequeue(){
        E temp = waiting.get(waitHead);
        for (int i = 0; i < waitTail; i++) {
            waiting.set(i, waiting.get(i+1));
        }
        waiting.remove(waitTail);
        waitTail--;
        System.out.println("Ticket available, Ticket booked for "+temp);
        enqueue(temp);
        return temp;
    }
    System.out.println("idk");

}
