module sim.railwaysim {
    requires javafx.controls;
    requires com.google.gson;
    requires org.apache.logging.log4j;
    requires junit;



    exports sim.railwaysim.model;
    exports sim.railwaysim.view;
    exports sim.railwaysim.controller;
    opens sim.railwaysim.model to com.google.gson;
}
