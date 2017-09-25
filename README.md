Tickle
======

A cross platform 2D game engine written in Kotlin, using LWJGL for high performance OpenGL graphics.

I have Android support in mind, but I'm concentrating on the desktop for now. iOS will never be supported by Tickle.

This is my 3rd time I've written a game engine. The previous one (called Itchy) was pretty damn good,
but I have bigger plans for Tickle!

The target audience for Tickle are people who are already familiar with programming.

The Aim
-------

# Fun : The primary aim is to make writing games as enjoyable as possible.
# Quick : No need for boiler plate code, such as loading resources. That is all done automatically.
# Easy : No knowledge of OpenGL required, and in fact most games won't need ANY drawing routines.
# Flexible : There should be very few limits on the type of 2D game. Horizontal / Vertical Scrollers, Isometric, Platform games are all covered.
# High Performance : I want to create good games that run well on my crappy and very out-of-date laptop! So redrawing should be QUICK.

I've not decided on the language for the game scripts yet. Given that I really like statically typed languages, I may use Kotlin
for the game scripts, but I may support Groovy too.

Resources Editor
----------------

Tickle includes a GUI to manage resources, such as graphics and sounds etc. But it also let's you edit custom properties
unique to your game.

For example a shoot-em-up game may have an object called "Ship", but this ship can have different "Costumes".
Not only will each costume look different, but it can have different properties, such as the speed of the ship, its maximum health etc.


Scene Editor
------------

It will have a built-in scene editor, allowing game levels to be created quickly. Saving and loading scenes is done for you.

As with the Resources Editor, custom properties are used. For example, keys and doors can share a property, so that picking up a key
opens a particular door (or a set of doors).



Why is it called Tickle?
------------------------

I was very impressed by Scratch, the game engine designed by MIT for kids to learn how to program. However, Scratch was very limited,
and even worse, it encouraged very bad programming practices. So I created "Itch", written in Python, and I soon realised that I hated Python,
so I created Itchy, written in Java, with game scripts in Groovy or Jython. Tickle is the next generation.

BTW, Itch and Itchy were target at programming novices (but you still had to understand programming - no GUI blocks like Scratch).
Tickle is aimed at a more advanced user, but I don't think that will change the design too much, because of my requirements for
"fun", "quick" and "easy".

Current Status
--------------

Very early development - I've barely scratched the surface!

