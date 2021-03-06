package services.path

import java.net.URLDecoder
import java.nio.file.Paths

import models.PathBreadcrumb

object PathService {
  def decodeNameFromUrl(key: String): String =
    URLDecoder.decode(key.replace("+", " "), "utf-8")

  def buildFilePath(path: String, name: String, isFolder: Boolean): String = {
    val decodedName = decodeNameFromUrl(name)
    val decodedPath = decodeNameFromUrl(path)

    path match {
      case "." if isFolder  => Paths.get(decodedName, ".gitkeep").toString
      case "." if !isFolder => decodedName
      case _ if isFolder    => Paths.get(decodedPath, name, ".gitkeep").toString
      case _ if !isFolder   => Paths.get(decodedPath, name).toString
    }
  }

  def getPathWithoutFilename(path: String): String = {
    try {
      Paths.get(path).getParent.toString
    } catch {
      case _: java.lang.NullPointerException => "." // if path is a filename, we are in root directory
    }

  }

  def buildTreeFromPath(path: String, isFile: Boolean = false): Array[PathBreadcrumb] = {
    path match {
      case "." => Array()
      case _ =>
        val fullPath = path.split("/")
        val breadcrumbs = fullPath.zipWithIndex.map {
          case (element, index) =>
            PathBreadcrumb(decodeNameFromUrl(element), fullPath.slice(0, index + 1).mkString("/"))
        }
        if (isFile) {
          breadcrumbs.dropRight(1) // we don't need a file name in path
        } else {
          breadcrumbs
        }
    }
  }

}
