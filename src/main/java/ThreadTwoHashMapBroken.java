import java.util.HashMap;
import java.util.Set;

public class ThreadTwoHashMapBroken extends Thread {
    HashMap<String, Thread> threadMap;

    public ThreadTwoHashMapBroken(String name) {
        super(name);
        this.threadMap = new HashMap<>();
    }

    @Override
    public void run() {
        System.out.println("ThreadTwoHashMapB - START "+Thread.currentThread().getName());
        try {
            Thread.sleep(1000);
            //Get database connection, delete unused data from DB
            doDBProcessing();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("ThreadTwoHashMapB - END "+Thread.currentThread().getName());
    }

    private void doDBProcessing() throws InterruptedException {
        Thread.sleep(5000);
    }


    // Run this
    public static void main(String[] args){
        ThreadTwoHashMapBroken tm = new ThreadTwoHashMapBroken(""+10);

        // What's wrong with this idea??...
        new Thread("Run of " + 6){
            public void run(){
                tm.runMapOfSize(6);
            }
        }.start(); //it looks like the ones that get started before an exception continue to run
                    // this makes sense since they are their own threads
        new Thread("Run of " + 8){
            public void run(){
                tm.runMapOfSize(8);
            }
        }.start();
    //the issue may be 6 or 8 trying to access the hashmap while the other has it open, 6 seems to fail a lot more than 8
    //when i remove one of the threads it stops breaking
    //it is odd to me that this is actually a hashmap of threads rather than there being a hash map of threads in a method
    //i get concurrent modification expeptions and illegal state exceptions
    }

    private void runMapOfSize(int size) {
        System.out.println("Constructing HashMap of Size "+size);
        Integer threadCount = size;

        for (int i = 0; i < threadCount; i++) {
            this.threadMap.put("T"+ i, new ThreadTwoHashMapBroken("T"+ i));
        }
        System.out.println("Starting Threads in HashMap");
        Set<String> names = this.threadMap.keySet();
        for (String name : names) {
            this.threadMap.get(name).start();
        }
        System.out.println("Thread HashMap, all have been started");
    }
}
