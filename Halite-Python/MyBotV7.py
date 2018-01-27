import hlt
import logging
from collections import OrderedDict
game = hlt.Game("Infinitum-V7")
logging.info("Starting SentdeBot")
turnCount = 0

while True:
    game_map = game.update_map()
    num_attackships = int(len(game_map.get_me().all_ships())*.4)

    team_attackships  = game_map.get_me().all_ships()[0:num_attackships]
    mining_ships = game_map.get_me().all_ships()[num_attackships:len(game_map.get_me().all_ships())]
    command_queue = []
    myplanets = []
    full_planets = []
    for ship in game_map.get_me().all_ships():
        shipid = ship.id
        if ship.docking_status != ship.DockingStatus.UNDOCKED:
            # Skip this ship
            continue

        entities_by_distance = game_map.nearby_entities_by_distance(ship)
        entities_by_distance = OrderedDict(sorted(entities_by_distance.items(), key=lambda t: t[0]))
        
        closest_empty_planets = [entities_by_distance[distance][0] for distance in entities_by_distance if isinstance(entities_by_distance[distance][0], hlt.entity.Planet) and not entities_by_distance[distance][0].is_owned() and entities_by_distance[distance][0]]
        team_ships = game_map.get_me().all_ships()
        closest_my_planet_notFull = [entities_by_distance[distance][0] for distance in entities_by_distance if isinstance(entities_by_distance[distance][0], hlt.entity.Planet) and entities_by_distance[distance][0].is_owned() and entities_by_distance[distance][0].owner == ship.owner and not entities_by_distance[distance][0].is_full() and entities_by_distance[distance][0] not in full_planets]
        closest_enemy_ships = [entities_by_distance[distance][0] for distance in entities_by_distance if isinstance(entities_by_distance[distance][0], hlt.entity.Ship) and  entities_by_distance[distance][0] not in team_ships]

        # If there are any empty planets, let's try to mine!
        if len(closest_my_planet_notFull) > 0:
            target_planet = closest_my_planet_notFull[0]
            if ship in team_attackships:
                target_ship = closest_enemy_ships[0]
                navigate_command = ship.navigate(   
                ship.closest_point_to(target_ship),
                game_map,
                speed=int(hlt.constants.MAX_SPEED),
                ignore_ships=False)
                if navigate_command:
                    command_queue.append(navigate_command)
            elif ship.can_dock(target_planet):
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
        
        elif len(closest_empty_planets) > 0:
            target_planet = closest_empty_planets[0]
            if ship in team_attackships:
                target_ship = closest_enemy_ships[0]
                navigate_command = ship.navigate(   
                ship.closest_point_to(target_ship),
                game_map,
                speed=int(hlt.constants.MAX_SPEED),
                ignore_ships=False)
                if navigate_command:
                    command_queue.append(navigate_command)
            elif ship.can_dock(target_planet):
                command_queue.append(ship.dock(target_planet))
            else:
                navigate_command = ship.navigate(
                            ship.closest_point_to(target_planet),
                            game_map,
                            speed=int(hlt.constants.MAX_SPEED),
                            ignore_ships=False)

                if navigate_command:
                    command_queue.append(navigate_command)
            
                
        # FIND SHIP TO ATTACK!
        elif len(closest_enemy_ships) > 0:
            target_ship = closest_enemy_ships[0]
            navigate_command = ship.navigate(
                        ship.closest_point_to(target_ship),
                        game_map,
                        speed=int(hlt.constants.MAX_SPEED),
                        ignore_ships=False)

            if navigate_command:
                command_queue.append(navigate_command)
        

    game.send_command_queue(command_queue)
    # TURN END
# GAME END