package dev.lechzek.packshork;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class Pack {

    private final WeakHashMap<Path, byte[]> map;
    private final boolean noTextWhitespace;

    private Pack(boolean noWhiteSpace) {
        this.map = new WeakHashMap<>();
        this.noTextWhitespace = noWhiteSpace;
    }

    private void appendText(Path path, String name, String text) {
        if (noTextWhitespace) text = text.replaceAll("\\s+", "");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.writeBytes(text.getBytes(StandardCharsets.UTF_8));
        map.put(path.resolve(name), stream.toByteArray());
        try {
            stream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void appendBytes(Path path, byte[] bytes) {
        map.put(path, bytes);
    }

    private void appendFile(Path path, Path file) {
        try {
            if (Files.isDirectory(file)) {
                Files.walk(file).forEach(it -> {
                    if (!Files.isDirectory(it)) {
                        try {
                            map.put(path.resolve(it.subpath(file.getNameCount()-1, it.getNameCount())), Files.readAllBytes(it));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            } else map.put(path.resolve(file.getName(file.getNameCount()-1)), Files.readAllBytes(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void appendDir(Path root, String path, Consumer<Directory> lambda) {
        lambda.accept(new Directory(root.resolve(path)));
    }

    public final class Directory {

        private final Path path;
        Directory(Path path) {
            this.path = path;
        }

        public void file(File file) {
            appendFile(this.path, file.toPath());
        }

        public void file(Path file) {
            appendFile(this.path, file);
        }

        public void text(String name, String text) {
            appendText(this.path, name, text);
        }

        public void bytes(String name, byte[] bytes) {
            appendBytes(this.path, bytes);
        }

        public void dir(String path, Consumer<Directory> lambda) {
            appendDir(this.path, path, lambda);
        }
    }

    public Pack dir(String path, Consumer<Directory> lambda) {
        appendDir(Path.of(""), path, lambda);
        return this;
    }

    public Pack text(String name, String text) {
        appendText(Path.of(""), name, text);
        return this;
    }

    public Pack bytes(byte[] bytes) {
        appendBytes(Path.of(""), bytes);
        return this;
    }

    public Pack file(Path file) {
        appendFile(Path.of(""), file);
        return this;
    }

    public static Pack builder() {
        return new Pack(false);
    }

    public static Pack builder(boolean noTextWhitespace) {
        return new Pack(noTextWhitespace);
    }

    public ByteArrayOutputStream build() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(stream);
        map.forEach((k, v) -> {
            try {
                ZipEntry entry = new ZipEntry(k.toString());
                entry.setCreationTime(FileTime.from(Instant.MIN));
                entry.setTime(0);
                entry.setTimeLocal(LocalDateTime.MIN);
                entry.setLastAccessTime(FileTime.from(Instant.MIN));
                entry.setLastModifiedTime(FileTime.from(Instant.MIN));
                entry.setComment(null);
                entry.setExtra(null);
                zip.putNextEntry(entry);
                zip.write(v);
                zip.closeEntry();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            zip.finish();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stream;
    }

    public void build(Path outputPath) {
        map.forEach((k,v) -> {
            try {
                Files.createDirectories(outputPath.resolve(k).getParent());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                if (Files.exists(outputPath.resolve(k))) Files.write(outputPath.resolve(k), v, StandardOpenOption.TRUNCATE_EXISTING) ;else Files.write(outputPath.resolve(k), v, StandardOpenOption.CREATE);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void cleanUp() {
        map.clear();
    }

}