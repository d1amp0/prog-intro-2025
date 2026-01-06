package markup;

import java.util.List;

public class Strikeout extends ParagraphMarking {
    @Override
    protected String markdownDelimiter() {
        return "~";
    }

    @Override
    protected String texDelimiter() {
        return "\\textst";
    }

    public Strikeout(List<ParagraphItem> items) {
        super(items);
    }
}