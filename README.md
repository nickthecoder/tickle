Tickle
======

A cross platform 2D game engine written in Kotlin, using LWJGL for high performance OpenGL graphics.

This is my 3rd game engine, and I'm very pleased with the result.
The previous one (called Itchy) was pretty damn good, but Tickle is much better!

The Aim
-------

- Fun : The primary aim is to make writing games as enjoyable as possible.
- Quick : No need for boiler plate code, such as loading resources. That is all done automatically.
- Easy : No knowledge of OpenGL required, and in fact most games won't need ANY drawing routines.
- Flexible : There should be very few limits on the type of 2D game. Horizontal / Vertical Scrollers, Isometric, Platform games are all covered.
- High Performance : Ability to create good games that run well on my crappy and very out-of-date laptop, which has very basic graphics hardware.

I think Tickle scores 4 out of 5 (it isn't as flexible as I'd like yet).
I can knock up an quick demo game in less than a day using Tickle. It really is fun, quick and easy, and I've yet
to find a performance bottle neck for the kind of games that I enjoy writing.

The current target audience for Tickle are people who are already familiar with programming.
Tickle is capable of writing top-quality indie style games.
It will NEVER be suitable for triple-A games,
not least because triple-A games tend to use 3D models (which Tickle will never support).

At a later date, I hope to create a simplified environment similar to
[Scratch](https://en.wikipedia.org/wiki/Scratch_(programming_language)),
suitable for complete novices (including pre-teens).

I also plan on offering a gentle transition from novice to expert programmers.
Scratch is great until you out-grow it, at which point your only option is to start again (from scratch),
using a completely new game engine. Surely this leads many (most??) to give up.

The first stage in this transition is to convert the "Scratch" diagram into
a "real", text based programming language at the press of a button (I'll probably choose Groovy, or maybe Jython)
I took this idea from the [Alice game engine](https://en.wikipedia.org/wiki/Alice_(software)).
This feature shows children (or novice adults) that while dragging and dropping blocks,
they have really been writing code, but didn't know it! How wonderful!

At this stage, the code will still be quite limited, offering only a sub-set of the power of Tickle.
Each script can contain endless loops (just as Scratch does).
Tickle will run each script in their own thread (so that endless loops aren't problematic).
Synchronization is hidden behind the scenes, so that the nastiness of multi-threading is avoided.
Note, this won't really be multi-threaded (because all bar one thread will be blocking).
It will be easy to code, at the expense of performance and flexibility.

The next stage will continue using Groovy, but will reveal the full power of Tickle.
The game logic will be harder to write, but will run faster.
The fake multi-threading is gone, and therefore endless loops are forbidden.
Instead each game object has a "tick" method, which is called once per frame.
This is quite a tricky mental leap. Hopefully the additional features will give enough encouragement to
struggle through.

The final stage is to use Kotlin for all, or part of your game.
This gives the flexibility to re-implement any of Tickle's interfaces to suit your game's particular needs.
Allowing you to do things which Tickle doesn't directly support. The world if your oyster!
For example, create a game using isometric views (which Tickle doesn't yet support out of the box).

At any of these stages, you can switch from using the simplified editor to the full version.
Offering the more complex features, such as "costume events" and "layouts" for multiple views per scene.


Resources Editor
----------------

Tickle includes a GUI to manage resources, such as graphics and sounds etc.
Loading and saving resources is done automatically (saved as simple json strings)

It also let's you edit custom properties unique to your game.
For example a shoot-em-up game may have an object called "Ship", but this ship can have different "Costumes".
Not only will each costume look different, but it can have different properties,
such as the speed of the ship, its maximum health etc.


Scene Editor
------------

Tickle has a built-in scene editor, allowing game levels to be created quickly.
Saving and loading scenes is done for you. (They are saved a simple json strings).

As with the Resources Editor, objects placed in a scene can have custom properties.
For example, keys and doors can share a property, so that picking up a key
opens a particular door (or a set of doors).

You could also use custom properties within the Scene Editor so that every ship has their own speed and maximum health etc.
(though it is more common to do so per Costume, from within the Resources Editor).


Compiling
---------

First, ensure that you have a suitable Java JDK, including JavaFX, as well as Gradle and git

For Debian Linux (as root), you can use the following command :

    apt-get install openjdk-8-jdk libopenjfx-java gradle git

(I wish installing code in Windows was as easy or as safe).

Tickle depends on another of my projects (called ParaTask).
Alas, ParaTask isn't in any gradle/maven repositories yet. Sorry.
So download the latest version, and compile it :

    git clone https://github.com/nickthecoder/paratask.git
    cd paratask
    gradle install

Note. 'gradle install' will install the compiled paratask jar files into the **local** maven repository.

Now we can compile Tickle :

    git clone https://github.com/nickthecoder/tickle.git
    cd tickle
    gradle install installApp

Note. 'gradle install installApp' will install Tickle into the **local** maven repository, and build the application.

You can now run the (crappy) demo :

    build/install/tickle/bin/tickle


To run the editor :

    build/install/tickle/bin/tickle --editor


Structure
---------

Tickle is split into modules...

**tickle-core** contains everything needed during actual game-play

**tickle-editor** contains additional classes needed for the editor (to edit game resources, and game leveals (aka Scenes).

**tickle-groovy** Adds support for groovy to be used as a scripting language for game development

**tickle-kotlin** Adds support for kotlin to be used as a scripting language for game development.
However, I'm not sure I'll continue using Kotlin as a scripting language.
It is slow. It requires a bodge to get the Class defined in the script.
Worst of all, it doesn't support setting a path, and therefore I don't think there's a good way to
allow a base class to be defined in one file, and use that base class from another file.

The top-level **tickle** module contains a demo game (which doesn't have any decent game play).
It helps me test things as I develop Tickle.
It is also useful for you, to check that compiling tickle worked!
It may also be helpful as example code.

Your own games should depend on *tickle-core* and *tickle-editor*, but not *tickle* itself (as it doesn't need any of that
demo code).

Why is it called Tickle?
------------------------

I was very impressed by [Scratch](https://en.wikipedia.org/wiki/Scratch_(programming_language)),
the game engine designed by MIT for children to learn how to program.

However, Scratch was very limited, and even worse, it encouraged very bad programming practices.
So I created "Itch", written in Python, and I soon realised that I hated Python.

I moved onto "Itchy", written in Java, with game scripts in Groovy or Jython.
Itchy has its own problems though, the biggest was that the backend used an outdated version of SDL.
Rewriting from scratch (no pun intended) would be quicker than refactoring scratch to use a modern backend.

Thus Tickle was born, written in my new favourite language, Kotlin. Gosh Kotlin is good, I cannot praise it enough.

Writing a Game
--------------

First, use the "new game wizard" :

    cd tickle
    build/install/tickle/bin/tickle --new

You can test your new game (if you called it foo) :

    cd foo
    build/install/foo/bin/foo

Hopefully a window will appear, with a boring black contents.

Now start up your favourite IDE, and get writing.
Sorry, I haven't written any documentation yet. I think your best bet is to look the demo code inside Tickle,
or look at a complete game, such as [Rapid Rag Doll](https://github.com/nickthecoder/rapidragdoll).

You can recompile at any time :

    gradle installApp

Start the editor :

    build/install/foo/bin/foo --editor

Run the game :

    build/install/foo/bin/foo

You can of course perform these actions from within your IDE (that's what I do).
But as these steps are different depending on the IDE you choose, I only show the command line version here.

Current Status
--------------

All of the key features have been written, but there is still a huge number of features that I'd like to add.

I've not found any bugs for quite a while now (but I'm sure you can find some for me to fix ;-)

Documentation is sparse / non-existent. Sorry.
If you contact me, that will help spur me on to do this less than glamourous work.

For the type of games I intend to write, I've had no problems with performance, even on my clunky old laptop,
whose graphics card doesn't work for most 3d games I've thrown at it.

The Scene Editor is a little rough in places, but no show-stoppers AFAIK.

I've only tested it on Linux. I hope it will work on Windows and MacOS, but I've never tried.
I don't own a windows box or a Mac. Feel free to send me an old Mac if you have one gathering dust.
I don't want a windows machine though! The thought of reverting to a machine with a C: drive
gives me nightmares. Do you know that it's called "C:" because "A:" and "B:" are reserved floppy disk drives!?
Do you still have to press the "start" button to stop the machine?
FYI I gave up on windows back in the Windows 98 era, so maybe things have improved.

I've written my first Tickle-powered, fully-featured game (called [Rapid Rag Doll](https://github.com/nickthecoder/rapidragdoll)).
I think its quite slick (though not finished - no sound, and not enough levels).

Planned Additional Features
---------------------------

Have a look in the todo.txt for a bucket load of ideas.


Notable Limitations
-------------------

No support for destructive changes to game sprites (e.g. for games such as Lemmings).

No out-of-the-box support for isometric (or other 2.5D games), such as Age of Empires I and II.
However, a competent programmer could implement their own StageView, so I don't see this as a show-stopper.

I have no idea how robust my support for game pads is. I've barely tested it. Sorry.

No support for scaling windowed games.
If you have a high res monitor, and a low res game, then in windowed mode (i.e. not full screen),
the window will be small.
Maybe you would be kind enough to send me a few quid to put towards a new monitor if this bothers you ;-)

Rendering is simplistic. There's no "fog-of-war", nor other more specialised rendering features
(such as outlining objects hidden behind buildings as seen in Age of Empires I and II).
It is *possible* to implement these using Tickle, but you will need to get your hands dirty, talking to OpenGL,
and that breaks two of my key ideals. It isn't easy or fun!

No path-finding. You could write your own, but this is such a common feature for certain game genres,
that Tickle should do much of the hard work for you.

No Multi-threading.
Tickle is NOT thread safe.
My current thinking is to keep most of Tickle like this.
This may sound crazy in the world of multi-cores which aren't getting faster.
However, debugging a multi-threaded game isn't fun, nor easy, so it goes against two of my ideals.

I may introduce multi-threading to a limited extent.
For example, I could render the scene in a separate thread from the game logic's thread.
e.g. by caching the position and other states of each game object at the start of each frame,
and then let the renderer and game logic run run in parallel.
Note, using synchronized methods rather than caching isn't as easy as it sounds. I don't want to game logic
to have to deal with synchronization, and doing it externally to the game logic will either cause too much blocking
(and therefore make the process essentially serial), or a scene could render incorrectly.
e.g. if a piece of game logic moves two related Actors by the same amount,
then the renderer may render them after the first is changed, but before the second is changed.
This would show them the wrong distance apart (e.g. a head disjoint from the body).
That's hard to debug, and impossible to fix. Not fun or easy!
I've yet to run into performance problems, even on old hardware. If it aint broke, don't fix it.

I may allow CPU intensive tasks, such as route finding to be run in separate threads.
But these threads will be forbidden from changing the state of the game objects.

In this way, a four core machine could be saturated much of the time without giving up my ideals
of fun and easy to write.

