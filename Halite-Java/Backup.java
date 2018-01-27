import hlt.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

public class Backup {

    public static void main(final String[] args) {
    	final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("Backup");
        final String initialMapIntelligence =
                "width: " + gameMap.getWidth() +
                "; height: " + gameMap.getHeight() +
                "; players: " + gameMap.getAllPlayers().size() +
                "; planets: " + gameMap.getAllPlanets().size();
        //Log.log(initialMapIntelligence);

        final ArrayList<Move> moveList = new ArrayList<>();


        while(true) {
            moveList.clear();
            networking.updateMap(gameMap);
            Map<Planet, Integer> myPlanetsPlannedShips = new HashMap();
            for(Planet planet: gameMap.getAllPlanets().values()) {
            	myPlanetsPlannedShips.put(planet, 0);
            }
           /* Map<Ship, Integer> plannedShips = new HashMap();
            for(Ship ship: gameMap.getAllShips()) {
            	if(gameMap.getMyPlayerId() != ship.getOwner())
            		plannedShips.put(ship, 0);
            }*/
            //Log.log("size of plannedShips " + plannedShips.size());
            Map<Planet, Integer> planetsShips = gameMap.numberOfShipsOnPlanet();
            HashSet<Planet> fullPlanets = new HashSet();
            
            ArrayList<Ship> activeShips = new ArrayList();
            ArrayList<Ship> attackShips = new ArrayList();
            ArrayList<Ship> miningShips = new ArrayList();
            ArrayList<Ship> freezedShips = new ArrayList();
            for(Ship ship: gameMap.getMyPlayer().getShips().values()) {
            	if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
                    continue;
                }
                activeShips.add(ship);
                
                
            }
            
            Log.log("My Ships: " + gameMap.getMyPlayer().getShips().values().size() + "Total Ships: " + gameMap.getAllShips().size());
            for (final Ship ship : gameMap.getMyPlayer().getShips().values()){
            	
            	if(gameMap.getMyPlayer().getShips().values().size()*1.0/gameMap.getAllShips().size() < .13 && gameMap.getAllPlayers().size()>2) {
                	if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
                        moveList.add(new UndockMove(ship));
                		continue;
                    }
            		int corner = gameMap.goToClosestCorner(ship);
            		int fudge = 0;
            		final ThrustMove newThrustMove;
            		if(corner == 0) {
            			Position targetPos = new Position(0+fudge, 0+fudge);
            			newThrustMove = Navigation.navigateShipTowardsTarget(gameMap, ship, targetPos, Constants.MAX_SPEED, true, Constants.MAX_NAVIGATION_CORRECTIONS, Math.PI/180.0);
            		} else if(corner == 1) {
            			Position targetPos = new Position(0+fudge, gameMap.getHeight()-fudge);
            			newThrustMove = Navigation.navigateShipTowardsTarget(gameMap, ship, targetPos, Constants.MAX_SPEED, true, Constants.MAX_NAVIGATION_CORRECTIONS, Math.PI/180.0);
            		} else if(corner == 2) {
            			Position targetPos = new Position(gameMap.getWidth()-fudge, gameMap.getHeight()+fudge);
            			newThrustMove = Navigation.navigateShipTowardsTarget(gameMap, ship, targetPos, Constants.MAX_SPEED, true, Constants.MAX_NAVIGATION_CORRECTIONS, Math.PI/180.0);
            		} else if(corner == 3){ 
            			Position targetPos = new Position(gameMap.getWidth()-fudge, 0+fudge);
            			newThrustMove = Navigation.navigateShipTowardsTarget(gameMap, ship, targetPos, Constants.MAX_SPEED, true, Constants.MAX_NAVIGATION_CORRECTIONS, Math.PI/180.0);
            		} else {
            			newThrustMove = null;
            		}
            		if(newThrustMove != null)
            			moveList.add(newThrustMove);
            		continue;
            	} else {
            	if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
                    continue;
                }
            	
            	
            	TreeMap<Double, Ship> closestEnemyShips = gameMap.nearbyShips(ship);
            	Log.log("size of closestEnemeyShips" + closestEnemyShips.size());
            	TreeMap<Double, Planet> closestPlanetsNotFull = gameMap.nearbyMyPlanetsNotFull(ship, planetsShips, myPlanetsPlannedShips);
            	TreeMap<Double, Planet> closestEmptyPlanet = gameMap.nearbyEmptyPlanetsByDistance(ship, planetsShips, myPlanetsPlannedShips);
            	if(closestEnemyShips.size() == 0) {
            		closestEnemyShips.put(Double.MAX_VALUE, null);
            	}
            	if(closestPlanetsNotFull.size() == 0) {
            		closestPlanetsNotFull.put(Double.MAX_VALUE, null);
            	}
            	if(closestEmptyPlanet.size() == 0) {
            		closestEmptyPlanet.put(Double.MAX_VALUE, null);
            	}
            	if(closestEnemyShips.firstKey() < closestPlanetsNotFull.firstKey() && closestEnemyShips.firstKey() < closestEmptyPlanet.firstKey()) {
            		Ship targetShip = closestEnemyShips.pollFirstEntry().getValue();
                	final ThrustMove newThrustMove = Navigation.navigateShipToDock(gameMap, ship, targetShip, Constants.MAX_SPEED);
                	if (newThrustMove != null) {
                		moveList.add(newThrustMove);
                		//plannedShips.put(ship, plannedShips.get(ship) + 1);
                	}
                	continue;
            	}

 /*           	try {
            	Log.log("Closest Planet Not Full: " + closestPlanetsNotFull.firstEntry().getValue().getId() + " Closest Empty Planet: " + closestPlanetsNotFull.firstEntry().getValue().getId());
            	} catch (Exception e) {
            		
            	}
            	for(Planet planet: myPlanetsPlannedShips.keySet()) {
            		Log.log("Planet: " + planet.getId() + " Docked Ships: " + myPlanetsPlannedShips.get(planet) + " Actual Docked Ships: " + planetsShips.get(planet));
            		if(myPlanetsPlannedShips.get(planet) == planet.getDockingSpots()) {
            			fullPlanets.add(planet);
            		}
            	}
            	Log.log("My Planets Not Full: " + closestPlanetsNotFull.size() + " Empty Planets: " + closestEmptyPlanet.size() + " Full Planets: " + fullPlanets.size());*/
            	
            	//if(miningShips.contains(ship)) {
            	else {
            		Log.log("is a miningShip");
            		if(closestEmptyPlanet.size() != 0 && closestPlanetsNotFull.size() != 0) {
            			Log.log("first branch");
	            		if(closestEmptyPlanet.firstKey() < closestPlanetsNotFull.firstKey()) {
	            			Log.log("Ship " + ship.getId() + " is going to an empty planet");
	            			Planet targetPlanet = closestEmptyPlanet.pollFirstEntry().getValue();
	            			if (ship.canDock(targetPlanet)) {
		                        moveList.add(new DockMove(ship, targetPlanet));
		                        
		                        continue;
		                    }
		                    //This is a navigation command
		                    final ThrustMove newThrustMove = Navigation.navigateShipToDock(gameMap, ship, targetPlanet, Constants.MAX_SPEED);
		                    	if (newThrustMove != null) {
		                    		moveList.add(newThrustMove);
		                    		myPlanetsPlannedShips.put(targetPlanet, myPlanetsPlannedShips.get(targetPlanet) + 1);
		                    	}
		                  
	            		} else if(closestEmptyPlanet.firstKey() >= closestPlanetsNotFull.firstKey()) {
	            			
	            			Planet targetPlanet = closestPlanetsNotFull.pollFirstEntry().getValue();
	            			Log.log("Ship " + ship.getId() + " is going to one of my planets: " + targetPlanet.getId());
	            			if (ship.canDock(targetPlanet)) {
		                        moveList.add(new DockMove(ship, targetPlanet));
		                       
		                        continue;
		                    }
		                    //This is a navigation command
		                    final ThrustMove newThrustMove = Navigation.navigateShipToDock(gameMap, ship, targetPlanet, Constants.MAX_SPEED);
		                    	if (newThrustMove != null) {
		                    		moveList.add(newThrustMove);
		                    		myPlanetsPlannedShips.put(targetPlanet, myPlanetsPlannedShips.get(targetPlanet)+1);
		                    	}
		                   
	            		} else {
	                		Ship targetShip = closestEnemyShips.pollFirstEntry().getValue();
	                    	final ThrustMove newThrustMove = Navigation.navigateShipToDock(gameMap, ship, targetShip, Constants.MAX_SPEED);
	                    	if (newThrustMove != null) {
	                    		moveList.add(newThrustMove);
	                    		//plannedShips.put(ship, plannedShips.get(ship) + 1);
	                    	}
	                    	continue;
	            		}
            		} else {
            			Log.log("2nd branch");
                		if(closestPlanetsNotFull.size() > 0) {
                			Log.log("Ship " + ship.getId() + " is going to one of my planets");
                			Planet targetPlanet = closestPlanetsNotFull.pollFirstEntry().getValue();
                			if (ship.canDock(targetPlanet)) {
    	                        moveList.add(new DockMove(ship, targetPlanet));
    	                       
    	                        continue;
    	                    }
    	                    //This is a navigation command
    	                    final ThrustMove newThrustMove = Navigation.navigateShipToDock(gameMap, ship, targetPlanet, Constants.MAX_SPEED);
    	                    	if (newThrustMove != null) {
    	                    		moveList.add(newThrustMove);
    	                    		 myPlanetsPlannedShips.put(targetPlanet, myPlanetsPlannedShips.get(targetPlanet)+1);
    	                    	}
    	                   
                		} else if (closestEmptyPlanet.size() > 0) {
                			 Log.log("Ship " + ship.getId() + " is going to an empty planet");
                			Planet targetPlanet = closestEmptyPlanet.pollFirstEntry().getValue();
                			if (ship.canDock(targetPlanet)) {
    	                        moveList.add(new DockMove(ship, targetPlanet));
    	                       
    	                        continue;
    	                    }
    	                    //This is a navigation command
    	                    final ThrustMove newThrustMove = Navigation.navigateShipToDock(gameMap, ship, targetPlanet, Constants.MAX_SPEED);
    	                    	if (newThrustMove != null) {
    	                    		moveList.add(newThrustMove);
    	                    		myPlanetsPlannedShips.put(targetPlanet, myPlanetsPlannedShips.get(targetPlanet) + 1);
    	                    	}
    	                   
                		} else {
                			Log.log("Ship " + ship.getId() + " is going to attack");
                			Ship targetShip = closestEnemyShips.pollFirstEntry().getValue();
                        	final ThrustMove newThrustMove = Navigation.navigateShipToDock(gameMap, ship, targetShip, Constants.MAX_SPEED);
                        	if (newThrustMove != null) {
                        		moveList.add(newThrustMove);
                        		//plannedShips.put(ship, plannedShips.get(ship) + 1);
                        	}
                        	
                        	continue;
                        	
                		}
            		}
            	}
            	}
            }
            Networking.sendMoves(moveList);
        }
    }
}
