import java.util.HashMap;
import java.util.Map;

/**
 * Book My Stay - Hotel Booking System
 *
 * Use Case 3: Centralized Room Inventory Management
 * Demonstrates how HashMap can be used to maintain a centralized
 * inventory for room availability.
 *
 * @author Nishitha
 * @version 3.1
 */


 class RoomInventory {

     private HashMap<String, Integer> inventory;

     public RoomInventory() {

        inventory = new HashMap<>();

        inventory.put("Single Room", 10);
        inventory.put("Double Room", 7);
        inventory.put("Suite Room", 3);
    }

     public int getAvailability(String roomType) {

        return inventory.getOrDefault(roomType, 0);
    }

     public void updateAvailability(String roomType, int change) {

        int current = inventory.getOrDefault(roomType, 0);

        inventory.put(roomType, current + change);
    }

     public void displayInventory() {

        System.out.println("\nCurrent Room Inventory:");

        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {

            System.out.println(entry.getKey() + " : " + entry.getValue() + " rooms available");
        }
    }
}


// Main Application Class
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("=====================================");
        System.out.println("        Book My Stay - v3.1");
        System.out.println("  Centralized Room Inventory System");
        System.out.println("=====================================");

         RoomInventory inventory = new RoomInventory();

         inventory.displayInventory();

         System.out.println("\nBooking 1 Single Room...");
        inventory.updateAvailability("Single Room", -1);

         inventory.displayInventory();

         System.out.println("\nCancelling 1 Double Room...");
        inventory.updateAvailability("Double Room", +1);

         inventory.displayInventory();
    }
}