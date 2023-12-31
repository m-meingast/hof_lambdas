package exercise5

import java.io.File

fun page(init: PageBuilder.() -> Unit): Page {
    val b: PageBuilder = PageBuilder()
    b.init()
    return Page(b.title, b.elements)
}

abstract class ContainerBuilder<E> {
    internal val elements: MutableList<E> = mutableListOf()
}

open class ElementContainerBuilder : ContainerBuilder<Element>() {
    fun heading(level: Int, text: String) {
        elements.add(Heading(level, text))
    }

    fun text(text: String) {
        elements.add(Text(text))
    }

    fun paragraph(init: ParagraphBuilder.() -> Unit) {
        val builder = ParagraphBuilder()
        builder.init()
        builder.elements.forEach() { elements.add(Paragraph(it)) }
    }

    fun div(init: ElementContainerBuilder.() -> Unit) {
        val builder = ElementContainerBuilder()
        builder.init()
        val div = Div(builder.elements)
        elements.add(div)
    }

    fun list(ordered: Boolean, init: ListBuilder.() -> Unit) {
        val builder = ListBuilder()
        builder.init()
        val list = HTMLList(builder.elements, ordered)
        elements.add(list)
    }
}

class PageBuilder : ElementContainerBuilder() {
    var title: String = ""
}

class ListBuilder : ContainerBuilder<ListItem>() {
    fun item(init: ElementContainerBuilder.() -> Unit) {
        val builder = ElementContainerBuilder()
        builder.init()
        val item = ListItem(builder.elements)
        elements.add(item)
    }
}

class ParagraphBuilder : ContainerBuilder<String>() {

    operator fun String.unaryPlus() {
        elements.add(this)
    }
}

fun main() {


    val page =
        page {
            title = "My Page"
            heading(1, "Welcome to the Kotlin course")
            div {

                paragraph {
                    +"Kotlin is"
                }
                list(true) {
                    item {
                        heading(3, "General-purpose programming language")
                        list(false) {
                            item { text("Backend") }
                            item { text("Mobile") }
                            item { text("Stand-Alone") }
                            item { text("Web") }
                            item { text("...") }
                        }
                    }
                    item {
                        heading(3, "Modern, multi-paradigm")
                        list(false) {
                            item {
                                text("Object-oriented, functional programming (functions as first-class citizens, …), etc.")
                            }
                            item {
                                text("Statically typed but automatically inferred types")
                            }
                        }
                    }
                    item {
                        heading(3, "Emphasis on conciseness / expressiveness / practicality")
                        list(false) {
                            item {
                                text("Goodbye Java boilerplate code (getter methods, setter methods, final, etc.)")
                            }
                            item {
                                text("Common tasks should be short and easy")
                            }
                            item {
                                text("Mistakes should be caught as early as possible")
                            }
                            item {
                                text("But no cryptic operators as in Scala")
                            }
                        }
                    }
                    item {
                        heading(3, "100 % Interoperable with Java")
                        list(false) {
                            item {
                                text("You have a Java project? Make it a Java/Kotlin project in minutes with 100% interop")
                            }
                            item {
                                text("Kotlin-to-Java as well as Java-to-Kotlin calls")
                            }
                            item {
                                text("For example, Kotlin reuses Java’s existing standard library (ArrayList, etc.) and extends it with extension functions (opposed to, e.g., Scala that uses its own list implementations)")
                            }
                        }

                    }
                }
            }

        }
    File("page2.html").writeText(HTMLRenderer.render(page))


}
