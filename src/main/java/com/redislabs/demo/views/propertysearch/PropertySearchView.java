package com.redislabs.demo.views.propertysearch;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.redislabs.demo.views.main.MainView;
import com.vaadin.flow.router.RouteAlias;

@Route(value = "properties", layout = MainView.class)
@PageTitle("Property Search")
@CssImport(value = "./styles/views/propertysearch/property-search-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
@RouteAlias(value = "", layout = MainView.class)
public class PropertySearchView extends Div {

    VerticalLayout mainLayout = new VerticalLayout();

    private Grid<Property> grid;

    private Grid.Column<Property> bedroomColumn;
    private Grid.Column<Property> bathroomColumn;
    private Grid.Column<Property> yearBuiltColumn;
    private Grid.Column<Property> taxValueColumn;
    private Grid.Column<Property> lotSizeColumn;
    private Grid.Column<Property> zipcodeColumn;


    Button searchButton = new Button("Search..");
    TextField returnedResults = new TextField("Results");
    TextField query = new TextField("Query");
    Button resultsButton = new Button("Fetch More Results..");
    Button aggregateButton = new Button("Aggregate..");
    TextField geoFilter = new TextField("Geo Location");

    private int gridSize = 100;
    private PropertySearchEngine searchEngine = new PropertySearchEngine(gridSize);
    private SearchEngineFilter filter = new SearchEngineFilter();


    public PropertySearchView() {
        setSizeFull();
        mainLayout.setSizeFull();
        mainLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        addSearchBar();
        addAggregateBar();
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
        layout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);

        ComboBox<String> bathroomFilter = new ComboBox<>("Bath Rooms");
        bathroomFilter.setItems(Arrays.asList("1+", "2+", "3+", "4+", "5+"));
        bathroomFilter.setValue("1+");
        bathroomFilter.setWidth("80px");
        bathroomFilter.setClearButtonVisible(false);
        bathroomFilter.addValueChangeListener(
                event -> filter.setMinBaths(Integer.parseInt(bathroomFilter.getValue().substring(0, 1))));
        filter.setMinBaths(Integer.parseInt(bathroomFilter.getValue().substring(0, 1)));
        layout.add(bathroomFilter);


        ComboBox<String> bedroomFilter = new ComboBox<>("Bed Rooms");
        bedroomFilter.setItems(Arrays.asList("1+", "2+", "3+", "4+", "5+", "6+"));
        bedroomFilter.setValue("1+");
        bedroomFilter.setWidth("80px");
        bedroomFilter.setClearButtonVisible(false);
        bedroomFilter.addValueChangeListener(
                event -> filter.setMinBeds(Integer.parseInt(bedroomFilter.getValue().substring(0, 1))));
        filter.setMinBeds(Integer.parseInt(bedroomFilter.getValue().substring(0, 1)));
        layout.add(bedroomFilter);

        IntegerField yearBuiltFilter = new IntegerField("Built After");
        yearBuiltFilter.setMin(1900);
        yearBuiltFilter.setMax(Calendar.getInstance().get(Calendar.YEAR));
        yearBuiltFilter.setValue(1970);
        yearBuiltFilter.setHasControls(true);
        yearBuiltFilter.setClearButtonVisible(false);
        yearBuiltFilter.addValueChangeListener(
                event -> filter.setMinYearBuilt(yearBuiltFilter.getValue()));
        filter.setMinYearBuilt(yearBuiltFilter.getValue());
        layout.add(yearBuiltFilter);

        IntegerField minTaxValueFilter = new IntegerField("Tax Value");
        minTaxValueFilter.setMin(0);
        minTaxValueFilter.setValue(0);
        minTaxValueFilter.setWidth("100px");
        minTaxValueFilter.setClearButtonVisible(false);
        minTaxValueFilter.addValueChangeListener(
                event -> filter.setMinTaxValue(minTaxValueFilter.getValue()));
        filter.setMinTaxValue(minTaxValueFilter.getValue());

        IntegerField maxTaxValueFilter = new IntegerField("");
        maxTaxValueFilter.setMin(0);
        maxTaxValueFilter.setValue(1000000);
        maxTaxValueFilter.setClearButtonVisible(false);
        maxTaxValueFilter.setWidth("100px");
         maxTaxValueFilter.addValueChangeListener(
                event -> filter.setMaxTaxValue(maxTaxValueFilter.getValue()));
        filter.setMaxTaxValue(maxTaxValueFilter.getValue());
        layout.add(minTaxValueFilter, maxTaxValueFilter);

        NumberField minLotSizeFilter = new NumberField("Lot Size");
        minLotSizeFilter.setMin(0);
        minLotSizeFilter.setValue((double) 0);
        minLotSizeFilter.setClearButtonVisible(false);
        minLotSizeFilter.setWidth("80px");
        minLotSizeFilter.addValueChangeListener(
                event -> filter.setMinLotSize(minLotSizeFilter.getValue()));
        filter.setMinLotSize(minLotSizeFilter.getValue());

        NumberField maxLotSizeFilter = new NumberField();
        maxLotSizeFilter.setMin(0);
        maxLotSizeFilter.setValue((double) 5000);
        maxLotSizeFilter.setClearButtonVisible(false);
        maxLotSizeFilter.setWidth("80px");
        maxLotSizeFilter.addValueChangeListener(
                event -> filter.setMaxLotSize(maxLotSizeFilter.getValue()));
        filter.setMaxLotSize(maxLotSizeFilter.getValue());

        layout.add(minLotSizeFilter, maxLotSizeFilter);

        TextField zipCodeFilter = new TextField("Zip Code");
        zipCodeFilter.setPreventInvalidInput(true);
        zipCodeFilter.setWidth("100px");
        zipCodeFilter.addValueChangeListener(
                event -> filter.setZipCode(zipCodeFilter.getValue()));
        filter.setZipCode(zipCodeFilter.getValue());
        layout.add(zipCodeFilter);

        geoFilter.setWidth("250px");
        geoFilter.addValueChangeListener(
                event -> filter.setGeo(geoFilter.getValue()));
        filter.setGeo("");
        layout.add(geoFilter);

        NumberField distanceFilter = new NumberField("Distance");
        distanceFilter.setMin(0);
        distanceFilter.setValue((double) 0);
        distanceFilter.setWidth("80px");
        distanceFilter.addValueChangeListener(
                event -> filter.setDistance(distanceFilter.getValue()));
        filter.setDistance(distanceFilter.getValue());
        layout.add(distanceFilter);

        ComboBox<String> unitFilter = new ComboBox<>("Unit");
        unitFilter.setItems(Arrays.asList("mi", "ft", "km", "m"));
        unitFilter.setValue("mi");
        unitFilter.setClearButtonVisible(false);
        unitFilter.setWidth("70px");
        unitFilter.addValueChangeListener(
                event -> filter.setUnit(unitFilter.getValue()));
        filter.setUnit(unitFilter.getValue());
        layout.add(unitFilter);

        mainLayout.add(layout);
    }

    private void addAggregateBar() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setPadding(true);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        layout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);

        ComboBox<String> groupByColumn = new ComboBox<>("Group By");
        groupByColumn.setItems(Arrays.asList("Bedrooms", "Bathrooms", "Year Built", "Zip Code"));
        groupByColumn.setValue("Zip Code");
        groupByColumn.setClearButtonVisible(false);
        groupByColumn.setWidth("150px");
        groupByColumn.addValueChangeListener(
                event -> filter.setGroupBy(groupByColumn.getValue()));
        filter.setGroupBy(groupByColumn.getValue());
        layout.add(groupByColumn);

        ComboBox<String> aggregateColumn = new ComboBox<>("Reduce By");
        aggregateColumn.setItems(Arrays.asList("Bathrooms", "Bedrooms", "Tax Value", "Year Built", "Lot Size"));
        aggregateColumn.setValue("Tax Value");
        aggregateColumn.setClearButtonVisible(false);
        aggregateColumn.setWidth("150px");
        aggregateColumn.addValueChangeListener(
                event -> filter.setReduce(aggregateColumn.getValue()));
        filter.setReduce(aggregateColumn.getValue());
        layout.add(aggregateColumn);

        ComboBox<String> aggregateFunction = new ComboBox<>("Reduce Function");
        aggregateFunction.setItems(Arrays.asList("Avg", "Min", "Max", "Sum", "Count"));
        aggregateFunction.setValue("Avg");
        aggregateFunction.setClearButtonVisible(false);
        aggregateFunction.setWidth("150px");
        aggregateFunction.addValueChangeListener(
                event -> filter.setReducer(aggregateFunction.getValue()));
        filter.setReducer(aggregateFunction.getValue());
        layout.add(aggregateFunction);

        IntegerField limitFilter = new IntegerField("Limit");
        limitFilter.setMin(1);
        limitFilter.setValue(10);
        limitFilter.setHasControls(true);
        limitFilter.setClearButtonVisible(false);
        limitFilter.setWidth("120px");
        limitFilter.addValueChangeListener(
                event -> filter.setLimit(limitFilter.getValue()));
        filter.setLimit(limitFilter.getValue());
        layout.add(limitFilter);

        RadioButtonGroup<String> sortOrderGroup = new RadioButtonGroup<>();
        sortOrderGroup.setLabel("Sort Order");
        sortOrderGroup.setItems("Ascending", "Descending");
        sortOrderGroup.setValue("Ascending");
        sortOrderGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        sortOrderGroup.addValueChangeListener(
                event -> filter.setAscending(event.getValue().equals("Ascending")));
        filter.setAscending(true);
        layout.add(sortOrderGroup);


        aggregateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        aggregateButton.addClickListener(this::aggregateButtonClickedMessage);

        layout.add(aggregateButton);

        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchButton.addClickListener(this::searchButtonClickedMessage);
        resultsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        resultsButton.setEnabled(false);
        resultsButton.addClickListener(this::moreResultsButtonClickedMessage);

        layout.add(searchButton, resultsButton);

        mainLayout.add(layout);

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

    private void searchButtonClickedMessage(ClickEvent<Button> buttonClickEvent) {
        enableSearchGrid();
        resultsButton.setEnabled(false);
        resultsButton.setText("Fetch More Results..");
        returnedResults.setValue("");
        long start = System.currentTimeMillis();
        List<Property> results = searchEngine.search(filter, true);
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
        List<Property> results = searchEngine.search(filter, false);
        long time = System.currentTimeMillis() - start;
        grid.setItems(results);
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

    private void aggregateButtonClickedMessage(ClickEvent<Button> buttonClickEvent) {
        resultsButton.setText("Fetch More Results..");
        resultsButton.setEnabled(false);
        String reduceTitle = filter.getReducer();
        if ( !filter.getReducer().equals("Count") )
            reduceTitle += " " + filter.getReduce();
        enableAggregateGrid(filter.getGroupBy(), reduceTitle);
        long start = System.currentTimeMillis();
        List<Property> results = searchEngine.aggregate(filter);
        long time = System.currentTimeMillis() - start;
        returnedResults.setValue(String.valueOf(results.size()) + " in " + time + "ms");
        query.setValue(filter.getAggregation().getArgsString());
        grid.setItems(results);
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

    private void enableAggregateGrid(String groupTitle, String reduceTitle) {
        zipcodeColumn.setHeader(groupTitle);
        bathroomColumn.setVisible(false);
        bedroomColumn.setVisible(false);
        taxValueColumn.setHeader(reduceTitle);
        yearBuiltColumn.setVisible(false);
        lotSizeColumn.setVisible(false);
    }

    private void enableSearchGrid() {
        zipcodeColumn.setHeader("Zip Code");
        bathroomColumn.setVisible(true);
        bedroomColumn.setVisible(true);
        taxValueColumn.setHeader("Tax Value");
        yearBuiltColumn.setVisible(true);
        lotSizeColumn.setVisible(true);
    }

    private void addColumnsToGrid() {
        createZipcodeColumn();
        createBathroomColumn();
        createBedroomColumn();
        createYearBuiltColumn();
        createTaxValueColumn();
        createLotSizeColumn();
    }

    private void createBathroomColumn() {
        bathroomColumn = grid
                .addColumn(new NumberRenderer<>(Property::getBathroomCount, "%.1f",
                        Locale.US, "0"))
                .setComparator(property -> property.getBathroomCount())
                .setHeader("Bath Rooms").setResizable(true).setWidth("10%");
    }

    private void createBedroomColumn() {
        bedroomColumn = grid
                .addColumn(Property::getBedroomCount)
                .setComparator(property -> property.getBedroomCount())
                .setHeader("Bed Rooms").setResizable(true).setWidth("10%");
    }

    private void createTaxValueColumn() {
        taxValueColumn = grid
                .addColumn(new NumberRenderer<>(Property::getTaxValue, "%.2f",
                        Locale.US, "0.00"))
                .setComparator(property -> property.getTaxValue())
                .setHeader("Tax Value").setResizable(true).setWidth("25%");
    }

    private void createYearBuiltColumn() {
        yearBuiltColumn = grid
                .addColumn(Property::getYearBuilt)
                .setComparator(property -> property.getYearBuilt())
                .setHeader("Year Built").setResizable(true).setWidth("15%");
    }

    private void createLotSizeColumn() {
        lotSizeColumn = grid
                .addColumn(Property::getLotSize)
                .setComparator(property -> property.getLotSize())
                .setHeader("Lot Size").setResizable(true).setWidth("25%");
    }

    private void createZipcodeColumn() {
        zipcodeColumn = grid
                .addColumn(Property::getZipcode)
                .setComparator(property -> property.getZipcode())
                .setHeader("Zip Code").setResizable(true).setWidth("15%");
    }
};
