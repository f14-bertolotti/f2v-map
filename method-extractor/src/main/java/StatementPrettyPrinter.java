package src.main.java;

import                   com.github.javaparser.ast.Node;
import                 com.github.javaparser.ast.stmt.*;
import                 com.github.javaparser.ast.expr.*;
import com.github.javaparser.printer.configuration.*;
import com.github.javaparser.printer.DefaultPrettyPrinterVisitor;
import java.util.*;

public class StatementPrettyPrinter extends DefaultPrettyPrinterVisitor {

    public StatementPrettyPrinter() {
        super(new DefaultPrinterConfiguration());
    }

    @Override
    public void visit(final BlockStmt n, final Void arg) {}

    @Override
    public void visit(final IfStmt n, final Void arg) {
        printComment(n.getComment(), arg);
        printer.print("if (");
        n.getCondition().accept(this, arg);
        final boolean thenBlock = n.getThenStmt() instanceof BlockStmt;
        if (// block statement should start on the same line
        thenBlock)
            printer.print(") ");
        else {
            printer.println(")");
            printer.indent();
        }
        if (!thenBlock)
            printer.unindent();
        if (n.getElseStmt().isPresent()) {
            if (thenBlock)
                printer.print(" ");
            else
                printer.println();
            final boolean elseIf = n.getElseStmt().orElse(null) instanceof IfStmt;
            final boolean elseBlock = n.getElseStmt().orElse(null) instanceof BlockStmt;
            if (// put chained if and start of block statement on a same level
            elseIf || elseBlock)
                printer.print("else ");
            else {
                printer.println("else");
                printer.indent();
            }
            if (!(elseIf || elseBlock))
                printer.unindent();
        }
    }
    @Override
    public void visit(final WhileStmt n, final Void arg) {
        printComment(n.getComment(), arg);
        printer.print("while (");
        n.getCondition().accept(this, arg);
        printer.print(") ");
    }
    @Override
    public void visit(final DoStmt n, final Void arg) {
        printComment(n.getComment(), arg);
        printer.print("do ");
        printer.print(" while (");
        n.getCondition().accept(this, arg);
        printer.print(");");
    }
    @Override
    public void visit(final ForEachStmt n, final Void arg) {
        printComment(n.getComment(), arg);
        printer.print("for (");
        n.getVariable().accept(this, arg);
        printer.print(" : ");
        n.getIterable().accept(this, arg);
        printer.print(") ");
    }
    @Override
    public void visit(final ForStmt n, final Void arg) {
        printComment(n.getComment(), arg);
        printer.print("for (");
        if (n.getInitialization() != null) {
            for (final Iterator<Expression> i = n.getInitialization().iterator(); i.hasNext(); ) {
                final Expression e = i.next();
                e.accept(this, arg);
                if (i.hasNext()) {
                    printer.print(", ");
                }
            }
        }
        printer.print("; ");
        if (n.getCompare().isPresent()) {
            n.getCompare().get().accept(this, arg);
        }
        printer.print("; ");
        if (n.getUpdate() != null) {
            for (final Iterator<Expression> i = n.getUpdate().iterator(); i.hasNext(); ) {
                final Expression e = i.next();
                e.accept(this, arg);
                if (i.hasNext()) {
                    printer.print(", ");
                }
            }
        }
        printer.print(") ");
    }
    @Override
    public void visit(final SynchronizedStmt n, final Void arg) {
        printComment(n.getComment(), arg);
        printer.print("synchronized (");
        n.getExpression().accept(this, arg);
        printer.print(") ");
    }
    @Override
    public void visit(final TryStmt n, final Void arg) {
        printComment(n.getComment(), arg);
        printer.print("try ");
        if (!n.getResources().isEmpty()) {
            printer.print("(");
            Iterator<Expression> resources = n.getResources().iterator();
            boolean first = true;
            while (resources.hasNext()) {
                resources.next().accept(this, arg);
                if (resources.hasNext()) {
                    printer.print(";");
                    printer.println();
                    if (first) {
                        printer.indent();
                    }
                }
                first = false;
            }
            if (n.getResources().size() > 1) {
                printer.unindent();
            }
            printer.print(") ");
        }
        n.getTryBlock().accept(this, arg);
        for (final CatchClause c : n.getCatchClauses()) {
            c.accept(this, arg);
        }
        if (n.getFinallyBlock().isPresent()) {
            printer.print(" finally ");
        }
    }
    @Override
    public void visit(final CatchClause n, final Void arg) {
        printComment(n.getComment(), arg);
        printer.print(" catch (");
        n.getParameter().accept(this, arg);
        printer.print(") ");
    }
}

