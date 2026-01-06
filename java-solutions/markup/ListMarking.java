package markup;

import java.util.List;

public abstract class ListMarking implements ListItemItem {
    protected abstract String word();

    private final List<ListItem> items;

    public ListMarking(List<ListItem> items) {
        this.items = items;
    }

    @Override
    public void toTex(StringBuilder sb) {
        sb.append("\\begin{").append(word()).append("}");
        for (ListItem item : items) {
            item.toTex(sb);
        }
        sb.append("\\end{").append(word()).append("}");
    }
}