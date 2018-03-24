package com.fasolutions.stock;

import com.fasolutions.stock.callback.CallBack;
import com.fasolutions.stock.model.StockElement;
import com.fasolutions.stock.service.StockService;
import com.vaadin.annotations.Theme;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import de.steinwedel.vaadin.MessageBox;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringUI
@Theme("valo")
public class StockUI extends UI {

    private List<StockElement> stockElements = null;
    private VerticalLayout layout;
    @Autowired
    StockService stockService;
    @Autowired
    CallBack callBack;

    ProgressBar bar = new ProgressBar(0.0f);
    Button downLoadButton;
    final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();


    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setupLayout();
        //addHeader();
        //addForm();
        //addTodoList();
        //addActionButtons();
        setProgressBar();
/*

        System.out.println("testing");
       String str =  stockService.getStockContent();
       if(str != null && !str.isEmpty())
       {
           List<StockElement> stockElements = stockService.parseStockContents(str);
           for(StockElement element : stockElements)
           {
               System.out.println(element.toString());
               System.out.println();
           }
       }

*/
    }

    private void setupLayout() {
        layout = new VerticalLayout();
        layout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        setContent(layout);
    }

    private void addHeader() {
        Label header = new Label("TODO");
        header.addStyleName(ValoTheme.LABEL_H1);
        layout.addComponent(header);
    }

    private void addForm() {
        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setWidth("80%");

        TextField taskField = new TextField();
        taskField.focus();
        Button addButton = new Button("");

        formLayout.addComponentsAndExpand(taskField);
        formLayout.addComponent(addButton);
        layout.addComponent(formLayout);

        addButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        addButton.setIcon(VaadinIcons.PLUS);

        addButton.addClickListener(click -> {
            //todoList.addTodo(new Todo(taskField.getValue()));
            taskField.setValue("");
            taskField.focus();
        });
        addButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
    }

    private void addTodoList() {
        // layout.addComponent(todoList);
    }

    private void addActionButtons() {
        Button deleteButton = new Button("Delete completed items");

        //deleteButton.addClickListener(click->todoList.deleteCompleted());

        layout.addComponent(deleteButton);

    }

    private void setProgressBar() {
        bar.setHeight("100");
        bar.setWidth("500");
        bar.setIndeterminate(true);
        bar.setVisible(false);
        layout.addComponent(bar);
        downLoadButton = new Button("Start download", click -> {
            bar.setVisible(true);
            float current = bar.getValue();
            if (current < 1.0f)
                bar.setValue(current + 0.10f);
            stockService.downloadStockContent();
            startTimer();
            Notification.show("Downloading",
                    "Stock data downloading. Please wait.",
                    Notification.Type.TRAY_NOTIFICATION);
        });
        downLoadButton.addStyleName("huge");
        layout.addComponent(downLoadButton);
    }

    private void startTimer() {
        timer.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                downLoadButton.setEnabled(false);
                System.out.println(new Date());
                checkDownloadStatus();
            }
        }, 10, 1, TimeUnit.SECONDS);
    }

    void checkDownloadStatus() {
        stockElements = callBack.getDownloadedStock();
        if (stockElements != null) {
            bar.setVisible(false);
            layout.removeAllComponents();
            timer.shutdownNow();
            for (StockElement element : stockElements) {
                System.out.println(element.toString());
            }
        }
    }
}
