package com.redislabs.demo.views.addresssearch;

import com.redislabs.demo.views.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import java.util.Arrays;
import java.util.List;

@Route(value = "addresses", layout = MainView.class)
@PageTitle("Address Search")
@CssImport(value = "./styles/views/propertysearch/property-search-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
//@RouteAlias(value = "", layout = MainView.class)
public class AddressSearchView extends Div {

    VerticalLayout mainLayout = new VerticalLayout();

    private Grid<Address> grid;

    private Grid.Column<Address> numberColumn;
    private Grid.Column<Address> streetColumn;
    private Grid.Column<Address> unitColumn;
    private Grid.Column<Address> cityColumn;
    private Grid.Column<Address> postalCodeColumn;
    private Grid.Column<Address> stateColumn;


    Button searchButton = new Button("Search..");
    TextField returnedResults = new TextField("Results");
    TextField query = new TextField("Query");
    Button resultsButton = new Button("Fetch More Results..");

    IntegerField slopField = new IntegerField("Slop");

    TextField geoFilter = new TextField("Geo Location");



    private int gridSize = 100;
    private AddressSearchEngine searchEngine = new AddressSearchEngine(gridSize);
    private SearchEngineFilter filter = new SearchEngineFilter();

    public AddressSearchView() {
        setSizeFull();
        mainLayout.setSizeFull();
        mainLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        addSearchBar();
        addResultsBar();
        createGrid();
        mainLayout.setSpacing(false);
        add(mainLayout);
    }

    private void createGrid() {
        createGridComponent();
        addColumnsToGrid();
        grid.recalculateColumnWidths();
        mainLayout.add(grid);
    }

    private void addSearchBar() {

        HorizontalLayout layout = new HorizontalLayout();
        layout.setPadding(true);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);

        TextField freeTextFilter = new TextField("Free Text");
        freeTextFilter.setWidth("30%");
        freeTextFilter.addValueChangeListener(
                event -> filter.setFree(freeTextFilter.getValue()));
        filter.setFree("");
        layout.add(freeTextFilter);

        TextField streetFilter = new TextField("Street");
        streetFilter.setWidth("20%");
        streetFilter.addValueChangeListener(
                event -> filter.setStreet(streetFilter.getValue()));
        filter.setStreet("");
        layout.add(streetFilter);

        TextField cityFilter = new TextField("City");
        cityFilter.setWidth("15%");
        cityFilter.addValueChangeListener(
                event -> filter.setCity(cityFilter.getValue()));
        filter.setCity("");
        layout.add(cityFilter);


        TextField postalCodeFilter = new TextField("Postal Code");
        postalCodeFilter.addValueChangeListener(
                event -> filter.setPostalCode(postalCodeFilter.getValue()));
        filter.setPostalCode("");
        layout.add(postalCodeFilter);

        ComboBox<String> stateFilter = new ComboBox<>("State");
        stateFilter.setItems(Arrays.asList("NY", "NJ", "MA"));
        stateFilter.setValue("NY");
        stateFilter.setWidth("80px");
        stateFilter.setClearButtonVisible(false);
        stateFilter.addValueChangeListener(
                event -> filter.setState(stateFilter.getValue()));
        filter.setState(stateFilter.getValue());
        layout.add(stateFilter);

        ComboBox<String> sortBy = new ComboBox<>("Sort");
        sortBy.setItems(Arrays.asList("Street", "Unit", "City", "Postal Code", "State"));
        sortBy.setValue("City");
        sortBy.setWidth("150px");
        sortBy.setClearButtonVisible(false);
        sortBy.addValueChangeListener(
                event -> filter.setSort(sortBy.getValue()));
        filter.setSort(sortBy.getValue());
        layout.add(sortBy);

        RadioButtonGroup<String> sortOrderGroup = new RadioButtonGroup<>();
        sortOrderGroup.setLabel("Sort Order");
        sortOrderGroup.setItems("Ascending", "Descending");
        sortOrderGroup.setValue("Ascending");
        sortOrderGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        sortOrderGroup.addValueChangeListener(
                event -> filter.setAscending(event.getValue().equals("Ascending")));
        filter.setAscending(true);
        layout.add(sortOrderGroup);

        HorizontalLayout optionsLayout = new HorizontalLayout();
        optionsLayout.setPadding(true);

        HorizontalLayout checkBoxLayout = new HorizontalLayout();
        checkBoxLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        Checkbox phoneticCheck = new Checkbox();
        phoneticCheck.setLabel("Phonetic");
        phoneticCheck.setValue(true);
        phoneticCheck.addValueChangeListener(
                event -> filter.setPhonetic(phoneticCheck.getValue()));
        filter.setPhonetic(phoneticCheck.getValue());
        checkBoxLayout.add(phoneticCheck);

        Checkbox inorderCheck = new Checkbox();
        inorderCheck.setLabel("In Order");
        inorderCheck.setValue(false);
        inorderCheck.addValueChangeListener(
                event -> filter.setInOrder(inorderCheck.getValue()));
        filter.setInOrder(inorderCheck.getValue());
        checkBoxLayout.add(inorderCheck);

        Checkbox slopCheck = new Checkbox();
        slopCheck.setLabel("Slop");
        slopCheck.setValue(false);
        slopCheck.addValueChangeListener(
                event -> {
                    slopField.setEnabled(event.getValue());
                    filter.setSlop(event.getValue());
                });
        filter.setSlop(false);
        checkBoxLayout.add(slopCheck);
        optionsLayout.add(checkBoxLayout);

        slopField.setEnabled(false);
        slopField.setMin(0);
        slopField.setMax(10);
        slopField.setValue(0);
        slopField.setHasControls(true);
        slopField.addValueChangeListener(
                event -> filter.setSlopValue(slopField.getValue()));
        filter.setSlopValue(0);
        optionsLayout.add(slopField);

        HorizontalLayout geoLayout = new HorizontalLayout();
        geoLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);

        geoFilter.setWidth("250px");
        geoFilter.addValueChangeListener(
                event -> filter.setGeo(geoFilter.getValue()));
        filter.setGeo("");
        geoLayout.add(geoFilter);

        NumberField distanceFilter = new NumberField("Distance");
        distanceFilter.setMin(0);
        distanceFilter.setValue((double) 0);
        distanceFilter.setWidth("100px");
        distanceFilter.addValueChangeListener(
                event -> filter.setDistance(distanceFilter.getValue()));
        filter.setDistance(distanceFilter.getValue());
        geoLayout.add(distanceFilter);

        ComboBox<String> unitFilter = new ComboBox<>("Unit");
        unitFilter.setItems(Arrays.asList("mi", "ft", "km", "m"));
        unitFilter.setValue("mi");
        unitFilter.setClearButtonVisible(false);
        unitFilter.setWidth("70px");
        unitFilter.addValueChangeListener(
                event -> filter.setDistUnit(unitFilter.getValue()));
        filter.setDistUnit(unitFilter.getValue());
        geoLayout.add(unitFilter);
        optionsLayout.add(geoLayout);

        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        resultsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchButton.addClickListener(this::searchButtonClickedMessage);
        resultsButton.setEnabled(false);
        resultsButton.addClickListener(this::moreResultsButtonClickedMessage);

        Checkbox highLightCheck = new Checkbox();
        highLightCheck.setLabel("Highlight Results");
        highLightCheck.setValue(true);
        highLightCheck.addValueChangeListener(
                event -> filter.setHighlight(highLightCheck.getValue()));
        filter.setHighlight(highLightCheck.getValue());

        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.setSpacing(true);
        searchLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        searchLayout.add(searchButton, resultsButton, highLightCheck);
        optionsLayout.add(searchLayout);

        mainLayout.add(layout);
        mainLayout.add(optionsLayout);
    }

    private void addResultsBar() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setPadding(true);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        layout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);

        returnedResults.setReadOnly(true);
        query.setReadOnly(true);
        query.setSizeFull();

        layout.add(returnedResults, query);
        mainLayout.add(layout);

    }

    private List<String> getSuggestions(String prefix, Integer max) {
        return searchEngine.getSuggestions(prefix, max);
    }


    private void searchButtonClickedMessage(ClickEvent<Button> buttonClickEvent) {
        resultsButton.setEnabled(false);
        returnedResults.setValue("");
        resultsButton.setText("Fetch More Results..");
        long start = System.currentTimeMillis();
        List<Address> results = searchEngine.search(filter, true);
        long time = System.currentTimeMillis() - start;
        grid.setItems(results);
        returnedResults.setValue(String.valueOf(results.size()) + " in " + time + "ms");
        query.setValue(filter.getQuery());
        long moreResults = searchEngine.moreResults();
        if ( moreResults > 0 ) {
            resultsButton.setText(String.format("Fetch More Results (%d)..", moreResults));
            resultsButton.setEnabled(true);
        }
    }

    private void moreResultsButtonClickedMessage(ClickEvent<Button> buttonClickEvent) {
        long start = System.currentTimeMillis();
        List<Address> results = searchEngine.search(filter, false);
        long time = System.currentTimeMillis() - start;
        grid.setItems(results);
        returnedResults.setValue(String.valueOf(results.size()));
        returnedResults.setValue(String.valueOf(results.size()) + " in " + time + "ms");
        long moreResults = searchEngine.moreResults();
        if ( moreResults == 0 ) {
            resultsButton.setText("Fetch More Results..");
            resultsButton.setEnabled(false);
        }else{
            resultsButton.setText(String.format("Fetch More Results (%d)..", moreResults));
            resultsButton.setEnabled(true);
        }
    }

    private void createGridComponent() {
        grid = new Grid<>();
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_COLUMN_BORDERS);
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null)
                geoFilter.setValue(event.getValue().getGeo().replaceFirst(",", "  "));
        });
    }

    private void addColumnsToGrid() {
        createNumberColumn();
        createStreetColumn();
        createUnitColumn();
        createCityColumn();
        createPostalCodeColumn();
        createStateColumn();
    }

    private void createNumberColumn() {
        numberColumn = grid
                .addColumn(Address::getNumber)
                .setComparator(Address -> Address.getNumber())
                .setHeader("Number").setResizable(true).setWidth("10%");
    }

    private void createStreetColumn() {
        streetColumn = grid
                .addColumn(TemplateRenderer
                        .<Address> of("<ul inner-h-t-m-l=\"[[item.street]]\"></ul>")
                        .withProperty("street", address -> address.getStreet()))
                .setComparator(Address -> Address.getStreet())
                .setHeader("Street").setResizable(true).setWidth("25%");
    }

    private void createUnitColumn() {
        unitColumn = grid
                .addColumn(Address::getUnit)
                .setComparator(Address -> Address.getUnit())
                .setHeader("Unit").setResizable(true).setWidth("15%");
    }

    private void createCityColumn() {
        cityColumn = grid
                .addColumn(TemplateRenderer
                        .<Address> of("<ul inner-h-t-m-l=\"[[item.city]]\"></ul>")
                        .withProperty("city", address -> address.getCity()))
                .setComparator(Address -> Address.getCity())
                .setHeader("City").setResizable(true).setWidth("20%");
    }

    private void createPostalCodeColumn() {
        postalCodeColumn = grid
                .addColumn(Address::getPostalCode)
                .setComparator(Address -> Address.getPostalCode())
                .setHeader("Postal Code").setResizable(true).setWidth("15%");
    }

    private void createStateColumn() {
        stateColumn = grid
                .addColumn(Address::getState)
                .setComparator(Address -> Address.getState())
                .setHeader("State").setResizable(true).setWidth("15%");
    }
};
