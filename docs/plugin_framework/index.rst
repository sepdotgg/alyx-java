Writing Plugins for Alyx
========================

Alyx provides a plugin framework built  on pf4j_ which allows users to develop
and create their own plugins and extensions to Alyx. These extensions can provide
features ranging from additional bot commands or Discord event listeners to
facilitate basically any type of functionality you could want or expect from
a Discord bot. Moderation, automation, music, and memes -- the sky is the limit.

Plugins are written in Java, but in theory should work with Kotlin as well.

Getting Started
===============

Requirements
^^^^^^^^^^^^

* **Java 11+** - Alyx is built and tested with OpenJDK 11, 13, and 14.

This documentation assumes that you are familiar with the basics of Java
programming and have your development environment set up for compiling
Java applications. Some parts of the documentation might go into more
detail surrounding packaging (manifests).

Your First Plugin
^^^^^^^^^^^^^^^^^

By being built on top of pf4j_, Alyx's plugin framework provides "extension points"
which can be loaded at runtime into the bot. There are two things required to define
an Alyx Plugin:

* Implement the ``AlyxPlugin`` interface.
* Mark your plugin with pf4j's ``@Extension`` annotation.

That's it. Alyx will take care of the rest. Now, of course, you need to define
code in your plugin that actually *does* something. Let's start with a very simple example.
The following plugin defines a bot command ``ping`` that responds with ``Pong!`` in the
same channel.

.. code-block:: java

   @Extension
   public class PingCommandPlugin extends StatelessAlyxPlugin {
       private static final String NAME = "PingCommandPlugin";
       private static final long PLUGIN_SERIAL = 93227548547886L;

       public PingCommandPlugin(final Alyx alyx) {
           super(NAME, PLUGIN_SERIAL, false, alyx);
       }

       @Command(name = "ping")
       public void ping(final MessageReceivedEvent event) {
           event.getChannel().sendMessage("Pong!").queue();
       }
   }

Let's break this down. As explained above, we need to mark our plugin with
pf4j's ``@Extension`` annotation in order to load it at runtime. Our
``PingCommandPlugin`` class extends the ``StatelessAlyxPlugin`` abstract class.
We will talk about this more in detail later when we get to the plugin data
persistence section, but what you need to know now is "Stateless" in this
context means that this plugin does not require any data to persist
between plugin reloads or bot restarts. ``StatelessAlyxPlugin`` implements the
requied ``AlyxPlugin`` interface for us.

Second, we define a ``NAME`` for our plugin. Give the plugin a friendly name
which can be surfaced to the bot admin. Next, define a ``SERIAL`` for the bot.
The serial is a way to uniquely identify a plugin in the event that there's
a conflict with another plugin. One recommended value to put here is your
Discord user's snowflake ID, so that you effectively get your own "namespace" to
yourself across all Alyx plugins.

Our plugin's constructor must **exclusively** accept ``Alyx`` as a parameter.



.. _pf4j: https://github.com/pf4j/pf4j