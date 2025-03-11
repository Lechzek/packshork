<h1 align="center">
  <br>
  <img src="images/packshork.png" alt="packshork logo" width="256">
  <br>
</h1>

___

<h3 align="center">Simple Java tool for generating minecraft resource / data packs.</h3>

___

[![](https://jitpack.io/v/Lechzek/packshork.svg)](https://jitpack.io/#Lechzek/packshork)

## Features

- DSL-like builder style using lambdas
- Support for automatic whitespace removal in text files
- No other dependencies involved, only vanilla JDK 

## Code examples

```java
Pack builder = Pack.builder();

builder.text("pack.mcmeta", """
{
  "pack": {
    "description": "This is the description of your resource pack",
    "pack_format": 18,
    "supported_formats": [18, 46]
  }
}
""").dir("assets/minecraft", minecraft -> minecraft.dir("models/item", items -> items.text("torch.json", """
{
  "parent": "item/generated",
  "textures": {
    "layer0": "block/torch"
  },
  "display": {
    "thirdperson_righthand": {
      "rotation": [ -90, 0, 0 ],
      "translation": [ 0, 1, -3 ],
      "scale": [ 0.55, 0.55, 0.55 ]
    },
    "firstperson_lefthand": {
      "rotation": [ 0, -135, 25 ],
      "translation": [ 0, 4, 2 ],
      "scale": [ 1.7, 1.7, 1.7 ],
      "scale": [ 0.9, 0.9, 0.9 ]
    }
  }
}
""")));


// Build resource pack to a zip stream and write it to a path
try {
    Files.write(Path.of("./generated.zip"), builder.build().toByteArray(), StandardOpenOption.CREATE);
} catch (IOException e) {
    throw new RuntimeException(e);
}
// Or write directly into a path, without compressing
builder.build(Path.of("./generated"));

```

Optimizing with [PackSquash](https://github.com/ComunidadAylas/PackSquash)
```java
...
builder.build(Path.of("./generated"))

ProcessBuilder pB = new ProcessBuilder("./packsquash", "options.toml");
pB.start();
```
