# Description

Railway Simulator is a program that simulates railway with trains moving on lines and passengers trying to access destination.
Program opens with main menu with two options:

Click "New" then "Start". That will provide empty map and options to change it via options.

CLick "Load" then "Start". That loads already existing simulation. There is already existing simulation, that will be deleted when user creates new simulation.

Simulation starts with loading json file with stations and rails between them, then it creates graph and print it on the map. 
After it program loads json file with lines. To put trains user have to use menu.
Then generator start to spawn passengers on stations and trains start to move on rails according to their lines. 
Each line is different thread, that controls moves of trains and disembarks and boards passengers according to stops of each passenger. 
Passengers calls A* algorithm to find their way to destination station, and then another algorithm to create list of stops that they have to take in order to get to destination using trains.


# User manual

### Communication with the simulation

List of buttons:

-"Add Train" - When pressed, it will display text input window where user will enter line color, in order to add train to that line. Option "all" will add trains to every line in the simulation.

-"Delete Train" - When pressed, it will display text input window where user will enter line color, in order to delete random train on that line.

-"Add Station" - After selecting this option user would be able to click on map, and text input window will appear, where user will input name of the station. Station will be displayed on map where user clicked. In order to disable this button, user have to click it again.

-"Add Edge" - After selecting 2 stations this button will be enabled. When pressed, it will create new edge between two selected stations.

-"Add Line" - After selecting 2 or more stations this button will be enabled. When pressed, it will display text input window where user will enter line color, in order to add that line.

-"Delete Line" - When pressed, it will display text input window where user will enter line color, in order to delete that line.

-"Speed" - Changes speed of every train. There are 3 speeds that changes after each press.

-"Safe" - Will save the simulation state. This state will be loaded when chose to load at the start.

### Visible statistics

In the left upper corner will be printed statistics with the next information:

-Stations amount.

-Lines amount.

-Trains amount.

-Passengers that got to their destination.

### Other features

-When the mouse hovers over the train a tooltip will appear with information about the number of passengers.

-Under each station there is a number of passengers waiting on that station.

-Map is bigger than the window, that means user can use mouse or arrows to move on the map.

-Also user can zoom in/out on the map. Press and hold ctrl and use mouse wheel.

-When starting the program, as argument user can pass one of the following in order to set logging levels: trace/debug/info/warn/error/fatal/off. By default it set on "off".



