package sim.railwaysim.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Needed to store List of lines to have access to it from every part of the program.
 * Implemented using Singleton pattern to ensure accessibility from every class.
 */
public class LinesSingleton {
    private static LinesSingleton instance;
    private List<LineModel> lines;

    private LinesSingleton() {
        lines = new CopyOnWriteArrayList<>();
    }

    /**
     * This method used only for deserializing from json file. Takes created instance and set whole singleton to it
     * @param l instance that created by deserialization
     * @return instance by deserialization
     */
    public static LinesSingleton getInstance(LinesSingleton l) {
        if(instance == null){
            instance = l;
        }
        return instance;
    }
    public static LinesSingleton getInstance() {
        if(instance == null){
            instance = new LinesSingleton();
        }
        return instance;
    }

    public List<LineModel> getLines() {
        return lines;
    }

    public void setLines(List<LineModel> lines) {
        this.lines = lines;
    }
}
