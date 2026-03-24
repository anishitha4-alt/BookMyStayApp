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

                String roomId = roomType.replace(" ", "") + "-" +
                        UUID.randomUUID().toString().substring(0,5);

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

// -------------------- NEW USE CASE 7 --------------------

// Add-On Service class
class AddOnService {
    private String name;
    private double cost;

    public AddOnService(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() { return name; }
    public double getCost() { return cost; }
}

// Manager class (Map + List)
class AddOnServiceManager {

    private Map<String, List<AddOnService>> serviceMap = new HashMap<>();

    // Add service to reservation
    public void addService(String reservationId, AddOnService service) {
        serviceMap
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);
    }

    // Display services
    public void displayServices(String reservationId) {
        List<AddOnService> list = serviceMap.getOrDefault(reservationId, new ArrayList<>());

        if (list.isEmpty()) {
            System.out.println("No add-on services for " + reservationId);
            return;
        }

        System.out.println("\nServices for " + reservationId + ":");
        for (AddOnService s : list) {
            System.out.println("- " + s.getName() + " : ₹" + s.getCost());
        }
    }

    // Calculate cost
    public double getTotalCost(String reservationId) {
        double total = 0;
        for (AddOnService s : serviceMap.getOrDefault(reservationId, new ArrayList<>())) {
            total += s.getCost();
        }
        return total;
    }
}

// -------------------- MAIN CLASS --------------------

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("=====================================");
        System.out.println("       Book My Stay v7.0");
        System.out.println("   Add-On Service Enabled System");
        System.out.println("=====================================");

        Queue<Reservation> requestQueue = new LinkedList<>();

        requestQueue.add(new Reservation("Alice", "Single Room"));
        requestQueue.add(new Reservation("Bob", "Double Room"));
        requestQueue.add(new Reservation("Charlie", "Single Room"));

        InventoryService inventory = new InventoryService();
        BookingService bookingService = new BookingService(requestQueue, inventory);

        bookingService.processBookings();
        bookingService.displayAllocations();
        inventory.displayInventory();

        // -------- ADD-ON SERVICE PART --------

        AddOnServiceManager manager = new AddOnServiceManager();

        // Add services (mapped using guestName as reservation ID)
        manager.addService("Alice", new AddOnService("Breakfast", 500));
        manager.addService("Alice", new AddOnService("Spa", 1200));
        manager.addService("Bob", new AddOnService("Airport Pickup", 800));

        // Display services
        manager.displayServices("Alice");
        manager.displayServices("Bob");

        // Show total cost
        System.out.println("\nTotal Add-On Cost for Alice: ₹" +
                manager.getTotalCost("Alice"));
    }
}