package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.constraints.BlockConstraint;

import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {

        var grid = new Grid(new Character[][]{
                {'4', '2', '3'},
                {'3', '1', '2'},
                {'2', '1', '3'}
        }, List.of(
                new BlockConstraint(Set.of('1', '2', '3', '4'), 0, 0, 2, 2)
        ));

        grid.display();
        System.out.println(grid.areConstraintsSatisfied());
    }
}