package markup;

import java.util.List;

public class Strong extends ParagraphMarking {
    @Override
    protected String markdownDelimiter() {
        return "__";
    }

    @Override
    protected String texDelimiter() {
        return "\\textbf";
    }

    public Strong(List<ParagraphItem> items) {
        super(items);
    }
}