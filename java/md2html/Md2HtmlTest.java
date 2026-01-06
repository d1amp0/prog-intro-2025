package md2html;

import base.Selector;

import java.util.function.Consumer;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class Md2HtmlTest {
    // === 3637
    private static final Consumer<? super Md2HtmlTester> INS = tester -> tester
            .test("<<вставка>>", "<p><ins>вставка</ins></p>")
            .test("Это <<вставка>>, вложенная в текст", "<p>Это <ins>вставка</ins>, вложенная в текст</p>")
            .spoiled("Это не <<вставка>>", "<p>Это не &lt;&lt;вставка&gt;&gt;</p>", "<", ">")
            .spoiled("Это не <<вставка>> 2", "<p>Это не &lt;&lt;вставка&gt;&gt; 2</p>", "<", ">")
            .addElement("ins", "<<", ">>");
    private static final Consumer<? super Md2HtmlTester> DEL = tester -> tester
            .test("}}удаление{{", "<p><del>удаление</del></p>")
            .test("Это }}удаление{{, вложенное в текст", "<p>Это <del>удаление</del>, вложенное в текст</p>")
            .spoiled("Это не }}удаление{{", "<p>Это не }}удаление{{</p>", "{")
            .spoiled("Это не }}удаление{{ 2", "<p>Это не }}удаление{{ 2</p>", "{")
            .addElement("del", "}}", "{{");

    // === 3839
    private static final Consumer<? super Md2HtmlTester> PRE = tester -> tester
            .test("```код __без__ форматирования```", "<p><pre>код __без__ форматирования</pre></p>")
            .test(
                    "Это не `\\``код __без__ форматирования``\\`",
                    "<p>Это не <code>`</code>код <strong>без</strong> форматирования<code></code>`</p>"
            )
            .addElement("pre", "```", (checker, markup, input, output) -> {
                final String contentS = checker.generateInput(markup).replace("`", "");

                input.append("```").append(contentS).append("```");
                output.append("<pre>").append(contentS.replace("<", "&lt;").replace(">", "")).append("</pre>");
            });

    // === 3435
    private static final Consumer<? super Md2HtmlTester> SAMP = tester -> tester
            .test("!!пример!!", "<p><samp>пример</samp></p>")
            .test("Это !!пример!!, вложенный в текст", "<p>Это <samp>пример</samp>, вложенный в текст</p>")
            .spoiled("Это не !!пример!!", "<p>Это не !!пример!!</p>", "!")
            .spoiled("Это не !!пример!! 2", "<p>Это не !!пример!! 2</p>", "!")
            .addElement("samp", "!!");

    // === 3233
    private static final Consumer<Md2HtmlTester> VAR = tester -> tester
            .test("%переменная%", "<p><var>переменная</var></p>")
            .test("Это %переменная%, вложенная в текст", "<p>Это <var>переменная</var>, вложенная в текст</p>")
            .spoiled("Это не %переменная%", "<p>Это не %переменная%</p>", "%")
            .spoiled("Это не %переменная% 2", "<p>Это не %переменная% 2</p>", "%")
            .addElement("var", "%");

    // === Common

    public static final Selector SELECTOR = Selector.composite(Md2HtmlTest.class, c -> new Md2HtmlTester(), Md2HtmlTester::test)
            .variant("Base")
            .variant("3637", INS, DEL)
            .variant("3839", PRE)
            .variant("3435", SAMP)
            .variant("3233", VAR)
            .selector();

    private Md2HtmlTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}
