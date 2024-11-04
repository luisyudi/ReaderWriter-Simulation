import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

public class Prioridade {
    public static void main(String[] args) {
        Thread lists[] = new Thread[100];
        ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
        Base base = new Base(rwl, "logComPrioridade.txt");

        Runnable writer = () ->{
            base.modify();
        };

        Runnable reader = () ->{
            String read[] = new String[100];
            rwl.isWriteLocked();
            read = base.readPriority();
        };

        String backup[] = new String[36242];
        backup = base.getWords();
        
        for (int i = 0; i <= 100; i++) {
            int numReader = i;
            int times = 0;
            float totalTimeSpent = 0;
            while (times != 50) {
                for (int j = 0; j < numReader; j++) {
                    lists[j] = new Thread(reader);
                }
                for (int j = numReader; j < 100; j++) {
                    lists[j] = new Thread(writer);
                }

                List<Thread> threads = Arrays.asList(lists);
                Collections.shuffle(threads);

                long startTime = System.currentTimeMillis();
                
                for (Thread thread : threads) {
                    thread.start();
                }

                for (Thread thread : threads) {
                    if (thread.isAlive()) {
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            System.out.println(e);
                        }
                    }
                }

                float endTime = ((float)(System.currentTimeMillis() - startTime)) / 1000;

                totalTimeSpent = totalTimeSpent + endTime;

                //Restores the original base (opcional)
                for (int j = 0; j < backup.length; j++) {
                    base.setWord(backup[j], j);
                }
                
                times++;
            }
            
            String log = "Tempo médio com " + numReader + " Leitores e " +(100-numReader)+ " Escritores: " + totalTimeSpent / 50 + "\n";
            base.write(log);
            
        }
        System.out.println("fim");
    }
}