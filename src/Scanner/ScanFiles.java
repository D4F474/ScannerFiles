package Scanner;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScanFiles {
    private ThreadPoolExecutor pool;

    public ScanFiles(){

    }

    private long folderSize(File folder){
        long res = 0;
        File[] childrens = folder.listFiles();
        if(childrens == null){
            return 0;
        }
        for(File children : childrens){
                if(!children.isDirectory()){
                    res += children.length();
                }else{
                    res += folderSize(children);
                }
        }
        return res;
    }

    ScanItem scanItems(File file){
        ScanItem item = new ScanItem();
        item.setFileName(file.getName());
        item.setPath(file.getPath());
        LocalDate date = Instant.ofEpochMilli(file.lastModified())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        item.setDate(date.atStartOfDay());

        if(file.isDirectory()){
            item.setFolder(true);
            item.setSize(folderSize(file));
        }else{
            item.setFolder(false);
            item.setSize(file.length());
        }
        return item;
    }

    public List<ScanItem> scanFolder(File file, int threadCount){
        File[] files = file.listFiles();
        if(files == null){
            return null;
        }
        pool = new ThreadPoolExecutor(
                threadCount,  // Core pool size
                threadCount,  // Maximum pool size
                1,  // Keep alive time (seconds)
                TimeUnit.SECONDS,  // Time unit for keep alive time
                new LinkedBlockingQueue<>()  // Task queue
        );

        List<Future<ScanItem>> numbers = new ArrayList<>();
        List<ScanItem> result = new ArrayList<>();

        try{
            for(File child: files){
                Future<ScanItem> number = pool.submit(()-> scanItems(child));

                numbers.add(number);
            }

            for(Future<ScanItem> num : numbers){
                result.add(num.get());
            }

        }catch (Exception e){

        }finally {
            pool.shutdown();
        }

        return result;
    }


}
