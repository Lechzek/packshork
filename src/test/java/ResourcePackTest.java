import dev.lechzek.packshork.Pack;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ResourcePackTest {

    @Test
    public void test() {

        // Produce pack builder with "noTextWhitespace" argument enabled
        Pack builder = Pack.builder(true);

        // Pack.mcmeta
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

        // Clean up
        builder.cleanUp();

    }
}
