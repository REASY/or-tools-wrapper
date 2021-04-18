package com.github.beam;

import java.io.*;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class OrToolsLoader {
    private static final String mac = "mac.zip";
    private static final String ubuntu18 = "ubuntu-18-04.zip";
    private static final String centOS8 = "centos-8.zip";
    private static final String windows = "windows.zip";

    public static void load() throws IOException {
        File tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "or-tools").toFile();
        tempDir.mkdirs();
        tempDir.deleteOnExit();

        String libraryArchive = getLibraryArchive();
        unzipLibArchive(tempDir, libraryArchive);

        String libname = (libraryArchive.equals(mac) || libraryArchive.equals(ubuntu18)) ? "libortools" : System.mapLibraryName("jniortools");
        System.load(Paths.get(tempDir.getAbsolutePath(), libname).toString());
    }

    private static void unzipLibArchive(File directory, String libraryArchive) {
        try (ZipInputStream zis = new ZipInputStream(Objects.requireNonNull(OrToolsLoader.class.getResourceAsStream("/" + libraryArchive)))) {
            ZipEntry entry = zis.getNextEntry();
            while (entry != null) {
                String fileName = Paths.get(directory.getAbsolutePath(), entry.getName()).toString();

                new File(fileName).delete();

                try (FileOutputStream fout = new FileOutputStream(fileName)) {
                    byte[] buffer = new byte[1024];
                    int bufferSize = zis.read(buffer);
                    while (bufferSize != -1) {
                        fout.write(buffer, 0, bufferSize);
                        bufferSize = zis.read(buffer);
                    }
                }

                zis.closeEntry();
                entry = zis.getNextEntry();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getLibraryArchive() throws IOException {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) return windows;
        else if (osName.contains("mac")) return mac;
        else if (osName.contains("nix") || osName.contains("nux")) {
            InputStreamReader sr = new InputStreamReader(new ProcessBuilder("lsb_release", "-ir").start().getInputStream());
            BufferedReader bf = new BufferedReader(sr);
            String rawDistro = bf.readLine();
            Optional<String> maybeDistro = getDistro(rawDistro);
            if (!maybeDistro.isPresent()) {
                throw new RuntimeException(String.format("Could not derive distro from '%s'. Check the result of `lsb_release -ir`", rawDistro));
            }
            String rawVersion = bf.readLine();
            Optional<String> maybeVersion = getVersion(rawVersion);
            if (!maybeVersion.isPresent()) {
                throw new RuntimeException(String.format("Could not derive distro version from '%s'. Check the result of `lsb_release -ir`", rawVersion));
            }
            String distro = maybeDistro.get();
            String version = maybeVersion.get();
            if (distro.equals("Ubuntu")) {
                if (version.contains("18")) return ubuntu18;
                else
                    throw new RuntimeException(String.format("Ubuntu version %s is not supported by or-tools-library-wrapper", version));
            }
            else if (distro.contains("CentOS")) {
                if (version.contains("8")) return centOS8;
                else
                    throw new RuntimeException(String.format("CentOS version %s is not supported by or-tools-library-wrapper", version));
            }
            else {
                throw new RuntimeException(String.format("Linux %s version %s is not supported by or-tools-library-wrapper", distro, version));
            }
        }
        else {
            throw new RuntimeException(String.format("OS %s is not supported by or-tools-library-wrapper", osName));
        }
    }

    private static Optional<String> getDistro(String rawDistro) {
        int idx = rawDistro.indexOf("Distributor ID:");
        if (idx >= 0) {
            return Optional.of(rawDistro.substring(idx).trim());
        }
        else {
            return Optional.empty();
        }
    }

    private static Optional<String> getVersion(String rawVersion) {
        int idx = rawVersion.indexOf("Release:");
        if (idx >= 0) {
            return Optional.of(rawVersion.substring(idx).trim());
        }
        else {
            return Optional.empty();
        }
    }
}