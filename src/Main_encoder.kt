import kotlin.math.ceil
import kotlin.math.sqrt
import java.awt.Color
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File

// NULL_BLOCK ASCII value
const val NULL_BLOCK = 26.toChar()

fun getColorChunk(url: String, index: Int): Triple<Int, Int, Int> {
    // Get a chunk of 3 characters, padded with NULL_BLOCK if necessary
    val chunk = url.substring(index, (index + 3).coerceAtMost(url.length)).padEnd(3, NULL_BLOCK_CHAR)

    // Use Char.code to get the Unicode code point (integer value)
    val r = chunk[0].code % 256
    val g = chunk[1].code % 256
    val b = chunk[2].code % 256

    return Triple(r, g, b)
}

fun createSnakingQrGrid(url: String): BufferedImage {
    val urlLength = url.length
    val requiredCells = ceil(urlLength / 3.0).toInt() + 3 // Add 3 for reserved corner cells
    val gridSize = ceil(sqrt(requiredCells.toDouble())).toInt() // Ensure grid is large enough
    val cellSize = 2
    val imgSize = gridSize * cellSize

    val img = BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB)
    val graphics = img.createGraphics()
    graphics.color = Color.WHITE
    graphics.fillRect(0, 0, imgSize, imgSize)

    // Corner markers
    val corners = mapOf(
        Pair(0, 0) to Color(0, 255, 0),
        Pair(0, gridSize - 1) to Color(255, 0, 0),
        Pair(gridSize - 1, 0) to Color(255, 255, 0)
    )

    // Generate snaking coordinates
    val coordinates = mutableListOf<Pair<Int, Int>>()
    for (row in gridSize - 1 downTo 0) {
        if ((gridSize - 1 - row) % 2 == 0) {
            coordinates.addAll((gridSize - 1 downTo 0).map { Pair(row, it) })
        } else {
            coordinates.addAll((0..<gridSize).map { Pair(row, it) })
        }
    }

    // Fill the grid
    var index = 0
    for (coord in coordinates) {
        val (row, col) = coord
        val color: Color = if (corners.containsKey(coord)) {
            corners[coord]!!
        } else {
            if (index < url.length) {
                val (r, g, b) = getColorChunk(url, index)
                index += 3
                Color(r, g, b)
            } else {
                Color(NULL_BLOCK_CHAR.code, NULL_BLOCK_CHAR.code, NULL_BLOCK_CHAR.code)
            }
        }

        val x0 = col * cellSize
        val y0 = row * cellSize
        graphics.color = color
        graphics.fillRect(x0, y0, cellSize, cellSize)
    }

    graphics.dispose()
    return img
}

fun main() {
    println("Enter a URL: ")
    val url = readlnOrNull() ?: return
    val gridImage = createSnakingQrGrid(url)
    ImageIO.write(gridImage, "png", File("null_block_qr_grid.png"))
}