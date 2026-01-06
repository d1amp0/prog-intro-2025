package markup;

import base.Selector;
import base.TestCounter;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class MarkupTest {
    private static final Consumer<TestCounter> MARKDOWN = MarkupTest.variant(
            "Markdown", Map.of(
                    "&[", "", "&]", "",
                    "<", "", ">", ""
            )
    );

    private static final Consumer<TestCounter> HTML = MarkupTest.variant(
            "Html", Map.of(
                    "&[", "<p>", "&]", "</p>",
                    "*<", "<em>", "*>", "</em>",
                    "__<", "<strong>", "__>", "</strong>",
                    "~<", "<s>", "~>", "</s>"
            )
    );

    public static final Selector SELECTOR = new Selector(MarkupTest.class)
            .variant("Base", MARKDOWN)
            .variant("3637", MARKDOWN)
            .variant("3839", MARKDOWN)
            .variant("3435", HTML)
            .variant("3233", HTML)
            .variant("4142", MARKDOWN)
            .variant("4749", MARKDOWN)

            ;

    public static Consumer<TestCounter> variant(final String name, final Map<String, String> mapping) {
        return MarkupTester.variant(MarkupTest::test, name, mapping);
    }

    private MarkupTest() {
    }

    public static void test(final MarkupTester.Checker checker) {
        test(checker, new Paragraph(List.of(new Text("Hello"))), "Hello");
        test(checker, new Paragraph(List.of(new Emphasis(List.of(new Text("Hello"))))), "*<Hello*>");
        test(checker, new Paragraph(List.of(new Strong(List.of(new Text("Hello"))))), "__<Hello__>");
        test(checker, new Paragraph(List.of(new Strikeout(List.of(new Text("Hello"))))), "~<Hello~>");

        final Paragraph paragraph = new Paragraph(List.of(
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
        test(checker, paragraph, "__<1~<2*<34*>5~>6__>");
        test(
                checker,
                new Paragraph(List.of(new Strong(List.of(
                        new Text("sdq"),
                        new Strikeout(List.of(new Emphasis(List.of(new Text("r"))), new Text("vavc"))),
                        new Text("zg")
                )))),
                "__<sdq~<*<r*>vavc~>zg__>"
        );
        test(
                checker,
                new Paragraph(List.of(new Strikeout(List.of(new Strong(List.of(new Strikeout(List.of(new Text("е"), new Text("е"), new Text("г"))), new Text("ftje"), new Strong(List.of(new Text("йцць"), new Text("р"))))), new Strong(List.of(new Strikeout(List.of(new Text("д"), new Text("б"), new Text("е"))), new Strong(List.of(new Text("лъ"), new Text("шщ"))), new Strong(List.of(new Text("б"), new Text("еи"))))), new Emphasis(List.of(new Emphasis(List.of(new Text("м"), new Text("к"))), new Emphasis(List.of(new Text("уаужш"), new Text("ш"))), new Strong(List.of(new Text("рб"), new Text("щ"))))))), new Text("a"), new Strikeout(List.of(new Text("no"), new Text("ddw"), new Strong(List.of(new Emphasis(List.of(new Text("щ"), new Text("ча"))), new Emphasis(List.of(new Text("ъп"), new Text("ш"))), new Text("psk"))))))),
                "~<__<~<еег~>ftje__<йццьр__>__>__<~<дбе~>__<лъшщ__>__<беи__>__>*<*<мк*>*<уаужшш*>__<рбщ__>*>~>a~<noddw__<*<щча*>*<ъпш*>psk__>~>"
        );
        test(
                checker,
                new Paragraph(List.of(new Strikeout(List.of(new Strong(List.of(new Strikeout(List.of(new Emphasis(List.of(new Text("об"))), new Strikeout(List.of(new Text("ц"))), new Text("зснцйцць"), new Text("р"), new Text("а"))), new Strikeout(List.of(new Strikeout(List.of(new Text("б"))), new Strikeout(List.of(new Text("ялъ"))), new Text("шщ"), new Text("ф"), new Text("м"))), new Emphasis(List.of(new Emphasis(List.of(new Text("узр"))), new Text("i"), new Text("и"), new Text("г"), new Text("с"))), new Strong(List.of(new Strong(List.of(new Text("шмрб"))), new Strong(List.of(new Text("ь"))), new Text("з"), new Text("з"), new Text("фь"))), new Text("ddw"))), new Strong(List.of(new Emphasis(List.of(new Emphasis(List.of(new Text("ввтъп"))), new Strong(List.of(new Text("ш"))), new Text("хте"), new Text("чюе"), new Text("х"))), new Text("g"), new Strikeout(List.of(new Strikeout(List.of(new Text("ддзюцщ"))), new Strong(List.of(new Text("к"))), new Text("йщ"), new Text("э"), new Text("то"))), new Strong(List.of(new Emphasis(List.of(new Text("ж"))), new Text("t"), new Text("ыеж"), new Text("ч"), new Text("г"))), new Text("kwt"))), new Strong(List.of(new Strong(List.of(new Emphasis(List.of(new Text("ш"))), new Strong(List.of(new Text("х"))), new Text("уи"), new Text("о"), new Text("п"))), new Emphasis(List.of(new Text("zn"), new Strong(List.of(new Text("нш"))), new Text("диуьг"), new Text("вй"), new Text("шш"))), new Strong(List.of(new Emphasis(List.of(new Text("ьмша"))), new Emphasis(List.of(new Text("у"))), new Text("в"), new Text("у"), new Text("ир"))), new Emphasis(List.of(new Strikeout(List.of(new Text("я"))), new Strikeout(List.of(new Text("зоъ"))), new Text("эн"), new Text("ъ"), new Text("ьо"))), new Text("cqqzbhtn"))), new Text("i"), new Strong(List.of(new Text("i"), new Strikeout(List.of(new Strong(List.of(new Text("ш"))), new Strong(List.of(new Text("к"))), new Text("ж"), new Text("б"), new Text("ащ"))), new Strikeout(List.of(new Strikeout(List.of(new Text("пян"))), new Emphasis(List.of(new Text("ц"))), new Text("ю"), new Text("дрк"), new Text("лщ"))), new Strong(List.of(new Text("xywa"), new Text("ряэк"), new Text("п"), new Text("э"), new Text("т"))), new Strong(List.of(new Strikeout(List.of(new Text("е"))), new Text("чб"), new Text("зс"), new Text("екйюд"), new Text("чх"))))))), new Strikeout(List.of(new Strong(List.of(new Strong(List.of(new Strong(List.of(new Text("юи"))), new Emphasis(List.of(new Text("и"))), new Text("п"), new Text("вх"), new Text("ф"))), new Strong(List.of(new Strong(List.of(new Text("щюереф"))), new Text("otvic"), new Text("ж"), new Text("уыа"), new Text("ьскю"))), new Text("x"), new Strikeout(List.of(new Emphasis(List.of(new Text("ж"))), new Strikeout(List.of(new Text("зыйгг"))), new Text("о"), new Text("ш"), new Text("ф"))), new Text("zf"))), new Emphasis(List.of(new Text("a"), new Strikeout(List.of(new Emphasis(List.of(new Text("э"))), new Text("amqcfdzrg"), new Text("кыч"), new Text("к"), new Text("я"))), new Strikeout(List.of(new Strong(List.of(new Text("нфны"))), new Strikeout(List.of(new Text("гщ"))), new Text("ы"), new Text("шя"), new Text("е"))), new Strong(List.of(new Strong(List.of(new Text("ъю"))), new Emphasis(List.of(new Text("яхе"))), new Text("б"), new Text("бц"), new Text("еящж"))), new Text("cn"))), new Emphasis(List.of(new Strong(List.of(new Strong(List.of(new Text("л"))), new Text("wl"), new Text("оде"), new Text("кл"), new Text("еок"))), new Strikeout(List.of(new Strikeout(List.of(new Text("яяиь"))), new Strong(List.of(new Text("ик"))), new Text("юью"), new Text("ь"), new Text("э"))), new Emphasis(List.of(new Strikeout(List.of(new Text("жп"))), new Emphasis(List.of(new Text("ц"))), new Text("ндюз"), new Text("ч"), new Text("н"))), new Text("r"), new Strikeout(List.of(new Strikeout(List.of(new Text("зьуес"))), new Text("к"), new Text("и"), new Text("к"), new Text("й"))))), new Strikeout(List.of(new Emphasis(List.of(new Strikeout(List.of(new Text("еы"))), new Emphasis(List.of(new Text("б"))), new Text("сйсыр"), new Text("я"), new Text("т"))), new Emphasis(List.of(new Emphasis(List.of(new Text("з"))), new Strong(List.of(new Text("ахыы"))), new Text("х"), new Text("м"), new Text("п"))), new Strikeout(List.of(new Text("b"), new Text("o"), new Text("шьх"), new Text("йз"), new Text("ж"))), new Text("udlh"), new Strikeout(List.of(new Strikeout(List.of(new Text("п"))), new Text("хъфоз"), new Text("е"), new Text("ыф"), new Text("ю"))))), new Text("z"))), new Text("hy"), new Strong(List.of(new Text("tyv"), new Text("x"), new Strikeout(List.of(new Text("vzb"), new Strong(List.of(new Text("oi"), new Text("r"), new Text("ю"), new Text("с"), new Text("еппзмл"))), new Text("r"), new Emphasis(List.of(new Strikeout(List.of(new Text("игс"))), new Emphasis(List.of(new Text("нян"))), new Text("ю"), new Text("с"), new Text("цлъ"))), new Text("rptq"))), new Emphasis(List.of(new Strong(List.of(new Text("u"), new Strong(List.of(new Text("кще"))), new Text("пхте"), new Text("у"), new Text("з"))), new Text("zbmflu"), new Strikeout(List.of(new Strong(List.of(new Text("л"))), new Emphasis(List.of(new Text("ко"))), new Text("ъ"), new Text("щ"), new Text("жч"))), new Strong(List.of(new Strong(List.of(new Text("ж"))), new Strikeout(List.of(new Text("еъ"))), new Text("в"), new Text("ф"), new Text("йб"))), new Text("kvuf"))), new Strikeout(List.of(new Text("azn"), new Strikeout(List.of(new Strong(List.of(new Text("ъ"))), new Emphasis(List.of(new Text("ре"))), new Text("йч"), new Text("н"), new Text("ир"))), new Emphasis(List.of(new Emphasis(List.of(new Text("с"))), new Strong(List.of(new Text("щ"))), new Text("ъсбчиюзи"), new Text("сэ"), new Text("е"))), new Strikeout(List.of(new Emphasis(List.of(new Text("о"))), new Text("г"), new Text("бвщ"), new Text("пр"), new Text("йвъч"))), new Text("c"))))), new Strong(List.of(new Strikeout(List.of(new Strikeout(List.of(new Emphasis(List.of(new Text("жбфц"))), new Strong(List.of(new Text("рцч"))), new Text("хе"), new Text("ж"), new Text("ы"))), new Strikeout(List.of(new Emphasis(List.of(new Text("я"))), new Emphasis(List.of(new Text("мн"))), new Text("яе"), new Text("е"), new Text("дхпг"))), new Emphasis(List.of(new Emphasis(List.of(new Text("нй"))), new Text("gf"), new Text("и"), new Text("хю"), new Text("ця"))), new Strong(List.of(new Emphasis(List.of(new Text("о"))), new Emphasis(List.of(new Text("ъ"))), new Text("лм"), new Text("ы"), new Text("рц"))), new Emphasis(List.of(new Strikeout(List.of(new Text("я"))), new Text("ыл"), new Text("г"), new Text("я"), new Text("эй"))))), new Text("qi"), new Emphasis(List.of(new Text("dtt"), new Emphasis(List.of(new Strong(List.of(new Text("пв"))), new Text("i"), new Text("яешц"), new Text("йшй"), new Text("щмив"))), new Text("u"), new Text("d"), new Strikeout(List.of(new Strikeout(List.of(new Text("о"))), new Text("иов"), new Text("к"), new Text("кои"), new Text("яс"))))), new Strikeout(List.of(new Emphasis(List.of(new Text("j"), new Strong(List.of(new Text("эя"))), new Text("шыф"), new Text("дрн"), new Text("щ"))), new Text("j"), new Strong(List.of(new Emphasis(List.of(new Text("ю"))), new Strikeout(List.of(new Text("чцин"))), new Text("сф"), new Text("з"), new Text("юэи"))), new Emphasis(List.of(new Emphasis(List.of(new Text("цс"))), new Text("ювус"), new Text("ъ"), new Text("щэны"), new Text("б"))), new Emphasis(List.of(new Text("cbogf"), new Text("э"), new Text("ж"), new Text("ш"), new Text("м"))))), new Strikeout(List.of(new Strong(List.of(new Strong(List.of(new Text("ф"))), new Text("w"), new Text("цеъ"), new Text("н"), new Text("ем"))), new Strikeout(List.of(new Strikeout(List.of(new Text("л"))), new Strong(List.of(new Text("э"))), new Text("лд"), new Text("эд"), new Text("л"))), new Emphasis(List.of(new Emphasis(List.of(new Text("уг"))), new Strikeout(List.of(new Text("зп"))), new Text("юб"), new Text("сгы"), new Text("шю"))), new Strikeout(List.of(new Emphasis(List.of(new Text("рйей"))), new Text("с"), new Text("зюй"), new Text("р"), new Text("в"))), new Emphasis(List.of(new Text("p"), new Text("у"), new Text("на"), new Text("б"), new Text("х"))))))))),
                "~<__<~<*<об*>~<ц~>зснцйццьра~>~<~<б~>~<ялъ~>шщфм~>*<*<узр*>iигс*>__<__<шмрб__>__<ь__>ззфь__>ddw__>__<*<*<ввтъп*>__<ш__>хтечюех*>g~<~<ддзюцщ~>__<к__>йщэто~>__<*<ж*>tыежчг__>kwt__>__<__<*<ш*>__<х__>уиоп__>*<zn__<нш__>диуьгвйшш*>__<*<ьмша*>*<у*>вуир__>*<~<я~>~<зоъ~>энъьо*>cqqzbhtn__>i__<i~<__<ш__>__<к__>жбащ~>~<~<пян~>*<ц*>юдрклщ~>__<xywaряэкпэт__>__<~<е~>чбзсекйюдчх__>__>~>~<__<__<__<юи__>*<и*>пвхф__>__<__<щюереф__>otvicжуыаьскю__>x~<*<ж*>~<зыйгг~>ошф~>zf__>*<a~<*<э*>amqcfdzrgкычкя~>~<__<нфны__>~<гщ~>ышяе~>__<__<ъю__>*<яхе*>ббцеящж__>cn*>*<__<__<л__>wlодеклеок__>~<~<яяиь~>__<ик__>юьюьэ~>*<~<жп~>*<ц*>ндюзчн*>r~<~<зьуес~>кикй~>*>~<*<~<еы~>*<б*>сйсырят*>*<*<з*>__<ахыы__>хмп*>~<boшьхйзж~>udlh~<~<п~>хъфозеыфю~>~>z~>hy__<tyvx~<vzb__<oirюсеппзмл__>r*<~<игс~>*<нян*>юсцлъ*>rptq~>*<__<u__<кще__>пхтеуз__>zbmflu~<__<л__>*<ко*>ъщжч~>__<__<ж__>~<еъ~>вфйб__>kvuf*>~<azn~<__<ъ__>*<ре*>йчнир~>*<*<с*>__<щ__>ъсбчиюзисэе*>~<*<о*>гбвщпрйвъч~>c~>__>__<~<~<*<жбфц*>__<рцч__>хежы~>~<*<я*>*<мн*>яеедхпг~>*<*<нй*>gfихюця*>__<*<о*>*<ъ*>лмырц__>*<~<я~>ылгяэй*>~>qi*<dtt*<__<пв__>iяешцйшйщмив*>ud~<~<о~>иовккоияс~>*>~<*<j__<эя__>шыфдрнщ*>j__<*<ю*>~<чцин~>сфзюэи__>*<*<цс*>ювусъщэныб*>*<cbogfэжшм*>~>~<__<__<ф__>wцеънем__>~<~<л~>__<э__>лдэдл~>*<*<уг*>~<зп~>юбсгышю*>~<*<рйей*>сзюйрв~>*<pунабх*>~>__>"
        );
    }

    private static void test(final MarkupTester.Checker checker, final Paragraph paragraph, final String template) {
        checker.test(paragraph, String.format("&[%s&]", template));
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}
