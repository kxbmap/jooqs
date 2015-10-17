package jooqs.play;

import java.io.Serializable;
import java.lang.annotation.Annotation;

@SuppressWarnings("ClassExplicitlyAnnotation")
public class NamedDatabaseImpl implements NamedDatabase, Serializable {

    private static final long serialVersionUID = -762564255433311352L;

    private final String value;

    public NamedDatabaseImpl(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return NamedDatabase.class;
    }

    @Override
    public int hashCode() {
        // This is specified in java.lang.Annotation.
        return (127 * "value".hashCode()) ^ value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NamedDatabase && value.equals(((NamedDatabase) obj).value());
    }

    @Override
    public String toString() {
        return "@" + NamedDatabase.class.getName() + "(value=" + value + ")";
    }

}
