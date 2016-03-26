package my.vaadin.app;

import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
@Widgetset("my.vaadin.app.MyAppWidgetset")
public class MyUI extends UI {

	private CustomerService service = CustomerService.getInstance();
    private Grid grid = new Grid();
    private TextField filterText = new TextField();
    private CustomerForm form = new CustomerForm(this);
	
    @Override
    protected void init(VaadinRequest vaadinRequest) {
    	final VerticalLayout layout = new VerticalLayout();
    	
    	grid.setColumns("firstName", "lastName", "email");
    	grid.setCaption("Caption");
    	
    	filterText.setInputPrompt("filter by name...");
    	filterText.addTextChangeListener(e -> {
    	    grid.setContainerDataSource(new BeanItemContainer<>(Customer.class,
    	            service.findAll(e.getText())));
    	});
    	
    	Button clearFilterTextBtn = new Button(FontAwesome.TIMES);
    	clearFilterTextBtn.setDescription("Clear the current filter");
    	clearFilterTextBtn.addClickListener(e -> {
    	  filterText.clear();
    	  updateList();
    	});
    	
    	
    	
    	CssLayout filtering = new CssLayout();
    	filtering.addComponents(filterText, clearFilterTextBtn);
    	filtering.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
    	
    	Button addCustomerBtn = new Button("Add new customer");
    	addCustomerBtn.addClickListener(e -> {
    	    grid.select(null);
    	    form.setCustomer(new Customer());
    	});
    	
    	HorizontalLayout toolbar = new HorizontalLayout(filtering, addCustomerBtn);
    	toolbar.setSpacing(true);
    	
    	form.setVisible(false);
    	
        // add Grid to the layout
    	HorizontalLayout main = new HorizontalLayout(grid, form);
    	main.setSpacing(true);
    	main.setSizeFull();
    	grid.setSizeFull();
    	main.setExpandRatio(grid, 1);

    	layout.addComponents(toolbar, main);

        // fetch list of Customers from service and assign it to Grid
        updateList();

        layout.setMargin(true);
        setContent(layout);
        
        grid.addSelectionListener(event -> {
            if (event.getSelected().isEmpty()) {
                form.setVisible(false);
            } else {
                Customer customer = (Customer) event.getSelected().iterator().next();
                form.setCustomer(customer);
            }
        });
    }

	public void updateList() {
		List<Customer> customers = service.findAll();
        grid.setContainerDataSource(new BeanItemContainer<>(Customer.class, customers));
	}

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
