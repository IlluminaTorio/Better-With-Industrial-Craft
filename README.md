# Better With Industrial Craft

A faithful port of IndustrialCraft² v1.00 (Minecraft Beta 1.7.3) to Better Than Adventure 8.0.1.

> "IndustrialCraft 2 loaded. Remember: safety first!"

## Ported from the original

- **Ores and world generation**: copper, tin and uranium ore veins plus rubber trees in forest and swamp biomes, with the exact spawn rates of the original v1.00 (configurable via `WorldGen.*`).
- **Power grid**: a full port of the original `EnergyNet` — path finding, distribution weighted by reverse losses, insulation and conductor breakdown, electric shock damage to entities.
- **Cables**: all 11 cable types (copper / gold / HV / glass fiber / tin, insulated and uninsulated) with the original per-block losses of 0.2–1.0 EU and 16-color dyeing.
- **Machines with GUIs**: iron furnace, electric furnace, macerator, extractor, compressor, canning machine, recycler, electrolyzer, induction furnace, mass fabricator, magnetizer.
- **Generators**: generator, geothermal generator, water mill, solar panel, wind mill, nuclear reactor with chambers.
- **Storage and conversion**: BatBox (40k EU), MFE (600k EU), MFSU (10M EU), LV/MV/HV transformers.
- **Tools and armor**: mining drill, diamond drill, chainsaw, mining laser, nano saber, wrench, EU meter, painters (16 colors), treetap, OD/OV scanners; bronze, rubber, composite, nano and quantum armor, jetpacks, BatPack.
- **Items**: RE battery, energy crystal, Lapotron crystal, cells of all kinds, fuel cans, circuits, carbon materials, industrial diamond, UU-matter and scrap boxes.
- **Everything else from v1.00**: dynamite, industrial TNT, nuke, reinforced stone/glass/doors, iron fence, bronze doors, personal safe, trade-o-mat, teleporter, tesla coil, luminators, terraformer, miner, pump.

## Additions for BTA

- **Ores in every BTA stone type**: copper, tin and uranium generate as variants of stone, basalt, limestone, granite and permafrost, exactly like vanilla BTA ores; ores drop raw items which smelt into ingots or crush into 2x dust.
- **Steel economy**: steel dust as an early path to BTA steel, steel ↔ refined iron interchangeability, maceration of BTA raw ores.
- **Industrial diamond**: fully interchangeable with the vanilla diamond in BTA recipes — tools, armor, wolf armor, diamond block and back, jukebox.
- **Bronze set**: tools, armor, wolf armor, bronze bricks and bronze doors based on recolored BTA iron textures, plus copper bricks.
- **Trommel recipes**: IC2-specific trommel outputs and IC2 items added to vanilla trommel loot tables.
- **Scrap boxes**: drop both BTA items (raw ores, crude steel, pebbles) and IC2 items.
- **BTA recipe book**: all recipes (workbench, furnace, machines) are registered in the recipe registry and show up in the BTA recipe book; machine recipes use a dedicated recipe type and never leak into the vanilla furnace.
- **TMB (Too Many Blocks)**: optional integration — macerator, extractor, compressor, canning machine and mass fabricator recipe categories appear in TMB when it is installed (`gatherTMBPlugins` entrypoint).
- **BTWaila**: optional tooltips for machines, generators, energy storage and the reactor (progress, charge, heat).
- **Signal Industries / Catalyst**: when `signalindustries` is installed, two energy converters (EU ↔ Signal Energy) with their own crafts appear, and the water mill accepts water from SI fluid pipes.
- **Creative mode**: all items and blocks are sorted into the matching BTA creative tabs.

## Multiplayer

The mod works on dedicated servers. Every player needs the same mod set as the server (Fabric Loader, HalpLibe and this mod — keep the versions identical on both sides). Recipes are synchronized from the server to each client on login; the mod verifies this synchronization path on every startup and logs `IC2 dedicated-client recipe sync check: N recipes OK` when it passes.

## Building

The repository is fully self-contained: the Gradle wrapper, the version catalog (`gradle/libs.versions.toml`) and the local dependencies (`libs/*.jar`) are all included. Nothing needs to be downloaded manually.

```bash
./gradlew build        # -> build/libs/ic2-1.0.2+8.0.1.jar
./gradlew runClient    # run the client
./gradlew runServer    # run a dedicated server
```

On Windows: `gradlew.bat build`.

- **Java**: the Gradle daemon runs on Java 21 while the mod itself is compiled with Java 17 (Adoptium). Both JDKs are provisioned automatically (foojay-resolver + daemon toolchain provisioning); any JDK 17+ is enough to get started.
- **Dependencies** (BTA 8.0.1, HalpLibe, Fabric Loader) are pulled from public Maven repositories automatically.
- **TMB / BTWaila / Catalyst** are compileOnly integrations; their jars live in `libs/` (2.4 MB, included in the repository — do not remove).
- **GitHub Actions**: the workflow in `.github/workflows/build.yml` builds the jar on every push and publishes it as an artifact.

## Configuration

`run/config/ic2.cfg` (created on first launch):

- `Gameplay.machineExplosions` — `false` disables the voltage system entirely: machines accept any current (excess is returned to the grid), cables never burn or break down. With `true`, the original IC2 behavior (over-voltage explosions, insulation breakdown, shocks).
- `Gameplay.nuclearMeltdowns` — nuclear reactor meltdown on overheat.
- `WorldGen.*` — ore and rubber tree density.

## Block and item IDs

The mod uses the fixed ranges **1600–1677 for blocks** and **19000–19141 for items**, outside of vanilla BTA, Signal Industries, Retrostorage and other common mods of this ecosystem. The layout is protected by a config marker (`idLayoutVersion = 2`): a config from an older mod version is migrated automatically on the next launch, and duplicate IDs inside a legacy config are self-healed. **A dedicated server and its clients must run the same mod version** — block and item IDs are not synchronized over the network, both sides read them from their own config.

## Version history

- **1.0.2** — fixed a client crash when joining a dedicated server (`Group 'mass_fabricator' in namespace 'ic2' doesn't exist!`): machine recipe groups are now created during namespace init, which is the only path the client runs on login. Added a startup self-check that replays the whole login recipe-sync path and logs `IC2 dedicated-client recipe sync check: N recipes OK` on success.
- **1.0.1** — fixed the server-side disconnect during login (`IllegalArgumentException` in `PacketRecipeSync`): machine recipes now have a proper JSON adapter and a registered recipe type (`ic2:machine`). Moved block IDs to 1600–1677 and item IDs to 19000–19141 to clear conflicts with Signal Industries and Retrostorage; old configs migrate automatically.
- **1.0.0** — initial public release: full port of IndustrialCraft² v1.00 content, BTA ore variants, recipe book integration, TMB / BTWaila / Signal Industries integrations.

## License

IndustrialCraft² v1.00 by Alblaka is released under CC0 1.0 Universal; this port is published under the same license (see `LICENSE`).
