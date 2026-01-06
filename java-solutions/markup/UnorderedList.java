package markup;

import java.util.List;

public class UnorderedList extends ListMarking {
    @Override
    protected String word() {
        return "itemize";
    }

    public UnorderedList(List<ListItem> items) {
        super(items);
    }
}