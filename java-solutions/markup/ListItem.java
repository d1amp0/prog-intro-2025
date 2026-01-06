package markup;

import java.util.List;

public class ListItem implements TexInterface {
    private final List<ListItemItem> items;

    public ListItem(List<ListItemItem> items) {
        this.items = items;
    }

    @Override
    public void toTex(StringBuilder sb) {
        sb.append("\\item ");
        for (ListItemItem item : items) {
            item.toTex(sb);
        }
    }
}