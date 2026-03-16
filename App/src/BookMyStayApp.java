import java.util.LinkedList;
import java.util.Queue;

/**
 * Book My Stay - Hotel Booking System
 *
 * Use Case 5: Booking Request Queue (First-Come-First-Served)
 * Demonstrates how booking requests are collected using a Queue
 * to preserve arrival order before allocation.
 *
 * @author Nishitha
 * @version 5.0
 */

 class Reservation {

    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void displayRequest() {
        System.out.println("Guest: " + guestName + " requested " + roomType);
    }
}


 class BookingRequestQueue {

    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() {
        requestQueue = new LinkedList<>();
    }

     public void addRequest(Reservation reservation) {

        requestQueue.offer(reservation);
        System.out.println("Request added to queue: "
                + reservation.getGuestName() + " (" + reservation.getRoomType() + ")");
    }

     public void displayRequests() {

        System.out.println("\nCurrent Booking Request Queue:");

        for (Reservation r : requestQueue) {
            r.displayRequest();
        }
    }
}


 public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("===================================");
        System.out.println("        Book My Stay v5.0");
        System.out.println("   Booking Request Queue System");
        System.out.println("===================================");

         BookingRequestQueue bookingQueue = new BookingRequestQueue();

         Reservation r1 = new Reservation("Alice", "Single Room");
        Reservation r2 = new Reservation("Bob", "Double Room");
        Reservation r3 = new Reservation("Charlie", "Suite Room");

        bookingQueue.addRequest(r1);
        bookingQueue.addRequest(r2);
        bookingQueue.addRequest(r3);

         bookingQueue.displayRequests();

        System.out.println("\nRequests are waiting for allocation processing.");
    }
}