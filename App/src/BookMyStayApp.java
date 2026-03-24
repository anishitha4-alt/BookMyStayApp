import java.util.*;

// -------------------- CUSTOM EXCEPTION --------------------

class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// -------------------- RESERVATION --------------------

class Reservation {

    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

// -------------------- INVENTORY (THREAD SAFE) --------------------

class InventoryService {

    private HashMap<String, Integer> inventory = new HashMap<>();

    public InventoryService() {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 1);
    }

    // synchronized method → prevents race condition
    public synchronized boolean bookRoom(String roomType) {

        int available = inventory.getOrDefault(roomType, 0);

        if (available > 0) {
            inventory.put(roomType, available - 1);
            return true;
        }
        return false;
    }

    public synchronized void displayInventory() {
        System.out.println("\nFinal Inventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " : " + inventory.get(type));
        }
    }
}

// -------------------- BOOKING PROCESSOR (THREAD) --------------------

class BookingProcessor extends Thread {

    private Queue<Reservation> requestQueue;
    private InventoryService inventory;

    public BookingProcessor(Queue<Reservation> requestQueue,
                            InventoryService inventory,
                            String threadName) {
        super(threadName);
        this.requestQueue = requestQueue;
        this.inventory = inventory;
    }

    public void run() {

        while (true) {

            Reservation request;

            // 🔒 synchronized queue access (critical section)
            synchronized (requestQueue) {

                if (requestQueue.isEmpty()) {
                    break;
                }

                request = requestQueue.poll();
            }

            process(request);
        }
    }

    private void process(Reservation request) {

        String guest = request.getGuestName();
        String roomType = request.getRoomType();

        System.out.println(getName() + " processing " + guest);

        boolean success = inventory.bookRoom(roomType);

        if (success) {
            System.out.println("✅ Booking Confirmed for " + guest
                    + " (" + roomType + ")");
        } else {
            System.out.println("❌ Booking Failed for " + guest
                    + " (No rooms available)");
        }
    }
}

// -------------------- MAIN CLASS --------------------

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("=====================================");
        System.out.println("   Book My Stay v11.0");
        System.out.println("   Concurrent Booking Simulation");
        System.out.println("=====================================");

        // Shared queue
        Queue<Reservation> requestQueue = new LinkedList<>();

        requestQueue.add(new Reservation("Alice", "Single Room"));
        requestQueue.add(new Reservation("Bob", "Single Room"));
        requestQueue.add(new Reservation("Charlie", "Single Room")); // will fail
        requestQueue.add(new Reservation("David", "Double Room"));
        requestQueue.add(new Reservation("Eva", "Double Room"));
        requestQueue.add(new Reservation("Frank", "Double Room")); // may fail

        InventoryService inventory = new InventoryService();

        // Multiple threads (simulate users)
        BookingProcessor t1 = new BookingProcessor(requestQueue, inventory, "Thread-1");
        BookingProcessor t2 = new BookingProcessor(requestQueue, inventory, "Thread-2");
        BookingProcessor t3 = new BookingProcessor(requestQueue, inventory, "Thread-3");

        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        inventory.displayInventory();
    }
}