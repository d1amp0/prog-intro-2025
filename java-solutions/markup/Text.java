package markup;

public class Text implements ParagraphItem {
    private final String text;

    public Text(String text) {
        this.text = text;
    }

    @Override
    public void toMarkdown(StringBuilder sb) {
        sb.append(text);
    }

    @Override
    public void toTex(StringBuilder sb) {
        toMarkdown(sb);
    }
}