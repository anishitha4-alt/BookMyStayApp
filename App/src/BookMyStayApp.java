import java.util.*;

/**
 * Book My Stay - Hotel Booking System
 *
 * Use Case 6: Reservation Confirmation & Room Allocation
 * Demonstrates how booking requests are processed safely
 * while preventing double-booking using Set and HashMap.
 *
 * @author Nishitha
 * @version 6.0
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


 class BookingService {

    private Queue<Reservation> requestQueue;
    private InventoryService inventory;

     private HashMap<String, Set<String>> allocatedRooms = new HashMap<>();

    public BookingService(Queue<Reservation> requestQueue, InventoryService inventory) {
        this.requestQueue = requestQueue;
        this.inventory = inventory;
    }

    public void processBookings() {

        while (!requestQueue.isEmpty()) {

            Reservation request = requestQueue.poll();
            String roomType = request.getRoomType();

            System.out.println("\nProcessing request for " + request.getGuestName());

            if (inventory.getAvailability(roomType) > 0) {

                 String roomId = roomType.replace(" ", "") + "-" + UUID.randomUUID().toString().substring(0,5);

                 allocatedRooms.putIfAbsent(roomType, new HashSet<>());
                allocatedRooms.get(roomType).add(roomId);

                 inventory.decreaseRoom(roomType);

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


 public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("=====================================");
        System.out.println("       Book My Stay v6.0");
        System.out.println("   Reservation Allocation System");
        System.out.println("=====================================");

        // Create booking request queue
        Queue<Reservation> requestQueue = new LinkedList<>();

        requestQueue.add(new Reservation("Alice", "Single Room"));
        requestQueue.add(new Reservation("Bob", "Double Room"));
        requestQueue.add(new Reservation("Charlie", "Single Room"));
        requestQueue.add(new Reservation("David", "Suite Room"));
        requestQueue.add(new Reservation("Eva", "Suite Room")); // will fail

         InventoryService inventory = new InventoryService();

         BookingService bookingService = new BookingService(requestQueue, inventory);

         bookingService.processBookings();

         bookingService.displayAllocations();
        inventory.displayInventory();
    }
}