import java.util.*;

// -------------------- CUSTOM EXCEPTION --------------------

class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

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

    // VALIDATION added
    public void decreaseRoom(String roomType) throws InvalidBookingException {
        int current = inventory.getOrDefault(roomType, -1);

        if (current <= 0) {
            throw new InvalidBookingException("Cannot allocate room. No availability for " + roomType);
        }

        inventory.put(roomType, current - 1);
    }

    public boolean isValidRoomType(String roomType) {
        return inventory.containsKey(roomType);
    }

    public void displayInventory() {
        System.out.println("\nCurrent Inventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " : " + inventory.get(type));
        }
    }
}

// -------------------- BOOKING HISTORY --------------------

class BookingHistory {

    private List<Reservation> history = new ArrayList<>();

    public void add(Reservation r) {
        history.add(r);
    }

    public List<Reservation> getAll() {
        return history;
    }
}

// -------------------- UPDATED BOOKING SERVICE --------------------

class BookingService {

    private Queue<Reservation> requestQueue;
    private InventoryService inventory;
    private BookingHistory history;

    private HashMap<String, Set<String>> allocatedRooms = new HashMap<>();

    public BookingService(Queue<Reservation> requestQueue,
                          InventoryService inventory,
                          BookingHistory history) {
        this.requestQueue = requestQueue;
        this.inventory = inventory;
        this.history = history;
    }

    public void processBookings() {

        while (!requestQueue.isEmpty()) {

            Reservation request = requestQueue.poll();
            String roomType = request.getRoomType();

            System.out.println("\nProcessing request for " + request.getGuestName());

            try {

                // ✅ VALIDATION: room type check
                if (!inventory.isValidRoomType(roomType)) {
                    throw new InvalidBookingException("Invalid Room Type: " + roomType);
                }

                // ✅ VALIDATION: availability check
                if (inventory.getAvailability(roomType) <= 0) {
                    throw new InvalidBookingException("No rooms available for " + roomType);
                }

                String roomId = roomType.replace(" ", "") + "-"
                        + UUID.randomUUID().toString().substring(0, 5);

                allocatedRooms.putIfAbsent(roomType, new HashSet<>());
                allocatedRooms.get(roomType).add(roomId);

                // may throw exception
                inventory.decreaseRoom(roomType);

                history.add(request);

                System.out.println("Reservation Confirmed for " + request.getGuestName());
                System.out.println("Allocated Room ID: " + roomId);

            } catch (InvalidBookingException e) {

                // ✅ GRACEFUL ERROR HANDLING
                System.out.println("Reservation Failed for " + request.getGuestName());
                System.out.println("Reason: " + e.getMessage());
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

// -------------------- REPORT SERVICE --------------------

class BookingReportService {

    private BookingHistory history;

    public BookingReportService(BookingHistory history) {
        this.history = history;
    }

    public void showAllBookings() {
        System.out.println("\nBooking History:");

        for (Reservation r : history.getAll()) {
            System.out.println(r.getGuestName() + " -> " + r.getRoomType());
        }
    }
}

// -------------------- MAIN CLASS --------------------

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("=====================================");
        System.out.println("       Book My Stay v9.0");
        System.out.println("   Error Handling & Validation");
        System.out.println("=====================================");

        Queue<Reservation> requestQueue = new LinkedList<>();

        requestQueue.add(new Reservation("Alice", "Single Room"));
        requestQueue.add(new Reservation("Bob", "Double Room"));
        requestQueue.add(new Reservation("Charlie", "Invalid Room")); // ❌ invalid
        requestQueue.add(new Reservation("David", "Suite Room"));
        requestQueue.add(new Reservation("Eva", "Suite Room")); // ❌ no availability

        InventoryService inventory = new InventoryService();
        BookingHistory history = new BookingHistory();

        BookingService bookingService =
                new BookingService(requestQueue, inventory, history);

        bookingService.processBookings();
        bookingService.displayAllocations();
        inventory.displayInventory();

        BookingReportService report = new BookingReportService(history);
        report.showAllBookings();
    }
}