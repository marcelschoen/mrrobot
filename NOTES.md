# Development Notes

## Known Issues / Bugs

* When killed by flame while on an elevator, death animation may get stuck ->
  probably because elevating action collides with death fall animation

## To Dos

* Implement correct key presses / touchscreen support for all screens (touchscreen on menu)
* Fix joypad button mappings
* Tune speed of movements, jumping, elevators etc.
* Check which elements of the map get restored when level restarts after death (bombs? flames?)

### Gameplay features

* Trampolins

### General

* Attract mode (rotate display of levels until key press)
* Pause
* Abort ("Are you sure Y/N")
* Timer ("Energy"): Starts with 99, decreases every second, remaining seconds give score points

### Debugging / Development

* F1: Restart level
* F2: Next level
* F3: Back to Menu
