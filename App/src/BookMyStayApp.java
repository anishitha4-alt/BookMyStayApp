import java.io.*;
import java.util.*;

// -------------------- RESERVATION --------------------

class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

// -------------------- INVENTORY --------------------

class InventoryService implements Serializable {

    private static final long serialVersionUID = 1L;

    private HashMap<String, Integer> inventory = new HashMap<>();

    public InventoryService() {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 1);
    }

    public boolean bookRoom(String roomType) {
        int available = inventory.getOrDefault(roomType, 0);
        if (available > 0) {
            inventory.put(roomType, available - 1);
            return true;
        }
        return false;
    }

    public void displayInventory() {
        System.out.println("\nInventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " : " + inventory.get(type));
        }
    }
}

// -------------------- BOOKING HISTORY --------------------

class BookingHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Reservation> history = new ArrayList<>();

    public void add(Reservation r) {
        history.add(r);
    }

    public List<Reservation> getAll() {
        return history;
    }

    public void display() {
        System.out.println("\nBooking History:");
        for (Reservation r : history) {
            System.out.println(r.getGuestName() + " -> " + r.getRoomType());
        }
    }
}

// -------------------- PERSISTENCE SERVICE --------------------

class PersistenceService {

    private static final String FILE_NAME = "bookmyStay.dat";

    // Save state
    public static void save(InventoryService inventory, BookingHistory history) {

        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            oos.writeObject(inventory);
            oos.writeObject(history);

            System.out.println("\n💾 System state saved successfully!");

        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    // Load state
    public static Object[] load() {

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_NAME))) {

            InventoryService inventory = (InventoryService) ois.readObject();
            BookingHistory history = (BookingHistory) ois.readObject();

            System.out.println("\n🔄 System state restored successfully!");

            return new Object[]{inventory, history};

        } catch (Exception e) {
            System.out.println("\n⚠ No previous data found. Starting fresh.");
            return null;
        }
    }
}

// -------------------- MAIN CLASS --------------------

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("=====================================");
        System.out.println("   Book My Stay v12.0");
        System.out.println("   Persistence & Recovery");
        System.out.println("=====================================");

        InventoryService inventory;
        BookingHistory history;

        // 🔄 LOAD DATA
        Object[] data = PersistenceService.load();

        if (data != null) {
            inventory = (InventoryService) data[0];
            history = (BookingHistory) data[1];
        } else {
            inventory = new InventoryService();
            history = new BookingHistory();
        }

        // Simulate bookings
        Reservation r1 = new Reservation("Alice", "Single Room");
        Reservation r2 = new Reservation("Bob", "Double Room");

        if (inventory.bookRoom(r1.getRoomType())) {
            history.add(r1);
        }

        if (inventory.bookRoom(r2.getRoomType())) {
            history.add(r2);
        }

        // Display current state
        inventory.displayInventory();
        history.display();

        // 💾 SAVE DATA
        PersistenceService.save(inventory, history);
    }
}