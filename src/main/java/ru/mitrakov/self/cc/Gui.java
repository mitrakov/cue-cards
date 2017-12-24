package ru.mitrakov.self.cc;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import java.util.*;

/**
 * Created by Tommy on 06.11.2016
 */
class Gui {
    private int i = 0;

    void show(List<String> keys, int delayMinutes) {
        assert !keys.isEmpty() && delayMinutes > 0;

        Timer timer = new Timer(true);
        Collections.shuffle(keys, new Random(System.nanoTime()));

        Display display = new Display();
        Shell shell = new Shell(display, SWT.NONE);
        Label label = new Label(shell, SWT.NONE);
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                shell.setVisible(false);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        display.syncExec(() -> {
                            label.setText(keys.get(i));
                            label.pack();
                            shell.pack();
                            shell.open();
                        });
                    }
                }, delayMinutes * 60 * 1000);
                if (++i == keys.size()) {
                    i = 0;
                    Collections.shuffle(keys, new Random(System.nanoTime()));
                }
            }
        });

        label.setText(keys.get(i));
        label.pack();
        shell.pack();
        shell.open();

        while (!shell.isDisposed())
            if (!display.readAndDispatch())
                display.sleep();
        display.dispose();
    }
}
