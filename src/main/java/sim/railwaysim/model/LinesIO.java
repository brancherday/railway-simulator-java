package sim.railwaysim.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
/**
 * This class is used to save and load line singleton to/from json file
 */
public class LinesIO {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    /**
     * Saves lines to file at given file name
     * @param l LinesSingleton object to safe
     * @param filename String of filename
     * @throws IOException if can't save
     */
    public static void safe(LinesSingleton l, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(l, writer);
        }
    }
    /**
     * Loads LinesSingleton(Lines) from given file
     * @param filename File to load from
     * @return LinesSingleton object
     * @throws IOException if can't load
     */
    public static LinesSingleton load(String filename, Graph g) throws IOException {
        Gson gson = new Gson();
        LinesSingleton l = null;
        try (FileReader reader = new FileReader(filename)) {
            l = gson.fromJson(reader, LinesSingleton.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LinesSingleton.getInstance(l);
        assert l != null;
        //Creating map for faster finding of stations
        HashMap<String, StationModel> stationMap = new HashMap<>();
        for (StationModel station : g.getStationsList()) {
            stationMap.put(station.getId(), station);
        }
        //This is needed to have same objects because deserialization
        // creates new objects and not links to existing ones
        for(LineModel lineModel : l.getLines()){
            HashSet<StationModel> stationsToPut = new HashSet<>();
            lineModel.setStart(stationMap.get(lineModel.getStart().getId()));
            for(StationModel station : lineModel.getStations()){
                stationsToPut.add(stationMap.get(station.getId()));
            }
            lineModel.createLine(stationsToPut);
        }

        return l;
    }
}
