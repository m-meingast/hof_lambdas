package exercise5

import java.io.File

sealed interface Element

sealed interface TaggedElement : Element {
    val tag: String
    val openTag
        get() = "<$tag>"
    val closeTag
        get() = "</$tag>"
}

sealed interface TextElement : Element {
    val text: String
}

sealed interface TaggedTextElement : TaggedElement, TextElement

sealed interface ContainerElement : TaggedElement {
    val elements: List<Element>
}

data class Text(override val text: String) : TextElement

data class Div(override val elements: List<Element>) : ContainerElement {
    override val tag: String
        get() = "div"

    constructor(vararg elements: Element) : this(elements.toList())
}

data class HTMLList(override val elements: List<ListItem>, val ordered: Boolean = false) : ContainerElement {
    override val tag: String
        get() = if (ordered) "ol" else "ul"

    constructor(ordered: Boolean, vararg elements: ListItem) : this(elements.toList(), ordered)
}

data class ListItem(override val elements: List<Element>) : ContainerElement {
    override val tag: String
        get() = "li"

    constructor(vararg elements: Element) : this(elements.toList())
}

data class Paragraph(override val text: String) : TaggedTextElement {
    override val tag: String
        get() = "p"
}

data class Heading(val level: Int = 1, override val text: String) : TaggedTextElement {
    override val tag: String
        get() = "h$level"

    init {
        if (level !in 1..6) {
            error("Invalid level")
        }
    }
}

data class Page(val title: String, val elements: List<Element>) {
    constructor(title: String, vararg elements: Element) : this(title, elements.toList())
}

object HTMLRenderer {
    fun render(element: Element): String =
        when (element) {
            is ContainerElement -> {
                buildString {
                    appendLine(element.openTag)
                    for (child in element.elements) {
                        appendLine(render(child).indentEachLine())
                    }
                    append(element.closeTag)
                }
            }
            is TaggedTextElement -> "${element.openTag}${element.text}${element.closeTag}"
            is Text -> element.text
        }

    fun render(page: Page) = """
            |<html>
            |  <head>
            |    <title>${page.title}</title>
            |  </head>
            |  <body>
                 |${page.elements.joinToString("\n") { render(it) }.indentEachLine(4)}
            |  </body>
            |</html>""".trimMargin()
}

fun String.text() = Text(this)
fun String.p() = Paragraph(this)
fun String.h(level: Int) = Heading(level, this)
fun String.h1() = this.h(1)
fun String.h2() = this.h(2)
fun String.h3() = this.h(3)
fun String.h4() = this.h(4)
fun String.h5() = this.h(5)
fun String.h6() = this.h(6)

fun main() {
    val page = Page(
        "My Page",
        "Welcome to the Kotlin course".h1(),
        Div(
            "Kotlin is".p(),
            HTMLList(
                true,
                ListItem(
                    "General-purpose programming language".h3(),
                    HTMLList(
                        false,
                        ListItem(
                            "Backend, Mobile, Stand-Alone, Web, ...".text()
                        )
                    )
                ),
                ListItem(
                    "Modern, multi-paradigm".h3(),
                    HTMLList(
                        false,
                        ListItem(
                            "Object-oriented, functional programming (functions as first-class citizens, …), etc.".text()
                        ),
                        ListItem(
                            "Statically typed but automatically inferred types".text()
                        )
                    )
                ),
                ListItem(
                    "Emphasis on conciseness / expressiveness / practicality".h3(),
                    HTMLList(
                        false,
                        ListItem(
                            "Goodbye Java boilerplate code (getter methods, setter methods, final, etc.)".text()
                        ),
                        ListItem(
                            "Common tasks should be short and easy".text()
                        ),
                        ListItem(
                            "Mistakes should be caught as early as possible".text()
                        ),
                        ListItem(
                            "But no cryptic operators as in Scala".text()
                        )
                    )
                ),
                ListItem(
                    "100% interoperable with Java".h3(),
                    HTMLList(
                        false,
                        ListItem(
                            "You have a Java project? Make it a Java/Kotlin project in minutes with 100% interop".text()
                        ),
                        ListItem(
                            "Kotlin-to-Java as well as Java-to-Kotlin calls".text()
                        ),
                        ListItem(
                            "For example, Kotlin reuses Java’s existing standard library (ArrayList, etc.) and extends it with extension functions (opposed to, e.g., Scala that uses its own list implementations)".text()
                        )
                    )
                ),
            )
        )
    )

    File("page.html").writeText(HTMLRenderer.render(page))
}

fun String.indentEachLine(depth: Int = 2, symbol: String = " ") =
    this.split("\n").joinToString("\n") { symbol.repeat(depth) + it }