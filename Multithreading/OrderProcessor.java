//Orderprocessor is a very basic example to understand how threading works in java.

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderProcessor implements Runnable {
    private final String orderId;
    private static int inventory = 5;
    private static ConcurrentHashMap<String, ORDERSTATUS> orderStatus = new ConcurrentHashMap<>();

    private enum ORDERSTATUS {
        PROCESSING("Processing"),
        PROCESSED("Processed"),
        OUT_OF_STOCK("Out of stock");
    
        private final String status;
    
        ORDERSTATUS(String status) {
            this.status = status;
        }
    
        public String getStatus() {
            return status;
        }
    }

    public OrderProcessor(String id) {
        this.orderId = id;
    }

    /* If we want to use synchronized processing, this will be the way
    @Override
    public void run() {
       processOrder();
    }

    private synchronized void processOrder() {
        if (inventory > 0) {
            System.out.println("Processing order: " + orderId + " - " + Thread.currentThread().getName());
            inventory--;
            System.out.println("Order processed: " + orderId + "Remaining inventory - " + inventory);
        } else {
            System.out.println("All inventory exhausted");
        }
    }
    */

    //This approach is used if we want to do if we need a multiple threads to access a common map byusing ConcurrentHashMap collection in java.
    @Override 
    public void run() {
        if (inventory > 0) {
            orderStatus.put(orderId, ORDERSTATUS.PROCESSING);
            System.out.println(orderId + " status:" + orderStatus.get(orderId));

            try {
                Thread.sleep(1000);
                inventory--;
                orderStatus.put(orderId, ORDERSTATUS.PROCESSED);
                System.out.println(orderId + " status: " + orderStatus.get(orderId));
            } catch (InterruptedException e) {
                System.out.println("Processing interrupted for " + orderId);
            }
        } else {
            orderStatus.put(orderId, ORDERSTATUS.OUT_OF_STOCK);
            System.out.println(orderId + " status: " + orderStatus.get(orderId));
        }
    }


    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (int i = 0; i <= 10 ; i++) {
            executor.submit(new OrderProcessor("Order" + i));
        }

        executor.shutdown();
    }
}
