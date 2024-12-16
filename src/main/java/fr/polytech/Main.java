package fr.polytech;

import fr.polytech.constraints.ColumnConstraint;

import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {

        var grid = new Grid(new char[][]{
                {'1', '2', '3'},
                {'3', '3', '2'},
                {'2', '1', '1'}
        }, List.of(
                new ColumnConstraint(Set.of('1', '2', '3'))
        ));

        grid.display();
        System.out.println(grid.areConstraintsSatisfied());
    }
}