package cellular.test

// implement interface by delegating to multiple classes

interface I {
    fun a1(): Unit
    fun a2(): Unit
    fun b1(): Unit
}

interface IA {
    fun a1(): Unit
    fun a2(): Unit
}

interface IB {
    fun b1(): Unit
}

class CA: IA {
    override fun a1() = println("a1")
    override fun a2() = println("a2")
}

class CB: IB {
    override fun b1() = println("b1")
}

class C(val a: IA, val b: IB) : IA by a, IB by b, I

fun main() {
    val c = C(CA(), CB())
    c.a1()
    c.a2()
    c.b1()
}