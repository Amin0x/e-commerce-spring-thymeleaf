import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class StockNotificationService {

    @Autowired
    private InventoryRepository inventoryRepository;

    // Check stock levels every day
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkLowStock() {
        List<Inventory> inventories = inventoryRepository.findAll();
        for (Inventory inventory : inventories) {
            if (inventory.getQuantity() < 10) {
                // Notify that stock is low (you can send an email here)
                System.out.println("Stock is low for product: " + inventory.getProductId());
            }
        }
    }
}
