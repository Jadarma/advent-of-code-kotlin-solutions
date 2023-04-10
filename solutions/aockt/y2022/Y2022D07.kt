package aockt.y2022

import io.github.jadarma.aockt.core.Solution
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.div

object Y2022D07 : Solution {

    /**
     * A filesystem node.
     * @property path The absolute path to this node.
     * @property size The total size on disk of this node.
     */
    private sealed interface Node {
        val path: Path
        val size: Long
    }

    /** A file node, but file contents are not implemented because the elves don't pay me enough. */
    private data class File(
        override val path: Path,
        override val size: Long,
    ) : Node

    /**
     * A filesystem node that can contain other nodes.
     * @property parent The parent directory or `null` if this is the filesystem root.
     */
    private data class Directory(
        override val path: Path,
        private val parent: Directory? = null,
    ) : Node {

        init {
            require(path.toString() == "/" || (parent != null && path.startsWith(parent.path))) {
                "Path $path not a valid child of $parent"
            }
        }

        /** The total size on disk of this directory, including recursive contents. */
        override var size: Long = 0L
            private set

        private val _children: MutableMap<String, Node> = mutableMapOf()

        /** All direct child nodes of this directory. */
        val children: Iterable<Node> = _children.values

        /** Returns a lazy sequence of directories in a depth first search traversal. */
        fun walk(): Sequence<Directory> = sequence {
            yield(this@Directory)
            children.filterIsInstance<Directory>().forEach { yieldAll(it.walk()) }
        }

        /** Create a new [File] in this [Directory] with the given [fileName] and [fileSize]. */
        fun touch(fileName: String, fileSize: Long): File {
            require(fileName !in _children)
            return File(path / fileName, fileSize).also {
                _children[fileName] = it
                this.size += fileSize
                var dir: Directory? = this
                while (dir?.parent != null) dir = dir.parent?.also { parent -> parent.size += fileSize }
            }
        }

        /** Create a new empty directory with the given [dirName]. */
        fun mkdir(dirName: String): Directory {
            require(dirName !in _children)
            return Directory(path / dirName, this).also { _children[dirName] = it }
        }

        /** Accepts a relative [subPath] and returns the associated [Node], or throws if not found. */
        fun resolve(subPath: String): Node = when (val nextDir = subPath.substringBefore('/')) {
            "" -> this
            ".." -> (parent ?: this).resolve(subPath.substringAfter(nextDir).trimStart('/'))
            subPath -> _children[subPath]!!
            else -> _children[nextDir] ?: error("$path does not contain $nextDir")
        }
    }

    /** Given the CLI output of a manual filesystem exploration, reconstructs a virtual replica of the structure. */
    private fun reconstructFilesystem(input: List<String>): Directory {
        val cdPrefix = "$ cd "
        val lsPrefix = "$ ls"
        val lsOutputRegex = Regex("""^(\d+|dir) (\S+)$""")
        val virtualFSRoot = Directory(Path("/"))
        var node: Directory = virtualFSRoot

        val reader = input.iterator()
        while (reader.hasNext()) {
            val line = reader.next()
            when {
                line.startsWith(lsPrefix) -> Unit
                line.startsWith(cdPrefix) -> node = when (val subPath = line.substringAfter(cdPrefix)) {
                    "/" -> virtualFSRoot
                    else -> node.resolve(subPath) as Directory
                }

                else -> {
                    val (size, file) = lsOutputRegex.matchEntire(line)!!.destructured
                    when (size) {
                        "dir" -> node.mkdir(file)
                        else -> node.touch(file, size.toLong())
                    }
                }
            }
        }

        return virtualFSRoot
    }

    override fun partOne(input: String) = reconstructFilesystem(input.lines())
        .walk()
        .filter { it.size <= 100_000 }
        .sumOf { it.size }

    override fun partTwo(input: String): Long {
        val filesystem = reconstructFilesystem(input.lines())
        val freeSpace = 70_000_000L - filesystem.size

        return filesystem
            .walk()
            .filter { freeSpace + it.size >= 30_000_000L }
            .minOf { it.size }
    }
}
