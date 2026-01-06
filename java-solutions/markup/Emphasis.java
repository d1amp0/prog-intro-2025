package markup;

import java.util.List;

public class Emphasis extends ParagraphMarking {
    @Override
    protected String markdownDelimiter() {
        return "*";
    }

    @Override
    protected String texDelimiter() {
        return "\\emph";
    }

    public Emphasis(List<ParagraphItem> items) {
        super(items);
    }
}