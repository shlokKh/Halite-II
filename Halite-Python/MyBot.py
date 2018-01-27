import hlt
import logging
from collections import OrderedDict
game = hlt.Game("Infinitum-V12")
logging.info("Starting SentdeBot")

while True:
    game_map = game.update_map()
    command_queue = []
    attack_ships =  game_map.get_me().all_ships()[0:int(len(game_map.get_me().all_ships())*.5)]
    mining_ships =  game_map.get_me().all_ships()[int(len(game_map.get_me().all_ships())*.5):]
    full_planets = []
    for ship in game_map.get_me().all_ships():
        
        if ship.docking_status != ship.DockingStatus.UNDOCKED:
            continue

        entities_by_distance = game_map.nearby_entities_by_distance(ship)
        entities_by_distance = OrderedDict(sorted(entities_by_distance.items(), key=lambda t: t[0]))
        
        
        team_ships = game_map.get_me().all_ships()
        closest_enemy_ships = [entities_by_distance[distance][0] for distance in entities_by_distance if isinstance(entities_by_distance[distance][0], hlt.entity.Ship) and  entities_by_distance[distance][0] not in team_ships]

        if ship in attack_ships:
            target_ship = closest_enemy_ships[0]
            navigate_command = ship.navigate(   
            ship.closest_point_to(target_ship),
            game_map,
            speed=int(hlt.constants.MAX_SPEED),
            ignore_ships=False)
                
            if navigate_command:
                command_queue.append(navigate_command)
        
        closest_empty_planets = [entities_by_distance[distance][0] for distance in entities_by_distance if isinstance(entities_by_distance[distance][0], hlt.entity.Planet) and not entities_by_distance[distance][0].is_owned() and entities_by_distance[distance][0]]
        closest_my_planet_notFull = [entities_by_distance[distance][0] for distance in entities_by_distance if isinstance(entities_by_distance[distance][0], hlt.entity.Planet) and entities_by_distance[distance][0].is_owned() and entities_by_distance[distance][0].owner == ship.owner and not entities_by_distance[distance][0].is_full() and entities_by_distance[distance][0] not in full_planets]
        largest_empty_planets = [planet.radius for planet in closest_empty_planets]

        if ship in mining_ships:
            if len(closest_my_planet_notFull) > 0:
                target_planet = closest_my_planet_notFull[0]
                if ship.can_dock(target_planet):
                    command_queue.append(ship.dock(target_planet))
                else:
                    navigate_command = ship.navigate(
                                ship.closest_point_to(target_planet),
                                game_map,
                                speed=int(hlt.constants.MAX_SPEED),
                                ignore_ships=False)

                    if navigate_command:
                        command_queue.append(navigate_command)
                        full_planets.append(target_planet)
            else:
                if len(full_planets) == 0:
                    target_planet = max(planet.radius for planet in game_map.all_planets())
                    if ship.can_dock(target_planet):
                        command_queue.append(ship.dock(target_planet))
                    else:
                        navigate_command = ship.navigate(
                        ship.closest_point_to(target_planet),
                        game_map,
                        speed=int(hlt.constants.MAX_SPEED),
                        ignore_ships=False)

                        if navigate_command:
                            command_queue.append(navigate_command)

                else:
                    target_planet = closest_empty_planets[0]
                
                    if ship.can_dock(target_planet):
                        command_queue.append(ship.dock(target_planet))
                    else:
                        navigate_command = ship.navigate(
                        ship.closest_point_to(target_planet),
                        game_map,
                        speed=int(hlt.constants.MAX_SPEED),
                        ignore_ships=False)

                        if navigate_command:
                            command_queue.append(navigate_command)
            
    game.send_command_queue(command_queue)
    # TURN END
# GAME END