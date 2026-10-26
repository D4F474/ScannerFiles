package GUI;

import Scanner.ScanFiles;
import Scanner.ScanItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainMenu extends JFrame{
    private JPanel mainPanel;
    private JButton scanButton;
    private JTable ScanData;
    private JButton sortButton;
    private JButton openButton;
    private JButton deleteButton;
    private JButton visualButton;
    private JButton browseButton;
    private JProgressBar progressBar;
    private ScanFiles sf;
    private File currentFolder = new File(System.getProperty("user.home"), "Downloads");
    private List<ScanItem> data;
    public MainMenu() {
        this.setTitle("Scanner.ScanFiles");
        this.setSize(800, 600);
        this.setContentPane(this.mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        scanButton.addActionListener(e -> startScan());
        sortButton.addActionListener(e -> openSort());
        browseButton.addActionListener(e -> browse());
        this.setVisible(true);
    }

    private void openSort() {
        SortType st = new SortType();
    }

    private void startScan() {
        scanButton.setEnabled(false);
        browseButton.setEnabled(false);

        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);

        // 1. Записваме началния момент (в наносекунди е най-точно)
        long startTime = System.nanoTime();

        // 2. Създаваме Swing Таймер, който се задейства на всеки 100 милисекунди (0.1 сек)
        // и обновява текста върху прогрес бара на живо!
        javax.swing.Timer liveTimer = new javax.swing.Timer(100, e -> {
            double elapsedSeconds = (System.nanoTime() - startTime) / 1_000_000_000.0;
            progressBar.setString(String.format("Сканиране... %.1f сек.", elapsedSeconds));
        });
        liveTimer.start(); // Стартираме часовника на екрана!

        // 3. Стартираме сканирането във фонов режим
        SwingWorker<List<ScanItem>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ScanItem> doInBackground() {
                sf = new ScanFiles();
                return sf.scanFolder(currentFolder, 8);
            }

            @Override
            protected void done() {
                // 4. Спираме живия таймер веднага щом сканирането приключи!
                liveTimer.stop();

                // Изчисляваме финалното време с висока точност (до хилядни от секундата)
                double totalTime = (System.nanoTime() - startTime) / 1_000_000_000.0;

                try {
                    data = get();
                    if (data != null) {
                        visualize();
                    }
                    // 5. Изписваме за колко точно време се е справила програмата
                    progressBar.setString(String.format("Готово за %.3f сек. (%d обекта)", totalTime, data != null ? data.size() : 0));
                } catch (Exception e) {
                    progressBar.setString("Грешка при сканиране!");
                    e.printStackTrace();
                } finally {
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(100);
                    scanButton.setEnabled(true);
                    browseButton.setEnabled(true);
                }
            }
        };

        worker.execute();
    }
    private void visualize(){
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel = new DefaultTableModel(new Object[]{"Име", "Големина", "Дата", "Папка", "Път"}, 0);
        ScanData.setModel(tableModel);

        for(ScanItem item : data){
            tableModel.addRow(new Object[]{
                    item.getFileName(),
                    formatSize(item.getSize()),
                    item.getDate(),
                    item.isFolder(),
                    item.getPath()
            });
        }
    }

    private String formatSize(long bytes) {
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        double value = bytes;
        int i = 0;
        while (value >= 1024 && i < units.length - 1) {
            value /= 1024;
            i++;
        }
        return String.format("%.1f %s", value, units[i]);
    }

    private void browse() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Избери папка за сканиране");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setCurrentDirectory(currentFolder);

        int result = chooser.showDialog(this, "Избери");
        if (result == JFileChooser.APPROVE_OPTION) {
            currentFolder = chooser.getSelectedFile();
            startScan();
        }
    }


}
