import java.awt.geom.Point2D
import java.util.*

/**
 * Made with love by AntiSquid, Illedan and Wildum.
 * You can help children learn to code while you participate by donating to CoderDojo.
 **/
fun main(args: Array<String>) {
    val input = Scanner(System.`in`)
    team = input.nextInt()
    val bushAndSpawnPointCount = input.nextInt() // useful from wood1, represents the number of bushes and the number of places where neutral units can spawn
    for (i in 0 until bushAndSpawnPointCount) {
        val entityType = input.next() // BUSH, from wood1 it can also be SPAWN
        val x = input.nextInt()
        val y = input.nextInt()
        val radius = input.nextInt()
    }
    val itemCount = input.nextInt() // useful from wood2
    for (i in 0 until itemCount) {
        val itemName = input.next() // contains keywords such as BRONZE, SILVER and BLADE, BOOTS connected by "_" to help you sort easier
        val itemCost = input.nextInt() // BRONZE items have lowest cost, the most expensive items are LEGENDARY
        val damage = input.nextInt() // keyword BLADE is present if the most important item stat is damage
        val health = input.nextInt()
        val maxHealth = input.nextInt()
        val mana = input.nextInt()
        val maxMana = input.nextInt()
        val moveSpeed = input.nextInt() // keyword BOOTS is present if the most important item stat is moveSpeed
        val manaRegeneration = input.nextInt()
        val isPotion = input.nextInt() // 0 if it's not instantly consumed
    }

    // game loop
    while (true) {
        val gold = input.nextInt()
        val enemyGold = input.nextInt()
        val roundType = input.nextInt() // a positive value will show the number of heroes that await a command
        val entityCount = input.nextInt()
        for (i in 0 until entityCount) {
            val unit = Unit()
            unit.unitId = input.nextInt()
            unit.team = input.nextInt()
            unit.unitType = input.next() // UNIT, HERO, TOWER, can also be GROOT from wood1
            unit.x = input.nextInt()
            unit.y = input.nextInt()
            unit.attackRange = input.nextInt()
            unit.health = input.nextInt()
            unit.maxHealth = input.nextInt()
            unit.shield = input.nextInt() // useful in bronze
            unit.attackDamage = input.nextInt()
            unit.movementSpeed = input.nextInt()
            unit.stunDuration = input.nextInt() // useful in bronze
            unit.goldValue = input.nextInt()
            unit.countDown1 = input.nextInt() // all countDown and mana variables are useful starting in bronze
            unit.countDown2 = input.nextInt()
            unit.countDown3 = input.nextInt()
            unit.mana = input.nextInt()
            unit.maxMana = input.nextInt()
            unit.manaRegeneration = input.nextInt()
            unit.heroType = input.next() // DEADPOOL, VALKYRIE, DOCTOR_STRANGE, HULK, IRONMAN
            unit.isVisible = input.nextInt() // 0 if it isn't
            unit.itemsOwned = input.nextInt() // useful from wood1

            addUnit(unit)
        }

        if (roundType < 0) {
            println("HULK")
            println("IRONMAN")
        } else makeMove()
        clear()
    }
}

var roundStartTime = System.currentTimeMillis()

val tankType = "HULK"
val rangerType = "IRONMAN"

var team = 0
val enemyUnits = mutableListOf<Unit>()
val myUnits = mutableListOf<Unit>()
var tankHero = Unit()
var rangeHero = Unit()
var enemyHero = Unit()
var enemyHero2 = Unit()

var tower: Unit = Unit()
var enemyTower: Unit = Unit()

fun addUnit(unit: Unit) {
    if (unit.unitType == "UNIT") {
        if (unit.team == team) myUnits.add(unit)
        else enemyUnits.add(unit)
    } else if (unit.unitType == "HERO") {
        if (unit.team == team) {
            if (unit.heroType == tankType) tankHero = unit
            else rangeHero = unit;
        } else enemyHero = unit
    } else if (unit.unitType == "TOWER") {
        if (unit.team == team) tower = unit
        else enemyTower = unit
    }
}

fun makeMove() {
    roundStartTime = System.currentTimeMillis()
    moveHero(tankHero)
    moveHero(rangeHero)

    System.err.println("Time for move ${System.currentTimeMillis() - roundStartTime}")
}

fun moveHero(hero: Unit) {
    /*if (hero.health < hero.maxHealth / 3 && hero.health + enemyHero.attackDamage < enemyHero.health) {
        println("MOVE ${tower.x} ${tower.y}")
        return
    }

    val unitsInRange = enemyUnits.filter { unit -> hero.dist(unit) < hero.attackRange }
    val killAbleUnits = unitsInRange.filter { unit -> unit.health < hero.attackDamage }

    if (killAbleUnits.isNotEmpty()) {
        val goodUnit = killAbleUnits[0]
        println("ATTACK ${goodUnit.unitId}")
        return
    }

    if (enemyHero.dist(enemyTower) > enemyTower.attackRange)
        println("ATTACK_NEAREST HERO")
    else if (unitsInRange.isNotEmpty()) {
        println("ATTACK ${unitsInRange[0].x} ${unitsInRange[0].y}")
    } else
        println("WAIT")*/

    val moves = mutableListOf<Move>()
    moves.add(flee(hero))
    moves.add(attackHero(hero))
    moves.add(attackUint(hero))
    moves.add(Move(2, "WAIT"))

    val bestMove = moves.maxBy { move -> move.value }
    println(bestMove!!.move)
}

fun flee(hero: Unit): Move {
    var value = 0
    if (hero.health < hero.maxHealth / 3) value += 10
    if (hero.health < enemyHero.health) value += 5

    return Move(value, "MOVE ${tower.x} ${tower.y}")
}

fun attackHero(hero: Unit): Move {
    var value = 0
    if (enemyHero.dist(enemyTower) > enemyTower.attackRange) value += 3
    if (enemyHero.dist(hero) < hero.attackRange) value += 3
    if (enemyHero.health < hero.attackDamage) value += 10

    return Move(value, "ATTACK_NEAREST HERO")
}

fun attackUint(hero: Unit): Move {
    var value = 0
    if(enemyUnits.isEmpty()) {
        return Move(-1, "WAIT")
    }

    val unitsInRange = enemyUnits.filter { unit -> hero.dist(unit) < hero.attackRange }
    val killAbleUnits = unitsInRange.filter { unit -> unit.health < hero.attackDamage }

    if(killAbleUnits.isNotEmpty()) {
        return Move(6, "ATTACK ${killAbleUnits[0].unitId}")
    }

    if(unitsInRange.isNotEmpty()) {
        return Move(4, "ATTACK ${unitsInRange[0].unitId}")
    }

    val goodUnits = enemyUnits.filter { unit -> unit.dist(enemyTower) > enemyTower.attackRange }
            .sortedBy { unit -> unit.dist(hero)  }
    if(goodUnits.isNotEmpty()) {
        return Move(3, "ATTACK ${goodUnits[0].unitId}")
    }

    return Move(0, "ATTACK ${enemyUnits[0].unitId}")
}

fun clear() {
    enemyUnits.clear()
    myUnits.clear()
}

open class Unit {
    var unitId = 0
    var team = 0
    lateinit var unitType: String
    var x = 0
    var y = 0
    var attackRange = 0
    var health = 0
    var maxHealth = 0
    var shield = 0
    var attackDamage = 0
    var movementSpeed = 0
    var stunDuration = 0
    var goldValue = 0
    var countDown1 = 0
    var countDown2 = 0
    var countDown3 = 0
    var mana = 0
    var maxMana = 0
    var manaRegeneration = 0
    lateinit var heroType: String
    var isVisible = 0
    var itemsOwned = 0

    fun dist(other: Unit): Double {
        return Point2D.distance(x.toDouble(), y.toDouble(), other.x.toDouble(), other.y.toDouble())
    }
}

data class Move(val value: Int, val move: String)
