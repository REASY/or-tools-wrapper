package com.github.beam
import java.io.{BufferedReader, File, FileOutputStream, InputStreamReader}
import java.nio.file.Paths
import java.util.zip.ZipInputStream

object OrToolsLoader {
  private val mac = "mac.zip"
  private val ubuntu18 = "ubuntu-18-04.zip"
  private val ubuntu16 = "ubuntu-16-04.zip"
  private val windows = "windows.zip"

  def load(): Unit = {
    val tempDir = new File(System.getProperty("java.io.tmpdir"))

    tempDir.mkdirs()
    tempDir.deleteOnExit()

    val libraryArchive = getLibraryArchive

    unzipLibArchive(tempDir, libraryArchive)

    // mac lib extension workaround
    val libname = if (libraryArchive == mac) "libjniortools.jnilib" else System.mapLibraryName("jniortools")

    System.load(Paths.get(tempDir.getAbsolutePath, libname).toString)
  }

  private def unzipLibArchive(directory: File, libraryArchive: String): Unit = {
    val zis = new ZipInputStream(getClass.getResourceAsStream("/" + libraryArchive))
    Stream.continually(zis.getNextEntry).takeWhile(_ != null).foreach { file =>
      val fileName = Paths.get(directory.getAbsolutePath, file.getName).toString
      new File(fileName).delete()
      val fout = new FileOutputStream(fileName)
      val buffer = new Array[Byte](1024)
      Stream.continually(zis.read(buffer)).takeWhile(_ != -1).foreach(fout.write(buffer, 0, _))
      fout.close()
      zis.closeEntry()
    }
    zis.close()
  }

  private def getLibraryArchive: String =
    System.getProperty("os.name").toLowerCase match {
      case name if name.contains("win") => windows
      case name if name.contains("mac") => mac
      case name if name.contains("nix") || name.contains("nux") =>
        val version = new BufferedReader(
          new InputStreamReader(new ProcessBuilder("lsb_release", "-rs").start().getInputStream)
        ).readLine()
        if (version.contains("16")) ubuntu16 else ubuntu18

      case other => throw new RuntimeException(s"Os $other is not supported by or-tools-library-wrapper")
    }
}
