package markup;

import java.util.List;

public class OrderedList extends ListMarking {
    @Override
    protected String word() {
        return "enumerate";
    }

    public OrderedList(List<ListItem> items) {
        super(items);
    }
}