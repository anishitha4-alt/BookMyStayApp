import java.util.*;

// -------------------- EXISTING CLASSES --------------------

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
}

class InventoryService {

    private HashMap<String, Integer> inventory = new HashMap<>();

    public InventoryService() {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 1);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void decreaseRoom(String roomType) {
        int current = inventory.get(roomType);
        inventory.put(roomType, current - 1);
    }

    public void displayInventory() {
        System.out.println("\nCurrent Inventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " : " + inventory.get(type));
        }
    }
}

// -------------------- NEW: BOOKING HISTORY --------------------

class BookingHistory {

    private List<Reservation> history = new ArrayList<>();

    // store confirmed booking
    public void add(Reservation r) {
        history.add(r);
    }

    // retrieve all bookings
    public List<Reservation> getAll() {
        return history;
    }
}

// -------------------- UPDATED BOOKING SERVICE --------------------

class BookingService {

    private Queue<Reservation> requestQueue;
    private InventoryService inventory;
    private BookingHistory history;   // NEW

    private HashMap<String, Set<String>> allocatedRooms = new HashMap<>();

    public BookingService(Queue<Reservation> requestQueue,
                          InventoryService inventory,
                          BookingHistory history) {
        this.requestQueue = requestQueue;
        this.inventory = inventory;
        this.history = history;  // NEW
    }

    public void processBookings() {

        while (!requestQueue.isEmpty()) {

            Reservation request = requestQueue.poll();
            String roomType = request.getRoomType();

            System.out.println("\nProcessing request for " + request.getGuestName());

            if (inventory.getAvailability(roomType) > 0) {

                String roomId = roomType.replace(" ", "") + "-"
                        + UUID.randomUUID().toString().substring(0, 5);

                allocatedRooms.putIfAbsent(roomType, new HashSet<>());
                allocatedRooms.get(roomType).add(roomId);

                inventory.decreaseRoom(roomType);

                // ⭐ ADD TO HISTORY (IMPORTANT CHANGE)
                history.add(request);

                System.out.println("Reservation Confirmed for " + request.getGuestName());
                System.out.println("Allocated Room ID: " + roomId);

            } else {

                System.out.println("Reservation Failed for " + request.getGuestName()
                        + " (No rooms available)");
            }
        }
    }

    public void displayAllocations() {

        System.out.println("\nAllocated Rooms:");

        for (String type : allocatedRooms.keySet()) {
            System.out.println(type + " -> " + allocatedRooms.get(type));
        }
    }
}

// -------------------- NEW: REPORT SERVICE --------------------

class BookingReportService {

    private BookingHistory history;

    public BookingReportService(BookingHistory history) {
        this.history = history;
    }

    // Display all bookings
    public void showAllBookings() {
        System.out.println("\nBooking History:");

        for (Reservation r : history.getAll()) {
            System.out.println(r.getGuestName() + " -> " + r.getRoomType());
        }
    }

    // Summary report
    public void generateSummary() {
        Map<String, Integer> summary = new HashMap<>();

        for (Reservation r : history.getAll()) {
            String type = r.getRoomType();
            summary.put(type, summary.getOrDefault(type, 0) + 1);
        }

        System.out.println("\nBooking Summary Report:");
        for (String type : summary.keySet()) {
            System.out.println(type + " : " + summary.get(type));
        }
    }
}

// -------------------- MAIN CLASS --------------------

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("=====================================");
        System.out.println("       Book My Stay v8.0");
        System.out.println("   Booking History & Reporting");
        System.out.println("=====================================");

        Queue<Reservation> requestQueue = new LinkedList<>();

        requestQueue.add(new Reservation("Alice", "Single Room"));
        requestQueue.add(new Reservation("Bob", "Double Room"));
        requestQueue.add(new Reservation("Charlie", "Single Room"));
        requestQueue.add(new Reservation("David", "Suite Room"));
        requestQueue.add(new Reservation("Eva", "Suite Room"));

        InventoryService inventory = new InventoryService();
        BookingHistory history = new BookingHistory();

        BookingService bookingService =
                new BookingService(requestQueue, inventory, history);

        bookingService.processBookings();
        bookingService.displayAllocations();
        inventory.displayInventory();

        // REPORTING
        BookingReportService report = new BookingReportService(history);

        report.showAllBookings();
        report.generateSummary();
    }
}