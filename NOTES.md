# Development Notes

## Known Issues / Bugs

* When killed by flame while on an elevator, death animation may get stuck ->
  probably because elevating action collides with death fall animation

## To Dos

* Tune speed of movements, jumping, elevators etc.
* Check which elements of the map get restored when level restarts after death (bombs? flames?)

### Gameplay features

* Magnets (https://www.youtube.com/watch?v=DRrW01Hfs1k / 23:06)
** Small magnet icons (left/right): Player picks it up, becomes magnetic for matching magnet
* Trampolins
* Bombs kill flames (only when standing on top of exploding bomb)
* Flames move around

### General

* Pause
* Abort ("Are you sure Y/N")
* Timer ("Energy"): Starts with 99, decreases every second, remaining seconds give score points

### Debugging / Development

* F1: Restart level
* F2: Next level
* F3: Back to Menu
