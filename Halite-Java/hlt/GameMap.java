package hlt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import hlt.Ship.DockingStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;

public class GameMap {
    private final int width, height;
    private final int playerId;
    private final List<Player> players;
    private final List<Player> playersUnmodifiable;
    private final Map<Integer, Planet> planets;
    private final List<Ship> allShips;
    private final List<Ship> allShipsUnmodifiable;

    // used only during parsing to reduce memory allocations
    private final List<Ship> currentShips = new ArrayList<>();

    public GameMap(final int width, final int height, final int playerId) {
        this.width = width;
        this.height = height;
        this.playerId = playerId;
        players = new ArrayList<>(Constants.MAX_PLAYERS);
        playersUnmodifiable = Collections.unmodifiableList(players);
        planets = new TreeMap<>();
        allShips = new ArrayList<>();
        allShipsUnmodifiable = Collections.unmodifiableList(allShips);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getMyPlayerId() {
        return playerId;
    }

    public List<Player> getAllPlayers() {
        return playersUnmodifiable;
    }

    public Player getMyPlayer() {
        return getAllPlayers().get(getMyPlayerId());
    }

    public Ship getShip(final int playerId, final int entityId) throws IndexOutOfBoundsException {
        return players.get(playerId).getShip(entityId);
    }

    public Planet getPlanet(final int entityId) {
        return planets.get(entityId);
    }

    public Map<Integer, Planet> getAllPlanets() {
        return planets;
    }

    public List<Ship> getAllShips() {
        return allShipsUnmodifiable;
    }

    public ArrayList<Entity> objectsBetween(Position start, Position target) {
        final ArrayList<Entity> entitiesFound = new ArrayList<>();

        addEntitiesBetween(entitiesFound, start, target, planets.values());
        addEntitiesBetween(entitiesFound, start, target, allShips);

        return entitiesFound;
    }

    private static void addEntitiesBetween(final List<Entity> entitiesFound,
                                           final Position start, final Position target,
                                           final Collection<? extends Entity> entitiesToCheck) {

        for (final Entity entity : entitiesToCheck) {
            if (entity.equals(start) || entity.equals(target)) {
                continue;
            }
            if (Collision.segmentCircleIntersect(start, target, entity, Constants.FORECAST_FUDGE_FACTOR)) {
                entitiesFound.add(entity);
            }
        }
    }

    public Map<Double, Entity> nearbyEntitiesByDistance(final Entity entity) {
        final Map<Double, Entity> entityByDistance = new TreeMap<>();

        for (final Planet planet : planets.values()) {
            if (planet.equals(entity)) {
                continue;
            }
            entityByDistance.put(entity.getDistanceTo(planet), planet);
        }

        for (final Ship ship : allShips) {
            if (ship.equals(entity)) {
                continue;
            }
            entityByDistance.put(entity.getDistanceTo(ship), ship);
        }

        return entityByDistance;
    }
    public TreeMap<Double, Ship> nearbyDockedShips(final Entity entity){
    	final TreeMap<Double, Ship> entityByDistance = new TreeMap<>(); 
    	for (final Ship ship : allShips) {
             if (ship.equals(entity)) {
                 continue;
             }
             if(ship.getOwner() != ((Ship)(entity)).getOwner() && ship.getDockingStatus() == DockingStatus.Docked)
            	 entityByDistance.put(entity.getDistanceTo(ship), ship);
         }
    	
    	return entityByDistance;
    }
    public HashMap<Ship, Integer> plannedShips(){
    	HashMap<Ship, Integer> plannedShips = new HashMap();
    	for(final Ship ship: allShips) {
    		if(ship.getOwner() != playerId) {
    			plannedShips.put(ship, 0);
    		}
    	}
    	return plannedShips;
    }
    
    public TreeMap<Double, Ship> nearbyShips(final Entity entity){
    	final TreeMap<Double, Ship> entityByDistance = new TreeMap<>(); 
    	for (final Ship ship : allShips) {
             if (ship.equals(entity)) {
                 continue;
             }
             if(ship.getOwner() != ((Ship)(entity)).getOwner()) {
 
            		 entityByDistance.put(entity.getDistanceTo(ship), ship);
             }
         }

         return entityByDistance;
    }
    public TreeMap<Double, Ship> nearbyMyShips(Ship myShip){
    	final TreeMap<Double, Ship> entityByDistance = new TreeMap<>(); 
    	for (final Ship ship : allShips) {
             if (ship.equals(myShip)) {
                 continue;
             }
             if(ship.getOwner() == myShip.getOwner()) {
            	 if(myShip.getDistanceTo(ship) > 3) {
            		 break;
            	 }
            	 if(ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
            		 continue;
            	 }
            	 entityByDistance.put(myShip.getDistanceTo(ship), ship);
             }
         }

         return entityByDistance;
    }
    public void closeShips(Ship ship, ArrayList<Ship> freezed){
    	TreeMap<Double, Ship> closeShips = nearbyMyShips(ship);
    	if(freezed.contains(ship)) {
    		return;
    	}
    	for(Ship closeShip: closeShips.values()) {
       	 if(closeShip.getDockingStatus() != Ship.DockingStatus.Undocked) {
    		 continue;
    	 }
    		if(!freezed.contains(closeShip)) {
    			freezed.add(closeShip);
    		}
    	}
    }
    public TreeMap<Double, Planet> nearbyEmptyPlanetsByDistance(final Entity ship, Map<Planet, Integer> planetShips, Map<Planet, Integer> plannedPlanetShips){
    	final TreeMap<Double, Planet> planetByDistance = new TreeMap<>();
        for (final Planet planet : planets.values()) {
            if(!planet.isOwned() && planetShips.get(planet) == 0 && plannedPlanetShips.get(planet) == 0) {
            	planetByDistance.put(ship.getDistanceTo(planet), planet);
            }
        }
        
        return planetByDistance;
    }
    
    public TreeMap<Double, Planet> nearbyMyPlanetsNotFull(final Entity ship, Map<Planet, Integer> planetShips, Map<Planet, Integer> plannedPlanetShips){
    	final TreeMap<Double, Planet> planetByDistance = new TreeMap<>();
        for (final Planet planet : plannedPlanetShips.keySet()) {
        	if(planet.isOwned()) {
        		if(planet.getOwner() != this.playerId)
        			continue;
        	}
        	if(plannedPlanetShips.get(planet) + planetShips.get(planet) < planet.getDockingSpots()) {
        		planetByDistance.put(ship.getDistanceTo(planet), planet);
        	}
        	
        }
        
        return planetByDistance;
    }
    
    public Map<Planet, Integer> numberOfShipsOnPlanet(){
    	final Map<Planet, Integer> planetByDistance = new HashMap<>();
    	for(final Planet planet: planets.values()) {
    		planetByDistance.put(planet, planet.getDockedShips().size());
    	}
    	return planetByDistance;
    }
    
    public int goToClosestCorner(Ship ship) {
    	TreeMap<Double, Integer> closestCorner = new TreeMap();
    	Position target = new Position(0, 0);
    	closestCorner.put(ship.getDistanceTo(target), 0);
    	target = new Position(0, this.height);
    	closestCorner.put(ship.getDistanceTo(target), 1);
    	target = new Position(this.width, this.height);
    	closestCorner.put(ship.getDistanceTo(target), 2);
    	target = new Position(this.width, 0);
    	closestCorner.put(ship.getDistanceTo(target), 3);
    	if(closestCorner.firstEntry().getKey() < 2) {
    		return -2;
    	}
    	return closestCorner.pollFirstEntry().getValue();
    }

    public GameMap updateMap(final Metadata mapMetadata) {
        final int numberOfPlayers = MetadataParser.parsePlayerNum(mapMetadata);

        players.clear();
        planets.clear();
        allShips.clear();

        // update players info
        for (int i = 0; i < numberOfPlayers; ++i) {
            currentShips.clear();
            final Map<Integer, Ship> currentPlayerShips = new TreeMap<>();
            final int playerId = MetadataParser.parsePlayerId(mapMetadata);

            final Player currentPlayer = new Player(playerId, currentPlayerShips);
            MetadataParser.populateShipList(currentShips, playerId, mapMetadata);
            allShips.addAll(currentShips);

            for (final Ship ship : currentShips) {
                currentPlayerShips.put(ship.getId(), ship);
            }
            players.add(currentPlayer);
        }

        final int numberOfPlanets = Integer.parseInt(mapMetadata.pop());

        for (int i = 0; i < numberOfPlanets; ++i) {
            final List<Integer> dockedShips = new ArrayList<>();
            final Planet planet = MetadataParser.newPlanetFromMetadata(dockedShips, mapMetadata);
            planets.put(planet.getId(), planet);
        }

        if (!mapMetadata.isEmpty()) {
            throw new IllegalStateException("Failed to parse data from Halite game engine. Please contact maintainers.");
        }

        return this;
    }
}
