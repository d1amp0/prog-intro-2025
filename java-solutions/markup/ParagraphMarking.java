package markup;

import java.util.List;

public abstract class ParagraphMarking implements ParagraphItem {
    protected abstract String markdownDelimiter();

    protected abstract String texDelimiter();

    private final List<ParagraphItem> items;

    public ParagraphMarking(List<ParagraphItem> items) {
        this.items = items;
    }

    @Override
    public void toMarkdown(StringBuilder sb) {
        sb.append(markdownDelimiter());
        for (ParagraphItem item : items) {
            item.toMarkdown(sb);
        }
        sb.append(markdownDelimiter());
    }

    @Override
    public void toTex(StringBuilder sb) {
        sb.append(texDelimiter()).append("{");
        for (ParagraphItem item : items) {
            item.toTex(sb);
        }
        sb.append("}");
    }
}