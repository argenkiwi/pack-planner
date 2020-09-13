import java.io.File
import java.io.FileNotFoundException
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

enum class Order {
    NATURAL,
    SHORT_TO_LONG,
    LONG_TO_SHORT
}

data class Item(
        val id: Int,
        val length: Int,
        var quantity: Int,
        val weight: Double
)

data class Pack(
        val totalPieces: Int,
        val totalWeight: Double
)

fun List<Item>.pack(maxPieces: Int, maxWeight: Double) {
    var position = 0;
    var pack = 0;
    do {
        println("Pack Number: ${++pack}")

        var remainingPieces = maxPieces
        var remainingWeight = maxWeight
        var currentLength = 0
        do {
            val item = this[position]

            val piecesToAdd = min(
                    min(remainingPieces, item.quantity),
                    floor(remainingWeight / item.weight).toInt()
            )

            if (piecesToAdd > 0) {
                println("${item.id},${item.length},${piecesToAdd},${item.weight}")
            }

            item.quantity -= piecesToAdd

            position = when {
                item.quantity > 0 -> position
                else -> position + 1
            }

            remainingPieces -= piecesToAdd
            remainingWeight -= piecesToAdd * item.weight
            currentLength = max(currentLength, item.length)
        } while (position < size && item.quantity == 0)

        println("Pack Length: $currentLength, Pack Weight: ${String.format("%.2f", maxWeight - remainingWeight)}")
        println()
    } while (position < size)
}

fun main(args: Array<String>) {
    try {
        val input = File(args.first()).readLines()
        val limitations = input.first().split(',')
        val order = Order.valueOf(limitations[0])
        val maxPieces = limitations[1].toInt()
        val maxWeight = limitations[2].toDouble()

        input.drop(1)
                .map {
                    val params = it.split(',')
                    Item(params[0].toInt(), params[1].toInt(), params[2].toInt(), params[3].toDouble())
                }.let { items ->
                    when (order) {
                        Order.NATURAL -> items
                        Order.LONG_TO_SHORT -> items.sortedByDescending { it.length }
                        Order.SHORT_TO_LONG -> items.sortedBy { it.length }
                    }
                }
                .pack(maxPieces, maxWeight)

    } catch (e: NoSuchElementException) {
        println("Input file path not provided or file is empty.")
    } catch (e: FileNotFoundException) {
        println("File not found.")
    } catch (e: Exception) {
        println("Failed to parse input file.")
    }
}
