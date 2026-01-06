package markup;

import base.Asserts;
import base.Selector;
import base.TestCounter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class MarkupListTest {
    public static final Consumer<TestCounter> VARIANT = MarkupListTest.variant(
            "Tex", Map.ofEntries(
                    Map.entry("<p>", "\\par{}"), Map.entry("</p>", ""),
                    Map.entry("<em>", "\\emph{"), Map.entry("</em>", "}"),
                    Map.entry("<strong>", "\\textbf{"), Map.entry("</strong>", "}"),
                    Map.entry("<s>", "\\textst{"), Map.entry("</s>", "}"),
                    Map.entry("<ul>", "\\begin{itemize}"), Map.entry("</ul>", "\\end{itemize}"),
                    Map.entry("<ol>", "\\begin{enumerate}"), Map.entry("</ol>", "\\end{enumerate}"),
                    Map.entry("<li>", "\\item "), Map.entry("</li>", "")
            )
    );


    public static final Selector SELECTOR = new Selector(MarkupListTest.class)
            .variant("3637", VARIANT)
            .variant("3839", VARIANT)
            .variant("4142", VARIANT)
            .variant("4749", VARIANT)

            ;

    private MarkupListTest() {
    }

    public static Consumer<TestCounter> variant(final String name, final Map<String, String> mapping) {
        return MarkupTester.variant(MarkupListTest::test, name, mapping);
    }

    private static void test(final MarkupTester.Checker checker) {
        final Paragraph paragraph0 = new Paragraph(List.of(new Text("hello")));
        final String paragraph0Markup = "<p>hello</p>";

        final Paragraph paragraph1 = new Paragraph(List.of(
                new Strong(List.of(
                        new Text("1"),
                        new Strikeout(List.of(
                                new Text("2"),
                                new Emphasis(List.of(
                                        new Text("3"),
                                        new Text("4")
                                )),
                                new Text("5")
                        )),
                        new Text("6")
                ))
        ));
        final String paragraph1Markup = "<p><strong>1<s>2<em>34</em>5</s>6</strong></p>";

        final Paragraph paragraph2 = new Paragraph(List.of(new Strong(List.of(
                new Text("sdq"),
                new Strikeout(List.of(new Emphasis(List.of(new Text("r"))), new Text("vavc"))),
                new Text("zg")))
        ));
        final String paragraph2Markup = "<p><strong>sdq<s><em>r</em>vavc</s>zg</strong></p>";

        checker.test(paragraph0, paragraph0Markup);
        checker.test(paragraph1, paragraph1Markup);
        checker.test(paragraph2, paragraph2Markup);

        final ListItem li1 = new ListItem(List.of(new Paragraph(List.of(new Text("1.1"))), new Paragraph(List.of(new Text("1.2")))));
        final String li1Markup = "<p>1.1</p><p>1.2</p>";
        final ListItem li2 = new ListItem(List.of(new Paragraph(List.of(new Text("2")))));
        final String li2Markup = "<p>2</p>";
        final ListItem pli1 = new ListItem(List.of(paragraph1));
        final ListItem pli2 = new ListItem(List.of(paragraph2));

        final ListItem nestedUl = new ListItem(List.of(ul(li1, li2)));
        final String nestedUlMarkup = ul(li1Markup, li2Markup);

        checker.test(ul(li1), ul(li1Markup));
        checker.test(ul(li2), ul(li2Markup));
        checker.test(ul(pli1), ul(paragraph1Markup));
        checker.test(ul(pli2), ul(paragraph2Markup));
        checker.test(ul(li1, li2), nestedUlMarkup);
        checker.test(ul(pli1, pli2), ul(paragraph1Markup, paragraph2Markup));
        checker.test(ul(nestedUl), ul(nestedUlMarkup));

        final ListItem nestedOl = new ListItem(List.of(ol(li1, li2)));
        final String nestedOlMarkup = ol(li1Markup, li2Markup);
        checker.test(ol(li1), ol(li1Markup));
        checker.test(ol(li2), ol(li2Markup));
        checker.test(ol(pli1), ol(paragraph1Markup));
        checker.test(ol(pli2), ol(paragraph2Markup));
        checker.test(ol(li1, li2), nestedOlMarkup);
        checker.test(ol(pli1, pli2), ol(paragraph1Markup, paragraph2Markup));
        checker.test(ol(nestedOl), ol(nestedOlMarkup));

        checker.test(ul(nestedUl, nestedOl), ul(nestedUlMarkup, nestedOlMarkup));
        checker.test(ol(nestedUl, nestedOl), ol(nestedUlMarkup, nestedOlMarkup));

        checker.test(
                ul(nestedUl, nestedOl, pli1, pli2),
                ul(nestedUlMarkup, nestedOlMarkup, paragraph1Markup, paragraph2Markup)
        );
        checker.test(
                ol(nestedUl, nestedOl, pli1, pli2),
                ol(nestedUlMarkup, nestedOlMarkup, paragraph1Markup, paragraph2Markup)
        );

        checker.test(
                new Paragraph(List.of(new Strikeout(List.of(new Strong(List.of(new Strikeout(List.of(new Emphasis(List.of(new Strikeout(List.of(new Text("е"), new Text("г"), new Text("ц"))), new Strong(List.of(new Text("щэш"), new Text("игепы"), new Text("хм"))), new Strikeout(List.of(new Text("б"), new Text("е"))))), new Strong(List.of(new Strong(List.of(new Text("ю"), new Text("дърб"), new Text("еи"))), new Emphasis(List.of(new Text("зр"), new Text("дуаужш"), new Text("ш"))), new Strong(List.of(new Text("рб"), new Text("щ"))))), new Text("a"))), new Strikeout(List.of(new Text("no"), new Text("ddw"), new Strong(List.of(new Emphasis(List.of(new Text("щ"), new Text("ча"), new Text("эгфш"))), new Strikeout(List.of(new Text("фяи"), new Text("штел"), new Text("н"))), new Strikeout(List.of(new Text("ту"), new Text("ьъг"))))))), new Emphasis(List.of(new Emphasis(List.of(new Text("tc"), new Strong(List.of(new Text("щ"), new Text("э"), new Text("то"))), new Strong(List.of(new Text("а"), new Text("ц"))))), new Emphasis(List.of(new Text("hld"), new Emphasis(List.of(new Text("ыо"), new Text("яще"), new Text("лэ"))), new Text("i"))), new Text("tm"))))), new Emphasis(List.of(new Text("q"), new Emphasis(List.of(new Text("zn"), new Strong(List.of(new Text("mnphd"), new Strong(List.of(new Text("г"), new Text("вй"), new Text("шш"))), new Strong(List.of(new Text("з"), new Text("ввъ"))))), new Strikeout(List.of(new Emphasis(List.of(new Text("у"), new Text("в"), new Text("у"))), new Strikeout(List.of(new Text("лдяр"), new Text("зоъ"), new Text("эн"))), new Strikeout(List.of(new Text("в"), new Text("м"))))))), new Strikeout(List.of(new Text("cqqzbhtn"), new Text("i"), new Strong(List.of(new Text("i"), new Strikeout(List.of(new Text("э"), new Text("як"))), new Text("i"))))))), new Text("ef"))), new Strikeout(List.of(new Strikeout(List.of(new Strong(List.of(new Emphasis(List.of(new Strong(List.of(new Text("шец"), new Text("ю"), new Text("дрк"))), new Strikeout(List.of(new Text("е"), new Text("мь"), new Text("б"))), new Strong(List.of(new Text("еп"), new Text("ряэк"))))), new Strong(List.of(new Text("t"), new Emphasis(List.of(new Text("сы"), new Text("в"), new Text("к"))), new Text("rf"))), new Text("x"))), new Emphasis(List.of(new Emphasis(List.of(new Emphasis(List.of(new Text("юд"), new Text("чх"), new Text("яжюи"))), new Emphasis(List.of(new Text("и"), new Text("п"), new Text("вх"))), new Text("mf"))), new Emphasis(List.of(new Strong(List.of(new Text("шб"), new Text("вс"), new Text("е"))), new Strong(List.of(new Text("т"), new Text("шж"), new Text("ину"))), new Strong(List.of(new Text("ыа"), new Text("ьскю"))))), new Text("x"))), new Strikeout(List.of(new Emphasis(List.of(new Strong(List.of(new Text("в"), new Text("зыйгг"), new Text("о"))), new Strikeout(List.of(new Text("ок"), new Text("уч"), new Text("л"))), new Text("v"))), new Emphasis(List.of(new Strong(List.of(new Text("н"), new Text("ъчжфзтодг"), new Text("кыч"))), new Strikeout(List.of(new Text("вд"), new Text("лпбзс"), new Text("гщ"))), new Emphasis(List.of(new Text("ъ"), new Text("й"))))), new Text("n"))))), new Strong(List.of(new Strong(List.of(new Emphasis(List.of(new Strong(List.of(new Text("ю"), new Text("сдям"), new Text("ш"))), new Strong(List.of(new Text("ц"), new Text("еящж"), new Text("шн"))), new Text("upg"))), new Text("d"), new Strikeout(List.of(new Text("xu"), new Strikeout(List.of(new Text("кл"), new Text("еок"), new Text("с"))), new Strong(List.of(new Text("а"), new Text("ь"))))))), new Strong(List.of(new Strikeout(List.of(new Text("zn"), new Text("syb"), new Strong(List.of(new Text("ъзюкмц"), new Text("ндюз"))))), new Strong(List.of(new Strikeout(List.of(new Text("н"), new Text("с"), new Text("ь"))), new Strikeout(List.of(new Text("зьуес"), new Text("к"), new Text("и"))), new Strong(List.of(new Text("тв"), new Text("у"))))), new Strikeout(List.of(new Strong(List.of(new Text("ы"), new Text("г"), new Text("гм"))), new Strong(List.of(new Text("сыр"), new Text("я"), new Text("т"))), new Emphasis(List.of(new Text("ь"), new Text("махыы"))))))), new Text("k"))), new Text("q"))), new Strikeout(List.of(new Text("b"), new Text("o"), new Emphasis(List.of(new Strong(List.of(new Strikeout(List.of(new Strong(List.of(new Text("х"), new Text("йз"), new Text("ж"))), new Text("udlh"), new Strikeout(List.of(new Text("чъ"), new Text("с"))))), new Strong(List.of(new Strong(List.of(new Text("ю"), new Text("т"), new Text("яъайл"))), new Strong(List.of(new Text("х"), new Text("ри"), new Text("в"))), new Strong(List.of(new Text("щ"), new Text("вт"))))), new Text("m"))), new Text("vzb"), new Strong(List.of(new Text("oi"), new Text("r"), new Text("inpz"))))))))),
                "<p><s><strong><s><em><s>егц</s><strong>щэшигепыхм</strong><s>бе</s></em><strong><strong>юдърбеи</strong><em>зрдуаужшш</em><strong>рбщ</strong></strong>a</s><s>noddw<strong><em>щчаэгфш</em><s>фяиштелн</s><s>туьъг</s></strong></s><em><em>tc<strong>щэто</strong><strong>ац</strong></em><em>hld<em>ыоящелэ</em>i</em>tm</em></strong><em>q<em>zn<strong>mnphd<strong>гвйшш</strong><strong>зввъ</strong></strong><s><em>уву</em><s>лдярзоъэн</s><s>вм</s></s></em><s>cqqzbhtni<strong>i<s>эяк</s>i</strong></s></em>ef</s><s><s><strong><em><strong>шецюдрк</strong><s>емьб</s><strong>епряэк</strong></em><strong>t<em>сывк</em>rf</strong>x</strong><em><em><em>юдчхяжюи</em><em>ипвх</em>mf</em><em><strong>шбвсе</strong><strong>тшжину</strong><strong>ыаьскю</strong></em>x</em><s><em><strong>взыйгго</strong><s>окучл</s>v</em><em><strong>нъчжфзтодгкыч</strong><s>вдлпбзсгщ</s><em>ъй</em></em>n</s></s><strong><strong><em><strong>юсдямш</strong><strong>цеящжшн</strong>upg</em>d<s>xu<s>клеокс</s><strong>аь</strong></s></strong><strong><s>znsyb<strong>ъзюкмцндюз</strong></s><strong><s>нсь</s><s>зьуески</s><strong>тву</strong></strong><s><strong>ыггм</strong><strong>сырят</strong><em>ьмахыы</em></s></strong>k</strong>q</s><s>bo<em><strong><s><strong>хйзж</strong>udlh<s>чъс</s></s><strong><strong>ютяъайл</strong><strong>хрив</strong><strong>щвт</strong></strong>m</strong>vzb<strong>oirinpz</strong></em></s></p>"
        );

        checker.test(
                new OrderedList(List.of(new ListItem(List.of(new OrderedList(List.of(new ListItem(List.of(new OrderedList(List.of()), new Paragraph(List.of(new Text("е"))), new Paragraph(List.of(new Text("х"))))), new ListItem(List.of(new OrderedList(List.of()), new OrderedList(List.of()), new Paragraph(List.of(new Text("эш"))))), new ListItem(List.of(new UnorderedList(List.of()), new Paragraph(List.of(new Text("цць"))))), new ListItem(List.of(new UnorderedList(List.of()), new Paragraph(List.of(new Text("м"))))))), new UnorderedList(List.of(new ListItem(List.of(new OrderedList(List.of()), new OrderedList(List.of()), new OrderedList(List.of()))), new ListItem(List.of(new Paragraph(List.of(new Text("ю"))), new UnorderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new OrderedList(List.of()), new OrderedList(List.of()))), new ListItem(List.of(new UnorderedList(List.of()), new UnorderedList(List.of()))))), new Paragraph(List.of(new Emphasis(List.of(new Emphasis(List.of(new Text("узр"))), new Text("i"), new Emphasis(List.of(new Text("аужш"))), new Text("ш"))), new Strong(List.of(new Text("c"), new Strikeout(List.of(new Text("щ"))), new Text("a"), new Text("з"))), new Strong(List.of(new Emphasis(List.of(new Text("ь"))), new Text("ddw"), new Text("зщ"), new Text("ча"))), new Emphasis(List.of(new Strong(List.of(new Text("гфш"))), new Strikeout(List.of(new Text("фяи"))), new Text("штел"), new Text("н"))))), new OrderedList(List.of(new ListItem(List.of(new UnorderedList(List.of()), new OrderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new OrderedList(List.of()), new Paragraph(List.of(new Text("юцщ"))), new UnorderedList(List.of()))), new ListItem(List.of(new UnorderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new OrderedList(List.of()), new Paragraph(List.of(new Text("э"))))))))), new ListItem(List.of(new OrderedList(List.of(new ListItem(List.of(new UnorderedList(List.of()), new OrderedList(List.of()), new Paragraph(List.of(new Text("ж"))))), new ListItem(List.of(new OrderedList(List.of()), new Paragraph(List.of(new Text("ыеж"))), new Paragraph(List.of(new Text("ыо"))))), new ListItem(List.of(new Paragraph(List.of(new Text("ще"))), new Paragraph(List.of(new Text("щш"))))), new ListItem(List.of(new UnorderedList(List.of()), new OrderedList(List.of()))))), new OrderedList(List.of(new ListItem(List.of(new Paragraph(List.of(new Text("щосз"))), new OrderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new UnorderedList(List.of()), new OrderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new UnorderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new Paragraph(List.of(new Text("сс"))), new UnorderedList(List.of()))))), new Paragraph(List.of(new Text("yu"), new Text("w"), new Text("ghtry"), new Emphasis(List.of(new Strikeout(List.of(new Text("прф"))), new Emphasis(List.of(new Text("р"))), new Text("я"), new Text("я"))))), new Paragraph(List.of(new Text("w"), new Strong(List.of(new Text("k"), new Emphasis(List.of(new Text("н"))), new Strikeout(List.of(new Text("в"))), new Text("м"))), new Strikeout(List.of(new Text("cqqzbhtn"), new Text("i"), new Text("м"), new Text("ю"))), new Strikeout(List.of(new Strong(List.of(new Text("ш"))), new Strong(List.of(new Text("к"))), new Text("ж"), new Text("б"))))))), new ListItem(List.of(new UnorderedList(List.of(new ListItem(List.of(new OrderedList(List.of()), new OrderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new UnorderedList(List.of()), new OrderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new OrderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new UnorderedList(List.of()), new UnorderedList(List.of()))))), new UnorderedList(List.of(new ListItem(List.of(new UnorderedList(List.of()), new OrderedList(List.of()), new OrderedList(List.of()))), new ListItem(List.of(new UnorderedList(List.of()), new Paragraph(List.of(new Text("е"))), new UnorderedList(List.of()))), new ListItem(List.of(new OrderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new Paragraph(List.of(new Text("ед"))), new UnorderedList(List.of()))))), new OrderedList(List.of(new ListItem(List.of(new OrderedList(List.of()), new OrderedList(List.of()), new Paragraph(List.of(new Text("п"))))), new ListItem(List.of(new UnorderedList(List.of()), new Paragraph(List.of(new Text("э"))), new UnorderedList(List.of()))), new ListItem(List.of(new UnorderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new OrderedList(List.of()), new Paragraph(List.of(new Text("к"))))))), new Paragraph(List.of(new Strong(List.of(new Strong(List.of(new Text("с"))), new Text("x"), new Emphasis(List.of(new Text("йюд"))), new Text("чх"))), new Strikeout(List.of(new Strong(List.of(new Text("жюи"))), new Emphasis(List.of(new Text("и"))), new Strong(List.of(new Text("ьмт"))), new Text("йц"))), new Emphasis(List.of(new Strong(List.of(new Text("шб"))), new Strong(List.of(new Text("еф"))), new Text("ут"), new Text("шж"))), new Emphasis(List.of(new Emphasis(List.of(new Text("ну"))), new Strong(List.of(new Text("ыа"))), new Text("ьскю"), new Text("чз"))))))), new ListItem(List.of(new UnorderedList(List.of(new ListItem(List.of(new OrderedList(List.of()), new OrderedList(List.of()), new Paragraph(List.of(new Text("ыйгг"))))), new ListItem(List.of(new OrderedList(List.of()), new UnorderedList(List.of()), new Paragraph(List.of(new Text("ф"))))), new ListItem(List.of(new OrderedList(List.of()), new Paragraph(List.of(new Text("ч"))))), new ListItem(List.of(new OrderedList(List.of()), new OrderedList(List.of()))))), new Paragraph(List.of(new Strikeout(List.of(new Emphasis(List.of(new Text("э"))), new Text("amqcfdzrg"), new Emphasis(List.of(new Text("т"))), new Text("з"))), new Text("b"), new Emphasis(List.of(new Strikeout(List.of(new Text("энфны"))), new Strikeout(List.of(new Text("гщ"))), new Text("ы"), new Text("шя"))), new Text("uvpqzhn"))), new UnorderedList(List.of(new ListItem(List.of(new UnorderedList(List.of()), new OrderedList(List.of()), new OrderedList(List.of()))), new ListItem(List.of(new OrderedList(List.of()), new Paragraph(List.of(new Text("ящж"))), new UnorderedList(List.of()))), new ListItem(List.of(new OrderedList(List.of()), new Paragraph(List.of(new Text("цлл"))))), new ListItem(List.of(new OrderedList(List.of()), new Paragraph(List.of(new Text("ъ"))))))), new Paragraph(List.of(new Strong(List.of(new Strong(List.of(new Text("ъ"))), new Strikeout(List.of(new Text("кл"))), new Strikeout(List.of(new Text("счи"))), new Text("ра"))), new Strong(List.of(new Strikeout(List.of(new Text("ь"))), new Text("zn"), new Text("ъ"), new Text("умъъзюкмц"))), new Strikeout(List.of(new Emphasis(List.of(new Text("дюз"))), new Strong(List.of(new Text("эы"))), new Text("и"), new Text("р"))), new Emphasis(List.of(new Strong(List.of(new Text("ьуес"))), new Strikeout(List.of(new Text("йгтв"))), new Text("у"), new Text("еы"))))))))),
                "<ol><li><ol><li><ol></ol><p>е</p><p>х</p></li><li><ol></ol><ol></ol><p>эш</p></li><li><ul></ul><p>цць</p></li><li><ul></ul><p>м</p></li></ol><ul><li><ol></ol><ol></ol><ol></ol></li><li><p>ю</p><ul></ul><ul></ul></li><li><ol></ol><ol></ol></li><li><ul></ul><ul></ul></li></ul><p><em><em>узр</em>i<em>аужш</em>ш</em><strong>c<s>щ</s>aз</strong><strong><em>ь</em>ddwзщча</strong><em><strong>гфш</strong><s>фяи</s>штелн</em></p><ol><li><ul></ul><ol></ol><ul></ul></li><li><ol></ol><p>юцщ</p><ul></ul></li><li><ul></ul><ul></ul></li><li><ol></ol><p>э</p></li></ol></li><li><ol><li><ul></ul><ol></ol><p>ж</p></li><li><ol></ol><p>ыеж</p><p>ыо</p></li><li><p>ще</p><p>щш</p></li><li><ul></ul><ol></ol></li></ol><ol><li><p>щосз</p><ol></ol><ul></ul></li><li><ul></ul><ol></ol><ul></ul></li><li><ul></ul><ul></ul></li><li><p>сс</p><ul></ul></li></ol><p>yuwghtry<em><s>прф</s><em>р</em>яя</em></p><p>w<strong>k<em>н</em><s>в</s>м</strong><s>cqqzbhtniмю</s><s><strong>ш</strong><strong>к</strong>жб</s></p></li><li><ul><li><ol></ol><ol></ol><ul></ul></li><li><ul></ul><ol></ol><ul></ul></li><li><ol></ol><ul></ul></li><li><ul></ul><ul></ul></li></ul><ul><li><ul></ul><ol></ol><ol></ol></li><li><ul></ul><p>е</p><ul></ul></li><li><ol></ol><ul></ul></li><li><p>ед</p><ul></ul></li></ul><ol><li><ol></ol><ol></ol><p>п</p></li><li><ul></ul><p>э</p><ul></ul></li><li><ul></ul><ul></ul></li><li><ol></ol><p>к</p></li></ol><p><strong><strong>с</strong>x<em>йюд</em>чх</strong><s><strong>жюи</strong><em>и</em><strong>ьмт</strong>йц</s><em><strong>шб</strong><strong>еф</strong>утшж</em><em><em>ну</em><strong>ыа</strong>ьскючз</em></p></li><li><ul><li><ol></ol><ol></ol><p>ыйгг</p></li><li><ol></ol><ul></ul><p>ф</p></li><li><ol></ol><p>ч</p></li><li><ol></ol><ol></ol></li></ul><p><s><em>э</em>amqcfdzrg<em>т</em>з</s>b<em><s>энфны</s><s>гщ</s>ышя</em>uvpqzhn</p><ul><li><ul></ul><ol></ol><ol></ol></li><li><ol></ol><p>ящж</p><ul></ul></li><li><ol></ol><p>цлл</p></li><li><ol></ol><p>ъ</p></li></ul><p><strong><strong>ъ</strong><s>кл</s><s>счи</s>ра</strong><strong><s>ь</s>znъумъъзюкмц</strong><s><em>дюз</em><strong>эы</strong>ир</s><em><strong>ьуес</strong><s>йгтв</s>уеы</em></p></li></ol>"
        );

        checker.test(
                new UnorderedList(List.of(new ListItem(List.of(new OrderedList(List.of(new ListItem(List.of(new OrderedList(List.of()), new Paragraph(List.of(new Text("е"))))), new ListItem(List.of(new UnorderedList(List.of()), new OrderedList(List.of()))), new ListItem(List.of(new OrderedList(List.of()), new OrderedList(List.of()))), new ListItem(List.of(new Paragraph(List.of(new Text("нцйцць"))), new OrderedList(List.of()))), new ListItem(List.of(new Paragraph(List.of(new Text("м"))))))), new UnorderedList(List.of(new ListItem(List.of(new OrderedList(List.of()), new OrderedList(List.of()))), new ListItem(List.of(new OrderedList(List.of()), new Paragraph(List.of(new Text("ю"))))), new ListItem(List.of(new UnorderedList(List.of()), new Paragraph(List.of(new Text("щ"))))), new ListItem(List.of(new UnorderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new UnorderedList(List.of()))))), new Paragraph(List.of(new Strikeout(List.of(new Emphasis(List.of(new Text("зр"))), new Text("i"), new Text("и"), new Text("г"), new Text("с"))), new Strong(List.of(new Strong(List.of(new Text("шмрб"))), new Strong(List.of(new Text("ь"))), new Text("з"), new Text("з"), new Text("фь"))), new Text("ddw"), new Strong(List.of(new Emphasis(List.of(new Text("щ"))), new Strong(List.of(new Text("втъп"))), new Text("ш"), new Text("ч"), new Text("фяи"))), new Strong(List.of(new Emphasis(List.of(new Text("тел"))), new Text("н"), new Text("ь"), new Text("ддзюцщ"), new Text("пт"))))), new Paragraph(List.of(new Text("n"), new Text("zi"), new Strong(List.of(new Emphasis(List.of(new Text("ж"))), new Text("t"), new Text("ыеж"), new Text("ч"), new Text("г"))), new Text("kwt"), new Strong(List.of(new Strong(List.of(new Text("э"))), new Text("нх"), new Text("уи"), new Text("о"), new Text("п"))))), new UnorderedList(List.of(new ListItem(List.of(new OrderedList(List.of()), new OrderedList(List.of()))), new ListItem(List.of(new Paragraph(List.of(new Text("ж"))), new UnorderedList(List.of()))), new ListItem(List.of(new UnorderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new Paragraph(List.of(new Text("сс"))))), new ListItem(List.of(new Paragraph(List.of(new Text("т"))))))))), new ListItem(List.of(new UnorderedList(List.of(new ListItem(List.of(new UnorderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new UnorderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new OrderedList(List.of()), new OrderedList(List.of()))), new ListItem(List.of(new OrderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new Paragraph(List.of(new Text("щу"))))))), new OrderedList(List.of(new ListItem(List.of(new OrderedList(List.of()), new Paragraph(List.of(new Text("ир"))))), new ListItem(List.of(new OrderedList(List.of()), new OrderedList(List.of()))), new ListItem(List.of(new Paragraph(List.of(new Text("зоъ"))), new Paragraph(List.of(new Text("е"))))), new ListItem(List.of(new Paragraph(List.of(new Text("в"))), new UnorderedList(List.of()))), new ListItem(List.of(new UnorderedList(List.of()))))), new OrderedList(List.of(new ListItem(List.of(new Paragraph(List.of(new Text("сснюпия"))), new Paragraph(List.of(new Text("щ"))))), new ListItem(List.of(new OrderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new Paragraph(List.of(new Text("э"))), new OrderedList(List.of()))), new ListItem(List.of(new OrderedList(List.of()), new OrderedList(List.of()))), new ListItem(List.of(new OrderedList(List.of()))))), new UnorderedList(List.of(new ListItem(List.of(new Paragraph(List.of(new Text("м"))), new OrderedList(List.of()))), new ListItem(List.of(new UnorderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new OrderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new OrderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new UnorderedList(List.of()))))), new UnorderedList(List.of(new ListItem(List.of(new UnorderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new OrderedList(List.of()), new OrderedList(List.of()))), new ListItem(List.of(new Paragraph(List.of(new Text("е"))), new UnorderedList(List.of()))), new ListItem(List.of(new OrderedList(List.of()))), new ListItem(List.of(new UnorderedList(List.of()))))))), new ListItem(List.of(new Paragraph(List.of(new Strong(List.of(new Emphasis(List.of(new Text("п"))), new Text("l"), new Text("р"), new Text("п"), new Text("уерсы"))), new Strikeout(List.of(new Strikeout(List.of(new Text("к"))), new Text("rf"), new Text("екйюд"), new Text("чх"), new Text("яжюи"))), new Emphasis(List.of(new Strikeout(List.of(new Text("кьмт"))), new Strikeout(List.of(new Text("рщюереф"))), new Text("ут"), new Text("шж"), new Text("ину"))), new Strong(List.of(new Strong(List.of(new Text("дгб"))), new Emphasis(List.of(new Text("кю"))), new Text("чз"), new Text("мв"), new Text("зыйгг"))), new Strong(List.of(new Strikeout(List.of(new Text("ш"))), new Text("ф"), new Text("я"), new Text("ч"), new Text("ме"))))), new Paragraph(List.of(new Strikeout(List.of(new Emphasis(List.of(new Text("э"))), new Text("amqcfdzrg"), new Text("кыч"), new Text("к"), new Text("я"))), new Strikeout(List.of(new Strong(List.of(new Text("нфны"))), new Strikeout(List.of(new Text("гщ"))), new Text("ы"), new Text("шя"), new Text("е"))), new Strong(List.of(new Strong(List.of(new Text("ъю"))), new Emphasis(List.of(new Text("яхе"))), new Text("б"), new Text("бц"), new Text("еящж"))), new Text("cn"), new Emphasis(List.of(new Strong(List.of(new Text("як"))), new Text("въ"), new Text("оде"), new Text("кл"), new Text("еок"))))), new Paragraph(List.of(new Strikeout(List.of(new Strong(List.of(new Text("а"))), new Strong(List.of(new Text("иь"))), new Text("аш"), new Text("ъ"), new Text("умъъзюкмц"))), new Strikeout(List.of(new Emphasis(List.of(new Text("дюз"))), new Strong(List.of(new Text("эы"))), new Text("и"), new Text("р"), new Text("зьуес"))), new Strikeout(List.of(new Strikeout(List.of(new Text("и"))), new Strong(List.of(new Text("тв"))), new Text("у"), new Text("еы"), new Text("г"))), new Text("atsui"), new Strikeout(List.of(new Text("y"), new Text("щз"), new Text("н"), new Text("е"), new Text("э"))))), new Paragraph(List.of(new Emphasis(List.of(new Text("o"), new Text("rz"), new Text("к"), new Text("к"), new Text("б"))), new Emphasis(List.of(new Strong(List.of(new Text("ьх"))), new Emphasis(List.of(new Text("ил"))), new Text("ф"), new Text("пмгр"), new Text("и"))), new Emphasis(List.of(new Text("lhovy"), new Emphasis(List.of(new Text("ъайл"))), new Text("ь"), new Text("э"), new Text("п"))), new Strikeout(List.of(new Strong(List.of(new Text("щщ"))), new Strong(List.of(new Text("х"))), new Text("б"), new Text("е"), new Text("к"))), new Emphasis(List.of(new Strikeout(List.of(new Text("чяя"))), new Text("х"), new Text("я"), new Text("р"), new Text("ю"))))), new Paragraph(List.of(new Strikeout(List.of(new Emphasis(List.of(new Text("йл"))), new Emphasis(List.of(new Text("змл"))), new Text("б"), new Text("аж"), new Text("ъ"))), new Strong(List.of(new Strong(List.of(new Text("энян"))), new Emphasis(List.of(new Text("ю"))), new Text("п"), new Text("ымы"), new Text("ешьи"))), new Emphasis(List.of(new Strong(List.of(new Text("к"))), new Strikeout(List.of(new Text("яэ"))), new Text("п"), new Text("юзщ"), new Text("я"))), new Text("w"), new Emphasis(List.of(new Text("se"), new Text("о"), new Text("ъязе"), new Text("гзко"), new Text("ъ"))))))), new ListItem(List.of(new OrderedList(List.of(new ListItem(List.of(new Paragraph(List.of(new Text("ч"))), new Paragraph(List.of(new Text("пз"))))), new ListItem(List.of(new OrderedList(List.of()), new Paragraph(List.of(new Text("й"))))), new ListItem(List.of(new Paragraph(List.of(new Text("лчж"))), new Paragraph(List.of(new Text("чв"))))), new ListItem(List.of(new Paragraph(List.of(new Text("с"))), new OrderedList(List.of()))), new ListItem(List.of(new OrderedList(List.of()))))), new UnorderedList(List.of(new ListItem(List.of(new Paragraph(List.of(new Text("ь"))), new Paragraph(List.of(new Text("ъ"))))), new ListItem(List.of(new OrderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new UnorderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new Paragraph(List.of(new Text("вп"))), new Paragraph(List.of(new Text("р"))))), new ListItem(List.of(new OrderedList(List.of()))))), new Paragraph(List.of(new Text("ds"), new Emphasis(List.of(new Strikeout(List.of(new Text("дйгып"))), new Emphasis(List.of(new Text("и"))), new Text("сэ"), new Text("е"), new Text("юо"))), new Emphasis(List.of(new Strikeout(List.of(new Text("бвщ"))), new Text("d"), new Text("ъ"), new Text("ит"), new Text("бщ"))), new Emphasis(List.of(new Text("w"), new Strikeout(List.of(new Text("гсщ"))), new Text("ъ"), new Text("срцч"), new Text("хе"))), new Text("m"))), new OrderedList(List.of(new ListItem(List.of(new UnorderedList(List.of()), new OrderedList(List.of()))), new ListItem(List.of(new UnorderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new OrderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new Paragraph(List.of(new Text("е"))), new OrderedList(List.of()))), new ListItem(List.of(new UnorderedList(List.of()))))), new UnorderedList(List.of(new ListItem(List.of(new OrderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new Paragraph(List.of(new Text("оото"))), new OrderedList(List.of()))), new ListItem(List.of(new OrderedList(List.of()), new OrderedList(List.of()))), new ListItem(List.of(new UnorderedList(List.of()))), new ListItem(List.of(new UnorderedList(List.of()))))))), new ListItem(List.of(new Paragraph(List.of(new Emphasis(List.of(new Emphasis(List.of(new Text("я"))), new Strong(List.of(new Text("сшъ"))), new Text("лм"), new Text("ы"), new Text("рц"))), new Emphasis(List.of(new Strikeout(List.of(new Text("я"))), new Strikeout(List.of(new Text("ъ"))), new Text("п"), new Text("дхдэ"), new Text("щэ"))), new Emphasis(List.of(new Text("dtt"), new Emphasis(List.of(new Text("дрм"))), new Text("в"), new Text("яешц"), new Text("йшй"))), new Strong(List.of(new Strong(List.of(new Text("мив"))), new Text("u"), new Text("у"), new Text("к"), new Text("б"))), new Strikeout(List.of(new Text("c"), new Text("э"), new Text("м"), new Text("п"), new Text("о"))))), new UnorderedList(List.of(new ListItem(List.of(new OrderedList(List.of()), new OrderedList(List.of()))), new ListItem(List.of(new Paragraph(List.of(new Text("х"))), new Paragraph(List.of(new Text("й"))))), new ListItem(List.of(new Paragraph(List.of(new Text("эя"))), new UnorderedList(List.of()))), new ListItem(List.of(new UnorderedList(List.of()), new Paragraph(List.of(new Text("ф"))))), new ListItem(List.of(new OrderedList(List.of()))))), new OrderedList(List.of(new ListItem(List.of(new OrderedList(List.of()), new Paragraph(List.of(new Text("щ"))))), new ListItem(List.of(new Paragraph(List.of(new Text("чи"))), new OrderedList(List.of()))), new ListItem(List.of(new Paragraph(List.of(new Text("к"))), new OrderedList(List.of()))), new ListItem(List.of(new UnorderedList(List.of()), new OrderedList(List.of()))), new ListItem(List.of(new Paragraph(List.of(new Text("ф"))))))), new OrderedList(List.of(new ListItem(List.of(new OrderedList(List.of()), new UnorderedList(List.of()))), new ListItem(List.of(new Paragraph(List.of(new Text("м"))), new Paragraph(List.of(new Text("щцс"))))), new ListItem(List.of(new Paragraph(List.of(new Text("вус"))), new Paragraph(List.of(new Text("я"))))), new ListItem(List.of(new Paragraph(List.of(new Text("кр"))))), new ListItem(List.of(new UnorderedList(List.of()))))), new UnorderedList(List.of(new ListItem(List.of(new UnorderedList(List.of()), new Paragraph(List.of(new Text("я"))))), new ListItem(List.of(new UnorderedList(List.of()), new Paragraph(List.of(new Text("гр"))))), new ListItem(List.of(new Paragraph(List.of(new Text("ж"))), new UnorderedList(List.of()))), new ListItem(List.of(new UnorderedList(List.of()))), new ListItem(List.of(new OrderedList(List.of()))))))))),
                "<ul><li><ol><li><ol></ol><p>е</p></li><li><ul></ul><ol></ol></li><li><ol></ol><ol></ol></li><li><p>нцйцць</p><ol></ol></li><li><p>м</p></li></ol><ul><li><ol></ol><ol></ol></li><li><ol></ol><p>ю</p></li><li><ul></ul><p>щ</p></li><li><ul></ul><ul></ul></li><li><ul></ul></li></ul><p><s><em>зр</em>iигс</s><strong><strong>шмрб</strong><strong>ь</strong>ззфь</strong>ddw<strong><em>щ</em><strong>втъп</strong>шчфяи</strong><strong><em>тел</em>ньддзюцщпт</strong></p><p>nzi<strong><em>ж</em>tыежчг</strong>kwt<strong><strong>э</strong>нхуиоп</strong></p><ul><li><ol></ol><ol></ol></li><li><p>ж</p><ul></ul></li><li><ul></ul><ul></ul></li><li><p>сс</p></li><li><p>т</p></li></ul></li><li><ul><li><ul></ul><ul></ul></li><li><ul></ul><ul></ul></li><li><ol></ol><ol></ol></li><li><ol></ol><ul></ul></li><li><p>щу</p></li></ul><ol><li><ol></ol><p>ир</p></li><li><ol></ol><ol></ol></li><li><p>зоъ</p><p>е</p></li><li><p>в</p><ul></ul></li><li><ul></ul></li></ol><ol><li><p>сснюпия</p><p>щ</p></li><li><ol></ol><ul></ul></li><li><p>э</p><ol></ol></li><li><ol></ol><ol></ol></li><li><ol></ol></li></ol><ul><li><p>м</p><ol></ol></li><li><ul></ul><ul></ul></li><li><ol></ol><ul></ul></li><li><ol></ol><ul></ul></li><li><ul></ul></li></ul><ul><li><ul></ul><ul></ul></li><li><ol></ol><ol></ol></li><li><p>е</p><ul></ul></li><li><ol></ol></li><li><ul></ul></li></ul></li><li><p><strong><em>п</em>lрпуерсы</strong><s><s>к</s>rfекйюдчхяжюи</s><em><s>кьмт</s><s>рщюереф</s>утшжину</em><strong><strong>дгб</strong><em>кю</em>чзмвзыйгг</strong><strong><s>ш</s>фячме</strong></p><p><s><em>э</em>amqcfdzrgкычкя</s><s><strong>нфны</strong><s>гщ</s>ышяе</s><strong><strong>ъю</strong><em>яхе</em>ббцеящж</strong>cn<em><strong>як</strong>въодеклеок</em></p><p><s><strong>а</strong><strong>иь</strong>ашъумъъзюкмц</s><s><em>дюз</em><strong>эы</strong>ирзьуес</s><s><s>и</s><strong>тв</strong>уеыг</s>atsui<s>yщзнеэ</s></p><p><em>orzккб</em><em><strong>ьх</strong><em>ил</em>фпмгри</em><em>lhovy<em>ъайл</em>ьэп</em><s><strong>щщ</strong><strong>х</strong>бек</s><em><s>чяя</s>хярю</em></p><p><s><em>йл</em><em>змл</em>бажъ</s><strong><strong>энян</strong><em>ю</em>пымыешьи</strong><em><strong>к</strong><s>яэ</s>пюзщя</em>w<em>seоъязегзкоъ</em></p></li><li><ol><li><p>ч</p><p>пз</p></li><li><ol></ol><p>й</p></li><li><p>лчж</p><p>чв</p></li><li><p>с</p><ol></ol></li><li><ol></ol></li></ol><ul><li><p>ь</p><p>ъ</p></li><li><ol></ol><ul></ul></li><li><ul></ul><ul></ul></li><li><p>вп</p><p>р</p></li><li><ol></ol></li></ul><p>ds<em><s>дйгып</s><em>и</em>сэеюо</em><em><s>бвщ</s>dъитбщ</em><em>w<s>гсщ</s>ъсрцчхе</em>m</p><ol><li><ul></ul><ol></ol></li><li><ul></ul><ul></ul></li><li><ol></ol><ul></ul></li><li><p>е</p><ol></ol></li><li><ul></ul></li></ol><ul><li><ol></ol><ul></ul></li><li><p>оото</p><ol></ol></li><li><ol></ol><ol></ol></li><li><ul></ul></li><li><ul></ul></li></ul></li><li><p><em><em>я</em><strong>сшъ</strong>лмырц</em><em><s>я</s><s>ъ</s>пдхдэщэ</em><em>dtt<em>дрм</em>вяешцйшй</em><strong><strong>мив</strong>uукб</strong><s>cэмпо</s></p><ul><li><ol></ol><ol></ol></li><li><p>х</p><p>й</p></li><li><p>эя</p><ul></ul></li><li><ul></ul><p>ф</p></li><li><ol></ol></li></ul><ol><li><ol></ol><p>щ</p></li><li><p>чи</p><ol></ol></li><li><p>к</p><ol></ol></li><li><ul></ul><ol></ol></li><li><p>ф</p></li></ol><ol><li><ol></ol><ul></ul></li><li><p>м</p><p>щцс</p></li><li><p>вус</p><p>я</p></li><li><p>кр</p></li><li><ul></ul></li></ol><ul><li><ul></ul><p>я</p></li><li><ul></ul><p>гр</p></li><li><p>ж</p><ul></ul></li><li><ul></ul></li><li><ol></ol></li></ul></li></ul>"
        );

        checkTypes();
    }

    private static OrderedList ol(final ListItem... items) {
        return new OrderedList(List.of(items));
    }

    private static String ol(final String... items) {
        return list("ol", items);
    }

    private static UnorderedList ul(final ListItem... items) {
        return new UnorderedList(List.of(items));
    }

    private static String ul(final String... items) {
        return list("ul", items);
    }

    private static String list(final String type, final String[] items) {
        return "<" + type + ">" + Stream.of(items).map(item -> "<li>" + item + "</li>").collect(Collectors.joining()) + "</" + type + ">";
    }

    private static Class<?> loadClass(final String name) {
        try {
            return Class.forName(name);
        } catch (final ClassNotFoundException e) {
            throw Asserts.error("Cannot find class %s: %s", name, e);
        }
    }

    private static Map<String, Class<?>> loadClasses(final String... names) {
        return Arrays.stream(names)
                .collect(Collectors.toUnmodifiableMap(Function.identity(), name -> loadClass("markup." + name)));
    }

    private static void checkTypes() {
        final Map<String, Class<?>> classes = loadClasses("Text", "Emphasis", "Strikeout", "Strong", "Paragraph", "OrderedList", "UnorderedList", "ListItem");
        final String[] inlineClasses = {"Text", "Emphasis", "Strikeout", "Strong"};

        checkConstructor(classes, "OrderedList", "ListItem");
        checkConstructor(classes, "UnorderedList", "ListItem");
        checkConstructor(classes, "ListItem", "OrderedList", "UnorderedList", "Paragraph");
        Stream.of("Paragraph", "Emphasis", "Strong", "Strikeout")
                .forEach(parent -> checkConstructor(classes, parent, inlineClasses));
    }

    private static void checkConstructor(final Map<String, Class<?>> classes, final String parent, final String... children) {
        new TypeChecker(classes, parent, children).checkConstructor();
    }

    private static class TypeChecker {
        private final Map<String, Class<?>> classes;
        private final Set<Class<?>> children;
        private final Class<?> parent;

        public TypeChecker(final Map<String, Class<?>> classes, final String parent, final String[] children) {
            this.classes = classes;
            this.children = Arrays.stream(children).map(classes::get).collect(Collectors.toUnmodifiableSet());
            this.parent = Objects.requireNonNull(classes.get(parent));
        }

        private void checkClassType(final Class<?> classType) {
            final Predicate<Class<?>> isAssignableFrom = classType::isAssignableFrom;
            checkType(parent, Predicate.not(isAssignableFrom), "not ", children.stream());
            checkType(parent, isAssignableFrom, "", classes.values().stream().filter(Predicate.not(children::contains)));
        }

        private static void checkType(final Class<?> parent, final Predicate<Class<?>> predicate, final String not, final Stream<Class<?>> children) {
            children.filter(predicate).findAny().ifPresent(child -> {
                throw Asserts.error("%s is %scompatible with child of type %s", parent, not, child);
            });
        }

        @SuppressWarnings("ChainOfInstanceofChecks")
        private void checkParametrizedType(final ParameterizedType type) {
            final Type actualType = type.getActualTypeArguments()[0];
            if (actualType instanceof Class) {
                checkClassType((Class<?>) actualType);
            } else if (actualType instanceof WildcardType) {
                for (final Type boundType : ((WildcardType) actualType).getUpperBounds()) {
                    if (boundType instanceof Class) {
                        checkClassType((Class<?>) boundType);
                    } else {
                        throw Asserts.error("Unsupported wildcard bound type in %s(List<...>): %s", parent, boundType);
                    }
                }
            } else {
                throw Asserts.error("Unsupported type argument type in %s(List<...>): %s", parent, actualType);
            }
        }

        @SuppressWarnings("ChainOfInstanceofChecks")
        private void checkConstructor() {
            try {
                final Type argType = parent.getConstructor(List.class).getGenericParameterTypes()[0];
                if (argType instanceof ParameterizedType) {
                    checkParametrizedType((ParameterizedType) argType);
                } else if (argType instanceof Class) {
                    throw Asserts.error("Raw List type in %s(List)", parent.getName());
                } else {
                    throw Asserts.error("Unsupported argument type in %s(List<...>): %s", parent.getName(), argType);
                }
            } catch (final NoSuchMethodException e) {
                throw Asserts.error("Missing %s(List<...>) constructor: %s", parent.getName(), e);
            }
        }
    }

    public static void main(final String... args) {
        MarkupTest.main(args);
        SELECTOR.main(args);
    }
}
