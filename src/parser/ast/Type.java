package parser.ast;

/**
 * Represents a type in the Java subset.
 */
public class Type {
    public enum Kind {
        INT, BOOLEAN, STRING, DOUBLE, LONG, CHAR, VOID, CLASS
    }

    private final Kind kind;
    private final String className;  // For CLASS kind

    public Type(Kind kind) {
        this.kind = kind;
        this.className = null;
    }

    public Type(String className) {
        this.kind = Kind.CLASS;
        this.className = className;
    }

    public Kind getKind() {
        return kind;
    }

    public String getClassName() {
        return className;
    }

    public boolean isVoid() {
        return kind == Kind.VOID;
    }

    public boolean isPrimitive() {
        return kind != Kind.CLASS && kind != Kind.VOID;
    }

    @Override
    public String toString() {
        if (kind == Kind.CLASS) {
            return className;
        }
        return kind.toString().toLowerCase();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Type)) return false;
        Type other = (Type) obj;
        if (kind != other.kind) return false;
        if (kind == Kind.CLASS) {
            return className != null && className.equals(other.className);
        }
        return true;
    }

    @Override
    public int hashCode() {
        return kind.hashCode() * 31 + (className != null ? className.hashCode() : 0);
    }
}
