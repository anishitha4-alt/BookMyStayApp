import java.util.*;

/**
 * Book My Stay - Hotel Booking System
 *
 * Use Case 4: Room Search & Availability Check
 * Demonstrates read-only search functionality where guests can
 * view available rooms without modifying inventory state.
 *
 * @author Nishitha
 * @version 4.0
 */


abstract class Room {

    protected String roomType;
    protected int beds;
    protected int size;
    protected double price;

    public Room(String roomType, int beds, int size, double price) {
        this.roomType = roomType;
        this.beds = beds;
        this.size = size;
        this.price = price;
    }

    public void displayDetails() {
        System.out.println("Room Type : " + roomType);
        System.out.println("Beds      : " + beds);
        System.out.println("Size      : " + size + " sq.ft");
        System.out.println("Price     : ₹" + price);
    }

    public String getRoomType() {
        return roomType;
    }
}

 class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 200, 2500);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 350, 4000);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 3, 600, 7500);
    }
}



class RoomInventory {

    private HashMap<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 0); // example unavailable room
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }
}



class RoomSearchService {

    public static void searchAvailableRooms(RoomInventory inventory, List<Room> rooms) {

        System.out.println("\nAvailable Rooms:\n");

        for (Room room : rooms) {

            int available = inventory.getAvailability(room.getRoomType());

             if (available > 0) {

                room.displayDetails();
                System.out.println("Available Rooms: " + available);
                System.out.println("-----------------------------");
            }
        }
    }
}



public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("        Book My Stay v4.0");
        System.out.println("      Room Search Service");
        System.out.println("=================================");

         RoomInventory inventory = new RoomInventory();

         List<Room> rooms = new ArrayList<>();
        rooms.add(new SingleRoom());
        rooms.add(new DoubleRoom());
        rooms.add(new SuiteRoom());

         RoomSearchService.searchAvailableRooms(inventory, rooms);

        System.out.println("\nSearch completed successfully.");
    }
}