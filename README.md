# utilrun
### Minecraft utility mod engine
### Fabric 1.21
### Default command prefix: ```>>```

## How to:
1. Download the stable version of utilrun in [releases](https://github.com/ZimnyCat/utilrun/releases) or clone this repo
2. [Set up a development environment](https://fabricmc.net/wiki/tutorial:setup)
3. Create an **util** (ex: [ExampleUtil](https://github.com/ZimnyCat/utilrun/blob/main/src/main/java/zimnycat/utilrun/utils/ExampleUtil.java)) in **utils** folder or a **command** (ex: [ExampleCmd](https://github.com/ZimnyCat/utilrun/blob/main/src/main/java/zimnycat/utilrun/commands/ExampleCmd.java)) in **commands** folder
4. Add instances of new utils(s) **/** command(s) to the ```onInitialize()``` method in the main class (```base.Utilrun```)
5. Use ```gradlew build``` when you're done to build the mod
