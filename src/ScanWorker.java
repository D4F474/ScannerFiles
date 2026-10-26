import Scanner.ScanItem;

import javax.swing.*;
import java.util.List;

public class ScanWorker extends SwingWorker<List<ScanItem>, ScanItem> {

    @Override
    protected List<ScanItem> doInBackground() throws Exception {
        return List.of();
    }
}
