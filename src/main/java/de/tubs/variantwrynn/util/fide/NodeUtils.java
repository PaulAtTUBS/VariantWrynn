package de.tubs.variantwrynn.util.fide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.prop4j.And;
import org.prop4j.Literal;
import org.prop4j.Node;

import de.ovgu.featureide.fm.core.base.IFeature;
import org.prop4j.Not;

public class NodeUtils {
    // FeatureIDE does not support True and False... or does it? Chico and me didn't know.
    public static Node True = null;

    public static Node negate(Node node) {
        if (node instanceof Literal) {
            return negate((Literal) node);
        }

        return new Not(node);
    }

    public static Literal negate(Literal lit) {
        if (lit == null || lit.var == null) {
            throw new NullPointerException();
        }
        return new Literal(lit.var, !lit.positive);
    }

    public static Literal referenceLiteral(Literal lit) {
        return referenceLiteral(lit, lit.positive);
    }

    public static Literal referenceLiteral(Literal lit, boolean positive) {
        if (lit == null || lit.var == null) {
            throw new NullPointerException();
        }
        return new Literal(lit.var, positive);
    }

    public static Literal reference(Object object) {
        if (object instanceof Literal) {
            // Careful: Dont put literals into literals!
            return referenceLiteral((Literal) object);
        }
        if (object instanceof IFeature) {
            // Careful: Use name as string here!
            return reference(((IFeature) object).getName());
        }

        // Object.toString()?
        return new Literal(object);
    }

    public static Literal reference(Object object, boolean positive) {
        if (object instanceof Literal) {
            return referenceLiteral((Literal) object, positive);
        }
        if (object instanceof IFeature) {
            return reference(((IFeature) object).getName(), positive);
        }

        return new Literal(object, positive);
    }

    public static Node AndSimplified(Node a, Node b) {
        if (a == True) {
            return b;
        } else if (b == True) {
            return a;
        }

        return new And(a, b);
    }

    /**
     * This method inlines all recursive Ands into the top level And, passed as argument.
     */
    public static void flatten(And and) {
        List<And> redundantChildren = new ArrayList<>();

        do {
            List<Node> andsChildren = new ArrayList<>(Arrays.asList(and.getChildren()));
            for (And redundantChild : redundantChildren) {
                andsChildren.remove(redundantChild);
                andsChildren.addAll(Arrays.asList(redundantChild.getChildren()));
            }
            redundantChildren.clear();
            and.setChildren(andsChildren.toArray());

            for (Node child : and.getChildren()) {
                if (child instanceof And) {
                    redundantChildren.add((And)child);
                }
            }
        } while (!redundantChildren.isEmpty());
    }

    public static String toString(Node node) {
        if (node != null)
            return node.toString();
        return "True";
    }
}

