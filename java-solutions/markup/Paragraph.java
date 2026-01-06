package markup;

import java.util.List;

public class Paragraph implements MarkdownInterface, ListItemItem {
    private final List<ParagraphItem> items;

    public Paragraph(List<ParagraphItem> items) {
        this.items = items;
    }

    @Override
    public void toMarkdown(StringBuilder sb) {
        for (ParagraphItem item : items) {
            item.toMarkdown(sb);
        }
    }

    @Override
    public void toTex(StringBuilder sb) {
        sb.append("\\par{}");
        for (ParagraphItem item : items) {
            item.toTex(sb);
        }
    }
}