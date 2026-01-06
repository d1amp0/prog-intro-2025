package base;

import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class Log {
    private final Pattern LINES = Pattern.compile("\n");
    private int indent;

    public static Supplier<Void> action(final Runnable action) {
        return () -> {
            action.run();
            return null;
        };
    }

    public void scope(final String name, final Runnable action) {
        scope(name, action(action));
    }

    public <T> T scope(final String name, final Supplier<T> action) {
        println(name);
        indent++;
        try {
            return silentScope(name, action);
        } finally {
            indent--;
        }
    }

    public <T> T silentScope(final String ignoredName, final Supplier<T> action) {
        return action.get();
    }

    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    public void println(final Object value) {
        for (final String line : LINES.split(String.valueOf(value))) {
            System.out.println(indent() + line);
        }
    }

    public void format(final String format, final Object... args) {
        println(String.format(format,args));
    }

    private String indent() {
        return "    ".repeat(indent);
    }

    protected int getIndent() {
        return indent;
    }
}
