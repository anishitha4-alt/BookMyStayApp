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

    public void decreaseRoom(String roomType) throws InvalidBookingException {
        int current = inventory.getOrDefault(roomType, -1);

        if (current <= 0) {
            throw new InvalidBookingException("No availability for " + roomType);
        }

        inventory.put(roomType, current - 1);
    }

    // ⭐ NEW: rollback support
    public void increaseRoom(String roomType) {
        inventory.put(roomType, inventory.getOrDefault(roomType, 0) + 1);
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

    public boolean remove(String guestName) {
        return history.removeIf(r -> r.getGuestName().equals(guestName));
    }

    public List<Reservation> getAll() {
        return history;
    }
}

// -------------------- BOOKING SERVICE --------------------

class BookingService {

    private Queue<Reservation> requestQueue;
    private InventoryService inventory;
    private BookingHistory history;

    private HashMap<String, String> guestRoomMap = new HashMap<>(); // guest -> roomId
    private HashMap<String, String> guestRoomType = new HashMap<>(); // guest -> type

    private Stack<String> rollbackStack = new Stack<>(); // ⭐ LIFO

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
            String guest = request.getGuestName();
            String roomType = request.getRoomType();

            System.out.println("\nProcessing request for " + guest);

            try {

                if (!inventory.isValidRoomType(roomType)) {
                    throw new InvalidBookingException("Invalid Room Type");
                }

                if (inventory.getAvailability(roomType) <= 0) {
                    throw new InvalidBookingException("No rooms available");
                }

                String roomId = roomType.replace(" ", "") + "-"
                        + UUID.randomUUID().toString().substring(0, 5);

                inventory.decreaseRoom(roomType);

                history.add(request);

                // ⭐ track allocation
                guestRoomMap.put(guest, roomId);
                guestRoomType.put(guest, roomType);
                rollbackStack.push(roomId);

                System.out.println("Reservation Confirmed for " + guest);
                System.out.println("Room ID: " + roomId);

            } catch (InvalidBookingException e) {
                System.out.println("Reservation Failed for " + guest);
                System.out.println("Reason: " + e.getMessage());
            }
        }
    }

    // ⭐ NEW: Cancellation
    public void cancelBooking(String guestName) {

        System.out.println("\nCancellation request for " + guestName);

        if (!guestRoomMap.containsKey(guestName)) {
            System.out.println("Cancellation Failed: No such booking found");
            return;
        }

        String roomId = guestRoomMap.get(guestName);
        String roomType = guestRoomType.get(guestName);

        // LIFO rollback
        if (!rollbackStack.isEmpty() && rollbackStack.peek().equals(roomId)) {
            rollbackStack.pop();
        }

        // restore inventory
        inventory.increaseRoom(roomType);

        // remove from records
        guestRoomMap.remove(guestName);
        guestRoomType.remove(guestName);
        history.remove(guestName);

        System.out.println("Booking cancelled for " + guestName);
        System.out.println("Released Room ID: " + roomId);
    }
}

// -------------------- MAIN CLASS --------------------

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("=====================================");
        System.out.println("       Book My Stay v10.0");
        System.out.println("   Cancellation & Rollback");
        System.out.println("=====================================");

        Queue<Reservation> requestQueue = new LinkedList<>();

        requestQueue.add(new Reservation("Alice", "Single Room"));
        requestQueue.add(new Reservation("Bob", "Double Room"));
        requestQueue.add(new Reservation("Charlie", "Single Room"));

        InventoryService inventory = new InventoryService();
        BookingHistory history = new BookingHistory();

        BookingService bookingService =
                new BookingService(requestQueue, inventory, history);

        bookingService.processBookings();

        inventory.displayInventory();

        // ⭐ CANCEL BOOKINGS
        bookingService.cancelBooking("Charlie");
        bookingService.cancelBooking("Unknown"); // invalid

        inventory.displayInventory();
    }
}