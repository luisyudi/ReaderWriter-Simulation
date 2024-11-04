import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantReadWriteLock; 

public class Base {
    private String words[] = new String[36242];
    private final ReentrantReadWriteLock rwl;
    private File file;
    private FileWriter fw;
    public int activeReaders;

    public Base(ReentrantReadWriteLock rwl, String fileName){
        //read file
        this.rwl = rwl;
        this.file = new File(fileName);
        try {
            this.fw = new FileWriter(file, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            File file = new File("bd.txt");
            Scanner scanner = new Scanner(file);
            int i = 0;
            while (scanner.hasNextLine()) {
                this.words[i] = scanner.nextLine();
                i++;
            }
            scanner.close();
        } catch (Exception e) {
            System.out.println("Arquivo nao encontrado");
            e.printStackTrace();
        }
    }

    public synchronized void modify(){
        rwl.writeLock().lock();
        try{
            for (int i = 0; i < 100; i++) {
                int randomNum = ThreadLocalRandom.current().nextInt(0, 36242);
                this.words[randomNum] = "MODIFICADO";
            }
        }finally{
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            rwl.writeLock().unlock();
        }
    }

    @SuppressWarnings("finally")
    public String[] readPriority(){
        rwl.readLock().lock();
        String list[] = new String[100];
        try{
            for (int i = 0; i < 100; i++) {
                int randomNum = ThreadLocalRandom.current().nextInt(0, 36242);
                list[i] = words[randomNum];
            }
        }finally{
            try {
                Thread.sleep(1);
                rwl.readLock().unlock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return list;
        }
    }

    @SuppressWarnings("finally")
    public String[] read(){
        rwl.writeLock().lock();
        String list[] = new String[100];
        try{
            for (int i = 0; i < 100; i++) {
                int randomNum = ThreadLocalRandom.current().nextInt(0, 36242);
                list[i] = words[randomNum];
            }
        }finally{
            try {
                Thread.sleep(1);
                rwl.writeLock().unlock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return list;
        }
    }

    public String[] getWords(){
        return this.words;
    }

    public void setWord(String word,int pos){
        this.words[pos] = word;
    }

    public void write(String s){
        try {
            synchronized (fw) {
                fw.append(s);
                fw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
